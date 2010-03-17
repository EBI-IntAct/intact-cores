/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.intact.core.users.unit;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.users.persistence.dao.UsersDaoFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Base for all intact-users tests.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration(locations = {"classpath*:/META-INF/intact-users.spring.xml",
                                   "classpath*:/META-INF/standalone/*-standalone.spring.xml"})
@TransactionConfiguration( transactionManager = "usersTransactionManager" )
@Transactional( "users" )
public abstract class UsersBasicTestCase {

    @Autowired
    private UsersDaoFactory usersDaoFactory;

    @PersistenceContext(unitName = "intact-users-default")
    private EntityManager entityManager;

    @Before
    public void prepareBasicTest() throws Exception {

    }

    @After
    public void afterBasicTest() throws Exception {

    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public UsersDaoFactory getDaoFactory() {
        return usersDaoFactory;
    }
}
