/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */

package uk.ac.ebi.intact.context;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test for <code>IntactContextTest</code>
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 09/04/2006
 */
public class IntactContextTest extends TestCase {

    public IntactContextTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testInitContext_Default() throws Exception {
        IntactContext ctx = IntactContext.getCurrentInstance();

        assertNotNull( ctx.getInstitution() );
        assertEquals( "TEST", ctx.getConfig().getAcPrefix() );

        assertNotNull( ctx.getDataContext() );

        assertFalse( "App not read-only", ctx.getDataContext().isReadOnly() );
    }

    public static Test suite() {
        return new TestSuite( IntactContextTest.class );
    }
}
