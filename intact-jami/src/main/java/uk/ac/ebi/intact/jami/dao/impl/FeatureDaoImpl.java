package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.dao.FeatureDao;
import uk.ac.ebi.intact.jami.finder.FinderException;
import uk.ac.ebi.intact.jami.finder.IntactCvTermSynchronizer;
import uk.ac.ebi.intact.jami.finder.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Implementation of feature dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23/01/14</pre>
 */
@Repository
public class FeatureDaoImpl<F extends AbstractIntactFeature> extends AbstractIntactBaseDao<F> implements FeatureDao<F>{
    private IntactDbSynchronizer<CvTerm> topicFinder;
    private IntactDbSynchronizer<CvTerm> typeFinder;
    private IntactDbSynchronizer<CvTerm> dbFinder;
    private IntactDbSynchronizer<CvTerm> qualifierFinder;
    private IntactDbSynchronizer<CvTerm> aliasTypeFinder;
    private IntactDbSynchronizer<CvTerm> rangeStatusFinder;

    public F getByAc(String ac) {
        return getEntityManager().find(getEntityClass(), ac);
    }

    public F getByShortName(String value) {
        Query query = getEntityManager().createQuery("select f from "+getEntityClass()+" f " +
                "where f.shortName = :name");
        query.setParameter("name",value);
        return (F) query.getSingleResult();
    }

    public Collection<F> getByShortNameLike(String value) {
        Query query = getEntityManager().createQuery("select f from "+getEntityClass()+" f " +
                "where upper(f.shortName) like :name");
        query.setParameter("name","%"+value.toUpperCase()+"%");
        return query.getResultList();
    }

