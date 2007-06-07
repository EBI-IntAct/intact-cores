package uk.ac.ebi.intact.persistence.dao.impl;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.persistence.dao.InteractorDao;

import java.util.List;

/**
 * InteractorDaoImpl Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version 1.0
 * @since <pre>01/31/2007</pre>
 */
public class InteractorDaoImplTest extends TestCase {

    public InteractorDaoImplTest( String name ) {
        super( name );
    }

    private InteractorDao dao;

    public void setUp() throws Exception {
        super.setUp();
        dao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getInteractorDao();
    }

    public void tearDown() throws Exception {
        super.tearDown();
        IntactContext.getCurrentInstance().getDataContext().commitAllActiveTransactions();
        dao = null;
    }

    public static Test suite() {
        return new TestSuite( InteractorDaoImplTest.class );
    }

    ////////////////////
    // Tests

    public void testGetGeneNamesByInteractorAc() throws Exception {
//        List<String> geneNames = dao.getGeneNamesByInteractorAc( "TEST-5153" );
        Interactor protein = (Interactor) dao.getByShortLabel("cara_ecoli");
        List<String> geneNames = dao.getGeneNamesByInteractorAc( protein.getAc() );
        assertEquals( 3, geneNames.size() );
        assertTrue( geneNames.contains( "carA" ) );
        assertTrue( geneNames.contains( "b0032" ) );
        assertTrue( geneNames.contains( "JW0030" ) );
    }

    public void testGetInteractorInvolvedInInteraction() throws Exception {

//        InteractorDao dao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getInteractorDao();
        List<Interactor> interactors = dao.getInteractorInvolvedInInteraction( 0, 2 );

        for ( Interactor interactor : interactors ) {
            System.out.println( interactor.getAc() + " - " + interactor.getShortLabel() );
        }

        assertEquals( 2, interactors.size() );

        Interactor i1 = interactors.get( 0 );
        Interactor i2 = interactors.get( 1 );

        boolean try1 = "cara_ecoli".equals( i1.getShortLabel() ) && "carb_ecoli".equals( i2.getShortLabel() );
        boolean try2 = "carb_ecoli".equals( i2.getShortLabel() ) && "cara_ecoli".equals( i1.getShortLabel() );
        assertTrue( try1 || try2 );

//        IntactContext.getCurrentInstance().getDataContext().commitAllActiveTransactions();
//        dao = null;
    }

    public void testCountInteractorInvolvedInInteraction() throws Exception {

        // We do not have any interactor not interacting
//        InteractorDao dao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getInteractorDao();
        int count = dao.countInteractorInvolvedInInteraction();
        assertEquals( 4, count );
//        IntactContext.getCurrentInstance().getDataContext().commitAllActiveTransactions();
//        dao = null;
    }
}
