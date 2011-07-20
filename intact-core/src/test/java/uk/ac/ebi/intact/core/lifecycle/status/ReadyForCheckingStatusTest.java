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

import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.intact.core.lifecycle.LifecycleManager;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.CvLifecycleEventType;
import uk.ac.ebi.intact.model.CvPublicationStatus;
import uk.ac.ebi.intact.model.CvPublicationStatusType;
import uk.ac.ebi.intact.model.Publication;
import uk.ac.ebi.intact.model.util.PublicationUtils;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ReadyForCheckingStatusTest extends IntactBasicTestCase {

    @Autowired private LifecycleManager lifecycleManager;

    @Test
    public void accept_ok() throws Exception {
        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setStatus(getDaoFactory().getCvObjectDao(CvPublicationStatus.class).getByIdentifier(CvPublicationStatusType.READY_FOR_CHECKING.identifier()));

        lifecycleManager.getReadyForCheckingStatus().accept(publication, "this looks good!");

        Assert.assertEquals(CvPublicationStatusType.ACCEPTED.identifier(), publication.getStatus().getIdentifier());
        Assert.assertEquals(2, publication.getLifecycleEvents().size());
        Assert.assertEquals(CvLifecycleEventType.ACCEPTED.identifier(), publication.getLifecycleEvents().get(1).getEvent().getIdentifier());
    }

    @Test
    public void accept_onHold() throws Exception {
        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setStatus(getDaoFactory().getCvObjectDao(CvPublicationStatus.class).getByIdentifier(CvPublicationStatusType.READY_FOR_CHECKING.identifier()));

        PublicationUtils.markAsOnHold(getIntactContext(), publication, "on hold because of test");

        lifecycleManager.getReadyForCheckingStatus().accept(publication, "this looks good, but I am not allow to publish it");

        Assert.assertEquals(CvPublicationStatusType.ACCEPTED_ON_HOLD.identifier(), publication.getStatus().getIdentifier());
        Assert.assertEquals(2, publication.getLifecycleEvents().size());
        Assert.assertEquals(CvLifecycleEventType.ACCEPTED.identifier(), publication.getLifecycleEvents().get(1).getEvent().getIdentifier());
    }
}
