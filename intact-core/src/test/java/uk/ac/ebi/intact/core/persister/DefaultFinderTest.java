package uk.ac.ebi.intact.core.persister;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.*;

/**
 * DefaultFinder Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.8.0
 */
public class DefaultFinderTest extends IntactBasicTestCase {

    private Finder finder;

    @Before
    public void initFinder() {
        finder = new DefaultFinder();
    }

    @After
    public void cleanUp() {
        finder = null;
    }

    @Test
    public void findAcForInstitution_byAc() throws Exception {
        final Institution i = getMockBuilder().createInstitution( "MI:xxxx", "ebi" );
        PersisterHelper.saveOrUpdate( i );
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
        PersisterHelper.saveOrUpdate( i );

        String ac = finder.findAc( getMockBuilder().createInstitution( "MI:xxxx", "ebi" ) );
        Assert.assertNotNull( ac );
        Assert.assertEquals( i.getAc(), ac );

        // cannot be found
        Assert.assertNull( finder.findAc( getMockBuilder().createInstitution( "MI:zzzz", "mint" ) ) );
    }

    @Test
    public void findAcForPublication_byAc() {
        final Publication p = getMockBuilder().createPublication( "123456789" );
        PersisterHelper.saveOrUpdate( p );
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
        PersisterHelper.saveOrUpdate( p );

        final String ac = finder.findAc( getMockBuilder().createPublication( "123456789" ) );
        Assert.assertNotNull( ac );
        Assert.assertEquals( p.getAc(), ac );

        Assert.assertNull( finder.findAc( getMockBuilder().createPublication( "987654321" ) ) );
    }

    @Test
    public void findAcForExperiment_byAc() {
        final Experiment i = getMockBuilder().createDeterministicExperiment();
        PersisterHelper.saveOrUpdate( i );
        final String originalAc = i.getAc();

        Institution empty = new Institution( "bla" );
        empty.setAc( originalAc );
        String ac = finder.findAc( empty );
        Assert.assertNotNull( ac );
        Assert.assertEquals( i.getAc(), ac );
    }

    @Test
    public void findAcForExperiment() {
        final Experiment e = getMockBuilder().createExperimentEmpty( "bruno-2007-1", "123456789" );
        PersisterHelper.saveOrUpdate( e );

        String ac = finder.findAc( getMockBuilder().createExperimentEmpty( "bruno-2007-1", "123456789" ) );
        Assert.assertNotNull( ac );
        Assert.assertEquals( e.getAc(), ac );

        // TODO don't we want to search by primary-reference ????
        ac = finder.findAc( getMockBuilder().createExperimentEmpty( "bruno-2007-1", "123" ) );
        Assert.assertNotNull( ac );
        Assert.assertEquals( e.getAc(), ac );

        Assert.assertNull( finder.findAc( getMockBuilder().createExperimentEmpty( "samuel-2007-1", "123" ) ) );
    }

    @Test
    public void findAcForInteraction_byAc() throws Exception {
        final Interaction i = getMockBuilder().createDeterministicInteraction();
        PersisterHelper.saveOrUpdate( i );
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
        PersisterHelper.saveOrUpdate( i );

        final String ac = finder.findAc( getMockBuilder().createDeterministicInteraction() );
        Assert.assertNotNull( ac );
        Assert.assertEquals( i.getAc(), ac );

        Assert.assertNull( finder.findAc( getMockBuilder().createInteraction( "P12345", "Q98765", "P78634" ) ) );
    }

    @Test
    public void findAcForInteractor_byAc() {
        final Protein p = getMockBuilder().createProtein( "P12345", "foo" );
        PersisterHelper.saveOrUpdate( p );
        final String originalAc = p.getAc();

        Protein empty = getMockBuilder().createProtein( "P12345", "foo" );
        empty.setAc( originalAc );
        String ac = finder.findAc( empty );
        Assert.assertNotNull( ac );
        Assert.assertEquals( p.getAc(), ac );
    }

    @Test
    public void findAcForInteractor_uniprot_identity() {
        final Protein p = getMockBuilder().createProtein( "P12345", "foo" );
        PersisterHelper.saveOrUpdate( p );

        // same xref, different shorltabel -> should work
        String ac = finder.findAc( getMockBuilder().createProtein( "P12345", "abcd" ) );
        Assert.assertNotNull( ac );
        Assert.assertEquals( p.getAc(), ac );

        // different xref, same label -> should work
        ac = finder.findAc( getMockBuilder().createProtein( "Q98765", "foo" ) );
        Assert.assertNotNull( ac );
        Assert.assertEquals( p.getAc(), ac );

        // different uniprot id and shortlabel
        Assert.assertNull( finder.findAc( getMockBuilder().createProtein( "Q98765", "bar" ) ) );

        // same xrefs but different type, should not work
        final SmallMolecule sm = getMockBuilder().createSmallMoleculeRandom();
        sm.getXrefs().clear();
        for ( InteractorXref xref : p.getXrefs() ) {
            sm.addXref( xref );
        }
        Assert.assertNull( finder.findAc( sm ) );
    }

