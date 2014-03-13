package uk.ac.ebi.intact.jami.dao.impl;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ParticipantEvidence;
import psidev.psi.mi.jami.model.Xref;
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
public class ParticipantEvidenceDaoImpl<P extends ParticipantEvidence, I extends IntactParticipantEvidence> extends ParticipantDaoImpl<P, I>
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

    public Collection<I> getByExperimentalRole(String typeName, String typeMI, int first, int max) {
        Query query;
        if (typeMI != null){
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass()+" f "  +
                    "join f.experimentalRole as e " +
                    "join e.persistentXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi order by e.ac");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", typeMI);
        }
        else{
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "join f.experimentalRole as e " +
                    "where e.shortName = :name order by e.ac");
            query.setParameter("name", typeName);
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<I> getByExperimentalPreparation(String name, String mi, int first, int max) {
        Query query;
        if (mi != null){
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass()+" f "  +
                    "join f.experimentalPreparations as e " +
                    "join e.persistentXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi order by e.ac");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", mi);
        }
        else{
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass()+" f "  +
                    "join f.experimentalPreparations as e " +
                    "where e.shortName = :name order by e.ac");
            query.setParameter("name", name);
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<I> getByDetectionMethod(String name, String mi, int first, int max) {
        Query query;
        if (name == null && mi == null){
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass()+" f "  +
                    "where f.dbIdentificationMethods is empty order by f.ac");
        }
        else if (mi != null){
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass()+" f "  +
                    "join f.dbIdentificationMethods as i " +
                    "join i.persistentXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi order by f.ac");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", mi);
        }
        else{
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass()+" f "  +
                    "join f.dbIdentificationMethods as i " +
                    "where i.shortName = :name order by f.ac");
            query.setParameter("name", name);
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<I> getByExpressedInTaxid(String taxid, int first, int max) {
        Query query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                "join f.expressedInOrganism as o " +
                "where o.persistentTaxid = :taxid order by f.ac");
        query.setParameter("taxid", taxid);
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<I> getByExpressedInAc(String ac, int first, int max) {
        Query query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                "join f.expressedInOrganism as o " +
                "where o.ac = :orgAc order by f.ac");
        query.setParameter("orgAc", ac);
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<I> getByConfidence(String typeName, String typeMI, String value, int first, int max) {
        Query query;
        if (typeName == null && typeMI == null){
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass()+" f "  +
                    "where f.confidences is empty order by f.ac");
        }
        else if (typeMI != null){
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass()+" f "  +
                    "join f.confidences as c " +
                    "join c.type as t " +
                    "join t.persistentXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi " + (value != null ? " and c.value = :confValue ":" ")+
                    "order by f.ac");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", typeMI);
            if (value != null){
                query.setParameter("confValue", value);
            }
        }
        else{
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass()+" f "  +
                    "join f.confidences as c " +
                    "join c.type as t " +
                    "where t.shortName = :name "  + (value != null ? " and c.value = :confValue ":" ")+
                    "order by f.ac");
            query.setParameter("name", typeName);
            if (value != null){
                query.setParameter("confValue", value);
            }
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<I> getByInteractionAc(String ac) {
        Query query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                "join f.dbParentInteraction as i " +
                "where i.ac = : interAc");
        query.setParameter("interAc", ac);
        return query.getResultList();
    }
}
