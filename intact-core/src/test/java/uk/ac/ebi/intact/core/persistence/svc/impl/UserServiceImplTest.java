package uk.ac.ebi.intact.core.persistence.svc.impl;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.intact.core.persistence.dao.user.UserDao;
import uk.ac.ebi.intact.core.persistence.svc.UserService;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.user.User;

import java.util.Arrays;
import java.util.List;

/**
 * UserServiceImpl Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.5
 */
public class UserServiceImplTest extends IntactBasicTestCase {

    @Autowired
    private UserService userService;

    @Test
    public void importUsers() throws Exception {

        final User sandra = getMockBuilder().createUserSandra();
        final User jyoti = getMockBuilder().createUserJyoti();

        final UserDao userDao = getDataContext().getDaoFactory().getUserDao();
        userDao.persist( sandra );
        userDao.persist( jyoti );

        final List<User> users = userDao.getAll();
        for ( User user : users ) {
            System.out.println(user);
        }

        // admin + undefined + 2
        Assert.assertEquals( 4, users.size() );

        // Test user import
        User updatedSandra = getMockBuilder().createUser( "sandra", "s", "o", "so@example.com" );
        User sam = getMockBuilder().createUser( "sam", "s", "k", "sk@example.com" );

        userService.importUsers( Arrays.asList( updatedSandra, sam, jyoti ), true );

        final User reloadedSandra = userDao.getByLogin( "sandra" );
        Assert.assertNotNull( reloadedSandra );
        Assert.assertEquals( "sandra", reloadedSandra.getLogin() );
        Assert.assertEquals( "s", reloadedSandra.getFirstName() );
        Assert.assertEquals( "o", reloadedSandra.getLastName() );
        Assert.assertEquals( "so@example.com", reloadedSandra.getEmail() );

        final User reloadedSam = userDao.getByLogin( "sam" );
        Assert.assertNotNull( reloadedSam );
        Assert.assertEquals( "sam", reloadedSam.getLogin() );
        Assert.assertEquals( "s", reloadedSam.getFirstName() );
        Assert.assertEquals( "k", reloadedSam.getLastName() );
        Assert.assertEquals( "sk@example.com", reloadedSam.getEmail() );

        final User reloadedJyoti = userDao.getByLogin( "jyoti" );
        Assert.assertNotNull( reloadedJyoti );
        Assert.assertEquals( "jyoti", reloadedJyoti.getLogin() );
        Assert.assertEquals( "jyoti", reloadedJyoti.getFirstName() );
        Assert.assertEquals( "-", reloadedJyoti.getLastName() );
        Assert.assertEquals( "jyoti@example.com", reloadedJyoti.getEmail() );
    }

    @Test
    public void importUsers_noUpdate() throws Exception {

        final User sandra = getMockBuilder().createUserSandra();
        final User jyoti = getMockBuilder().createUserJyoti();

        getCorePersister().saveOrUpdate( sandra, jyoti );

        final UserDao userDao = getDataContext().getDaoFactory().getUserDao();
        final List<User> users = userDao.getAll();
        // admin + undefined + 2
        Assert.assertEquals( 4, users.size() );

        // Test user import
        User updatedSandra = getMockBuilder().createUser( "sandra", "s", "o", "so@example.com" );
        User sam = getMockBuilder().createUser( "sam", "s", "k", "sk@example.com" );

        userService.importUsers( Arrays.asList( updatedSandra, sam, jyoti ), false );

        // this user should be identical to the original (mock) one
        final User reloadedSandra = userDao.getByLogin( "sandra" );
        Assert.assertNotNull( reloadedSandra );
        Assert.assertEquals( "sandra", reloadedSandra.getLogin() );
        Assert.assertEquals( "sandra", reloadedSandra.getFirstName() );
        Assert.assertEquals( "-", reloadedSandra.getLastName() );
        Assert.assertEquals( "sandra@example.com", reloadedSandra.getEmail() );

        final User reloadedSam = userDao.getByLogin( "sam" );
        Assert.assertNotNull( reloadedSam );
        Assert.assertEquals( "sam", reloadedSam.getLogin() );
        Assert.assertEquals( "s", reloadedSam.getFirstName() );
        Assert.assertEquals( "k", reloadedSam.getLastName() );
        Assert.assertEquals( "sk@example.com", reloadedSam.getEmail() );

        final User reloadedJyoti = userDao.getByLogin( "jyoti" );
        Assert.assertNotNull( reloadedJyoti );
        Assert.assertEquals( "jyoti", reloadedJyoti.getLogin() );
        Assert.assertEquals( "jyoti", reloadedJyoti.getFirstName() );
        Assert.assertEquals( "-", reloadedJyoti.getLastName() );
        Assert.assertEquals( "jyoti@example.com", reloadedJyoti.getEmail() );
    }
}
