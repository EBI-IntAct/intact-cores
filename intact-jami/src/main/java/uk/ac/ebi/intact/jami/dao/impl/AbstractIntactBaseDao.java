package uk.ac.ebi.intact.jami.dao.impl;

import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.IntactBaseDao;
import uk.ac.ebi.intact.jami.model.audit.Auditable;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Abstract class for
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */
public abstract class AbstractIntactBaseDao<I,T extends Auditable> implements IntactBaseDao<T> {

    private Class<T> entityClass;
    private EntityManager entityManager;
    private SynchronizerContext synchronizerContext;

    public AbstractIntactBaseDao( Class<T> entityClass, EntityManager entityManager, SynchronizerContext context) {
        if (entityClass == null){
            throw new IllegalArgumentException("Entity class is mandatory");
        }
        this.entityClass = entityClass;
        if (entityManager == null){
            throw new IllegalArgumentException("The entityManager cannot be null");
        }
        this.entityManager = entityManager;
        if (context == null){
            throw new IllegalArgumentException("The Intact database synchronizer context cannot be null");
        }
        this.synchronizerContext = context;
    }

    public void flush() {
        getEntityManager().flush();
    }

    public List<T> getByQuery(String query, Map<String, Object> queryParameters, int first, int max) {

        Query queryObject = getEntityManager().createQuery(query);
        if (queryParameters != null && !queryParameters.isEmpty()){
            for (Map.Entry<String,Object> param : queryParameters.entrySet()){
                queryObject.setParameter(param.getKey(), param.getValue());
            }
        }
        queryObject.setFirstResult(first);
        queryObject.setMaxResults(max);
        return queryObject.getResultList();
    }

    public long countByQuery(String query, Map<String, Object> queryParameters) {

        Query queryObject = getEntityManager().createQuery(query);
        if (queryParameters != null && !queryParameters.isEmpty()){
            for (Map.Entry<String,Object> param : queryParameters.entrySet()){
                queryObject.setParameter(param.getKey(), param.getValue());
            }
        }
        return (Long)queryObject.getSingleResult();
    }

    public List<T> getAll() {
        return this.entityManager.createQuery("select o from "+getEntityClass().getName()+" o")
                .getResultList();
    }

    public List<T> getAll(String sortProperty, int firstResult, int maxResults) {
        String strQuery = "select o from "+getEntityClass().getName()+" o order by "+sortProperty;
        Query query = this.entityManager.createQuery(strQuery);
        return query
                .setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .getResultList();
    }

    public List<T> getAllSorted(int firstResult, int maxResults, String sortProperty, boolean ascendant) {
        String strQuery = "select o from "+getEntityClass().getName()+" o order by "+sortProperty+" "+((ascendant)? "asc" : "desc");
        Query query = this.entityManager.createQuery(strQuery);
        return query
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
        T objInSync = synchronizeAndUpdateObjectProperties(objToUpdate);
        entityManager.flush();
        return objInSync;
    }

    public void persist(T objToPersist) throws FinderException,PersisterException,SynchronizerException{
        synchronizeAndUpdateObjectProperties(objToPersist);
        entityManager.flush();
    }

    public void persistAll(Collection<T> objsToPersist) throws FinderException,PersisterException,SynchronizerException{
        for (T obj : objsToPersist){
            persist(obj);
        }
    }

    public void delete(T objToDelete) {
        getDbSynchronizer().delete(objToDelete);
        entityManager.flush();
    }

    public void deleteAll(Collection<T> objsToDelete) {
        for (T obj : objsToDelete){
            delete(obj);
        }
    }

    public int deleteAll() {
        return getEntityManager().createQuery("delete from "+getEntityClass().getSimpleName()).executeUpdate();
    }

    public void refresh(T obj) {
         getEntityManager().refresh(obj);
    }

    public void detach(T objToEvict) {
        getEntityManager().detach(objToEvict);
    }

    public void merge(T objToReplicate) throws FinderException,PersisterException,SynchronizerException{
        synchronizeAndUpdateObjectProperties(objToReplicate);
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

    public abstract IntactDbSynchronizer getDbSynchronizer();

    protected void synchronizeObjectProperties(T objToUpdate) throws PersisterException, FinderException, SynchronizerException {
        getDbSynchronizer().synchronizeProperties(objToUpdate);
    }

    protected T synchronizeAndUpdateObjectProperties(T objToUpdate) throws PersisterException, FinderException, SynchronizerException {
        return (T)getDbSynchronizer().synchronize(objToUpdate, true);
    }

    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    protected SynchronizerContext getSynchronizerContext() {
        return synchronizerContext;
    }
}
