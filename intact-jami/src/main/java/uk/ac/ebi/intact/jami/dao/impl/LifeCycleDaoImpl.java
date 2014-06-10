package uk.ac.ebi.intact.jami.dao.impl;

import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.LifeCycleEventDao;
import uk.ac.ebi.intact.jami.model.AbstractLifecycleEvent;
import uk.ac.ebi.intact.jami.model.LifeCycleEvent;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;
import java.util.Date;

/**
 * Implementation of lifecycle dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */
public class LifeCycleDaoImpl<A extends AbstractLifecycleEvent> extends AbstractIntactBaseDao<LifeCycleEvent, A> implements LifeCycleEventDao<A> {

    public LifeCycleDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super((Class<A>)AbstractLifecycleEvent.class, entityManager, context);
    }

    public LifeCycleDaoImpl(Class<A> entityClass, EntityManager entityManager, SynchronizerContext context) {
        super(entityClass, entityManager, context);
    }

    public Collection<A> getByNote(String note, int first, int max) {
        Query query;
        if (note == null){
            query = getEntityManager().createQuery("select l from " + getEntityClass().getSimpleName() + " l where l.note is null order by l.ac");
        }
        else{
            query = getEntityManager().createQuery("select l from " + getEntityClass().getSimpleName() + " l where l.note = :note order by l.ac");
            query.setParameter("note",note);
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<A> getByNoteLike(String note, int first, int max) {
        Query query;
        if (note == null){
            query = getEntityManager().createQuery("select l from " + getEntityClass().getSimpleName() + " l where l.note is null order by l.ac");
        }
        else{
            query = getEntityManager().createQuery("select l from " + getEntityClass().getSimpleName() + " l where upper(l.note) like :note order by l.ac");
            query.setParameter("note","%"+note.toUpperCase()+"%");
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<A> getByEvent(String eventName, int first, int max) {
        Query query = getEntityManager().createQuery("select l from "+getEntityClass().getSimpleName()+" l " +
                "join l.event as e " +
                "where e.shortName = :name order by l.ac");
        query.setParameter("name", eventName);
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<A> getByUser(String user, int first, int max) {
        Query query = getEntityManager().createQuery("select l from " + getEntityClass().getSimpleName() + " l " +
                "join l.who as w " +
                "where w.login = :login order by l.ac");
        query.setParameter("login",user);
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<A> getByDate(Date date, int first, int max) {
        Query query;
        if (date == null){
            query = getEntityManager().createQuery("select l from " + getEntityClass().getSimpleName() + " l where l.when is null order by l.ac");
        }
        else{
            query = getEntityManager().createQuery("select l from " + getEntityClass().getSimpleName() + " l where l.when = :evtDate order by l.ac");
            query.setParameter("evtDate",date);
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    @Override
    public IntactDbSynchronizer getDbSynchronizer() {
        return getSynchronizerContext().getLifecycleSynchronizer(getEntityClass());
    }
}
