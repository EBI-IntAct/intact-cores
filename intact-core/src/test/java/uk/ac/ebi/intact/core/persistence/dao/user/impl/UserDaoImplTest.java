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
package uk.ac.ebi.intact.core.persistence.dao.user.impl;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.user.Role;
import uk.ac.ebi.intact.model.user.User;

import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class UserDaoImplTest extends IntactBasicTestCase {

    @Test
    public void testGetByLogin() throws Exception {
        User sandra = getMockBuilder().createUserSandra();
        User jyoti = getMockBuilder().createUserJyoti();
        getCorePersister().saveOrUpdate(sandra, jyoti);

        User reloadedSandra = getDaoFactory().getUserDao().getByLogin("sandra");
        Assert.assertNotNull(reloadedSandra);

        User reloadedJyotiUppercase = getDaoFactory().getUserDao().getByLogin("JYOTI");
        Assert.assertNotNull(reloadedJyotiUppercase);

        User nonExistentUser = getDaoFactory().getUserDao().getByLogin("abcd");
        Assert.assertNull(nonExistentUser);
    }

    @Test
    public void testGetByEmail() throws Exception {
         User sandra = getMockBuilder().createUserSandra();
        User jyoti = getMockBuilder().createUserJyoti();
        getCorePersister().saveOrUpdate(sandra, jyoti);

        User reloadedSandra = getDaoFactory().getUserDao().getByEmail("sandra@example.com");
        Assert.assertNotNull(reloadedSandra);

        User reloadedJyotiFunnyCase = getDaoFactory().getUserDao().getByEmail("JyOtI@ExAmPlE.com");
        Assert.assertNotNull(reloadedJyotiFunnyCase);

        User nonExistentUser = getDaoFactory().getUserDao().getByEmail("abcd@example.com");
        Assert.assertNull(nonExistentUser);
    }

    @Test
    public void testGetByRole() throws Exception {
        User reviewer1 = getMockBuilder().createReviewer("lalaReviewer", "Lala", "Smith", "lala@example.com");
        User reviewer2 = getMockBuilder().createReviewer("tataReviewer", "Tata", "Toto", "tata@example.com");
        User curator = getMockBuilder().createCurator("loloCurator", "Lolo", "Jones", "lolo@example.com");
        getCorePersister().saveOrUpdate(reviewer1, reviewer2, curator);

        List<User> users = getDaoFactory().getUserDao().getByRole(Role.ROLE_REVIEWER);

        Assert.assertEquals(2, users.size());
        Assert.assertTrue(users.contains(reviewer1));
        Assert.assertTrue(users.contains(reviewer2));
        Assert.assertFalse(users.contains(curator));
    }

    @Test
    public void testGetCurators() throws Exception {
        User reviewer = getMockBuilder().createReviewer("lalaReviewer", "Lala", "Smith", "lala@example.com");
        User curator = getMockBuilder().createCurator("loloCurator", "Lolo", "Jones", "lolo@example.com");

        getCorePersister().saveOrUpdate(reviewer, curator);

        List<User> users = getDaoFactory().getUserDao().getCurators();
        Assert.assertEquals(2, users.size());
        Assert.assertTrue(users.contains(reviewer));
        Assert.assertTrue(users.contains(curator));
    }

    @Test
    public void testGetReviewers() throws Exception {
        User reviewer = getMockBuilder().createReviewer("lalaReviewer", "Lala", "Smith", "lala@example.com");
        User curator = getMockBuilder().createCurator("loloCurator", "Lolo", "Jones", "lolo@example.com");

        getCorePersister().saveOrUpdate(reviewer, curator);

        List<User> users = getDaoFactory().getUserDao().getReviewers();
        Assert.assertEquals(1, users.size());
        Assert.assertEquals(reviewer, users.iterator().next());
    }

    @Test
    public void testGetAdmins() throws Exception {
        User reviewer = getMockBuilder().createReviewer("lalaReviewer", "Lala", "Smith", "lala@example.com");
        User curator = getMockBuilder().createCurator("loloCurator", "Lolo", "Jones", "lolo@example.com");

        getCorePersister().saveOrUpdate(reviewer, curator);

        List<User> users = getDaoFactory().getUserDao().getAdmins();

        Assert.assertEquals(2, users.size()); // admins during initialization
    }
}
