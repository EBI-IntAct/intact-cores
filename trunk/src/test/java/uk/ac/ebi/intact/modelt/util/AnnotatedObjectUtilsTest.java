package uk.ac.ebi.intact.modelt.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.AnnotatedObjectUtils;

import java.util.Collection;

/**
 * AnnotatedObjectUtils Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.5.0-rc1
 */
public class AnnotatedObjectUtilsTest extends TestCase {

    public AnnotatedObjectUtilsTest( String name ) {
        super( name );
    }

    Institution institution;

    public void setUp() throws Exception {
        super.setUp();
        institution = new Institution( "test" );
    }

    public void tearDown() throws Exception {
        super.tearDown();
        institution = null;
    }

    public static Test suite() {
        return new TestSuite( AnnotatedObjectUtilsTest.class );
    }

    ////////////////////
    // Tests


    private AnnotatedObject buildObject() {
        Institution i = new Institution( "test" );
        AnnotatedObject ao = new CvTopic( institution, "test-object" );

        ao.addXref( buildXref( "id1", "db1", "qu1" ) );
        ao.addXref( buildXref( "id2", "db1", "qu1" ) );
        ao.addXref( buildXref( "id3", "db2", "qu1" ) );
        ao.addXref( buildXref( "id4", "db3", "qu2" ) );

        return ao;
    }

    private Xref buildXref( String id, String db, String qualifier ) {
        return new CvObjectXref( institution,
                                 new CvDatabase( institution, db ),
                                 id,
                                 new CvXrefQualifier( institution, qualifier ) );
    }

    public void testSearchXref_database() {
        AnnotatedObject ao = buildObject();
        Collection<Xref> xrefs;

        xrefs = AnnotatedObjectUtils.searchXrefs( ao, new CvDatabase( institution, "db1" ) );
        assertEquals( 2, xrefs.size() );

        xrefs = AnnotatedObjectUtils.searchXrefs( ao, new CvDatabase( institution, "db2" ) );
        assertEquals( 1, xrefs.size() );

        xrefs = AnnotatedObjectUtils.searchXrefs( ao, new CvDatabase( institution, "XXX" ) );
        assertEquals( 0, xrefs.size() );

        try {
            xrefs = AnnotatedObjectUtils.searchXrefs( ao, (CvDatabase) null );
            fail( "null param not allowed." );
        } catch ( Exception e ) {
            // ok
        }
    }

    public void testSearchXref_databaseAndQualifier() {
        AnnotatedObject ao = buildObject();
        Collection<Xref> xrefs;

        xrefs = AnnotatedObjectUtils.searchXrefs( ao,
                                                  new CvDatabase( institution, "db1" ),
                                                  new CvXrefQualifier( institution, "qu1" ) );
        assertEquals( 2, xrefs.size() );

        xrefs = AnnotatedObjectUtils.searchXrefs( ao,
                                                  new CvDatabase( institution, "db2" ),
                                                  new CvXrefQualifier( institution, "qu1" ) );
        assertEquals( 1, xrefs.size() );

        xrefs = AnnotatedObjectUtils.searchXrefs( ao,
                                                  new CvDatabase( institution, "XXX" ),
                                                  new CvXrefQualifier( institution, "qu1" ) );
        assertEquals( 0, xrefs.size() );

        xrefs = AnnotatedObjectUtils.searchXrefs( ao,
                                                  new CvDatabase( institution, "db1" ),
                                                  new CvXrefQualifier( institution, "XXX" ) );
        assertEquals( 0, xrefs.size() );

        try {
            xrefs = AnnotatedObjectUtils.searchXrefs( ao, null, null );
            fail( "null param not allowed." );
        } catch ( Exception e ) {
            // ok
        }
    }

    public void testSearchXref_qualifier() {
        AnnotatedObject ao = buildObject();
        Collection<Xref> xrefs;

        xrefs = AnnotatedObjectUtils.searchXrefs( ao,
                                                  new CvXrefQualifier( institution, "qu1" ) );
        assertEquals( 3, xrefs.size() );

        xrefs = AnnotatedObjectUtils.searchXrefs( ao,
                                                  new CvXrefQualifier( institution, "qu2" ) );
        assertEquals( 1, xrefs.size() );

        xrefs = AnnotatedObjectUtils.searchXrefs( ao,
                                                  new CvXrefQualifier( institution, "XXX" ) );
        assertEquals( 0, xrefs.size() );

        try {
            xrefs = AnnotatedObjectUtils.searchXrefs( ao, (CvXrefQualifier) null );
            fail( "null param not allowed." );
        } catch ( Exception e ) {
            // ok
        }
    }

}
