package uk.ac.ebi.intact.jami.dao.impl;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ModelledParticipant;
import psidev.psi.mi.jami.model.Xref;
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
        Query query = getEntityManager().createQuery("select f from "+getEntityClass().getSimpleName()+" f "  +
                "join f.dbParentInteraction as i " +
                "where i.ac = : interAc");
        query.setParameter("interAc", ac);
        return query.getResultList();
    }

    public Collection<I> getByCausalRelationType(String typeName, String typeMI) {
        Query query;
        if (typeMI != null){
            query = getEntityManager().createQuery("select distinct e from "+getEntityClass().getSimpleName()+" e " +
                    "join e.causalRelationships as c " +
                    "join c.relationType as t " +
                    "join t.dbXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and x.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", typeMI);
        }
        else{
            query = getEntityManager().createQuery("select e from "+getEntityClass()+" e " +
                    "join e.causalRelationships as c " +
                    "join c.relationType as t " +
                    "where t.shortName = :unitName");
            query.setParameter("unitName", typeName);
        }
        return query.getResultList();
    }

    public Collection<I> getByCausalRelationshipTargetAc(String parentAc) {
        Query query = getEntityManager().createQuery("select e from "+getEntityClass().getSimpleName()+" e  " +
                "join e.causalRelationships as c " +
                "join c.target as t " +
                "where t.ac = :ac ");
        query.setParameter("ac",parentAc);
        return query.getResultList();
    }

    public Collection<I> getByCausalRelationship(String name, String mi, String targetAc) {
        Query query;
        if (mi != null){
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f "  +
                    "join f.causalRelationships as c " +
                    "join c.dbXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    (targetAc != null ? "join c.target as t " : "")+
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi"+(targetAc != null ? " and t.ac = :tarAc" : ""));
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", mi);
            if (targetAc != null){
                query.setParameter("tarAc", targetAc);
            }
        }
        else{
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f "  +
                    "join f.causalRelationships as c " +
                    (targetAc != null ? "join c.target as t " : "")+
                    "where c.shortName = :effectName"+(targetAc != null ? " and t.ac = :tarAc" : ""));
            query.setParameter("effectName", name);
            if (targetAc != null){
                query.setParameter("tarAc", targetAc);
            }
        }
        return query.getResultList();
    }
}
