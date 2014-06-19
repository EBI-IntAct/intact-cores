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
import uk.ac.ebi.intact.jami.lifecycle.IllegalTransitionException;
import uk.ac.ebi.intact.jami.lifecycle.LifecycleEventListener;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEventType;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleStatus;
import uk.ac.ebi.intact.jami.model.lifecycle.Releasable;

/**
 */
@Component
public class ReadyForCheckingStatus extends GlobalStatus {

    public ReadyForCheckingStatus() {
        setLifecycleStatus(LifeCycleStatus.READY_FOR_CHECKING);
    }

    /**
     * The reviewer accepts the publication.
     *
     * @param releasable the releasable
     * @param comment optional comment
     */
    public void accept(Releasable releasable, String comment) {
        if (canChangeStatus(releasable)){
            throw new IllegalTransitionException("Transition ready for checking to accepted or accepted_on_hold cannot be applied to object '"+ releasable.toString()+
                    "' with state: '"+releasable.getStatus()+"'");
        }
        if (releasable.isOnHold()) {
            changeStatus(releasable, LifeCycleStatus.ACCEPTED_ON_HOLD, LifeCycleEventType.ACCEPTED, comment);
            // Notify listeners
            for ( LifecycleEventListener listener : getListeners() ) {
                listener.fireAcceptedOnHold( releasable );
            }
        } else {
            changeStatus(releasable, LifeCycleStatus.ACCEPTED, LifeCycleEventType.ACCEPTED, comment);
            // Notify listeners
            for ( LifecycleEventListener listener : getListeners() ) {
                listener.fireAccepted( releasable );
            }
        }
    }

    /**
     * The reviewer rejects the publication and corrections are required by the owner.
     *
     * @param releasable the releasable
     * @param comment mandatory comment
     */
    public void reject(Releasable releasable, String comment) {
        if (canChangeStatus(releasable)){
            throw new IllegalTransitionException("Transition ready for checking to curation in progress cannot be applied to object '"+ releasable.toString()+
                    "' with state: '"+releasable.getStatus()+"'");
        }
        enfoceMandatory(comment);
        changeStatus(releasable, LifeCycleStatus.CURATION_IN_PROGRESS, LifeCycleEventType.REJECTED, comment);

        // Notify listeners
        for ( LifecycleEventListener listener : getListeners() ) {
            listener.fireRejected( releasable );
        }
    }
}
