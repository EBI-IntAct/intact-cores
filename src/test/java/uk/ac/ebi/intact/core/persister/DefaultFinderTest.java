package uk.ac.ebi.intact.core.persister;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.clone.IntactCloner;

import javax.persistence.FlushModeType;

/**
 * DefaultFinder Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.8.0
 */
public class DefaultFinderTest extends IntactBasicTestCase {

    @Autowired
    private Finder finder;

    @BeforeTransaction
    public void beforeTransaction() {
        getEntityManager().setFlushMode(FlushModeType.COMMIT);
    }

    @AfterTransaction
    public void afterTransaction() {
        getEntityManager().setFlushMode(FlushModeType.AUTO);
    }

    @Test
    public void findAcForInstitution_byAc() throws Exception {
        final Institution i = getMockBuilder().createInstitution( "MI:xxxx", "ebi" );
        getPersisterHelper().save( i );
        final String originalAc = i.getAc();

        Institution empty = new Institution( "bla" );
        empty.setAc( originalAc );
        String ac = finder.findAc( empty );
        Assert.assertNotNull( ac );
        Assert.assertEquals( i.getAc(), ac );
    }

    @Test
    public void findAcForInstitution() throws Exception {
        final Institution i = getMockBuilder().createInstitution( "MI:xxxx", "ebi" );
        getPersisterHelper().save( i );

        String ac = finder.findAc( getMockBuilder().createInstitution( "MI:xxxx", "ebi" ) );
        Assert.assertNotNull( ac );
        Assert.assertEquals( i.getAc(), ac );

        // cannot be found
        Assert.assertNull( finder.findAc( getMockBuilder().createInstitution( "MI:zzzz", "lala" ) ) );
    }

    @Test
    public void findAcForPublication_byAc() {
        final Publication p = getMockBuilder().createPublication( "123456789" );
        getPersisterHelper().save( p );
        final String originalAc = p.getAc();

        Publication empty = getMockBuilder().createPublication( "123456789" );
        empty.setAc( originalAc );
        String ac = finder.findAc( empty );
        Assert.assertNotNull( ac );
        Assert.assertEquals( p.getAc(), ac );
    }

    @Test
    public void findAcForPublication() {
        final Publication p = getMockBuilder().createPublication( "123456789" );
        getPersisterHelper().save( p );

        final String ac = finder.findAc( getMockBuilder().createPublication( "123456789" ) );
        Assert.assertNotNull( ac );
        Assert.assertEquals( p.getAc(), ac );

        Assert.assertNull( finder.findAc( getMockBuilder().createPublication( "987654321" ) ) );
    }

    @Test
    public void findAcForExperiment_byAc() {
        final Experiment i = getMockBuilder().createDeterministicExperiment();
        getPersisterHelper().save( i );
        final String originalAc = i.getAc();

        Institution empty = new Institution( "bla" );
        empty.setAc( originalAc );
        String ac = finder.findAc( empty );
        Assert.assertNotNull( ac );
        Assert.assertEquals( i.getAc(), ac );
    }

    @Test
    public void findAcForExperiment() throws Exception {
        final Experiment e = getMockBuilder().createExperimentEmpty( "bruno-2007-1", "123456789" );
        e.addAnnotation(getMockBuilder().createAnnotation("annot1", "IA:0001", "topic1"));
        getPersisterHelper().save( e );

        IntactCloner cloner = new IntactCloner();
        cloner.setExcludeACs(true);

        String ac = finder.findAc( cloner.clone(e) );
        Assert.assertNotNull( ac );
        Assert.assertEquals( e.getAc(), ac );

        Assert.assertNull( finder.findAc( getMockBuilder().createExperimentEmpty( "samuel-2007-1", "123" ) ) );
    }

    @Test
    public void findAcForExperiment_diffAnnotations_sameNumOfAnnots() throws Exception {
        final Experiment exp1 = getMockBuilder().createExperimentEmpty( "bruno-2007-1", "123456789" );
        exp1.addAnnotation(getMockBuilder().createAnnotation("annot1", "IA:0001", "topic1"));

        getPersisterHelper().save( exp1 );

        IntactCloner cloner = new IntactCloner();
        cloner.setExcludeACs(true);

        final Experiment exp2 = cloner.clone(exp1);
        exp2.getAnnotations().clear();
        exp2.addAnnotation(getMockBuilder().createAnnotation("annot2", "IA:0001", "topic1"));

        String ac = finder.findAc(exp2);
        Assert.assertNull( ac );
    }

