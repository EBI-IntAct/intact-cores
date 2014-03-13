package uk.ac.ebi.intact.jami.dao.impl;

import psidev.psi.mi.jami.model.ModelledParticipant;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.ModelledParticipantDao;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledParticipant;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;

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
public class ModelledParticipantDaoImpl<P extends ModelledParticipant, I extends IntactModelledParticipant> extends ParticipantDaoImpl<P, I>
        implements ModelledParticipantDao<I> {

    public ModelledParticipantDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super((Class<I>)IntactModelledParticipant.class, entityManager, context);
    }

    public ModelledParticipantDaoImpl(Class<I> entityClass, EntityManager entityManager, SynchronizerContext context) {
        super(entityClass, entityManager, context);
    }

    @Override
    public IntactDbSynchronizer getDbSynchronizer() {
        return getSynchronizerContext().getModelledParticipantSynchronizer();
    }

    public Collection<I> getByInteractionAc(String ac) {
        Query query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                "join f.dbParentInteraction as i " +
                "where i.ac = : interAc");
        query.setParameter("interAc", ac);
        return query.getResultList();
    }
}
