/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
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
 *  limitations under the License.
 */
package uk.ac.ebi.intact.persistence.svc.impl;

import junit.framework.TestCase;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.InteractionImpl;
import uk.ac.ebi.intact.persistence.svc.SearchService;

/**
 * Test for <code>SearchableDaoImplTest</code>
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 10/10/2006
 */
public class SimpleSearchServiceTest extends TestCase {

    private SearchService searchService;

    public void setUp() throws Exception {
        super.setUp();
        searchService = new SimpleSearchService();
    }

    public void tearDown() throws Exception {
        super.tearDown();
        IntactContext.getCurrentInstance().getDataContext().commitAllActiveTransactions();
        searchService = null;
    }

    public void testCount_experiment_all() throws Exception {
        int count = searchService.count( Experiment.class, "*" );
        assertEquals( 6, count );
    }

    public void testCount_cvobjects_all() throws Exception {
        int count = searchService.count( CvObject.class, "*" );
        System.out.println( count );
    }

    public void testCount_interaction_cara() throws Exception {
        int count = searchService.count( InteractionImpl.class, "cara" );
        assertEquals( 1, count );
    }

    public void testCount_interaction_cara_uppercase() throws Exception {
        int count = searchService.count( InteractionImpl.class, "CARA" );
        assertEquals( 1, count );
    }

}
