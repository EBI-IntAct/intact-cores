package uk.ac.ebi.intact.core.lifecycle.correctionassigner;

import org.springframework.stereotype.Controller;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.util.DebugUtil;
import uk.ac.ebi.intact.model.Publication;
import uk.ac.ebi.intact.model.user.Role;
import uk.ac.ebi.intact.model.user.User;
import uk.ac.ebi.intact.model.util.UserUtils;

import java.util.List;

/**
 * Assigns a reviewer to a publication
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Controller
public class CorrectionAssigner {

    public CorrectionAssigner() {

    }

    public User assignReviewer(Publication publication) {
        final User owner = publication.getCurrentOwner();
        if (owner == null) {
            throw new IllegalArgumentException("Cannot assign a reviewer to a publication without owner: "+ DebugUtil.annotatedObjectToString(publication, false));
        }

        // check if the curator has a "mentor" reviewer, the one that is supposed to check her entries while learning
        User reviewer = UserUtils.getMentorReviewer(IntactContext.getCurrentInstance(), owner);

        if (reviewer == null) {
            SelectionRandomizer<User> selectionRandomizer = createSelectionRandomizer();

            reviewer = selectionRandomizer.randomSelection(owner);

            if (reviewer == null) {
                throw new IllegalStateException("No reviewers could be found");
            }
        }

        publication.setCurrentReviewer(reviewer);

        return reviewer;
    }

    private SelectionRandomizer<User> createSelectionRandomizer() {
        SelectionRandomizer<User> selectionRandomizer = new SelectionRandomizer<User>();

        List<User> reviewers = IntactContext.getCurrentInstance().getDaoFactory().getUserDao().getByRole(Role.ROLE_REVIEWER);

        for (User reviewer : reviewers) {
            Integer availability = UserUtils.getReviewerAvailability(reviewer);
            selectionRandomizer.addObject(reviewer, availability);
        }

        return selectionRandomizer;
    }

}
