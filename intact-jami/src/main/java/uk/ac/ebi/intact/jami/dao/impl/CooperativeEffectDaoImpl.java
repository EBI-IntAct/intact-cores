package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.CooperativeEffect;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.dao.CooperativeEffectDao;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactCooperativeEffect;
import uk.ac.ebi.intact.jami.synchronizer.impl.CooperativeEffectSynchronizerTemplate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of cooperative effect dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23/01/14</pre>
 */
@Repository
public class CooperativeEffectDaoImpl<T extends CooperativeEffect, F extends AbstractIntactCooperativeEffect> extends AbstractIntactBaseDao<T, F> implements CooperativeEffectDao<F> {

    protected CooperativeEffectDaoImpl() {
        super((Class<F>)AbstractIntactCooperativeEffect.class);
    }

    public CooperativeEffectDaoImpl(Class<F> entityClass) {
        super(entityClass);
    }

    public CooperativeEffectDaoImpl(Class<F> entityClass, EntityManager entityManager) {
        super(entityClass, entityManager);
    }

    public Collection<F> getByAnnotationTopic(String topicName, String topicMI) {
        Query query;
        if (topicMI != null){
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "join f.annotations as a " +
                    "join a.topic as t " +
                    "join t.persistentXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", topicMI);
        }
        else{
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "join f.annotations as a " +
                    "join a.topic as t " +
                    "where t.shortName = :topicName");
            query.setParameter("topicName", topicName);
        }
        return query.getResultList();
    }

    public Collection<F> getByAnnotationTopicAndValue(String topicName, String topicMI, String value) {
        Query query;
        if (topicMI != null){
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "join f.annotations as a " +
                    "join a.topic as t " +
                    "join t.persistentXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi"+(value != null ? " and a.value = :annotValue" : ""));
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", topicMI);
            if (value != null){
                query.setParameter("annotValue", value);
            }
        }
        else{
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "join f.annotations as a " +
                    "join a.topic as t " +
                    "where t.shortName = :topicName"+(value != null ? " and a.value = :annotValue" : ""));
            query.setParameter("topicName", topicName);
            if (value != null){
                query.setParameter("annotValue", value);
            }
        }
        return query.getResultList();
    }

    public Collection<F> getByAffectedInteractionAc(String ac) {
        Query query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                "join f.affectedInteractions as i " +
                "where i.ac = :interactionAc");
        query.setParameter("interactionAc", ac);
        return query.getResultList();
    }

    public Collection<F> getByComplexAc(String ac) {
        Query query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                "join f.complex as c " +
                "where c.ac = :interactionAc");
        query.setParameter("interactionAc", ac);
        return query.getResultList();
    }

    public Collection<F> getByOutcome(String name, String mi) {
        Query query;
        if (mi != null){
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "join f.outcome as c " +
                    "join c.persistentXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", mi);
        }
        else{
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "join f.outcome as c " +
                    "where c.shortName = :name");
            query.setParameter("name", name);
        }
        return query.getResultList();
    }

    public Collection<F> getByResponse(String name, String mi) {
        Query query;
        if (mi != null){
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "join f.response as r " +
                    "join r.persistentXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", mi);
        }
        else{
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "join f.response as r " +
                    "where r.shortName = :name");
            query.setParameter("name", name);
        }
        return query.getResultList();
    }

    @Override
    protected void initialiseDbSynchronizer() {
        super.setDbSynchronizer(new CooperativeEffectSynchronizerTemplate<T, F>(getEntityManager(), getEntityClass()));
    }

    @Override
    public void setEntityClass(Class<F> entityClass) {
        super.setEntityClass(entityClass);
        initialiseDbSynchronizer();
    }
}
