package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.ModelledParticipant;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.dao.ModelledParticipantDao;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledParticipant;
import uk.ac.ebi.intact.jami.synchronizer.impl.ModelledEntitySynchronizerTemplate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of modelled entity pool dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23/01/14</pre>
 */
@Repository
public class ModelledParticipantDaoImpl<P extends ModelledParticipant, I extends IntactModelledParticipant> extends ModelledEntityDaoImpl<P, I>
        implements ModelledParticipantDao<I> {

    protected ModelledParticipantDaoImpl() {
        super((Class<I>)IntactModelledParticipant.class);
    }

    public ModelledParticipantDaoImpl(Class<I> entityClass) {
        super(entityClass);
    }

    public ModelledParticipantDaoImpl(Class<I> entityClass, EntityManager entityManager) {
        super(entityClass, entityManager);
    }

    @Override
    protected void initialiseDbSynchronizer() {
        super.setDbSynchronizer(new ModelledEntitySynchronizerTemplate<P, I>(new DefaultSynchronizerContext(getEntityManager()), getEntityClass()));
    }

    public Collection<I> getByInteractionAc(String ac) {
        Query query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                "join f.interaction as i " +
                "where i.ac = : interAc");
        query.setParameter("interAc", ac);
        return query.getResultList();
    }
}
