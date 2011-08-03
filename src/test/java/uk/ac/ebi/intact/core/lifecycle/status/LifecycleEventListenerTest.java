package uk.ac.ebi.intact.core.lifecycle.status;

import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.lifecycle.LifecycleManager;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.user.User;
import uk.ac.ebi.intact.model.util.PublicationUtils;

import java.util.Arrays;
import java.util.Collection;

/**
 * LifecycleEventListener Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.5
 */
public class LifecycleEventListenerTest extends IntactBasicTestCase {

    @Autowired private LifecycleManager lifecycleManager;

    @Test
    public void fireCreated() throws Exception {

        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setStatus( null );
        publication.getLifecycleEvents().clear();

        final CountingLifecycleEventListener countingListener = new CountingLifecycleEventListener();
        lifecycleManager.registerListener( countingListener );

        Assert.assertEquals( 0, countingListener.getCreatedCount() );

        lifecycleManager.getStartStatus().create( publication, "test" );

        Assert.assertEquals( 1, countingListener.getCreatedCount() );
    }

    @Test
    public void fireCreated_remove() throws Exception {

        final CountingLifecycleEventListener countingListener = new CountingLifecycleEventListener();
        lifecycleManager.registerListener( countingListener );
        lifecycleManager.removeListener( countingListener );

        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setStatus( null );
        publication.getLifecycleEvents().clear();

        lifecycleManager.getStartStatus().create( publication, "test" );

        Assert.assertEquals( 0, countingListener.getCreatedCount() );
    }

    @Test
    public void fireChangeOwnership() throws Exception {

        final CountingLifecycleEventListener countingListener = new CountingLifecycleEventListener();
        lifecycleManager.registerListener( countingListener );

        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setStatus(null);
        publication.getLifecycleEvents().clear();

        final User sandra = getMockBuilder().createUserSandra();

        Assert.assertEquals( 0, countingListener.getOwnerChangedCount() );

        Assert.assertNull( publication.getCurrentOwner() );
        lifecycleManager.getGlobalStatus().changeOwnership( publication, sandra, "got some free time" );
        Assert.assertEquals( sandra, publication.getCurrentOwner() );

        Assert.assertEquals( 1, countingListener.getOwnerChangedCount() );
    }

    @Test
    public void fireChangeReviewer() throws Exception {

        final CountingLifecycleEventListener countingListener = new CountingLifecycleEventListener();
        lifecycleManager.registerListener( countingListener );

        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setStatus(null);
        publication.getLifecycleEvents().clear();

        final User sandra = getMockBuilder().createUserSandra();

        Assert.assertEquals( 0, countingListener.getReviewerChangedCount() );

        Assert.assertNull( publication.getCurrentReviewer() );
        lifecycleManager.getGlobalStatus().changeReviewer( publication, sandra, "got some free time" );
        Assert.assertEquals( sandra, publication.getCurrentReviewer() );

        Assert.assertEquals( 1, countingListener.getReviewerChangedCount() );
    }

    @Test
    public void fireReserved() throws Exception {

        final CountingLifecycleEventListener countingListener = new CountingLifecycleEventListener();
        lifecycleManager.registerListener( countingListener );

        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setStatus( getStatus( CvPublicationStatusType.NEW ) );
        publication.getLifecycleEvents().clear();

        Assert.assertEquals( 0, countingListener.getReservedCount() );

        lifecycleManager.getNewStatus().reserve( publication, "just couldn't help" );

        Assert.assertEquals( 1, countingListener.getReservedCount() );
    }

    @Test
    public void fireAssigned_claimOwnership() throws Exception {

        final CountingLifecycleEventListener countingListener = new CountingLifecycleEventListener();
        lifecycleManager.registerListener( countingListener );

        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setStatus( getStatus( CvPublicationStatusType.NEW ) );
        publication.getLifecycleEvents().clear();

        Assert.assertEquals( 0, countingListener.getOwnerChangedCount() );
        Assert.assertEquals( 0, countingListener.getAssignedCount() );

        lifecycleManager.getNewStatus().claimOwnership( publication );

        Assert.assertEquals( 1, countingListener.getOwnerChangedCount() );
        Assert.assertEquals( 1, countingListener.getAssignedCount() );
    }

