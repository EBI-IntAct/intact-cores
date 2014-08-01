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
@Component(value = "jamiAssignedStatus")
public class AssignedStatus extends GlobalStatus {

    public AssignedStatus() {
        setLifecycleStatus(LifeCycleStatus.ASSIGNED);
    }

    /**
     * The curator starts to work on a specific publication.
     *
     * @param releasable the releasable
     */
    public void startCuration(Releasable releasable) {
        if (!canChangeStatus(releasable)){
            throw new IllegalTransitionException("Transition assigned to curation in progress cannot be applied to object '"+ releasable.toString()+
                    "' with state: '"+releasable.getStatus()+"'");
        }

        changeStatus(releasable, LifeCycleStatus.CURATION_IN_PROGRESS, LifeCycleEventType.CURATION_STARTED, "");

        for ( LifecycleEventListener listener : getListeners() ) {
            listener.fireCurationInProgress( releasable );
        }
    }

    /**
     * The curator decides not to work on the publication.
     *
     * @param releasable the releasable
     * @param reason a mandatory reason
     */
    public void unassign(Releasable releasable, String reason) {
        if (!canChangeStatus(releasable)){
            throw new IllegalTransitionException("Transition assigned to reserved cannot be applied to object '"+ releasable.toString()+
                    "' with state: '"+releasable.getStatus()+"'");
        }

        enfoceMandatory(reason);
        changeStatus(releasable, LifeCycleStatus.RESERVED, LifeCycleEventType.ASSIGNMENT_DECLINED, reason);

        // notify listeners
        for ( LifecycleEventListener listener : getListeners() ) {
            listener.fireAssignentDeclined( releasable );
        }
    }
}
