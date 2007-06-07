package uk.ac.ebi.intact.context;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.business.IntactTransactionException;

/**
 * DataContext Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version 1.0
 * @since <pre>02/15/2007</pre>
 */
public class DataContextTest extends TestCase {

    public DataContextTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite( DataContextTest.class );
    }

    ////////////////////
    // Tests

    public void testIsTransactionActive() {

        DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();
        assertNotNull( dataContext );

        assertFalse( dataContext.isTransactionActive() );

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        assertTrue( dataContext.isTransactionActive() );

        try {
            IntactContext.getCurrentInstance().getDataContext().commitTransaction();
        } catch ( IntactTransactionException e ) {
            fail( "Transaction should have been commited succesfully" );
        }

        assertFalse( dataContext.isTransactionActive() );
    }
}
