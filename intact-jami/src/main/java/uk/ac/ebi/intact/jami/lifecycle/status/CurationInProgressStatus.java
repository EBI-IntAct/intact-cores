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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.context.UserContext;
import uk.ac.ebi.intact.jami.lifecycle.IllegalTransitionException;
import uk.ac.ebi.intact.jami.lifecycle.LifecycleEventListener;
import uk.ac.ebi.intact.jami.lifecycle.correctionassigner.CorrectionAssigner;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEventType;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleStatus;
import uk.ac.ebi.intact.jami.model.lifecycle.Releasable;
import uk.ac.ebi.intact.jami.model.user.User;

/**
 */
@Component(value = "jamiCurationInProgressStatus")
public class CurationInProgressStatus extends GlobalStatus {

    @Autowired
    @Qualifier("jamiCorrectionAssigner")
    private CorrectionAssigner jamiCorrectionAssigner;

    public CurationInProgressStatus() {
        setLifecycleStatus(LifeCycleStatus.CURATION_IN_PROGRESS);
    }

    /**
     * The curator decides the publication can now be checked. Needs to know the outcome of the sanity check
     * in order to do the transition to the right status.
     *
     * @param releasable the releasable
     * @param message mechanism of creation of the publication
     */
    public void readyForChecking(Releasable releasable, String message, boolean successfulSanityCheck) {
        if (canChangeStatus(releasable)){
            throw new IllegalTransitionException("Transition curation in progress to ready for checking cannot be applied to object '"+ releasable.toString()+
                    "' with state: '"+releasable.getStatus()+"'");
        }

        if (successfulSanityCheck) {
            changeStatus(releasable, LifeCycleStatus.READY_FOR_CHECKING, LifeCycleEventType.READY_FOR_CHECKING, message);

            if (releasable.getCurrentReviewer() == null) {
                jamiCorrectionAssigner.assignReviewer(releasable);
            }

            // notify listeners
            for ( LifecycleEventListener listener : getListeners() ) {
                listener.fireReadyForChecking( releasable );
            }

        } else {
            User currentUser = null;
            UserContext userContext = ApplicationContextProvider.getBean("jamiUserContext", UserContext.class);
            if (userContext != null && userContext.getUserId() != null) {
                currentUser = userContext.getUser();
            }
            addLifecycleEvent(releasable, LifeCycleEventType.SANITY_CHECK_FAILED, message, currentUser);
        }
    }
}