package uk.ac.ebi.intact.config;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceProvider;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactPersistence {

    /**
     * Create and return an EntityManagerFactory for the
     * named persistence unit.
     *
     * @param persistenceUnitName The name of the persistence unit
     * @return The factory that creates EntityManagers configured
     * according to the specified persistence unit
     */
    public static EntityManagerFactory createEntityManagerFactory(String persistenceUnitName) {
        return createEntityManagerFactory(persistenceUnitName, null);
    }

    /**
     * Create and return an EntityManagerFactory for the
     * named persistence unit using the given properties.
     *
     * @param persistenceUnitName The name of the persistence unit
     * @param properties Additional properties to use when creating the
     * factory. The values of these properties override any values
     * that may have been configured elsewhere.
     * @return The factory that creates EntityManagers configured
     * according to the specified persistence unit.
     */
    public static EntityManagerFactory createEntityManagerFactory(
            String persistenceUnitName, Map properties) {
        return new IntactPersistenceProvider().createEntityManagerFactory(persistenceUnitName, properties);
    }

    public static EntityManagerFactory createEntityManagerFactoryInMemory() {
        String memName = "intact-core-mem";
        return createEntityManagerFactory(memName, null);
    }
}
