package uk.ac.ebi.intact.jami.dao.impl;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ParticipantEvidencePool;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.ParticipantEvidencePoolDao;
import uk.ac.ebi.intact.jami.model.extension.IntactParticipantEvidencePool;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of experimental entity pool dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23/01/14</pre>
 */
public class ParticipantEvidencePoolDaoImpl extends ParticipantEvidenceDaoImpl<ParticipantEvidencePool, IntactParticipantEvidencePool>
        implements ParticipantEvidencePoolDao {

    public ParticipantEvidencePoolDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super(IntactParticipantEvidencePool.class, entityManager, context);
    }

    @Override
    public IntactDbSynchronizer<ParticipantEvidencePool, IntactParticipantEvidencePool> getDbSynchronizer() {
        return getSynchronizerContext().getParticipantEvidencePoolSynchronizer();
    }

    public Collection<IntactParticipantEvidencePool> getByType(String typeName, String typeMI) {
        Query query;
        if (typeMI != null){
            query = getEntityManager().createQuery("select distinct f from IntactExperimentalEntityPool f "  +
                    "join f.type as t " +
                    "join t.persistentXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", typeMI);
        }
        else{
            query = getEntityManager().createQuery("select f from IntactExperimentalEntityPool f "  +
                    "join f.type as t " +
                    "where t.shortName = :name");
            query.setParameter("name", typeName);
        }
        return query.getResultList();
    }
}
