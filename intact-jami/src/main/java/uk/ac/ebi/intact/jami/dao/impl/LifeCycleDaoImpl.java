package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.stereotype.Repository;
import uk.ac.ebi.intact.jami.dao.LifeCycleEventDao;
import uk.ac.ebi.intact.jami.model.AbstractLifecycleEvent;
import uk.ac.ebi.intact.jami.model.LifeCycleEvent;
import uk.ac.ebi.intact.jami.synchronizer.IntactLifeCycleSynchronizer;

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
@Repository
public class LifeCycleDaoImpl<A extends AbstractLifecycleEvent> extends AbstractIntactBaseDao<LifeCycleEvent, A> implements LifeCycleEventDao<A> {

    public LifeCycleDaoImpl() {
        super((Class<A>)AbstractLifecycleEvent.class);
    }

    public LifeCycleDaoImpl(Class<A> entityClass) {
        super(entityClass);
    }

    public LifeCycleDaoImpl(Class<A> entityClass, EntityManager entityManager) {
        super(entityClass, entityManager);
    }

    public Collection<A> getByNote(String note, int first, int max) {
        Query query;
        if (note == null){
            query = getEntityManager().createQuery("select l from " + getEntityClass() + " l where l.note is null");
        }
        else{
            query = getEntityManager().createQuery("select l from " + getEntityClass() + " l where l.note = :note");
            query.setParameter("note",note);
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<A> getByNoteLike(String note, int first, int max) {
        Query query;
        if (note == null){
            query = getEntityManager().createQuery("select l from " + getEntityClass() + " l where l.note is null");
        }
        else{
            query = getEntityManager().createQuery("select l from " + getEntityClass() + " l where upper(l.note) like :note");
            query.setParameter("note","%"+note.toUpperCase()+"%");
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<A> getByEvent(String eventName, int first, int max) {
        Query query = getEntityManager().createQuery("select l from "+getEntityClass()+" l " +
                "join l.event as e " +
                "where e.shortName = :name");
        query.setParameter("name", eventName);
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<A> getByUser(String user, int first, int max) {
        Query query = getEntityManager().createQuery("select l from " + getEntityClass() + " l " +
                "join l.who as w " +
                "where w.login = :login");
        query.setParameter("login",user);
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<A> getByDate(Date date, int first, int max) {
        Query query;
        if (date == null){
            query = getEntityManager().createQuery("select l from " + getEntityClass() + " l where l.when is null");
        }
        else{
            query = getEntityManager().createQuery("select l from " + getEntityClass() + " l where l.when = :evtDate");
            query.setParameter("evtDate",date);
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<A> getByParentAc(String parentAc) {
        Query query = getEntityManager().createQuery("select l from " + getEntityClass() + " l " +
                "join l.parent as p " +
                "where p.ac = :ac ");
        query.setParameter("ac",parentAc);
        return query.getResultList();
    }

    @Override
    public void setEntityClass(Class<A> entityClass) {
        super.setEntityClass(entityClass);
        initialiseDbSynchronizer();
    }

    @Override
    protected void initialiseDbSynchronizer() {
        super.setDbSynchronizer(new IntactLifeCycleSynchronizer<A>(getEntityManager(), getEntityClass()));
    }
}