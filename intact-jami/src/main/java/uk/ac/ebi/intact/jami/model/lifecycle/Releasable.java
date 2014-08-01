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

    public final static String ON_HOLD = "on-hold";
    public final static String TO_BE_REVIEWED = "to-be-reviewed";
    public final static String ACCEPTED = "accepted";

    public LifeCycleStatus getStatus();

    public void setStatus( LifeCycleStatus status );

    public User getCurrentOwner();

    public void setCurrentOwner( User currentOwner );

    public User getCurrentReviewer();

    public void setCurrentReviewer( User currentReviewer );

    public boolean areLifeCycleEventsInitialized();

    public List<LifeCycleEvent> getLifecycleEvents();

    public void onHold(String message);

    public boolean isOnHold();

    public void removeOnHold();

    public String getOnHoldComment();

    public void onToBeReviewed(String message);

    public boolean isToBeReviewed();

    public void removeToBeReviewed();

    public String getToBeReviewedComment();

    public void onAccepted(String message);

    public boolean isAccepted();

    public void removeAccepted();

    public String getAcceptedComment();

}
