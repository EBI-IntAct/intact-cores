package uk.ac.ebi.intact.core.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ejb.HibernatePersistence;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import java.util.Map;

/**
 * Persistence provider for IntAct, that actually delegates the creation of the
 * EntityManagerFactory to the HibernatePersister
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactPersistenceProvider implements PersistenceProvider {

    private Log log = LogFactory.getLog(IntactPersistenceProvider.class);

    public EntityManagerFactory createEntityManagerFactory(String emName, Map map) {
        if (log.isInfoEnabled()) log.info("Creating EntityManagerFactory: "+emName+" ("+map+")");

        HibernatePersistence hibernatePersistence = new HibernatePersistence();

        EntityManagerFactory emf = hibernatePersistence.createEntityManagerFactory(emName, map);
        return emf;
    }

    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map map) {
        if (log.isInfoEnabled()) log.info("Creating Container EntityManagerFactory: "+info.getPersistenceUnitName()+" ("+map+")");

        HibernatePersistence hibernatePersistence = new HibernatePersistence();

        EntityManagerFactory emf = hibernatePersistence.createContainerEntityManagerFactory(info, map);
        return emf;
    }

}
