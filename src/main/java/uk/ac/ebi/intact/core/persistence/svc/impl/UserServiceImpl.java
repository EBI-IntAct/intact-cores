package uk.ac.ebi.intact.core.persistence.svc.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.intact.core.persistence.dao.user.PreferenceDao;
import uk.ac.ebi.intact.core.persistence.dao.user.RoleDao;
import uk.ac.ebi.intact.core.persistence.dao.user.UserDao;
import uk.ac.ebi.intact.core.persistence.dao.user.impl.UserDaoImpl;
import uk.ac.ebi.intact.core.persistence.svc.UserService;
import uk.ac.ebi.intact.core.persister.CorePersister;
import uk.ac.ebi.intact.model.user.Preference;
import uk.ac.ebi.intact.model.user.Role;
import uk.ac.ebi.intact.model.user.User;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * A service allowing to import new users in batches.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.5
 */
@Component
public class UserServiceImpl implements UserService {

    @Autowired
    private CorePersister corePersister;

    @Autowired
    private UserDao userDao;

    public UserServiceImpl() {
    }

    /**
     * Import new users into the local database.
     *
     * @param users the collection of users to import.
     * @param updateExistingUsers if false, only create new users, otherwise, override all attribute of existing users
     *                            with given ones.
     */
    @Override
    public void importUsers( Collection<User> users, boolean updateExistingUsers ) {

        if( userDao == null ) {
            System.err.println( "userDao is null" );
            return;
        }

        for ( User newUser : users ) {
            final User existingUser = userDao.getByLogin( newUser.getLogin() );
            if( existingUser == null ) {
                corePersister.saveOrUpdate( newUser );

            } else {
                if( updateExistingUsers ) {
                    // update existing user
                    existingUser.setPassword( newUser.getPassword() );
                    existingUser.setDisabled( newUser.isDisabled() );
                    existingUser.setFirstName( newUser.getFirstName() );
                    existingUser.setLastName( newUser.getLastName() );
                    existingUser.setEmail( newUser.getEmail() );
                    existingUser.setOpenIdUrl( newUser.getOpenIdUrl() );
                    existingUser.setPreferences( newUser.getPreferences() );
                    existingUser.setRoles( newUser.getRoles() );

                    corePersister.saveOrUpdate( existingUser );
                }
            }
        }
    }
}
