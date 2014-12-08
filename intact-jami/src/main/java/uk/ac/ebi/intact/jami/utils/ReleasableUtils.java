package uk.ac.ebi.intact.jami.utils;

import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEventType;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleStatus;
import uk.ac.ebi.intact.jami.model.lifecycle.Releasable;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Utility methods for releasable
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/12/14</pre>
 */

public class ReleasableUtils {

    public static boolean isAccepted(Releasable publication) {
        if (publication == null) {
            throw new NullPointerException("You must give a non null releasable");
        }

        if (publication.getStatus() != null) {

            return LifeCycleStatus.ACCEPTED == publication.getStatus() ||
                    LifeCycleStatus.ACCEPTED_ON_HOLD == publication.getStatus() ||
                    LifeCycleStatus.READY_FOR_RELEASE == publication.getStatus() ||
                    LifeCycleStatus.RELEASED == publication.getStatus();

        }

        return publication.isAccepted();
    }

    /**
     * Gets the last lifecycle event with the provided CvLifecycleEvent identifier for a publication.
     * @param publication the releasable
     * @param cvLifecycleEventId the identifier of the CvLifecycleEvent cv
     * @return The last event with that identifier
     * @since 2.5.0
     */
    public static LifeCycleEvent getLastEventOfType(Releasable publication, LifeCycleEventType cvLifecycleEventId) {
        LifeCycleEvent lastEvent = null;

        List<LifeCycleEvent> sorted = publication.getLifecycleEvents();

        Collections.sort(sorted, new Comparator<LifeCycleEvent>() {
            @Override
            public int compare(LifeCycleEvent o1, LifeCycleEvent o2) {
                return o1.getWhen().compareTo(o2.getWhen());
            }
        });

        for (LifeCycleEvent event : publication.getLifecycleEvents()) {
            if (cvLifecycleEventId == event.getEvent()) {
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
    public static boolean isRejected(Releasable publication) {
        if (publication == null) {
            throw new NullPointerException("You must give a non null publication");
        }

        LifeCycleEvent lifecycleEvent = getLastEventOfType(publication, LifeCycleEventType.REJECTED);

        if (lifecycleEvent != null && LifeCycleStatus.CURATION_IN_PROGRESS == publication.getStatus()) {
            return true;
        }

        return false;
    }
}
