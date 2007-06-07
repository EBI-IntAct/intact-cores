package uk.ac.ebi.intact.persistence.dao.impl;

import junit.framework.Test;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.DatabaseTestCase;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.InteractionDao;

import java.util.*;

/**
 * IntactObjectDaoImpl Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since TODO artifact version
 */
public class IntactObjectDaoImplTest extends DatabaseTestCase {
    private String interactionAc1 = null;
    private String interactionAc2 = null;
    private String shortLabel1 = null;
    private String shortLabel2 = null;

    public IntactObjectDaoImplTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite( IntactObjectDaoImplTest.class );
    }

    ////////////////////
    // Tests

//    public void testGetByAcLike() throws Exception {
//        fail( "Not yet implemented." );
//    }

//    public void testGetByAcLike1() throws Exception {
//        fail( "Not yet implemented." );
//    }

    public void testGetByAc_String() throws Exception {
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        InteractionDao idao = daoFactory.getInteractionDao();

        setInteractionAcsAndShortlabels(idao);

        InteractionImpl i = idao.getByAc( interactionAc1 );
        assertNotNull( i );
        assertEquals( shortLabel1, i.getShortLabel() );

        assertNull( idao.getByAc( "xxx" ) );
    }

    public void testGetByAc_array() throws Exception {
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        InteractionDao idao = daoFactory.getInteractionDao();
        setInteractionAcsAndShortlabels(idao);

       // retreive a single interaction
        List<InteractionImpl> is = idao.getByAc( new String[]{interactionAc1} );
//        List<InteractionImpl> is = idao.getByAc( new String[]{"TEST-5195"} );
        assertNotNull( is );
        assertEquals( 1, is.size() );
        InteractionImpl i = is.iterator().next();
        assertNotNull( i );
        assertEquals( shortLabel1, i.getShortLabel() );

        // retreive multiple interactions
        is = null;
        is = idao.getByAc( new String[]{interactionAc1, interactionAc2} );
//        is = idao.getByAc( new String[]{"TEST-5195", "TEST-5317"} );
        assertNotNull( is );
        assertEquals( 2, is.size() );
        for ( InteractionImpl ii : is ) {
            if ( ii.getAc().equals( interactionAc1 ) ) {
                assertEquals( shortLabel1, ii.getShortLabel() );
            } else if ( ii.getAc().equals( interactionAc2 ) ) {
                assertEquals(shortLabel2, ii.getShortLabel() );
            } else {
                fail( "Expected interaction to have wither AC " + interactionAc1 + " or "+ interactionAc2 + "." );
            }
        }

        // retreive multiple non existing interactions
        is = null;
        is = idao.getByAc( new String[]{"xxx", "yyy"} );
        assertNotNull( is );
        assertTrue( is.isEmpty() );

        // retreive a mixture of true and false potitive
        is = null;
        is = idao.getByAc( new String[]{interactionAc1, "yyy", interactionAc2, "zzz"} );
        assertNotNull( is );
        assertEquals( 2, is.size() );
        for ( InteractionImpl ii : is ) {
            if ( ii.getAc().equals( interactionAc1 ) ) {
                assertEquals( shortLabel1, ii.getShortLabel() );
            } else if ( ii.getAc().equals( interactionAc2 ) ) {
                assertEquals( shortLabel2, ii.getShortLabel() );
            } else {
                fail( "Expected interaction to have wither AC" + interactionAc1+ " or "+ interactionAc2 + "." );
            }
        }
    }

    public void testGetByAc_collection() throws Exception {
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        InteractionDao idao = daoFactory.getInteractionDao();
        setInteractionAcsAndShortlabels(idao);

        // retreive a single interaction
        List<InteractionImpl> is = idao.getByAc( Arrays.asList( new String[]{interactionAc1} ) );
        assertNotNull( is );
        assertEquals( 1, is.size() );
        InteractionImpl i = is.iterator().next();
        assertNotNull( i );
        assertEquals( shortLabel1, i.getShortLabel() );

        // retreive multiple interactions
        is = null;
        is = idao.getByAc( Arrays.asList( new String[]{interactionAc1, interactionAc2} ) );
        assertNotNull( is );
        assertEquals( 2, is.size() );
        for ( InteractionImpl ii : is ) {
            if ( ii.getAc().equals( interactionAc1) ) {
                assertEquals( shortLabel1, ii.getShortLabel() );
            } else if ( ii.getAc().equals(interactionAc2 ) ) {
                assertEquals( shortLabel2, ii.getShortLabel() );
            } else {
                fail( "Expected interaction to have wither AC " + interactionAc1 + " or " + interactionAc2+ "." );
            }
        }

        // retreive multiple non existing interactions
        is = null;
        is = idao.getByAc( Arrays.asList( new String[]{"xxx", "yyy"} ) );
        assertNotNull( is );
        assertTrue( is.isEmpty() );

        // retreive a mixture of true and false potitive
        is = null;
        is = idao.getByAc( Arrays.asList( new String[]{interactionAc1, "yyy",interactionAc2, "zzz"} ) );
        assertNotNull( is );
        assertEquals( 2, is.size() );
        for ( InteractionImpl ii : is ) {
            if ( ii.getAc().equals( interactionAc1 ) ) {
                assertEquals( shortLabel1, ii.getShortLabel() );
            } else if ( ii.getAc().equals( interactionAc2 ) ) {
                assertEquals( shortLabel2, ii.getShortLabel() );
            } else {
                fail( "Expected interaction to have wither AC " + interactionAc1 + " or " + interactionAc2 + "." );
            }
        }
    }

    public void testGetAll() throws Exception {
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        InteractionDao idao = daoFactory.getInteractionDao();
        List<InteractionImpl> list = idao.getAll();
        assertEquals( 8, list.size() );
    }

    public void testGetAllIterator() throws Exception {
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        InteractionDao idao = daoFactory.getInteractionDao();
        Iterator<InteractionImpl> ii = idao.getAllIterator();
        int count = 0;
        while( ii.hasNext() ) {
            count++;
            assertNotNull( ii.next() );
        }
        assertEquals( 8, count );
    }

    public void testGetAll_int_int() throws Exception {
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        InteractionDao idao = daoFactory.getInteractionDao();
        List<InteractionImpl> list = idao.getAll( 0, 3 );
        assertNotNull( list );
        assertEquals( 3, list.size() );
    }

    public void testExists() {
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        InteractionDao idao = daoFactory.getInteractionDao();
        List<InteractionImpl> list = idao.getAll();
        assertNotNull( list );
        assertEquals( 8, list.size() );

        InteractionImpl interaction = list.get( 3 );
        assertTrue( idao.exists( interaction ) );

        // build mock and check that it doesn't exist in the database
        Institution owner = new Institution( "test" );
        CvInteractionType type = new CvInteractionType( owner, "type" );
        CvInteractorType intType = new CvInteractorType( owner, "interaction" );
        Collection<Experiment> exps = new ArrayList<Experiment>();
        InteractionImpl i = new InteractionImpl( exps, type, intType, "sl", owner );
        i.setAc("test-ac");
        assertFalse( idao.exists( i ) );

        i.setAc( "lala" );
        assertFalse( idao.exists( i ) );
    }

    public void setInteractionAcsAndShortlabels(InteractionDao idao) {
        Collection<InteractionImpl> interactions = idao.getAll();
        System.out.println("interactions.size() = " + interactions.size());

        int count = 0;
        for(InteractionImpl interaction : interactions){
            System.out.println("count = " + count);

            System.out.println("for interaction.getAc() = " + interaction.getAc());
            System.out.println("for interaction.getShortLabel() = " + interaction.getShortLabel());

            if(count==0){
                System.out.println("if count == 0");
                System.out.println("interaction.getAc() = " + interaction.getAc());
                System.out.println("interaction.getShortLabel() = " + interaction.getShortLabel());
                interactionAc1 = interaction.getAc();
                shortLabel1 = interaction.getShortLabel();
            }
            if(count==1){
                System.out.println("if count == 1");
                System.out.println("interaction.getAc() = " + interaction.getAc());
                System.out.println("interaction.getShortLabel() = " + interaction.getShortLabel());
                interactionAc2=interaction.getAc();
                shortLabel2= interaction.getShortLabel();
                break;
            }
            count++;
        }
        System.out.println("interactionAc1 = " + interactionAc1);
        System.out.println("shortLabel1 = " + shortLabel1);
        System.out.println("interactionAc2 = " + interactionAc2);
        System.out.println("shortLabel2 = " + shortLabel2);
    }
}