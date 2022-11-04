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

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ReadyForReleaseStatusTest extends IntactBasicTestCase {

    @Autowired private LifecycleManager lifecycleManager;

    @Test
    public void readyForRelease() throws Exception {
        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setStatus(getDaoFactory().getCvObjectDao(CvPublicationStatus.class).getByIdentifier(CvPublicationStatusType.READY_FOR_RELEASE.identifier()));

        lifecycleManager.getReadyForReleaseStatus().release(publication, "Release X");

        Assert.assertEquals(CvPublicationStatusType.RELEASED.identifier(), publication.getStatus().getIdentifier());
        Assert.assertEquals(2, publication.getLifecycleEvents().size());
        Assert.assertEquals(CvLifecycleEventType.RELEASED.identifier(), publication.getLifecycleEvents().get(1).getEvent().getIdentifier());
    }

    @Test(expected = IllegalArgumentException.class)
    public void readyForRelease_fail_noComment() throws Exception {
        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setStatus(getDaoFactory().getCvObjectDao(CvPublicationStatus.class).getByIdentifier(CvPublicationStatusType.READY_FOR_RELEASE.identifier()));

        lifecycleManager.getReadyForReleaseStatus().release(publication, "");
    }

    @Test
    public void testPutOnHold() throws Exception {
         Publication publication = getMockBuilder().createPublicationRandom();
        publication.setStatus(getDaoFactory().getCvObjectDao(CvPublicationStatus.class).getByIdentifier(CvPublicationStatusType.READY_FOR_RELEASE.identifier()));

        lifecycleManager.getReadyForReleaseStatus().putOnHold(publication, "hold it!");

        Assert.assertEquals(CvPublicationStatusType.ACCEPTED_ON_HOLD.identifier(), publication.getStatus().getIdentifier());
        Assert.assertEquals(2, publication.getLifecycleEvents().size());
        Assert.assertEquals(CvLifecycleEventType.PUT_ON_HOLD.identifier(), publication.getLifecycleEvents().get(1).getEvent().getIdentifier());
    }
}
