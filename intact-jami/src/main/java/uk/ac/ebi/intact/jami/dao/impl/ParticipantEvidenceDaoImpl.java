package uk.ac.ebi.intact.jami.dao.impl;

import psidev.psi.mi.jami.model.ParticipantEvidence;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.ParticipantEvidenceDao;
import uk.ac.ebi.intact.jami.model.extension.IntactParticipantEvidence;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of participant evidence dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23/01/14</pre>
 */
public class ParticipantEvidenceDaoImpl<P extends ParticipantEvidence, I extends IntactParticipantEvidence> extends ExperimentalEntityDaoImpl<P, I>
        implements ParticipantEvidenceDao<I> {

    public ParticipantEvidenceDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super((Class<I>)IntactParticipantEvidence.class, entityManager, context);
    }

    public ParticipantEvidenceDaoImpl(Class<I> entityClass, EntityManager entityManager, SynchronizerContext context) {
        super(entityClass, entityManager, context);
    }

    @Override
    public IntactDbSynchronizer getDbSynchronizer() {
        return getSynchronizerContext().getParticipantEvidenceSynchronizer();
    }

    public Collection<I> getByInteractionAc(String ac) {
        Query query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                "join f.interaction as i " +
                "where i.ac = : interAc");
        query.setParameter("interAc", ac);
        return query.getResultList();
    }
}
