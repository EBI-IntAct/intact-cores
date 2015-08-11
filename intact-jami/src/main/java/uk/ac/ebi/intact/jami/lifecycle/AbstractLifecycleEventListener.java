package uk.ac.ebi.intact.jami.lifecycle;


import uk.ac.ebi.intact.jami.model.lifecycle.Releasable;
import uk.ac.ebi.intact.jami.model.user.User;

/**
 * LifecycleEventListener with empty implementation of all methods.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.5
 */
public class AbstractLifecycleEventListener implements LifecycleEventListener {

    @Override
    public void fireOwnerChanged( Releasable releaseable, User previousOwner, User newOwner ) {
    }

    @Override
    public void fireReviewerChanged( Releasable releaseable, User previousReviewer, User newReviewer ) {
    }

    @Override
    public void fireCreated( Releasable releaseable ) {
    }

    @Override
    public void fireReserved( Releasable releaseable ) {
    }

    @Override
    public void fireAssignentDeclined( Releasable releaseable ) {
    }

    @Override
    public void fireAssigned( Releasable releaseable, User byUser, User toUser ) {
    }

    @Override
    public void fireCurationInProgress( Releasable releaseable ) {
    }

    @Override
    public void fireReadyForChecking( Releasable releaseable ) {
    }

    @Override
    public void fireAccepted( Releasable releaseable ) {
    }

    @Override
    public void fireAcceptedOnHold( Releasable releaseable ) {
    }

    @Override
    public void fireRejected(Releasable releaseable ) {
    }

    @Override
    public void fireReadyForRelease( Releasable releaseable ) {
    }

    @Override
    public void fireReleased( Releasable releaseable ) {
    }

    @Override
    public void fireDiscarded(Releasable releaseable) {
    }

    @Override
    public void firePutOnHold(Releasable releaseable) {
    }
}
