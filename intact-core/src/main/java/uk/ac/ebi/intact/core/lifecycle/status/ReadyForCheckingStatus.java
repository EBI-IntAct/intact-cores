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
import uk.ac.ebi.intact.core.persistence.dao.CvObjectDao;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.model.CvLifecycleEventType;
import uk.ac.ebi.intact.model.CvPublicationStatus;
import uk.ac.ebi.intact.model.CvPublicationStatusType;
import uk.ac.ebi.intact.model.Publication;
import uk.ac.ebi.intact.model.util.PublicationUtils;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Controller
public class ReadyForCheckingStatus extends GlobalStatus {

    public ReadyForCheckingStatus() {
        setStatusType( CvPublicationStatusType.READY_FOR_CHECKING );
    }

    /**
     * The reviewer accepts the publication.
     *
     * @param publication the publication
     * @param comment optional comment
     */
    @LifecycleTransition(fromStatus = CvPublicationStatusType.READY_FOR_CHECKING,
                         toStatus = {CvPublicationStatusType.ACCEPTED, CvPublicationStatusType.ACCEPTED_ON_HOLD})
    public void accept(Publication publication, String comment) {
        if (PublicationUtils.isOnHold(publication)) {
            changeStatus(publication, CvPublicationStatusType.ACCEPTED_ON_HOLD, CvLifecycleEventType.ACCEPTED, comment);
            // Notify listeners
            for ( LifecycleEventListener listener : getListeners() ) {
                listener.fireAcceptedOnHold( publication );
            }
        } else {
            changeStatus(publication, CvPublicationStatusType.ACCEPTED, CvLifecycleEventType.ACCEPTED, comment);
            // Notify listeners
            for ( LifecycleEventListener listener : getListeners() ) {
                listener.fireAccepted( publication );
            }
        }
    }

    /**
     * The reviewer rejects the publication and corrections are required by the owner.
     *
     * @param publication the publication
     * @param comment mandatory comment
     */
    @LifecycleTransition(fromStatus = CvPublicationStatusType.READY_FOR_CHECKING,
                         toStatus = CvPublicationStatusType.CURATION_IN_PROGRESS)
    public void reject(Publication publication, String comment) {
        enfoceMandatory(comment);
        changeStatus(publication, CvPublicationStatusType.CURATION_IN_PROGRESS, CvLifecycleEventType.REJECTED, comment);

        // Notify listeners
        for ( LifecycleEventListener listener : getListeners() ) {
            listener.fireRejected( publication );
        }
    }
}
