package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.dao.InteractorDao;
import uk.ac.ebi.intact.jami.dao.PublicationDao;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractor;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;
import uk.ac.ebi.intact.jami.synchronizer.IntactInteractorBaseSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.IntactPublicationSynchronizer;

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
@Repository
public class PublicationDaoImpl extends AbstractIntactBaseDao<Publication, IntactPublication> implements PublicationDao {

    public PublicationDaoImpl() {
        super(IntactPublication.class);
    }

    public PublicationDaoImpl(EntityManager entityManager) {
        super(IntactPublication.class, entityManager);
    }

    public IntactPublication getByAc(String ac) {
        return getEntityManager().find(getEntityClass(), ac);
    }

    public IntactPublication getByPubmedId(String value) {
        Query query = getEntityManager().createQuery("select p from IntactPublication p " +
                "join p.persistentXrefs as x " +
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
        Query query = getEntityManager().createQuery("select p from IntactPublication p " +
                "join p.persistentXrefs as x " +
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

    public IntactPublication getByIMEx(String value) {
        Query query = getEntityManager().createQuery("select p from IntactPublication p " +
                "join p.persistentXrefs as x " +
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

    public Collection<IntactPublication> getByJournal(String value) {
        Query query;
        if (value == null){
            query = getEntityManager().createQuery("select p from IntactPublication p " +
                    "where p.journal is null");
        }
        else{
            query = getEntityManager().createQuery("select p from IntactPublication p " +
                    "where p.journal = :journal");
            query.setParameter("journal",value);
        }
        return query.getResultList();
    }

    public Collection<IntactPublication> getByJournalLike(String value) {
        Query query;
        if (value == null){
            query = getEntityManager().createQuery("select p from IntactPublication p " +
                    "where p.journal is null");
        }
        else{
            query = getEntityManager().createQuery("select p from IntactPublication p  " +
                    "where upper(p.journal) like :journal");
            query.setParameter("journal","%"+value.toUpperCase()+"%");
        }
        return query.getResultList();
    }

    public Collection<IntactPublication> getByPublicationDate(Date date) {
        Query query;
        if (date == null){
            query = getEntityManager().createQuery("select p from IntactPublication p " +
                    "where p.publicationDate is null");
        }
        else{
            query = getEntityManager().createQuery("select p from IntactPublication p " +
                    "where p.publicationDate = :datePub");
            query.setParameter("datePub",date);
        }
        return query.getResultList();
    }

    public Collection<IntactPublication> getByReleasedDate(Date date) {
        Query query;
        if (date == null){
            query = getEntityManager().createQuery("select p from IntactPublication p " +
                    "where p.releasedDate is null");
        }
        else{
            query = getEntityManager().createQuery("select p from IntactPublication p " +
                    "where p.releasedDate = :datePub");
            query.setParameter("datePub",date);
        }
        return query.getResultList();
    }

    public Collection<IntactPublication> getByXref(String primaryId) {
        Query query = getEntityManager().createQuery("select p from IntactPublication p "  +
                "join p.persistentXrefs as x " +
                "where x.id = :primaryId");
        query.setParameter("primaryId",primaryId);
        return query.getResultList();
    }

    public Collection<IntactPublication> getByXrefLike(String primaryId) {
        Query query = getEntityManager().createQuery("select p from IntactPublication p "  +
                "join p.persistentXrefs as x " +
                "where upper(x.id) like :primaryId");
        query.setParameter("primaryId","%"+primaryId.toUpperCase()+"%");
        return query.getResultList();
    }

    public Collection<IntactPublication> getByXref(String dbName, String dbMI, String primaryId) {
        Query query;
        if (dbMI != null){
            query = getEntityManager().createQuery("select p from IntactPublication p "  +
                    "join p.persistentXrefs as x " +
                    "join x.database as dat " +
                    "join dat.persistentXrefs as xref " +
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
            query = getEntityManager().createQuery("select p from IntactPublication p "  +
                    "join p.persistentXrefs as x " +
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
            query = getEntityManager().createQuery("select p from IntactPublication p "  +
                    "join p.persistentXrefs as x " +
                    "join x.database as dat " +
                    "join dat.persistentXrefs as xref " +
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
            query = getEntityManager().createQuery("select p from IntactPublication p "  +
                    "join p.persistentXrefs as x " +
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
                query = getEntityManager().createQuery("select p from IntactPublication p "  +
                        "join p.persistentXrefs as x " +
                        "join x.database as dat " +
                        "join dat.persistentXrefs as xref " +
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
                query = getEntityManager().createQuery("select p from IntactPublication p "  +
                        "join p.persistentXrefs as x " +
                        "join x.database as dat " +
                        "join dat.persistentXrefs as xref " +
                        "join x.qualifier as qual " +
                        "join qual.persistentXrefs as xref2 " +
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
                query = getEntityManager().createQuery("select p from IntactPublication p "  +
                        "join p.persistentXrefs as x " +
                        "join x.database as dat " +
                        "join x.qualifier as qual " +
                        "join dat.persistentXrefs as xref " +
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
                query = getEntityManager().createQuery("select p from IntactPublication p "  +
                        "join p.persistentXrefs as x " +
                        "join x.database as d " +
                        "where d.shortName = :dbName " +
                        "and x.qualifier is null " +
                        "and x.id = :primary");
                query.setParameter("dbName", dbName);
                query.setParameter("primary", primaryId);
            }
            else if (qualifierMI != null){
                query = getEntityManager().createQuery("select p from IntactPublication p "  +
                        "join p.persistentXrefs as x " +
                        "join x.database as dat " +
                        "join x.qualifier as qual " +
                        "join qual.persistentXrefs as xref " +
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
                query = getEntityManager().createQuery("select p from IntactPublication p "  +
                        "join p.persistentXrefs as x " +
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
                query = getEntityManager().createQuery("select p from IntactPublication p "  +
                        "join p.persistentXrefs as x " +
                        "join x.database as dat " +
                        "join dat.persistentXrefs as xref " +
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
                query = getEntityManager().createQuery("select p from IntactPublication p "  +
                        "join p.persistentXrefs as x " +
                        "join x.database as dat " +
                        "join dat.persistentXrefs as xref " +
                        "join x.qualifier as qual " +
                        "join qual.persistentXrefs as xref2 " +
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
                query = getEntityManager().createQuery("select p from IntactPublication p "  +
                        "join p.persistentXrefs as x " +
                        "join x.database as dat " +
                        "join x.qualifier as qual " +
                        "join dat.persistentXrefs as xref " +
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
                query = getEntityManager().createQuery("select p from IntactPublication p " +
                        "join p.persistentXrefs as x " +
                        "join x.database as d " +
                        "where d.shortName = :dbName " +
                        "and x.qualifier is null " +
                        "and upper(x.id) like :primary");
                query.setParameter("dbName", dbName);
                query.setParameter("primary", "%"+primaryId.toUpperCase()+"%");
            }
            else if (qualifierMI != null){
                query = getEntityManager().createQuery("select p from IntactPublication p "  +
                        "join p.persistentXrefs as x " +
                        "join x.database as dat " +
                        "join x.qualifier as qual " +
                        "join qual.persistentXrefs as xref " +
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
                query = getEntityManager().createQuery("select p from IntactPublication p "  +
                        "join p.persistentXrefs as x " +
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
            query = getEntityManager().createQuery("select p from IntactPublication p "  +
                    "join p.persistentAnnotations as a " +
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
            query = getEntityManager().createQuery("select p from IntactPublication p "  +
                    "join p.persistentAnnotations as a " +
                    "join a.topic as t " +
                    "where t.shortName = :topicName");
            query.setParameter("topicName", topicName);
        }
        return query.getResultList();
    }

    public Collection<IntactPublication> getByAnnotationTopicAndValue(String topicName, String topicMI, String value) {
        Query query;
        if (topicMI != null){
            query = getEntityManager().createQuery("select p from IntactPublication p "  +
                    "join p.persistentAnnotations as a " +
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
            query = getEntityManager().createQuery("select p from IntactPublication p "  +
                    "join p.persistentAnnotations as a " +
                    "join a.topic as t " +
                    "where t.shortName = :topicName"+(value != null ? " and a.value = :annotValue" : ""));
            query.setParameter("topicName", topicName);
            if (value != null){
                query.setParameter("annotValue", value);
            }
        }
        return query.getResultList();
    }

    public Collection<IntactPublication> getByCurationDepth(CurationDepth depth, int first, int max){
        Query query;
        if (depth != null){
            query = getEntityManager().createQuery("select p from IntactPublication p "  +
                    "where p.curationDepth is null or p.curationDepth = :unspecified");
            query.setParameter("unspecified", CurationDepth.undefined);
        }
        else{
            query = getEntityManager().createQuery("select p from IntactPublication p "  +
                    "where p.curationDepth = :depth");
            query.setParameter("depth", depth);
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<IntactPublication> getByLifecycleEvent(String evtName, int first, int max){
        Query query;
        if (evtName != null){
            query = getEntityManager().createQuery("select p from IntactPublication p "  +
                    "where p.lifecycleEvents is empty");
        }
        else{
            query = getEntityManager().createQuery("select p from IntactPublication p "  +
                    "join p.lifecycleEvents as l "  +
                    "join l.event as e "  +
                    "where e.shortName = :name");
            query.setParameter("name", evtName);
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<IntactPublication> getByStatus(String statusName, int first, int max){
        Query query;
        if (statusName != null){
            query = getEntityManager().createQuery("select p from IntactPublication p "  +
                    "where p.status is null");
        }
        else{
            query = getEntityManager().createQuery("select p from IntactPublication p "  +
                    "join p.status as s "  +
                    "where s.shortName = :name");
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
                    "where p.currentOwner is null");
        }
        else{
            query = getEntityManager().createQuery("select p from IntactPublication p "  +
                    "join p.currentOwner as o "  +
                    "where o.login = :name");
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
                    "where c.login = :name");
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
                    "where p.source is null");
        }
        else{
            query = getEntityManager().createQuery("select p from IntactPublication p "  +
                    "join p.source as s "  +
                    "where s.shortName = :name");
            query.setParameter("name", name);
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    @Override
    protected void initialiseDbSynchronizer() {
        super.setDbSynchronizer(new IntactPublicationSynchronizer(getEntityManager()));
    }
}
