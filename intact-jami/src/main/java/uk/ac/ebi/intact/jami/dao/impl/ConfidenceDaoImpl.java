package uk.ac.ebi.intact.jami.dao.impl;


import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.Confidence;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.dao.ConfidenceDao;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactConfidence;
import uk.ac.ebi.intact.jami.synchronizer.impl.ConfidenceSynchronizerTemplate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of confidence dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */
@Repository
public class ConfidenceDaoImpl<C extends AbstractIntactConfidence> extends AbstractIntactBaseDao<Confidence, C> implements ConfidenceDao<C> {

    public ConfidenceDaoImpl() {
        super((Class<C>)AbstractIntactConfidence.class);
    }

    public ConfidenceDaoImpl(Class<C> entityClass) {
        super(entityClass);
    }

    public ConfidenceDaoImpl(Class<C> entityClass, EntityManager entityManager) {
        super(entityClass, entityManager);
    }

    public Collection<C> getByValue(String value) {
        Query query = getEntityManager().createQuery("select c from " + getEntityClass() + " c where c.value = :confValue");
        query.setParameter("confValue",value);
        return query.getResultList();
    }

    public Collection<C> getByType(String typeName, String typeMI) {
        Query query;
        if (typeMI != null){
            query = getEntityManager().createQuery("select c from "+getEntityClass()+" c " +
                    "join c.type as t " +
                    "join t.persistentXrefs as x " +
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
            query = getEntityManager().createQuery("select c from "+getEntityClass()+" c " +
                    "join c.type as t " +
                    "where t.shortName = :confName");
            query.setParameter("confName", typeName);
        }
        return query.getResultList();
    }

    public Collection<C> getByTypeAndValue(String typeName, String typeMI, String value) {
        Query query;
        if (typeMI != null){
            query = getEntityManager().createQuery("select c from "+getEntityClass()+" c " +
                    "join c.type as t " +
                    "join t.persistentXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and x.id = :mi " +
                    "and c.value = :confValue");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", typeMI);
            query.setParameter("confValue", value);
        }
        else{
            query = getEntityManager().createQuery("select c from "+getEntityClass()+" c " +
                    "join c.type as t " +
                    "where t.shortName = :confName " +
                    "and c.value = :confValue");
            query.setParameter("confName", typeName);
            query.setParameter("confValue", value);
        }
        return query.getResultList();
    }

    public Collection<C> getByParentAc(String parentAc) {
        Query query = getEntityManager().createQuery("select c from " + getEntityClass() + " c " +
                "join c.parent as p " +
                "where p.ac = :ac ");
        query.setParameter("ac",parentAc);
        return query.getResultList();
    }

    @Override
    public void setEntityClass(Class<C> entityClass) {
        super.setEntityClass(entityClass);
        initialiseDbSynchronizer();
    }

    @Override
    protected void initialiseDbSynchronizer() {
        super.setDbSynchronizer(new ConfidenceSynchronizerTemplate<C>(getEntityManager(), getEntityClass()));
    }
}