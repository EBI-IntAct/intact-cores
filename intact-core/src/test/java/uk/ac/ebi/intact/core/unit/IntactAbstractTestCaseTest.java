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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import org.junit.Test;
import uk.ac.ebi.intact.config.impl.InMemoryDataConfig;
import uk.ac.ebi.intact.unitdataset.PsiTestDatasetProvider;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactAbstractTestCaseTest extends IntactAbstractTestCase {
    
    @Test
    public void defaultEmpty() throws Exception {
        assertTrue("Transaction must be active", getDataContext().isTransactionActive());
        assertEquals(1, getDataContext().getDaoFactory().getInstitutionDao().countAll());

        assertEquals(getIntactContext().getConfig().getDefaultDataConfig().getName(), InMemoryDataConfig.NAME);
    }

    @Test
    @IntactUnitDataset(provider = PsiTestDatasetProvider.class, dataset = PsiTestDatasetProvider.ALL_CVS)
    public void default_allCVs() throws Exception {
        assertTrue("Transaction must be active", getDataContext().isTransactionActive());
        
        assertEquals(1, getDataContext().getDaoFactory().getInstitutionDao().countAll());
        assertEquals("Wrong number of CvObjects", 973, getDataContext().getDaoFactory().getCvObjectDao().countAll());

        assertEquals(getIntactContext().getConfig().getDefaultDataConfig().getName(), InMemoryDataConfig.NAME);
    }
}