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
package uk.ac.ebi.intact.core.unit;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.core.persister.PersisterHelper;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

/**
 * Base for all intact-tests.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/intact.spring.xml",
        "classpath*:/META-INF/standalone/*-standalone.spring.xml"})
@TransactionConfiguration
@Transactional
public abstract class IntactBasicTestCase {
    @Autowired
    private IntactContext intactContext;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PersisterHelper persisterHelper;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private IntactMockBuilder mockBuilder;

    @Before
    public void prepareBasicTest() throws Exception {
        mockBuilder = new IntactMockBuilder(intactContext.getConfig().getDefaultInstitution());
    }

    @After
    public void afterBasicTest() throws Exception {
        mockBuilder = null;
    }

    @After
    public void end() throws Exception {
       //((ConfigurableApplicationContext)applicationContext).close();
    }

    protected IntactContext getIntactContext() {
        return intactContext;
    }

    protected DataContext getDataContext() {
        return getIntactContext().getDataContext();
    }

    protected DaoFactory getDaoFactory() {
        return getDataContext().getDaoFactory();
    }

    protected IntactMockBuilder getMockBuilder() {
        return mockBuilder;
    }

    public ConfigurableApplicationContext getSpringContext() {
        return (ConfigurableApplicationContext) applicationContext;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public PersisterHelper getPersisterHelper() {
        return persisterHelper;
    }
}