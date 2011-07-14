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
public class AcceptedStatus extends GlobalStatus {

    /**
     * The publication is ready to go through the release project.
     *
     * @param publication the publication
     * @param comment optional comment
     */
    @LifecycleTransition(fromStatus = CvPublicationStatusType.ACCEPTED, toStatus = CvPublicationStatusType.READY_FOR_RELEASE)
    public void readyForRelease(Publication publication, String comment) {
        changeStatus(publication, CvPublicationStatusType.READY_FOR_RELEASE, CvLifecycleEventType.READY_FOR_RELEASE, comment);
    }

}