    @Test
    public void findAcForExperiment_diffAnnotations_diffNumOfAnnots() throws Exception {
        final Experiment exp1 = getMockBuilder().createExperimentEmpty( "bruno-2007-1", "123456789" );
        exp1.addAnnotation(getMockBuilder().createAnnotation("annot1", "IA:0001", "topic1"));
        exp1.addAnnotation(getMockBuilder().createAnnotation("annot2", "IA:0001", "topic1"));

        getPersisterHelper().save( exp1 );

        IntactCloner cloner = new IntactCloner();
        cloner.setExcludeACs(true);

        final Experiment exp2 = cloner.clone(exp1);
        exp2.getAnnotations().clear();
        exp2.addAnnotation(getMockBuilder().createAnnotation("annot2", "IA:0001", "topic1"));

        String ac = finder.findAc(exp2);
        Assert.assertNull( ac );
    }

    @Test
    public void findAcForExperiment_noPublicationObject() throws Exception {
        final Experiment e = getMockBuilder().createExperimentEmpty( "bruno-2007-1", "unassigned" );
        e.setPublication(null);
        getPersisterHelper().save( e );

        IntactCloner cloner = new IntactCloner();
        cloner.setExcludeACs(true);

        final Experiment experimentWithoutPub = cloner.clone(e);
        experimentWithoutPub.setPublication(null);

        String ac = finder.findAc( experimentWithoutPub );
        Assert.assertNotNull( ac );
        Assert.assertEquals( e.getAc(), ac );

    }

    @Test
    public void findAcForInteraction_byAc() throws Exception {
        final Interaction i = getMockBuilder().createDeterministicInteraction();
        getPersisterHelper().save( i );
        final String originalAc = i.getAc();

        Interaction empty = getMockBuilder().createDeterministicInteraction();
        empty.setAc( originalAc );
        String ac = finder.findAc( empty );
        Assert.assertNotNull( ac );
        Assert.assertEquals( i.getAc(), ac );
    }

    @Test
    public void findAcForInteraction() throws Exception {
        final Interaction i = getMockBuilder().createDeterministicInteraction();
        getPersisterHelper().save( i );

        final String ac = finder.findAc( getMockBuilder().createDeterministicInteraction() );
        Assert.assertNotNull( ac );
        Assert.assertEquals( i.getAc(), ac );

        Assert.assertNull( finder.findAc( getMockBuilder().createInteraction( "P12345", "Q98765", "P78634" ) ) );
    }

    @Test
    public void findAcForInteractor_byAc() {
        final Protein p = getMockBuilder().createProtein( "P12345", "foo" );
        getPersisterHelper().save( p );
        final String originalAc = p.getAc();

        Protein empty = getMockBuilder().createProtein( "P12345", "foo" );
        empty.setAc( originalAc );
        String ac = finder.findAc( empty );
        Assert.assertNotNull( ac );
        Assert.assertEquals( p.getAc(), ac );
    }

    @Test
    public void findAcForInteractor_acAsPrimaryId() {
        final Protein p = getMockBuilder().createProtein( "P12345", "foo" );
        getPersisterHelper().save( p );
        final String originalAc = p.getAc();

        Assert.assertEquals(1, getDaoFactory().getProteinDao().countAll());

        Protein protSameAc = getMockBuilder().createProteinRandom();
        protSameAc.getXrefs().clear();
        CvDatabase intactDb = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT);
        protSameAc.addXref(getMockBuilder().createIdentityXref(protSameAc, originalAc, intactDb));

