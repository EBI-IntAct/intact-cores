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
import org.springframework.stereotype.Controller;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.lifecycle.status.*;
import uk.ac.ebi.intact.model.CvLifecycleEvent;
import uk.ac.ebi.intact.model.CvPublicationStatus;
import uk.ac.ebi.intact.model.LifecycleEvent;
import uk.ac.ebi.intact.model.Publication;
import uk.ac.ebi.intact.model.user.User;

/**
 * TODO comment this class header.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Controller
public class LifecycleManager {

    @Autowired private StartStatus startStatus;
    @Autowired private NewStatus newStatus;
    @Autowired private AssignedStatus assignedStatus;
    @Autowired private CurationInProgressStatus curationInProgressStatus;
    @Autowired private ReadyForCheckingStatus readyForCheckingStatus;
    @Autowired private AcceptedStatus acceptedStatus;
    @Autowired private AcceptedOnHoldStatus acceptedOnHoldStatus;
    @Autowired private ReadyForReleaseStatus readyForReleaseStatus;
    @Autowired private ReleaseStatus releaseStatus;

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

    public ReleaseStatus getReleaseStatus() {
        return releaseStatus;
    }
}
