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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.CvXrefQualifier;
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

    @Test
    public void isFromUniprot() throws Exception {
        Protein uniprotProt = getMockBuilder().createProtein("P12344", "lala");

        Protein nonUniprotProt = getMockBuilder().createProteinRandom();
        nonUniprotProt.getXrefs().clear();
        nonUniprotProt.addAnnotation(getMockBuilder().createAnnotation("nonUniprot", null, CvTopic.NON_UNIPROT));

        Assert.assertTrue(ProteinUtils.isFromUniprot(uniprotProt));
        Assert.assertFalse(ProteinUtils.isFromUniprot(nonUniprotProt));
    }

    @Test
    public void isSpliceVariant_true() throws Exception {
        Protein prot = getMockBuilder().createProteinRandom();

        CvXrefQualifier isoformParent = getMockBuilder().createCvObject(CvXrefQualifier.class, CvXrefQualifier.ISOFORM_PARENT_MI_REF, CvXrefQualifier.ISOFORM_PARENT);
        CvDatabase uniprotKb = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.UNIPROT_MI_REF, CvDatabase.UNIPROT);

        prot.addXref(getMockBuilder().createXref(prot, "parentProtUniprotId", isoformParent, uniprotKb));

        Assert.assertTrue(ProteinUtils.isSpliceVariant(prot));
    }
    
    @Test
    public void isSpliceVariant_false() throws Exception {
        Protein prot = getMockBuilder().createProteinRandom();
        Assert.assertFalse(ProteinUtils.isSpliceVariant(prot));
    }
}