package uk.ac.ebi.intact.jami.model.lifecycle;

import uk.ac.ebi.intact.jami.model.user.User;

import java.util.List;

/**
 * Interface for objects that can go through the IntAct lifecycle
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>18/06/14</pre>
 */

public interface Releasable {

    public LifeCycleStatus getStatus();

    public void setStatus( LifeCycleStatus status );

    public User getCurrentOwner();

    public void setCurrentOwner( User currentOwner );

    public User getCurrentReviewer();

    public void setCurrentReviewer( User currentReviewer );

    public boolean areLifeCycleEventsInitialized();

    public List<LifeCycleEvent> getLifecycleEvents();
}
