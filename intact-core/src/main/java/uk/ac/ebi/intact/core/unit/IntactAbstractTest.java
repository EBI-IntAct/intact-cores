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
import uk.ac.ebi.intact.context.DataContext;
import uk.ac.ebi.intact.context.IntactContext;

/**
 * Base for all intact-tests.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactAbstractTest {

    @BeforeClass
    public static void begin() throws Exception {

    }

    @Before
    public void setUp() throws Exception {
        getDataContext().beginTransaction();

        IntactUnit iu = new IntactUnit();
        iu.resetSchema();
    }

    @After
    public void tearDown() throws Exception {
        getDataContext().commitTransaction();
        getDataContext().beginTransaction();

        IntactUnit iu = new IntactUnit();
        iu.dropSchema();

        getDataContext().commitTransaction();
    }

    @AfterClass
    public static void close() throws Exception {
        IntactContext.getCurrentInstance().close();
    }

    protected IntactContext getIntactContext() {
        return IntactContext.getCurrentInstance();
    }

    protected DataContext getDataContext() {
        return getIntactContext().getDataContext();
    }
}