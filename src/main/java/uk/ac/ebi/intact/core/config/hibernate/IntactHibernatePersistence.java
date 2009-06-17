/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.core.config.hibernate;

import org.hibernate.ejb.HibernatePersistence;
import org.hibernate.ejb.Ejb3Configuration;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import java.util.Map;

import uk.ac.ebi.intact.core.config.hibernate.SequenceAuxiliaryDatabaseObject;

/**
 * Overrides the HibernatePersistence class only to add the intact auxiliary objects at start time.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactHibernatePersistence extends HibernatePersistence {

    public static final String CV_LOCAL_SEQ = "cv_local_seq";

    /**
     * Get an entity manager factory by its entity manager name and given the
     * appropriate extra properties. Those proeprties override the one get through
     * the peristence.xml file.
     *
     * @param persistenceUnitName entity manager name
     * @param overridenProperties properties passed to the persistence provider
     * @return initialized EntityManagerFactory
     */
    public EntityManagerFactory createEntityManagerFactory(String persistenceUnitName, Map overridenProperties) {
        Ejb3Configuration cfg = new Ejb3Configuration();
        Ejb3Configuration configured = cfg.configure(persistenceUnitName, overridenProperties);

        if (configured != null) {
            configure(configured);
            return configured.buildEntityManagerFactory();
        }
        return null;
    }

    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map map) {
        Ejb3Configuration cfg = new Ejb3Configuration();
        Ejb3Configuration configured = cfg.configure(info, map);

        if (configured != null) {
            configure(configured);
            return configured.buildEntityManagerFactory();
        }
        return null;
    }

    /**
     * create a factory from a canonical version
     *
     * @deprecated
     */
    // This is used directly by JBoss so don't remove until further notice.  bill@jboss.org
    public EntityManagerFactory createEntityManagerFactory(Map properties) {
        Ejb3Configuration cfg = new Ejb3Configuration();
        configure(cfg);
        return cfg.createEntityManagerFactory(properties);
    }

    public Ejb3Configuration getBasicConfiguration() {
        Ejb3Configuration cfg = new Ejb3Configuration();
        configure(cfg);
        return cfg;
    }

    public void configure(Ejb3Configuration configuration) {
        configuration.addAuxiliaryDatabaseObject(new SequenceAuxiliaryDatabaseObject(CV_LOCAL_SEQ, 1));
    }
}

