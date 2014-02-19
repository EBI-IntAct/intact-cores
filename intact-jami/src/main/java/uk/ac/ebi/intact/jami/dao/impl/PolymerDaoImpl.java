package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.Polymer;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.dao.PolymerDao;
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
@Repository
public class PolymerDaoImpl<T extends Polymer, P extends IntactPolymer> extends InteractorDaoImpl<T,P> implements PolymerDao<P>{
    protected PolymerDaoImpl() {
        super((Class<P>)IntactPolymer.class);
    }

    public PolymerDaoImpl(Class<P> entityClass) {
        super(entityClass);
    }

    public PolymerDaoImpl(Class<P> entityClass, EntityManager entityManager) {
        super(entityClass, entityManager);
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
