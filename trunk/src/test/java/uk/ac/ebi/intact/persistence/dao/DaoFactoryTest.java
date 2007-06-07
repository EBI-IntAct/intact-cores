package uk.ac.ebi.intact.persistence.dao;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.context.AutoBeginTransactionException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.CvXrefQualifier;

/**
 * DaoFactory Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version TODO artifact version
 * @since <pre>02/15/2007</pre>
 */
public class DaoFactoryTest extends TestCase {

    public DaoFactoryTest( String name ) {
        super( name );
    }


    DaoFactory daoFactory;

    public void setUp() throws Exception {
        super.setUp();
        daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
    }

    public void tearDown() throws Exception {
        super.tearDown();
        daoFactory = null;
    }

    public static Test suite() {
        return new TestSuite( DaoFactoryTest.class );
    }

    ////////////////////
    // Tests

//    public void testGetCurrentInstance() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetCurrentInstance1() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetCurrentInstance2() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetCurrentInstance3() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetAliasDao() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetAliasDao1() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetAnnotatedObjectDao() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetAnnotatedObjectDao1() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetAnnotationDao() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetBaseDao() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetBioSourceDao() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetComponentDao() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetCvObjectDao() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetCvObjectDao1() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetDbInfoDao() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetExperimentDao() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetFeatureDao() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetInstitutionDao() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetIntactObjectDao() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetIntactObjectDao1() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetInteractionDao() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetInteractorDao() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetInteractorDao1() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetMineInteractionDao() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetPolymerDao() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetPolymerDao1() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetProteinDao() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetPublicationDao() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetRangeDao() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetSearchableDao() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetSearchItemDao() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetXrefDao() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetXrefDao1() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetCurrentSession() throws Exception {
//        fail( "Not yet implemented." );
//    }
//
//    public void testGetCurrentTransaction() throws Exception {
//        fail( "Not yet implemented." );
//    }

    public void testIsTransactionActive_noRead() throws Exception {

        // set auto begin true
        IntactContext.getCurrentInstance().getConfig().setAutoBeginTransaction( true );

        assertFalse( daoFactory.isTransactionActive() );

        IntactTransaction transaction = daoFactory.beginTransaction();
        assertTrue( daoFactory.isTransactionActive() );

        transaction.commit();
        assertFalse( daoFactory.isTransactionActive() );
    }

    public void testIsTransactionActive_readCV() throws Exception {

        IntactContext.getCurrentInstance().getConfig().setAutoBeginTransaction( false );

        assertFalse( daoFactory.isTransactionActive() );

        IntactTransaction transaction = daoFactory.beginTransaction();
        assertTrue( daoFactory.isTransactionActive() );

        readData( daoFactory );

        transaction.commit();
        assertFalse( daoFactory.isTransactionActive() );
    }

    public void testIsTransactionActive_autoBeginFalse() throws Exception {

        try {
            // set auto begin false
            IntactContext.getCurrentInstance().getConfig().setAutoBeginTransaction( false );

            assertFalse( daoFactory.isTransactionActive() );

            readData( daoFactory );

            fail();
        } catch ( AutoBeginTransactionException e ) {
            // ok
        }
    }

    public void testIsTransactionActive_autoBeginTrue() throws Exception {

        // set auto begin true
        IntactContext.getCurrentInstance().getConfig().setAutoBeginTransaction( true );

        assertFalse( daoFactory.isTransactionActive() );

        readData( daoFactory );

        assertTrue( daoFactory.isTransactionActive() );

        // TODO Here it is possible to forget to close a transactopn :(
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();
        assertFalse( daoFactory.isTransactionActive() );
    }

    private void readData( DaoFactory daoFactory ) {
        CvObjectDao<CvObject> cvObjectDao = daoFactory.getCvObjectDao();
        cvObjectDao.getByPsiMiRef( CvXrefQualifier.GO_DEFINITION_REF_MI_REF );
    }
}