    @Test
    public void fireAssigned_assignToCurator() throws Exception {

        final CountingLifecycleEventListener countingListener = new CountingLifecycleEventListener();
        lifecycleManager.registerListener( countingListener );

        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setStatus( getStatus( CvPublicationStatusType.NEW ) );
        publication.getLifecycleEvents().clear();

        Assert.assertEquals( 0, countingListener.getOwnerChangedCount() );
        Assert.assertEquals( 0, countingListener.getAssignedCount() );

        final User sandra = getMockBuilder().createUserSandra();
        lifecycleManager.getNewStatus().assignToCurator( publication, sandra );

        Assert.assertEquals( 1, countingListener.getOwnerChangedCount() );
        Assert.assertEquals( 1, countingListener.getAssignedCount() );
    }

    @Test
    public void fireDeclined() throws Exception {

        final CountingLifecycleEventListener countingListener = new CountingLifecycleEventListener();
        lifecycleManager.registerListener( countingListener );

        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setStatus( getStatus( CvPublicationStatusType.ASSIGNED ) );
        publication.getLifecycleEvents().clear();

        Assert.assertEquals( 0, countingListener.getAssignementDeclinedCount() );

        lifecycleManager.getAssignedStatus().unassign( publication, "just cannot at the moment" );

        Assert.assertEquals( 1, countingListener.getAssignementDeclinedCount() );
    }

    @Test
    public void fireCurationInProgress() throws Exception {

        final CountingLifecycleEventListener countingListener = new CountingLifecycleEventListener();
        lifecycleManager.registerListener( countingListener );

        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setStatus( getStatus( CvPublicationStatusType.ASSIGNED ) );
        publication.getLifecycleEvents().clear();

        Assert.assertEquals( 0, countingListener.getCurationInProgressCount() );

        lifecycleManager.getAssignedStatus().startCuration( publication );

        Assert.assertEquals( 1, countingListener.getCurationInProgressCount() );
    }

    @Test
    public void fireReadyForChecking_successfulSanityCheck() throws Exception {
        User reviewer1 = getMockBuilder().createReviewer("lalaReviewer", "Lala", "Smith", "lala@example.com");
        User reviewer2 = getMockBuilder().createReviewer("tataReviewer", "Tata", "Toto", "tata@example.com");
        getCorePersister().saveOrUpdate(reviewer1, reviewer2);

        final CountingLifecycleEventListener countingListener = new CountingLifecycleEventListener();
        lifecycleManager.registerListener( countingListener );

        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setStatus( getStatus( CvPublicationStatusType.CURATION_IN_PROGRESS ) );
        publication.getLifecycleEvents().clear();
        publication.setCurrentOwner(reviewer1);

        Assert.assertEquals( 0, countingListener.getReadyForCheckingCount() );

        lifecycleManager.getCurationInProgressStatus().readyForChecking( publication, "have fun", true );

        Assert.assertEquals( 1, countingListener.getReadyForCheckingCount() );
    }

    @Test
    public void fireReadyForChecking_failedSanityCheck() throws Exception {

        final CountingLifecycleEventListener countingListener = new CountingLifecycleEventListener();
        lifecycleManager.registerListener( countingListener );

        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setStatus( getStatus( CvPublicationStatusType.CURATION_IN_PROGRESS ) );
        publication.getLifecycleEvents().clear();

        Assert.assertEquals( 0, countingListener.getReadyForCheckingCount() );

        lifecycleManager.getCurationInProgressStatus().readyForChecking( publication, "have fun", false );

        Assert.assertEquals( 0, countingListener.getReadyForCheckingCount() );
    }

    @Test
    public void fireAccepted() throws Exception {

        final CountingLifecycleEventListener countingListener = new CountingLifecycleEventListener();
        lifecycleManager.registerListener( countingListener );

        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setStatus( getStatus( CvPublicationStatusType.READY_FOR_CHECKING ) );
        publication.getLifecycleEvents().clear();

        Assert.assertEquals( 0, countingListener.getAcceptedCount() );

        lifecycleManager.getReadyForCheckingStatus().accept( publication, "nice stuff!" );

        Assert.assertEquals( 1, countingListener.getAcceptedCount() );
    }

