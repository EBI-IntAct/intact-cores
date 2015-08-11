package uk.ac.ebi.intact.jami.dao.impl;

import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.FeatureDao;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactFeature;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of feature dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23/01/14</pre>
 */
public class FeatureDaoImpl<T extends Feature, F extends AbstractIntactFeature> extends AbstractIntactBaseDao<T, F> implements FeatureDao<F> {

    public FeatureDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super((Class<F>)AbstractIntactFeature.class, entityManager, context);
    }

    public FeatureDaoImpl(Class<F> entityClass, EntityManager entityManager, SynchronizerContext context) {
        super(entityClass, entityManager, context);
    }

    public F getByAc(String ac) {
        return getEntityManager().find(getEntityClass(), ac);
    }

    public Collection<F> getByShortName(String value) {
        Query query = getEntityManager().createQuery("select f from "+getEntityClass().getSimpleName()+" f " +
                "where f.shortName = :name");
        query.setParameter("name",value);
        return query.getResultList();
    }

    public Collection<F> getByShortNameLike(String value) {
        Query query = getEntityManager().createQuery("select f from "+getEntityClass().getSimpleName()+" f " +
                "where upper(f.shortName) like :name");
        query.setParameter("name","%"+value.toUpperCase()+"%");
        return query.getResultList();
    }

    public Collection<F> getByXref(String primaryId) {
        Query query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f "  +
                "join f.dbXrefs as x " +
                "where x.id = :primaryId");
        query.setParameter("primaryId",primaryId);
        return query.getResultList();
    }

    public Collection<F> getByXrefLike(String primaryId) {
        Query query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f "  +
                "join f.dbXrefs as x " +
                "where upper(x.id) like :primaryId");
        query.setParameter("primaryId","%"+primaryId.toUpperCase()+"%");
        return query.getResultList();
    }

    public Collection<F> getByXref(String dbName, String dbMI, String primaryId) {
        Query query;
        if (dbMI != null){
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass()+" f "  +
                    "join f.dbXrefs as x " +
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
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass()+" f "  +
                    "join f.dbXrefs as x " +
                    "join x.database as d " +
                    "where d.shortName = :dbName " +
                    "and x.id = :primary");
            query.setParameter("dbName", dbName);
            query.setParameter("primary", primaryId);
        }
        return query.getResultList();
    }

    public Collection<F> getByXrefLike(String dbName, String dbMI, String primaryId) {
        Query query;
        if (dbMI != null){
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f "  +
                    "join f.dbXrefs as x " +
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
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f "  +
                    "join f.dbXrefs as x " +
                    "join x.database as d " +
                    "where d.shortName = :dbName " +
                    "and upper(x.id) like :primary");
            query.setParameter("dbName", dbName);
            query.setParameter("primary", "%"+primaryId.toUpperCase()+"%");
        }
        return query.getResultList();
    }

    public Collection<F> getByXref(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI) {
        Query query;
        if (dbMI != null){
            if (qualifierName == null && qualifierMI == null){
                query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f "  +
                        "join f.dbXrefs as x " +
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
                query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f "  +
                        "join f.dbXrefs as x " +
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
                query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f "  +
                        "join f.dbXrefs as x " +
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
                query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f "  +
                        "join f.dbXrefs as x " +
                        "join x.database as d " +
                        "where d.shortName = :dbName " +
                        "and x.qualifier is null " +
                        "and x.id = :primary");
                query.setParameter("dbName", dbName);
                query.setParameter("primary", primaryId);
            }
            else if (qualifierMI != null){
                query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f "  +
                        "join f.dbXrefs as x " +
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
                query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f "  +
                        "join f.dbXrefs as x " +
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

    public Collection<F> getByXrefLike(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI) {
        Query query;
        if (dbMI != null){
            if (qualifierName == null && qualifierMI == null){
                query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f "  +
                        "join f.dbXrefs as x " +
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
                query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f "  +
                        "join f.dbXrefs as x " +
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
                query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f "  +
                        "join f.dbXrefs as x " +
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
                query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f " +
                        "join f.dbXrefs as x " +
                        "join x.database as d " +
                        "where d.shortName = :dbName " +
                        "and x.qualifier is null " +
                        "and upper(x.id) like :primary");
                query.setParameter("dbName", dbName);
                query.setParameter("primary", "%"+primaryId.toUpperCase()+"%");
            }
            else if (qualifierMI != null){
                query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f "  +
                        "join f.dbXrefs as x " +
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
                query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f "  +
                        "join f.dbXrefs as x " +
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

    public Collection<F> getByAnnotationTopic(String topicName, String topicMI) {
        Query query;
        if (topicMI != null){
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f "  +
                    "join f.annotations as a " +
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
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f "  +
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
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f "  +
                    "join f.annotations as a " +
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
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f "  +
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

    public Collection<F> getByAliasName(String name) {
        Query query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f "  +
                "join f.aliases as s " +
                "where s.name = :name");
        query.setParameter("name", name);
        return query.getResultList();
    }

    public Collection<F> getByAliasNameLike(String name) {
        Query query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f " +
                "join f.aliases as s " +
                "where upper(s.name) = :name");
        query.setParameter("name", "%"+name.toUpperCase()+"%");
        return query.getResultList();
    }

    public Collection<F> getByAliasTypeAndName(String typeName, String typeMI, String name) {
        Query query;
        if (typeName == null && typeMI == null){
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f " +
                    "join f.aliases as s " +
                    "where s.type is null " +
                    "and s.name = :name");
            query.setParameter("name", name);
        }
        else if (typeMI != null){
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f " +
                    "join f.aliases as s " +
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
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f " +
                    "join f.aliases as s " +
                    "join s.type as t " +
                    "where t.shortName = :typeName " +
                    "and s.name = :name");
            query.setParameter("typeName", typeName);
            query.setParameter("name", name);
        }
        return query.getResultList();
    }

    public Collection<F> getByAliasTypeAndNameLike(String typeName, String typeMI, String name) {
        Query query;
        if (typeName == null && typeMI == null){
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f " +
                    "join f.aliases as s " +
                    "where s.type is null " +
                    "and upper(s.name) like :name");
            query.setParameter("name", "%"+name.toUpperCase()+"%");
        }
        else if (typeMI != null){
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f " +
                    "join f.aliases as s " +
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
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f "  +
                    "join f.aliases as s " +
                    "join s.type as t " +
                    "where t.shortName = :typeName " +
                    "and upper(s.name) like :name");
            query.setParameter("typeName", typeName);
            query.setParameter("name", "%"+name.toUpperCase()+"%");
        }
        return query.getResultList();
    }

    public Collection<F> getByInterproIdentifier(String primaryId) {
        Query query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f " +
                "join f.dbXrefs as x " +
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
        query.setParameter("mi", Xref.INTERPRO_MI);
        query.setParameter("mi2", Xref.IDENTITY_MI);
        query.setParameter("primary", primaryId);
        return query.getResultList();
    }

    public Collection<F> getByFeatureType(String typeName, String typeMI) {
        Query query;
        if (typeName == null && typeMI == null){
            query = getEntityManager().createQuery("select f from "+getEntityClass().getSimpleName()+" f "  +
                    "where f.type is null");
        }
        else if (typeMI != null){
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f "  +
                    "join f.type as t " +
                    "join t.dbXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", typeMI);
        }
        else{
            query = getEntityManager().createQuery("select f from "+getEntityClass().getSimpleName()+" f "  +
                    "join f.type as t " +
                    "where t.shortName = :typeName");
            query.setParameter("typeName", typeName);
        }
        return query.getResultList();
    }

    public Collection<F> getByFeatureRole(String dependencyName, String dependencyMI) {
        Query query;
        if (dependencyName == null && dependencyMI == null){
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "where f.role is null");
        }
        else if (dependencyMI != null){
            query = getEntityManager().createQuery("select distinct f from "+getEntityClass().getSimpleName()+" f "  +
                    "join f.role as id " +
                    "join id.dbXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", dependencyMI);
        }
        else{
            query = getEntityManager().createQuery("select f from "+getEntityClass().getSimpleName()+" f "  +
                    "join f.role as id " +
                    "where id.shortName = :dependencyName");
            query.setParameter("dependencyName", dependencyName);
        }
        return query.getResultList();
    }

    public Collection<F> getByParticipantAc(String ac) {
        Query query = getEntityManager().createQuery("select f from " + getEntityClass().getSimpleName() + " f " +
                "join f.participant as p " +
                "where p.ac = :ac ");
        query.setParameter("ac",ac);
        return query.getResultList();
    }

    public Collection<F> getByIsLinkProperty(boolean isLinked) {
        Query query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                "join f.ranges as r " +
                "where r.link = :isLinked ");
        query.setParameter("isLinked",isLinked);
        return query.getResultList();
    }

    public Collection<F> getByRangeStartStatus(String statusName, String statusMI) {
        Query query;
        if (statusMI != null){
            query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                    "join f.ranges as r " +
                    "join r.start.status as s " +
                    "join s.dbXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and x.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", statusMI);
        }
        else{
            query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                    "join f.ranges as r " +
                    "join r.start.status as s " +
                    "where s.shortName = :statusName");
            query.setParameter("statusName", statusName);
        }
        return query.getResultList();
    }

    public Collection<F> getByEndStatus(String statusName, String statusMI) {
        Query query;
        if (statusMI != null){
            query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                    "join f.ranges as r " +
                    "join r.end.status as s " +
                    "join s.dbXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and x.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", statusMI);
        }
        else{
            query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                    "join f.ranges as r " +
                    "join r.end.status as s " +
                    "where s.shortName = :statusName");
            query.setParameter("statusName", statusName);
        }
        return query.getResultList();
    }

    public Collection<F> getByStartAndEndStatus(String startName, String startMI, String endName, String endMI) {
        Query query;
        if (startMI != null){
            if (endMI != null){
                query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                        "join f.ranges as r " +
                        "join r.start.status as s " +
                        "join r.end.status as s2 " +
                        "join s.dbXrefs as x " +
                        "join x.database as d " +
                        "join x.qualifier as q " +
                        "join s2.dbXrefs as x2 " +
                        "join x2.database as d2 " +
                        "join x2.qualifier as q2 " +
                        "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and x.id = :mi " +
                        "and (q2.shortName = :identity or q2.shortName = :secondaryAc) " +
                        "and d2.shortName = :psimi " +
                        "and x2.id = :mi2");
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", startMI);
                query.setParameter("mi2", endMI);
            }
            else{
                query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                        "join f.ranges as r " +
                        "join r.start.status as s " +
                        "join r.end.status as s2 " +
                        "join s.dbXrefs as x " +
                        "join x.database as d " +
                        "join x.qualifier as q " +
                        "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and x.id = :mi " +
                        "and s2.shortName = :endName");
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", startMI);
                query.setParameter("endName", endName);
            }
        }
        else{
            query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                    "join f.ranges as r " +
                    "join r.start.status as s " +
                    "where s.shortName = :startName");
            query.setParameter("startName", startName);
            if (endMI != null){
                query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                        "join f.ranges as r " +
                        "join r.start.status as s2 " +
                        "join r.end.status as s " +
                        "join s.dbXrefs as x " +
                        "join x.database as d " +
                        "join x.qualifier as q " +
                        "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and x.id = :mi " +
                        "and s2.shortName = :startName");
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", startMI);
                query.setParameter("startName", startName);
            }
            else{
                query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                        "join f.ranges as r " +
                        "join r.end.status as s " +
                        "join r.start.status as s2 " +
                        "where s.shortName = :endName " +
                        "and s2.shortName = :startName");
                query.setParameter("endName", endName);
                query.setParameter("startName", startName);
            }
        }
        return query.getResultList();
    }

    public Collection<F> getByResultingSequenceXref(String primaryId) {
        Query query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                "join f.ranges as r " +
                "join r.resultingSequence.xrefs as x " +
                "where x.id = :primaryId");
        query.setParameter("primaryId",primaryId);
        return query.getResultList();
    }

    public Collection<F> getByResultingSequenceXrefLike(String primaryId) {
        Query query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                "join f.ranges as r " +
                "join r.resultingSequence.xrefs as x " +
                "where upper(x.id) like :primaryId");
        query.setParameter("primaryId","%"+primaryId.toUpperCase()+"%");
        return query.getResultList();
    }

    public Collection<F> getByResultingSequenceXref(String dbName, String dbMI, String primaryId) {
        Query query;
        if (dbMI != null){
            query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                    "join f.ranges as r " +
                    "join r.resultingSequence.xrefs as x " +
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
            query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                    "join f.ranges as r " +
                    "join f.dbXrefs as x " +
                    "join x.database as d " +
                    "where d.shortName = :dbName " +
                    "and x.id = :primary");
            query.setParameter("dbName", dbName);
            query.setParameter("primary", primaryId);
        }
        return query.getResultList();
    }

    public Collection<F> getByResultingSequenceXrefLike(String dbName, String dbMI, String primaryId) {
        Query query;
        if (dbMI != null){
            query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                    "join f.ranges as r " +
                    "join r.resultingSequence.xrefs as x " +
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
            query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                    "join f.ranges as r " +
                    "join r.dbXrefs as x " +
                    "join x.database as d " +
                    "where d.shortName = :dbName " +
                    "and upper(x.id) like :primary");
            query.setParameter("dbName", dbName);
            query.setParameter("primary", "%"+primaryId.toUpperCase()+"%");
        }
        return query.getResultList();
    }

    public Collection<F> getByResultingSequenceXref(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI) {
        Query query;
        if (dbMI != null){
            if (qualifierName == null && qualifierMI == null){
                query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                        "join f.ranges as r " +
                        "join r.resultingSequence.xrefs as x " +
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
                query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                        "join f.ranges as r " +
                        "join r.resultingSequence.xrefs as x " +
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
                query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                        "join f.ranges as r " +
                        "join r.resultingSequence.xrefs as x " +
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
                query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                        "join f.ranges as r " +
                        "join r.resultingSequence.xrefs as x " +
                        "join x.database as d " +
                        "where d.shortName = :dbName " +
                        "and x.qualifier is null " +
                        "and x.id = :primary");
                query.setParameter("dbName", dbName);
                query.setParameter("primary", primaryId);
            }
            else if (qualifierMI != null){
                query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                        "join f.ranges as r " +
                        "join r.resultingSequence.xrefs as x " +
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
                query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                        "join f.ranges as r " +
                        "join r.resultingSequence.xrefs as x " +
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

    public Collection<F> getByResultingSequenceXrefLike(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI) {
        Query query;
        if (dbMI != null){
            if (qualifierName == null && qualifierMI == null){
                query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                        "join f.ranges as r " +
                        "join r.resultingSequence.xrefs as x " +
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
                query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                        "join f.ranges as r " +
                        "join r.resultingSequence.xrefs as x " +
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
                query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                        "join f.ranges as r " +
                        "join r.resultingSequence.xrefs as x " +
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
                query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                        "join f.ranges as r " +
                        "join r.resultingSequence.xrefs as x " +
                        "join x.database as d " +
                        "where d.shortName = :dbName " +
                        "and x.qualifier is null " +
                        "and upper(x.id) like :primary");
                query.setParameter("dbName", dbName);
                query.setParameter("primary", "%"+primaryId.toUpperCase()+"%");
            }
            else if (qualifierMI != null){
                query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                        "join f.ranges as r " +
                        "join r.resultingSequence.xrefs as x " +
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
                query = getEntityManager().createQuery("select distinct f from " + getEntityClass().getSimpleName() + " f " +
                        "join f.ranges as r " +
                        "join r.resultingSequence.xrefs as x " +
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

    @Override
    public Collection<Xref> getXrefsForFeature(String ac) {
        Query query = getEntityManager().createQuery("select x from "+getEntityClass().getSimpleName()+" i " +
                "join i.dbXrefs as x " +
                "where i.ac = :ac");
        query.setParameter("ac", ac);
        return query.getResultList();
    }

    @Override
    public Collection<Annotation> getAnnotationsForFeature(String ac) {
        Query query = getEntityManager().createQuery("select a from "+getEntityClass().getSimpleName()+" i " +
                "join i.annotations as a " +
                "where i.ac = :ac");
        query.setParameter("ac", ac);
        return query.getResultList();
    }

    @Override
    public Collection<Alias> getAliasesForFeature(String ac) {
        Query query = getEntityManager().createQuery("select a from "+getEntityClass().getSimpleName()+" i " +
                "join i.aliases as a " +
                "where i.ac = :ac");
        query.setParameter("ac", ac);
        return query.getResultList();
    }

    @Override
    public int countAliasesForFeature(String ac) {
        Query query = getEntityManager().createQuery("select size(i.aliases) from "+getEntityClass().getSimpleName()+" i " +
                "where i.ac = :ac");
        query.setParameter("ac", ac);
        return (Integer)query.getSingleResult();
    }

    @Override
    public int countXrefsForFeature(String ac) {
        Query query = getEntityManager().createQuery("select size(i.dbXrefs) from "+getEntityClass().getSimpleName()+" i " +
                "where i.ac = :ac");
        query.setParameter("ac", ac);
        return (Integer)query.getSingleResult();
    }

    @Override
    public int countAnnotationsForFeature(String ac) {
        Query query = getEntityManager().createQuery("select size(i.annotations) from "+getEntityClass().getSimpleName()+" i " +
                "where i.ac = :ac");
        query.setParameter("ac", ac);
        return (Integer)query.getSingleResult();
    }

    @Override
    public int countRangesForFeature(String ac) {
        Query query = getEntityManager().createQuery("select size(i.ranges) from "+getEntityClass().getSimpleName()+" i " +
                "where i.ac = :ac");
        query.setParameter("ac", ac);
        return (Integer)query.getSingleResult();
    }

    @Override
    public IntactDbSynchronizer getDbSynchronizer() {
        return getSynchronizerContext().getFeatureSynchronizer();
    }
}
