package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.InteractorPool;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.InteractorPoolDao;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractor;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractorPool;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.impl.InteractorPoolSynchronizer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of interactorPoolDao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31/01/14</pre>
 */
public class InteractorPoolDaoImpl extends InteractorDaoImpl<InteractorPool,IntactInteractorPool> implements InteractorPoolDao{
    public InteractorPoolDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super(IntactInteractorPool.class, entityManager, context);
    }

    @Override
    public IntactDbSynchronizer<InteractorPool,IntactInteractorPool> getDbSynchronizer() {
        return getSynchronizerContext().getInteractorPoolSynchronizer();
    }

    public Collection<IntactInteractorPool> getByInteractorAc(String ac) {
        Query query = getEntityManager().createQuery("select f from IntactInteractorPool f " +
                "join f.interactors as i " +
                "where i.ac = :interactorAc");
        query.setParameter("interactorAc",ac);
        return query.getResultList();
    }

    public Collection<IntactInteractorPool> getByInteractorShortName(String value) {
        Query query = getEntityManager().createQuery("select f from IntactInteractorPool f " +
                "join f.interactors as i " +
                "where i.shortName = :name");
        query.setParameter("name",value);
        return query.getResultList();
    }
}
