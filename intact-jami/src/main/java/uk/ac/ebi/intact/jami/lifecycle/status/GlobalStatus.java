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
package uk.ac.ebi.intact.jami.lifecycle.status;


import psidev.psi.mi.jami.model.Publication;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.context.UserContext;
import uk.ac.ebi.intact.jami.lifecycle.LifecycleEventListener;
import uk.ac.ebi.intact.jami.model.lifecycle.*;
import uk.ac.ebi.intact.jami.model.user.User;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The state of a publication in the publication lifecycle (State machine).
 *
 */
public class GlobalStatus {

    private Collection<LifecycleEventListener> listeners=new ArrayList<LifecycleEventListener>();

    private LifeCycleStatus lifecycleStatus;

    /**
     * Returns the lifecycle status corresponding
     * @return
     */
    public LifeCycleStatus getLifecycleStatus() {
        return lifecycleStatus;
    }

    public void setLifecycleStatus (LifeCycleStatus statusType ) {
        this.lifecycleStatus = statusType;
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
     * An object cannot be processed for any reason.
     *
     * @param releasable the object that is managed by intact release lifecycle
     * @param reason a mandatory reason
     */
    public void discard(Releasable releasable, String reason) {
        enfoceMandatory(reason);
        changeStatus(releasable, LifeCycleStatus.DISCARDED, LifeCycleEventType.DISCARDED, reason);

        // Notify listeners
        for ( LifecycleEventListener listener : getListeners() ) {
            listener.fireDiscarded( releasable );
        }
    }

    /**
     * This curator decides to curate the intact obejct.
     *
     * @param releasable the object that is managed by intact release lifecycle
     * @param reason an optional comment
     */
    public void changeOwnership(Releasable releasable, User newOwner, String reason) {
        User currentUser = null;
        UserContext userContext = ApplicationContextProvider.getBean("jamiUserContext", UserContext.class);
        if (userContext != null && userContext.getUserId() != null) {
            currentUser = userContext.getUser();
        }

        User previousOwner = releasable.getCurrentOwner();
        releasable.setCurrentOwner( newOwner );

        addLifecycleEvent(releasable, LifeCycleEventType.OWNER_CHANGED,  "New owner: "+newOwner.getLogin()+" - "+reason, currentUser);

        // Notify listeners
        for ( LifecycleEventListener listener : getListeners() ) {
            listener.fireOwnerChanged( releasable, previousOwner, newOwner );
        }
    }

    /**
     * This reviewer decides to review the intact object.
     *
     * @param releasable the releasable to use
     * @param reason an optional comment
     */
    public void changeReviewer(Releasable releasable, User newReviewer, String reason, User currentUser) {
        addLifecycleEvent(releasable, LifeCycleEventType.REVIEWER_CHANGED, reason, currentUser);

        User previousReviewer = releasable.getCurrentReviewer();
        releasable.setCurrentReviewer(newReviewer);

        // Notify listeners
        for ( LifecycleEventListener listener : getListeners() ) {
            listener.fireReviewerChanged( releasable, previousReviewer, newReviewer );
        }
    }


    protected void enfoceMandatory(String var) {
        if (var == null || var.trim().isEmpty()) {
            throw new IllegalArgumentException("A comment for the lifecycle event is mandatory");
        }
    }

    protected void addLifecycleEvent(Releasable releasable, LifeCycleEventType cvLifecycleEventIdentifier, String comment, User currentUser) {
        LifeCycleEvent evt=null;
        if (releasable instanceof Publication){
           evt = new PublicationLifeCycleEvent(cvLifecycleEventIdentifier, currentUser, comment);
        }
        else {
            evt = new ComplexLifeCycleEvent(cvLifecycleEventIdentifier, currentUser, comment);
        }
        releasable.getLifecycleEvents().add(evt);
    }

    protected void changeStatus(Releasable releasable, LifeCycleStatus cvPublicationStatusIdentifier) {
        releasable.setStatus(cvPublicationStatusIdentifier);
    }

    protected void changeStatus(Releasable releasable, LifeCycleStatus cvPublicationStatusType, LifeCycleEventType cvLifecycleEventType, String comment) {
        User currentUser = null;
        UserContext userContext = ApplicationContextProvider.getBean("jamiUserContext", UserContext.class);
        if (userContext != null && userContext.getUserId() != null) {
            currentUser = userContext.getUser();
        }

        addLifecycleEvent(releasable, cvLifecycleEventType, comment, currentUser);
        changeStatus(releasable, cvPublicationStatusType);
    }

    protected boolean canChangeStatus(Releasable releasable){
        if (lifecycleStatus != null){
            return releasable.getStatus().equals(this.lifecycleStatus);
        }
        return true;
    }
}