    @Test
    public void findAcForInteractor_other_identity() {
        // small molecule doesn't not have a uniprot identity, we then fall back onto other identity (minus intact, dip, dip)
        final SmallMolecule sm = getMockBuilder().createSmallMolecule( "CHEBI:0001", "nice molecule" );
        PersisterHelper.saveOrUpdate( sm );

        // same xref, different shorltabel -> should work
        String ac = finder.findAc( getMockBuilder().createSmallMolecule( "CHEBI:0001", "nice molecule" ) );
        Assert.assertNotNull( ac );
        Assert.assertEquals( sm.getAc(), ac );

        // different xref, same shorltabel -> should work
        ac = finder.findAc( getMockBuilder().createSmallMolecule( "CHEBI:9999", "nice molecule" ) );
        Assert.assertNotNull( ac );
        Assert.assertEquals( sm.getAc(), ac );

        // different xref, different shorltabel -> should NOT work
        Assert.assertNull( finder.findAc( getMockBuilder().createSmallMolecule( "CHEBI:555555", " another nice molecule" ) ) );
    }

    @Test
    public void findAcForInteractor_shortlabel() {
        final Protein p = getMockBuilder().createProtein( "P12345", "foo" );
        PersisterHelper.saveOrUpdate( p );

        final String ac = finder.findAc( getMockBuilder().createProtein( "Q99999", "foo" ) );
        Assert.assertNotNull( ac );
        Assert.assertEquals( p.getAc(), ac );

        Assert.assertNull( finder.findAc( getMockBuilder().createProtein( "Q98765", "bla" ) ) );
    }

    @Test
    public void findAcForBioSource_byAc() {
        final BioSource bs = getMockBuilder().createBioSource( 9606, "human" );
        PersisterHelper.saveOrUpdate( bs );
        final String originalAc = bs.getAc();

        BioSource empty = getMockBuilder().createBioSource( 9606, "human" );
        empty.setAc( originalAc );
        String ac = finder.findAc( empty );
        Assert.assertNotNull( ac );
        Assert.assertEquals( bs.getAc(), ac );
    }

    @Test
    public void test4bruno() throws Exception {

        BioSource bs1 = getMockBuilder().createBioSource( 9606, "human" );
        PersisterHelper.saveOrUpdate( bs1 );
        String queryAc1 = bs1.getAc();

        BioSource bs2 = getMockBuilder().createBioSource( 9606, "human" );
        PersisterHelper.saveOrUpdate( bs2 );
        String queryAc2 = bs2.getAc();

    }

