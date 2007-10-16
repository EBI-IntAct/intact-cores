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
package uk.ac.ebi.intact.persistence.dao;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactUnit;
import uk.ac.ebi.intact.model.meta.ImexImport;
import uk.ac.ebi.intact.model.meta.ImexImportStatus;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ImexImportDaoTest extends IntactBasicTestCase {

    private ImexImportDao imexImportDao;

    @Before
    public void prepareTest() throws Exception {
        new IntactUnit().createSchema();
        beginTransaction();

        this.imexImportDao = getDaoFactory().getImexImportDao();
    }

    @After
    public void endTest() throws Exception {
        commitTransaction();

        this.imexImportDao = null;
    }

    @Test
    public void persist_default() throws Exception {

        ImexImport imex1 = new ImexImport(getIntactContext().getInstitution(), "1234567", ImexImportStatus.OK);

        imexImportDao.persist(imex1);

        commitTransaction();
        beginTransaction();

        Assert.assertEquals(1, imexImportDao.countAll());
        Assert.assertNotNull(imexImportDao.getByPmid("1234567"));
    }

    @Test
    public void getByPmid() throws Exception {

        ImexImport imex1 = new ImexImport(getIntactContext().getInstitution(), "1234567", ImexImportStatus.OK);

        imexImportDao.persist(imex1);

        commitTransaction();
        beginTransaction();

        final ImexImport imex = imexImportDao.getByPmid("1234567");
        Assert.assertNotNull(imex);
        Assert.assertEquals(ImexImportStatus.OK, imex.getStatus());
    }
    
    @Test
    public void getAllAndFailed() throws Exception {

        ImexImport imex1 = new ImexImport(getIntactContext().getInstitution(), "1234567", ImexImportStatus.OK);
        ImexImport imex2 = new ImexImport(getIntactContext().getInstitution(), "1234568", ImexImportStatus.ERROR);

        imexImportDao.persist(imex1);
        imexImportDao.persist(imex2);

        commitTransaction();
        beginTransaction();

        Assert.assertEquals(2, imexImportDao.getAllPmids().size());
        Assert.assertEquals(1, imexImportDao.getAllOkPmids().size());
        Assert.assertEquals(1, imexImportDao.getFailed().size());
    }
}