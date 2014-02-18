package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.dao.XrefDao;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactXref;
import uk.ac.ebi.intact.jami.synchronizer.impl.IntactXrefSynchronizer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of Xref DAO
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23/01/14</pre>
 */
@Repository
public class XrefDaoImpl<X extends AbstractIntactXref> extends AbstractIntactBaseDao<Xref, X> implements XrefDao<X> {

    public XrefDaoImpl() {
        super((Class<X>)AbstractIntactXref.class);
    }

    public XrefDaoImpl(Class<X> entityClass) {
        super(entityClass);
    }

    public XrefDaoImpl(Class<X> entityClass, EntityManager entityManager) {
        super(entityClass, entityManager);
    }

    public Collection<X> getByPrimaryId(String value, String version) {
        Query query;
        if (version == null){
            query = getEntityManager().createQuery("select x from " + getEntityClass() + " x where x.id =:primary");
            query.setParameter("primary",value);
        }
        else{
            query = getEntityManager().createQuery("select x from " + getEntityClass() + " x where x.id =:primary and x.version = :version");
            query.setParameter("primary",value);
            query.setParameter("version",version);
        }
        return query.getResultList();
    }

    public Collection<X> getByPrimaryIdLike(String value, String version) {
        Query query;
        if (version == null){
            query = getEntityManager().createQuery("select x from " + getEntityClass() + " x where upper(x.id) like :primary");
            query.setParameter("primary","%"+value.toUpperCase()+"%");
        }
        else{
            query = getEntityManager().createQuery("select x from " + getEntityClass() + " x where upper(x.id) like :primary and x.version = :version");
            query.setParameter("primary","%"+value.toUpperCase()+"%");
            query.setParameter("version",version);
        }
        return query.getResultList();
    }

