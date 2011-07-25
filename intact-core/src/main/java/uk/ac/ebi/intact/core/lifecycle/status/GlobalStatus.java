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

import com.google.common.collect.Lists;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.lifecycle.LifecycleEventListener;
import uk.ac.ebi.intact.core.lifecycle.LifecycleTransition;
import uk.ac.ebi.intact.core.persistence.dao.CvObjectDao;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.user.User;

import java.util.Collection;

/**
 * The state of a publication in the publication lifecycle (State machine).
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class GlobalStatus {

    private Collection<LifecycleEventListener> listeners = Lists.newArrayList();

    private CvPublicationStatusType statusType;

    /**
     * Returns the CvPublicationStatus corresponding
     * @return
     */
    public CvPublicationStatusType getCvPublicationStatusType() {
        return statusType;
    }

    public void setStatusType( CvPublicationStatusType statusType ) {
        this.statusType = statusType;
    }

    /////////////////////
    // Listeners

    public Collection<LifecycleEventListener> getListeners() {
        return listeners;
    }

    public void registerListener( LifecycleEventListener listener ) {
        if( ! listeners.contains( listener ) ) {
            listeners.add( listener );
        }
    }

    public void removeListener( LifecycleEventListener listener ) {
        listeners.remove( listener );
    }

    public void removeAllListeners() {
        listeners.clear();
    }

    ///////////////////////
    // State transitions

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

        // Notify listeners
        for ( LifecycleEventListener listener : getListeners() ) {
            listener.fireDiscarded( publication );
        }
    }

    /**
     * This curator decides to curate the publication.
     *
     * @param publication the publication to use
     * @param reason an optional comment
     */
    @LifecycleTransition(statusChange = false)
    public void changeOwnership(Publication publication, String reason) {
        if ( publication == null ) {
            throw new IllegalArgumentException( "You must give a non null publication" );
        }
        User newOwner = IntactContext.getCurrentInstance().getUserContext().getUser();
        User previousOwner = publication.getCurrentOwner();
        publication.setCurrentOwner( newOwner );

        addLifecycleEvent(publication, CvLifecycleEventType.OWNER_CHANGED, reason);

        // Notify listeners
        for ( LifecycleEventListener listener : getListeners() ) {
            listener.fireOwnerChanged( publication, previousOwner, newOwner );
        }
    }

    /**
     * This reviewer decides to review the publication.
     *
     * @param publication the publication to use
     * @param reason an optional comment
     */
    @LifecycleTransition(statusChange = false)
    public void changeReviewer(Publication publication, User newReviewer, String reason) {
        addLifecycleEvent(publication, CvLifecycleEventType.REVIEWER_CHANGED, reason);

        User previousReviewer = publication.getCurrentReviewer();
        publication.setCurrentReviewer( newReviewer );

        // Notify listeners
        for ( LifecycleEventListener listener : getListeners() ) {
            listener.fireReviewerChanged( publication, previousReviewer, newReviewer );
        }
    }


    protected void enfoceMandatory(String var) {
        if (var == null || var.trim().isEmpty()) {
            throw new IllegalArgumentException("A comment for the lifecycle event is mandatory");
        }
    }

    protected void addLifecycleEvent(Publication publication, CvLifecycleEventType cvLifecycleEventType, String comment) {
        addLifecycleEvent(publication, cvLifecycleEventType.identifier(), comment);
    }

    protected void addLifecycleEvent(Publication publication, String cvLifecycleEventIdentifier, String comment) {
        IntactContext intactContext = IntactContext.getCurrentInstance();
        final CvObjectDao<CvLifecycleEvent> cvObjectDao = intactContext.getDaoFactory().getCvObjectDao( CvLifecycleEvent.class );
        CvLifecycleEvent lifecycleEvent = cvObjectDao.getByIdentifier( cvLifecycleEventIdentifier );
        publication.addLifecycleEvent(new LifecycleEvent(lifecycleEvent, intactContext.getUserContext().getUser(), comment));
    }

    protected void changeStatus(Publication publication, CvPublicationStatusType cvPublicationStatusType) {
        changeStatus(publication, cvPublicationStatusType.identifier());
    }

    protected void changeStatus(Publication publication, String cvPublicationStatusIdentifier) {
        IntactContext intactContext = IntactContext.getCurrentInstance();
        final CvObjectDao<CvPublicationStatus> cvObjectDao = intactContext.getDaoFactory().getCvObjectDao( CvPublicationStatus.class );
        CvPublicationStatus publicationStatus = cvObjectDao.getByIdentifier( cvPublicationStatusIdentifier );
        publication.setStatus(publicationStatus);
    }

    protected void changeStatus(Publication publication, CvPublicationStatusType cvPublicationStatusType, CvLifecycleEventType cvLifecycleEventType, String comment) {
        addLifecycleEvent(publication, cvLifecycleEventType, comment);
        changeStatus(publication, cvPublicationStatusType);
    }
}
