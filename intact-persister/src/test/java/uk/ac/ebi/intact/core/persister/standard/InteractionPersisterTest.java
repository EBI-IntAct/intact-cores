/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.core.persister.standard;

import org.hibernate.stat.Statistics;
import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.PersisterContext;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * InteractionPersister tester.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk), Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionPersisterTest extends AbstractPersisterTest {
    
    @Test
    public void allPersisted() throws Exception {
        IntactMockBuilder builder = super.getMockBuilder();
        IntactEntry intactEntry = builder.createIntactEntryRandom();

        InteractionPersister interactorPersister = InteractionPersister.getInstance();

        for (Interaction interaction : intactEntry.getInteractions()) {
            interactorPersister.saveOrUpdate(interaction);
        }

        interactorPersister.commit();

        beginTransaction();
        Assert.assertEquals(intactEntry.getInteractions().size(), getDaoFactory().getInteractionDao().countAll());
        for (CvObject cv : getDaoFactory().getCvObjectDao().getAll()) {
            Assert.assertFalse(cv.getXrefs().isEmpty());
        }
        commitTransaction();
    }

    private void addFeature( Component component ) {
        IntactMockBuilder builder = super.getMockBuilder();
        Feature feature = builder.createFeatureRandom();
        Collection<Range> ranges = new ArrayList<Range>( );
        Range range = builder.createRangeRandom();
        range.setFeature( feature );
        range.setFromCvFuzzyType( builder.createCvObject( CvFuzzyType.class, "IA:9999", CvFuzzyType.RANGE ));
        range.setToCvFuzzyType( builder.createCvObject( CvFuzzyType.class, "IA:9999", CvFuzzyType.RANGE ));
        ranges.add( range );
        feature.setRanges( ranges );
        component.addBindingDomain( feature );
    }

    @Test
    public void allPersistedWithFeature() throws Exception {
        IntactMockBuilder builder = super.getMockBuilder();
        IntactEntry intactEntry = builder.createIntactEntryRandom(2, 2, 2);
        Assert.assertEquals( "intact", builder.getInstitution().getShortLabel() );
        getIntactContext().getConfig().setAcPrefix( "IA" );

        // add extra features/ranges on components
        for ( Interaction interaction : intactEntry.getInteractions() ) {
            for ( Component component : interaction.getComponents() ) {
                addFeature( component );
            }
        }

        InteractionPersister interactorPersister = InteractionPersister.getInstance();

        for (Interaction interaction : intactEntry.getInteractions()) {
            interactorPersister.saveOrUpdate(interaction);
        }

        interactorPersister.commit();

        beginTransaction();
        int count = getDaoFactory().getInteractionDao().countAll();
        Assert.assertEquals(intactEntry.getInteractions().size(), count);
        for (CvObject cv : getDaoFactory().getCvObjectDao().getAll()) {
            Assert.assertFalse(cv.getXrefs().isEmpty());
        }

        // print all publications
        for ( Publication pub : getDaoFactory().getPublicationDao().getAll() ) {
            System.out.println( pub );
        }

        commitTransaction();
        PersisterContext.getInstance().clear();

        beginTransaction();
        // having already persisted an entry in the database, we will persist an other one.
        // That involves reusing CV terms, Institution...

        for ( Institution institution : getDaoFactory().getInstitutionDao().getAll() ) {
            System.out.println( institution );
        }

        Assert.assertEquals( 2, getDaoFactory().getInstitutionDao().getAll().size() );
        Assert.assertEquals( 61, getDaoFactory().getAnnotatedObjectDao().getAll().size() );
        Assert.assertEquals(getDaoFactory().getInstitutionDao().getByShortLabel( "intact" ), 
                            getDaoFactory().getAnnotatedObjectDao().getAll().iterator().next().getOwner() );
        Assert.assertEquals( 17, getDaoFactory().getCvObjectDao().getAll().size() );
        Assert.assertEquals( 4, getDaoFactory().getInteractionDao().getAll().size() );

        intactEntry = builder.createIntactEntryRandom(2, 2, 2);

        // add extra features/ranges on components
        for ( Interaction interaction : intactEntry.getInteractions() ) {
            for ( Component component : interaction.getComponents() ) {
                addFeature( component );
            }
        }

        interactorPersister = InteractionPersister.getInstance();

        for (Interaction interaction : intactEntry.getInteractions()) {
            interactorPersister.saveOrUpdate(interaction);
        }

        interactorPersister.commit();

        beginTransaction();
        Assert.assertEquals(intactEntry.getInteractions().size() + count, getDaoFactory().getInteractionDao().countAll());
        for (CvObject cv : getDaoFactory().getCvObjectDao().getAll()) {
            Assert.assertFalse(cv.getXrefs().isEmpty());
        }
        commitTransaction();
    }

    @Test
    public void aliasPersisted() throws Exception {
        IntactMockBuilder builder = super.getMockBuilder();
        Interaction interaction = builder.createInteractionRandomBinary();

        InteractionPersister interactionPersister = InteractionPersister.getInstance();

        interactionPersister.saveOrUpdate(interaction);
        interactionPersister.commit();

        beginTransaction();

        CvAliasType aliasType = getDaoFactory().getCvObjectDao(CvAliasType.class).getByPsiMiRef(CvAliasType.GENE_NAME_MI_REF);
        Assert.assertNotNull(aliasType);
    }

    @Test
    public void institutionPersisted() throws Exception {
        final String ownerName = "LalaInstitute";
        Institution institution = new Institution(ownerName);

        IntactMockBuilder builder = new IntactMockBuilder(institution);
        Interaction interaction = builder.createInteractionRandomBinary();

        Assert.assertEquals(institution, interaction.getOwner());

        InteractionPersister interactorPersister = InteractionPersister.getInstance();

        interactorPersister.saveOrUpdate(interaction);
        interactorPersister.commit();

        beginTransaction();

        Institution reloadedInstitution = getDaoFactory().getInstitutionDao()
                .getByShortLabel(ownerName);
        Interaction reloadedInteraction = getDaoFactory().getInteractionDao()
                .getByShortLabel(interaction.getShortLabel());

        Assert.assertEquals(2, getDaoFactory().getInstitutionDao().countAll());
        Assert.assertEquals(ownerName, reloadedInstitution.getShortLabel());
        Assert.assertEquals(ownerName, reloadedInteraction.getOwner().getShortLabel());
    }

    @Test
    public void institution_notPersisted() throws Exception {
        Institution institution = getIntactContext().getInstitution();

        IntactMockBuilder builder = new IntactMockBuilder(institution);
        Interaction interaction = builder.createInteractionRandomBinary();

        Assert.assertEquals(institution, interaction.getOwner());

        InteractionPersister interactorPersister = InteractionPersister.getInstance();

        interactorPersister.saveOrUpdate(interaction);
        interactorPersister.commit();

        beginTransaction();

        Assert.assertEquals(1, getDaoFactory().getInstitutionDao().countAll());
    }

    @Test
    public void institution_notPersisted2() throws Exception {
        Institution institution = new Institution(getIntactContext().getInstitution().getShortLabel());

        IntactMockBuilder builder = new IntactMockBuilder(institution);
        Interaction interaction = builder.createInteractionRandomBinary();

        Assert.assertEquals(institution, interaction.getOwner());

        InteractionPersister interactionPersister = InteractionPersister.getInstance();

        interactionPersister.saveOrUpdate(interaction);
        interactionPersister.commit();

        beginTransaction();
        System.out.println(getDaoFactory().getInstitutionDao().getAll());
        Assert.assertEquals(1, getDaoFactory().getInstitutionDao().countAll());
        commitTransaction();
    }

    @Test
    public void onPersist_syncedLabel() throws Exception {
        Interaction interaction = getMockBuilder().createInteraction("lala", "lolo");

        beginTransaction();
        InteractionPersister.getInstance().saveOrUpdate(interaction);
        InteractionPersister.getInstance().commit();
        commitTransaction();

        beginTransaction();
        Interaction reloadedInteraction = getDaoFactory().getInteractionDao().getByShortLabel("lala-lolo");
        Assert.assertNotNull(reloadedInteraction);
        Assert.assertEquals(2, reloadedInteraction.getComponents().size());
        commitTransaction();
    }

    @Test
    public void persistAllInteractionInAExperiment() throws Exception {
        Experiment experiment = getMockBuilder().createExperimentRandom(3);

        for (Interaction interaction : experiment.getInteractions()) {
            beginTransaction();
            InteractionPersister.getInstance().saveOrUpdate(interaction);
            InteractionPersister.getInstance().commit();
            commitTransaction();
        }

        beginTransaction();
        Assert.assertEquals(3, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(6, getDaoFactory().getProteinDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
        commitTransaction();
    }

    @Test
    public void persistInteractionWithAnnotations() throws Exception {
        Experiment experiment = getMockBuilder().createExperimentEmpty();
        experiment.addAnnotation(getMockBuilder().createAnnotationRandom());
        PersisterHelper.saveOrUpdate(experiment);
    }    

    @Test
    public void onPersist_syncedLabel2() throws Exception {
        Interaction interaction = getMockBuilder().createInteraction("foo", "bar");

        beginTransaction();
        InteractionPersister.getInstance().saveOrUpdate(interaction);
        InteractionPersister.getInstance().commit();
        commitTransaction();

        interaction = getMockBuilder().createInteraction("foo", "bar");

        beginTransaction();
        InteractionPersister.getInstance().saveOrUpdate(interaction);
        InteractionPersister.getInstance().commit();
        commitTransaction();
        
        PersisterContext.getInstance().clear();

        beginTransaction();
        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getProteinDao().countAll());
        Assert.assertEquals(4, getDaoFactory().getComponentDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getExperimentDao().countAll());

        Interaction reloadedInteraction = getDaoFactory().getInteractionDao().getByShortLabel("bar-foo-1");
        Assert.assertNotNull(reloadedInteraction);
        Assert.assertEquals(2, reloadedInteraction.getComponents().size());
        commitTransaction();
    }

    @Test
    public void fetchFromDatasource_same() throws Exception {
        Interaction interaction = getMockBuilder().createDeterministicInteraction();

        PersisterHelper.saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());

        Interaction interaction2 = getMockBuilder().createDeterministicInteraction();

        PersisterHelper.saveOrUpdate(interaction2);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());
    }

    @Test
    public void fetchFromDatasource_switchedRoles() throws Exception {
        Interaction interaction = createReproducibleInteraction();

        PersisterHelper.saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());

        Interaction interaction2 = createReproducibleInteraction();
        Iterator<Component> componentIterator = interaction2.getComponents().iterator();
        CvExperimentalRole expRole1 = componentIterator.next().getCvExperimentalRole();
        CvExperimentalRole expRole2 = componentIterator.next().getCvExperimentalRole();

        componentIterator = interaction2.getComponents().iterator();
        componentIterator.next().setCvExperimentalRole(expRole2);
        componentIterator.next().setCvExperimentalRole(expRole1);

        PersisterHelper.saveOrUpdate(interaction2);

        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
    }

    @Test
    public void fetchFromDatasource_differentFeaturesInComponents() throws Exception {
        Interaction interaction = createReproducibleInteraction();

        PersisterHelper.saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());

        Interaction interaction2 = createReproducibleInteraction();
        final Feature feature = interaction2.getComponents().iterator().next().getBindingDomains().iterator().next();
        feature.getRanges().iterator().next().setFromIntervalStart(3);

        PersisterHelper.saveOrUpdate(interaction2);

        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(4, getDaoFactory().getComponentDao().countAll());
    }

    @Test
    public void fetchFromDatasource_differentAnnotations() throws Exception {
        Interaction interaction = createReproducibleInteraction();

        PersisterHelper.saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());

        Interaction interaction2 = createReproducibleInteraction();
        interaction2.getAnnotations().clear();
        interaction2.getAnnotations().add(getMockBuilder().createAnnotation("This is a different annotation", CvTopic.COMMENT_MI_REF, CvTopic.COMMENT));

        PersisterHelper.saveOrUpdate(interaction2);

        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(4, getDaoFactory().getComponentDao().countAll());
    }

    @Test
    public void fetchFromDatasource_differentAnnotations2() throws Exception {
        Interaction interaction = createReproducibleInteraction();

        PersisterHelper.saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());

        Interaction interaction2 = createReproducibleInteraction();
        interaction2.getAnnotations().clear();
        CvTopic topic = getMockBuilder().createCvObject(CvTopic.class, "IA:0", CvTopic.HIDDEN);
        topic.getXrefs().iterator().next().setCvDatabase(getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT));
        interaction2.getAnnotations().add(getMockBuilder().createAnnotation("This is an annotation", topic));

        PersisterHelper.saveOrUpdate(interaction2);

        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(4, getDaoFactory().getComponentDao().countAll());
    }

    @Test
    public void fetchFromDatasource_differentExperiments() throws Exception {
        //final Statistics statistics = getDaoFactory().getCurrentSession().getSessionFactory().getStatistics();
        //statistics.setStatisticsEnabled(true);

        Interaction interaction = getMockBuilder().createDeterministicInteraction();

        PersisterHelper.saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());

        Interaction interaction2 = getMockBuilder().createDeterministicInteraction();
        interaction2.setExperiments(Arrays.asList(getMockBuilder().createExperimentEmpty("exp-1979-2")));

        PersisterHelper.saveOrUpdate(interaction2);

        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());

        //System.out.println(statistics);
        //System.out.println(statistics.getQueryExecutionMaxTimeQueryString());
    }
}