package uk.ac.ebi.intact.jami.dao.impl;


import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.dao.AnnotationDao;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactAnnotation;
import uk.ac.ebi.intact.jami.synchronizer.*;

import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of annotation dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */
@Repository
public class AnnotationDaoImpl<A extends AbstractIntactAnnotation> extends AbstractIntactBaseDao<A> implements AnnotationDao<A> {
    private IntactDbSynchronizer<Annotation> annotationSynchronizer;

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
            query = getEntityManager().createQuery("select a from "+getEntityClass()+" a " +
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
            query = getEntityManager().createQuery("select a from "+getEntityClass()+" a " +
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
            query = getEntityManager().createQuery("select a from "+getEntityClass()+" a " +
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

    public Collection<A> getByParentAc(String parentAc) {
        Query query = getEntityManager().createQuery("select a from " + getEntityClass() + " a " +
                "join a.parent as p " +
                "where p.ac = :ac ");
        query.setParameter("ac",parentAc);
        return query.getResultList();
    }

    public IntactDbSynchronizer<Annotation> getAnnotationSynchronizer() {
        if (this.annotationSynchronizer == null){
            this.annotationSynchronizer = new IntactAnnotationsSynchronizer(getEntityManager(), AbstractIntactAnnotation.class);
        }
        return this.annotationSynchronizer;
    }

    public void setAnnotationSynchronizer(IntactDbSynchronizer<Annotation> annotationSynchronizer) {
        this.annotationSynchronizer = annotationSynchronizer;
    }

    @Override
    public void merge(A objToReplicate) {
        prepareAnnotationTopicAndValue(objToReplicate);
        super.merge(objToReplicate);
    }

    @Override
    public void persist(A objToPersist) {
        prepareAnnotationTopicAndValue(objToPersist);
        super.persist(objToPersist);
    }

    @Override
    public A update(A objToUpdate) {
        prepareAnnotationTopicAndValue(objToUpdate);
        return super.update(objToUpdate);
    }

    protected void prepareAnnotationTopicAndValue(A objToPersist) {
        getAnnotationSynchronizer().clearCache();
        try {
            getAnnotationSynchronizer().synchronizeProperties(objToPersist);
        } catch (FinderException e) {
            throw new IllegalStateException("Cannot persist the annotation because could not synchronize its annotation topic.");
        } catch (SynchronizerException e) {
            throw new IllegalStateException("Cannot persist the annotation because could not synchronize its annotation topic.");
        } catch (PersisterException e) {
            throw new IllegalStateException("Cannot persist the annotation because could not synchronize its annotation topic.");
        }
    }
}