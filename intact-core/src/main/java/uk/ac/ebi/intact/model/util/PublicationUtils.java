package uk.ac.ebi.intact.model.util;

import uk.ac.ebi.intact.core.config.SequenceManager;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.model.*;

import java.util.Date;

/**
 * Misc publication utilities.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public final class PublicationUtils {

    private PublicationUtils() {
    }

    public static boolean isAccepted(Publication publication) {
        if (publication == null) {
            throw new NullPointerException("You must give a non null publication");
        }

        for (Annotation a : publication.getAnnotations()) {
            if (a.getCvTopic() != null && CvTopic.ACCEPTED.equals(a.getCvTopic().getShortLabel())) {
                return true;
            }
        }

        return ExperimentUtils.areAllAccepted(publication.getExperiments());
    }

    public static boolean isToBeReviewed(Publication publication) {
        if (publication == null) {
            throw new NullPointerException("You must give a non null publication");
        }

        for (Annotation a : publication.getAnnotations()) {
            if (a.getCvTopic() != null && CvTopic.TO_BE_REVIEWED.equals(a.getCvTopic().getShortLabel())) {
                return true;
            }
        }

        for (Experiment experiment : publication.getExperiments()) {
            if (ExperimentUtils.isToBeReviewed(experiment)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isOnHold(Publication publication) {

        if (publication == null) {
            throw new NullPointerException("You must give a non null publication");
        }

        for (Annotation a : publication.getAnnotations()) {
            if (a.getCvTopic() != null && CvTopic.ON_HOLD.equals(a.getCvTopic().getShortLabel())) {
                return true;
            }
        }

        for (Experiment experiment : publication.getExperiments()) {
            if (ExperimentUtils.isOnHold(experiment)) {
                return true;
            }
        }

        return false;
    }

    public static String nextUnassignedId(IntactContext intactContext) {
        SequenceManager sequenceManager = (SequenceManager) intactContext.getSpringContext().getBean("sequenceManager");
        return "unassigned" + sequenceManager.getNextValueForSequence("unassigned_seq");
    }

    public static void markAsNew(IntactContext intactContext, Publication publication) {
        DaoFactory daoFactory = intactContext.getDaoFactory();

        CvPublicationStatus status = daoFactory.getCvObjectDao(CvPublicationStatus.class)
                .getByIdentifier(CvPublicationStatus.STATUS_NEW_MI);
        publication.setStatus(status);

        CvLifecycleEvent cvLifecycleEvent = daoFactory.getCvObjectDao(CvLifecycleEvent.class)
                .getByIdentifier(CvLifecycleEvent.EVENT_CREATED_MI);

        LifecycleEvent event = new LifecycleEvent(cvLifecycleEvent, intactContext.getUserContext().getUser(), new Date(), "Mock builder");

        publication.addLifecycleEvent(event);
    }
}
