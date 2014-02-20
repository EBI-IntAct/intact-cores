package uk.ac.ebi.intact.jami.dao.impl;

import psidev.psi.mi.jami.model.Polymer;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.PolymerDao;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledParticipant;
import uk.ac.ebi.intact.jami.model.extension.IntactPolymer;
import uk.ac.ebi.intact.jami.synchronizer.impl.PolymerSynchronizerTemplate;

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
public class PolymerDaoImpl<T extends Polymer, P extends IntactPolymer> extends InteractorDaoImpl<T,P> implements PolymerDao<P>{

    public PolymerDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super((Class<P>)IntactModelledParticipant.class, entityManager, context);
    }

    public PolymerDaoImpl(Class<P> entityClass, EntityManager entityManager, SynchronizerContext context) {
        super(entityClass, entityManager, context);
    }

    public Collection<P> getBySequence(String seq) {
        Query query;
        if (seq != null){
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "join f.sequence = :seq");
            query.setParameter("seq", seq);
        }
        else{
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "where f.seq is null");
        }
        return query.getResultList();
    }

    @Override
    protected void initialiseDbSynchronizer() {
        super.setDbSynchronizer(new PolymerSynchronizerTemplate<T, P>(new DefaultSynchronizerContext(getEntityManager()), getEntityClass()));
    }

    @Override
    public void setEntityClass(Class<P> entityClass) {
        super.setEntityClass(entityClass);
        getDbSynchronizer().setIntactClass(entityClass);
    }
}
