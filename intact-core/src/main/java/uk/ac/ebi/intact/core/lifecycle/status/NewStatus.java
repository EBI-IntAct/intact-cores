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
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.lifecycle.LifecycleTransition;
import uk.ac.ebi.intact.model.CvLifecycleEventType;
import uk.ac.ebi.intact.model.CvPublicationStatusType;
import uk.ac.ebi.intact.model.Publication;
import uk.ac.ebi.intact.model.user.User;

/**
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Controller
public class NewStatus extends GlobalStatus {

    /**
     * Mark as planned to be curated without specifying who.
     *
     * @param publication the publication
     * @param reason an optional reason
     */
    @LifecycleTransition(fromStatus = CvPublicationStatusType.NEW, toStatus = CvPublicationStatusType.RESERVED)
    public void reserve(Publication publication, String reason) {
        changeStatus(publication, CvPublicationStatusType.RESERVED, CvLifecycleEventType.RESERVED, reason);
    }

    /**
     * This curator decides to curate the publication.
     *
     * @param publication the publication
     */
    @LifecycleTransition(fromStatus = CvPublicationStatusType.NEW, toStatus = CvPublicationStatusType.ASSIGNED)
    public void claimOwnership(Publication publication) {
        IntactContext intactContext = IntactContext.getCurrentInstance();
        publication.setCurrentOwner(intactContext.getUserContext().getUser());

        changeStatus(publication, CvPublicationStatusType.ASSIGNED, CvLifecycleEventType.SELF_ASSIGNED, "Claimed ownership");
    }

    /**
     * A publication is assigned to another curator, who will be the owner.
     *
     * @param publication the publication
     * @param user the curator to be assigned
     */
    @LifecycleTransition(fromStatus = CvPublicationStatusType.NEW, toStatus = CvPublicationStatusType.ASSIGNED)
    public void assignToCurator(Publication publication, User user) {
        publication.setCurrentOwner(user);

        changeStatus(publication, CvPublicationStatusType.ASSIGNED, CvLifecycleEventType.ASSIGNED, "Assigned to: "+user.getLogin());
    }







}
