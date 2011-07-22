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
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.lifecycle.LifecycleManager;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.CvLifecycleEventType;
import uk.ac.ebi.intact.model.CvPublicationStatusType;
import uk.ac.ebi.intact.model.Publication;
import uk.ac.ebi.intact.model.user.User;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class GlobalStatusTest extends IntactBasicTestCase {

    @Autowired private LifecycleManager lifecycleManager;

    @Test
    public void discard() throws Exception {
        Publication publication = getMockBuilder().createPublicationRandom();

        lifecycleManager.getGlobalStatus().discard(publication, "this one is ugly");

        Assert.assertEquals(CvPublicationStatusType.DISCARDED.identifier(), publication.getStatus().getIdentifier());
        Assert.assertEquals(2, publication.getLifecycleEvents().size());
        Assert.assertEquals(CvLifecycleEventType.DISCARDED.identifier(), publication.getLifecycleEvents().get(1).getEvent().getIdentifier());
    }

    @Test
    public void changeOwnership() throws Exception {
        Publication publication = getMockBuilder().createPublicationRandom();

        Assert.assertEquals(CvPublicationStatusType.NEW.identifier(), publication.getStatus().getIdentifier());

        User sandra = getMockBuilder().createUserSandra();
        Assert.assertNull( publication.getCurrentOwner() );

        IntactContext.getCurrentInstance().getUserContext().setUser( sandra );
        lifecycleManager.getGlobalStatus().changeOwnership(publication, "the new kid on the block");

        Assert.assertEquals( sandra, publication.getCurrentOwner() );
        Assert.assertEquals(CvPublicationStatusType.NEW.identifier(), publication.getStatus().getIdentifier());
        Assert.assertEquals(2, publication.getLifecycleEvents().size());
        Assert.assertEquals(CvLifecycleEventType.OWNER_CHANGED.identifier(), publication.getLifecycleEvents().get(1).getEvent().getIdentifier());
    }

    @Test
    public void changeReviewer() throws Exception {
        Publication publication = getMockBuilder().createPublicationRandom();

        Assert.assertEquals(CvPublicationStatusType.NEW.identifier(), publication.getStatus().getIdentifier());

        User sandra = getMockBuilder().createUserSandra();
        Assert.assertNull( publication.getCurrentReviewer() );

        lifecycleManager.getGlobalStatus().changeReviewer(publication, sandra, "the new reviewer on the block");

        Assert.assertEquals( sandra, publication.getCurrentReviewer() );
        Assert.assertEquals(CvPublicationStatusType.NEW.identifier(), publication.getStatus().getIdentifier());
        Assert.assertEquals(2, publication.getLifecycleEvents().size());
        Assert.assertEquals(CvLifecycleEventType.REVIEWER_CHANGED.identifier(), publication.getLifecycleEvents().get(1).getEvent().getIdentifier());
    }

}
