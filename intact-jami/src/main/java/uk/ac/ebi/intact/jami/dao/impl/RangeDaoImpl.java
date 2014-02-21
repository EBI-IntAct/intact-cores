package uk.ac.ebi.intact.jami.dao.impl;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Range;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.RangeDao;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactRange;
import uk.ac.ebi.intact.jami.model.user.Preference;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.impl.RangeSynchronizer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of Range DAO
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */
public class RangeDaoImpl extends AbstractIntactBaseDao<Range, IntactRange> implements RangeDao {

    public RangeDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super(IntactRange.class, entityManager, context);
    }

    public IntactRange getByAc(String ac) {
        return getEntityManager().find(IntactRange.class, ac);
    }

    public Collection<IntactRange> getByFeatureAc(String ac) {
        Query query = getEntityManager().createQuery("select r from IntactRange r " +
                "join r.feature as f " +
                "where f.ac = :ac ");
        query.setParameter("ac",ac);
        return query.getResultList();
    }

    public Collection<IntactRange> getByIsLinkProperty(boolean isLinked) {
        Query query = getEntityManager().createQuery("select r from IntactRange r " +
                "where r.link = :isLinked ");
        query.setParameter("isLinked",isLinked);
        return query.getResultList();
    }

    public Collection<IntactRange> getByStartStatus(String statusName, String statusMI) {
        Query query;
        if (statusMI != null){
            query = getEntityManager().createQuery("select r from IntactRange r " +
                    "join r.start.status as s " +
                    "join s.persistentXrefs as x " +
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
            query = getEntityManager().createQuery("select r from IntactRange r " +
                    "join r.start.status as s " +
                    "where s.shortName = :statusName");
            query.setParameter("statusName", statusName);
        }
        return query.getResultList();
    }

    public Collection<IntactRange> getByEndStatus(String statusName, String statusMI) {
        Query query;
        if (statusMI != null){
            query = getEntityManager().createQuery("select r from IntactRange r " +
                    "join r.end.status as s " +
                    "join s.persistentXrefs as x " +
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
            query = getEntityManager().createQuery("select r from IntactRange r " +
                    "join r.end.status as s " +
                    "where s.shortName = :statusName");
            query.setParameter("statusName", statusName);
        }
        return query.getResultList();
    }

    public Collection<IntactRange> getByStartAndEndStatus(String startName, String startMI, String endName, String endMI) {
        Query query;
        if (startMI != null){
            if (endMI != null){
                query = getEntityManager().createQuery("select r from IntactRange r " +
                        "join r.start.status as s " +
                        "join r.end.status as s2 " +
                        "join s.persistentXrefs as x " +
                        "join x.database as d " +
                        "join x.qualifier as q " +
                        "join s2.persistentXrefs as x2 " +
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
                query = getEntityManager().createQuery("select r from IntactRange r " +
                        "join r.start.status as s " +
                        "join r.end.status as s2 " +
                        "join s.persistentXrefs as x " +
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
            query = getEntityManager().createQuery("select r from IntactRange r " +
                    "join r.start.status as s " +
                    "where s.shortName = :startName");
            query.setParameter("startName", startName);
            if (endMI != null){
                query = getEntityManager().createQuery("select r from IntactRange r " +
                        "join r.start.status as s2 " +
                        "join r.end.status as s " +
                        "join s.persistentXrefs as x " +
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
                query = getEntityManager().createQuery("select r from IntactRange r " +
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

    public Collection<IntactCvTerm> getByResultingSequenceXref(String primaryId) {
        Query query = getEntityManager().createQuery("select r from IntactRange r " +
                "join r.resultingSequence.xrefs as x " +
                "where x.id = :primaryId");
        query.setParameter("primaryId",primaryId);
        return query.getResultList();
    }

    public Collection<IntactCvTerm> getByResultingSequenceXrefLike(String primaryId) {
        Query query = getEntityManager().createQuery("select r from IntactRange r " +
                "join r.resultingSequence.xrefs as x " +
                "where upper(x.id) like :primaryId");
        query.setParameter("primaryId","%"+primaryId.toUpperCase()+"%");
        return query.getResultList();
    }

    public Collection<IntactCvTerm> getByResultingSequenceXref(String dbName, String dbMI, String primaryId) {
        Query query;
        if (dbMI != null){
            query = getEntityManager().createQuery("select r from IntactRange r " +
                    "join r.resultingSequence.xrefs as x " +
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
            query = getEntityManager().createQuery("select r from IntactRange r " +
                    "join cv.persistentXrefs as x " +
                    "join x.database as d " +
                    "where d.shortName = :dbName " +
                    "and x.id = :primary");
            query.setParameter("dbName", dbName);
            query.setParameter("primary", primaryId);
        }
        return query.getResultList();
    }

    public Collection<IntactCvTerm> getByResultingSequenceXrefLike(String dbName, String dbMI, String primaryId) {
        Query query;
        if (dbMI != null){
            query = getEntityManager().createQuery("select r from IntactRange r " +
                    "join r.resultingSequence.xrefs as x " +
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
            query = getEntityManager().createQuery("select r from IntactRange r " +
                    "join cv.persistentXrefs as x " +
                    "join x.database as d " +
                    "where d.shortName = :dbName " +
                    "and upper(x.id) like :primary");
            query.setParameter("dbName", dbName);
            query.setParameter("primary", "%"+primaryId.toUpperCase()+"%");
        }
        return query.getResultList();
    }

    public Collection<IntactCvTerm> getByResultingSequenceXref(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI) {
        Query query;
        if (dbMI != null){
            if (qualifierName == null && qualifierMI == null){
                query = getEntityManager().createQuery("select r from IntactRange r " +
                        "join r.resultingSequence.xrefs as x " +
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
                query = getEntityManager().createQuery("select r from IntactRange r " +
                        "join r.resultingSequence.xrefs as x " +
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
                query = getEntityManager().createQuery("select r from IntactRange r " +
                        "join r.resultingSequence.xrefs as x " +
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
                query = getEntityManager().createQuery("select r from IntactRange r " +
                        "join r.resultingSequence.xrefs as x " +
                        "join x.database as d " +
                        "where d.shortName = :dbName " +
                        "and x.qualifier is null " +
                        "and x.id = :primary");
                query.setParameter("dbName", dbName);
                query.setParameter("primary", primaryId);
            }
            else if (qualifierMI != null){
                query = getEntityManager().createQuery("select r from IntactRange r " +
                        "join r.resultingSequence.xrefs as x " +
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
                query = getEntityManager().createQuery("select r from IntactRange r " +
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

    public Collection<IntactCvTerm> getByResultingSequenceXrefLike(String dbName, String dbMI, String primaryId, String qualifierName, String qualifierMI) {
        Query query;
        if (dbMI != null){
            if (qualifierName == null && qualifierMI == null){
                query = getEntityManager().createQuery("select r from IntactRange r " +
                        "join r.resultingSequence.xrefs as x " +
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
                query = getEntityManager().createQuery("select r from IntactRange r " +
                        "join r.resultingSequence.xrefs as x " +
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
                query = getEntityManager().createQuery("select r from IntactRange r " +
                        "join r.resultingSequence.xrefs as x " +
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
                query = getEntityManager().createQuery("select r from IntactRange r " +
                        "join r.resultingSequence.xrefs as x " +
                        "join x.database as d " +
                        "where d.shortName = :dbName " +
                        "and x.qualifier is null " +
                        "and upper(x.id) like :primary");
                query.setParameter("dbName", dbName);
                query.setParameter("primary", "%"+primaryId.toUpperCase()+"%");
            }
            else if (qualifierMI != null){
                query = getEntityManager().createQuery("select r from IntactRange r " +
                        "join r.resultingSequence.xrefs as x " +
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
                query = getEntityManager().createQuery("select r from IntactRange r " +
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
    public IntactDbSynchronizer<Range, IntactRange> getDbSynchronizer() {
        return getSynchronizerContext().getRangeSynchronizer();
    }
}
