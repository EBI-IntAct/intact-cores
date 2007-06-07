/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */

package uk.ac.ebi.intact.context.impl;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.context.IntactEnvironment;
import uk.ac.ebi.intact.context.IntactSession;

/**
 * Test for <code>StandaloneSessionTest</code>
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since 09/04/2006
 */
public class StandaloneSessionTest extends TestCase {

    public StandaloneSessionTest( String name ) {
        super( name );
    }

    private static final String APP_ATT_NAME = "app_att_name";
    private static final String APP_ATT_VALUE = "app_att_value";
    private static final String ATT_NAME = "att_name";
    private static final String ATT_VALUE = "att_name";
    private static final String REQ_ATT_NAME = "req_att_name";
    private static final String REQ_ATT_VALUE = "req_att_name";

    IntactSession session;

    public void setUp() throws Exception {
        super.setUp();

        session = new StandaloneSession();
    }

    public void tearDown() throws Exception {
        super.tearDown();

        session = null;
    }

    public void testSetGetApplicationAttribute() throws Exception {
        session.setApplicationAttribute( APP_ATT_NAME, APP_ATT_VALUE );
        assertEquals( APP_ATT_VALUE, session.getApplicationAttribute( APP_ATT_NAME ) );
    }

    public void testSetGetAttribute() throws Exception {
        session.setAttribute( ATT_NAME, ATT_VALUE );
        assertEquals( ATT_VALUE, session.getAttribute( ATT_NAME ) );
    }

    public void testSetGetRequestAttribute() throws Exception {
        session.setRequestAttribute( REQ_ATT_NAME, REQ_ATT_VALUE );
        assertEquals( REQ_ATT_VALUE, session.getRequestAttribute( REQ_ATT_NAME ) );
    }

    public void testSetGetInitParam() throws Exception {
        assertEquals( "TEST", session.getInitParam( IntactEnvironment.AC_PREFIX_PARAM_NAME.getFqn() ) );
        assertEquals( "ebi", session.getInitParam( IntactEnvironment.INSTITUTION_LABEL.getFqn() ) );
    }

    public static Test suite() {
        return new TestSuite( StandaloneSessionTest.class );
    }
}
