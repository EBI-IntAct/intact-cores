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

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.model.*;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionPersisterTest extends AbstractPersisterTest
{

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
    }

    @Test
    public void aliasPersisted() throws Exception {
        IntactMockBuilder builder = super.getMockBuilder();
        Interaction interaction = builder.createInteractionRandomBinary();

        InteractionPersister interactorPersister = InteractionPersister.getInstance();

        interactorPersister.saveOrUpdate(interaction);
        interactorPersister.commit();

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

        InteractionPersister interactorPersister = InteractionPersister.getInstance();

        interactorPersister.saveOrUpdate(interaction);
        interactorPersister.commit();

        beginTransaction();

        System.out.println(getDaoFactory().getInstitutionDao().getAll());
        Assert.assertEquals(1, getDaoFactory().getInstitutionDao().countAll());
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
        Experiment interaction = getMockBuilder().createExperimentEmpty();
        interaction.addAnnotation(getMockBuilder().createAnnotationRandom());

        PersisterHelper.saveOrUpdate(interaction);
    }

}