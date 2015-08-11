package uk.ac.ebi.intact.jami.dao.impl;

import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.SourceDao;
import uk.ac.ebi.intact.jami.model.extension.IntactSource;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import java.util.Collection;
import java.util.List;

/**
 * Implementation of source Dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23/01/14</pre>
 */
public class SourceDaoImpl extends AbstractIntactBaseDao<Source, IntactSource> implements SourceDao {
    public SourceDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super(IntactSource.class, entityManager, context);
    }

    public IntactSource getByAc(String ac) {
        return getEntityManager().find(getEntityClass(), ac);
    }

    public IntactSource getByShortName(String value) {
        Query query = getEntityManager().createQuery("select s from IntactSource s " +
                "where s.shortName = :name");
        query.setParameter("name",value);
        List<IntactSource> results = query.getResultList();
        if (results.size() == 1){
            return results.iterator().next();
        }
        else if (results.isEmpty()){
            return null;
        }
        else{
            throw new NonUniqueResultException("We found "+results.size()+" sources matching shortlabel "+value);
        }
    }

    public Collection<IntactSource> getByShortNameLike(String value) {
        Query query = getEntityManager().createQuery("select s from IntactSource s " +
                "where upper(s.shortName) like :name");
        query.setParameter("name","%"+value.toUpperCase()+"%");
        return query.getResultList();
    }

    public Collection<IntactSource> getByXref(String primaryId) {
        Query query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                "join s.dbXrefs as x " +
                "where x.id = :primaryId");
        query.setParameter("primaryId",primaryId);
        return query.getResultList();
    }

    public Collection<IntactSource> getByXrefLike(String primaryId) {
        Query query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                "join s.dbXrefs as x " +
                "where upper(x.id) like :primaryId");
        query.setParameter("primaryId","%"+primaryId.toUpperCase()+"%");
        return query.getResultList();
    }

    public Collection<IntactSource> getByXref(String dbName, String dbMI, String primaryId) {
        Query query;
        if (dbMI != null){
            query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                    "join s.dbXrefs as x " +
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
            query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                    "join s.dbXrefs as x " +
                    "join x.database as d " +
                    "where d.shortName = :dbName " +
                    "and x.id = :primary");
            query.setParameter("dbName", dbName);
            query.setParameter("primary", primaryId);
        }
        return query.getResultList();
    }

    public Collection<IntactSource> getByXrefLike(String dbName, String dbMI, String primaryId) {
        Query query;
        if (dbMI != null){
            query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                    "join s.dbXrefs as x " +
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
            query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                    "join s.dbXrefs as x " +
                    "join x.database as d " +
                    "where d.shortName = :dbName " +
                    "and upper(x.id) like :primary");
            query.setParameter("dbName", dbName);
            query.setParameter("primary", "%"+primaryId.toUpperCase()+"%");
        }
        return query.getResultList();
    }

    public Collection<IntactSource> getByXref(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI) {
        Query query;
        if (dbMI != null){
            if (qualifierName == null && qualifierMI == null){
                query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                        "join s.dbXrefs as x " +
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
                query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                        "join s.dbXrefs as x " +
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
                query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                        "join s.dbXrefs as x " +
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
                query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                        "join s.dbXrefs as x " +
                        "join x.database as d " +
                        "where d.shortName = :dbName " +
                        "and x.qualifier is null " +
                        "and x.id = :primary");
                query.setParameter("dbName", dbName);
                query.setParameter("primary", primaryId);
            }
            else if (qualifierMI != null){
                query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                        "join s.dbXrefs as x " +
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
                query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                        "join s.dbXrefs as x " +
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

    public Collection<IntactSource> getByXrefLike(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI) {
        Query query;
        if (dbMI != null){
            if (qualifierName == null && qualifierMI == null){
                query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                        "join s.dbXrefs as x " +
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
                query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                        "join s.dbXrefs as x " +
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
                query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                        "join s.dbXrefs as x " +
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
                query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                        "join s.dbXrefs as x " +
                        "join x.database as d " +
                        "where d.shortName = :dbName " +
                        "and x.qualifier is null " +
                        "and upper(x.id) like :primary");
                query.setParameter("dbName", dbName);
                query.setParameter("primary", "%"+primaryId.toUpperCase()+"%");
            }
            else if (qualifierMI != null){
                query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                        "join s.dbXrefs as x " +
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
                query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                        "join s.dbXrefs as x " +
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

    public Collection<IntactSource> getByAnnotationTopic(String topicName, String topicMI) {
        Query query;
        if (topicMI != null){
            query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                    "join s.dbAnnotations as a " +
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
            query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                    "join s.dbAnnotations as a " +
                    "join a.topic as t " +
                    "where t.shortName = :topicName");
            query.setParameter("topicName", topicName);
        }
        return query.getResultList();
    }

    public Collection<IntactSource> getByAnnotationTopicAndValue(String topicName, String topicMI, String value) {
        Query query;
        if (topicMI != null){
            query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                    "join s.dbAnnotations as a " +
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
            query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                    "join s.dbAnnotations as a " +
                    "join a.topic as t " +
                    "where t.shortName = :topicName"+(value != null ? " and a.value = :annotValue" : ""));
            query.setParameter("topicName", topicName);
            if (value != null){
                query.setParameter("annotValue", value);
            }
        }
        return query.getResultList();
    }

    public Collection<IntactSource> getByAliasName(String name) {
        Query query = getEntityManager().createQuery("select distinct so from IntactSource so " +
                "join so.synonyms as s " +
                "where s.name = :name");
        query.setParameter("name", name);
        return query.getResultList();
    }

    public Collection<IntactSource> getByAliasNameLike(String name) {
        Query query = getEntityManager().createQuery("select distinct so from IntactSource so " +
                "join so.synonyms as s " +
                "where upper(s.name) = :name");
        query.setParameter("name", "%"+name.toUpperCase()+"%");
        return query.getResultList();
    }

    public Collection<IntactSource> getByAliasTypeAndName(String typeName, String typeMI, String name) {
        Query query;
        if (typeName == null && typeMI == null){
            query = getEntityManager().createQuery("select distinct so from IntactSource so " +
                    "join so.synonyms as s " +
                    "where s.type is null " +
                    "and s.name = :name");
            query.setParameter("name", name);
        }
        else if (typeMI != null){
            query = getEntityManager().createQuery("select distinct so from IntactSource so " +
                    "join so.synonyms as s " +
                    "join s.type as t " +
                    "join t.dbXrefs as xref " +
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
            query = getEntityManager().createQuery("select distinct so from IntactSource so " +
                    "join so.synonyms as s " +
                    "join s.type as t " +
                    "where t.shortName = :typeName " +
                    "and s.name = :name");
            query.setParameter("typeName", typeName);
            query.setParameter("name", name);
        }
        return query.getResultList();
    }

    public Collection<IntactSource> getByAliasTypeAndNameLike(String typeName, String typeMI, String name) {
        Query query;
        if (typeName == null && typeMI == null){
            query = getEntityManager().createQuery("select distinct so from IntactSource so " +
                    "join so.synonyms as s " +
                    "where s.type is null " +
                    "and upper(s.name) like :name");
            query.setParameter("name", "%"+name.toUpperCase()+"%");
        }
        else if (typeMI != null){
            query = getEntityManager().createQuery("select distinct so from IntactSource so " +
                    "join so.synonyms as s " +
                    "join s.type as t " +
                    "join t.dbXrefs as xref " +
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
            query = getEntityManager().createQuery("select distinct so from IntactSource so " +
                    "join so.synonyms as s " +
                    "join s.type as t " +
                    "where t.shortName = :typeName " +
                    "and upper(s.name) like :name");
            query.setParameter("typeName", typeName);
            query.setParameter("name", "%"+name.toUpperCase()+"%");
        }
        return query.getResultList();
    }

    public IntactSource getByMIIdentifier(String primaryId) {
        Query query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                "join s.dbXrefs as x " +
                "join x.database as dat " +
                "join x.qualifier as qual " +
                "where (qual.shortName = :identity or qual.shortName = :secondaryAc) " +
                "and dat.shortName = :psimi " +
                "and x.id = :primary");
        query.setParameter("identity", Xref.IDENTITY);
        query.setParameter("secondaryAc", Xref.SECONDARY);
        query.setParameter("psimi", CvTerm.PSI_MI);
        query.setParameter("primary", primaryId);
        List<IntactSource> results = query.getResultList();
        if (results.size() == 1){
            return results.iterator().next();
        }
        else if (results.isEmpty()){
            return null;
        }
        else{
            throw new NonUniqueResultException("We found "+results.size()+" sources matching MI identifier "+primaryId);
        }
    }

    public IntactSource getByPARIdentifier(String primaryId) {
        Query query = getEntityManager().createQuery("select distinct s from IntactSource s " +
                "join s.dbXrefs as x " +
                "join x.database as dat " +
                "join x.qualifier as qual " +
                "where dat.shortName = :par "+
                "and (qual.shortName = :identity or qual.shortName = :secondaryAc) " +
                "and x.id = :primary");
        query.setParameter("identity", Xref.IDENTITY);
        query.setParameter("secondaryAc", Xref.SECONDARY);
        query.setParameter("par", CvTerm.PSI_PAR);
        query.setParameter("primary", primaryId);
        List<IntactSource> results = query.getResultList();
        if (results.size() == 1){
            return results.iterator().next();
        }
        else if (results.isEmpty()){
            return null;
        }
        else{
            throw new NonUniqueResultException("We found "+results.size()+" sources matching PAR identifier "+primaryId);
        }
    }

    @Override
    public Collection<Xref> getXrefsForSource(String ac) {
        Query query = getEntityManager().createQuery("select x from IntactSource cv " +
                "join cv.dbXrefs as x " +
                "where cv.ac = :ac");
        query.setParameter("ac", ac);
        return query.getResultList();
    }

    @Override
    public Collection<Annotation> getAnnotationsForSource(String ac) {
        Query query = getEntityManager().createQuery("select a from IntactSource cv " +
                "join cv.dbAnnotations as a " +
                "where cv.ac = :ac");
        query.setParameter("ac", ac);
        return query.getResultList();
    }

    @Override
    public Collection<Alias> getSynonymsForSource(String ac) {
        Query query = getEntityManager().createQuery("select a from IntactSource cv " +
                "join cv.aliases as a " +
                "where cv.ac = :ac");
        query.setParameter("ac", ac);
        return query.getResultList();
    }

    @Override
    public int countSynonymsForSource(String ac) {
        Query query = getEntityManager().createQuery("select size(i.synonyms) from IntactSource i " +
                "where i.ac = :ac");
        query.setParameter("ac", ac);
        return (Integer)query.getSingleResult();
    }

    @Override
    public int countXrefsForSource(String ac) {
        Query query = getEntityManager().createQuery("select size(i.dbXrefs) from IntactSource i " +
                "where i.ac = :ac");
        query.setParameter("ac", ac);
        return (Integer)query.getSingleResult();
    }

    @Override
    public int countAnnotationsForSource(String ac) {
        Query query = getEntityManager().createQuery("select size(i.dbAnnotations) from IntactSource i " +
                "where i.ac = :ac");
        query.setParameter("ac", ac);
        return (Integer)query.getSingleResult();
    }

    @Override
    public IntactDbSynchronizer<Source, IntactSource> getDbSynchronizer() {
        return getSynchronizerContext().getSourceSynchronizer();
    }
}
