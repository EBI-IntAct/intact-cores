package uk.ac.ebi.intact.jami.dao.impl;


import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.AnnotationDao;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactAnnotation;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of annotation dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */
public class AnnotationDaoImpl<A extends AbstractIntactAnnotation> extends AbstractIntactBaseDao<Annotation, A> implements AnnotationDao<A> {

    public AnnotationDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super((Class<A>)AbstractIntactAnnotation.class, entityManager, context);
    }

    public AnnotationDaoImpl(Class<A> entityClass, EntityManager entityManager, SynchronizerContext context) {
        super(entityClass, entityManager, context);
    }

    public Collection<A> getByValue(String value) {
        Query query;
        if (value == null){
            query = getEntityManager().createQuery("select a from " + getEntityClass() + " a where a.value is null");
        }
        else{
            query = getEntityManager().createQuery("select a from " + getEntityClass() + " a where a.value = :annotValue");
            query.setParameter("annotValue",value);
        }
        return query.getResultList();
    }

    public Collection<A> getByValueLike(String value) {
        Query query;
        if (value == null){
            query = getEntityManager().createQuery("select a from " + getEntityClass() + " a where a.value is null");
        }
        else{
            query = getEntityManager().createQuery("select a from " + getEntityClass() + " a where upper(a.value) like :annotValue");
            query.setParameter("annotValue", "%" + value.toUpperCase() + "%");
        }
        return query.getResultList();
    }

    public Collection<A> getByTopic(String topicName, String topicMI) {
        Query query;
        if (topicMI != null){
            query = getEntityManager().createQuery("select distinct a from "+getEntityClass()+" a " +
                    "join a.topic as t " +
                    "join t.persistentXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and x.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", topicMI);
        }
        else{
            query = getEntityManager().createQuery("select a from "+getEntityClass()+" a " +
                    "join a.topic as t " +
                    "where t.shortName = :topicName");
            query.setParameter("topicName", topicName);
        }
        return query.getResultList();
    }

    public Collection<A> getByTopicAndValue(String topicName, String topicMI, String value) {
        Query query;
        if (topicMI != null){
            query = getEntityManager().createQuery("select distinct a from "+getEntityClass()+" a " +
                    "join a.topic as t " +
                    "join t.persistentXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and x.id = :mi " +
                    "and a.value"+(value != null ? " = :annotValue" : " is null"));
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", topicMI);
            if (value != null){
                query.setParameter("annotValue", value);
            }
        }
        else{
            query = getEntityManager().createQuery("select a from "+getEntityClass()+" a " +
                    "join a.type as t " +
                    "where t.shortName = :topicName " +
                    "and a.value"+(value != null ? " = :annotValue" : " is null"));
            query.setParameter("topicName", topicName);
            if (value != null){
                query.setParameter("annotValue", value);
            }
        }
        return query.getResultList();
    }

    public Collection<A> getByTopicAndValueLike(String topicName, String topicMI, String value) {
        Query query;
        if (topicMI != null){
            query = getEntityManager().createQuery("select distinct a from "+getEntityClass()+" a " +
                    "join a.topic as t " +
                    "join t.persistentXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and x.id = :mi " +
                    "and "+(value != null ? "upper(a.value) like :annotValue" : "a.value is null"));
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", topicMI);
            if (value != null){
                query.setParameter("annotValue", "%"+value.toUpperCase()+"%");
            }
        }
        else{
            query = getEntityManager().createQuery("select a from "+getEntityClass()+" a " +
                    "join a.type as t " +
                    "where t.shortName = :topicName " +
                    "and "+(value != null ? "upper(a.value) like :annotValue" : "a.value is null"));
            query.setParameter("topicName", topicName);
            if (value != null){
                query.setParameter("annotValue", "%"+value.toUpperCase()+"%");
            }
        }
        return query.getResultList();
    }

    @Override
    public IntactDbSynchronizer<Annotation, A> getDbSynchronizer() {
        return getSynchronizerContext().getAnnotationSynchronizer(getEntityClass());
    }
}