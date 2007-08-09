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
import uk.ac.ebi.intact.model.meta.ImexObject;
import uk.ac.ebi.intact.model.meta.ImexObjectStatus;
import uk.ac.ebi.intact.model.meta.ImexObjectAction;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ImexObjectDaoTest extends IntactBasicTestCase {

    private ImexObjectDao imexObjectDao;

    @Before
    public void prepareTest() throws Exception {
        new IntactUnit().createSchema();
        beginTransaction();

        this.imexObjectDao = getDaoFactory().getImexObjectDao();
    }

    @After
    public void endTest() throws Exception {
        commitTransaction();

        this.imexObjectDao = null;
    }

    @Test
    public void persist_default() throws Exception {

        ImexObject imex1 = new ImexObject(getIntactContext().getInstitution(), "1234567", ImexObjectStatus.OK, ImexObjectAction.I);

        imexObjectDao.persist(imex1);

        commitTransaction();
        beginTransaction();

        Assert.assertEquals(1, imexObjectDao.countAll());

        
    }
}