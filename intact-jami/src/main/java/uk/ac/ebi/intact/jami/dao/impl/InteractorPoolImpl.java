package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.InteractorPool;
import psidev.psi.mi.jami.model.Polymer;
import uk.ac.ebi.intact.jami.dao.InteractorPoolDao;
import uk.ac.ebi.intact.jami.dao.PolymerDao;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractorPool;
import uk.ac.ebi.intact.jami.model.extension.IntactPolymer;
import uk.ac.ebi.intact.jami.synchronizer.IntactInteractorPoolSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.IntactPolymerSynchronizer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of polymerDao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31/01/14</pre>
 */
@Repository
public class InteractorPoolImpl extends InteractorDaoImpl<InteractorPool,IntactInteractorPool> implements InteractorPoolDao{
    public InteractorPoolImpl() {
        super(IntactInteractorPool.class);
    }

    public InteractorPoolImpl(EntityManager entityManager) {
        super(IntactInteractorPool.class, entityManager);
    }

    @Override
    protected void initialiseDbSynchronizer() {
        super.setDbSynchronizer(new IntactInteractorPoolSynchronizer(getEntityManager()));
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
