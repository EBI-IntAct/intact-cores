package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ModelledEntityPool;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.ModelledEntityPoolDao;
import uk.ac.ebi.intact.jami.dao.ModelledParticipantDao;
import uk.ac.ebi.intact.jami.model.extension.IntactExperimentalEntityPool;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledEntity;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledEntityPool;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.impl.ModelledEntityPoolSynchronizer;

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
public class ModelledEntityPoolDaoImpl extends ModelledParticipantDaoImpl<ModelledEntityPool, IntactModelledEntityPool>
        implements ModelledEntityPoolDao {

    public ModelledEntityPoolDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super(IntactModelledEntityPool.class, entityManager, context);
    }

    @Override
    public IntactDbSynchronizer<ModelledEntityPool, IntactModelledEntityPool> getDbSynchronizer() {
        return getSynchronizerContext().getModelledEntityPoolSynchronizer();
    }

    public Collection<IntactExperimentalEntityPool> getByType(String typeName, String typeMI) {
        Query query;
        if (typeMI != null){
            query = getEntityManager().createQuery("select f from IntactModelledEntityPool f "  +
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
            query = getEntityManager().createQuery("select f from IntactModelledEntityPool f "  +
                    "join f.type as t " +
                    "where t.shortName = :name");
            query.setParameter("name", typeName);
        }
        return query.getResultList();
    }
}
