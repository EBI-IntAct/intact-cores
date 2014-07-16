package uk.ac.ebi.intact.jami.lifecycle.correctionassigner;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.context.IntactContext;
import uk.ac.ebi.intact.jami.dao.UserDao;
import uk.ac.ebi.intact.jami.model.lifecycle.Releasable;
import uk.ac.ebi.intact.jami.model.user.Role;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.utils.UserUtils;

import java.util.Collection;
import java.util.Collections;

/**
 * Assigns a reviewer to a publication
 *
 */
@Component(value = "jamiCorrectionAssigner")
public class CorrectionAssigner {

    public CorrectionAssigner() {

    }

    @Transactional(value = "jamiTransactionManager")
    public User assignReviewer(Releasable releasable) {
        final User owner = releasable.getCurrentOwner();
        if (owner == null) {
            throw new IllegalArgumentException("Cannot assign a reviewer to a publication/complex without owner: "+ releasable.toString());
        }

        UserDao currentDao = null;
        IntactContext intactContext = ApplicationContextProvider.getBean("intactJamiContext", IntactContext.class);
        if (intactContext != null && intactContext.getIntactDao() != null) {
            currentDao = intactContext.getIntactDao().getUserDao();
        }

        // check if the curator has a "mentor" reviewer, the one that is supposed to check her entries while learning
        User reviewer = UserUtils.getMentorReviewer(currentDao, owner);

        if (reviewer == null) {
            SelectionRandomizer<User> selectionRandomizer = createSelectionRandomizer();

            reviewer = selectionRandomizer.randomSelection(owner);

            if (reviewer == null) {
                throw new IllegalStateException("No reviewers could be found");
            }
        }

        releasable.setCurrentReviewer(reviewer);

        return reviewer;
    }

    private SelectionRandomizer<User> createSelectionRandomizer() {
        SelectionRandomizer<User> selectionRandomizer = new SelectionRandomizer<User>();
        UserDao currentDao = null;
        IntactContext intactContext = ApplicationContextProvider.getBean("intactJamiContext", IntactContext.class);
        if (intactContext != null && intactContext.getIntactDao() != null) {
            currentDao = intactContext.getIntactDao().getUserDao();
        }

        Collection<User> reviewers = currentDao != null ? currentDao.getByRole(Role.ROLE_REVIEWER) : Collections.EMPTY_LIST;

        for (User reviewer : reviewers) {
            Integer availability = UserUtils.getReviewerAvailability(reviewer);
            selectionRandomizer.addObject(reviewer, availability);
        }

        return selectionRandomizer;
    }

}