    public Collection<F> getByXref(String primaryId) {
        Query query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                "join f.persistentXrefs as x " +
                "where x.id = :primaryId");
        query.setParameter("primaryId",primaryId);
        return query.getResultList();
    }

    public Collection<F> getByXrefLike(String primaryId) {
        Query query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                "join f.persistentXrefs as x " +
                "where upper(x.id) like :primaryId");
        query.setParameter("primaryId","%"+primaryId.toUpperCase()+"%");
        return query.getResultList();
    }

    public Collection<F> getByXref(String dbName, String dbMI, String primaryId) {
        Query query;
        if (dbMI != null){
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "join f.persistentXrefs as x " +
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
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "join f.persistentXrefs as x " +
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
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "join f.persistentXrefs as x " +
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
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "join f.persistentXrefs as x " +
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
                query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                        "join f.persistentXrefs as x " +
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
                query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                        "join f.persistentXrefs as x " +
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
                query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                        "join f.persistentXrefs as x " +
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
                query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                        "join f.persistentXrefs as x " +
                        "join x.database as d " +
                        "where d.shortName = :dbName " +
                        "and x.qualifier is null " +
                        "and x.id = :primary");
                query.setParameter("dbName", dbName);
                query.setParameter("primary", primaryId);
            }
            else if (qualifierMI != null){
                query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                        "join f.persistentXrefs as x " +
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
                query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                        "join f.persistentXrefs as x " +
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
                query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                        "join f.persistentXrefs as x " +
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
                query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                        "join f.persistentXrefs as x " +
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
                query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                        "join f.persistentXrefs as x " +
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
                query = getEntityManager().createQuery("select f from "+getEntityClass()+" f " +
                        "join f.persistentXrefs as x " +
                        "join x.database as d " +
                        "where d.shortName = :dbName " +
                        "and x.qualifier is null " +
                        "and upper(x.id) like :primary");
                query.setParameter("dbName", dbName);
                query.setParameter("primary", "%"+primaryId.toUpperCase()+"%");
            }
            else if (qualifierMI != null){
                query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                        "join f.persistentXrefs as x " +
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
                query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                        "join f.persistentXrefs as x " +
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

    public Collection<F> getByAliasName(String name) {
        Query query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                "join f.aliases as s " +
                "where s.name = :name");
        query.setParameter("name", name);
        return query.getResultList();
    }

    public Collection<F> getByAliasNameLike(String name) {
        Query query = getEntityManager().createQuery("select f from "+getEntityClass()+" f " +
                "join f.aliases as s " +
                "where upper(s.name) = :name");
        query.setParameter("name", "%"+name.toUpperCase()+"%");
        return query.getResultList();
    }

    public Collection<F> getByAliasTypeAndName(String typeName, String typeMI, String name) {
        Query query;
        if (typeName == null && typeMI == null){
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f " +
                    "join f.aliases as s " +
                    "where s.type is null " +
                    "and s.name = :name");
            query.setParameter("name", name);
        }
        else if (typeMI != null){
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f " +
                    "join f.aliases as s " +
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
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f " +
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
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f " +
                    "join f.aliases as s " +
                    "where s.type is null " +
                    "and upper(s.name) like :name");
            query.setParameter("name", "%"+name.toUpperCase()+"%");
        }
        else if (typeMI != null){
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f " +
                    "join f.aliases as s " +
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
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
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
        Query query = getEntityManager().createQuery("select f from "+getEntityClass()+" f " +
                "join f.persistentXrefs as x " +
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
        query.setParameter("mi", Xref.INTERPRO_MI);
        query.setParameter("mi2", Xref.IDENTITY_MI);
        query.setParameter("primary", primaryId);
        return query.getResultList();
    }

    public Collection<F> getByFeatureType(String typeName, String typeMI) {
        Query query;
        if (typeName == null && typeMI == null){
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "where f.type is null");
        }
        else if (typeMI != null){
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "join f.type as t " +
                    "join t.persistentXrefs as xref " +
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
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "join f.type as t " +
                    "where t.shortName = :typeName");
            query.setParameter("typeName", typeName);
        }
        return query.getResultList();
    }

    public Collection<F> getByInteractionEffect(String effectName, String effectMI) {
        Query query;
        if (effectName == null && effectMI == null){
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "where f.interactionEffect is null");
        }
        else if (effectMI != null){
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "join f.interactionEffect as ie " +
                    "join ie.persistentXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", effectMI);
        }
        else{
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "join f.interactionEffect as ie " +
                    "where ie.shortName = :effectName");
            query.setParameter("effectName", effectName);
        }
        return query.getResultList();
    }

    public Collection<F> getByInteractionDependency(String dependencyName, String dependencyMI) {
        Query query;
        if (dependencyName == null && dependencyMI == null){
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "where f.interactionDependency is null");
        }
        else if (dependencyMI != null){
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "join f.interactionDependency as id " +
                    "join id.persistentXrefs as xref " +
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
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "join f.interactionDependency as id " +
                    "where id.shortName = :dependencyName");
            query.setParameter("dependencyName", dependencyName);
        }
        return query.getResultList();
    }

    public Collection<F> getByParticipantAc(String ac) {
        Query query = getEntityManager().createQuery("select f from " + getEntityClass() + " f " +
                "join f.participant as p " +
                "where p.ac = :ac ");
        query.setParameter("ac",ac);
        return query.getResultList();
    }

    public IntactDbSynchronizer<CvTerm> getTopicFinder() {
        if (this.topicFinder == null){
            this.topicFinder = new IntactCvTermSynchronizer(getEntityManager(), IntactUtils.TOPIC_OBJCLASS);
        }
        return this.topicFinder;
    }

    public void setTopicFinder(IntactDbSynchronizer<CvTerm> topicFinder) {
        this.topicFinder = topicFinder;
    }

    public IntactDbSynchronizer<CvTerm> getTypeFinder() {
        if (this.typeFinder == null){
            this.typeFinder = new IntactCvTermSynchronizer(getEntityManager(), IntactUtils.FEATURE_TYPE_OBJCLASS);
        }
        return this.typeFinder;
    }

    public void setTypeFinder(IntactDbSynchronizer<CvTerm> cvFinder) {
        this.typeFinder = cvFinder;
    }

    public IntactDbSynchronizer<CvTerm> getDbFinder() {
        if (this.dbFinder == null){
            this.dbFinder = new IntactCvTermSynchronizer(getEntityManager(), IntactUtils.DATABASE_OBJCLASS);
        }
        return dbFinder;
    }

    public void setDbFinder(IntactDbSynchronizer<CvTerm> dbFinder) {
        this.dbFinder = dbFinder;
    }

    public IntactDbSynchronizer<CvTerm> getQualifierFinder() {
        if (this.qualifierFinder == null){
            this.qualifierFinder = new IntactCvTermSynchronizer(getEntityManager(), IntactUtils.QUALIFIER_OBJCLASS);
        }
        return qualifierFinder;
    }

    public void setQualifierFinder(IntactDbSynchronizer<CvTerm> qualifierFinder) {
        this.qualifierFinder = qualifierFinder;
    }

    public IntactDbSynchronizer<CvTerm> getAliasTypeFinder() {
        if (this.aliasTypeFinder == null){
            this.aliasTypeFinder = new IntactCvTermSynchronizer(getEntityManager(), IntactUtils.ALIAS_TYPE_OBJCLASS);
        }
        return aliasTypeFinder;
    }

    public void setAliasTypeFinder(IntactDbSynchronizer<CvTerm> aliasTypeFinder) {
        this.aliasTypeFinder = aliasTypeFinder;
    }

    public IntactDbSynchronizer<CvTerm> getRangeStatusFinder() {
        if (this.rangeStatusFinder == null){
            this.rangeStatusFinder = new IntactCvTermSynchronizer(getEntityManager(), IntactUtils.RANGE_STATUS_OBJCLASS);
        }
        return rangeStatusFinder;
    }

    public void setRangeStatusFinder(IntactDbSynchronizer<CvTerm> rangeStatusFinder) {
        this.rangeStatusFinder = rangeStatusFinder;
    }

    @Override
    public void merge(F objToReplicate) {
        synchronizeProperties(objToReplicate);
        super.merge(objToReplicate);
    }

    @Override
    public void persist(F  objToPersist) {
        synchronizeProperties(objToPersist);
        super.persist(objToPersist);
    }

    @Override
    public F update(F  objToUpdate) {
        synchronizeProperties(objToUpdate);
        return super.update(objToUpdate);
    }

    protected void synchronizeProperties(F feature) throws FinderException {
        // check shortlabel/synchronize
        preparShortLabel(feature);
        // then check full name
        prepareFullName(feature);
        // then check type
        prepareTypeAndInteractionEffects(feature);
        // then check aliases
        prepareAliases(feature);
        // then check annotations
        prepareAnnotations(feature);
        // then check xrefs
        prepareXrefs(feature);
    }

    protected void prepareTypeAndInteractionEffects(F feature){
        IntactDbSynchronizer<CvTerm> typeFinder = getTypeFinder();
        typeFinder.clearCache();
        CvTerm type = feature.getType();
        try {
            feature.setType(typeFinder.synchronize(type));
        } catch (FinderException e) {
            throw new IllegalStateException("Cannot persist the feature because could not synchronize its feature type.");
        }

        // then interaction effects and dependency
        IntactDbSynchronizer<CvTerm> effectFinder = getTopicFinder();
        effectFinder.clearCache();
        CvTerm effect = feature.getInteractionEffect();
        CvTerm dependency = feature.getInteractionDependency();
        try {
            feature.setInteractionEffect(effectFinder.synchronize(effect));
        } catch (FinderException e) {
            throw new IllegalStateException("Cannot persist the feature because could not synchronize its interaction effect.");
        }
        try {
            feature.setInteractionDependency(effectFinder.synchronize(dependency));
        } catch (FinderException e) {
            throw new IllegalStateException("Cannot persist the feature because could not synchronize its interaction dependency.");
        }
    }

    protected void prepareXrefs(F feature) throws FinderException {
        IntactDbSynchronizer<CvTerm> dbFinder = getDbFinder();
        dbFinder.clearCache();
        IntactDbSynchronizer<CvTerm> qualifierFinder = getQualifierFinder();
        qualifierFinder.clearCache();

        List<Xref> xrefsToPersist = new ArrayList<Xref>(feature.getXrefs());
        for (Xref xref : xrefsToPersist){
            FeatureXref featureRef;
            // we have an instance of FeatureXref
            if (xref instanceof FeatureXref){
                featureRef = (FeatureXref) xref;
                if (featureRef.getParent() != null && featureRef.getParent() != feature){
                    feature.getXrefs().remove(featureRef);
                    featureRef = new FeatureXref(xref.getDatabase(), xref.getId(), xref.getVersion(), xref.getQualifier());
                    feature.getXrefs().add(featureRef);
                }
            }
            // we create a brand new feature xref and persist
            else{
                featureRef = new FeatureXref(xref.getDatabase(), xref.getId(), xref.getVersion(), xref.getQualifier());
                feature.getXrefs().remove(xref);
                feature.getXrefs().add(featureRef);
            }

            // pre persist database
            featureRef.setDatabase(dbFinder.synchronize(featureRef.getDatabase()));
            // pre persist qualifier
            if (featureRef.getQualifier() != null){
                featureRef.setQualifier(qualifierFinder.synchronize(featureRef.getQualifier()));
            }

            // check secondaryId value
            if (featureRef.getSecondaryId() != null && featureRef.getSecondaryId().length() > IntactUtils.MAX_ID_LEN){
                featureRef.setSecondaryId(featureRef.getSecondaryId().substring(0,IntactUtils.MAX_ID_LEN));
            }

            // check version value
            if (featureRef.getVersion() != null && featureRef.getVersion().length() > IntactUtils.MAX_DB_RELEASE_LEN){
                featureRef.setVersion(featureRef.getVersion().substring(0,IntactUtils.MAX_DB_RELEASE_LEN));
            }
        }
    }

    protected void prepareAnnotations(F feature) throws FinderException {
        List<Annotation> annotationsToPersist = new ArrayList<Annotation>(feature.getAnnotations());
        for (Annotation annotation : annotationsToPersist){
            FeatureAnnotation featureAnnot;
            // we have an instance of FeatureAnnotation
            if (annotation instanceof FeatureAnnotation){
                featureAnnot = (FeatureAnnotation) annotation;
                if (featureAnnot.getParent() != null && featureAnnot.getParent() != feature){
                    feature.getAnnotations().remove(featureAnnot);
                    featureAnnot = new FeatureAnnotation(annotation.getTopic(), annotation.getValue());
                    feature.getAnnotations().add(featureAnnot);
                }
            }
            // we create a brand new cv annotation and persist
            else{
                featureAnnot = new FeatureAnnotation(annotation.getTopic(), annotation.getValue());
                feature.getAnnotations().remove(annotation);
                feature.getAnnotations().add(featureAnnot);
            }

            // pre persist annotation topic
            featureAnnot.setTopic(synchronize(featureAnnot.getTopic(), IntactUtils.TOPIC_OBJCLASS));

            // check annotation value
            if (featureAnnot.getValue() != null && featureAnnot.getValue().length() > IntactUtils.MAX_DESCRIPTION_LEN){
                featureAnnot.setValue(featureAnnot.getValue().substring(0,IntactUtils.MAX_DESCRIPTION_LEN));
            }
        }
    }

    protected void prepareAliases(IntactCvTerm intactCv) throws FinderException {
        List<Alias> aliasesToPersist = new ArrayList<Alias>(intactCv.getSynonyms());
        for (Alias alias : aliasesToPersist){
            CvTermAlias cvAlias;
            // we have an instance of CvTermAlias
            if (alias instanceof CvTermAlias){
                cvAlias = (CvTermAlias) alias;
                if (cvAlias.getParent() != null && cvAlias.getParent() != intactCv){
                    intactCv.getSynonyms().remove(cvAlias);
                    cvAlias = new CvTermAlias(alias.getType(), alias.getName());
                    intactCv.getSynonyms().add(cvAlias);
                }
            }
            // we create a brand new cv alias and persist
            else{
                cvAlias = new CvTermAlias(alias.getType(), alias.getName());
                intactCv.getSynonyms().remove(alias);
                intactCv.getSynonyms().add(cvAlias);
            }

            // check alias type
            CvTerm aliasType = cvAlias.getType();
            if (aliasType != null){
                // pre persist alias type
                cvAlias.setType(synchronize(cvAlias.getType(), IntactUtils.ALIAS_TYPE_OBJCLASS));
            }

            // check alias name
            if (cvAlias.getName().length() > IntactUtils.MAX_ALIAS_NAME_LEN){
                cvAlias.setName(cvAlias.getName().substring(0,IntactUtils.MAX_ALIAS_NAME_LEN));
            }
        }
    }

    protected void prepareFullName(F feature) {
        // truncate if necessary
        if (feature.getFullName() != null && IntactUtils.MAX_FULL_NAME_LEN < feature.getFullName().length()){
            feature.setFullName(feature.getFullName().substring(0, IntactUtils.MAX_FULL_NAME_LEN));
        }
    }

    protected void preparShortLabel(F feature) {
        // truncate if necessary
        if (IntactUtils.MAX_SHORT_LABEL_LEN < feature.getShortName().length()){
            feature.setShortName(feature.getShortName().substring(0, IntactUtils.MAX_SHORT_LABEL_LEN));
        }
    }

    protected void initialiseObjClass(IntactCvTerm intactCv) {
        if (this.objClass != null){
            intactCv.setObjClass(this.objClass);
        }
    }

    protected CvTerm findOrPersist(CvTerm cvType, String objClass) throws FinderException {
        CvTerm existingInstance = find(cvType, objClass);
        if (existingInstance != null){
            return existingInstance;
        }
        else{
            this.entityManager.persist(cvType);
            return cvType;
        }
    }
}
