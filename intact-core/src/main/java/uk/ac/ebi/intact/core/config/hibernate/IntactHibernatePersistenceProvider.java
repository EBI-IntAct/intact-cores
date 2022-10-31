/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.core.config.hibernate;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Component;
import uk.ac.ebi.intact.model.IntactObject;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import java.util.*;

/**
 * Overrides the HibernatePersistence class only to add the intact auxiliary objects at start time.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Component
public class IntactHibernatePersistenceProvider extends HibernatePersistenceProvider {

    public static final String CV_LOCAL_SEQ = "cv_local_seq";
    public static final String UNASSIGNED_SEQ = "unassigned_seq";

    /**
     * Get an entity manager factory by its entity manager name and given the
     * appropriate extra properties. Those properties override the one get through
     * the persistence.xml file.
     *
     * @param persistenceUnitName  entity manager name
     * @param overriddenProperties properties passed to the persistence provider
     * @return initialized EntityManagerFactory
     */
    public EntityManagerFactory createEntityManagerFactory(String persistenceUnitName, Map overriddenProperties) {
        return super.createEntityManagerFactory(persistenceUnitName, overriddenProperties);
    }

    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map map) {
        EntityManagerFactory containerEntityManagerFactory = super.createContainerEntityManagerFactory(info, map);
        return containerEntityManagerFactory;
    }

    public MetadataBuilder getBasicMetaDataBuilder(String dialect) {
        StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();

        Properties properties = new Properties();
        if (dialect != null && !dialect.isBlank()) {
            registryBuilder.applySetting(Environment.DIALECT, dialect);
            properties.setProperty(Environment.DIALECT, dialect);
        }

        MetadataSources metadata = new MetadataSources(registryBuilder.build());
        HibernateConfig basicConfiguration = getBasicConfiguration(properties);
        MetadataBuilder metadataBuilder = configure(metadata.getMetadataBuilder()); // Add custom sequences
        basicConfiguration.getEntityClasses().forEach(metadata::addAnnotatedClass); // Add package classes
        return metadataBuilder;

    }

    public HibernateConfig getBasicConfiguration(Properties props) {
        if (props == null) props = new Properties();
        final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setPersistenceUnitName("intact-core-default");

        final IntactHibernateJpaVendorAdapter jpaVendorAdapter = new IntactHibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabasePlatform(Dialect.getDialect(props).getClass().getName());
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        factoryBean.afterPropertiesSet();

        factoryBean.getNativeEntityManagerFactory().close();

        HibernateConfig config = new HibernateConfig();
        return config.scanPackages(IntactObject.class.getPackageName());
    }

    private final static List<AuxiliaryDatabaseObject> auxiliaryDatabaseObjects = List.of(
            new SequenceAuxiliaryDatabaseObject(CV_LOCAL_SEQ, 1),
            new SequenceAuxiliaryDatabaseObject(UNASSIGNED_SEQ, 1)
    );

    public MetadataBuilder configure(MetadataBuilder configuration) {
        auxiliaryDatabaseObjects.forEach(configuration::applyAuxiliaryDatabaseObject);
        return configuration;
    }
}

