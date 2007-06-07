package uk.ac.ebi.intact.context;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

/**
 * RuntimeConfig Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @since 1.5
 * @version $Id$
 */
public class RuntimeConfigTest extends TestCase {
    
    public RuntimeConfigTest(String name) {
        super(name);
    }

    private RuntimeConfig config;

    public void setUp() throws Exception {
        super.setUp();
        config = IntactContext.getCurrentInstance().getConfig();
    }

    public void tearDown() throws Exception {
        super.tearDown();
        config = null;
    }

    public static Test suite() {
        return new TestSuite(RuntimeConfigTest.class);
    }

    ////////////////////
    // Tests
    
//    public void testGetCurrentInstance() throws Exception {
//        fail( "Not yet implemented." );
//    }
    
//    public void testSetGetInstitution() throws Exception {
//        fail( "Not yet implemented." );
//    }
    
//    public void testSetGetAcPrefix() throws Exception {
//        fail( "Not yet implemented." );
//    }
    
//    public void testGetDataConfigs() throws Exception {
//        fail( "Not yet implemented." );
//    }
    
//    public void testGetDataConfig() throws Exception {
//        fail( "Not yet implemented." );
//    }
    
//    public void testSetGetDefaultDataConfig() throws Exception {
//        fail( "Not yet implemented." );
//    }
    
    public void testSetAutoBeginTransaction() throws Exception {
        boolean b = config.isAutoBeginTransaction();

        config.setAutoBeginTransaction( false );
        assertFalse( config.isAutoBeginTransaction() );

        config.setAutoBeginTransaction( b );
    }
    
//    public void testSetReadOnlyApp() throws Exception {
//        fail( "Not yet implemented." );
//    }
    
//    public void testSetSynchronizedSearchItems() throws Exception {
//        fail( "Not yet implemented." );
//    }

    public void testIsAutoBeginTransaction() throws Exception {
        assertTrue( config.isAutoBeginTransaction() );
    }

    public void testIsDebugMode() {
        boolean b = config.isDebugMode();

        config.setDebugMode( false );
        assertFalse( config.isDebugMode() );

        config.setDebugMode( b );
    }

    public void testSetDebugMode() {
        assertTrue( config.isDebugMode() );
    }


}
