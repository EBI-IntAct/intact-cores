package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.collections.map.IdentityMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.Source;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringLocalObject;
import uk.ac.ebi.intact.jami.model.extension.IntactSource;
import uk.ac.ebi.intact.jami.model.user.Preference;
import uk.ac.ebi.intact.jami.model.user.Role;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.synchronizer.*;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Finder/persister for users
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class UserSynchronizer extends AbstractIntactDbSynchronizer<User, User> {

    private Map<User, User> persistedUsers;

    private static final Log log = LogFactory.getLog(UserSynchronizer.class);

    public UserSynchronizer(SynchronizerContext context){
        super(context, User.class);
        this.persistedUsers = new IdentityMap();
    }

    public User find(User user) throws FinderException {
        Query query;
        if (user == null){
            return null;
        }
        else if (this.persistedUsers.containsKey(user)){
            return this.persistedUsers.get(user);
        }
        // we have a simple roles. Only check its taxid
        else {
            query = getEntityManager().createQuery("select u from User u " +
                    "where u.login = :login");
            query.setParameter("login", user.getLogin().trim());
        }
        Collection<User> users = query.getResultList();
        if (users.size() == 1){
            return users.iterator().next();
        }
        else if (users.size() > 1){
            throw new FinderException("The user "+user + " can match "+users.size()+" users in the database and we cannot determine which one is valid.");
        }
        return null;
    }

    public void synchronizeProperties(User object) throws FinderException, PersisterException, SynchronizerException {
        // check first name
        // truncate if necessary
        if (IntactUtils.MAX_SHORT_LABEL_LEN < object.getFirstName().length()){
            log.warn("User first name too long: "+object.getFirstName()+", will be truncated to "+ IntactUtils.MAX_SHORT_LABEL_LEN+" characters.");
            object.setFirstName(object.getFirstName().substring(0, IntactUtils.MAX_SHORT_LABEL_LEN));
        }
        // check full name
        // truncate if necessary
        if (IntactUtils.MAX_SHORT_LABEL_LEN < object.getLastName().length()){
            log.warn("User last name too long: "+object.getLastName()+", will be truncated to "+ IntactUtils.MAX_SHORT_LABEL_LEN+" characters.");
            object.setLastName(object.getLastName().substring(0, IntactUtils.MAX_SHORT_LABEL_LEN));
        }

        // synchronize preferences
        preparePreferences(object);
        // synchronize roles
        prepareRoles(object);
    }

    public void clearCache() {
        this.persistedUsers.clear();
    }

    protected void prepareRoles(User intactUser) throws FinderException, PersisterException, SynchronizerException {
        if (intactUser.areRolesInitialized()){
            List<Role> rolesToPersist = new ArrayList<Role>(intactUser.getRoles());
            for (Role role : rolesToPersist){
                Role userRole = getContext().getRoleSynchronizer().synchronize(role, true);
                // we have a different instance because needed to be synchronized
                if (userRole != role){
                    intactUser.removeRole(role);
                    intactUser.addRole(userRole);
                }
            }
        }
    }

    protected void preparePreferences(User intactUser) throws FinderException, PersisterException, SynchronizerException {
        if (intactUser.arePreferencesInitialized()){
            List<Preference> preferencesToPersist = new ArrayList<Preference>(intactUser.getPreferences());
            for (Preference pref : preferencesToPersist){
                // do not persist or merge preferences because of cascades
                Preference userPref = getContext().getPreferenceSynchronizer().synchronize(pref, false);
                // we have a different instance because needed to be synchronized
                if (userPref != pref){
                    intactUser.removePreference(pref);
                    intactUser.addPreference(userPref);
                }
            }
        }
    }

    @Override
    protected Object extractIdentifier(User object) {
        return object.getAc();
    }

    @Override
    protected User instantiateNewPersistentInstance(User object, Class<? extends User> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        User user = new User(object.getLogin(), object.getFirstName(), object.getLastName(), object.getEmail());
        user.setDisabled(object.isDisabled());
        user.setLastLogin(object.getLastLogin());
        user.setPassword(object.getPassword());
        return user;
    }

    @Override
    protected void storeInCache(User originalObject, User persistentObject, User existingInstance) {
        if (existingInstance != null){
            this.persistedUsers.put(originalObject, existingInstance);
        }
        else{
            this.persistedUsers.put(originalObject, persistentObject);
        }
    }

    @Override
    protected User fetchObjectFromCache(User object) {
        return this.persistedUsers.get(object);
    }

    @Override
    protected boolean isObjectStoredInCache(User object) {
        return this.persistedUsers.containsKey(object);
    }

    @Override
    protected void initialiseDefaultMerger() {
        // never update a user that exist in the database!!! If it exists, use the existing instance
        super.setIntactMerger(new IntactDbMergerIgnoringLocalObject<User, User>(this));
    }
}
