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

import org.springframework.stereotype.Component;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.context.UserContext;
import uk.ac.ebi.intact.jami.lifecycle.IllegalTransitionException;
import uk.ac.ebi.intact.jami.lifecycle.LifecycleEventListener;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEventType;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleStatus;
import uk.ac.ebi.intact.jami.model.lifecycle.Releasable;
import uk.ac.ebi.intact.jami.model.user.User;

/**
 *
 */
@Component
public class NewStatus extends GlobalStatus {

    public NewStatus() {
        setLifecycleStatus(LifeCycleStatus.NEW);
    }

    /**
     * Mark as planned to be curated without specifying who.
     *
     * @param releasable the releasable
     * @param reason an optional reason
     */
    public void reserve(Releasable releasable, String reason) {
        if (canChangeStatus(releasable)){
            throw new IllegalTransitionException("Transition new to reserved cannot be applied to object '"+ releasable.toString()+
                    "' with state: '"+releasable.getStatus()+"'");
        }
        changeStatus(releasable, LifeCycleStatus.RESERVED, LifeCycleEventType.RESERVED, reason);

        // Notify listeners
        for ( LifecycleEventListener listener : getListeners() ) {
            listener.fireReserved( releasable );
        }
    }

    /**
     * This curator decides to curate the publication.
     *
     * @param releasable the releasable
     */
    public void claimOwnership(Releasable releasable) {
        if (canChangeStatus(releasable)){
            throw new IllegalTransitionException("Transition new to assigned cannot be applied to object '"+ releasable.toString()+
                    "' with state: '"+releasable.getStatus()+"'");
        }
        User currentUser = null;
        UserContext userContext = ApplicationContextProvider.getBean("userContext");
        if (userContext != null && userContext.getUserId() != null) {
            currentUser = userContext.getUser();
        }
        User oldOwner = releasable.getCurrentOwner();
        releasable.setCurrentOwner(currentUser);

        changeStatus(releasable, LifeCycleStatus.ASSIGNED, LifeCycleEventType.SELF_ASSIGNED, "Claimed ownership");

        // Notify listeners
        for ( LifecycleEventListener listener : getListeners() ) {
            listener.fireOwnerChanged( releasable, oldOwner, currentUser );
            listener.fireAssigned( releasable, null, currentUser );
        }
    }


    /**
     * A publication is assigned to another curator, who will be the owner.
     *
     * @param releasable the releasable
     * @param curator the curator to be assigned
     */
    public void assignToCurator(Releasable releasable, User curator ) {
        if (canChangeStatus(releasable)){
            throw new IllegalTransitionException("Transition new to assigned cannot be applied to object '"+ releasable.toString()+
                    "' with state: '"+releasable.getStatus()+"'");
        }
        User currentUser = null;
        UserContext userContext = ApplicationContextProvider.getBean("userContext");
        if (userContext != null && userContext.getUserId() != null) {
            currentUser = userContext.getUser();
        }
        final User previousOwner = releasable.getCurrentOwner();
        releasable.setCurrentOwner( curator );

        changeStatus(releasable, LifeCycleStatus.ASSIGNED, LifeCycleEventType.ASSIGNED, "Assigned to: "+ curator.getLogin() + " by " + currentUser.getLogin() );

        // Notify listeners
        for ( LifecycleEventListener listener : getListeners() ) {
            listener.fireOwnerChanged( releasable, previousOwner, curator );
            listener.fireAssigned( releasable, currentUser, curator );
        }
    }
}
