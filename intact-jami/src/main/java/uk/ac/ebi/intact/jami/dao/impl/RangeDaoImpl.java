package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.dao.RangeDao;
import uk.ac.ebi.intact.jami.finder.FinderException;
import uk.ac.ebi.intact.jami.finder.IntactCvTermSynchronizer;
import uk.ac.ebi.intact.jami.finder.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.model.extension.IntactPosition;
import uk.ac.ebi.intact.jami.model.extension.IntactRange;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

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
@Repository
public class RangeDaoImpl extends AbstractIntactBaseDao<IntactRange> implements RangeDao{

    private IntactDbSynchronizer<CvTerm> statusFinder;

    public RangeDaoImpl() {
        super(IntactRange.class);
    }

    public RangeDaoImpl(EntityManager entityManager) {
        super(IntactRange.class, entityManager);
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

    public IntactDbSynchronizer<CvTerm> getStatusFinder() {
        if (this.statusFinder == null){
            this.statusFinder = new IntactCvTermSynchronizer(getEntityManager(), IntactUtils.RANGE_STATUS_OBJCLASS);
        }
        return this.statusFinder;
    }

    public void setStatusFinder(IntactDbSynchronizer<CvTerm> statusFinder) {
        this.statusFinder = statusFinder;
    }

    @Override
    public void merge(IntactRange objToReplicate) {
        prepareRangeStatus(objToReplicate);
        super.merge(objToReplicate);
    }

    @Override
    public void persist(IntactRange  objToPersist) {
        prepareRangeStatus(objToPersist);
        super.persist(objToPersist);
    }

    @Override
    public IntactRange update(IntactRange  objToUpdate) {
        prepareRangeStatus(objToUpdate);
        return super.update(objToUpdate);
    }

    protected void prepareRangeStatus(IntactRange objToPersist) {
        IntactDbSynchronizer<CvTerm> statusFinder = getStatusFinder();
        statusFinder.clearCache();

        // prepare start position
        IntactPosition start;
        if (!(objToPersist.getStart() instanceof IntactPosition)){
            start = new IntactPosition(objToPersist.getStart().getStatus(), objToPersist.getStart().getStart(), objToPersist.getStart().getEnd());
        }
        else{
            start = (IntactPosition)objToPersist.getStart();
        }
        // prepare start status
        CvTerm startStatus = start.getStatus();
        try {
            start.setStatus(statusFinder.synchronize(startStatus));
        } catch (FinderException e) {
            throw new IllegalStateException("Cannot persist the range because could not synchronize its start status.");
        }

        // prepare end position
        IntactPosition end;
        if (!(objToPersist.getEnd() instanceof IntactPosition)){
            end = new IntactPosition(objToPersist.getEnd().getStatus(), objToPersist.getEnd().getStart(), objToPersist.getEnd().getEnd());
        }
        else{
            end = (IntactPosition)objToPersist.getEnd();
        }
        // prepare end status
        CvTerm endStatus = end.getStatus();
        try {
            end.setStatus(statusFinder.synchronize(endStatus));
        } catch (FinderException e) {
            throw new IllegalStateException("Cannot persist the range because could not synchronize its start status.");
        }

        objToPersist.setPositions(start, end);
    }
}
