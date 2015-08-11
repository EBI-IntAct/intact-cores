package uk.ac.ebi.intact.jami.synchronizer.listener.impl;

import org.apache.commons.collections.map.IdentityMap;
import psidev.psi.mi.jami.enricher.listener.EnrichmentStatus;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactMergerException;
import uk.ac.ebi.intact.jami.model.user.Preference;
import uk.ac.ebi.intact.jami.model.user.Role;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.synchronizer.listener.UserEnricherListener;
import uk.ac.ebi.intact.jami.synchronizer.listener.updates.UserUpdates;
import uk.ac.ebi.intact.jami.utils.IntactEnricherUtils;

import java.util.List;
import java.util.Map;

/**
 * Listener that will synchronize updates done to an existing object in the database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public class DbUserEnricherListener implements UserEnricherListener {
    private Map<User, UserUpdates> userUpdates;
    private SynchronizerContext context;
    private IntactDbSynchronizer dbSynchronizer;

    public DbUserEnricherListener(SynchronizerContext context, IntactDbSynchronizer dbSynchronizer) {
        if (context == null){
            throw new IllegalArgumentException("The listener needs a non null synchronizer context");
        }
        this.context = context;
        if (dbSynchronizer == null){
            throw new IllegalArgumentException("The listener needs a non null user synchronizer");
        }
        this.dbSynchronizer = dbSynchronizer;
        this.userUpdates = new IdentityMap();
    }

    @Override
    public void onEnrichmentComplete(User object, EnrichmentStatus status, String message) {
        if (userUpdates.containsKey(object)){
            UserUpdates updates = userUpdates.get(object);
            try {
                if (!updates.getAddedRoles().isEmpty()){

                    List<Role> synchronizedRoles = IntactEnricherUtils.synchronizeUserRolesToEnrich(updates.getAddedRoles(),
                            context.getRoleSynchronizer());
                    object.getRoles().removeAll(updates.getAddedRoles());
                    object.getRoles().addAll(synchronizedRoles);
                }

                userUpdates.remove(object);
            } catch (PersisterException e) {
                userUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged user", e);
            } catch (FinderException e) {
                userUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged user", e);
            } catch (SynchronizerException e) {
                userUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged user", e);
            }
        }
    }

    @Override
    public void onEnrichmentError(User object, String message, Exception e) {
        if (userUpdates.containsKey(object)){
            UserUpdates updates = userUpdates.get(object);
            try {
                if (!updates.getAddedRoles().isEmpty()){

                    List<Role> synchronizedRoles = IntactEnricherUtils.synchronizeUserRolesToEnrich(updates.getAddedRoles(),
                            context.getRoleSynchronizer());
                    object.getRoles().removeAll(updates.getAddedRoles());
                    object.getRoles().addAll(synchronizedRoles);
                }

                userUpdates.remove(object);
            } catch (PersisterException e2) {
                userUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged user", e2);
            } catch (FinderException e2) {
                userUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged user", e2);
            } catch (SynchronizerException e2) {
                userUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged user", e2);
            }
        }
    }

    public Map<User, UserUpdates> getUserUpdates() {
        return userUpdates;
    }

    protected SynchronizerContext getContext() {
        return context;
    }

    protected IntactDbSynchronizer getDbSynchronizer() {
        return dbSynchronizer;
    }

    @Override
    public void onAddedPreference(User user, Preference added) {
        if (this.userUpdates.containsKey(user)){
            this.userUpdates.get(user).getAddedPreferences().add(added);
        }
        else{
            UserUpdates updates = new UserUpdates();
            updates.getAddedPreferences().add(added);
            this.userUpdates.put(user, updates);
        }
    }

    @Override
    public void onRemovedPreference(User user, Preference removed) {
         // nothing to do
    }

    @Override
    public void onAddedRole(User user, Role added) {
        if (this.userUpdates.containsKey(user)){
            this.userUpdates.get(user).getAddedRoles().add(added);
        }
        else{
            UserUpdates updates = new UserUpdates();
            updates.getAddedRoles().add(added);
            this.userUpdates.put(user, updates);
        }
    }

    @Override
    public void onRemovedRole(User user, Role removed) {
        // nothing to do
    }
}
