package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.listener.EnrichmentStatus;
import uk.ac.ebi.intact.jami.model.user.Preference;
import uk.ac.ebi.intact.jami.model.user.Role;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.synchronizer.listener.UserEnricherListener;

import java.util.Collection;
import java.util.Iterator;

/**
 * User merger that will update properties of an existing user.
 * It will override all properties of user
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class UserMergerOverride extends IntactDbMergerEnrichOnly<User, User> implements UserEnricher{
    private UserEnricherListener listener;

    public UserMergerOverride(){
        super(User.class);
    }

    @Override
    protected void mergeOtherProperties(User obj1, User obj2) {
        super.mergeOtherProperties(obj1, obj2);
        // obj2 is mergedUser
        User mergedUser = obj2;

        // merge last login
        if ((mergedUser.getLastLogin() == null && obj1.getLastLogin() != null) ||
                (mergedUser.getLastLogin() != null && !mergedUser.getLastLogin().equals(obj1.getLastLogin()))){
            mergedUser.setLastLogin(obj1.getLastLogin());
        }
        //merge roles
        if (obj1.areRolesInitialized()){
            mergeRoles(mergedUser, mergedUser.getRoles(), obj1.getRoles());
        }
        //merge preferences
        if (obj1.arePreferencesInitialized()){
            mergePreferences(mergedUser, mergedUser.getPreferences(), obj1.getPreferences());
        }
        if (getUserEnricherListener() != null){
            getUserEnricherListener().onEnrichmentComplete(mergedUser, EnrichmentStatus.SUCCESS, "user enriched");
        }
    }

    private void mergeRoles(User userToEnrich, Collection<Role> toEnrichRoles, Collection<Role> sourceRoles) {
        Iterator<Role> roleIterator = toEnrichRoles.iterator();
        while(roleIterator.hasNext()){
            Role role = roleIterator.next();
            boolean containsRole = false;
            for (Role role2 : sourceRoles){
                if (role.equals(role2)){
                    containsRole = true;
                    break;
                }
            }
            // remove roles not in second list
            if (!containsRole){
                roleIterator.remove();
                if (getUserEnricherListener() != null){
                    getUserEnricherListener().onRemovedRole(userToEnrich, role);
                }
            }
        }

        roleIterator = sourceRoles.iterator();
        while(roleIterator.hasNext()){
            Role role = roleIterator.next();
            boolean containsRole = false;
            for (Role role2 : toEnrichRoles){
                // identical roles
                if (role.equals(role2)){
                    containsRole = true;
                    break;
                }
            }
            // add missing role not in second list
            if (!containsRole){
                userToEnrich.getRoles().add(role);
                if (getUserEnricherListener() != null){
                    getUserEnricherListener().onAddedRole(userToEnrich, role);
                }
            }
        }
    }


    private void mergePreferences(User userToEnrich, Collection<Preference> toEnrichPreferences, Collection<Preference> sourcePreferences){

        Iterator<Preference> prefIterator = toEnrichPreferences.iterator();
        while(prefIterator.hasNext()){
            Preference pref = prefIterator.next();
            boolean containsPref = false;
            for (Preference pref2 : sourcePreferences){
                if (pref.equals(pref2)){
                    containsPref = true;
                    break;
                }
            }
            // remove preferences not in second list
            if (!containsPref){
                prefIterator.remove();
                if (getUserEnricherListener() != null){
                    getUserEnricherListener().onRemovedPreference(userToEnrich, pref);
                }
            }
        }

        prefIterator = sourcePreferences.iterator();
        while(prefIterator.hasNext()){
            Preference pref = prefIterator.next();
            boolean containsPref = false;
            for (Preference pref2 : toEnrichPreferences){
                // identical pref
                if (pref.equals(pref2)){
                    // if value updated, update value of pref in the preferences to enrich
                    if ((pref.getValue() == null && pref2.getValue() == null)
                            || (pref.getValue() != null && pref.getValue().equals(pref2.getValue()))){
                        pref2.setValue(pref.getValue());
                        break;
                    }
                    else{
                        containsPref = true;
                        break;
                    }
                }
            }
            // add missing pref not in second list
            if (!containsPref){
                toEnrichPreferences.add(pref);
                if (getUserEnricherListener() != null){
                    getUserEnricherListener().onRemovedPreference(userToEnrich, pref);
                }
            }
        }
    }

    @Override
    public UserEnricherListener getUserEnricherListener() {
        return listener;
    }

    @Override
    public void setUserEnricherListener(UserEnricherListener listener) {
        this.listener = listener;
    }
}
