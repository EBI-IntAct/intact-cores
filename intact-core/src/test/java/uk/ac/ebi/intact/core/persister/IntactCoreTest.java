/**
 * Copyright 2010 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.*;

import java.util.Collection;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactCoreTest extends IntactBasicTestCase {

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void testIsInitialized_yes() throws Exception {
        TransactionStatus transaction = getDataContext().beginTransaction();

        Interaction interaction = getMockBuilder().createInteractionRandomBinary();

        getCorePersister().saveOrUpdate(interaction);

        getDataContext().commitTransaction(transaction);

        TransactionStatus transaction2 = getDataContext().beginTransaction();

        Interaction refreshedInteraction = getDaoFactory().getInteractionDao().getByAc(interaction.getAc());
        Assert.assertEquals(2, refreshedInteraction.getComponents().size());

        Assert.assertTrue(IntactCore.isInitialized(refreshedInteraction.getComponents()));
        Assert.assertFalse(IntactCore.isInitialized(refreshedInteraction.getConfidences()));

        getDataContext().commitTransaction(transaction2);
    }

    @Test
    public void testIsInitialized_vanilla_yes() throws Exception {
        Experiment exp = getMockBuilder().createExperimentEmpty();
        Assert.assertTrue(IntactCore.isInitialized(exp.getAnnotations()));
    }


    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void testIsInitialized_no() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();

        getCorePersister().saveOrUpdate(interaction);

        Interaction refreshedInteraction = getDaoFactory().getInteractionDao().getByAc(interaction.getAc());

        Assert.assertFalse(IntactCore.isInitialized(refreshedInteraction.getComponents()));
        Assert.assertFalse(IntactCore.isInitialized(refreshedInteraction.getConfidences()));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void testIsInitialized_no_try_delete() throws Exception {
        TransactionStatus transaction = getDataContext().beginTransaction();

        Interaction interaction = getMockBuilder().createInteractionRandomBinary();

        getCorePersister().saveOrUpdate(interaction);

        getDataContext().commitTransaction(transaction);

        TransactionStatus transaction2 = getDataContext().beginTransaction();

        Interaction refreshedInteraction = getDaoFactory().getInteractionDao().getByAc(interaction.getAc());

        getDataContext().commitTransaction(transaction2);

        getCoreDeleter().delete(refreshedInteraction);
        Assert.assertNull(getDataContext().getDaoFactory().getInteractionDao().getByAc(refreshedInteraction.getAc()));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
     public void testIsInitialized_no_try_delete_and_remove_from_experiment() throws Exception {
        TransactionStatus transaction = getDataContext().beginTransaction();

        Experiment experiment = getMockBuilder().createDeterministicExperiment();
        Interaction interaction = getMockBuilder().createDeterministicInteraction();
        interaction.getComponents().clear();

        experiment.getInteractions().clear();

        getCorePersister().saveOrUpdate(experiment);

        interaction.getExperiments().clear();
        interaction.addExperiment(experiment);

        getCorePersister().saveOrUpdate(interaction);

        experiment.addInteraction(interaction);

        getCorePersister().saveOrUpdate(experiment);

        getDataContext().commitTransaction(transaction);

        TransactionStatus transaction2 = getDataContext().beginTransaction();

        Experiment refreshedExperiment = getDaoFactory().getExperimentDao().getByAc(experiment.getAc());
        Interaction refreshedInteraction = refreshedExperiment.getInteractions().iterator().next();

        Assert.assertFalse(IntactCore.isInitialized(refreshedInteraction.getComponents()));

        getDataContext().commitTransaction(transaction2);

        TransactionStatus transaction3 = getDataContext().beginTransaction();

        Interaction refreshedInteraction2 = getDaoFactory().getInteractionDao().getByAc(interaction.getAc());
        Assert.assertTrue(refreshedInteraction2.getComponents().isEmpty());

        Component c1 = getMockBuilder().createComponentRandom();
        Component c2 = getMockBuilder().createComponentRandom();
        Component c3 = getMockBuilder().createComponentRandom();

        refreshedInteraction2.addComponent(c1);
        refreshedInteraction2.addComponent(c2);
        refreshedInteraction2.addComponent(c3);

        getCorePersister().saveOrUpdate(refreshedInteraction2);

        getDataContext().commitTransaction(transaction3);

        TransactionStatus transaction4 = getDataContext().beginTransaction();
        getDataContext().commitTransaction(transaction3);

        Assert.assertFalse(IntactCore.isInitialized(refreshedInteraction.getComponents()));

        getCoreDeleter().delete(refreshedInteraction);
        getDataContext().commitTransaction(transaction4);
        Assert.assertNull(getDataContext().getDaoFactory().getInteractionDao().getByAc(refreshedInteraction.getAc()));
    }

    @Test
    public void testIsInitializedAndDirty_yes() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();

        getCorePersister().saveOrUpdate(interaction);

        Interaction refreshedInteraction = getDaoFactory().getInteractionDao().getByAc(interaction.getAc());
        refreshedInteraction.addComponent(getMockBuilder().createComponentRandom());

        Assert.assertTrue(IntactCore.isInitializedAndDirty(refreshedInteraction.getComponents()));
        Assert.assertFalse(IntactCore.isInitializedAndDirty(refreshedInteraction.getConfidences()));
    }

    @Test
    public void testIsInitializedAndDirty_yes2() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();

        getCorePersister().saveOrUpdate(interaction);

        Interaction refreshedInteraction = getDaoFactory().getInteractionDao().getByAc(interaction.getAc());
        refreshedInteraction.getComponents().iterator().next().getBindingDomains().clear();

        Assert.assertFalse(IntactCore.isInitializedAndDirty(refreshedInteraction.getComponents()));
        Assert.assertFalse(IntactCore.isInitializedAndDirty(refreshedInteraction.getConfidences()));
    }

    @Test
    public void testIsInitializedAndDirty_no() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();

        getCorePersister().saveOrUpdate(interaction);

        Interaction refreshedInteraction = getDaoFactory().getInteractionDao().getByAc(interaction.getAc());

        Assert.assertFalse(IntactCore.isInitializedAndDirty(refreshedInteraction.getComponents()));
        Assert.assertFalse(IntactCore.isInitializedAndDirty(refreshedInteraction.getConfidences()));
    }

    @Test
    public void classForAc() throws Exception {
        Publication publication = getMockBuilder().createPublication("12345");
        Experiment experiment = getMockBuilder().createExperimentEmpty();
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();
        Protein protein = getMockBuilder().createProteinRandom();
        BioSource bioSource = getMockBuilder().createBioSourceRandom();
        Feature feature = getMockBuilder().createFeatureRandom();

        Component component = interaction.getComponents().iterator().next();

        getCorePersister().saveOrUpdate(publication, experiment, interaction, protein, bioSource, feature);

        Assert.assertEquals(Publication.class, IntactCore.classForAc(getIntactContext(), publication.getAc()));
        Assert.assertEquals(Experiment.class, IntactCore.classForAc(getIntactContext(), experiment.getAc()));
        Assert.assertEquals(InteractionImpl.class, IntactCore.classForAc(getIntactContext(), interaction.getAc()));
        Assert.assertEquals(InteractorImpl.class, IntactCore.classForAc(getIntactContext(), protein.getAc()));
        Assert.assertEquals(BioSource.class, IntactCore.classForAc(getIntactContext(), bioSource.getAc()));
        Assert.assertEquals(Component.class, IntactCore.classForAc(getIntactContext(), component.getAc()));
        Assert.assertEquals(Feature.class, IntactCore.classForAc(getIntactContext(), feature.getAc()));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void isInitiallized_collections() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();

        getCorePersister().saveOrUpdate(interaction);

        Interaction refreshedInteraction = getDaoFactory().getInteractionDao().getByAc(interaction.getAc());

        Assert.assertFalse(IntactCore.isInitialized(refreshedInteraction.getAnnotations()));
        Assert.assertFalse(IntactCore.isInitialized(refreshedInteraction.getXrefs()));
        Assert.assertFalse(IntactCore.isInitialized(refreshedInteraction.getAliases()));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void ensuresInitializedAnnotations1() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();

        getCorePersister().saveOrUpdate(interaction);

        Interaction refreshedInteraction = getDaoFactory().getInteractionDao().getByAc(interaction.getAc());

        Assert.assertFalse(IntactCore.isInitialized(refreshedInteraction.getAnnotations()));

        Collection<Annotation> annotations = IntactCore.ensureInitializedAnnotations(refreshedInteraction);

        Assert.assertTrue(IntactCore.isInitialized(annotations));
        Assert.assertFalse(IntactCore.isInitialized(refreshedInteraction.getXrefs()));
        Assert.assertFalse(IntactCore.isInitialized(refreshedInteraction.getAliases()));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void ensuresInitializedXrefs() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();

        getCorePersister().saveOrUpdate(interaction);

        Interaction refreshedInteraction = getDaoFactory().getInteractionDao().getByAc(interaction.getAc());

        Assert.assertFalse(IntactCore.isInitialized(refreshedInteraction.getXrefs()));

        Collection<? extends Xref> xrefs = IntactCore.ensureInitializedXrefs(refreshedInteraction);

        Assert.assertFalse(IntactCore.isInitialized(refreshedInteraction.getAnnotations()));
        Assert.assertTrue(IntactCore.isInitialized(xrefs));
        Assert.assertFalse(IntactCore.isInitialized(refreshedInteraction.getAliases()));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void ensuresInitializedAliases() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();

        getCorePersister().saveOrUpdate(interaction);

        Interaction refreshedInteraction = getDaoFactory().getInteractionDao().getByAc(interaction.getAc());

        Assert.assertFalse(IntactCore.isInitialized(refreshedInteraction.getAliases()));

        Collection<? extends Alias> aliases = IntactCore.ensureInitializedAliases(refreshedInteraction);

        Assert.assertFalse(IntactCore.isInitialized(refreshedInteraction.getAnnotations()));
        Assert.assertFalse(IntactCore.isInitialized(refreshedInteraction.getXrefs()));
        Assert.assertTrue(IntactCore.isInitialized(aliases));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void ensuresInitializedParticipant() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();
        getCorePersister().saveOrUpdate(interaction);

        Interaction refreshedInteraction = getDaoFactory().getInteractionDao().getByAc(interaction.getAc());

        Assert.assertFalse(IntactCore.isInitialized(refreshedInteraction.getComponents()));

        Collection<Component> components = IntactCore.ensureInitializedParticipants(refreshedInteraction);

        Assert.assertTrue(IntactCore.isInitialized(components));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void ensuresInitializedConfidence() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();
        interaction.addConfidence(getMockBuilder().createDeterministicConfidence());
        getCorePersister().saveOrUpdate(interaction);

        Interaction refreshedInteraction = getDaoFactory().getInteractionDao().getByAc(interaction.getAc());

        Assert.assertFalse(IntactCore.isInitialized(refreshedInteraction.getConfidences()));

        Collection<Confidence> confidences = IntactCore.ensureInitializedConfidences(refreshedInteraction);

        Assert.assertTrue(IntactCore.isInitialized(confidences));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void ensureInitializedSequence() throws Exception {
        Protein protein = getMockBuilder().createProteinRandom();
        getCorePersister().saveOrUpdate(protein);

        Protein refreshedProtein = getDaoFactory().getProteinDao().getByAc(protein.getAc());

        Assert.assertFalse(IntactCore.isInitialized(refreshedProtein.getSequenceChunks()));

        String sequence = IntactCore.ensureInitializedSequence(refreshedProtein);

        Assert.assertNotNull(sequence);
    }

}