        String ac = finder.findAc( protSameAc );
        Assert.assertNotNull( ac );
        Assert.assertEquals( p.getAc(), ac );
    }

    @Test
    public void findAcForInteractor_uniprot_identity() {
        final Protein p = getMockBuilder().createProtein( "P12345", "foo" );
        getPersisterHelper().save( p );

        Assert.assertEquals(1, getDaoFactory().getProteinDao().countAll());

        // same xref, different shorltabel -> should work
        String ac = finder.findAc( getMockBuilder().createProtein( "P12345", "abcd" ) );
        Assert.assertNotNull( ac );
        Assert.assertEquals( p.getAc(), ac );

        // no xref, same label -> should work
        final Protein protNoXref = getMockBuilder().createProtein("removed", "foo");
        protNoXref.getXrefs().clear();
        ac = finder.findAc(protNoXref);

        Assert.assertNotNull( ac );
        Assert.assertEquals( p.getAc(), ac );

        // different uniprot id and same shortlabel
        Assert.assertNull( finder.findAc( getMockBuilder().createProtein( "Q98765", "foo" ) ) );

        // different uniprot id and shortlabel
        Assert.assertNull( finder.findAc( getMockBuilder().createProtein( "Q98765", "bar" ) ) );

        getEntityManager().clear();

        // same xrefs but different type, should not work
        final SmallMolecule sm = getMockBuilder().createSmallMoleculeRandom();
        sm.getXrefs().clear();
        for ( InteractorXref xref : p.getXrefs() ) {
            sm.addXref( xref );
        }
        Assert.assertNull( finder.findAc( sm ) );
    }

    @Test
    public void findAcForInteractor_uniprot_identity_differentDbRelease() {
        final Protein p = getMockBuilder().createProtein( "P12345", "foo" );
        p.getXrefs().iterator().next().setDbRelease("unique_dbrelease");
        getPersisterHelper().save( p );

        Assert.assertEquals(1, getDaoFactory().getProteinDao().countAll());

        // same xref, different dbreleas -> should work
        String ac = finder.findAc( getMockBuilder().createProtein( "P12345", "abcd" ) );
        Assert.assertNotNull( ac );
        Assert.assertEquals( p.getAc(), ac );
    }

    @Test
    public void findAcForInteractor_other_identity() {
        // small molecule doesn't not have a uniprot identity, we then fall back onto other identity (minus intact, dip, dip)
        final SmallMolecule sm = getMockBuilder().createSmallMolecule( "CHEBI:0001", "nice molecule" );
        getPersisterHelper().save( sm );

        // same xref, different shorltabel -> should work
        String ac = finder.findAc( getMockBuilder().createSmallMolecule( "CHEBI:0001", "nice molecule" ) );
        Assert.assertNotNull( ac );
        Assert.assertEquals( sm.getAc(), ac );

        // different xref, same shorltabel -> should NOT work
        Assert.assertNull( finder.findAc( getMockBuilder().createSmallMolecule( "CHEBI:9999", "nice molecule" ) ));

        // different xref, different shorltabel -> should NOT work
        Assert.assertNull( finder.findAc( getMockBuilder().createSmallMolecule( "CHEBI:555555", " another nice molecule" ) ) );
    }

    @Test
    public void findAcForInteractor_multipleIdentities() throws Exception {
        IntactCloner cloner = new IntactCloner();
        cloner.setExcludeACs(true);

        // p has one xref to uniprot
        final Protein p = getMockBuilder().createProtein( "P12345", "foo" );
        
        // p2 has two identity xrefs to uniprot
        final Protein p2 = getMockBuilder().createProtein( "P12345", "foo" );
        p2.addXref(getMockBuilder().createIdentityXrefUniprot(p2, "Q54321"));
        getPersisterHelper().save( p, p2 );

        Assert.assertEquals(2, getDaoFactory().getProteinDao().countAll());
        Assert.assertEquals(3, getDaoFactory().getXrefDao(InteractorXref.class).countAll());

        // one xref - P12345 - should be there
        String ac = finder.findAc( getMockBuilder().createProtein( "P12345", "abcd" ) );
        Assert.assertNotNull( ac );
        Assert.assertEquals( p.getAc(), ac );

        // one xref - Q54321 - should not be found, as only P12345+Q54321 should be found
        ac = finder.findAc( getMockBuilder().createProtein( "Q54321", "abcd" ) );
        Assert.assertNull( ac );

        // two xrefs - P12345+Q54321 should be found
        final Protein p2Clone = cloner.clone(p2);
        ac = finder.findAc(p2Clone);
        Assert.assertNotNull( ac );
        Assert.assertEquals( p2.getAc(), ac );

        // two xrefs - P12345+Q01010 does not exist
        final Protein protNotExist = getMockBuilder().createProtein( "P12345", "guru" );
        protNotExist.addXref(getMockBuilder().createIdentityXrefUniprot(protNotExist, "Q01010"));
        Assert.assertNull( finder.findAc( protNotExist ) );

        // two xrefs - P12345+Q01010 does not exist but shortLabel does - should not find anything
        final Protein protNotExist2 = getMockBuilder().createProtein( "P12345", "foo" );
        protNotExist2.addXref(getMockBuilder().createIdentityXrefUniprot(protNotExist2, "Q01010"));
        Assert.assertNull( finder.findAc( protNotExist2 ) );
    }

    @Test
    public void findAcForInteractor_noUniprotUpdate() throws Exception {

        // p has one xref to uniprot
        final Protein p = getMockBuilder().createProtein( "P12345", "foo" );
        p.setSequence( "ABCDEF" );
        getPersisterHelper().save( p );

        Assert.assertEquals(1, getDaoFactory().getProteinDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getXrefDao(InteractorXref.class).countAll());

        // p2 has one identity xrefs to uniprot
        final Protein p2 = getMockBuilder().createProtein( "P12345", "foo" );
        CvTopic noUniprotUpdate = getMockBuilder().createCvObject( CvTopic.class, null, CvTopic.NON_UNIPROT );
        p2.addAnnotation( getMockBuilder().createAnnotation( "", noUniprotUpdate ) );
        
        Assert.assertNull( finder.findAc( p2 ) );
    }

    @Test
    public void findAcForInteractor_noUniprotUpdate2() throws Exception {
        CvTopic noUniprotUpdate = getMockBuilder().createCvObject( CvTopic.class, null, CvTopic.NON_UNIPROT );

        // p has one xref to uniprot
        final Protein p = getMockBuilder().createProtein( "P12345", "foo" );
        p.addAnnotation( getMockBuilder().createAnnotation( "", noUniprotUpdate ) );
        p.setSequence( "ABCDEF" );
        getPersisterHelper().save( p );

        Assert.assertEquals(1, getDaoFactory().getProteinDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getXrefDao(InteractorXref.class).countAll());

        // p2 has one identity xrefs to uniprot
        final Protein p2 = getMockBuilder().createProtein( "P12345", "foo" );
        p2.addAnnotation( getMockBuilder().createAnnotation( "", noUniprotUpdate ) );

        // check that it doesn't find the proteins if they have no-uniprot-update and different sequence
        Assert.assertNull( finder.findAc( p2 ) );

        // p2 has one identity xrefs to uniprot
        final Protein p3 = getMockBuilder().createProtein( "P12345", "foo" );
        p3.setSequence( "ABCDEF" );
        p3.addAnnotation( getMockBuilder().createAnnotation( "", noUniprotUpdate ) );

        // both have no-uniprot-update and the same sequence
        Assert.assertEquals( p.getAc(), finder.findAc( p3 ) );
    }

    @Test
    public void findAcForBioSource_byAc() {
        final BioSource bs = getMockBuilder().createBioSource( 9606, "human" );
        getPersisterHelper().save( bs );
        final String originalAc = bs.getAc();

        BioSource empty = getMockBuilder().createBioSource( 9606, "human" );
        empty.setAc( originalAc );
        String ac = finder.findAc( empty );
        Assert.assertNotNull( ac );
        Assert.assertEquals( bs.getAc(), ac );
    }

    @Test
    public void findAcForBioSource_only_taxid() {
        BioSource bs1 = getMockBuilder().createBioSource( 9606, "human" );
        getPersisterHelper().save( bs1 );
        String queryAc1 = bs1.getAc();

        String ac = finder.findAc( getMockBuilder().createBioSource( 9606, "human" ) );
        Assert.assertNotNull( ac );
        Assert.assertEquals( queryAc1, ac );

        Assert.assertNull( finder.findAc( getMockBuilder().createBioSource( 4932, "yeast" ) ) );
    }

    @Test
    public void findAcForBioSource_taxid_cellType_tissue() {
        CvTissue brain = getMockBuilder().createCvObject( CvTissue.class, "MI:xxxx", "brain" );

        CvCellType typeA = getMockBuilder().createCvObject( CvCellType.class, "MI:aaaa", "A" );

        BioSource bs2 = getMockBuilder().createBioSource( 9606, "human" );
        bs2.setCvCellType( typeA );
        bs2.setCvTissue( brain );
        getPersisterHelper().save( bs2 );
        String queryAc2 = bs2.getAc();

        brain = getMockBuilder().createCvObject( CvTissue.class, "MI:xxxx", "brain" );
        typeA = getMockBuilder().createCvObject( CvCellType.class, "MI:aaaa", "A" );

        final BioSource qeryBs1 = getMockBuilder().createBioSource( 9606, "human" );
        qeryBs1.setCvCellType( typeA );
        qeryBs1.setCvTissue( brain );
        String ac = finder.findAc( qeryBs1 );
        Assert.assertNotNull( ac );
        Assert.assertEquals( queryAc2, ac );
    }

    @Test
    public void findAcForBioSource_taxid_cellType() {
        CvCellType typeA = getMockBuilder().createCvObject( CvCellType.class, "MI:aaaa", "A" );

        BioSource bs3 = getMockBuilder().createBioSource( 9606, "human" );
        bs3.setCvCellType( typeA );
        getPersisterHelper().save( bs3 );
        String queryAc3 = bs3.getAc();

        typeA = getMockBuilder().createCvObject( CvCellType.class, "MI:aaaa", "A" );

        final BioSource qeryBs3 = getMockBuilder().createBioSource( 9606, "human" );
        qeryBs3.setCvCellType( typeA );
        String ac = finder.findAc( qeryBs3 );
        Assert.assertNotNull( ac );
        Assert.assertEquals( queryAc3, ac );

        CvCellType typeB = getMockBuilder().createCvObject( CvCellType.class, "MI:xxxx", "B" );
        final BioSource otherBs3 = getMockBuilder().createBioSource( 9606, "human" );
        Assert.assertNull( finder.findAc( otherBs3 ) );

        otherBs3.setCvCellType( typeB );
        Assert.assertNull( finder.findAc( otherBs3 ) );
    }

    @Test
    public void findAcForBioSource_taxid_tissue() {
        CvTissue brain = getMockBuilder().createCvObject( CvTissue.class, "MI:xxxx", "brain" );

        BioSource bs4 = getMockBuilder().createBioSource( 9606, "human" );
        bs4.setCvTissue( brain );
        getPersisterHelper().save( bs4 );
        String queryAc4 = bs4.getAc();

        brain = getMockBuilder().createCvObject( CvTissue.class, "MI:xxxx", "brain" );

        final BioSource qeryBs4 = getMockBuilder().createBioSource( 9606, "human" );
        qeryBs4.setCvTissue( brain );
        String ac = finder.findAc( qeryBs4 );
        Assert.assertNotNull( ac );
        Assert.assertEquals( queryAc4, ac );

        CvTissue liver = getMockBuilder().createCvObject( CvTissue.class, "MI:zzzz", "liver" );
        final BioSource otherBs4 = getMockBuilder().createBioSource( 9606, "human" );
        Assert.assertNull( finder.findAc( otherBs4 ) );
        
        otherBs4.setCvTissue( liver );
        Assert.assertNull( finder.findAc( otherBs4 ) );
    }

    @Test
    public void findAcForComponent_byAc() {
        final Protein p = getMockBuilder().createProtein( "P12345", "foo" );
        final Component component = getMockBuilder().createComponentBait( p );
        getPersisterHelper().save( component );
        final String originalAc = component.getAc();

        Component empty = getMockBuilder().createComponentBait( p );
        empty.setAc( originalAc );
        String ac = finder.findAc( empty );
        Assert.assertNotNull( ac );
        Assert.assertEquals( component.getAc(), ac );
    }

    @Test
    public void findAcForComponent() {
        final Interaction interaction = getMockBuilder().createDeterministicInteraction();
        Component component = interaction.getComponents().iterator().next();
        Assert.assertNull( finder.findAc( component ) );
    }

    @Test
    public void findAcForFeature_byAc() {
        CvFeatureType type = getMockBuilder().createCvObject( CvFeatureType.class, "MI:xxxx", "type" );
        final Feature feature = getMockBuilder().createFeature( "region", type );
        getPersisterHelper().save( feature );
        final String originalAc = feature.getAc();

        Feature empty = getMockBuilder().createFeature( "region", type );
        empty.setAc( originalAc );
        String ac = finder.findAc( empty );
        Assert.assertNotNull( ac );
        Assert.assertEquals( feature.getAc(), ac );
    }

    @Test
    public void findAcForFeature() {
        Feature feature = getMockBuilder().createFeatureRandom();
        Assert.assertNull( finder.findAc( feature ) );
    }

    @Test
    public void findAcForCvObject_byAc() {
        final Interaction i = getMockBuilder().createDeterministicInteraction();
        getPersisterHelper().save( i );
        final String originalAc = i.getAc();

        Interaction empty = getMockBuilder().createDeterministicInteraction();
        empty.setAc( originalAc );
        String ac = finder.findAc( empty );
        Assert.assertNotNull( ac );
        Assert.assertEquals( i.getAc(), ac );
    }

    @Test
    public void findAcForCvObject_same_MI_different_class() {
        CvTopic topic = getMockBuilder().createCvObject( CvTopic.class, "MI:xxxx", "topic" );
        getPersisterHelper().save( topic );

        CvDatabase database = getMockBuilder().createCvObject( CvDatabase.class, "MI:xxxx", "db" );
        getPersisterHelper().save( database );

        String ac = finder.findAc( getMockBuilder().createCvObject( CvTopic.class, "MI:xxxx", "topic" ) );
        Assert.assertNotNull( topic.getAc() );
        Assert.assertEquals( topic.getAc(), ac );

        ac = finder.findAc( getMockBuilder().createCvObject( CvDatabase.class, "MI:xxxx", "db" ) );
        Assert.assertNotNull( database.getAc() );
        Assert.assertEquals( database.getAc(), ac );
    }
}
