package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.dao.CvTermDao;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.synchronizer.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of Cv dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23/01/14</pre>
 */
@Repository
public class CvTermDaoImpl extends AbstractIntactBaseDao<IntactCvTerm> implements CvTermDao {
    private IntactDbSynchronizer<CvTerm> cvSynchronizer;

    public CvTermDaoImpl() {
        super(IntactCvTerm.class);
    }

    public CvTermDaoImpl(EntityManager entityManager) {
        super(IntactCvTerm.class, entityManager);
    }

    public IntactCvTerm getByAc(String ac) {
        return getEntityManager().find(getEntityClass(), ac);
    }

    public IntactCvTerm getByShortName(String value, String objClass) {
        Query query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                "where cv.shortName = :name and cv.objClass = :objclass ");
        query.setParameter("name",value);
        query.setParameter("objclass",objClass);
        return (IntactCvTerm) query.getSingleResult();
    }

    public Collection<IntactCvTerm> getByShortName(String value) {
        Query query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                "where cv.shortName = :name");
        query.setParameter("name",value);
        return query.getResultList();
    }

    public Collection<IntactCvTerm> getByShortNameLike(String value) {
        Query query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                "where upper(cv.shortName) like :name");
        query.setParameter("name","%"+value.toUpperCase()+"%");
        return query.getResultList();
    }

    public Collection<IntactCvTerm> getByXref(String primaryId) {
        Query query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                "join cv.persistentXrefs as x " +
                "where x.id = :primaryId");
        query.setParameter("primaryId",primaryId);
        return query.getResultList();
    }

    public Collection<IntactCvTerm> getByXrefLike(String primaryId) {
        Query query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                "join cv.persistentXrefs as x " +
                "where upper(x.id) like :primaryId");
        query.setParameter("primaryId","%"+primaryId.toUpperCase()+"%");
        return query.getResultList();
    }

    public Collection<IntactCvTerm> getByXref(String dbName, String dbMI, String primaryId) {
        Query query;
        if (dbMI != null){
            query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                    "join cv.persistentXrefs as x " +
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
            query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                    "join cv.persistentXrefs as x " +
                    "join x.database as d " +
                    "where d.shortName = :dbName " +
                    "and x.id = :primary");
            query.setParameter("dbName", dbName);
            query.setParameter("primary", primaryId);
        }
        return query.getResultList();
    }

    public Collection<IntactCvTerm> getByXrefLike(String dbName, String dbMI, String primaryId) {
        Query query;
        if (dbMI != null){
            query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                    "join cv.persistentXrefs as x " +
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
            query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                    "join cv.persistentXrefs as x " +
                    "join x.database as d " +
                    "where d.shortName = :dbName " +
                    "and upper(x.id) like :primary");
            query.setParameter("dbName", dbName);
            query.setParameter("primary", "%"+primaryId.toUpperCase()+"%");
        }
        return query.getResultList();
    }

    public Collection<IntactCvTerm> getByXref(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI) {
        Query query;
        if (dbMI != null){
            if (qualifierName == null && qualifierMI == null){
                query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                        "join cv.persistentXrefs as x " +
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
                query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                        "join cv.persistentXrefs as x " +
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
                query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                        "join cv.persistentXrefs as x " +
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
                query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                        "join cv.persistentXrefs as x " +
                        "join x.database as d " +
                        "where d.shortName = :dbName " +
                        "and x.qualifier is null " +
                        "and x.id = :primary");
                query.setParameter("dbName", dbName);
                query.setParameter("primary", primaryId);
            }
            else if (qualifierMI != null){
                query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                        "join cv.persistentXrefs as x " +
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
                query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                        "join cv.persistentXrefs as x " +
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

    public Collection<IntactCvTerm> getByXrefLike(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI) {
        Query query;
        if (dbMI != null){
            if (qualifierName == null && qualifierMI == null){
                query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                        "join cv.persistentXrefs as x " +
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
                query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                        "join cv.persistentXrefs as x " +
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
                query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                        "join cv.persistentXrefs as x " +
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
                query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                        "join cv.persistentXrefs as x " +
                        "join x.database as d " +
                        "where d.shortName = :dbName " +
                        "and x.qualifier is null " +
                        "and upper(x.id) like :primary");
                query.setParameter("dbName", dbName);
                query.setParameter("primary", "%"+primaryId.toUpperCase()+"%");
            }
            else if (qualifierMI != null){
                query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                        "join cv.persistentXrefs as x " +
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
                query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                        "join cv.persistentXrefs as x " +
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

    public Collection<IntactCvTerm> getByAnnotationTopic(String topicName, String topicMI) {
        Query query;
        if (topicMI != null){
            query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                    "join cv.annotations as a " +
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
            query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                    "join cv.annotations as a " +
                    "join a.topic as t " +
                    "where t.shortName = :topicName");
            query.setParameter("topicName", topicName);
        }
        return query.getResultList();
    }

    public Collection<IntactCvTerm> getByAnnotationTopicAndValue(String topicName, String topicMI, String value) {
        Query query;
        if (topicMI != null){
            query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                    "join cv.annotations as a " +
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
            query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                    "join cv.annotations as a " +
                    "join a.topic as t " +
                    "where t.shortName = :topicName"+(value != null ? " and a.value = :annotValue" : ""));
            query.setParameter("topicName", topicName);
            if (value != null){
                query.setParameter("annotValue", value);
            }
        }
        return query.getResultList();
    }

    public Collection<IntactCvTerm> getByAliasName(String name) {
        Query query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                "join cv.synonyms as s " +
                "where s.name = :name");
        query.setParameter("name", name);
        return query.getResultList();
    }

    public Collection<IntactCvTerm> getByAliasNameLike(String name) {
        Query query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                "join cv.synonyms as s " +
                "where upper(s.name) = :name");
        query.setParameter("name", "%"+name.toUpperCase()+"%");
        return query.getResultList();
    }

    public Collection<IntactCvTerm> getByAliasTypeAndName(String typeName, String typeMI, String name) {
        Query query;
        if (typeName == null && typeMI == null){
            query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                    "join cv.synonyms as s " +
                    "where s.type is null " +
                    "and s.name = :name");
            query.setParameter("name", name);
        }
        else if (typeMI != null){
            query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                    "join cv.synonyms as s " +
                    "join s.type as t " +
                    "join t.persistentXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi " +
                    "and s.name = :name");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", typeMI);
            query.setParameter("name", name);
        }
        else{
            query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                    "join cv.synonyms as s " +
                    "join s.type as t " +
                    "where t.shortName = :typeName " +
                    "and s.name = :name");
            query.setParameter("typeName", typeName);
            query.setParameter("name", name);
        }
        return query.getResultList();
    }

    public Collection<IntactCvTerm> getByAliasTypeAndNameLike(String typeName, String typeMI, String name) {
        Query query;
        if (typeName == null && typeMI == null){
            query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                    "join cv.synonyms as s " +
                    "where s.type is null " +
                    "and upper(s.name) like :name");
            query.setParameter("name", "%"+name.toUpperCase()+"%");
        }
        else if (typeMI != null){
            query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                    "join cv.synonyms as s " +
                    "join s.type as t " +
                    "join t.persistentXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi " +
                    "and upper(s.name) like :name");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", typeMI);
            query.setParameter("name", "%"+name.toUpperCase()+"%");
        }
        else{
            query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                    "join cv.synonyms as s " +
                    "join s.type as t " +
                    "where t.shortName = :typeName " +
                    "and upper(s.name) like :name");
            query.setParameter("typeName", typeName);
            query.setParameter("name", "%"+name.toUpperCase()+"%");
        }
        return query.getResultList();
    }

    public Collection<IntactCvTerm> getByDefinition(String des) {
        Query query;
        if (des == null){
            query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                    "where cv.definition is null");
        }
        else{
            query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                    "where cv.definition = :def ");
            query.setParameter("def", des);
        }
        return query.getResultList();
    }

    public Collection<IntactCvTerm> getByDescriptionLike(String des) {
        Query query;
        if (des == null){
            query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                    "where cv.definition is null");
        }
        else{
            query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                    "where upper(cv.definition) like :def ");
            query.setParameter("def", "%"+des.toUpperCase()+"%");
        }
        return query.getResultList();
    }

    public Collection<IntactCvTerm> getByMIIdentifier(String primaryId) {
        Query query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                "join cv.persistentXrefs as x " +
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
        query.setParameter("mi", CvTerm.PSI_MI_MI);
        query.setParameter("mi2", Xref.IDENTITY_MI);
        query.setParameter("primary", primaryId);
        return query.getResultList();
    }

    public Collection<IntactCvTerm> getByMODIdentifier(String primaryId) {
        Query query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                "join cv.persistentXrefs as x " +
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
        query.setParameter("mi", CvTerm.PSI_MOD_MI);
        query.setParameter("mi2", Xref.IDENTITY_MI);
        query.setParameter("primary", primaryId);
        return query.getResultList();
    }

    public Collection<IntactCvTerm> getByPARIdentifier(String primaryId) {
        Query query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                "join cv.persistentXrefs as x " +
                "join x.database as dat " +
                "join x.qualifier as qual " +
                "join qual.persistentXrefs as xref2 " +
                "join xref2.database as d2 " +
                "join xref2.qualifier as q2 " +
                "where dat.shortName = :par "+
                "and (q2.shortName = :identity or q2.shortName = :secondaryAc) " +
                "and d2.shortName = :psimi " +
                "and xref2.id = :mi2 " +
                "and x.id = :primary");
        query.setParameter("identity", Xref.IDENTITY);
        query.setParameter("secondaryAc", Xref.SECONDARY);
        query.setParameter("psimi", CvTerm.PSI_MI);
        query.setParameter("par", CvTerm.PSI_PAR);
        query.setParameter("mi2", Xref.IDENTITY_MI);
        query.setParameter("primary", primaryId);
        return query.getResultList();
    }

    public IntactCvTerm getByMIIdentifier(String primaryId, String objClass) {
        Query query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                "join cv.persistentXrefs as x " +
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
                "and x.id = :primary " +
                "and cv.objClass = :objclass");
        query.setParameter("identity", Xref.IDENTITY);
        query.setParameter("secondaryAc", Xref.SECONDARY);
        query.setParameter("psimi", CvTerm.PSI_MI);
        query.setParameter("mi", CvTerm.PSI_MI_MI);
        query.setParameter("mi2", Xref.IDENTITY_MI);
        query.setParameter("primary", primaryId);
        query.setParameter("objclass", objClass);
        return (IntactCvTerm)query.getSingleResult();
    }

    public IntactCvTerm getByMODIdentifier(String primaryId, String objClass) {
        Query query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                "join cv.persistentXrefs as x " +
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
                "and x.id = :primary " +
                "and cv.objClass = :objclass");
        query.setParameter("identity", Xref.IDENTITY);
        query.setParameter("secondaryAc", Xref.SECONDARY);
        query.setParameter("psimi", CvTerm.PSI_MI);
        query.setParameter("mi", CvTerm.PSI_MOD_MI);
        query.setParameter("mi2", Xref.IDENTITY_MI);
        query.setParameter("primary", primaryId);
        query.setParameter("objclass", objClass);
        return (IntactCvTerm)query.getSingleResult();
    }

    public IntactCvTerm getByPARIdentifier(String primaryId, String objClass) {
        Query query = getEntityManager().createQuery("select cv from IntactCvTerm cv " +
                "join cv.persistentXrefs as x " +
                "join x.database as dat " +
                "join x.qualifier as qual " +
                "join qual.persistentXrefs as xref2 " +
                "join xref2.database as d2 " +
                "join xref2.qualifier as q2 " +
                "where dat.shortName = :par "+
                "and (q2.shortName = :identity or q2.shortName = :secondaryAc) " +
                "and d2.shortName = :psimi " +
                "and xref2.id = :mi2 " +
                "and x.id = :primary " +
                "and cv.objClass = :objclass");
        query.setParameter("identity", Xref.IDENTITY);
        query.setParameter("secondaryAc", Xref.SECONDARY);
        query.setParameter("psimi", CvTerm.PSI_MI);
        query.setParameter("par", CvTerm.PSI_PAR);
        query.setParameter("mi2", Xref.IDENTITY_MI);
        query.setParameter("primary", primaryId);
        query.setParameter("objclass", objClass);
        return (IntactCvTerm)query.getSingleResult();
    }

    public IntactDbSynchronizer<CvTerm> getCvSynchronizer() {
        if (this.cvSynchronizer == null){
            this.cvSynchronizer = new IntactCvTermSynchronizer(getEntityManager());
        }
        return this.cvSynchronizer;
    }

    public void setCvSynchronizer(IntactDbSynchronizer<CvTerm> cvSynchronizer) {
        this.cvSynchronizer = cvSynchronizer;
    }

    @Override
    public void merge(IntactCvTerm objToReplicate) {
        prepareCv(objToReplicate);
        super.merge(objToReplicate);
    }

    @Override
    public void persist(IntactCvTerm objToPersist) {
        prepareCv(objToPersist);
        super.persist(objToPersist);
    }

    @Override
    public IntactCvTerm update(IntactCvTerm objToUpdate) {
        prepareCv(objToUpdate);
        return super.update(objToUpdate);
    }

    protected void prepareCv(IntactCvTerm objToPersist) {
        getCvSynchronizer().clearCache();
        try {
            getCvSynchronizer().synchronizeProperties(objToPersist);
        } catch (FinderException e) {
            throw new IllegalStateException("Cannot persist the Cv because could not synchronize its properties.");
        } catch (SynchronizerException e) {
            throw new IllegalStateException("Cannot persist the Cv because could not synchronize its properties.");
        } catch (PersisterException e) {
            throw new IllegalStateException("Cannot persist the Cv because could not synchronize its properties.");
        }
    }
}
