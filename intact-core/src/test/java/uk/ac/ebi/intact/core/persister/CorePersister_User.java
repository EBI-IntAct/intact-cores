package uk.ac.ebi.intact.core.persister;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.persistence.dao.user.UserDao;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.user.Preference;
import uk.ac.ebi.intact.model.user.Role;
import uk.ac.ebi.intact.model.user.User;

/**
 * Testing handling of Users by the CorePersister.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.5
 */
public class CorePersister_User extends IntactBasicTestCase {

    @Test
    public void cascadeUpdate_preferences() throws Exception {

        final User sandra = getMockBuilder().createUserSandra();
        sandra.getPreferences().clear();
        final Preference preference = new Preference( sandra, "idea", "something bright" );
        sandra.addPreference( preference );

        final UserDao userDao = getDaoFactory().getUserDao();
        final int originalUserCount = userDao.getAll().size();

        getCorePersister().saveOrUpdate( sandra );

        Assert.assertEquals( originalUserCount + 1, userDao.getAll().size() );

        preference.setValue( "eureka !" );
        final Preference todo = new Preference( sandra, "todo", "some tasks" );
        sandra.addPreference( todo );

        getCorePersister().saveOrUpdate( sandra );

        final User reloadedSandra = userDao.getByLogin( "sandra" );
        Assert.assertEquals( 2, reloadedSandra.getPreferences().size() );
        Assert.assertEquals( "eureka !", reloadedSandra.getPreference( "idea" ).getValue() );
        Assert.assertNotNull( reloadedSandra.getPreference( "todo" ) );
    }

    @Test
    public void cascadeUpdate_roles() throws Exception {

        final User sandra = getMockBuilder().createUserSandra();
        sandra.getRoles().clear();
        Role curator = new Role( "CURATOR" );
        sandra.addRole( curator );

        final UserDao userDao = getDaoFactory().getUserDao();
        final int originalUserCount = userDao.getAll().size();

        getCorePersister().saveOrUpdate( sandra );

        Assert.assertEquals( originalUserCount + 1, userDao.getAll().size() );

        Role admin = new Role( "ADMIN" );
        sandra.addRole( admin );

        getCorePersister().saveOrUpdate( sandra );

        final User reloadedSandra = userDao.getByLogin( "sandra" );
        Assert.assertEquals( 2, reloadedSandra.getRoles().size() );
        Assert.assertTrue( sandra.hasRole( "CURATOR" ) );
        Assert.assertTrue( sandra.hasRole( "ADMIN" ) );
    }
}
