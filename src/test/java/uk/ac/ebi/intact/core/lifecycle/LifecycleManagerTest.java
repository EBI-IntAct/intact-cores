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

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Publication;
import uk.ac.ebi.intact.model.user.User;

/**
 * TODO comment this class header.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class LifecycleManagerTest extends IntactBasicTestCase {

    @Autowired
    private LifecycleManager lifecycleManager;

    @Test
    public void testTransition() throws Exception {
        Publication publication = getMockBuilder().createPublicationRandom();

        // assert original status
        Assert.assertEquals("new", publication.getStatus().getShortLabel());
        Assert.assertEquals(1, publication.getLifecycleEvents().size());

        lifecycleManager.getNewStatus().claimOwnership(publication);

        // assert new status is ASSIGNED
        Assert.assertEquals("assigned", publication.getStatus().getShortLabel());
        Assert.assertEquals(2, publication.getLifecycleEvents().size());
    }

    @Test
    public void testTransition2() throws Exception {
        Publication publication = getMockBuilder().createPublicationRandom();

        // set original status to NEW

        User user = new User("lala", "Lala", "Lala", "lala@example.com");

        lifecycleManager.getNewStatus().assignToCurator(publication, user);

        // assert new status is ASSIGNED
        Assert.assertEquals("assigned", publication.getStatus().getShortLabel());
    }
}
