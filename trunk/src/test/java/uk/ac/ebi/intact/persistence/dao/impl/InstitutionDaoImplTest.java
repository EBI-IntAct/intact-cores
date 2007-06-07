package uk.ac.ebi.intact.persistence.dao.impl;

import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.InstitutionDao;

/**
 * InstitutionDaoImpl Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.5.0
 */
public class InstitutionDaoImplTest {

    ////////////////////////////////
    // Compatibility with JUnit 3

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter( InstitutionDaoImplTest.class );
    }

    ////////////////////
    // Tests  

    @Test
    public void getByShortLabel() throws Exception {
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        InstitutionDao idao = daoFactory.getInstitutionDao();
        Institution institution = idao.getByShortLabel( "ebi" );
        assertNotNull( institution );
        assertEquals( "ebi", institution.getShortLabel() );

        institution = idao.getByShortLabel( "blabla" );
        assertNull( institution );
        
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();
    }

    @Ignore
    public void persist() throws Exception {
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        InstitutionDao idao = daoFactory.getInstitutionDao();

        Institution institution = new Institution( "institution" );
        assertNotNull( institution );
        assertEquals( "institution", institution.getShortLabel() );
        assertNull( institution.getAc() );

        idao.persist( institution );
        assertNotNull( institution.getAc() );


        Institution otherInstitution = idao.getByShortLabel( "institution" );
        assertNull( otherInstitution );
        assertSame( institution, otherInstitution );

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();
    }
}