    @Test
    public void findAcForBioSource() {
        // TODO split this test !!
        CvTissue brain = getMockBuilder().createCvObject( CvTissue.class, "MI:xxxx", "brain" );
        CvTissue liver = getMockBuilder().createCvObject( CvTissue.class, "MI:yyyy", "liver" );

        CvCellType typeA = getMockBuilder().createCvObject( CvCellType.class, "MI:aaaa", "A" );
        CvCellType typeB = getMockBuilder().createCvObject( CvCellType.class, "MI:bbbb", "B" );

        BioSource bs1 = getMockBuilder().createBioSource( 9606, "human" );
        PersisterHelper.saveOrUpdate( bs1 );
        String queryAc1 = bs1.getAc();

        BioSource bs2 = getMockBuilder().createBioSource( 9606, "human" );
        bs2.setCvCellType( typeA );
        bs2.setCvTissue( brain );
        PersisterHelper.saveOrUpdate( bs2 );
        String queryAc2 = bs2.getAc();

        BioSource bs3 = getMockBuilder().createBioSource( 9606, "human" );
        bs3.setCvCellType( typeA );
        PersisterHelper.saveOrUpdate( bs3 );
        String queryAc3 = bs3.getAc();

        BioSource bs4 = getMockBuilder().createBioSource( 9606, "human" );
        bs4.setCvTissue( brain );
        PersisterHelper.saveOrUpdate( bs4 );
        String queryAc4 = bs4.getAc();

        BioSource bs5 = getMockBuilder().createBioSource( 9606, "human" );
        bs5.setCvTissue( liver );
        bs5.setCvCellType( typeB );
        PersisterHelper.saveOrUpdate( bs5 );
        String queryAc5 = bs5.getAc();

        BioSource bs6 = getMockBuilder().createBioSource( 9606, "human" );
        bs6.setCvTissue( liver );
        PersisterHelper.saveOrUpdate( bs6 );

        BioSource bs7 = getMockBuilder().createBioSource( 4932, "yeast" );
        PersisterHelper.saveOrUpdate( bs7 );

        // Now search for data

        brain = getMockBuilder().createCvObject( CvTissue.class, "MI:xxxx", "brain" );
        liver = getMockBuilder().createCvObject( CvTissue.class, "MI:yyyy", "liver" );

        typeA = getMockBuilder().createCvObject( CvCellType.class, "MI:aaaa", "A" );
        typeB = getMockBuilder().createCvObject( CvCellType.class, "MI:bbbb", "B" );

        // plain taxid
        String ac = finder.findAc( getMockBuilder().createBioSource( 9606, "human" ) );
        Assert.assertNotNull( ac );
        Assert.assertEquals( queryAc1, ac );

        final BioSource qeryBs1 = getMockBuilder().createBioSource( 9606, "human" );
        qeryBs1.setCvCellType( typeA );
        qeryBs1.setCvTissue( brain );
        ac = finder.findAc( qeryBs1 );
        Assert.assertNotNull( ac );
        Assert.assertEquals( queryAc2, ac );

        final BioSource qeryBs2 = getMockBuilder().createBioSource( 9606, "human" );
        qeryBs2.setCvCellType( typeA );
        qeryBs2.setCvTissue( brain );
        ac = finder.findAc( qeryBs2 );
        Assert.assertNotNull( ac );
        Assert.assertEquals( queryAc2, ac );

        final BioSource qeryBs3 = getMockBuilder().createBioSource( 9606, "human" );
        qeryBs3.setCvCellType( typeA );
        ac = finder.findAc( qeryBs3 );
        Assert.assertNotNull( ac );
        Assert.assertEquals( queryAc3, ac );

        final BioSource qeryBs4 = getMockBuilder().createBioSource( 9606, "human" );
        qeryBs4.setCvTissue( brain );
        ac = finder.findAc( qeryBs4 );
        Assert.assertNotNull( ac );
        Assert.assertEquals( queryAc4, ac );

        final BioSource qeryBs5 = getMockBuilder().createBioSource( 9606, "human" );
        qeryBs5.setCvCellType( typeB );
        qeryBs5.setCvTissue( liver );
        ac = finder.findAc( qeryBs5 );
        Assert.assertNotNull( ac );
        Assert.assertEquals( queryAc5, ac );
    }

    @Test
    public void findAcForComponent_byAc() {
        final Protein p = getMockBuilder().createProtein( "P12345", "foo" );
        final Component component = getMockBuilder().createComponentBait( p );
        PersisterHelper.saveOrUpdate( component );
        final String originalAc = component.getAc();

        Component empty = getMockBuilder().createComponentBait( p );
        empty.setAc( originalAc );
        String ac = finder.findAc( empty );
        Assert.assertNotNull( ac );
        Assert.assertEquals( component.getAc(), ac );
    }

    @Test
    public void findAcForComponent() {
    }

    @Test
    public void findAcForFeature_byAc() {
        CvFeatureType type = getMockBuilder().createCvObject( CvFeatureType.class, "MI:xxxx", "type" );
        final Feature feature = getMockBuilder().createFeature( "region", type );
        PersisterHelper.saveOrUpdate( feature );
        final String originalAc = feature.getAc();

        Feature empty = getMockBuilder().createFeature( "region", type );
        empty.setAc( originalAc );
        String ac = finder.findAc( empty );
        Assert.assertNotNull( ac );
        Assert.assertEquals( feature.getAc(), ac );
    }

    @Test
    public void findAcForFeature() {


    }

    @Test
    public void findAcForCvObject_byAc() {
        final Interaction i = getMockBuilder().createDeterministicInteraction();
        PersisterHelper.saveOrUpdate( i );
        final String originalAc = i.getAc();

        Interaction empty = getMockBuilder().createDeterministicInteraction();
        empty.setAc( originalAc );
        String ac = finder.findAc( empty );
        Assert.assertNotNull( ac );
        Assert.assertEquals( i.getAc(), ac );
    }

    @Test
    public void findAcForCvObject() {
    }

    @Test
    public void findAcForCvObject_same_MI_different_class() {
    }
}
