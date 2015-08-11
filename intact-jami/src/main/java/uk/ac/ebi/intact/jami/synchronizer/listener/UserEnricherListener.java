package uk.ac.ebi.intact.jami.synchronizer.listener;

import psidev.psi.mi.jami.enricher.listener.EnricherListener;
import uk.ac.ebi.intact.jami.model.user.Preference;
import uk.ac.ebi.intact.jami.model.user.Role;
import uk.ac.ebi.intact.jami.model.user.User;

/**
 * User enricher listener
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public interface UserEnricherListener extends EnricherListener<User>{

    public void onAddedPreference(User user, Preference added);

    public void onRemovedPreference(User user, Preference removed);

    public void onAddedRole(User user, Role added);

    public void onRemovedRole(User user, Role removed);

}
