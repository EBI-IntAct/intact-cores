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

import org.springframework.stereotype.Controller;
import uk.ac.ebi.intact.core.context.IntactContext;
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
public class NewStatus extends GlobalStatus {

    public void assignToCurator(Publication publication, User user) {

    }

    public void claimOwnership(Publication publication) {
        IntactContext intactContext = IntactContext.getCurrentInstance();

        publication.setCurrentOwner(intactContext.getUserContext().getUser());

        CvLifecycleEvent lifecycleEvent = intactContext.getDaoFactory()
                .getCvObjectDao(CvLifecycleEvent.class).getByIdentifier("PL:0019");
        publication.addLifecycleEvent(new LifecycleEvent(lifecycleEvent, intactContext.getUserContext().getUser(), "Claimed ownership"));

        // TODO use constants
        CvPublicationStatus publicationStatus = intactContext.getDaoFactory()
                .getCvObjectDao(CvPublicationStatus.class).getByIdentifier("PL:0006");

        publication.setStatus(publicationStatus);
    }

}
