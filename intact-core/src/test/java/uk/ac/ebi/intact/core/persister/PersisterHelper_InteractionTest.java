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
package uk.ac.ebi.intact.core.persister;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.TransactionStatus;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.InteractionDao;
import uk.ac.ebi.intact.core.persister.stats.PersisterStatistics;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.core.util.DebugUtil;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.clone.IntactCloner;
import uk.ac.ebi.intact.model.util.CrcCalculator;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

import java.util.*;

/**
 * InteractionPersister tester.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PersisterHelper_InteractionTest extends IntactBasicTestCase {

    private static final Log log = LogFactory.getLog( PersisterHelper_InteractionTest.class );

    @Test
    public void allPersisted() throws Exception {
        IntactMockBuilder builder = super.getMockBuilder();
        IntactEntry intactEntry = builder.createIntactEntryRandom();

        final Collection<Interaction> interactions = intactEntry.getInteractions();
        getCorePersister().saveOrUpdate(interactions.toArray(new Interaction[interactions.size()]));

        Assert.assertEquals(intactEntry.getInteractions().size(), getDaoFactory().getInteractionDao().countAll());
        for (CvObject cv : getDaoFactory().getCvObjectDao().getAll()) {
            Assert.assertFalse(cv.getXrefs().isEmpty());
        }
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
        int startCvCount = getDaoFactory().getCvObjectDao().countAll();

        IntactMockBuilder builder = getMockBuilder();
        IntactEntry intactEntry = builder.createIntactEntryRandom(2, 2, 2);
        Assert.assertEquals( "unknown", builder.getInstitution().getShortLabel() );
        getIntactContext().getConfig().setAcPrefix( "IA" );

        // add extra features/ranges on components
        for ( Interaction interaction : intactEntry.getInteractions() ) {
            for ( Component component : interaction.getComponents() ) {
                addFeature( component );
            }
        }

        final Collection<Interaction> interactions = intactEntry.getInteractions();
        getCorePersister().saveOrUpdate(interactions.toArray(new Interaction[interactions.size()]));

        int count = getDaoFactory().getInteractionDao().countAll();
        Assert.assertEquals(intactEntry.getInteractions().size(), count);
        for (CvObject cv : getDaoFactory().getCvObjectDao().getAll()) {
            Assert.assertFalse(cv.getXrefs().isEmpty());
        }

        // having already persisted an entry in the database, we will persist an other one.
        // That involves reusing CV terms, Institution...
        Assert.assertEquals( 4, getDaoFactory().getInstitutionDao().getAll().size() );
        final Institution intact1 = getDaoFactory().getInstitutionDao().getByShortLabel( "unknown" );
        final Institution intact2 = getDaoFactory().getInteractionDao().getAll().iterator().next().getOwner();
        Assert.assertEquals( intact1, intact2 );

        Assert.assertEquals( startCvCount + 17, getDaoFactory().getCvObjectDao().getAll().size() );
        Assert.assertEquals( 4, getDaoFactory().getInteractionDao().getAll().size() );

        intactEntry = builder.createIntactEntryRandom(2, 2, 2);

        // add extra features/ranges on components
        for ( Interaction interaction : intactEntry.getInteractions() ) {
            for ( Component component : interaction.getComponents() ) {
                addFeature( component );
            }
        }

        final Collection<Interaction> interactions2 = intactEntry.getInteractions();
        getCorePersister().saveOrUpdate(interactions2.toArray(new Interaction[interactions2.size()]));

        Assert.assertEquals(intactEntry.getInteractions().size() + count, getDaoFactory().getInteractionDao().countAll());
        for (CvObject cv : getDaoFactory().getCvObjectDao().getAll()) {
            Assert.assertFalse(cv.getXrefs().isEmpty());
        }
    }

    @Test
    public void aliasPersisted() throws Exception {
        IntactMockBuilder builder = super.getMockBuilder();
        Interaction interaction = builder.createInteractionRandomBinary();

        getCorePersister().saveOrUpdate(interaction);

        CvAliasType aliasType = getDaoFactory().getCvObjectDao(CvAliasType.class).getByPsiMiRef(CvAliasType.GENE_NAME_MI_REF);
        Assert.assertNotNull(aliasType);
    }


    @Test
    public void confidencePersisted() throws Exception {
        IntactMockBuilder builder = super.getMockBuilder();
        Interaction interaction = builder.createDeterministicInteraction();
        Confidence confidenceExpected = interaction.getConfidences().iterator().next();

        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getConfidenceDao().countAll());

        Iterator<Confidence> confidenceIter = getDaoFactory().getConfidenceDao().getAllIterator();
        Confidence confidenceObserved = confidenceIter.next();
        Assert.assertEquals( confidenceExpected.getValue(), confidenceObserved.getValue());

        Iterator<InteractionImpl> interactionIter = getDaoFactory().getInteractionDao().getAllIterator();
        Interaction interactionObserved = interactionIter.next();
        Assert.assertEquals(1, interactionObserved.getConfidences().size());
        Confidence confidenceObserved2 = interactionObserved.getConfidences().iterator().next();
        Assert.assertEquals( confidenceExpected.getValue(), confidenceObserved2.getValue());
    }

    @Test
    public void interactionConfidencePersisted() throws Exception {
        /**
         * Having an interaction without confidence value in the database. Tests if it can add a confidence value and
         * persist it to database.
         */
        IntactMockBuilder builder = super.getMockBuilder();
        Interaction interaction = builder.createInteractionRandomBinary();
        Assert.assertEquals( 0, interaction.getConfidences().size() );

        getCorePersister().saveOrUpdate(interaction);

        Interaction reloadedInteraction = getDaoFactory().getInteractionDao().
                getByAc( interaction.getAc() );
        Assert.assertEquals( 0, reloadedInteraction.getConfidences().size() );

        Assert.assertEquals( interaction, reloadedInteraction );
        Confidence confidence = builder.createDeterministicConfidence();

        reloadedInteraction.addConfidence( confidence );
        Assert.assertEquals( 1, reloadedInteraction.getConfidences().size() );
        Assert.assertEquals( confidence, reloadedInteraction.getConfidences().iterator().next() );



        CvConfidenceType cvConfidenceType = builder.createCvObject( CvConfidenceType.class, "IA:997", "testShortLabel" );
        confidence.setCvConfidenceType( cvConfidenceType );
        getCorePersister().saveOrUpdate( cvConfidenceType );
        getDaoFactory().getInteractionDao().update( (InteractionImpl)reloadedInteraction );
        getDaoFactory().getConfidenceDao().persist( confidence);




        Interaction reloadedInteraction2 = getDaoFactory().getInteractionDao().getByAc( interaction.getAc() );
        Assert.assertEquals( reloadedInteraction, reloadedInteraction2 );
        Assert.assertEquals( 1, reloadedInteraction2.getConfidences().size() );
        Assert.assertEquals( confidence, reloadedInteraction2.getConfidences().iterator().next() );
    }

    @Test
    public void interactionParameterPersisted() throws Exception {
        IntactMockBuilder builder = super.getMockBuilder();
        Interaction interaction = builder.createDeterministicInteraction();
        InteractionParameter interactionParameterExpected = interaction.getParameters().iterator().next();
        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getInteractionParameterDao().countAll());

        Iterator<InteractionParameter> interactionParameterIter = getDaoFactory().getInteractionParameterDao().getAllIterator();
        InteractionParameter interactionParameterObserved = interactionParameterIter.next();
        Assert.assertEquals( interactionParameterExpected.getFactor(), interactionParameterObserved.getFactor());
        Assert.assertEquals( interactionParameterExpected.getCvParameterType(), interactionParameterObserved.getCvParameterType());
        Assert.assertEquals( interactionParameterExpected.getCvParameterUnit(), interactionParameterObserved.getCvParameterUnit());
        Assert.assertEquals( interactionParameterExpected.getBase(), interactionParameterObserved.getBase());
        Assert.assertEquals( interactionParameterExpected.getUncertainty(), interactionParameterObserved.getUncertainty());
        Assert.assertEquals( interactionParameterExpected.getExponent(), interactionParameterObserved.getExponent());

        Iterator<InteractionImpl> interactionIter = getDaoFactory().getInteractionDao().getAllIterator();
        Interaction interactionObserved = interactionIter.next();
        Assert.assertEquals(1, interactionObserved.getParameters().size());
        InteractionParameter interactionParameterObserved2 = interactionObserved.getParameters().iterator().next();
        Assert.assertEquals( interactionParameterExpected.getFactor(), interactionParameterObserved2.getFactor());
        Assert.assertEquals( interactionParameterExpected.getCvParameterType(), interactionParameterObserved2.getCvParameterType());
        Assert.assertEquals( interactionParameterExpected.getCvParameterUnit(), interactionParameterObserved2.getCvParameterUnit());
        Assert.assertEquals( interactionParameterExpected.getBase(), interactionParameterObserved2.getBase());
        Assert.assertEquals( interactionParameterExpected.getUncertainty(), interactionParameterObserved2.getUncertainty());
        Assert.assertEquals( interactionParameterExpected.getExponent(), interactionParameterObserved2.getExponent());
    }

    @Test
    public void componentPersisted() throws Exception {
        IntactMockBuilder builder = super.getMockBuilder();
        Interaction interaction = builder.createDeterministicInteraction();
        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());

        interaction.addComponent(getMockBuilder().createComponentRandom());
        interaction.setShortLabel("updated_interaction");

        getCorePersister().saveOrUpdate(interaction);
        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(3, getDaoFactory().getComponentDao().countAll());
        Assert.assertEquals("updated_interaction", interaction.getShortLabel());

        Component c = interaction.getComponents().iterator().next();
        c.setShortLabel("updated_component");
        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals("updated_component", getDaoFactory().getComponentDao().getByAc(c.getAc()).getShortLabel());
    }

    @Test
    public void institutionPersisted() throws Exception {
        final String ownerName = "LalaInstitute";
        Institution institution = new Institution(ownerName);

        IntactMockBuilder builder = new IntactMockBuilder(institution);
        Interaction interaction = builder.createInteractionRandomBinary();

        Assert.assertEquals(institution, interaction.getOwner());

        getCorePersister().saveOrUpdate(interaction);

        Institution reloadedInstitution = getDaoFactory().getInstitutionDao()
                .getByShortLabel(ownerName);
        Interaction reloadedInteraction = getDaoFactory().getInteractionDao()
                .getByShortLabel(interaction.getShortLabel());

        Assert.assertEquals(5, getDaoFactory().getInstitutionDao().countAll());
        Assert.assertEquals(ownerName, reloadedInstitution.getShortLabel());
        Assert.assertEquals(ownerName, reloadedInteraction.getOwner().getShortLabel());
    }

    @Test
    public void institution_notPersisted() throws Exception {
        Institution institution = getIntactContext().getInstitution();

        IntactMockBuilder builder = new IntactMockBuilder(institution);
        Interaction interaction = builder.createInteractionRandomBinary();

        Assert.assertEquals(institution, interaction.getOwner());

        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(4, getDaoFactory().getInstitutionDao().countAll());
    }

    @Test
    public void institution_notPersisted2() throws Exception {
        Institution institution = new Institution(getIntactContext().getInstitution().getShortLabel());

        IntactMockBuilder builder = new IntactMockBuilder(institution);
        Interaction interaction = builder.createInteractionRandomBinary();

        Assert.assertEquals(institution, interaction.getOwner());

        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(4, getDaoFactory().getInstitutionDao().countAll());
    }


    @Test
    public void onPersist_syncedLabel() throws Exception {
        Interaction interaction = getMockBuilder().createInteraction("lala", "lolo");

        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getProteinDao().countAll());

        System.out.println(getDaoFactory().getInteractionDao().getAll());


        Interaction reloadedInteraction = getDaoFactory().getInteractionDao().getByShortLabel("lala-lolo");
        Assert.assertNotNull(reloadedInteraction);
        Assert.assertEquals(2, reloadedInteraction.getComponents().size());
    }

    @Test
    public void persistAllInteractionInAExperiment() throws Exception {
        Experiment experiment = getMockBuilder().createExperimentRandom(3);

        final Collection<Interaction> interactions = experiment.getInteractions();
        getCorePersister().saveOrUpdate(interactions.toArray(new Interaction[interactions.size()]));

        Assert.assertEquals(3, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(6, getDaoFactory().getProteinDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
    }

    @Test
    public void persistInteractionWithAnnotations() throws Exception {
        Experiment experiment = getMockBuilder().createExperimentEmpty();
        experiment.addAnnotation(getMockBuilder().createAnnotationRandom());
        getCorePersister().saveOrUpdate(experiment);
    }

    @Test
    public void onPersist_syncedLabel2() throws Exception {
        Interaction interaction = getMockBuilder().createInteraction("foo", "bar");

        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getProteinDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());

        interaction = getMockBuilder().createInteraction("foo", "bar");

        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getProteinDao().countAll());
        Assert.assertEquals(4, getDaoFactory().getComponentDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getExperimentDao().countAll());

        System.out.println(DebugUtil.labelList(getDaoFactory().getInteractionDao().getAll()));

        Interaction reloadedInteraction = getDaoFactory().getInteractionDao().getByShortLabel("bar-foo-1");
        Assert.assertNotNull(reloadedInteraction);
        Assert.assertEquals(2, reloadedInteraction.getComponents().size());
    }

    @Test
    public void fetchFromDatasource_same() throws Exception {
        Interaction interaction = getMockBuilder().createDeterministicInteraction();

        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());

        Interaction interaction2 = getMockBuilder().createDeterministicInteraction();

        getCorePersister().saveOrUpdate(interaction2);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());
    }

    @Test
    public void fetchFromDatasource_switchedRoles() throws Exception {
        Interaction interaction = getMockBuilder().createDeterministicInteraction();

        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());

        Interaction interaction2 = getMockBuilder().createDeterministicInteraction();
        Iterator<Component> componentIterator = interaction2.getComponents().iterator();
        CvExperimentalRole expRole1 = componentIterator.next().getCvExperimentalRole();
        CvExperimentalRole expRole2 = componentIterator.next().getCvExperimentalRole();

        componentIterator = interaction2.getComponents().iterator();
        componentIterator.next().addExperimentalRole(expRole2);
        componentIterator.next().addExperimentalRole(expRole1);

        getCorePersister().saveOrUpdate(interaction2);

        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
    }

    @Test
    public void fetchFromDatasource_differentFeaturesInComponents() throws Exception {
        Interaction interaction = getMockBuilder().createDeterministicInteraction();

        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());

        Interaction interaction2 = getMockBuilder().createDeterministicInteraction();
        final Feature feature = interaction2.getComponents().iterator().next().getFeatures().iterator().next();
        feature.getRanges().iterator().next().setFromIntervalStart(3);

        getCorePersister().saveOrUpdate(interaction2);

        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(4, getDaoFactory().getComponentDao().countAll());
    }

    @Test
    public void fetchFromDatasource_differentAnnotations() throws Exception {
        Interaction interaction = getMockBuilder().createDeterministicInteraction();

        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());

        Interaction interaction2 = getMockBuilder().createDeterministicInteraction();
        interaction2.getAnnotations().clear();
        interaction2.getAnnotations().add(getMockBuilder().createAnnotation("This is a different annotation", CvTopic.COMMENT_MI_REF, CvTopic.COMMENT));

        getCorePersister().saveOrUpdate(interaction2);

        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(4, getDaoFactory().getComponentDao().countAll());
    }

    @Test
    public void fetchFromDatasource_differentAnnotations2() throws Exception {
        Interaction interaction = getMockBuilder().createDeterministicInteraction();

        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());

        Interaction interaction2 = getMockBuilder().createDeterministicInteraction();
        interaction2.getAnnotations().clear();
        CvTopic topic = getMockBuilder().createCvObject(CvTopic.class, "lala:2", CvTopic.ASSEMBLY);
        topic.getXrefs().iterator().next().setCvDatabase(getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT));
        interaction2.getAnnotations().add(getMockBuilder().createAnnotation("This is an annotation", topic));

        getCorePersister().saveOrUpdate(interaction2);

        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(4, getDaoFactory().getComponentDao().countAll());
    }

    @Test
    public void fetchFromDatasource_differentExperiments() throws Exception {
        Interaction interaction = getMockBuilder().createDeterministicInteraction();

        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());

        Interaction interaction2 = getMockBuilder().createDeterministicInteraction();
        interaction2.getExperiments().clear();
        interaction2.getExperiments().add(getMockBuilder().createExperimentEmpty("exp-1979-2"));

        getCorePersister().saveOrUpdate(interaction2);

        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getInteractionDao().getByAc(interaction.getAc()).getExperiments().size());
        Assert.assertEquals(1, getDaoFactory().getInteractionDao().getByAc(interaction2.getAc()).getExperiments().size());
    }

    @Test
    public void crcPreInsert() throws Exception {
        Interaction interaction = getMockBuilder().createDeterministicInteraction();
        getCorePersister().saveOrUpdate(interaction);
        Assert.assertNotNull(interaction.getCrc());
    }

    @Test
    public void crcPreUpdate() throws Exception {
        Interaction interaction = getMockBuilder().createDeterministicInteraction();
        getCorePersister().saveOrUpdate(interaction);
        Assert.assertNotNull(interaction.getCrc());

        String originalCrc = interaction.getCrc();

        InteractionImpl interaction2 = getDaoFactory().getInteractionDao().getByAc(interaction.getAc());
        final Component componentPrey = getMockBuilder().createComponentPrey(interaction2, getMockBuilder().createProteinRandom());
        interaction2.addComponent(componentPrey);
        getCorePersister().saveOrUpdate(interaction2);

        Assert.assertEquals(interaction.getAc(),interaction2.getAc());
        Assert.assertFalse(originalCrc.equals(interaction2.getCrc()));
    }

    @Test
    public void newInteraction_existingExperiment() throws Exception {
        Experiment exp = getMockBuilder().createExperimentRandom(1);

        getCorePersister().saveOrUpdate(exp);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());

        Experiment loadedExp = getDaoFactory().getExperimentDao().getByAc(exp.getAc());

        Interaction interaction = getMockBuilder().createInteractionRandomBinary();
        interaction.getExperiments().clear();
        loadedExp.addInteraction(interaction);

        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
    }

    @Test
    public void newInteraction_clonedInteraction() throws Exception {
        Interaction interaction = getMockBuilder().createDeterministicInteraction();

        PersisterStatistics stats = getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(1, stats.getPersistedCount(Interaction.class, true));

        // clone 1
        Interaction clonedInteraction = (Interaction) ((InteractionImpl)interaction).clone();
        clonedInteraction.getExperiments().addAll(interaction.getExperiments());

        getPersisterHelper().getCorePersister().setUpdateWithoutAcEnabled(true);
        PersisterStatistics stats2 = getCorePersister().saveOrUpdate(clonedInteraction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals("fooprey-barbait", getDaoFactory().getInteractionDao().getByAc(clonedInteraction.getAc()).getShortLabel());

        Assert.assertEquals(0, stats2.getPersistedCount(Interaction.class, true));
        Assert.assertEquals(0, stats2.getMergedCount(Interaction.class, true));
        Assert.assertEquals(1, stats2.getDuplicatesCount(Interaction.class, true));

        // clone 2
        Interaction clonedInteraction2 = (Interaction) ((InteractionImpl)interaction).clone();
        clonedInteraction2.setExperiments(interaction.getExperiments());

        getPersisterHelper().getCorePersister().setUpdateWithoutAcEnabled(false);
        PersisterStatistics stats3 = getCorePersister().saveOrUpdate(clonedInteraction2);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals("fooprey-barbait", getDaoFactory().getInteractionDao().getByAc(clonedInteraction2.getAc()).getShortLabel());

        Assert.assertEquals(0, stats3.getPersistedCount(Interaction.class, true));
        Assert.assertEquals(0, stats3.getMergedCount(Interaction.class, true));
        Assert.assertEquals(1, stats3.getDuplicatesCount(Interaction.class, true));
    }

    @Test
    public void newInteraction_clonedInteractionWithDifferentInteractor() throws Exception {
        Interaction interaction = getMockBuilder().createDeterministicInteraction();

        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getProteinDao().countAll());

        IntactCloner cloner = new IntactCloner(true);

        Interaction clonedInteraction =  cloner.clone(interaction);

        clonedInteraction.setShortLabel( "fooprey-barbait-1");
        clonedInteraction.addComponent(getMockBuilder().createComponentPrey(clonedInteraction,
                getMockBuilder().createProteinRandom()));

        getCorePersister().saveOrUpdate(clonedInteraction);

        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(5, getDaoFactory().getComponentDao().countAll());
        Assert.assertEquals(3, getDaoFactory().getProteinDao().countAll());
    }

    @Test
    public void existingInteraction_addingExistingExperiment() throws Exception {
        Experiment exp = getMockBuilder().createExperimentRandom(1);
        Experiment exp2 = getMockBuilder().createExperimentRandom(1);

        getCorePersister().saveOrUpdate(exp, exp2);

        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getExperimentDao().countAll());

        Experiment loadedExp = getDaoFactory().getExperimentDao().getByAc(exp.getAc());

        Interaction interaction = getMockBuilder().createInteractionRandomBinary();
        interaction.getExperiments().clear();
        loadedExp.addInteraction(interaction);

        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(2, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(3, getDaoFactory().getInteractionDao().countAll());

        refresh(interaction);

        interaction.addExperiment(exp2);

        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(2, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(3, getDaoFactory().getInteractionDao().countAll());

        getDaoFactory().getInteractionDao().refresh((InteractionImpl)interaction);

        Assert.assertEquals(2, interaction.getExperiments().size());
    }

    @Test
    public void removingExperiments() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();
        Experiment experimentToDelete = getMockBuilder().createExperimentEmpty("exptodelete-2007-1");
        interaction.addExperiment(experimentToDelete);

        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());

        interaction = reloadByAc(interaction);

        Assert.assertEquals(2, interaction.getExperiments().size());

        // remove experiment
        interaction.removeExperiment(experimentToDelete);

        Assert.assertEquals(1, interaction.getExperiments().size());

        getCorePersister().saveOrUpdate(interaction);
        interaction = reloadByAc(interaction);

        Assert.assertEquals(1, interaction.getExperiments().size());
    }

    @Test
    public void persistDisconnectedInteraction() throws Exception {
        Interaction interaction = getMockBuilder().createDeterministicInteraction();
        getCorePersister().saveOrUpdate(interaction);

        getDaoFactory().getEntityManager().clear();
        getDaoFactory().getEntityManager().close();

        interaction.setFullName("newFullName");

        PersisterStatistics stats = getCorePersister().saveOrUpdate(interaction);

        Interaction int2 = reloadByAc(interaction);

        Assert.assertEquals(1, stats.getTransientCount(Interaction.class, true));

        Assert.assertEquals("newFullName", int2.getFullName());
    }

    @Test
    public void persistSeveralInteractions() throws Exception {
        TransactionStatus transactionStatus = getDataContext().beginTransaction();

        Interaction interaction1 = getMockBuilder().createInteractionRandomBinary();

        getCorePersister().saveOrUpdate(interaction1);

        getDaoFactory().getEntityManager().clear();

        getDataContext().commitTransaction(transactionStatus);

        final IntactCloner intactCloner = new IntactCloner();
        intactCloner.setExcludeACs(true);

        Interaction clonedInteraction1 = intactCloner.clone(interaction1);

        Assert.assertNull(clonedInteraction1.getAc());

        Interaction interaction2 = getMockBuilder().createInteractionRandomBinary();
        Interaction interaction3 = getMockBuilder().createInteractionRandomBinary();

        getCorePersister().saveOrUpdate(clonedInteraction1, interaction2, interaction3);

        Assert.assertEquals(3, getDaoFactory().getInteractionDao().countAll());
    }

    @Test
    public void persistDuplicated() throws Exception {
        Interaction interaction1 = getMockBuilder().createDeterministicInteraction();

        final IntactCloner intactCloner = new IntactCloner();
        intactCloner.setExcludeACs(true);

        Interaction clonedInteraction1 = intactCloner.clone(interaction1);
        Assert.assertNull(clonedInteraction1.getAc());

        Assert.assertEquals(interaction1, clonedInteraction1);

        PersisterStatistics stats = getCorePersister().saveOrUpdate(interaction1, clonedInteraction1);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(1, stats.getPersistedCount(Interaction.class, true));
        Assert.assertEquals(1, stats.getDuplicatesCount(Interaction.class, true));
    }

    @Test
    public void persistDuplicated_differentShortLabel() throws Exception {
        Interaction interaction1 = getMockBuilder().createInteractionRandomBinary();
        interaction1.setShortLabel("foo-bar-1");

        getCorePersister().saveOrUpdate(interaction1);

        final IntactCloner intactCloner = new IntactCloner();
        intactCloner.setExcludeACs(true);

        Interaction interactionDiffShortLabel = intactCloner.clone(interaction1);
        interactionDiffShortLabel.setShortLabel("foo-bar-2");
        Assert.assertNull(interactionDiffShortLabel.getAc());

        CrcCalculator crcCalculator = new CrcCalculator();
        Assert.assertEquals(crcCalculator.crc64(interaction1), crcCalculator.crc64(interactionDiffShortLabel));

        PersisterStatistics stats = getCorePersister().saveOrUpdate(interactionDiffShortLabel);

        // as the second interaction is a duplicate of the first, we should get have the first
        // shortlabel set (unless it had an AC already)
        Assert.assertEquals("foo-bar-1", interactionDiffShortLabel.getShortLabel());

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(1, stats.getDuplicatesCount(Interaction.class, true));
        Assert.assertEquals(0, stats.getPersistedCount(Interaction.class, true));
        Assert.assertEquals(0, stats.getMergedCount(Interaction.class, true));
    }

    @Test
    public void persist_updateNewComponent() throws Exception {
        TransactionStatus transactionStatus = getDataContext().beginTransaction();

        Interaction interaction1 = getMockBuilder().createDeterministicInteraction();
        getCorePersister().saveOrUpdate(interaction1);

        getDataContext().commitTransaction(transactionStatus);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());

        Interaction interactionSameNewComponent = new IntactCloner().clone(interaction1);
        Component comp = getMockBuilder().createComponentPrey(interactionSameNewComponent, getMockBuilder().createProteinRandom());
        interactionSameNewComponent.addComponent(comp);

        PersisterStatistics stats = getCorePersister().saveOrUpdate(interactionSameNewComponent);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(0, stats.getPersistedCount(Interaction.class, true));
        Assert.assertEquals(1, stats.getPersistedCount(Protein.class, true));
        Assert.assertEquals(1, stats.getPersistedCount(Component.class, false));
        Assert.assertEquals(1, stats.getMergedCount(Interaction.class, true));

        Assert.assertEquals(3, reloadByAc(interactionSameNewComponent).getComponents().size());
    }

    @Test
    public void persist_updateFullName() throws Exception {
        TransactionStatus transactionStatus = getDataContext().beginTransaction();

        Interaction interaction1 = getMockBuilder().createDeterministicInteraction();
        getCorePersister().saveOrUpdate(interaction1);

        getDataContext().commitTransaction(transactionStatus);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());

        Interaction interactionUpdatedFullName = new IntactCloner().clone(interaction1);
        interactionUpdatedFullName.setFullName("fullName");

        PersisterStatistics stats = getCorePersister().saveOrUpdate(interactionUpdatedFullName);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(0, stats.getPersistedCount(Interaction.class, true));
        Assert.assertEquals(1, stats.getMergedCount(Interaction.class, true));

        Assert.assertEquals("fullName", reloadByAc(interactionUpdatedFullName).getFullName());
    }

    @Test
    public void persist_twoSameInteractorsDifferentRole() throws Exception {
        Protein p = getMockBuilder().createProtein("P12345", "prot");

        Component c1 = getMockBuilder().createComponentBait(p);
        Component c2 = getMockBuilder().createComponentPrey(p);

        Interaction interaction1 = getMockBuilder().createInteraction(c1, c2);

        getCorePersister().saveOrUpdate(interaction1);

        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());
    }

    @Test
    public void persist_sameComponents() throws Exception {
        Protein p = getMockBuilder().createProtein("P12345", "GOT2");

        Component c1 = getMockBuilder().createComponentNeutral( p );
        c1.getBindingDomains().clear();
        c1.getParameters().clear();
        c1.setInteraction( null );

        Component c2 = getMockBuilder().createComponentNeutral( p );
        c2.getBindingDomains().clear();
        c2.getParameters().clear();
        c2.setInteraction( null );

        Interaction interaction = getMockBuilder().createInteraction(c1, c2);

        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals( 1, getDaoFactory().getInteractionDao().getAll().size() );
        Assert.assertEquals( 2, getDaoFactory().getComponentDao().countAll());
        Assert.assertEquals( 2, getDaoFactory().getInteractionDao().getByAc( interaction.getAc() ).getComponents().size() );
    }

    @Test
    public void persist_proteinSmallMoleculeInteraction() throws Exception {
        Protein p = getMockBuilder().createProtein("P12345", "lala");
        final SmallMolecule sm = getMockBuilder().createSmallMolecule( "CHEBI:00001", "2-{1-[2-(2-amino-thiazol-4-yl)-2-methoxyimino-acetylamino]-2-oxo-ethyl}-5,5-dimethyl-thiazolidine-4-carboxylic acid" );

        Component c1 = getMockBuilder().createComponentBait(sm);
        Component c2 = getMockBuilder().createComponentPrey(p);

        Interaction interaction1 = getMockBuilder().createInteraction(c1, c2);

        getCorePersister().saveOrUpdate(interaction1);

        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        final InteractionImpl interaction = getDaoFactory().getInteractionDao().getAll().get( 0 );
        Assert.assertEquals("2__1__2__2_amino_thiazol_4_yl__2_methoxyimino_acetylamino__2_oxo_ethyl__5_5_dimethyl_thiazolidine_4_carboxylic_acid-lala", interaction.getShortLabel());
    }

    @Test
    public void persist_longInteractionShortlabel() throws Exception {

        IntactContext.getCurrentInstance().getConfig().setAutoUpdateInteractionLabel(false);

        Protein p = getMockBuilder().createProtein("P12345", "lala");
        final SmallMolecule sm = getMockBuilder().createSmallMolecule( "CHEBI:00001", "2-{1-[2-(2-amino-thiazol-4-yl)-2-methoxyimino-acetylamino]-2-oxo-ethyl}-5,5-dimethyl-thiazolidine-4-carboxylic acid" );

        Component c1 = getMockBuilder().createComponentBait(sm);
        Component c2 = getMockBuilder().createComponentPrey(p);

        Interaction interaction1 = getMockBuilder().createInteraction(c1, c2);
        interaction1.setShortLabel( "2-{1-[2-(2-amino-thiazol-4-yl)-2-methoxyimino-acetylamino]-2-oxo-ethyl}-5,5-dimethyl-thiazolidine-4-carboxylic acid-lala" );

        getCorePersister().saveOrUpdate(interaction1);

        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        final InteractionImpl interaction = getDaoFactory().getInteractionDao().getAll().get( 0 );
        Assert.assertEquals("2-{1-[2-(2-amino-thiazol-4-yl)-2-methoxyimino-acetylamino]-2-oxo-ethyl}-5,5-dimethyl-thiazolidine-4-carboxylic acid-lala", interaction.getShortLabel());

        IntactContext.getCurrentInstance().getConfig().setAutoUpdateInteractionLabel(false);
    }

    @Test
    public void persist_longInteractionShortlabel_autoUpdateShortlabel() throws Exception {

        IntactContext.getCurrentInstance().getConfig().setAutoUpdateInteractionLabel(true);

        Protein p = getMockBuilder().createProtein("P12345", "lala");
        final SmallMolecule sm = getMockBuilder().createSmallMolecule( "CHEBI:00001", "2-{1-[2-(2-amino-thiazol-4-yl)-2-methoxyimino-acetylamino]-2-oxo-ethyl}-5,5-dimethyl-thiazolidine-4-carboxylic acid" );

        Component c1 = getMockBuilder().createComponentBait(sm);
        Component c2 = getMockBuilder().createComponentPrey(p);

        Interaction interaction1 = getMockBuilder().createInteraction(c1, c2);
        interaction1.setShortLabel( "2-{1-[2-(2-amino-thiazol-4-yl)-2-methoxyimino-acetylamino]-2-oxo-ethyl}-5,5-dimethyl-thiazolidine-4-carboxylic acid-lala" );

        getCorePersister().saveOrUpdate(interaction1);

        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        final InteractionImpl interaction = getDaoFactory().getInteractionDao().getAll().get( 0 );
        Assert.assertEquals("2-{1-[2-(2-amino-thiazol-4-yl)-2-methoxyimino-acetylamino]-2-oxo-ethyl}-5,5-dimethyl-thiazolidine-4-carboxylic acid-lala", interaction.getShortLabel());
    }

    @Test
    public void persist_addAdditionalComponentToManagedInteraction() throws Exception {
        Protein p = getMockBuilder().createProtein("P12345", "prot");
        Component c1 = getMockBuilder().createComponentBait(p);
        Interaction interaction1 = getMockBuilder().createInteraction(c1);

        getCorePersister().saveOrUpdate(interaction1);

        Interaction refreshedInteraction = reloadByAc(interaction1);
        Component bait = refreshedInteraction.getBait();

        CvExperimentalRole preyRole = CvObjectUtils.createCvObject(bait.getOwner(), CvExperimentalRole.class, CvExperimentalRole.PREY_PSI_REF, CvExperimentalRole.PREY);
        Component prey = new Component(bait.getOwner(), bait.getInteraction(), bait.getInteractor(), preyRole, bait.getCvBiologicalRole());
        refreshedInteraction.getComponents().add(prey);

        getCorePersister().saveOrUpdate(prey);

        Interaction finalInteraction = reloadByAc(refreshedInteraction);
        Assert.assertEquals(2, finalInteraction.getComponents().size());
    }

    @Test
    public void persist_addAdditionalComponentToExistingInteraction() throws Exception {
        Interaction interaction1 = null;

        TransactionStatus transactionStatus = IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        Protein p = getMockBuilder().createProtein("P12345", "prot");
        Component c1 = getMockBuilder().createComponentBait(p);
        interaction1 = getMockBuilder().createInteraction(c1);

        getCorePersister().saveOrUpdate(interaction1);

        IntactContext.getCurrentInstance().getDataContext().commitTransaction(transactionStatus);

        Interaction refreshedInteraction = interaction1;
        Assert.assertEquals(1, refreshedInteraction.getComponents().size());

        InteractionDao interactionDao = getIntactContext().getDaoFactory().getInteractionDao();
        Assert.assertTrue(interactionDao.isTransient((InteractionImpl)refreshedInteraction));
        Component bait = refreshedInteraction.getBait();

        CvExperimentalRole preyRole = CvObjectUtils.createCvObject(bait.getOwner(), CvExperimentalRole.class, CvExperimentalRole.PREY_PSI_REF, CvExperimentalRole.PREY);
        Component prey = new Component(bait.getOwner(), bait.getInteraction(), bait.getInteractor(), preyRole, bait.getCvBiologicalRole());
        refreshedInteraction.addComponent(prey);

        getCorePersister().saveOrUpdate(refreshedInteraction);

        Interaction finalInteraction = reloadByAc(refreshedInteraction);
        Assert.assertEquals(2, finalInteraction.getComponents().size());
    }

    @Test
    public void persist_shortLabelSync() throws Exception {
        Interactor interactorA = getMockBuilder().createProtein("Q13158", "abcdefghijklmn");
        Interactor interactorB = getMockBuilder().createProtein("Q14790", "zxcvbnmsasdfghj");
        Component componentA = getMockBuilder().createComponentBait(interactorA);
        Component componentB = getMockBuilder().createComponentPrey(interactorB);
        Interaction interaction = getMockBuilder().createInteraction(componentA, componentB);

        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals("abcdefghijklmn-zxcvbnmsasdfghj", interaction.getShortLabel());

        Component component1 = getMockBuilder().createComponentBait(interactorA);
        Component component2 = getMockBuilder().createComponentPrey(interactorB);

        Interaction interaction2 = getMockBuilder().createInteraction(component1, component2);

        getCorePersister().saveOrUpdate(interaction2);

        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals("abcdefghijklmn-zxcvbnmsasdfghj-1", interaction2.getShortLabel());

        System.out.println(getDaoFactory().getInteractionDao().getAll());
    }

    @Test
    public void persist_sameInteractorBaitAndPrey() throws Exception {
        Interactor interactor = getMockBuilder().createProteinRandom();

        Component bait = getMockBuilder().createComponentBait(interactor);
        Component prey = getMockBuilder().createComponentPrey(interactor);

        Interaction interaction = getMockBuilder().createInteraction(bait, prey);

        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getProteinDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());
    }

    @Test
    public void persist_identicalComponents() throws Exception {
        Interaction interaction = getMockBuilder().createDeterministicInteraction();
        interaction.getComponents().clear();

        Assert.assertEquals(0, interaction.getComponents().size());

        final Protein prot = getMockBuilder().createProtein("P12345", "lala");
        Component a = getMockBuilder().createComponentNeutral(interaction, prot);

        IntactCloner cloner = new IntactCloner(true);
        Component b = cloner.clone(a);
        b.setInteraction(a.getInteraction());

        interaction.getComponents().add(b);

        Assert.assertEquals(2, interaction.getComponents().size());

        getCorePersister().saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getProteinDao().countAll());
        final List<InteractionImpl> list = getDaoFactory().getInteractionDao().getAll();
        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());
    }

    private Interaction reloadByAc(Interaction interaction) {
        return getDaoFactory().getInteractionDao().getByAc(interaction.getAc());
    }

    private void refresh(Interaction interaction) {
        getDaoFactory().getInteractionDao().refresh((InteractionImpl)interaction);
    }
}
