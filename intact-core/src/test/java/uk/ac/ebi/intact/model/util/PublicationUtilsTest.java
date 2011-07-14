package uk.ac.ebi.intact.model.util;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Publication;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PublicationUtilsTest extends IntactBasicTestCase {

    @Test
    public void testIsAccepted_yes() throws Exception {
        Experiment exp1 = getMockBuilder().createExperimentEmpty();
        exp1.addAnnotation(getMockBuilder().createAnnotation("Accepted 2010-FEB-30 by Lala", null, CvTopic.ACCEPTED));
        exp1.setPublication(null);

        Experiment exp2 = getMockBuilder().createExperimentEmpty();
        exp2.addAnnotation(getMockBuilder().createAnnotation("Accepted 2010-FEB-30 by Lala", null, CvTopic.ACCEPTED));
        exp2.setPublication(null);

        Publication pub = getMockBuilder().createPublication("1234567");
        pub.addExperiment(exp1);
        pub.addExperiment(exp2);

        Assert.assertEquals(true, PublicationUtils.isAccepted(pub));
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
        PublicationUtils.markAsOnHold(pub, "on hold because of test");

        Assert.assertTrue(PublicationUtils.isOnHold(pub));
    }
}
