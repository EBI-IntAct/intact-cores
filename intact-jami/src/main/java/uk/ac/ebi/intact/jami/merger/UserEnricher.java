package uk.ac.ebi.intact.jami.merger;

import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.synchronizer.listener.UserEnricherListener;

/**
 * User enricher
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>05/02/15</pre>
 */

public interface UserEnricher extends IntactDbMerger<User,User>{

    public UserEnricherListener getUserEnricherListener();
    public void setUserEnricherListener(UserEnricherListener listener);
}
