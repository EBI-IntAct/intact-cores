package uk.ac.ebi.intact.persistence.dao.impl;

import junit.framework.JUnit4TestAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.persistence.dao.PublicationDao;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.CvObjectDao;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.business.IntactTransactionException;
import static org.junit.Assert.*;

/**
 * PublicationDaoImpl Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @since TODO artifact version
 * @version $Id$
 */
public class PublicationDaoImplTest {
    
    ////////////////////////////////
    // Compatibility with JUnit 3

    public static junit.framework.Test suite() {
         return new JUnit4TestAdapter( PublicationDaoImplTest.class );
    }    
    
    //////////////////////////
    // Initialisation
    
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    ////////////////////
    // Tests

    @Test
    public void persit_search_delete() throws IntactTransactionException {

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        PublicationDao pdao = daoFactory.getPublicationDao();
        CvObjectDao<CvObject> cvdao = daoFactory.getCvObjectDao();

        final String pmid = "123456789";
        Institution owner = IntactContext.getCurrentInstance().getConfig().getInstitution();
        Publication p = new Publication( owner, pmid );

        CvDatabase pubmed = (CvDatabase ) cvdao.getByPsiMiRef( CvDatabase.PUBMED_MI_REF );
        assertNotNull( pubmed );

        CvXrefQualifier primaryRef = (CvXrefQualifier ) cvdao.getByPsiMiRef( CvXrefQualifier.PRIMARY_REFERENCE_MI_REF );
        assertNotNull( primaryRef );

        PublicationXref xref = new PublicationXref( owner, pubmed, pmid, primaryRef );
        p.addXref( xref );

        pdao.persist( p );

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        String ac = null;

        
        // check that we can access the data by shortlabel and Xref
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        pdao = daoFactory.getPublicationDao();

        Publication pubByLabel = pdao.getByShortLabel( pmid );
        assertNotNull( pubByLabel );

        Publication pubByPrimaryId = pdao.getByXref( pmid );
        assertNotNull( pubByPrimaryId );

        assertEquals( pubByLabel, pubByPrimaryId );
        assertEquals( pubByLabel.getAc(), pubByPrimaryId.getAc() );

        ac = pubByLabel.getAc();
        assertNotNull( ac );

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();


        // clean up created data
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        pdao = daoFactory.getPublicationDao();

        pdao.deleteByAc( ac );

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();
    }
}
