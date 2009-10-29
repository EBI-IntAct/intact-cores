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
package uk.ac.ebi.intact.model.util;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.clone.IntactCloner;
import uk.ac.ebi.intact.model.util.filter.CvObjectFilterGroup;
import uk.ac.ebi.intact.model.util.filter.XrefCvFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class AnnotatedObjectUtilsTest {

    @Test
    public void searchXrefsByDatabase() throws Exception {
        CvDatabase cvDatabase = CvObjectUtils.createCvObject(new Institution("testInstitution"), CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);

        Collection<CvObjectXref> cvObjectXrefCollection = AnnotatedObjectUtils.searchXrefsByDatabase(cvDatabase, CvDatabase.PSI_MI_MI_REF);
        Assert.assertEquals(1, cvObjectXrefCollection.size());
        Assert.assertEquals(CvDatabase.INTACT_MI_REF, cvObjectXrefCollection.iterator().next().getPrimaryId());
    }

    @Test
    public void searchXrefsByQualifier() throws Exception {
        CvDatabase cvDatabase = CvObjectUtils.createCvObject(new Institution("testInstitution"), CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);

        Collection<CvObjectXref> cvObjectXrefCollection = AnnotatedObjectUtils.searchXrefsByQualifier(cvDatabase, CvXrefQualifier.IDENTITY_MI_REF);
        Assert.assertEquals(1, cvObjectXrefCollection.size());
        Assert.assertEquals(CvDatabase.INTACT_MI_REF, cvObjectXrefCollection.iterator().next().getPrimaryId());
    }
    
    @Test
    public void searchXrefs() throws Exception {
        CvDatabase cvDatabase = CvObjectUtils.createCvObject(new Institution("testInstitution"), CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);

        Collection<CvObjectXref> cvObjectXrefCollection = AnnotatedObjectUtils.searchXrefs(cvDatabase, CvDatabase.PSI_MI_MI_REF, CvXrefQualifier.IDENTITY_MI_REF);
        Assert.assertEquals(1, cvObjectXrefCollection.size());
        Assert.assertEquals(CvDatabase.INTACT_MI_REF, cvObjectXrefCollection.iterator().next().getPrimaryId());
    }

    @Test
    public void searchXrefs_usingCVs() throws Exception {
        final CvObjectBuilder builder = new CvObjectBuilder();
        CvXrefQualifier identity = builder.createIdentityCvXrefQualifier(getMockBuilder().getInstitution());
        CvDatabase uniprot = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.UNIPROT_MI_REF, CvDatabase.UNIPROT_MI_REF);

        Protein prot = getMockBuilder().createProtein("P12345", "nana");
        
        Collection<Xref> cvObjectXrefCollection = AnnotatedObjectUtils.searchXrefs(prot, uniprot, identity);
        Assert.assertEquals(1, cvObjectXrefCollection.size());
        Assert.assertEquals("P12345", cvObjectXrefCollection.iterator().next().getPrimaryId());
    }
    
    @Test
    public void searchXrefs_nullQualifier() throws Exception {
        CvDatabase cvDatabase = CvObjectUtils.createCvObject(new Institution("testInstitution"), CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);

        Collection<CvObjectXref> cvObjectXrefCollection = AnnotatedObjectUtils.searchXrefs(cvDatabase, CvDatabase.PSI_MI_MI_REF, null);
        Assert.assertEquals(1, cvObjectXrefCollection.size());
        Assert.assertEquals(CvDatabase.INTACT_MI_REF, cvObjectXrefCollection.iterator().next().getPrimaryId());
    }

    @Test
    public void containsTheSameIdentities_simple() throws Exception {
        CvDatabase cv1 = CvObjectUtils.createCvObject(new Institution("testInstitution"), CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);
        CvDatabase cv2 = CvObjectUtils.createCvObject(new Institution("testInstitution"), CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);

        CvObjectFilterGroup cvObjectFilterGroup = new CvObjectFilterGroup(true, false);
        XrefCvFilter xrefFilter = new XrefCvFilter(cvObjectFilterGroup);
        
        Assert.assertTrue(AnnotatedObjectUtils.containTheSameXrefs(xrefFilter, cv1, cv2));
    }

    @Test
    public void containsTheSameIdentities_qualIdentity() throws Exception {
        CvXrefQualifier secondaryAc = getMockBuilder().createCvObject(CvXrefQualifier.class, CvXrefQualifier.SECONDARY_AC_MI_REF, CvXrefQualifier.SECONDARY_AC);
        CvDatabase uniprotkb = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.UNIPROT_MI_REF, CvDatabase.UNIPROT);
        CvDatabase go = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.GO_MI_REF, CvDatabase.GO);

        Protein prot = getMockBuilder().createDeterministicProtein("P12345", "nana");
        prot.addXref(getMockBuilder().createXref(prot, "Q88334", secondaryAc, uniprotkb));
        prot.addXref(getMockBuilder().createXref(prot, "GO:123456", null, go));

        Protein prot2 = getMockBuilder().createDeterministicProtein("P12345", "nana");

        CvObjectFilterGroup databaseGroup = new CvObjectFilterGroup(false, false);

        CvObjectFilterGroup qualifierGroup = new CvObjectFilterGroup();
        qualifierGroup.addIncludedIdentifier(CvXrefQualifier.IDENTITY_MI_REF);

        XrefCvFilter xrefFilter = new XrefCvFilter(databaseGroup, qualifierGroup);

        Assert.assertTrue(AnnotatedObjectUtils.containTheSameXrefs(xrefFilter, prot, prot2));
    }

    @Test
    public void containsTheSameIdentities_simple_no() throws Exception {
        CvDatabase cv1 = CvObjectUtils.createCvObject(new Institution("testInstitution"), CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);
        CvDatabase cv2 = CvObjectUtils.createCvObject(new Institution("testInstitution"), CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);
        cv2.getXrefs().iterator().next().setPrimaryId("nana");

        CvObjectFilterGroup cvObjectFilterGroup = new CvObjectFilterGroup(true, false);
        XrefCvFilter xrefFilter = new XrefCvFilter(cvObjectFilterGroup);

        Assert.assertFalse(AnnotatedObjectUtils.containTheSameXrefs(xrefFilter, cv1, cv2));
    }

    @Test
    public void containsTheSameIdentities_filtered_yes() throws Exception {
        Protein prot = getMockBuilder().createProteinRandom();

        Protein prot2 = new IntactCloner().clone(prot);
        prot2.addXref(getMockBuilder().createIdentityXref(prot, "nanaPRIMARY",
                                                 getMockBuilder().createCvObject(CvDatabase.class, "db:0001", "nanadb")));

        CvObjectFilterGroup cvObjectFilterGroup = new CvObjectFilterGroup();
        cvObjectFilterGroup.addIncludedIdentifier(CvDatabase.UNIPROT_MI_REF);

        XrefCvFilter xrefFilter = new XrefCvFilter(cvObjectFilterGroup);

        Assert.assertTrue(AnnotatedObjectUtils.containTheSameXrefs(xrefFilter, prot, prot2));
    }

    @Test
    public void containsTheSameIdentities_filtered_yes_3() throws Exception {
        Protein prot = getMockBuilder().createProteinRandom();

        Protein prot2 = new IntactCloner().clone(prot);
        prot2.addXref(getMockBuilder().createIdentityXref(prot, "nanaPRIMARY",
                                                 getMockBuilder().createCvObject(CvDatabase.class, "db:0001", "nanadb")));

        Protein prot3 = new IntactCloner().clone(prot2);
        prot3.addXref(getMockBuilder().createIdentityXref(prot, "LOLO",
                                                 getMockBuilder().createCvObject(CvDatabase.class, "db:0002", "lolodb")));

        CvObjectFilterGroup cvObjectFilterGroup = new CvObjectFilterGroup();
        cvObjectFilterGroup.addIncludedIdentifier(CvDatabase.UNIPROT_MI_REF);

        XrefCvFilter xrefFilter = new XrefCvFilter(cvObjectFilterGroup);

        Assert.assertTrue(AnnotatedObjectUtils.containTheSameXrefs(xrefFilter, prot, prot2, prot3));
    }
    
    @Test
    public void containsTheSameIdentities_filtered_no_3() throws Exception {
        Protein prot = getMockBuilder().createProteinRandom();

        Protein prot2 = new IntactCloner().clone(prot);
        prot2.addXref(getMockBuilder().createIdentityXref(prot, "nanaPRIMARY",
                                                 getMockBuilder().createCvObject(CvDatabase.class, "db:0001", "nanadb")));

        Protein prot3 = new IntactCloner().clone(prot2);
        prot3.addXref(getMockBuilder().createIdentityXref(prot, "LOLO",
                                                 getMockBuilder().createCvObject(CvDatabase.class, "db:0002", "lolodb")));

        CvObjectFilterGroup cvObjectFilterGroup = new CvObjectFilterGroup();
        cvObjectFilterGroup.addIncludedIdentifier(CvXrefQualifier.IDENTITY_MI_REF);

        XrefCvFilter xrefFilter = new XrefCvFilter(new CvObjectFilterGroup(), cvObjectFilterGroup);

        Assert.assertFalse(AnnotatedObjectUtils.containTheSameXrefs(xrefFilter, prot, prot2, prot3));
    }

    @Test
    public void searchXrefs_xrefFilter_noFilter() throws Exception {
        Protein prot = getMockBuilder().createProteinRandom();
        prot.addXref(getMockBuilder().createIdentityXref(prot, "nanaPRIMARY",
                                                 getMockBuilder().createCvObject(CvDatabase.class, "db:0001", "nanadb")));

        CvObjectFilterGroup cvObjectFilterGroup = new CvObjectFilterGroup(true, false);
        XrefCvFilter xrefFilter = new XrefCvFilter(cvObjectFilterGroup);

        final Collection<InteractorXref> xrefs = AnnotatedObjectUtils.searchXrefs(prot, xrefFilter);
        Assert.assertEquals(2, xrefs.size());
    }
    
    @Test
    public void searchXrefs_xrefFilter_filter() throws Exception {
        Protein prot = getMockBuilder().createProteinRandom();
        prot.addXref(getMockBuilder().createIdentityXref(prot, "nanaPRIMARY",
                                                 getMockBuilder().createCvObject(CvDatabase.class, "db:0001", "nanadb")));

        CvObjectFilterGroup cvObjectFilterGroup = new CvObjectFilterGroup();
        cvObjectFilterGroup.addIncludedIdentifier(CvDatabase.UNIPROT_MI_REF);

        XrefCvFilter xrefFilter = new XrefCvFilter(cvObjectFilterGroup);

        final Collection<InteractorXref> xrefs = AnnotatedObjectUtils.searchXrefs(prot, xrefFilter);
        Assert.assertEquals(1, xrefs.size());
    }

    private IntactMockBuilder getMockBuilder() {
        return new IntactMockBuilder(new Institution("testInstitution"));
    }
    
    @Test
    public void findANnotationsByCvTopic() throws Exception {

        final CvInteraction interactionType = getMockBuilder().createCvObject( CvInteraction.class, "MI:xxxx", "bla" );

        final CvTopic usedInClass = getMockBuilder().createCvObject( CvTopic.class, "MI:0001", "used-in-class" );
        final CvTopic obsolete = getMockBuilder().createCvObject( CvTopic.class, "MI:0002", "obsolete" );
        final CvTopic comment = getMockBuilder().createCvObject( CvTopic.class, "MI:0003", "comment" );
        final CvTopic remark = getMockBuilder().createCvObject( CvTopic.class, "MI:0004", "internal-remark" );

        interactionType.addAnnotation( getMockBuilder().createAnnotation( "annot1", usedInClass ) );
        interactionType.addAnnotation( getMockBuilder().createAnnotation( "annot1", comment ) );
        interactionType.addAnnotation( getMockBuilder().createAnnotation( "annot1", comment ) );
        interactionType.addAnnotation( getMockBuilder().createAnnotation( "annot1", obsolete ) );

        Collection<Annotation> annotations = null;

        annotations = AnnotatedObjectUtils.findAnnotationsByCvTopic( interactionType,
                                                                     Arrays.asList( obsolete,
                                                                                    usedInClass,
                                                                                    remark ) );
        Assert.assertNotNull( annotations );
        Assert.assertEquals( 2, annotations.size() );

        annotations = AnnotatedObjectUtils.findAnnotationsByCvTopic( interactionType, new ArrayList<CvTopic>() );
        Assert.assertNotNull( annotations );
        Assert.assertEquals( 0, annotations.size() );

        try {
            AnnotatedObjectUtils.findAnnotationsByCvTopic( null, new ArrayList<CvTopic>() );
            Assert.fail( "AnnotatedObjectUtils.findANnotationsByCvTopic() should not allow null annotatedObject" );
        } catch ( Exception e ) {
            // ok
        }
    }

}
