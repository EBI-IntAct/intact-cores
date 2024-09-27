/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.model.util.filter;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.*;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class XrefCvFilterTest extends IntactBasicTestCase {

    @Test
    public void accept_inclusionsAndExclusions() {
        Protein prot = getMockBuilder().createProteinRandom();

        CvDatabase intact = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);
        Xref xref = getMockBuilder().createIdentityXref(prot, "EBI-12345", intact);

        CvObjectFilterGroup databaseFilterGroup = new CvObjectFilterGroup();
        databaseFilterGroup.addExcludedIdentifier(CvDatabase.INTACT_MI_REF);

        CvObjectFilterGroup qualifierFilterGroup = new CvObjectFilterGroup();
        qualifierFilterGroup.addIncludedIdentifier(CvXrefQualifier.IDENTITY_MI_REF);

        XrefCvFilter xrefFilter = new XrefCvFilter(databaseFilterGroup, qualifierFilterGroup);

        Assert.assertFalse(xrefFilter.accept(xref));


    }

    @Test
    public void accept_someInclusions() {
        Protein prot = getMockBuilder().createProteinRandom();

        CvDatabase intact = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);
        Xref xref = getMockBuilder().createIdentityXref(prot, "EBI-12345", intact);

        CvObjectFilterGroup databaseCvFilterGroup = new CvObjectFilterGroup();
        databaseCvFilterGroup.addIncludedIdentifier(CvDatabase.INTACT_MI_REF);

        CvObjectFilterGroup qualifierCvFilterGroup = new CvObjectFilterGroup();
        qualifierCvFilterGroup.addIncludedIdentifier(CvXrefQualifier.IDENTITY_MI_REF);

        XrefCvFilter xrefFilter = new XrefCvFilter(databaseCvFilterGroup, qualifierCvFilterGroup);

        Assert.assertTrue(xrefFilter.accept(xref));
     }

    @Test
    public void accept_qualifier_null() {
        Protein prot = getMockBuilder().createProteinRandom();

        CvDatabase intact = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);
        Xref xref = getMockBuilder().createIdentityXref(prot, "EBI-12345", intact);

        CvObjectFilterGroup databaseCvFilterGroup = new CvObjectFilterGroup();
        databaseCvFilterGroup.addIncludedIdentifier(CvDatabase.INTACT_MI_REF);

        CvObjectFilterGroup qualifierCvFilterGroup = new CvObjectFilterGroup();
        qualifierCvFilterGroup.addIncludedIdentifier(null);

        XrefCvFilter xrefFilter = new XrefCvFilter(databaseCvFilterGroup, qualifierCvFilterGroup);

        Assert.assertTrue(xrefFilter.accept(xref));
     }

    @Test
    public void accept_identity() {
        Protein prot = getMockBuilder().createProteinRandom();
        CvDatabase intactDb = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);
        final InteractorXref xref = getMockBuilder().createIdentityXref(prot, "EBI-1234", intactDb);

        CvObjectFilterGroup databaseCvFilterGroup = new CvObjectFilterGroup();

        CvObjectFilterGroup qualifierCvFilterGroup = new CvObjectFilterGroup();
        qualifierCvFilterGroup.addIncludedIdentifier(CvXrefQualifier.IDENTITY_MI_REF);

        XrefCvFilter xrefFilter = new XrefCvFilter(databaseCvFilterGroup, qualifierCvFilterGroup);

        Assert.assertTrue(xrefFilter.accept(xref));
     }

//    private IntactMockBuilder getMockBuilder() {
//        return new IntactMockBuilder(new Institution("testInstitution"));
//    }
}
