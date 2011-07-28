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
package uk.ac.ebi.intact.core.lifecycle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import uk.ac.ebi.intact.core.lifecycle.status.*;

import java.util.Collection;

/**
 * Lifecycle manager.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Controller
public class LifecycleManager {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired private StartStatus startStatus;
    @Autowired private NewStatus newStatus;
    @Autowired private AssignedStatus assignedStatus;
    @Autowired private CurationInProgressStatus curationInProgressStatus;
    @Autowired private ReadyForCheckingStatus readyForCheckingStatus;
    @Autowired private AcceptedStatus acceptedStatus;
    @Autowired private AcceptedOnHoldStatus acceptedOnHoldStatus;
    @Autowired private ReadyForReleaseStatus readyForReleaseStatus;
    @Autowired private ReleasedStatus releasedStatus;

    private Collection<GlobalStatus> getAllStatus() {
        return applicationContext.getBeansOfType( GlobalStatus.class ).values();
    }

    public void registerListener( LifecycleEventListener listener ) {
        final Collection<GlobalStatus> allStatus = getAllStatus();
        for ( GlobalStatus status : allStatus ) {
            System.out.println( status.getClass().getSimpleName() + ".registerListener("+ listener.getClass().getSimpleName() +")" );
            status.registerListener( listener );
        }
    }

    public void removeListener( LifecycleEventListener listener ) {
        final Collection<GlobalStatus> allStatus = getAllStatus();
        for ( GlobalStatus status : allStatus ) {
            System.out.println( status.getClass().getSimpleName() + ".removeListener("+ listener.getClass().getSimpleName() +")" );
            status.removeListener( listener );
        }
    }

    public void removeAllListeners(){
        final Collection<GlobalStatus> allStatus = getAllStatus();
        for ( GlobalStatus status : allStatus ) {
            status.removeAllListeners();
        }
    }

    public GlobalStatus getGlobalStatus() {
        // return any of the implementations that does not override any of its methods
        return startStatus;
    }

    public StartStatus getStartStatus() {
        return startStatus;
    }

    public NewStatus getNewStatus() {
        return newStatus;
    }

    public AssignedStatus getAssignedStatus() {
        return assignedStatus;
    }

    public CurationInProgressStatus getCurationInProgressStatus() {
        return curationInProgressStatus;
    }

    public ReadyForCheckingStatus getReadyForCheckingStatus() {
        return readyForCheckingStatus;
    }

    public AcceptedStatus getAcceptedStatus() {
        return acceptedStatus;
    }

    public AcceptedOnHoldStatus getAcceptedOnHoldStatus() {
        return acceptedOnHoldStatus;
    }

    public ReadyForReleaseStatus getReadyForReleaseStatus() {
        return readyForReleaseStatus;
    }

    public ReleasedStatus getReleasedStatus() {
        return releasedStatus;
    }
}