    @Test
    public void fireAccepted_onHold() throws Exception {

        final CountingLifecycleEventListener countingListener = new CountingLifecycleEventListener();
        lifecycleManager.registerListener( countingListener );

        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setStatus( getStatus( CvPublicationStatusType.READY_FOR_CHECKING ) );
        publication.getLifecycleEvents().clear();

        PublicationUtils.markAsOnHold( IntactContext.getCurrentInstance(), publication, "it's not getting out yet" );

        Assert.assertEquals( 0, countingListener.getAcceptedCount() );
        Assert.assertEquals( 0, countingListener.getAcceptedOnHoldCount() );

        lifecycleManager.getReadyForCheckingStatus().accept( publication, "good stuff" );

        Assert.assertEquals( 0, countingListener.getAcceptedCount() );
        Assert.assertEquals( 1, countingListener.getAcceptedOnHoldCount() );

        Assert.assertEquals( getStatus( CvPublicationStatusType.ACCEPTED_ON_HOLD ), publication.getStatus()  );
    }

    @Test
    public void fireRejected() throws Exception {

        final CountingLifecycleEventListener countingListener = new CountingLifecycleEventListener();
        lifecycleManager.registerListener( countingListener );

        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setStatus( getStatus( CvPublicationStatusType.READY_FOR_CHECKING ) );
        publication.getLifecycleEvents().clear();

        Assert.assertEquals( 0, countingListener.getRejectedCount() );

        lifecycleManager.getReadyForCheckingStatus().reject( publication, "can do better" );

        Assert.assertEquals( 1, countingListener.getRejectedCount() );
    }

    @Test
    public void fireReadyForRelease() throws Exception {

        final CountingLifecycleEventListener countingListener = new CountingLifecycleEventListener();
        lifecycleManager.registerListener( countingListener );

        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setStatus( getStatus( CvPublicationStatusType.ACCEPTED ) );
        publication.getLifecycleEvents().clear();

        Assert.assertEquals( 0, countingListener.getReadyForReleaseCount() );

        lifecycleManager.getAcceptedStatus().readyForRelease( publication, "off it goes" );

        Assert.assertEquals( 1, countingListener.getReadyForReleaseCount() );
    }

    @Test
    public void fireReleased() throws Exception {

        final CountingLifecycleEventListener countingListener = new CountingLifecycleEventListener();
        lifecycleManager.registerListener( countingListener );

        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setStatus( getStatus( CvPublicationStatusType.READY_FOR_RELEASE ) );
        publication.getLifecycleEvents().clear();

        Assert.assertEquals( 0, countingListener.getReleasedCount() );

        lifecycleManager.getReadyForReleaseStatus().release( publication, "off it goes" );

        Assert.assertEquals( 1, countingListener.getReleasedCount() );
    }

    @Test
    public void fireDiscarded() throws Exception {

        // check that from all status, one can discard a paper
        final Collection<GlobalStatus> allStatus = getSpringContext().getBeansOfType( GlobalStatus.class ).values();
        for ( GlobalStatus status : allStatus ) {
            lifecycleManager.removeAllListeners();

            final CountingLifecycleEventListener countingListener = new CountingLifecycleEventListener();
            lifecycleManager.registerListener( countingListener );

            Publication publication = getMockBuilder().createPublicationRandom();
            final CvPublicationStatusType statusType = status.getCvPublicationStatusType();
            if( statusType == null ){
                publication.setStatus( null );
            } else {
                publication.setStatus( getStatus( statusType ) );
            }
            publication.getLifecycleEvents().clear();

            Assert.assertEquals( 0, countingListener.getDiscardedCount() );

            status.discard( publication, "discarded" );

            Assert.assertEquals( "fireDiscarded() wasn't called when discarding from status " +
                                 (statusType == null ? null : statusType.shortLabel() ),
                                 1, countingListener.getDiscardedCount() );
        }
    }

    private CvPublicationStatus getStatus( CvPublicationStatusType status ) {
        return getDaoFactory().getCvObjectDao( CvPublicationStatus.class ).getByIdentifier( status.identifier() );
    }
}
