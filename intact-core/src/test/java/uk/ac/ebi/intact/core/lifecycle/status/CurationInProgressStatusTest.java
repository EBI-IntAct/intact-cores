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
import uk.ac.ebi.intact.model.user.User;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class CurationInProgressStatusTest extends IntactBasicTestCase {

    @Autowired private LifecycleManager lifecycleManager;

    @Test
    public void readyForChecking_sanityCheckOk() throws Exception {
        User reviewer1 = getMockBuilder().createReviewer("lalaReviewer", "Lala", "Smith", "lala@example.com");
        User reviewer2 = getMockBuilder().createReviewer("tataReviewer", "Tata", "Toto", "tata@example.com");
        getCorePersister().saveOrUpdate(reviewer1, reviewer2);

        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setStatus(getDaoFactory().getCvObjectDao(CvPublicationStatus.class).getByIdentifier(CvPublicationStatusType.CURATION_IN_PROGRESS.identifier()));

        publication.setCurrentOwner(reviewer1);

        lifecycleManager.getCurationInProgressStatus().readyForChecking(publication, "ready!", true);

        Assert.assertEquals(CvPublicationStatusType.READY_FOR_CHECKING.identifier(), publication.getStatus().getIdentifier());
        Assert.assertEquals(2, publication.getLifecycleEvents().size());
        Assert.assertEquals(CvLifecycleEventType.READY_FOR_CHECKING.identifier(), publication.getLifecycleEvents().get(1).getEvent().getIdentifier());

        Assert.assertEquals(reviewer2, publication.getCurrentReviewer());
    }

    @Test
    public void readyForChecking_reviewerAlready() throws Exception {
        User reviewer1 = getMockBuilder().createReviewer("lalaReviewer", "Lala", "Smith", "lala@example.com");
        User reviewer2 = getMockBuilder().createReviewer("tataReviewer", "Tata", "Toto", "tata@example.com");
        User reviewer3 = getMockBuilder().createReviewer("loloReviewer", "Lolo", "Toto", "tata@example.com");
        getCorePersister().saveOrUpdate(reviewer1, reviewer2, reviewer3);

        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setStatus(getDaoFactory().getCvObjectDao(CvPublicationStatus.class).getByIdentifier(CvPublicationStatusType.CURATION_IN_PROGRESS.identifier()));

        publication.setCurrentOwner(reviewer1);
        publication.setCurrentReviewer(reviewer2);

        lifecycleManager.getCurationInProgressStatus().readyForChecking(publication, "ready!", true);

        Assert.assertEquals(CvPublicationStatusType.READY_FOR_CHECKING.identifier(), publication.getStatus().getIdentifier());
        Assert.assertEquals(2, publication.getLifecycleEvents().size());
        Assert.assertEquals(CvLifecycleEventType.READY_FOR_CHECKING.identifier(), publication.getLifecycleEvents().get(1).getEvent().getIdentifier());

        Assert.assertEquals(reviewer2, publication.getCurrentReviewer());
    }

    @Test
    public void readyForChecking_sanityCheckFail() throws Exception {
        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setStatus(getDaoFactory().getCvObjectDao(CvPublicationStatus.class).getByIdentifier(CvPublicationStatusType.CURATION_IN_PROGRESS.identifier()));

        lifecycleManager.getCurationInProgressStatus().readyForChecking(publication, "ready?", false);

        Assert.assertEquals(CvPublicationStatusType.CURATION_IN_PROGRESS.identifier(), publication.getStatus().getIdentifier());
        Assert.assertEquals(2, publication.getLifecycleEvents().size());
        Assert.assertEquals(CvLifecycleEventType.SANITY_CHECK_FAILED.identifier(), publication.getLifecycleEvents().get(1).getEvent().getIdentifier());
    }
}
