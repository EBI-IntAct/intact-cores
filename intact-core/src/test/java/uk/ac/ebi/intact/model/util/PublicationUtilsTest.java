package uk.ac.ebi.intact.model.util;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.lifecycle.LifecycleManager;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.user.User;

import java.util.Collections;
import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PublicationUtilsTest extends IntactBasicTestCase {

    @Test
    public void testIsAccepted_yes() throws Exception {
         LifecycleManager lifecycleManager = (LifecycleManager) getSpringContext().getBean("lifecycleManager");

        User reviewer = getMockBuilder().createUserSandra();
        getCorePersister().saveOrUpdate(reviewer);

        Publication pub = getMockBuilder().createPublicationRandom();
        Assert.assertFalse(PublicationUtils.isAccepted(pub));

        lifecycleManager.getNewStatus().claimOwnership(pub);
        Assert.assertFalse(PublicationUtils.isAccepted(pub));
        lifecycleManager.getAssignedStatus().startCuration(pub);
        Assert.assertFalse(PublicationUtils.isAccepted(pub));
        lifecycleManager.getCurationInProgressStatus().readyForChecking(pub, "Ready", true);
        Assert.assertFalse(PublicationUtils.isAccepted(pub));
        lifecycleManager.getReadyForCheckingStatus().reject(pub, "Rejected!");
        Assert.assertFalse(PublicationUtils.isAccepted(pub));
        lifecycleManager.getCurationInProgressStatus().readyForChecking(pub, "Ready again", true);
        Assert.assertFalse(PublicationUtils.isAccepted(pub));
        lifecycleManager.getReadyForCheckingStatus().accept(pub, "Now accepted!");
        Assert.assertTrue(PublicationUtils.isAccepted(pub));
        lifecycleManager.getAcceptedStatus().readyForRelease(pub, "Ready...");
        Assert.assertTrue(PublicationUtils.isAccepted(pub));
        lifecycleManager.getReadyForReleaseStatus().release(pub, "12345");
        Assert.assertTrue(PublicationUtils.isAccepted(pub));
    }

    @Test
    public void testIsAccepted_no1() throws Exception {
        Experiment exp1 = getMockBuilder().createExperimentEmpty();
        exp1.addAnnotation(getMockBuilder().createAnnotation("Accepted 2010-FEB-30 by Lala", null, CvTopic.ACCEPTED));
        exp1.setPublication(null);

        Experiment exp2 = getMockBuilder().createExperimentEmpty();
        exp2.setPublication(null);

        Publication pub = getMockBuilder().createPublication("1234567");
        pub.addExperiment(exp1);
        pub.addExperiment(exp2);

        Assert.assertEquals(false, PublicationUtils.isAccepted(pub));
    }
    
    @Test
    public void testIsAccepted_no2() throws Exception {
        Experiment exp1 = getMockBuilder().createExperimentEmpty();
        exp1.setPublication(null);

        Experiment exp2 = getMockBuilder().createExperimentEmpty();
        exp2.setPublication(null);

        Publication pub = getMockBuilder().createPublication("1234567");
        pub.addExperiment(exp1);
        pub.addExperiment(exp2);

        Assert.assertEquals(false, PublicationUtils.isAccepted(pub));
    }

    @Test
    public void testIsToBeReviewed_yes1() throws Exception {
        Experiment exp1 = getMockBuilder().createExperimentEmpty();
        exp1.addAnnotation(getMockBuilder().createAnnotation("Needs revision", null, CvTopic.TO_BE_REVIEWED));
        exp1.setPublication(null);

        Experiment exp2 = getMockBuilder().createExperimentEmpty();
        exp2.addAnnotation(getMockBuilder().createAnnotation("Needs revision", null, CvTopic.TO_BE_REVIEWED));
        exp2.setPublication(null);

        Publication pub = getMockBuilder().createPublication("1234567");
        pub.addExperiment(exp1);
        pub.addExperiment(exp2);

        Assert.assertEquals(true, PublicationUtils.isToBeReviewed(pub));
    }

@Test
    public void testIsToBeReviewed_yes2() throws Exception {
        Experiment exp1 = getMockBuilder().createExperimentEmpty();
        exp1.addAnnotation(getMockBuilder().createAnnotation("Needs revision", null, CvTopic.TO_BE_REVIEWED));
        exp1.setPublication(null);

        Experiment exp2 = getMockBuilder().createExperimentEmpty();
        exp2.setPublication(null);

        Publication pub = getMockBuilder().createPublication("1234567");
        pub.addExperiment(exp1);
        pub.addExperiment(exp2);

        Assert.assertEquals(true, PublicationUtils.isToBeReviewed(pub));
    }

    
    @Test
    public void testIsToBeReviewed_no() throws Exception {
        Experiment exp1 = getMockBuilder().createExperimentEmpty();
        exp1.setPublication(null);

        Experiment exp2 = getMockBuilder().createExperimentEmpty();
        exp2.setPublication(null);

        Publication pub = getMockBuilder().createPublication("1234567");
        pub.addExperiment(exp1);
        pub.addExperiment(exp2);

        Assert.assertEquals(false, PublicationUtils.isToBeReviewed(pub));
    }


    @Test
    public void testIsOnHold_yes1() throws Exception {
        Experiment exp1 = getMockBuilder().createExperimentEmpty();
        exp1.addAnnotation(getMockBuilder().createAnnotation("Texas Holdin'", null, CvTopic.ON_HOLD));
        exp1.setPublication(null);

        Experiment exp2 = getMockBuilder().createExperimentEmpty();
        exp2.addAnnotation(getMockBuilder().createAnnotation("Texas Holdin'", null, CvTopic.ON_HOLD));
        exp2.setPublication(null);

        Publication pub = getMockBuilder().createPublication("1234567");
        pub.addExperiment(exp1);
        pub.addExperiment(exp2);

        Assert.assertEquals(true, PublicationUtils.isOnHold(pub));
    }

    @Test
    public void testIsOnHold_yes2() throws Exception {
        Experiment exp1 = getMockBuilder().createExperimentEmpty();
        exp1.addAnnotation(getMockBuilder().createAnnotation("Texas Holdin'", null, CvTopic.ON_HOLD));
        exp1.setPublication(null);

        Experiment exp2 = getMockBuilder().createExperimentEmpty();
        exp2.setPublication(null);

        Publication pub = getMockBuilder().createPublication("1234567");
        pub.addExperiment(exp1);
        pub.addExperiment(exp2);

        Assert.assertEquals(true, PublicationUtils.isOnHold(pub));
    }

    @Test
    public void testIsOnHold_no() throws Exception {
        Experiment exp1 = getMockBuilder().createExperimentEmpty();
        exp1.setPublication(null);

        Experiment exp2 = getMockBuilder().createExperimentEmpty();
        exp2.setPublication(null);

        Publication pub = getMockBuilder().createPublication("1234567");
        pub.addExperiment(exp1);
        pub.addExperiment(exp2);

        Assert.assertEquals(false, PublicationUtils.isOnHold(pub));
    }

    @Test
    public void testNextUnassignedId() throws Exception {
        Assert.assertEquals("unassigned1", PublicationUtils.nextUnassignedId(IntactContext.getCurrentInstance()));
    }

    @Test
    public void testMarkAsOnHold() throws Exception {
        Publication pub = getMockBuilder().createPublicationRandom();
        PublicationUtils.markAsOnHold(getIntactContext(), pub, "on hold because of test");

        Assert.assertTrue(PublicationUtils.isOnHold(pub));
    }

    @Test
    public void isRejected_yes() throws Exception {
        LifecycleManager lifecycleManager = (LifecycleManager) getSpringContext().getBean("lifecycleManager");

        User reviewer = getMockBuilder().createUserSandra();
        getCorePersister().saveOrUpdate(reviewer);

        Publication pub = getMockBuilder().createPublicationRandom();

        lifecycleManager.getNewStatus().claimOwnership(pub);
        lifecycleManager.getAssignedStatus().startCuration(pub);
        lifecycleManager.getCurationInProgressStatus().readyForChecking(pub, "Ready", true);
        lifecycleManager.getReadyForCheckingStatus().reject(pub, "Rejected!");

        Assert.assertTrue(PublicationUtils.isRejected(pub));
    }
    @Test
    public void isRejected_no() throws Exception {
        Publication pub = getMockBuilder().createPublicationRandom();

        Assert.assertFalse(PublicationUtils.isRejected(pub));
    }

    @Test
    public void isRejected_no2() throws Exception {
         LifecycleManager lifecycleManager = (LifecycleManager) getSpringContext().getBean("lifecycleManager");

        User reviewer = getMockBuilder().createUserSandra();
        getCorePersister().saveOrUpdate(reviewer);

        Publication pub = getMockBuilder().createPublicationRandom();
        Assert.assertFalse(PublicationUtils.isRejected(pub));

        lifecycleManager.getNewStatus().claimOwnership(pub);
        Assert.assertFalse(PublicationUtils.isRejected(pub));
        lifecycleManager.getAssignedStatus().startCuration(pub);
        Assert.assertFalse(PublicationUtils.isRejected(pub));
        lifecycleManager.getCurationInProgressStatus().readyForChecking(pub, "Ready", true);
        Assert.assertFalse(PublicationUtils.isRejected(pub));
        lifecycleManager.getReadyForCheckingStatus().reject(pub, "Rejected!");
        Assert.assertTrue(PublicationUtils.isRejected(pub));
        lifecycleManager.getCurationInProgressStatus().readyForChecking(pub, "Ready again", true);
        Assert.assertFalse(PublicationUtils.isRejected(pub));
        lifecycleManager.getReadyForCheckingStatus().accept(pub, "Now accepted!");
        Assert.assertFalse(PublicationUtils.isRejected(pub));
    }

    @Test
    public void getLastEventOfType1() throws Exception {
        LifecycleManager lifecycleManager = (LifecycleManager) getSpringContext().getBean("lifecycleManager");

        User reviewer = getMockBuilder().createUserSandra();
        getCorePersister().saveOrUpdate(reviewer);

        Publication pub = getMockBuilder().createPublicationRandom();
        Assert.assertFalse(PublicationUtils.isRejected(pub));

        lifecycleManager.getNewStatus().claimOwnership(pub);
        lifecycleManager.getAssignedStatus().startCuration(pub);
        lifecycleManager.getCurationInProgressStatus().readyForChecking(pub, "Ready", true);
        lifecycleManager.getReadyForCheckingStatus().reject(pub, "Rejected!");
        lifecycleManager.getCurationInProgressStatus().readyForChecking(pub, "Ready 2", true);
        lifecycleManager.getReadyForCheckingStatus().reject(pub, "Rejected 2");
        lifecycleManager.getCurationInProgressStatus().readyForChecking(pub, "Ready 3", true);

        List<LifecycleEvent> shuffled = pub.getLifecycleEvents();
        Collections.shuffle(shuffled);
        pub.setLifecycleEvents(shuffled);

        Assert.assertEquals("Rejected 2", PublicationUtils.getLastEventOfType(pub, CvLifecycleEventType.REJECTED.identifier()).getNote());
        Assert.assertEquals("Ready 3", PublicationUtils.getLastEventOfType(pub, CvLifecycleEventType.READY_FOR_CHECKING.identifier()).getNote());
    }
}
