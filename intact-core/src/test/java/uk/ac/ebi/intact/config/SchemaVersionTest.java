/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */

package uk.ac.ebi.intact.config;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test for <code>SchemaVersionTest</code>
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 09/04/2006
 */
public class SchemaVersionTest extends TestCase {

    public SchemaVersionTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testParse() throws Exception {
        String version = "1.2.3";
        SchemaVersion sv = SchemaVersion.parse( version );

        assertEquals( Integer.valueOf( 1 ), sv.getMajor() );
        assertEquals( Integer.valueOf( 2 ), sv.getMinor() );
        assertEquals( Integer.valueOf( 3 ), sv.getBuild() );

        SchemaVersion sv2 = new SchemaVersion( 1, 2, 3 );
        assertEquals( sv2, sv );
    }

    public void testIsCompatible_True() throws Exception {
        SchemaVersion minVersion = new SchemaVersion( 1, 2, 4 );
        SchemaVersion compVersion = new SchemaVersion( 1, 2, 5 );

        assertTrue( compVersion + " should be compatible with " + minVersion, compVersion.isCompatibleWith( minVersion ) );
    }

    public void testIsCompatible_False() throws Exception {
        SchemaVersion minVersion = new SchemaVersion( 2, 0, 5 );
        SchemaVersion compVersion = new SchemaVersion( 1, 2, 4 );

        assertFalse( compVersion.isCompatibleWith( minVersion ) );
    }

    public static Test suite() {
        return new TestSuite( SchemaVersionTest.class );
    }
}
