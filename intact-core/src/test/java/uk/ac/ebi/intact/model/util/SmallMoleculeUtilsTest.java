/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
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
 * limitations under the License.
 */
package uk.ac.ebi.intact.model.util;

import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.SmallMolecule;
import uk.ac.ebi.intact.model.InteractorXref;
import org.junit.Test;
import org.junit.Assert;

/**
 * Test class for SmallMoleculeUtils
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since TODO specify the maven artifact version
 */
public class SmallMoleculeUtilsTest extends IntactBasicTestCase {

    @Test
    public void chebiXrefTest() {

        final SmallMolecule smallMolecule = getMockBuilder().createSmallMolecule( "CHEBI:16851", "pi35p2" );
        final InteractorXref chebiXref = SmallMoleculeUtils.getChebiXref( smallMolecule );
        Assert.assertEquals( "CHEBI:16851", chebiXref.getPrimaryId() );
    }
}
