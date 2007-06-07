package uk.ac.ebi.intact.persistence.dao.impl;

import junit.framework.Test;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.DatabaseTestCase;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.AnnotatedObjectUtils;
import uk.ac.ebi.intact.persistence.dao.ProteinDao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ProteinDaoImpl Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.5.0-rc2
 */
public class ProteinDaoImplTest extends DatabaseTestCase {

    public ProteinDaoImplTest( String name ) {
        super( name );
    }

    private ProteinDao proteinDao;

    public void setUp() throws Exception {
        super.setUp();
        proteinDao = getDaoFactory().getProteinDao();
    }

    public void tearDown() throws Exception {
        super.tearDown();
        proteinDao = null;
    }

    public static Test suite() {
        return new TestSuite( ProteinDaoImplTest.class );
    }

    ////////////////////
    // Tests

//    public void testGetIdentityXrefByProteinAc() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetUniprotAcByProteinAc() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetUniprotUrlTemplateByProteinAc() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetPartnersCountingInteractionsByProteinAc() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetUniprotProteins() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetUniprotProteinsInvolvedInInteractions() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetByUniprotId() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetPartnersWithInteractionAcsByProteinAc() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetPartnersUniprotIdsByProteinAc() throws Exception {
//        fail( "Not yet implemented." );
//    }

    /////////////////////////
    // Splice variant tests

    public void testGetSpliceVariants_null() throws Exception {
        try {
            proteinDao.getSpliceVariants( null );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }

    public void testGetSpliceVariants_noAc() throws Exception {
        Institution institution = new Institution( "test" );
        BioSource bs = new BioSource( institution, "human", "9606" );
        CvInteractorType type = new CvInteractorType( institution, "protein" );
        Protein p = new ProteinImpl( institution, bs, "test", type );

        List<ProteinImpl> spliceVariants = proteinDao.getSpliceVariants( p );

        assertNotNull( spliceVariants );
        assertTrue( spliceVariants.isEmpty() );
    }

    public void testGetSpliceVariants_noFound() throws Exception {
        List<ProteinImpl> proteins = proteinDao.getUniprotProteins( 0, 10 );
        assertNotNull( proteins );
        assertFalse( proteins.isEmpty() );
        Protein p = proteins.get( 0 );
        List<ProteinImpl> spliceVariants = proteinDao.getSpliceVariants( p );
        assertTrue( spliceVariants.isEmpty() );
    }

    public void testGetSpliceVariants_found() throws Exception {

        List<ProteinImpl> proteins = proteinDao.getByUniprotId( "P83949" );
        assertNotNull( proteins );
        assertEquals( 1, proteins.size() );

        Protein protein = proteins.iterator().next();
        List<ProteinImpl> variants = proteinDao.getSpliceVariants( protein );
        assertNotNull( variants );
        assertEquals( 6, variants.size() );

        assertContainsProteinWithIdentity( variants, "P83949-1" );
        assertContainsProteinWithIdentity( variants, "P83949-2" );
        assertContainsProteinWithIdentity( variants, "P83949-3" );
        assertContainsProteinWithIdentity( variants, "P83949-4" );
        assertContainsProteinWithIdentity( variants, "P83949-5" );
        assertContainsProteinWithIdentity( variants, "P83949-6" );
    }

    private void assertContainsProteinWithIdentity( List<ProteinImpl> variants, String id ) {

        if ( variants == null ) {
            throw new NullPointerException( "variants must not be null." );
        }
        if ( variants.isEmpty() ) {
            throw new NullPointerException( "variants must not be empty." );
        }
        if ( id == null ) {
            throw new NullPointerException( "id must not be null." );
        }

        CvDatabase uniprotkb = getDaoFactory().getCvObjectDao( CvDatabase.class ).getByPsiMiRef( CvDatabase.UNIPROT_MI_REF );
        assertNotNull( uniprotkb );

        CvXrefQualifier identity = getDaoFactory().getCvObjectDao( CvXrefQualifier.class ).getByPsiMiRef( CvXrefQualifier.IDENTITY_MI_REF );
        assertNotNull( identity );

        for ( ProteinImpl variant : variants ) {
            Collection<Xref> xrefs = AnnotatedObjectUtils.searchXrefs( variant, uniprotkb, identity );
            if ( !xrefs.isEmpty() ) {
                return;
            }
        }

        // none of the protein in the collection matched the id, fail !
        fail( "Could not find a Splice Variant with Uniprot AC: " + id );
    }

    //////////////////////////
    // Splice variant master

    public void testGetSpliceVariantMasterProtein_error() {
        try {
            proteinDao.getSpliceVariantMasterProtein( null );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }

    public void testGetSpliceVariantMasterProtein() {

        List<ProteinImpl> proteins = proteinDao.getByUniprotId( "P83949-1" );
        assertNotNull( proteins );
        assertEquals( 1, proteins.size() );

        Protein protein = proteins.iterator().next();
        ProteinImpl master = proteinDao.getSpliceVariantMasterProtein( protein );
        assertNotNull( master );

        proteins = new ArrayList<ProteinImpl>( 1 );
        proteins.add( master );
        assertContainsProteinWithIdentity( proteins, "P83949" );
    }

    public void testGetSpliceVariantMasterProtein_givenSomethingElseThanSpliceVariant() {

        List<ProteinImpl> proteins = proteinDao.getByUniprotId( "P83949" );
        assertNotNull( proteins );
        assertEquals( 1, proteins.size() );

        Protein protein = proteins.iterator().next();
        ProteinImpl master = proteinDao.getSpliceVariantMasterProtein( protein );
        assertNull( master );
    }
}