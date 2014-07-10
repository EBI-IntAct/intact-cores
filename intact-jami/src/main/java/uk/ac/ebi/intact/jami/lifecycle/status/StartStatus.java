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
@Component(value = "jamiStartStatus")
public class StartStatus extends GlobalStatus {

    public StartStatus() {
        setLifecycleStatus(null);
    }

    /**
     * Create a publication object.
     *
     * @param releasable the releasable
     * @param mechanism mechanism of creation of the publication
     */
    public void create(Releasable releasable, String mechanism) {
        if (!canChangeStatus(releasable)){
            throw new IllegalTransitionException("Transition to new cannot be applied to object '"+ releasable.toString()+
                    "' with state: '"+releasable.getStatus()+"'");
        }

        if( releasable.getStatus() != null ) {
            throw new IllegalTransitionException( "Cannot get publication in status NEW when it's status is already set ("+
                    releasable.getStatus() +"): ");
        }

        changeStatus(releasable, LifeCycleStatus.NEW, LifeCycleEventType.CREATED, mechanism);

        // Notify listeners
        for ( LifecycleEventListener listener : getListeners() ) {
            listener.fireCreated( releasable );
        }
    }
}