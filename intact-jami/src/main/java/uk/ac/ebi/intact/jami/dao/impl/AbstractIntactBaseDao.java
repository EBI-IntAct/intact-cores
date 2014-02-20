package uk.ac.ebi.intact.jami.dao.impl;

import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.IntactBaseDao;
import uk.ac.ebi.intact.jami.model.audit.Auditable;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.List;

/**
 * Abstract class for
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */
public abstract class AbstractIntactBaseDao<I,T extends Auditable> implements IntactBaseDao<T> {

    private Class<T> entityClass;
    private IntactDbSynchronizer<I,T> dbSynchronizer;
    private EntityManager entityManager;
    private SynchronizerContext synchronizerContext;

    public AbstractIntactBaseDao( Class<T> entityClass, EntityManager entityManager, SynchronizerContext context) {
        if (entityClass == null){
            throw new IllegalArgumentException("Entity class is mandatory");
        }
        this.entityClass = entityClass;
        if (this.entityManager == null){
            throw new IllegalArgumentException("The entityManager cannot be null");
        }
        this.entityManager = entityManager;
        if (context == null){
            throw new IllegalArgumentException("The Intact database synchronizer context cannot be null");
        }
    }

    public void flush() {
        getEntityManager().flush();
    }

    public List<T> getAll() {
        return getEntityManager().createQuery(this.entityManager.getCriteriaBuilder().
                createQuery(this.entityClass))
                .getResultList();
    }

    public List<T> getAll(int firstResult, int maxResults) {
        return getEntityManager().createQuery(this.entityManager.getCriteriaBuilder().
                createQuery(this.entityClass))
                .setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .getResultList();
    }

    public List<T> getAllSorted(int firstResult, int maxResults, String sortProperty, boolean ascendant) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(getEntityClass());
        Root<T> root = criteria.from(getEntityClass());
        Order order = ascendant ? builder.asc(root.get(sortProperty)) : builder.desc(root.get(sortProperty));
        return this.entityManager.createQuery(criteria.orderBy(order))
                .setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .getResultList();
    }

    public long countAll() {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        criteria.select(builder.count(criteria.from(getEntityClass())));
        return this.entityManager.createQuery(criteria)
                .getSingleResult();
    }

    public T update(T objToUpdate) throws FinderException,PersisterException,SynchronizerException{
        synchronizeObjectProperties(objToUpdate);
        return getEntityManager().merge(objToUpdate);
    }

    public void persist(T objToPersist) throws FinderException,PersisterException,SynchronizerException{
        synchronizeObjectProperties(objToPersist);
        getEntityManager().persist(objToPersist);
    }

    public void persistAll(Collection<T> objsToPersist) throws FinderException,PersisterException,SynchronizerException{
        for (T obj : objsToPersist){
            persist(obj);
        }
    }

    public void delete(T objToDelete) {
        getEntityManager().remove(objToDelete);
    }

    public void deleteAll(Collection<T> objsToDelete) {
        for (T obj : objsToDelete){
            delete(obj);
        }
    }

    public int deleteAll() {
        return getEntityManager().createQuery("delete from "+getEntityClass()).executeUpdate();
    }

    public void refresh(T obj) {
         getEntityManager().refresh(obj);
    }

    public void detach(T objToEvict) {
        getEntityManager().detach(objToEvict);
    }

    public void merge(T objToReplicate) throws FinderException,PersisterException,SynchronizerException{
        synchronizeObjectProperties(objToReplicate);
        getEntityManager().merge(objToReplicate);
    }

    public boolean isTransient(T object) {
        return !getEntityManager().contains(object);
    }

    protected void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public Class<T> getEntityClass() {
        if (entityClass == null){
            throw new IllegalArgumentException("Entity class is mandatory");
        }
        return this.entityClass;
    }

    public IntactDbSynchronizer<I,T> getDbSynchronizer() {
        if (this.dbSynchronizer == null){
            initialiseDbSynchronizer();
        }
        return this.dbSynchronizer;
    }

    public void setDbSynchronizer(IntactDbSynchronizer<I, T> dbSynchronizer) {
        this.dbSynchronizer = dbSynchronizer;
    }

    protected abstract void initialiseDbSynchronizer();

    protected void synchronizeObjectProperties(T objToUpdate) throws PersisterException, FinderException, SynchronizerException {
        getDbSynchronizer().synchronizeProperties(objToUpdate);
    }

    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    protected SynchronizerContext getSynchronizerContext() {
        return synchronizerContext;
    }
}
