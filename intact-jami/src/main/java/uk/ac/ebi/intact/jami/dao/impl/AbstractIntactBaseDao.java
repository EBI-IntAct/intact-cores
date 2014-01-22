package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.stereotype.Repository;
import uk.ac.ebi.intact.jami.dao.IntactBaseDao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
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
@Repository
public abstract class AbstractIntactBaseDao<T> implements IntactBaseDao<T> {

    @PersistenceContext(unitName = "intact-core")
    private EntityManager entityManager;

    @PersistenceUnit(unitName = "intact-core")
    private EntityManagerFactory entityManagerFactory;

    private Class<T> entityClass;

    protected AbstractIntactBaseDao(){

    }

    public AbstractIntactBaseDao( Class<T> entityClass) {
        if (entityClass == null){
            throw new IllegalArgumentException("Entity class is mandatory");
        }
        this.entityClass = entityClass;
    }

    public AbstractIntactBaseDao( Class<T> entityClass, EntityManager entityManager) {
        this(entityClass);
        this.entityManager = entityManager;
    }

    public EntityManager getEntityManager() {
        if (this.entityManager != null && !this.entityManager.isOpen()) {
            this.entityManager = this.entityManagerFactory.createEntityManager();
        }
        return this.entityManager;
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

    public T update(T objToUpdate) {
        return getEntityManager().merge(objToUpdate);
    }

    public void persist(T objToPersist) {
        getEntityManager().persist(objToPersist);
    }

    public void persistAll(Collection<T> objsToPersist) {
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

    public void merge(T objToReplicate) {
        getEntityManager().merge(objToReplicate);
    }

    public boolean isTransient(T object) {
        return !getEntityManager().contains(object);
    }

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public Class<T> getEntityClass() {
        if (entityClass == null){
            throw new IllegalArgumentException("Entity class is mandatory");
        }
        return this.entityClass;
    }
}
