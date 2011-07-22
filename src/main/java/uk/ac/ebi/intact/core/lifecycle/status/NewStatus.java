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
import uk.ac.ebi.intact.core.lifecycle.LifecycleEventListener;
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

        // Notify listeners
        for ( LifecycleEventListener listener : getListeners() ) {
            listener.fireReserved( publication );
        }
    }

    /**
     * This curator decides to curate the publication.
     *
     * @param publication the publication
     */
    @LifecycleTransition(fromStatus = CvPublicationStatusType.NEW, toStatus = CvPublicationStatusType.ASSIGNED)
    public void claimOwnership(Publication publication) {
        IntactContext intactContext = IntactContext.getCurrentInstance();
        final User previousOwner = publication.getCurrentOwner();
        publication.setCurrentOwner(intactContext.getUserContext().getUser());

        changeStatus(publication, CvPublicationStatusType.ASSIGNED, CvLifecycleEventType.SELF_ASSIGNED, "Claimed ownership");

        // Notify listeners
        for ( LifecycleEventListener listener : getListeners() ) {
            listener.fireOwnerChanged( publication, previousOwner, intactContext.getUserContext().getUser() );
            listener.fireAssigned( publication, null, intactContext.getUserContext().getUser() );
        }
    }

    @Override
    public void changeOwnership(Publication publication, String reason) {
        claimOwnership(publication);
    }

    /**
     * A publication is assigned to another curator, who will be the owner.
     *
     * @param publication the publication
     * @param curator the curator to be assigned
     */
    @LifecycleTransition(fromStatus = CvPublicationStatusType.NEW, toStatus = CvPublicationStatusType.ASSIGNED)
    public void assignToCurator(Publication publication, User curator ) {
        final User previousOwner = publication.getCurrentOwner();
        publication.setCurrentOwner( curator );

        final User currentUser = IntactContext.getCurrentInstance().getUserContext().getUser();
        changeStatus(publication, CvPublicationStatusType.ASSIGNED, CvLifecycleEventType.ASSIGNED, "Assigned to: "+ curator.getLogin() + " by " + currentUser.getLogin() );

        // Notify listeners
        for ( LifecycleEventListener listener : getListeners() ) {
            listener.fireOwnerChanged( publication, previousOwner, curator );
            listener.fireAssigned( publication, currentUser, curator );
        }
    }
}
