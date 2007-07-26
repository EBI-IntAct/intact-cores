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
import uk.ac.ebi.intact.model.CvAliasType;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InteractorPersisterTest {

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
    public void aliasPersisted() throws Exception {
        IntactMockBuilder builder = new IntactMockBuilder(getIntactContext().getInstitution());
        Interactor interactor = builder.createProteinRandom();

        InteractorPersister interactorPersister = InteractorPersister.getInstance();

        interactorPersister.saveOrUpdate(interactor);
        interactorPersister.commit();

        beginTransaction();

        CvAliasType aliasType = getDaoFactory().getCvObjectDao(CvAliasType.class).getByPsiMiRef(CvAliasType.GENE_NAME_MI_REF);
        Assert.assertNotNull(aliasType);
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