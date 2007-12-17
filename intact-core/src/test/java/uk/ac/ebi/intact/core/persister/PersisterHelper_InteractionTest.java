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

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.stats.PersisterStatistics;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.clone.IntactCloner;
import uk.ac.ebi.intact.model.util.CrcCalculator;

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
public class PersisterHelper_InteractionTest extends IntactBasicTestCase {

    @Test
    public void allPersisted() throws Exception {
        IntactMockBuilder builder = super.getMockBuilder();
        IntactEntry intactEntry = builder.createIntactEntryRandom();

        final Collection<Interaction> interactions = intactEntry.getInteractions();
        PersisterHelper.saveOrUpdate(interactions.toArray(new Interaction[interactions.size()]));

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
        IntactMockBuilder builder = getMockBuilder();
        IntactEntry intactEntry = builder.createIntactEntryRandom(2, 2, 2);
        Assert.assertEquals( "intact", builder.getInstitution().getShortLabel() );
        getIntactContext().getConfig().setAcPrefix( "IA" );

        // add extra features/ranges on components
        for ( Interaction interaction : intactEntry.getInteractions() ) {
            for ( Component component : interaction.getComponents() ) {
                addFeature( component );
            }
        }

        final Collection<Interaction> interactions = intactEntry.getInteractions();
        PersisterHelper.saveOrUpdate(interactions.toArray(new Interaction[interactions.size()]));

        int count = getDaoFactory().getInteractionDao().countAll();
        Assert.assertEquals(intactEntry.getInteractions().size(), count);
        for (CvObject cv : getDaoFactory().getCvObjectDao().getAll()) {
            Assert.assertFalse(cv.getXrefs().isEmpty());
        }

        // having already persisted an entry in the database, we will persist an other one.
        // That involves reusing CV terms, Institution...
        Assert.assertEquals( 2, getDaoFactory().getInstitutionDao().getAll().size() );
        final Institution intact1 = getDaoFactory().getInstitutionDao().getByShortLabel( "intact" );
        final Institution intact2 = getDaoFactory().getAnnotatedObjectDao().getAll().iterator().next().getOwner();
        Assert.assertEquals( intact1, intact2 );

        Assert.assertEquals( 18, getDaoFactory().getCvObjectDao().getAll().size() );
        Assert.assertEquals( 4, getDaoFactory().getInteractionDao().getAll().size() );

        intactEntry = builder.createIntactEntryRandom(2, 2, 2);

        // add extra features/ranges on components
        for ( Interaction interaction : intactEntry.getInteractions() ) {
            for ( Component component : interaction.getComponents() ) {
                addFeature( component );
            }
        }

        final Collection<Interaction> interactions2 = intactEntry.getInteractions();
        PersisterHelper.saveOrUpdate(interactions2.toArray(new Interaction[interactions2.size()]));

        Assert.assertEquals(intactEntry.getInteractions().size() + count, getDaoFactory().getInteractionDao().countAll());
        for (CvObject cv : getDaoFactory().getCvObjectDao().getAll()) {
            Assert.assertFalse(cv.getXrefs().isEmpty());
        }
    }

    @Test
    public void aliasPersisted() throws Exception {
        IntactMockBuilder builder = super.getMockBuilder();
        Interaction interaction = builder.createInteractionRandomBinary();

        PersisterHelper.saveOrUpdate(interaction);

        CvAliasType aliasType = getDaoFactory().getCvObjectDao(CvAliasType.class).getByPsiMiRef(CvAliasType.GENE_NAME_MI_REF);
        Assert.assertNotNull(aliasType);
    }


    @Test
    public void confidencePersisted() throws Exception {
        IntactMockBuilder builder = super.getMockBuilder();
        Interaction interaction = builder.createDeterministicInteraction();
        Confidence confidenceExpected = interaction.getConfidences().iterator().next();

        PersisterHelper.saveOrUpdate(interaction);

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
    public void institutionPersisted() throws Exception {
        final String ownerName = "LalaInstitute";
        Institution institution = new Institution(ownerName);

        IntactMockBuilder builder = new IntactMockBuilder(institution);
        Interaction interaction = builder.createInteractionRandomBinary();

        Assert.assertEquals(institution, interaction.getOwner());

        PersisterHelper.saveOrUpdate(interaction);

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

        PersisterHelper.saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInstitutionDao().countAll());
    }

