package uk.ac.ebi.intact.persistence.dao.impl;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.persistence.dao.BioSourceDao;

import java.util.Collection;

/**
 * BioSourceDaoImpl Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version 1.0
 * @since <pre>02/14/2007</pre>
 */
public class BioSourceDaoImplTest extends TestCase {

    public BioSourceDaoImplTest( String name ) {
        super( name );
    }

    private BioSourceDao dao;

    public void setUp() throws Exception {
        super.setUp();
        dao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getBioSourceDao();
    }

    public void tearDown() throws Exception {
        super.tearDown();
        IntactContext.getCurrentInstance().getDataContext().commitAllActiveTransactions();
        dao = null;
    }

    public static Test suite() {
        return new TestSuite( BioSourceDaoImplTest.class );
    }

    ////////////////////
    // Tests

    public void testGetByTaxonIdUnique() throws Exception {
        try {
            dao.getByTaxonIdUnique( null );
            fail( "null param should not be allowed." );
        } catch ( Exception e ) {
            // ok
        }
    }

    public void testGetByTaxonId() throws Exception {
        try {
            Collection<BioSource> id = dao.getByTaxonId( null );
            fail( "null param should not be allowed." );
        } catch ( Exception e ) {
            // ok
        }

    }
}
