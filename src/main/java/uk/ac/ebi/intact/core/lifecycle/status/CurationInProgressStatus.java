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

import org.springframework.stereotype.Controller;
import uk.ac.ebi.intact.core.lifecycle.LifecycleTransition;
import uk.ac.ebi.intact.model.CvLifecycleEventType;
import uk.ac.ebi.intact.model.CvPublicationStatusType;
import uk.ac.ebi.intact.model.Publication;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Controller
public class CurationInProgressStatus extends GlobalStatus {

    /**
     * The curator decides the publication can now be checked. Needs to know the outcome of the sanity check
     * in order to do the transition to the right status.
     *
     * @param publication the publication
     * @param message mechanism of creation of the publication
     */
    @LifecycleTransition(fromStatus = CvPublicationStatusType.CURATION_IN_PROGRESS, toStatus = CvPublicationStatusType.READY_FOR_CHECKING)
    public void readyForChecking(Publication publication, String message, boolean successfulSanityCheck) {
        // TODO instead of a boolean, it could be a SanityCheckReport kind of object

        if (successfulSanityCheck) {
            // TODO assign a reviewer
            changeStatus(publication, CvPublicationStatusType.READY_FOR_CHECKING, CvLifecycleEventType.READY_FOR_CHECKING, message);
        } else {
            addLifecycleEvent(publication, CvLifecycleEventType.SANITY_CHECK_FAILED, message); // TODO the message should be the ID of the sanity check
        }
    }

}
