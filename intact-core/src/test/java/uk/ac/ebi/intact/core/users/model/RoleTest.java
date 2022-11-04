package uk.ac.ebi.intact.core.users.model;

import junit.framework.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.persistence.dao.user.RoleDao;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.user.Role;

import javax.persistence.PersistenceException;

/**
 * Role Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public class RoleTest extends IntactBasicTestCase {

    @Test
    public void search() throws Exception {
        final RoleDao roleDao = getDaoFactory().getRoleDao();

        Assert.assertNotNull( roleDao.getRoleByName( "CURATOR" ) );
        Assert.assertNotNull( roleDao.getRoleByName( "cuRAtor" ) );
        Assert.assertNotNull( roleDao.getRoleByName( "curator" ) );

        Assert.assertNull( roleDao.getRoleByName( "foo" ) );
    }

    @Test( expected = PersistenceException.class )
    public void uniqueRole() throws Exception {
        final RoleDao roleDao = getDaoFactory().getRoleDao();

        Role role1 = new Role( "CURATOR" );
        roleDao.persist( role1 );

        Role role2 = new Role( "CURATOR" );
        roleDao.persist( role2 );

        getDaoFactory().getEntityManager().flush();
    }
}
