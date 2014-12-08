package uk.ac.ebi.intact.jami.dao.impl;

import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.PublicationDao;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEventType;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Implementation of publication dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23/01/14</pre>
 */
public class PublicationDaoImpl extends AbstractIntactBaseDao<Publication, IntactPublication> implements PublicationDao {

    public PublicationDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super(IntactPublication.class, entityManager, context);
    }

    public IntactPublication getByAc(String ac) {
        return getEntityManager().find(IntactPublication.class, ac);
    }

    public IntactPublication getByPubmedId(String value) {
        Query query = getEntityManager().createQuery("select distinct p from IntactPublication p " +
                "join p.dbXrefs as x " +
                "join x.database as dat " +
                "join x.qualifier as qual " +
                "where dat.shortName = :pubmed "+
                "and (qual.shortName = :identity or qual.shortName = :secondaryAc or qual.shortName = :primary) " +
                "and x.id = :id");
        query.setParameter("identity", Xref.IDENTITY);
        query.setParameter("secondaryAc", Xref.SECONDARY);
        query.setParameter("primary", Xref.PRIMARY_MI);
        query.setParameter("pubmed", Xref.PUBMED);
        query.setParameter("id", value);
        List<IntactPublication> results = query.getResultList();
        if (results.size() == 1){
            return results.iterator().next();
        }
        else if (results.isEmpty()){
            return null;
        }
        else{
            throw new NonUniqueResultException("We found "+results.size()+" publications matching pubmed identifier "+value);
        }
    }

    public IntactPublication getByDOI(String value) {
        Query query = getEntityManager().createQuery("select distinct p from IntactPublication p " +
                "join p.dbXrefs as x " +
                "join x.database as dat " +
                "join x.qualifier as qual " +
                "where dat.shortName = :doi "+
                "and (qual.shortName = :identity or qual.shortName = :secondaryAc or qual.shortName = :primary) " +
                "and x.id = :id");
        query.setParameter("identity", Xref.IDENTITY);
        query.setParameter("secondaryAc", Xref.SECONDARY);
        query.setParameter("primary", Xref.PRIMARY_MI);
        query.setParameter("doi", Xref.DOI);
        query.setParameter("id", value);
        List<IntactPublication> results = query.getResultList();
        if (results.size() == 1){
            return results.iterator().next();
        }
        else if (results.isEmpty()){
            return null;
        }
        else{
            throw new NonUniqueResultException("We found "+results.size()+" publications matching DOI "+value);
        }
    }

    public Collection<IntactPublication> getByTitle(String value) {
        Query query;
        if (value == null){
            query = getEntityManager().createQuery("select p from IntactPublication p " +
                    "where p.title is null");
        }
        else{
            query = getEntityManager().createQuery("select p from IntactPublication p " +
                    "where p.title = :title");
            query.setParameter("title",value);
        }
        return query.getResultList();
    }

    public Collection<IntactPublication> getByTitleLike(String value) {
        Query query;
        if (value == null){
            query = getEntityManager().createQuery("select p from IntactPublication p " +
                    "where p.title is null");
        }
        else{
            query = getEntityManager().createQuery("select p from IntactPublication p  " +
                    "where upper(p.title) like :title");
            query.setParameter("title","%"+value.toUpperCase()+"%");
        }
        return query.getResultList();
    }

    public Collection<IntactPublication> getByXref(String primaryId) {
        Query query = getEntityManager().createQuery("select distinct p from IntactPublication p "  +
                "join p.dbXrefs as x " +
                "where x.id = :primaryId");
        query.setParameter("primaryId",primaryId);
        return query.getResultList();
    }

    public Collection<IntactPublication> getByXrefLike(String primaryId) {
        Query query = getEntityManager().createQuery("select distinct p from IntactPublication p "  +
                "join p.dbXrefs as x " +
                "where upper(x.id) like :primaryId");
        query.setParameter("primaryId","%"+primaryId.toUpperCase()+"%");
        return query.getResultList();
    }

    public Collection<IntactPublication> getByXref(String dbName, String dbMI, String primaryId) {
        Query query;
        if (dbMI != null){
            query = getEntityManager().createQuery("select distinct p from IntactPublication p "  +
                    "join p.dbXrefs as x " +
                    "join x.database as dat " +
                    "join dat.dbXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi " +
                    "and x.id = :primary");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", dbMI);
            query.setParameter("primary", primaryId);
        }
        else{
            query = getEntityManager().createQuery("select distinct p from IntactPublication p "  +
                    "join p.dbXrefs as x " +
                    "join x.database as d " +
                    "where d.shortName = :dbName " +
                    "and x.id = :primary");
            query.setParameter("dbName", dbName);
            query.setParameter("primary", primaryId);
        }
        return query.getResultList();
    }

    public Collection<IntactPublication> getByXrefLike(String dbName, String dbMI, String primaryId) {
        Query query;
        if (dbMI != null){
            query = getEntityManager().createQuery("select distinct p from IntactPublication p "  +
                    "join p.dbXrefs as x " +
                    "join x.database as dat " +
                    "join dat.dbXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi " +
                    "and upper(x.id) like :primary");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", dbMI);
            query.setParameter("primary", "%"+primaryId.toUpperCase()+"%");
        }
        else{
            query = getEntityManager().createQuery("select distinct p from IntactPublication p "  +
                    "join p.dbXrefs as x " +
                    "join x.database as d " +
                    "where d.shortName = :dbName " +
                    "and upper(x.id) like :primary");
            query.setParameter("dbName", dbName);
            query.setParameter("primary", "%"+primaryId.toUpperCase()+"%");
        }
        return query.getResultList();
    }

    public Collection<IntactPublication> getByXref(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI) {
        Query query;
        if (dbMI != null){
            if (qualifierName == null && qualifierMI == null){
                query = getEntityManager().createQuery("select distinct p from IntactPublication p "  +
                        "join p.dbXrefs as x " +
                        "join x.database as dat " +
                        "join dat.dbXrefs as xref " +
                        "join xref.database as d " +
                        "join xref.qualifier as q " +
                        "where x.qualifier is null " +
                        "and (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and xref.id = :mi " +
                        "and x.id = :primary");
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", dbMI);
                query.setParameter("primary", primaryId);
            }
            else if (qualifierMI != null){
                query = getEntityManager().createQuery("select distinct p from IntactPublication p "  +
                        "join p.dbXrefs as x " +
                        "join x.database as dat " +
                        "join dat.dbXrefs as xref " +
                        "join x.qualifier as qual " +
                        "join qual.dbXrefs as xref2 " +
                        "join xref.database as d " +
                        "join xref.qualifier as q " +
                        "join xref2.database as d2 " +
                        "join xref2.qualifier as q2 " +
                        "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and xref.id = :mi "+
                        "and (q2.shortName = :identity or q2.shortName = :secondaryAc) " +
                        "and d2.shortName = :psimi " +
                        "and xref2.id = :mi2 " +
                        "and x.id = :primary");
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", dbMI);
                query.setParameter("mi2", qualifierMI);
                query.setParameter("primary", primaryId);
            }
            else{
                query = getEntityManager().createQuery("select distinct p from IntactPublication p "  +
                        "join p.dbXrefs as x " +
                        "join x.database as dat " +
                        "join x.qualifier as qual " +
                        "join dat.dbXrefs as xref " +
                        "join xref.database as d " +
                        "join xref.qualifier as q " +
                        "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and xref.id = :mi " +
                        "and qual.shortName = :qName " +
                        "and x.id = :primary");
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", dbMI);
                query.setParameter("qName", qualifierName);
                query.setParameter("primary", primaryId);
            }
        }
        else{
            if (qualifierName == null && qualifierMI == null){
                query = getEntityManager().createQuery("select distinct p from IntactPublication p "  +
                        "join p.dbXrefs as x " +
                        "join x.database as d " +
                        "where d.shortName = :dbName " +
                        "and x.qualifier is null " +
                        "and x.id = :primary");
                query.setParameter("dbName", dbName);
                query.setParameter("primary", primaryId);
            }
            else if (qualifierMI != null){
                query = getEntityManager().createQuery("select distinct p from IntactPublication p "  +
                        "join p.dbXrefs as x " +
                        "join x.database as dat " +
                        "join x.qualifier as qual " +
                        "join qual.dbXrefs as xref " +
                        "join xref.database as d " +
                        "join xref.qualifier as q " +
                        "where dat.shortName = :dbName " +
                        "and (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and xref.id = :mi " +
                        "and x.id = :primary");
                query.setParameter("dbName", dbName);
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", qualifierMI);
                query.setParameter("primary", primaryId);
            }
            else{
                query = getEntityManager().createQuery("select distinct p from IntactPublication p "  +
                        "join p.dbXrefs as x " +
                        "join x.database as d " +
                        "join x.qualifier as q " +
                        "where d.shortName = :dbName " +
                        "and q.shortName = :qName " +
                        "and x.id = :primary");
                query.setParameter("dbName", dbName);
                query.setParameter("qName", qualifierName);
                query.setParameter("primary", primaryId);
            }
        }
        return query.getResultList();
    }

    public Collection<IntactPublication> getByXrefLike(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI) {
        Query query;
        if (dbMI != null){
            if (qualifierName == null && qualifierMI == null){
                query = getEntityManager().createQuery("select distinct p from IntactPublication p "  +
                        "join p.dbXrefs as x " +
                        "join x.database as dat " +
                        "join dat.dbXrefs as xref " +
                        "join xref.database as d " +
                        "join xref.qualifier as q " +
                        "where x.qualifier is null " +
                        "and (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and xref.id = :mi " +
                        "and upper(x.id) like :primary");
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", dbMI);
                query.setParameter("primary", "%"+primaryId.toUpperCase()+"%");
            }
            else if (qualifierMI != null){
                query = getEntityManager().createQuery("select distinct p from IntactPublication p "  +
                        "join p.dbXrefs as x " +
                        "join x.database as dat " +
                        "join dat.dbXrefs as xref " +
                        "join x.qualifier as qual " +
                        "join qual.dbXrefs as xref2 " +
                        "join xref.database as d " +
                        "join xref.qualifier as q " +
                        "join xref2.database as d2 " +
                        "join xref2.qualifier as q2 " +
                        "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and xref.id = :mi "+
                        "and (q2.shortName = :identity or q2.shortName = :secondaryAc) " +
                        "and d2.shortName = :psimi " +
                        "and xref2.id = :mi2 " +
                        "and upper(x.id) like :primary");
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", dbMI);
                query.setParameter("mi2", qualifierMI);
                query.setParameter("primary", "%"+primaryId.toUpperCase()+"%");
            }
            else{
                query = getEntityManager().createQuery("select distinct p from IntactPublication p "  +
                        "join p.dbXrefs as x " +
                        "join x.database as dat " +
                        "join x.qualifier as qual " +
                        "join dat.dbXrefs as xref " +
                        "join xref.database as d " +
                        "join xref.qualifier as q " +
                        "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and xref.id = :mi " +
                        "and qual.shortName = :qName " +
                        "and upper(x.id) like :primary");
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", dbMI);
                query.setParameter("qName", qualifierName);
                query.setParameter("primary", "%"+primaryId.toUpperCase()+"%");
            }
        }
        else{
            if (qualifierName == null && qualifierMI == null){
                query = getEntityManager().createQuery("select distinct p from IntactPublication p " +
                        "join p.dbXrefs as x " +
                        "join x.database as d " +
                        "where d.shortName = :dbName " +
                        "and x.qualifier is null " +
                        "and upper(x.id) like :primary");
                query.setParameter("dbName", dbName);
                query.setParameter("primary", "%"+primaryId.toUpperCase()+"%");
            }
            else if (qualifierMI != null){
                query = getEntityManager().createQuery("select distinct p from IntactPublication p "  +
                        "join p.dbXrefs as x " +
                        "join x.database as dat " +
                        "join x.qualifier as qual " +
                        "join qual.dbXrefs as xref " +
                        "join xref.database as d " +
                        "join xref.qualifier as q " +
                        "where dat.shortName = :dbName " +
                        "and (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and xref.id = :mi " +
                        "and upper(x.id) like :primary");
                query.setParameter("dbName", dbName);
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", qualifierMI);
                query.setParameter("primary", "%"+primaryId.toUpperCase()+"%");
            }
            else{
                query = getEntityManager().createQuery("select distinct p from IntactPublication p "  +
                        "join p.dbXrefs as x " +
                        "join x.database as d " +
                        "join x.qualifier as q " +
                        "where d.shortName = :dbName " +
                        "and q.shortName = :qName " +
                        "and upper(x.id) like :primary");
                query.setParameter("dbName", dbName);
                query.setParameter("qName", qualifierName);
                query.setParameter("primary", "%"+primaryId.toUpperCase()+"%");
            }
        }
        return query.getResultList();
    }

    public Collection<IntactPublication> getByAnnotationTopic(String topicName, String topicMI) {
        Query query;
        if (topicMI != null){
            query = getEntityManager().createQuery("select distinct p from IntactPublication p "  +
                    "join p.dbAnnotations as a " +
                    "join a.topic as t " +
                    "join t.dbXrefs as xref " +
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
            query = getEntityManager().createQuery("select distinct p from IntactPublication p "  +
                    "join p.dbAnnotations as a " +
                    "join a.topic as t " +
                    "where t.shortName = :topicName");
            query.setParameter("topicName", topicName);
        }
        return query.getResultList();
    }

    public Collection<IntactPublication> getByAnnotationTopicAndValue(String topicName, String topicMI, String value) {
        Query query;
        if (topicMI != null){
            query = getEntityManager().createQuery("select distinct p from IntactPublication p "  +
                    "join p.dbAnnotations as a " +
                    "join a.topic as t " +
                    "join t.dbXrefs as xref " +
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
            query = getEntityManager().createQuery("select distinct p from IntactPublication p "  +
                    "join p.dbAnnotations as a " +
                    "join a.topic as t " +
                    "where t.shortName = :topicName"+(value != null ? " and a.value = :annotValue" : ""));
            query.setParameter("topicName", topicName);
            if (value != null){
                query.setParameter("annotValue", value);
            }
        }
        return query.getResultList();
    }

    public IntactPublication getByIMEx(String value) {
        Query query = getEntityManager().createQuery("select distinct p from IntactPublication p " +
                "join p.dbXrefs as x " +
                "join x.database as dat " +
                "join x.qualifier as qual " +
                "where dat.shortName = :imex "+
                "and qual.shortName = :primary " +
                "and x.id = :id");
        query.setParameter("primary", Xref.IMEX_PRIMARY);
        query.setParameter("imex", Xref.IMEX);
        query.setParameter("id", value);
        List<IntactPublication> results = query.getResultList();
        if (results.size() == 1){
            return results.iterator().next();
        }
        else if (results.isEmpty()){
            return null;
        }
        else{
            throw new NonUniqueResultException("We found "+results.size()+" publications matching IMEx "+value);
        }
    }

    public Collection<IntactPublication> getByReleasedDate(Date date) {
        Query query;
        if (date == null){
            query = getEntityManager().createQuery("select p from IntactPublication p " +
                    "join p.lifecycleEvents as l " +
                    "join l.event as t " +
                    "where l.when is null and t.shortName = :released");
            query.setParameter("released", LifeCycleEventType.RELEASED.toString());
        }
        else{
            query = getEntityManager().createQuery("select p from IntactPublication p " +
                    "join p.lifecycleEvents as l " +
                    "join l.event as t " +
                    "where l.when = :datePub and t.shortName = :released");
            query.setParameter("datePub",date);
        }
        return query.getResultList();
    }

    public Collection<IntactPublication> getByCurationDepth(CurationDepth depth, int first, int max){
        Query query = getEntityManager().createQuery("select p from IntactPublication p "  +
                "join p.dbAnnotations as a " +
                "join a.topic as t " +
                "where t.shortName = :depthName and t.value = :curation order by p.ac");
        switch (depth){
            case IMEx:
                query.setParameter("curation", Annotation.IMEX_CURATION);
                break;
            case MIMIx:
                query.setParameter("curation", Annotation.MIMIX_CURATION);
                break;
            case rapid_curation:
                query.setParameter("curation", Annotation.RAPID_CURATION);
                break;
            default:
                break;
        }
        query.setParameter("depthName", Annotation.CURATION_DEPTH);
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<IntactPublication> getByLifecycleEvent(String evtName, int first, int max){
        Query query;
        if (evtName == null){
            query = getEntityManager().createQuery("select p from IntactPublication p "  +
                    "where p.lifecycleEvents is empty order by p.ac");
        }
        else{
            query = getEntityManager().createQuery("select distinct p from IntactPublication p "  +
                    "join p.lifecycleEvents as l "  +
                    "join l.cvEvent as e "  +
                    "where e.shortName = :name order by p.ac");
            query.setParameter("name", evtName);
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<IntactPublication> getByStatus(String statusName, int first, int max){
        Query query;
        if (statusName == null){
            query = getEntityManager().createQuery("select p from IntactPublication p "  +
                    "where p.cvStatus is null order by p.ac");
        }
        else{
            query = getEntityManager().createQuery("select p from IntactPublication p "  +
                    "join p.cvStatus as s "  +
                    "where s.shortName = :name order by p.ac");
            query.setParameter("name", statusName);
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<IntactPublication> getByCurator(String login, int first, int max){
        Query query;
        if (login != null){
            query = getEntityManager().createQuery("select p from IntactPublication p "  +
                    "where p.currentOwner is null order by p.ac");
        }
        else{
            query = getEntityManager().createQuery("select p from IntactPublication p "  +
                    "join p.currentOwner as o "  +
                    "where o.login = :name order by p.ac");
            query.setParameter("name", login);
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<IntactPublication> getByReviewer(String login, int first, int max){
        Query query;
        if (login != null){
            query = getEntityManager().createQuery("select p from IntactPublication p "  +
                    "where p.currentReviewer is null");
        }
        else{
            query = getEntityManager().createQuery("select p from IntactPublication p "  +
                    "join p.currentReviewer as c "  +
                    "where c.login = :name order by p.ac");
            query.setParameter("name", login);
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<IntactPublication> getBySource(String name, int first, int max){
        Query query;
        if (name != null){
            query = getEntityManager().createQuery("select p from IntactPublication p "  +
                    "where p.source is null order by p.ac");
        }
        else{
            query = getEntityManager().createQuery("select p from IntactPublication p "  +
                    "join p.source as s "  +
                    "where s.shortName = :name order by p.ac");
            query.setParameter("name", name);
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<IntactPublication> getByJournal(String value) {
        Query query = getEntityManager().createQuery("select distinct p from IntactPublication p " +
                "join p.dbAnnotations as a " +
                "join a.topic as t " +
                "where t.shortName = :journalName and a.value = :journal");
        query.setParameter("journalName", Annotation.PUBLICATION_JOURNAL);
        query.setParameter("journal",value);

        return query.getResultList();
    }

    public Collection<IntactPublication> getByJournalLike(String value) {
        Query query = getEntityManager().createQuery("select distinct p from IntactPublication p  " +
                "join p.dbAnnotations as a " +
                "join a.topic as t " +
                "where t.shortName = :journalName and upper(t.value) like :journal");
        query.setParameter("journalName", Annotation.PUBLICATION_JOURNAL);
        query.setParameter("journal","%"+value.toUpperCase()+"%");
        return query.getResultList();
    }

    public Collection<IntactPublication> getByPublicationDate(Date date) {
        Query query = getEntityManager().createQuery("select distinct p from IntactPublication p " +
                "join p.dbAnnotations as a " +
                "join a.topic as t " +
                "where t.shortName = :pubDateName and t.value = :datePub");
        query.setParameter("pubDateName", Annotation.PUBLICATION_YEAR);
        query.setParameter("datePub", IntactUtils.YEAR_FORMAT.format(date));
        return query.getResultList();
    }

    @Override
    public Collection<Xref> getXrefsForPublication(String ac) {
        Query query = getEntityManager().createQuery("select x from IntactPublication p " +
                "join p.dbXrefs as x " +
                "where p.ac = :ac");
        query.setParameter("ac", ac);
        return query.getResultList();
    }

    @Override
    public Collection<LifeCycleEvent> getLifeCycleEventsForPublication(String ac) {
        Query query = getEntityManager().createQuery("select l from IntactPublication p " +
                "join p.lifecycleEvents as l " +
                "where p.ac = :ac");
        query.setParameter("ac", ac);
        return query.getResultList();
    }

    @Override
    public int countExperimentsForPublication(String ac) {
        Query query = getEntityManager().createQuery("select size(p.experiments) from IntactPublication p " +
                "where p.ac = :ac");
        query.setParameter("ac", ac);
        return (Integer)query.getSingleResult();
    }

    @Override
    public int countXrefsForPublication(String ac) {
        Query query = getEntityManager().createQuery("select size(i.dbXrefs) from IntactPublication i " +
                "where i.ac = :ac");
        query.setParameter("ac", ac);
        return (Integer)query.getSingleResult();
    }

    @Override
    public int countAnnotationsForPublication(String ac) {
        Query query = getEntityManager().createQuery("select size(i.dbAnnotations) from IntactPublication i " +
                "where i.ac = :ac");
        query.setParameter("ac", ac);
        return (Integer)query.getSingleResult();
    }

    @Override
    public int countInteractionsForPublication(String ac) {
        Query query = getEntityManager().createQuery("select sum(size(e.interactionEvidences)) from IntactPublication i " +
                "join i.experiments as e " +
                "where i.ac = :ac");
        query.setParameter("ac", ac);
        return (Integer)query.getSingleResult();
    }

    @Override
    public IntactDbSynchronizer<Publication, IntactPublication> getDbSynchronizer() {
        return getSynchronizerContext().getPublicationSynchronizer();
    }
}
