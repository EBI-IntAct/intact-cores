/**
 * Copyright 2011 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.core.lifecycle.status;

import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.lifecycle.LifecycleTransition;
import uk.ac.ebi.intact.model.*;

/**
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class GlobalStatus {

    /**
     * A publication cannot be processed for any reason.
     *
     * @param publication the publication
     * @param reason a mandatory reason
     */
    @LifecycleTransition(toStatus = CvPublicationStatusType.DISCARDED)
    public void discard(Publication publication, String reason) {
        enfoceMandatory(reason);
        changeStatus(publication, CvPublicationStatusType.DISCARDED, CvLifecycleEventType.DISCARDED, reason);
    }

    /**
     * This curator decides to curate the publication.
     *
     * @param publication the publication to use
     * @param reason a mandatory reason
     */
    @LifecycleTransition(statusChange = false)
    public void changeOwnership(Publication publication, String reason) {
        enfoceMandatory(reason);
        addLifecycleEvent(publication, CvLifecycleEventType.OWNER_CHANGED, reason);
    }


    protected void enfoceMandatory(String var) {
        if (var == null || var.isEmpty()) {
            throw new IllegalArgumentException("A comment for the lifecycle event is mandatory");
        }
    }

    protected void addLifecycleEvent(Publication publication, CvLifecycleEventType cvLifecycleEventType, String comment) {
        addLifecycleEvent(publication, cvLifecycleEventType.identifier(), comment);
    }

    protected void addLifecycleEvent(Publication publication, String cvLifecycleEventIdentifier, String comment) {
        IntactContext intactContext = IntactContext.getCurrentInstance();

        CvLifecycleEvent lifecycleEvent = intactContext.getDaoFactory()
                .getCvObjectDao(CvLifecycleEvent.class).getByIdentifier(cvLifecycleEventIdentifier);
        publication.addLifecycleEvent(new LifecycleEvent(lifecycleEvent, intactContext.getUserContext().getUser(), comment));
    }

    protected void changeStatus(Publication publication, CvPublicationStatusType cvPublicationStatusType) {
        changeStatus(publication, cvPublicationStatusType.identifier());
    }

    protected void changeStatus(Publication publication, String cvPublicationStatusIdentifier) {
        IntactContext intactContext = IntactContext.getCurrentInstance();

        CvPublicationStatus publicationStatus = intactContext.getDaoFactory()
                .getCvObjectDao(CvPublicationStatus.class).getByIdentifier(cvPublicationStatusIdentifier);

        publication.setStatus(publicationStatus);
    }

    protected void changeStatus(Publication publication, CvPublicationStatusType cvPublicationStatusType, CvLifecycleEventType cvLifecycleEventType, String comment) {
        addLifecycleEvent(publication, cvLifecycleEventType, comment);
        changeStatus(publication, cvPublicationStatusType);
    }
}