    @Test
    public void institution_notPersisted2() throws Exception {
        Institution institution = new Institution(getIntactContext().getInstitution().getShortLabel());

        IntactMockBuilder builder = new IntactMockBuilder(institution);
        Interaction interaction = builder.createInteractionRandomBinary();

        Assert.assertEquals(institution, interaction.getOwner());

        PersisterHelper.saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInstitutionDao().countAll());
    }

    @Test
    public void onPersist_syncedLabel() throws Exception {
        Interaction interaction = getMockBuilder().createInteraction("lala", "lolo");

        PersisterHelper.saveOrUpdate(interaction);

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
        PersisterHelper.saveOrUpdate(interactions.toArray(new Interaction[interactions.size()]));

        Assert.assertEquals(3, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(6, getDaoFactory().getProteinDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
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

        PersisterHelper.saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getProteinDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());

        interaction = getMockBuilder().createInteraction("foo", "bar");

        PersisterHelper.saveOrUpdate(interaction);

        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getProteinDao().countAll());
        Assert.assertEquals(4, getDaoFactory().getComponentDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getExperimentDao().countAll());

        Interaction reloadedInteraction = getDaoFactory().getInteractionDao().getByShortLabel("bar-foo-1");
        Assert.assertNotNull(reloadedInteraction);
        Assert.assertEquals(2, reloadedInteraction.getComponents().size());
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
        Interaction interaction = getMockBuilder().createDeterministicInteraction();

        PersisterHelper.saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());

        Interaction interaction2 = getMockBuilder().createDeterministicInteraction();
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
        Interaction interaction = getMockBuilder().createDeterministicInteraction();

        PersisterHelper.saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());

        Interaction interaction2 = getMockBuilder().createDeterministicInteraction();
        final Feature feature = interaction2.getComponents().iterator().next().getBindingDomains().iterator().next();
        feature.getRanges().iterator().next().setFromIntervalStart(3);

        PersisterHelper.saveOrUpdate(interaction2);

        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(4, getDaoFactory().getComponentDao().countAll());
    }

    @Test
    public void fetchFromDatasource_differentAnnotations() throws Exception {
        Interaction interaction = getMockBuilder().createDeterministicInteraction();

        PersisterHelper.saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());

        Interaction interaction2 = getMockBuilder().createDeterministicInteraction();
        interaction2.getAnnotations().clear();
        interaction2.getAnnotations().add(getMockBuilder().createAnnotation("This is a different annotation", CvTopic.COMMENT_MI_REF, CvTopic.COMMENT));

        PersisterHelper.saveOrUpdate(interaction2);

        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(4, getDaoFactory().getComponentDao().countAll());
    }

    @Test
    public void fetchFromDatasource_differentAnnotations2() throws Exception {
        Interaction interaction = getMockBuilder().createDeterministicInteraction();

        PersisterHelper.saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());

        Interaction interaction2 = getMockBuilder().createDeterministicInteraction();
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
        Assert.assertEquals(1, getDaoFactory().getInteractionDao().getByAc(interaction.getAc()).getExperiments().size());
        Assert.assertEquals(1, getDaoFactory().getInteractionDao().getByAc(interaction2.getAc()).getExperiments().size());

        //System.out.println(statistics);
        //System.out.println(statistics.getQueryExecutionMaxTimeQueryString());
    }

    @Test
    public void crcPreInsert() throws Exception {
        Interaction interaction = getMockBuilder().createDeterministicInteraction();
        PersisterHelper.saveOrUpdate(interaction);
        Assert.assertNotNull(interaction.getCrc());
    }

    @Test
    public void crcPreUpdate() throws Exception {
        Interaction interaction = getMockBuilder().createDeterministicInteraction();
        PersisterHelper.saveOrUpdate(interaction);
        Assert.assertNotNull(interaction.getCrc());

        String originalCrc = interaction.getCrc();

        InteractionImpl interaction2 = getDaoFactory().getInteractionDao().getByAc(interaction.getAc());
        final Component componentPrey = getMockBuilder().createComponentPrey(interaction2, getMockBuilder().createProteinRandom());
        interaction2.addComponent(componentPrey);
        PersisterHelper.saveOrUpdate(interaction2);

        Assert.assertFalse(originalCrc.equals(interaction2.getCrc()));
    }

    @Test
    public void newInteraction_existingExperiment() throws Exception {
        Experiment exp = getMockBuilder().createExperimentRandom(1);

        PersisterHelper.saveOrUpdate(exp);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());

        Experiment loadedExp = getDaoFactory().getExperimentDao().getByAc(exp.getAc());

        Interaction interaction = getMockBuilder().createInteractionRandomBinary();
        interaction.getExperiments().clear();
        loadedExp.addInteraction(interaction);

        PersisterHelper.saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
    }

    @Test
    public void newInteraction_clonedInteraction() throws Exception {
        Interaction interaction = getMockBuilder().createDeterministicInteraction();

        PersisterStatistics stats = PersisterHelper.saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(1, stats.getPersistedCount(Interaction.class, true));

        Interaction clonedInteraction = (Interaction) ((InteractionImpl)interaction).clone();
        clonedInteraction.setExperiments(interaction.getExperiments());

        PersisterStatistics stats2 = PersisterHelper.saveOrUpdate(clonedInteraction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals("fooprey-barbait", getDaoFactory().getInteractionDao().getByAc(clonedInteraction.getAc()).getShortLabel());

        Assert.assertEquals(0, stats2.getPersistedCount(Interaction.class, true));
        Assert.assertEquals(0, stats2.getMergedCount(Interaction.class, true));
        Assert.assertEquals(1, stats2.getDuplicatesCount(Interaction.class, true));
    }

    @Test
    public void newInteraction_clonedInteractionWithDifferentInteractor() throws Exception {
        Interaction interaction = getMockBuilder().createDeterministicInteraction();

        PersisterHelper.saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getProteinDao().countAll());

        Interaction clonedInteraction =  (Interaction) ((InteractionImpl)interaction).clone();

        clonedInteraction.setShortLabel( "fooprey-barbait-1");
        clonedInteraction.addComponent(getMockBuilder().createComponentPrey(clonedInteraction,
                                                        getMockBuilder().createProteinRandom()));

        PersisterHelper.saveOrUpdate(clonedInteraction);

        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(5, getDaoFactory().getComponentDao().countAll());
        Assert.assertEquals(3, getDaoFactory().getProteinDao().countAll());
    }

    @Test
    public void existingInteraction_addingExistingExperiment() throws Exception {
        Experiment exp = getMockBuilder().createExperimentRandom(1);
        Experiment exp2 = getMockBuilder().createExperimentRandom(1);

        PersisterHelper.saveOrUpdate(exp, exp2);

        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getExperimentDao().countAll());

        Experiment loadedExp = getDaoFactory().getExperimentDao().getByAc(exp.getAc());

        Interaction interaction = getMockBuilder().createInteractionRandomBinary();
        interaction.getExperiments().clear();
        loadedExp.addInteraction(interaction);

        PersisterHelper.saveOrUpdate(interaction);

        Assert.assertEquals(2, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(3, getDaoFactory().getInteractionDao().countAll());

        refresh(interaction);

        interaction.addExperiment(exp2);

        PersisterHelper.saveOrUpdate(interaction);

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

        PersisterHelper.saveOrUpdate(interaction);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());

        interaction = reloadByAc(interaction);

        Assert.assertEquals(2, interaction.getExperiments().size());

        // remove experiment
        interaction.removeExperiment(experimentToDelete);

        Assert.assertEquals(1, interaction.getExperiments().size());

        PersisterHelper.saveOrUpdate(interaction);
        interaction = reloadByAc(interaction);

        Assert.assertEquals(1, interaction.getExperiments().size());
    }

    @Test
    public void persistDisconnectedInteraction() throws Exception {
        Interaction interaction = getMockBuilder().createDeterministicInteraction();
        PersisterHelper.saveOrUpdate(interaction);

        getDaoFactory().getEntityManager().clear();
        getDaoFactory().getEntityManager().close();

        interaction.setFullName("newFullName");

        PersisterStatistics stats = PersisterHelper.saveOrUpdate(interaction);

        Interaction int2 = reloadByAc(interaction);

        Assert.assertEquals(1, stats.getTransientCount(Interaction.class, true));

        Assert.assertEquals("newFullName", int2.getFullName());
    }

    @Test
    public void persistSeveralInteractions() throws Exception {
        Interaction interaction1 = getMockBuilder().createInteractionRandomBinary();

        PersisterHelper.saveOrUpdate(interaction1);

        getDaoFactory().getEntityManager().clear();
        getDaoFactory().getEntityManager().close();

        final IntactCloner intactCloner = new IntactCloner();
        intactCloner.setExcludeACs(true);

        Interaction clonedInteraction1 = intactCloner.clone(interaction1);

        Assert.assertNull(clonedInteraction1.getAc());

        Interaction interaction2 = getMockBuilder().createInteractionRandomBinary();
        Interaction interaction3 = getMockBuilder().createInteractionRandomBinary();

        PersisterHelper.saveOrUpdate(clonedInteraction1, interaction2, interaction3);

        Assert.assertEquals(3, getDaoFactory().getInteractionDao().countAll());
    }

    @Test
    public void persistDuplicated() throws Exception {
        //IntactContext.getCurrentInstance().close();
        //IntactContext.initContext("intact-core-pg", new StandaloneSession());
        
        Interaction interaction1 = getMockBuilder().createDeterministicInteraction();

        final IntactCloner intactCloner = new IntactCloner();
        intactCloner.setExcludeACs(true);

        Interaction clonedInteraction1 = intactCloner.clone(interaction1);
        Assert.assertNull(clonedInteraction1.getAc());

        Assert.assertEquals(interaction1, clonedInteraction1);

        PersisterStatistics stats = PersisterHelper.saveOrUpdate(interaction1, clonedInteraction1);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(1, stats.getPersistedCount(Interaction.class, true));
        Assert.assertEquals(1, stats.getDuplicatesCount(Interaction.class, true));
    }

    @Test
    public void persistDuplicated_differentShortLabel() throws Exception {
        Interaction interaction1 = getMockBuilder().createInteractionRandomBinary();
        interaction1.setShortLabel("foo-bar-1");

        PersisterHelper.saveOrUpdate(interaction1);

        final IntactCloner intactCloner = new IntactCloner();
        intactCloner.setExcludeACs(true);

        Interaction interactionDiffShortLabel = intactCloner.clone(interaction1);
        interactionDiffShortLabel.setShortLabel("foo-bar-2");
        Assert.assertNull(interactionDiffShortLabel.getAc());

        CrcCalculator crcCalculator = new CrcCalculator();
        Assert.assertEquals(crcCalculator.crc64(interaction1), crcCalculator.crc64(interactionDiffShortLabel));

        PersisterStatistics stats = PersisterHelper.saveOrUpdate(interactionDiffShortLabel);

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
        Interaction interaction1 = getMockBuilder().createDeterministicInteraction();
        PersisterHelper.saveOrUpdate(interaction1);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());

        Interaction interactionSameNewComponent = new IntactCloner().clone(interaction1);
        Component comp = getMockBuilder().createComponentPrey(interactionSameNewComponent, getMockBuilder().createProteinRandom());
        interactionSameNewComponent.addComponent(comp);

        PersisterStatistics stats = PersisterHelper.saveOrUpdate(interactionSameNewComponent);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(0, stats.getPersistedCount(Interaction.class, true));
        Assert.assertEquals(1, stats.getPersistedCount(Protein.class, true));
        Assert.assertEquals(1, stats.getPersistedCount(Component.class, false));
        Assert.assertEquals(1, stats.getMergedCount(Interaction.class, true));

        Assert.assertEquals(3, reloadByAc(interactionSameNewComponent).getComponents().size());
    }

    @Test
    public void persist_updateFullName() throws Exception {
        Interaction interaction1 = getMockBuilder().createDeterministicInteraction();
        PersisterHelper.saveOrUpdate(interaction1);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());

        Interaction interactionUpdatedFullName = new IntactCloner().clone(interaction1);
        interactionUpdatedFullName.setFullName("fullName");

        PersisterStatistics stats = PersisterHelper.saveOrUpdate(interactionUpdatedFullName);

        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(0, stats.getPersistedCount(Interaction.class, true));
        Assert.assertEquals(1, stats.getMergedCount(Interaction.class, true));

        Assert.assertEquals("fullName", reloadByAc(interactionUpdatedFullName).getFullName());
    }

    private Interaction reloadByAc(Interaction interaction) {
        return getDaoFactory().getInteractionDao().getByAc(interaction.getAc());
    }

    private void refresh(Interaction interaction) {
        getDaoFactory().getInteractionDao().refresh((InteractionImpl)interaction);
    }
}