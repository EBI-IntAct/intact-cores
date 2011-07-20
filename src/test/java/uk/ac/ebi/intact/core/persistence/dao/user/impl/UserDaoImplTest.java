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
import uk.ac.ebi.intact.model.user.User;

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
}