    public Collection<X> getByDatabase(String dbName, String dbMI) {
        Query query;
        if (dbMI != null){
            query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                    "join x.database as dat " +
                    "join dat.persistentXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", dbMI);
        }
        else{
            query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                    "join x.database as d " +
                    "where d.shortName = :dbName");
            query.setParameter("dbName", dbName);
        }
        return query.getResultList();
    }

    public Collection<X> getByQualifier(String qualifierName, String qualifierMI) {
        Query query;
        if (qualifierName == null && qualifierMI == null){
            query = getEntityManager().createQuery("select x from "+getEntityClass()+" x where x.qualifier is null");
        }
        else if (qualifierMI != null){
            query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                    "join x.qualifier as qual " +
                    "join qual.persistentXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", qualifierMI);
        }
        else{
            query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                    "join x.qualifier as q " +
                    "where q.shortName = :qName");
            query.setParameter("qName", qualifierName);
        }
        return query.getResultList();
    }

    public Collection<X> getByDatabaseAndQualifier(String dbName, String dbMI, String qualifierName, String qualifierMI) {
        Query query;
        if (dbMI != null){
            if (qualifierName == null && qualifierMI == null){
                query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                        "join x.database as dat " +
                        "join dat.persistentXrefs as xref " +
                        "join xref.database as d " +
                        "join xref.qualifier as q " +
                        "where x.qualifier is null " +
                        "and (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and xref.id = :mi");
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", dbMI);
            }
            else if (qualifierMI != null){
                query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
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
                        "and xref2.id = :mi2");
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", dbMI);
                query.setParameter("mi2", qualifierMI);
            }
            else{
                query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                        "join x.database as dat " +
                        "join x.qualifier as qual " +
                        "join dat.persistentXrefs as xref " +
                        "join xref.database as d " +
                        "join xref.qualifier as q " +
                        "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and xref.id = :mi " +
                        "and qual.shortName = :qName");
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", dbMI);
                query.setParameter("qName", qualifierName);
            }
        }
        else{
            if (qualifierName == null && qualifierMI == null){
                query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                        "join x.database as d " +
                        "where d.shortName = :dbName " +
                        "and x.qualifier is null");
                query.setParameter("dbName", dbName);
            }
            else if (qualifierMI != null){
                query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                        "join x.database as dat " +
                        "join x.qualifier as qual " +
                        "join qual.persistentXrefs as xref " +
                        "join xref.database as d " +
                        "join xref.qualifier as q " +
                        "where dat.shortName = :dbName " +
                        "and (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and xref.id = :mi");
                query.setParameter("dbName", dbName);
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", qualifierMI);
            }
            else{
                query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                        "join x.database as d " +
                        "join x.qualifier as q " +
                        "where d.shortName = :dbName " +
                        "and q.shortName = :qName");
                query.setParameter("dbName", dbName);
                query.setParameter("qName", qualifierName);
            }
        }
        return query.getResultList();
    }

    public Collection<X> getByDatabaseAndPrimaryId(String dbName, String dbMI, String id, String version) {
        Query query;
        if (dbMI != null){
            query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                    "join x.database as dat " +
                    "join dat.persistentXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi " +
                    "and x.id = :primary"+(version != null ? " and x.version = :version" : ""));
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", dbMI);
            query.setParameter("primary", id);
            if (version != null){
                query.setParameter("version", version);
            }
        }
        else{
            query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                    "join x.database as d " +
                    "where d.shortName = :dbName " +
                    "and x.id = :primary"+(version != null ? " and x.version = :version" : ""));
            query.setParameter("dbName", dbName);
            query.setParameter("primary", id);
            if (version != null){
                query.setParameter("version", version);
            }
        }
        return query.getResultList();
    }

    public Collection<X> getByDatabaseAndPrimaryIdLike(String dbName, String dbMI, String id, String version) {
        Query query;
        if (dbMI != null){
            query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                    "join x.database as dat " +
                    "join dat.persistentXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi " +
                    "and upper(x.id) like :primary"+(version != null ? " and x.version = :version" : ""));
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", dbMI);
            query.setParameter("primary", "%"+id.toUpperCase()+"%");
            if (version != null){
                query.setParameter("version", version);
            }
        }
        else{
            query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                    "join x.database as d " +
                    "where d.shortName = :dbName " +
                    "and upper(x.id) like :primary"+(version != null ? " and x.version = :version" : ""));
            query.setParameter("dbName", dbName);
            query.setParameter("primary", "%"+id.toUpperCase()+"%");
            if (version != null){
                query.setParameter("version", version);
            }
        }
        return query.getResultList();
    }

    public Collection<X> getByQualifierAndPrimaryId(String qualifierName, String qualifierMI, String id, String version) {
        Query query;
        if (qualifierMI == null && qualifierName == null){
            query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                    "where x.qualifier is null " +
                    "and x.id = :primary"+(version != null ? " and x.version = :version" : ""));
            query.setParameter("primary", id);
            if (version != null){
                query.setParameter("version", version);
            }
        }
        else if (qualifierMI != null){
            query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                    "join x.qualifier as qua " +
                    "join qua.persistentXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi " +
                    "and x.id = :primary"+(version != null ? " and x.version = :version" : ""));
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", qualifierMI);
            query.setParameter("primary", id);
            if (version != null){
                query.setParameter("version", version);
            }
        }
        else{
            query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                    "join x.qualifier as q " +
                    "where q.shortName = :dbName " +
                    "and x.id = :primary"+(version != null ? " and x.version = :version" : ""));
            query.setParameter("qName", qualifierName);
            query.setParameter("primary", id);
            if (version != null){
                query.setParameter("version", version);
            }
        }
        return query.getResultList();
    }

    public Collection<X> getByQualifierAndPrimaryIdLike(String qualifierName, String qualifierMI, String id, String version) {
        Query query;
        if (qualifierMI == null && qualifierName == null){
            query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                    "where x.qualifier is null " +
                    "and upper(x.id) like :primary"+(version != null ? " and x.version = :version" : ""));
            query.setParameter("primary", "%"+id.toUpperCase()+"%");
            if (version != null){
                query.setParameter("version", version);
            }
        }
        else if (qualifierMI != null){
            query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                    "join x.qualifier as qua " +
                    "join qua.persistentXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi " +
                    "and upper(x.id) like :primary"+(version != null ? " and x.version = :version" : ""));
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", qualifierMI);
            query.setParameter("primary", "%"+id.toUpperCase()+"%");
            if (version != null){
                query.setParameter("version", version);
            }
        }
        else{
            query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                    "join x.qualifier as q " +
                    "where q.shortName = :dbName " +
                    "and upper(x.id) like :primary"+(version != null ? " and x.version = :version" : ""));
            query.setParameter("qName", qualifierName);
            query.setParameter("primary", "%"+id.toUpperCase()+"%");
            if (version != null){
                query.setParameter("version", version);
            }
        }
        return query.getResultList();
    }

    public Collection<X> getByDatabasePrimaryIdAndQualifier(String dbName, String dbMI, String id, String version, String qualifierName, String qualifierMI) {
        Query query;
        if (dbMI != null){
            if (qualifierName == null && qualifierMI == null){
                query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                        "join x.database as dat " +
                        "join dat.persistentXrefs as xref " +
                        "join xref.database as d " +
                        "join xref.qualifier as q " +
                        "where x.qualifier is null " +
                        "and (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and xref.id = :mi " +
                        "and x.id = :primary"+(version != null ? " and x.version = :version" : ""));
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", dbMI);
                query.setParameter("primary", id);
                if (version != null){
                    query.setParameter("version", version);
                }
            }
            else if (qualifierMI != null){
                query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
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
                        "and x.id = :primary"+(version != null ? " and x.version = :version" : ""));
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", dbMI);
                query.setParameter("mi2", qualifierMI);
                query.setParameter("primary", id);
                if (version != null){
                    query.setParameter("version", version);
                }
            }
            else{
                query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                        "join x.database as dat " +
                        "join x.qualifier as qual " +
                        "join dat.persistentXrefs as xref " +
                        "join xref.database as d " +
                        "join xref.qualifier as q " +
                        "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and xref.id = :mi " +
                        "and qual.shortName = :qName " +
                        "and x.id = :primary"+(version != null ? " and x.version = :version" : ""));
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", dbMI);
                query.setParameter("qName", qualifierName);
                query.setParameter("primary", id);
                if (version != null){
                    query.setParameter("version", version);
                }
            }
        }
        else{
            if (qualifierName == null && qualifierMI == null){
                query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                        "join x.database as d " +
                        "where d.shortName = :dbName " +
                        "and x.qualifier is null " +
                        "and x.id = :primary"+(version != null ? " and x.version = :version" : ""));
                query.setParameter("dbName", dbName);
                query.setParameter("primary", id);
                if (version != null){
                    query.setParameter("version", version);
                }
            }
            else if (qualifierMI != null){
                query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                        "join x.database as dat " +
                        "join x.qualifier as qual " +
                        "join qual.persistentXrefs as xref " +
                        "join xref.database as d " +
                        "join xref.qualifier as q " +
                        "where dat.shortName = :dbName " +
                        "and (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and xref.id = :mi " +
                        "and x.id = :primary"+(version != null ? " and x.version = :version" : ""));
                query.setParameter("dbName", dbName);
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", qualifierMI);
                query.setParameter("primary", id);
                if (version != null){
                    query.setParameter("version", version);
                }
            }
            else{
                query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                        "join x.database as d " +
                        "join x.qualifier as q " +
                        "where d.shortName = :dbName " +
                        "and q.shortName = :qName " +
                        "and x.id = :primary"+(version != null ? " and x.version = :version" : ""));
                query.setParameter("dbName", dbName);
                query.setParameter("qName", qualifierName);
                query.setParameter("primary", id);
                if (version != null){
                    query.setParameter("version", version);
                }
            }
        }
        return query.getResultList();
    }

    public Collection<X> getByDatabasePrimaryIdLikeAndQualifier(String dbName, String dbMI, String id, String version, String qualifierName, String qualifierMI) {
        Query query;
        if (dbMI != null){
            if (qualifierName == null && qualifierMI == null){
                query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                        "join x.database as dat " +
                        "join dat.persistentXrefs as xref " +
                        "join xref.database as d " +
                        "join xref.qualifier as q " +
                        "where x.qualifier is null " +
                        "and (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and xref.id = :mi " +
                        "and upper(x.id) like :primary"+(version != null ? " and x.version = :version" : ""));
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", dbMI);
                query.setParameter("primary", "%"+id.toUpperCase()+"%");
                if (version != null){
                    query.setParameter("version", version);
                }
            }
            else if (qualifierMI != null){
                query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
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
                        "and upper(x.id) like :primary"+(version != null ? " and x.version = :version" : ""));
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", dbMI);
                query.setParameter("mi2", qualifierMI);
                query.setParameter("primary", "%"+id.toUpperCase()+"%");
                if (version != null){
                    query.setParameter("version", version);
                }
            }
            else{
                query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                        "join x.database as dat " +
                        "join x.qualifier as qual " +
                        "join dat.persistentXrefs as xref " +
                        "join xref.database as d " +
                        "join xref.qualifier as q " +
                        "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and xref.id = :mi " +
                        "and qual.shortName = :qName " +
                        "and upper(x.id) like :primary"+(version != null ? " and x.version = :version" : ""));
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", dbMI);
                query.setParameter("qName", qualifierName);
                query.setParameter("primary", "%"+id.toUpperCase()+"%");
                if (version != null){
                    query.setParameter("version", version);
                }
            }
        }
        else{
            if (qualifierName == null && qualifierMI == null){
                query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                        "join x.database as d " +
                        "where d.shortName = :dbName " +
                        "and x.qualifier is null " +
                        "and upper(x.id) like :primary"+(version != null ? " and x.version = :version" : ""));
                query.setParameter("dbName", dbName);
                query.setParameter("primary", "%"+id.toUpperCase()+"%");
                if (version != null){
                    query.setParameter("version", version);
                }
            }
            else if (qualifierMI != null){
                query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                        "join x.database as dat " +
                        "join x.qualifier as qual " +
                        "join qual.persistentXrefs as xref " +
                        "join xref.database as d " +
                        "join xref.qualifier as q " +
                        "where dat.shortName = :dbName " +
                        "and (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and xref.id = :mi " +
                        "and upper(x.id) like :primary"+(version != null ? " and x.version = :version" : ""));
                query.setParameter("dbName", dbName);
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", qualifierMI);
                query.setParameter("primary", "%"+id.toUpperCase()+"%");
                if (version != null){
                    query.setParameter("version", version);
                }
            }
            else{
                query = getEntityManager().createQuery("select x from "+getEntityClass()+" x " +
                        "join x.database as d " +
                        "join x.qualifier as q " +
                        "where d.shortName = :dbName " +
                        "and q.shortName = :qName " +
                        "and upper(x.id) like :primary"+(version != null ? " and x.version = :version" : ""));
                query.setParameter("dbName", dbName);
                query.setParameter("qName", qualifierName);
                query.setParameter("primary", "%"+id.toUpperCase()+"%");
                if (version != null){
                    query.setParameter("version", version);
                }
            }
        }
        return query.getResultList();
    }

    public Collection<X> getByParentAc(String parentAc) {
        Query query = getEntityManager().createQuery("select x from " + getEntityClass() + " x " +
                "join x.parent as p " +
                "where p.ac = :ac ");
        query.setParameter("ac",parentAc);
        return query.getResultList();
    }

    @Override
    public void setEntityClass(Class<X> entityClass) {
        super.setEntityClass(entityClass);
        initialiseDbSynchronizer();
    }

    @Override
    protected void initialiseDbSynchronizer() {
        super.setDbSynchronizer(new IntactXrefSynchronizer<X>(getEntityManager(), getEntityClass()));
    }
}
