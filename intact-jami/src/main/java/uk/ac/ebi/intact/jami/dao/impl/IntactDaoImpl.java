package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.IntactBaseDao;
import uk.ac.ebi.intact.jami.dao.IntactDao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

/**
 * Implementation of IntactDao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>20/02/14</pre>
 */
@Repository
public class IntactDaoImpl implements IntactDao{
    @PersistenceContext(unitName = "intact-core")
    private EntityManager entityManager;

    @PersistenceUnit(unitName = "intact-core")
    @Qualifier("intactEntityManagerFactory")
    private EntityManagerFactory intactEntityManagerFactory;

    private SynchronizerContext synchronizerContext;

    public EntityManager getEntityManager() {
        if (this.entityManager != null && !this.entityManager.isOpen()) {
            this.entityManager = this.intactEntityManagerFactory.createEntityManager();
        }
        return this.entityManager;
    }

}
