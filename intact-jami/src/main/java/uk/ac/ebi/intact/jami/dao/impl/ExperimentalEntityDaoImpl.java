package uk.ac.ebi.intact.jami.dao.impl;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ExperimentalEntity;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.ExperimentalEntityDao;
import uk.ac.ebi.intact.jami.model.extension.IntactExperimentalEntity;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of experimental entity dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23/01/14</pre>
 */
public class ExperimentalEntityDaoImpl<T extends ExperimentalEntity, F extends IntactExperimentalEntity> extends EntityDaoImpl<T, F>
        implements ExperimentalEntityDao<F> {

    public ExperimentalEntityDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super((Class<F>)IntactExperimentalEntity.class, entityManager, context);
    }

    public ExperimentalEntityDaoImpl(Class<F> entityClass, EntityManager entityManager, SynchronizerContext context) {
        super(entityClass, entityManager, context);
    }

    @Override
    public IntactDbSynchronizer getDbSynchronizer() {
        return getSynchronizerContext().getExperimentalEntitySynchronizer();
    }

    public Collection<F> getByExperimentalRole(String typeName, String typeMI, int first, int max) {
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

    public Collection<F> getByExperimentalPreparation(String name, String mi, int first, int max) {
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

    public Collection<F> getByDetectionMethod(String name, String mi, int first, int max) {
        Query query;
        if (name == null && mi == null){
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass()+" f "  +
                    "where f.identificationMethods is empty order by f.ac");
        }
        else if (mi != null){
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass()+" f "  +
                    "join f.identificationMethods as i " +
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
                    "join f.identificationMethods as i " +
                    "where i.shortName = :name order by f.ac");
            query.setParameter("name", name);
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<F> getByExpressedInTaxid(String taxid, int first, int max) {
        Query query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                "join f.expressedInOrganism as o " +
                "where o.persistentTaxid = :taxid order by f.ac");
        query.setParameter("taxid", taxid);
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<F> getByExpressedInAc(String ac, int first, int max) {
        Query query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                "join f.expressedInOrganism as o " +
                "where o.ac = :orgAc order by f.ac");
        query.setParameter("orgAc", ac);
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<F> getByConfidence(String typeName, String typeMI, String value, int first, int max) {
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
}
