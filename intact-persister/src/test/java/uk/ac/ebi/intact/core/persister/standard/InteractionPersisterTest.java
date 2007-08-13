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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.core.unit.IntactUnit;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionPersisterTest {

    @Before
    public void beforeTest() throws Exception {
        new IntactUnit().createSchema();
        beginTransaction();
    }

    @After
    public void afterTest() throws Exception {
        commitTransaction();
    }

    @Test
    public void allPersisted() throws Exception {
        IntactMockBuilder builder = new IntactMockBuilder(getIntactContext().getInstitution());
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
        IntactMockBuilder builder = new IntactMockBuilder(getIntactContext().getInstitution());
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

    protected DaoFactory getDaoFactory() {
         return IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
    }

    protected IntactContext getIntactContext() {
         return IntactContext.getCurrentInstance();
    }
    
    protected void beginTransaction() {
         IntactContext.getCurrentInstance().getDataContext().beginTransaction();
    }

    protected void commitTransaction() throws Exception {
         IntactContext.getCurrentInstance().getDataContext().commitTransaction();
    }
}