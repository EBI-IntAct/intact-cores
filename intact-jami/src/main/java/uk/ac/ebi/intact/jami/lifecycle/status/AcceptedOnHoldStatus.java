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
import uk.ac.ebi.intact.jami.model.user.User;

/**
 */
@Component(value = "jamiAcceptedOnHoldStatus")
public class AcceptedOnHoldStatus extends GlobalStatus {

    public AcceptedOnHoldStatus() {
        setLifecycleStatus(LifeCycleStatus.ACCEPTED_ON_HOLD);
    }

    /**
     * The releasable is ready to go through the release project.
     *
     * @param releasable the releasable
     * @param comment optional comment
     */
    public void onHoldRemoved(Releasable releasable, String comment, User who) {
        if (!canChangeStatus(releasable)){
            throw new IllegalTransitionException("Transition accepted_on_hold to ready for release cannot be applied to object '"+ releasable.toString()+
                    "' with state: '"+releasable.getStatus()+"'");
        }

        // remove any existing on-hold annotation
        releasable.removeOnHold();

        changeStatus(releasable, LifeCycleStatus.READY_FOR_RELEASE, LifeCycleEventType.READY_FOR_RELEASE, comment, who);

        for ( LifecycleEventListener listener : getListeners() ) {
            listener.fireReadyForRelease( releasable );
        }
    }
}