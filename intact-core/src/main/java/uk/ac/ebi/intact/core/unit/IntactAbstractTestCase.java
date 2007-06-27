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
package uk.ac.ebi.intact.core.unit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.commons.dataset.DbUnitTestDataset;
import uk.ac.ebi.intact.commons.dataset.TestDataset;
import uk.ac.ebi.intact.commons.dataset.TestDatasetProvider;
import uk.ac.ebi.intact.context.DataContext;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import java.lang.reflect.Method;

/**
 * Base for all intact-tests.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@RunWith(IntactTestRunner.class)
public class IntactAbstractTestCase {

    @BeforeClass
    public static void begin() throws Exception {

    }

    @Before
    public void setUp() throws Exception {
        //getDataContext().beginTransaction();

        Method currentMethod = IntactTestRunner.getTestMethod();

        if (currentMethod == null) {
            throw new RuntimeException("This test cannot be run in IDEA");
        }

        IntactUnit iu = new IntactUnit();

        IgnoreDatabase ignoreDbAnnot = currentMethod.getAnnotation(IgnoreDatabase.class);

        IntactUnitDataset datasetAnnot = null;
        boolean loadDataset = false;

        // if the ignoreDbAnnot is present at method level, ignore the db, else
        if (ignoreDbAnnot == null) {
            // try to get the IntactUnitDataset annotation from the method, and then the class
            datasetAnnot = currentMethod.getAnnotation(IntactUnitDataset.class);

            if (datasetAnnot == null) {
                ignoreDbAnnot = currentMethod.getDeclaringClass().getAnnotation(IgnoreDatabase.class);

                if (ignoreDbAnnot == null) {
                    datasetAnnot = currentMethod.getDeclaringClass().getAnnotation(IntactUnitDataset.class);

                    if (datasetAnnot != null) {
                        loadDataset = true;
                    } else {
                        iu.createSchema();
                    }
                }
            } else {
                loadDataset = true;
            }
        }

        if (loadDataset) {
            if (datasetAnnot != null) {
                TestDataset testDataset = getTestDataset(datasetAnnot);

                if (testDataset instanceof DbUnitTestDataset) {
                    iu.createSchema(false);
                    getDataContext().beginTransaction();
                    iu.importTestDataset((DbUnitTestDataset) testDataset);
                } else {
                    throw new IntactTestException("Cannot import TestDatasets of type: " + testDataset.getClass().getName());
                }

                getDataContext().commitTransaction();
            } else {
                iu.createSchema();
            }
            getDataContext().commitTransaction();
        }

        getDataContext().beginTransaction();
    }

    private TestDataset getTestDataset(IntactUnitDataset datasetAnnot) throws Exception {
        TestDatasetProvider provider = datasetAnnot.provider().newInstance();
        return provider.getTestDataset(datasetAnnot.dataset());
    }

    @After
    public void tearDown() throws Exception {
        getDataContext().commitTransaction();

        //IntactUnit iu = new IntactUnit();
        //iu.dropSchema();
    }

    @AfterClass
    public static void close() throws Exception {
        if (IntactContext.currentInstanceExists()) {
            IntactContext.getCurrentInstance().close();
        }
    }

    protected void beginTransaction() {
        getDataContext().beginTransaction();
    }

    protected void commitTransaction() throws IntactTestException {
        if (getDataContext().isTransactionActive()) {
            try {
                getDataContext().commitTransaction();
            } catch (IntactTransactionException e) {
                throw new IntactTestException(e);
            }
        }
    }

    protected IntactContext getIntactContext() {
        return IntactContext.getCurrentInstance();
    }

    protected DataContext getDataContext() {
        return getIntactContext().getDataContext();
    }

    protected DaoFactory getDaoFactory() {
        return getDataContext().getDaoFactory();
    }

}