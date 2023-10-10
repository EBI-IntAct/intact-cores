package uk.ac.ebi.intact.model.util;

import uk.ac.ebi.intact.core.config.SequenceCreationException;
import uk.ac.ebi.intact.core.config.SequenceManager;
import uk.ac.ebi.intact.core.config.hibernate.IntactHibernatePersistenceProvider;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persister.IntactCore;
import uk.ac.ebi.intact.model.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

        if (publication.getStatus() != null) {
            final String statusId = publication.getStatus().getIdentifier();

            return CvPublicationStatusType.ACCEPTED.identifier().equals(statusId) ||
                    CvPublicationStatusType.ACCEPTED_ON_HOLD.identifier().equals(statusId) ||
                    CvPublicationStatusType.READY_FOR_RELEASE.identifier().equals(statusId) ||
                    CvPublicationStatusType.RELEASED.identifier().equals(statusId);

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

    /**
     * Gets the last lifecycle event with the provided CvLifecycleEvent identifier for a publication.
     * @param publication the publication
     * @param cvLifecycleEventId the identifier of the CvLifecycleEvent cv
     * @return The last event with that identifier
     * @since 2.5.0
     */
    public static LifecycleEvent getLastEventOfType(Publication publication, String cvLifecycleEventId) {
        LifecycleEvent lastEvent = null;

        List<LifecycleEvent> sorted = IntactCore.ensureInitializedLifecycleEvents(publication);

        Collections.sort(sorted, new Comparator<LifecycleEvent>() {
            @Override
            public int compare(LifecycleEvent o1, LifecycleEvent o2) {
                return o1.getWhen().compareTo(o2.getWhen());
            }
        });

        for (LifecycleEvent event : IntactCore.ensureInitializedLifecycleEvents(publication)) {
            if (cvLifecycleEventId.equals(event.getEvent().getIdentifier())) {
                lastEvent = event;
            }
        }

        return lastEvent;
    }

    /**
     * Checks if a lifecycle event "rejected" exists and the publication status is "curation in progress"
     * @param publication  the publication to check
     * @return if the publication has been rejected
     * @since 2.5.0
     */
    public static boolean isRejected(Publication publication) {
        if (publication == null) {
            throw new NullPointerException("You must give a non null publication");
        }

        LifecycleEvent lifecycleEvent = getLastEventOfType(publication, CvLifecycleEventType.REJECTED.identifier());

        if (lifecycleEvent != null && CvPublicationStatusType.CURATION_IN_PROGRESS.identifier().equals(publication.getStatus().getIdentifier())) {
            return true;
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

    public static String nextUnassignedId(IntactContext intactContext) throws SequenceCreationException {
        SequenceManager sequenceManager = (SequenceManager) intactContext.getSpringContext().getBean("sequenceManager");
        sequenceManager.createSequenceIfNotExists(IntactHibernatePersistenceProvider.UNASSIGNED_SEQ, 1);
        return "unassigned" + sequenceManager.getNextValueForSequence(IntactHibernatePersistenceProvider.UNASSIGNED_SEQ);
    }

    /**
     * Adds the 'on-hold' to a publication.
     *
     * @param intactContext The IntactContext as accessing data is necessary
     * @param publication the publication to hold
     * @param reason the reason for the 'on-hold' status
     * @since 2.5.0
     */
    public static void markAsOnHold(IntactContext intactContext, Publication publication, String reason) {
        CvTopic onholdTopic = intactContext.getDaoFactory().getCvObjectDao(CvTopic.class).getByShortLabel(CvTopic.ON_HOLD);

        if (onholdTopic == null) throw new IllegalStateException("CvTopic on-hold was not found in the database");

        Annotation annotation = new Annotation(onholdTopic, reason);
        publication.addAnnotation(annotation);
    }

    public static PublicationXref getPubmedPrimaryReferenceXref(Publication publication) {
        for (PublicationXref xref : publication.getXrefs()) {
            boolean qualMi = false;
            boolean dbMi = false;

            if (xref.getCvXrefQualifier() != null) {
                if (xref.getCvXrefQualifier().getIdentifier() != null){
                   qualMi = CvXrefQualifier.PRIMARY_REFERENCE_MI_REF.equals(xref.getCvXrefQualifier().getIdentifier());
                }
                else {
                    qualMi = CvXrefQualifier.PRIMARY_REFERENCE.equalsIgnoreCase(xref.getCvXrefQualifier().getShortLabel());
                }
            }

            if (xref.getCvDatabase() != null) {
                if (xref.getCvDatabase().getIdentifier() != null){
                    dbMi = CvDatabase.PUBMED_MI_REF.equals(xref.getCvDatabase().getIdentifier());
                }
                else {
                    dbMi = CvDatabase.PUBMED.equalsIgnoreCase(xref.getCvDatabase().getShortLabel());
                }
            }


            if (qualMi && dbMi) {
                return xref;
            }
        }

        return null;
    }
}
