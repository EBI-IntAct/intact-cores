package uk.ac.ebi.intact.core.lifecycle.correctionassigner;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.intact.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Publication;
import uk.ac.ebi.intact.model.user.User;
import uk.ac.ebi.intact.model.util.UserUtils;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class CorrectionAssignerTest extends IntactBasicTestCase {

    @Autowired
    private CorrectionAssigner correctionAssigner;

    @Test
    public void assignReviewer_excludeOneSelf() throws Exception {
        User reviewer1 = getMockBuilder().createReviewer("lalaReviewer", "Lala", "Smith", "lala@example.com");
        User reviewer2 = getMockBuilder().createReviewer("tataReviewer", "Tata", "Toto", "tata@example.com");
        getCorePersister().saveOrUpdate(reviewer1, reviewer2);

        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setCurrentOwner(reviewer1);

        correctionAssigner.assignReviewer(publication);

        Assert.assertEquals(reviewer2, publication.getCurrentReviewer());
    }

    @Test
    public void assignReviewer_withAvailabilities() throws Exception {
        User reviewer1 = getMockBuilder().createReviewer("lalaReviewer", "Lala", "Smith", "lala@example.com");
        User reviewer2 = getMockBuilder().createReviewer("tataReviewer", "Tata", "Toto", "tata@example.com");

        UserUtils.setReviewerAvailability(reviewer1, 100);
        UserUtils.setReviewerAvailability(reviewer2, 0);

        User curator1 = getMockBuilder().createCurator("loloCurator", "Lolo", "Jones", "lolo@example.com");
        User curator2 = getMockBuilder().createCurator("totoCurator", "Toto", "Titi", "toto@example.com");

        getCorePersister().saveOrUpdate(reviewer1, reviewer2, curator1, curator2);

        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setCurrentOwner(curator1);

        correctionAssigner.assignReviewer(publication);

        Assert.assertEquals(reviewer1, publication.getCurrentReviewer());
    }

    @Test
    public void assignReviewer_withMentor() throws Exception {
        User reviewer1 = getMockBuilder().createReviewer("lalaReviewer", "Lala", "Smith", "lala@example.com");
        User reviewer2 = getMockBuilder().createReviewer("tataReviewer", "Tata", "Toto", "tata@example.com");

        UserUtils.setReviewerAvailability(reviewer1, 50);
        UserUtils.setReviewerAvailability(reviewer2, 50);

        User curator1 = getMockBuilder().createCurator("loloCurator", "Lolo", "Jones", "lolo@example.com");
        User curator2 = getMockBuilder().createCurator("totoCurator", "Toto", "Titi", "toto@example.com");

        getCorePersister().saveOrUpdate(reviewer1, reviewer2, curator1, curator2);

        // mentor is reviewer2 for curator1
        UserUtils.setMentorReviewer(curator1, reviewer2);

        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setCurrentOwner(curator1);

        correctionAssigner.assignReviewer(publication);

        Assert.assertNotNull(publication.getCurrentReviewer());
        Assert.assertEquals(reviewer2, publication.getCurrentReviewer());
    }

    @Test (expected = IllegalStateException.class)
    public void assignReviewer_noReviewers() throws Exception {
        User reviewer1 = getMockBuilder().createReviewer("lalaReviewer", "Lala", "Smith", "lala@example.com");
        User reviewer2 = getMockBuilder().createReviewer("tataReviewer", "Tata", "Toto", "tata@example.com");

        UserUtils.setReviewerAvailability(reviewer1, 0);
        UserUtils.setReviewerAvailability(reviewer2, 0);

        User curator1 = getMockBuilder().createCurator("loloCurator", "Lolo", "Jones", "lolo@example.com");
        User curator2 = getMockBuilder().createCurator("totoCurator", "Toto", "Titi", "toto@example.com");

        getCorePersister().saveOrUpdate(reviewer1, reviewer2, curator1, curator2);

        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setCurrentOwner(curator1);

        correctionAssigner.assignReviewer(publication);
    }
}
