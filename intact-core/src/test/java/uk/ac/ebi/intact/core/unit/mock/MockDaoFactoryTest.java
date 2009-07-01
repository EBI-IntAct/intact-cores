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
package uk.ac.ebi.intact.core.unit.mock;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.unit.IntactAbstractMockTestCase;
import uk.ac.ebi.intact.persistence.dao.ProteinDao;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class MockDaoFactoryTest extends IntactAbstractMockTestCase  {

    @Before
    public void prepareDAOs() throws Exception {
        MockIntactContext.configureMockDaoFactory().setMockProteinDao(new DummyProteinDao());
    }

    @Test
    public void methodImplementedInDao() throws Exception {
        ProteinDao proteinDao = IntactContext.getCurrentInstance().getDataContext()
                .getDaoFactory().getProteinDao();

        assertEquals(75, proteinDao.countUniprotProteins());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void methodNotImplementedInDao() throws Exception {
        ProteinDao proteinDao = IntactContext.getCurrentInstance().getDataContext()
                .getDaoFactory().getProteinDao();

        proteinDao.countAll();
    }

    @Test(expected = IllegalMockDaoException.class)
    public void daoNotRegistered() throws Exception {
        IntactContext.getCurrentInstance().getDataContext()
                .getDaoFactory().getExperimentDao();
    }

    private class DummyProteinDao extends MockProteinDao {

        @Override
        public Integer countUniprotProteins() {
            return 75;
        }
    }
}