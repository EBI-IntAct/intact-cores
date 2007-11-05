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

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.Interaction;

import java.util.Arrays;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class CrcCalculatorTest extends IntactBasicTestCase {

    @Test
    public void crc_default() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionFooBar();

        CrcCalculator crcCalculator = new CrcCalculator();

        //System.out.println(crcCalculator.crc64(interaction));
        //System.out.println(crcCalculator.createUniquenessString(interaction));

        Assert.assertEquals("51E12A43101D5171", crcCalculator.crc64(interaction));
        Assert.assertEquals("a1|0.0MI:0499|MI:0498|MI:0396|MI:0350|a2|0.0MI:0499|MI:0496|MI:0505|MI:0338|1-1|MI:0338|5-5|MI:0396|MI:0350|foobar-2006-1|MI:0396|MI:0027|5|MI:0407|MI:0612|This is an annotation|",
                            crcCalculator.createUniquenessString(interaction).toString());
    }

    @Test
    public void crc_different() throws Exception {
        Interaction interaction1 = getMockBuilder().createInteractionRandomBinary();
        Interaction interaction2 = getMockBuilder().createInteractionRandomBinary();

        CrcCalculator crcCalculator = new CrcCalculator();

        Assert.assertFalse(crcCalculator.crc64(interaction1).equals(crcCalculator.crc64(interaction2)));
    }

    @Test
    public void crc_different_exp() throws Exception {
        Interaction interaction1 = getMockBuilder().createInteractionFooBar();
        Interaction interaction2 = getMockBuilder().createInteractionFooBar();
        interaction2.getExperiments().add(getMockBuilder().createExperimentEmpty("lala-1714"));

        CrcCalculator crcCalculator = new CrcCalculator();

        Assert.assertFalse(crcCalculator.crc64(interaction1).equals(crcCalculator.crc64(interaction2)));
    }

    @Test
    public void crc_different_sameExpDifferentShortLabel() throws Exception {
        Interaction interaction1 = getMockBuilder().createInteractionFooBar();
        Interaction interaction2 = getMockBuilder().createInteractionFooBar();
        interaction2.getExperiments().iterator().next().setShortLabel("lala-1320-2");

        CrcCalculator crcCalculator = new CrcCalculator();

        Assert.assertFalse(crcCalculator.crc64(interaction1).equals(crcCalculator.crc64(interaction2)));
    }

    @Test
    public void crc_different_smallMolecules() throws Exception {
        Interaction interaction1 = getMockBuilder().createInteractionFooBar();
        Component c1_1 = interaction1.getComponents().iterator().next();
        Component sm1 = getMockBuilder().createComponentPrey(getMockBuilder().createSmallMoleculeRandom());
        interaction1.setComponents(Arrays.asList(c1_1, sm1));

        Interaction interaction2 = getMockBuilder().createInteractionFooBar();
        Component c2_1 = interaction1.getComponents().iterator().next();
        Component sm2 = getMockBuilder().createComponentPrey(getMockBuilder().createSmallMoleculeRandom());
        interaction1.setComponents(Arrays.asList(c2_1, sm2));

        CrcCalculator crcCalculator = new CrcCalculator();

        Assert.assertFalse(crcCalculator.crc64(interaction1).equals(crcCalculator.crc64(interaction2)));
    }

    @Test
    public void crc_pdbXref_same() throws Exception {
        CvDatabase pdb = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.RCSB_PDB_MI_REF, CvDatabase.RCSB_PDB);

        Interaction interaction1 = getMockBuilder().createInteractionFooBar();
        interaction1.getXrefs().add(getMockBuilder().createIdentityXref(interaction1, "pdb1", pdb));
        Interaction interaction2 = getMockBuilder().createInteractionFooBar();
        interaction2.getXrefs().add(getMockBuilder().createIdentityXref(interaction2, "pdb1", pdb));

        CrcCalculator crcCalculator = new CrcCalculator();

        Assert.assertEquals(crcCalculator.crc64(interaction1),crcCalculator.crc64(interaction2));
    }

    @Test
    public void crc_pdbXref_notSame() throws Exception {
        CvDatabase pdb = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.RCSB_PDB_MI_REF, CvDatabase.RCSB_PDB);

        Interaction interaction1 = getMockBuilder().createInteractionFooBar();
        interaction1.getXrefs().add(getMockBuilder().createIdentityXref(interaction1, "pdb1", pdb));
        Interaction interaction2 = getMockBuilder().createInteractionFooBar();
        interaction2.getXrefs().add(getMockBuilder().createIdentityXref(interaction1, "pdb2", pdb));

        CrcCalculator crcCalculator = new CrcCalculator();

        Assert.assertFalse(crcCalculator.crc64(interaction1).equals(crcCalculator.crc64(interaction2)));
    }

}