package uk.ac.ebi.intact.core.lifecycle.status;

import uk.ac.ebi.intact.core.lifecycle.AbstractLifecycleEventListener;
import uk.ac.ebi.intact.model.Publication;
import uk.ac.ebi.intact.model.user.User;

/**
 * TODO document this !
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.5
 */
public class CountingLifecycleEventListener extends AbstractLifecycleEventListener {

    private int ownerChangedCount;
    private int reviewerChangedCount;
    private int createdCount;
    private int reservedCount;
    private int assignementDeclinedCount;
    private int assignedCount;
    private int curationInProgressCount;
    private int readyForCheckingCount;
    private int acceptedCount;
    private int acceptedOnHoldCount;
    private int rejectedCount;
    private int readyForReleaseCount;
    private int releasedCount;
    private int discardedCount;

    ////////////////////////
    // LifecycleEventListener

    @Override
    public void fireOwnerChanged( Publication publication, User previousOwner, User newOwner ) {
        ownerChangedCount++;
    }

    @Override
    public void fireReviewerChanged( Publication publication, User previousReviewer, User newReviewer ) {
        reviewerChangedCount++;
    }

    @Override
    public void fireCreated( Publication pub ) {
        createdCount++;
    }

    @Override
    public void fireReserved( Publication publication ) {
        reservedCount++;
    }

    @Override
    public void fireAssignentDeclined( Publication publication ) {
        assignementDeclinedCount++;
    }

    @Override
    public void fireAssigned( Publication publication, User byUser, User toUser ) {
        assignedCount++;
    }

    @Override
    public void fireCurationInProgress( Publication publication ) {
        curationInProgressCount++;
    }

    @Override
    public void fireReadyForChecking( Publication publication ) {
        readyForCheckingCount++;
    }

    @Override
    public void fireAccepted( Publication publication ) {
        acceptedCount++;
    }

    @Override
    public void fireRejected( Publication publication ) {
        rejectedCount++;
    }

    @Override
    public void fireReadyForRelease( Publication publication ) {
        readyForReleaseCount++;
    }

    @Override
    public void fireReleased( Publication publication ) {
        releasedCount++;
    }

    @Override
    public void fireAcceptedOnHold( Publication publication ) {
        acceptedOnHoldCount++;
    }

    @Override
    public void fireDiscarded( Publication publication ) {
        discardedCount++;
    }

    //////////////////
    // Getters

    public int getCreatedCount() {
        return createdCount;
    }

    public int getOwnerChangedCount() {
        return ownerChangedCount;
    }

    public int getReviewerChangedCount() {
        return reviewerChangedCount;
    }

    public int getReservedCount() {
        return reservedCount;
    }

    public int getAssignementDeclinedCount() {
        return assignementDeclinedCount;
    }

    public int getAssignedCount() {
        return assignedCount;
    }

    public int getCurationInProgressCount() {
        return curationInProgressCount;
    }

    public int getReadyForCheckingCount() {
        return readyForCheckingCount;
    }

    public int getAcceptedCount() {
        return acceptedCount;
    }

    public int getRejectedCount() {
        return rejectedCount;
    }

    public int getReadyForReleaseCount() {
        return readyForReleaseCount;
    }

    public int getReleasedCount() {
        return releasedCount;
    }

    public int getAcceptedOnHoldCount() {
        return acceptedOnHoldCount;
    }

    public int getDiscardedCount() {
        return discardedCount;
    }
}
