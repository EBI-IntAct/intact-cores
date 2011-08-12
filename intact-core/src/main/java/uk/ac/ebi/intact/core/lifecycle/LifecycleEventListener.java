package uk.ac.ebi.intact.core.lifecycle;

import uk.ac.ebi.intact.model.Publication;
import uk.ac.ebi.intact.model.user.User;

/**
 * LifecycleEvent Listener.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.5
 */
public interface LifecycleEventListener {

    void fireOwnerChanged( Publication publication, User previousOwner, User newOwner );

    void fireReviewerChanged( Publication publication, User previousReviewer, User newReviewer );

    void fireCreated( Publication publication );

    void fireReserved( Publication publication );

    void fireAssignentDeclined( Publication publication );

    void fireAssigned( Publication publication, User byUser, User toUser );

    void fireCurationInProgress( Publication publication );

    void fireReadyForChecking( Publication publication );

    void fireAccepted( Publication publication );

    void fireAcceptedOnHold( Publication publication );

    void fireRejected( Publication publication );

    void fireReadyForRelease( Publication publication );

    void fireReleased( Publication publication );

    void fireDiscarded( Publication publication );

    void firePutOnHold ( Publication publication );
}