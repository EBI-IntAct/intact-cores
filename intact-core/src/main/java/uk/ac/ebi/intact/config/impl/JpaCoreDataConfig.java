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
package uk.ac.ebi.intact.config.impl;

import org.hibernate.cfg.Configuration;
import org.hibernate.ejb.Ejb3Configuration;
import uk.ac.ebi.intact.config.IntactAuxiliaryConfigurator;
import uk.ac.ebi.intact.context.IntactSession;

import javax.persistence.EntityManagerFactory;
import java.util.Map;

/**
 * This configuration uses a memory database (H2)
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class JpaCoreDataConfig extends AbstractJpaDataConfig {

    public static final String NAME = "uk.ac.ebi.intact.config.JPA_CORE";

    private String persistenceUnitName;
    private EntityManagerFactory entityManagerFactory;
    private Ejb3Configuration configuration;

    public JpaCoreDataConfig(IntactSession session, String persistenceUnitName) {
        super(session);
        this.persistenceUnitName = persistenceUnitName;
    }

    @Deprecated
    public EntityManagerFactory getSessionFactory() {
        return getEntityManagerFactory();
    }

    public EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            entityManagerFactory = buildEntityManagerFactory(null);
        }
        return entityManagerFactory;
    }

    protected EntityManagerFactory buildEntityManagerFactory(Map props) {
        configuration = new Ejb3Configuration();
        configuration = configuration.configure(persistenceUnitName, props);
        IntactAuxiliaryConfigurator.configure(configuration);
        return configuration.buildEntityManagerFactory();
    }

    public Configuration getConfiguration() {
        return configuration.getHibernateConfiguration();
    }

    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }
    
}