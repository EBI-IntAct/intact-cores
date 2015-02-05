package uk.ac.ebi.intact.jami.synchronizer.listener.updates;

import uk.ac.ebi.intact.jami.model.user.Preference;
import uk.ac.ebi.intact.jami.model.user.Role;

import java.util.ArrayList;
import java.util.List;

/**
 * Class listing all updates in a cv term
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public class UserUpdates {
    private List<Preference> addedPreferences = new ArrayList<Preference>();
    private List<Role> addedRoles = new ArrayList<Role>();

    public List<Preference> getAddedPreferences() {
        return addedPreferences;
    }

    public List<Role> getAddedRoles() {
        return addedRoles;
    }
}
