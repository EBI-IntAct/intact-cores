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

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.intact.core.lifecycle.IllegalTransitionException;
import uk.ac.ebi.intact.core.lifecycle.LifecycleManager;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.CvLifecycleEventType;
import uk.ac.ebi.intact.model.CvPublicationStatus;
import uk.ac.ebi.intact.model.CvPublicationStatusType;
import uk.ac.ebi.intact.model.Publication;
import uk.ac.ebi.intact.model.user.User;

/**
 * TODO comment this class header.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class NewStatusTest extends IntactBasicTestCase {

    @Autowired
    private LifecycleManager lifecycleManager;

    @Test
    public void reserve() throws Exception {
        Publication publication = getMockBuilder().createPublicationRandom();

        // assert original status
        Assert.assertEquals(CvPublicationStatusType.NEW.shortLabel(), publication.getStatus().getShortLabel());
        Assert.assertEquals(1, publication.getLifecycleEvents().size());

        lifecycleManager.getNewStatus().reserve(publication, "because I say so");

        Assert.assertEquals(CvPublicationStatusType.RESERVED.shortLabel(), publication.getStatus().getShortLabel());
        Assert.assertEquals(2, publication.getLifecycleEvents().size());
        Assert.assertEquals(CvLifecycleEventType.RESERVED.identifier(), publication.getLifecycleEvents().get(1).getEvent().getIdentifier());
    }

    @Test
    public void claimOwnership() throws Exception {
        Publication publication = getMockBuilder().createPublicationRandom();

        // assert original status
        Assert.assertEquals(CvPublicationStatusType.NEW.shortLabel(), publication.getStatus().getShortLabel());
        Assert.assertEquals(1, publication.getLifecycleEvents().size());

        lifecycleManager.getNewStatus().claimOwnership(publication);

        Assert.assertEquals(CvPublicationStatusType.ASSIGNED.shortLabel(), publication.getStatus().getShortLabel());
        Assert.assertEquals(2, publication.getLifecycleEvents().size());
    }

    @Test (expected = IllegalTransitionException.class)
    public void claimOwnership_wrongInitialStatus() throws Exception {
        Publication publication = getMockBuilder().createPublicationRandom();
        publication.setStatus(getDaoFactory().getCvObjectDao(CvPublicationStatus.class).getByIdentifier(CvPublicationStatusType.DISCARDED.identifier()));

        Assert.assertEquals(CvPublicationStatusType.DISCARDED.shortLabel(), publication.getStatus().getShortLabel());
        Assert.assertEquals(1, publication.getLifecycleEvents().size());

        lifecycleManager.getNewStatus().claimOwnership(publication);
    }

    @Test
    public void assignToCurator() throws Exception {
        Publication publication = getMockBuilder().createPublicationRandom();

        // set original status to NEW

        User user = new User("lala", "Lala", "Lala", "lala@example.com");

        lifecycleManager.getNewStatus().assignToCurator(publication, user);

        // assert new status is ASSIGNED
        Assert.assertEquals(CvPublicationStatusType.ASSIGNED.shortLabel(), publication.getStatus().getShortLabel());
    }
}
