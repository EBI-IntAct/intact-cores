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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.*;

/** *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactCoreTest extends IntactBasicTestCase{

    @Test
    @Transactional(propagation = Propagation.NEVER)
    @DirtiesContext
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
    @Transactional(propagation = Propagation.NEVER)
    @DirtiesContext
    public void testIsInitialized_no() throws Exception {
        TransactionStatus transaction = getDataContext().beginTransaction();

        Interaction interaction = getMockBuilder().createInteractionRandomBinary();

        getCorePersister().saveOrUpdate(interaction);

        getDataContext().commitTransaction(transaction);

        TransactionStatus transaction2 = getDataContext().beginTransaction();

        Interaction refreshedInteraction = getDaoFactory().getInteractionDao().getByAc(interaction.getAc());

        getDataContext().commitTransaction(transaction2);

        Assert.assertFalse(IntactCore.isInitialized(refreshedInteraction.getComponents()));
        Assert.assertFalse(IntactCore.isInitialized(refreshedInteraction.getConfidences()));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    @DirtiesContext
    public void testIsInitializedAndDirty_yes() throws Exception {
        TransactionStatus transaction = getDataContext().beginTransaction();

        Interaction interaction = getMockBuilder().createInteractionRandomBinary();

        getCorePersister().saveOrUpdate(interaction);

        getDataContext().commitTransaction(transaction);

        TransactionStatus transaction2 = getDataContext().beginTransaction();

        Interaction refreshedInteraction = getDaoFactory().getInteractionDao().getByAc(interaction.getAc());
        refreshedInteraction.addComponent(getMockBuilder().createComponentRandom());

        Assert.assertTrue(IntactCore.isInitializedAndDirty(refreshedInteraction.getComponents()));
        Assert.assertFalse(IntactCore.isInitializedAndDirty(refreshedInteraction.getConfidences()));

        getDataContext().commitTransaction(transaction2);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    @DirtiesContext
    public void testIsInitializedAndDirty_yes2() throws Exception {
        TransactionStatus transaction = getDataContext().beginTransaction();

        Interaction interaction = getMockBuilder().createInteractionRandomBinary();

        getCorePersister().saveOrUpdate(interaction);

        getDataContext().commitTransaction(transaction);

        TransactionStatus transaction2 = getDataContext().beginTransaction();

        Interaction refreshedInteraction = getDaoFactory().getInteractionDao().getByAc(interaction.getAc());
        refreshedInteraction.getComponents().iterator().next().getBindingDomains().clear();

        Assert.assertFalse(IntactCore.isInitializedAndDirty(refreshedInteraction.getComponents()));
        Assert.assertFalse(IntactCore.isInitializedAndDirty(refreshedInteraction.getConfidences()));

        getDataContext().commitTransaction(transaction2);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    @DirtiesContext
    public void testIsInitializedAndDirty_no() throws Exception {
         TransactionStatus transaction = getDataContext().beginTransaction();

        Interaction interaction = getMockBuilder().createInteractionRandomBinary();

        getCorePersister().saveOrUpdate(interaction);

        getDataContext().commitTransaction(transaction);

        TransactionStatus transaction2 = getDataContext().beginTransaction();

        Interaction refreshedInteraction = getDaoFactory().getInteractionDao().getByAc(interaction.getAc());

        getDataContext().commitTransaction(transaction2);

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
}
