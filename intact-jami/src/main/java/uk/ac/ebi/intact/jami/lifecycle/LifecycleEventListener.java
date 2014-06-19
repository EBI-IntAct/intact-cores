package uk.ac.ebi.intact.jami.lifecycle;

import uk.ac.ebi.intact.jami.model.lifecycle.Releasable;
import uk.ac.ebi.intact.jami.model.user.User;

/**
 * LifecycleEvent Listener.
 *
 */
public interface LifecycleEventListener {

    void fireOwnerChanged(Releasable releaseable, User previousOwner, User newOwner);

    void fireReviewerChanged(Releasable releaseable, User previousReviewer, User newReviewer);

    void fireCreated(Releasable releaseable);

    void fireReserved(Releasable releaseable);

    void fireAssignentDeclined(Releasable releaseable);

    void fireAssigned(Releasable releaseable, User byUser, User toUser);

    void fireCurationInProgress(Releasable releaseable);

    void fireReadyForChecking(Releasable releaseable);

    void fireAccepted(Releasable releaseable);

    void fireAcceptedOnHold(Releasable releaseable);

    void fireRejected(Releasable releaseable);

    void fireReadyForRelease(Releasable releaseable);

    void fireReleased(Releasable releaseable);

    void fireDiscarded(Releasable releaseable);

    void firePutOnHold(Releasable releaseable);
}