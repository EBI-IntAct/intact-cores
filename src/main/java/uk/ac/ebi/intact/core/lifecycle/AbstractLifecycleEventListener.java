package uk.ac.ebi.intact.core.lifecycle;

import uk.ac.ebi.intact.model.Publication;
import uk.ac.ebi.intact.model.user.User;

/**
 * LifecycleEventListener with empty implementation of all methods.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.5
 */
public class AbstractLifecycleEventListener implements LifecycleEventListener {

    @Override
    public void fireOwnerChanged( Publication publication, User previousOwner, User newOwner ) {
    }

    @Override
    public void fireReviewerChanged( Publication publication, User previousReviewer, User newReviewer ) {
    }

    @Override
    public void fireCreated( Publication publication ) {
    }

    @Override
    public void fireReserved( Publication publication ) {
    }

    @Override
    public void fireAssignentDeclined( Publication publication ) {
    }

    @Override
    public void fireAssigned( Publication publication, User byUser, User toUser ) {
    }

    @Override
    public void fireCurationInProgress( Publication publication ) {
    }

    @Override
    public void fireReadyForChecking( Publication publication ) {
    }

    @Override
    public void fireAccepted( Publication publication ) {
    }

    @Override
    public void fireRejected( Publication publication ) {
    }

    @Override
    public void fireReadyForRelease( Publication publication ) {
    }

    @Override
    public void fireReleased( Publication publication ) {
    }

    @Override
    public void fireReleasedOnHold( Publication publication ) {
    }

    @Override
    public void fireDiscarded( Publication publication ) {
    }
}
