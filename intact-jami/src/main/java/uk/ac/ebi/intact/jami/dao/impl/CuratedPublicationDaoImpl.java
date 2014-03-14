package uk.ac.ebi.intact.jami.dao.impl;

import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.CuratedPublicationDao;
import uk.ac.ebi.intact.jami.model.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.extension.IntactCuratedPublication;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.impl.CuratedPublicationSynchronizer;
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
public class CuratedPublicationDaoImpl extends PublicationDaoImpl<IntactCuratedPublication> implements CuratedPublicationDao {

    public CuratedPublicationDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super(IntactCuratedPublication.class, entityManager, context);
    }

    public IntactCuratedPublication getByIMEx(String value) {
        Query query = getEntityManager().createQuery("select distinct p from IntactCuratedPublication p " +
                "join p.dbXrefs as x " +
                "join x.database as dat " +
                "join x.qualifier as qual " +
                "where dat.shortName = :imex "+
                "and qual.shortName = :primary " +
                "and x.id = :id");
        query.setParameter("primary", Xref.IMEX_PRIMARY);
        query.setParameter("imex", Xref.IMEX);
        query.setParameter("id", value);
        List<IntactCuratedPublication> results = query.getResultList();
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

    public Collection<IntactCuratedPublication> getByReleasedDate(Date date) {
        Query query;
        if (date == null){
            query = getEntityManager().createQuery("select p from IntactCuratedPublication p " +
                    "join p.lifecycleEvents as l " +
                    "join l.event as t " +
                    "where l.when is null and t.shortName = :released");
            query.setParameter("released", LifeCycleEvent.RELEASED);
        }
        else{
            query = getEntityManager().createQuery("select p from IntactCuratedPublication p " +
                    "join p.lifecycleEvents as l " +
                    "join l.event as t " +
                    "where l.when = :datePub and t.shortName = :released");
            query.setParameter("datePub",date);
        }
        return query.getResultList();
    }

    public Collection<IntactCuratedPublication> getByCurationDepth(CurationDepth depth, int first, int max){
        Query query = getEntityManager().createQuery("select p from IntactCuratedPublication p "  +
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

    public Collection<IntactCuratedPublication> getByLifecycleEvent(String evtName, int first, int max){
        Query query;
        if (evtName != null){
            query = getEntityManager().createQuery("select p from IntactCuratedPublication p "  +
                    "where p.lifecycleEvents is empty order by p.ac");
        }
        else{
            query = getEntityManager().createQuery("select distinct p from IntactCuratedPublication p "  +
                    "join p.lifecycleEvents as l "  +
                    "join l.event as e "  +
                    "where e.shortName = :name order by p.ac");
            query.setParameter("name", evtName);
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<IntactCuratedPublication> getByStatus(String statusName, int first, int max){
        Query query;
        if (statusName != null){
            query = getEntityManager().createQuery("select p from IntactCuratedPublication p "  +
                    "where p.status is null order by p.ac");
        }
        else{
            query = getEntityManager().createQuery("select p from IntactCuratedPublication p "  +
                    "join p.status as s "  +
                    "where s.shortName = :name order by p.ac");
            query.setParameter("name", statusName);
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<IntactCuratedPublication> getByCurator(String login, int first, int max){
        Query query;
        if (login != null){
            query = getEntityManager().createQuery("select p from IntactCuratedPublication p "  +
                    "where p.currentOwner is null order by p.ac");
        }
        else{
            query = getEntityManager().createQuery("select p from IntactCuratedPublication p "  +
                    "join p.currentOwner as o "  +
                    "where o.login = :name order by p.ac");
            query.setParameter("name", login);
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<IntactCuratedPublication> getByReviewer(String login, int first, int max){
        Query query;
        if (login != null){
            query = getEntityManager().createQuery("select p from IntactCuratedPublication p "  +
                    "where p.currentReviewer is null");
        }
        else{
            query = getEntityManager().createQuery("select p from IntactCuratedPublication p "  +
                    "join p.currentReviewer as c "  +
                    "where c.login = :name order by p.ac");
            query.setParameter("name", login);
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<IntactCuratedPublication> getBySource(String name, int first, int max){
        Query query;
        if (name != null){
            query = getEntityManager().createQuery("select p from IntactCuratedPublication p "  +
                    "where p.source is null order by p.ac");
        }
        else{
            query = getEntityManager().createQuery("select p from IntactCuratedPublication p "  +
                    "join p.source as s "  +
                    "where s.shortName = :name order by p.ac");
            query.setParameter("name", name);
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<IntactCuratedPublication> getByJournal(String value) {
        Query query = getEntityManager().createQuery("select distinct p from IntactCuratedPublication p " +
                "join p.dbAnnotations as a " +
                "join a.topic as t " +
                "where t.shortName = :journalName and a.value = :journal");
        query.setParameter("journalName", Annotation.PUBLICATION_JOURNAL);
        query.setParameter("journal",value);

        return query.getResultList();
    }

    @Override
    public Collection<IntactCuratedPublication> getByJournalLike(String value) {
        Query query = getEntityManager().createQuery("select distinct p from IntactCuratedPublication p  " +
                "join p.dbAnnotations as a " +
                "join a.topic as t " +
                "where t.shortName = :journalName and upper(t.value) like :journal");
        query.setParameter("journalName", Annotation.PUBLICATION_JOURNAL);
        query.setParameter("journal","%"+value.toUpperCase()+"%");
        return query.getResultList();
    }

    @Override
    public Collection<IntactCuratedPublication> getByPublicationDate(Date date) {
        Query query = getEntityManager().createQuery("select distinct p from IntactCuratedPublication p " +
                "join p.dbAnnotations as a " +
                "join a.topic as t " +
                "where t.shortName = :pubDateName and t.value = :datePub");
        query.setParameter("pubDateName", Annotation.PUBLICATION_YEAR);
        query.setParameter("datePub", IntactUtils.YEAR_FORMAT.format(date));
        return query.getResultList();
    }

    @Override
    public IntactDbSynchronizer<Publication, IntactCuratedPublication> getDbSynchronizer() {
        return getSynchronizerContext().getPublicationSynchronizer();
    }
}
