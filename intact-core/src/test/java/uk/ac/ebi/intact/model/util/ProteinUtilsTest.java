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
package uk.ac.ebi.intact.model.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Protein;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinUtilsTest extends IntactBasicTestCase {

    @Test
    public void getGeneName_default() throws Exception {
        Protein prot = getMockBuilder().createProtein("P0A6F1", "cara");
        prot.getAliases().clear();
        prot.getAliases().add(getMockBuilder().createAliasGeneName(prot, "carA"));

        assertNotNull(prot);

        String geneName = ProteinUtils.getGeneName(prot);
        
        assertNotNull(geneName);
        assertEquals("carA", geneName);
    }
}