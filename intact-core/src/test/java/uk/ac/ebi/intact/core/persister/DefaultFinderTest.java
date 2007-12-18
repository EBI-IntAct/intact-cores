package uk.ac.ebi.intact.core.persister;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.finder.DefaultFinder;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.clone.IntactCloner;

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
    public void findAcForExperiment() throws Exception {
        final Experiment e = getMockBuilder().createExperimentEmpty( "bruno-2007-1", "123456789" );
        PersisterHelper.saveOrUpdate( e );

        IntactCloner cloner = new IntactCloner();
        cloner.setExcludeACs(true);

        String ac = finder.findAc( cloner.clone(e) );
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
        PersisterHelper.saveOrUpdate( p, p2 );

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
    public void findAcForBioSource_only_taxid() {
        BioSource bs1 = getMockBuilder().createBioSource( 9606, "human" );
        PersisterHelper.saveOrUpdate( bs1 );
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
        PersisterHelper.saveOrUpdate( bs2 );
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
        PersisterHelper.saveOrUpdate( bs3 );
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
        PersisterHelper.saveOrUpdate( bs4 );
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
        final Interaction interaction = getMockBuilder().createDeterministicInteraction();
        Component component = interaction.getComponents().iterator().next();
        Assert.assertNull( finder.findAc( component ) );
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
        Feature feature = getMockBuilder().createFeatureRandom();
        Assert.assertNull( finder.findAc( feature ) );
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
    public void findAcForCvObject_same_MI_different_class() {
        CvTopic topic = getMockBuilder().createCvObject( CvTopic.class, "MI:xxxx", "topic" );
        PersisterHelper.saveOrUpdate( topic );

        CvDatabase database = getMockBuilder().createCvObject( CvDatabase.class, "MI:xxxx", "db" );
        PersisterHelper.saveOrUpdate( database );

        String ac = finder.findAc( getMockBuilder().createCvObject( CvTopic.class, "MI:xxxx", "topic" ) );
        Assert.assertNotNull( topic.getAc() );
        Assert.assertEquals( topic.getAc(), ac );

        ac = finder.findAc( getMockBuilder().createCvObject( CvDatabase.class, "MI:xxxx", "db" ) );
        Assert.assertNotNull( database.getAc() );
        Assert.assertEquals( database.getAc(), ac );
    }
}
