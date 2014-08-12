package uk.ac.ebi.intact.jami.synchronizer;

import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMerger;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringLocalObject;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.audit.Auditable;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;

/**
 * Abstract synchronizer for intact objects
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public abstract class AbstractIntactDbSynchronizer<I, T extends Auditable> implements IntactDbSynchronizer<I,T> {

    private EntityManager entityManager;
    private SynchronizerContext context;
    private Class<? extends T> intactClass;

    private IntactDbMerger<I,T> intactMerger;

    public AbstractIntactDbSynchronizer(SynchronizerContext context, Class<? extends T> intactClass){
        if (context == null){
            throw new IllegalArgumentException("An IntAct database synchronizer needs a non null synchronizer context");
        }
        this.context = context;
        this.entityManager = this.context.getEntityManager();
        if (intactClass == null){
            throw new IllegalArgumentException("An IntAct database synchronizer needs a non null intact class");
        }
        this.intactClass = intactClass;
    }

    public T persist(T object) throws FinderException, PersisterException, SynchronizerException {

        // check cache when possible
        if (isObjectStoredInCache((I) object)){
            return fetchObjectFromCache((I)object);
        }

        // store in cache
        storeInCache((I)object, object, null);

        // synchronize properties
        synchronizeProperties(object);

        // persist the cv
        persistObject(object);

        return object;
    }

    public T synchronize(I object, boolean persist) throws FinderException, PersisterException, SynchronizerException {

        if (!this.intactClass.isAssignableFrom(object.getClass())){
            // check cache when possible. The object should be fully initialised as it is not a hibernate entity
            if (isObjectStoredInCache(object)){
                return fetchObjectFromCache(object);
            }

            T newObject = null;
            try {
                newObject = instantiateNewPersistentInstance(object, this.intactClass);
            } catch (InstantiationException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.intactClass, e);
            } catch (IllegalAccessException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.intactClass, e);
            } catch (InvocationTargetException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.intactClass, e);
            } catch (NoSuchMethodException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.intactClass, e);
            }
            // new object to synchronize with db
            return findOrPersist(object, newObject, persist);
        }
        else{
            T intactObject = (T)object;
            Object identifier = extractIdentifier(intactObject);
            // detached existing instance or new transient instance
            if (identifier != null && !this.entityManager.contains(intactObject)){
                return mergeExistingInstanceToCurrentSession(intactObject, identifier);

            }
            // retrieve and or persist transient instance
            else if (identifier == null){
                // check cache when possible
                if (isObjectStoredInCache(object)){
                    return fetchObjectFromCache(object);
                }
                // new object to synchronize with db
                return findOrPersist(object, intactObject, persist);
            }
            else{
                // check cache when possible
                if (isObjectStoredInCache(object)){
                    return fetchObjectFromCache(object);
                }
                // cache object to persist if allowed
                storeInCache(object, intactObject, intactObject);
                // synchronize properties
                synchronizeProperties(intactObject);
                return intactObject;
            }
        }
    }

    public IntactDbMerger<I,T> getIntactMerger() {
        if (this.intactMerger == null){
            initialiseDefaultMerger();
        }
        return intactMerger;
    }

    public void setIntactMerger(IntactDbMerger<I,T> intactMerger) {
        this.intactMerger = intactMerger;
    }

    public Class<? extends T> getIntactClass() {
        return intactClass;
    }

    public void setIntactClass(Class<? extends T> intactClass) {
        if (intactClass == null){
            throw new IllegalArgumentException("Intact class cannot be null");
        }
        this.intactClass = intactClass;
    }

    public boolean delete(I object) {
        if (!this.intactClass.isAssignableFrom(object.getClass())){
            return false;
        }
        else{
            T intactObject = (T)object;
            Object identifier = extractIdentifier(intactObject);
            // detached existing instance or new transient instance
            if (identifier != null && !this.entityManager.contains(intactObject)){
                deleteRelatedProperties(intactObject);
                this.entityManager.remove(this.entityManager.getReference(getIntactClass(), identifier));
                return true;

            }
            // retrieve and or persist transient instance
            else if (identifier == null){
                return false;
            }
            else{
                deleteRelatedProperties(intactObject);
                this.entityManager.remove(intactObject);
                return true;
            }
        }
    }

    protected void deleteRelatedProperties(T intactObject){
        // does nothing
    }

    protected T mergeExistingInstanceToCurrentSession(T intactObject, Object identifier) throws FinderException, PersisterException, SynchronizerException {
        // is the object in local cache for conversion
        if (containsDetachedOrTransientObject((I) intactObject)){
            return fetchMatchingPersistableObject((I)intactObject);
        }
        // cache object to persist if allowed (avoid internal loop afterwise)
        // different cache than for synchronization
        storeDetachedOrTransientObjectInCache((I) intactObject, intactObject);

        // do not merge existing instance if the merger is a merger ignoring source
        if (getIntactMerger() instanceof IntactDbMergerIgnoringLocalObject){
            T reloaded = getEntityManager().find(getIntactClass(), identifier);
            // the reloaded object is not null and is the one that should be used in the cache because we ignore local changes
            if (reloaded != null){
                // cache object to persist if allowed
                storeInCache((I)reloaded, intactObject, reloaded);
                return reloaded;
            }
            // the reloaded object does not exist which means the object is corrupted? Already deleted?
            else{
                throw new SynchronizerException("The persistent entity "+intactObject.getClass() + " has an identifier "+identifier+" but cannot be found in the database.");
            }
        }
        else{
            // synchronize properties first
            synchronizeProperties(intactObject);
            // merge
            T mergedObject = this.entityManager.merge(intactObject);
            // cache object to persist if allowed
            storeInCache((I)mergedObject, intactObject, mergedObject);

            return mergedObject;
        }
    }

    @Override
    public T convertToPersistentObject(I object) throws SynchronizerException, PersisterException, FinderException {

        // check cache when possible
        if (containsDetachedOrTransientObject(object)){
            return fetchMatchingPersistableObject(object);
        }

        if (!this.intactClass.isAssignableFrom(object.getClass())){
            T newObject = null;
            try {
                newObject = instantiateNewPersistentInstance(object, this.intactClass);
            } catch (InstantiationException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.intactClass, e);
            } catch (IllegalAccessException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.intactClass, e);
            } catch (InvocationTargetException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.intactClass, e);
            } catch (NoSuchMethodException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.intactClass, e);
            }

            // cache
            storeDetachedOrTransientObjectInCache(object, newObject);

            // convert properties if not done
            convertPersistableProperties(newObject);

            // new object fully persistable
            return newObject;
        }
        else{
            T intactObject = (T)object;
            // cache object
            storeDetachedOrTransientObjectInCache(object, intactObject);

            // convert properties
            convertPersistableProperties(intactObject);

            return intactObject;
        }
    }

    protected void initialiseDefaultMerger() {
        this.intactMerger = new IntactDbMergerIgnoringPersistentObject<I, T>(this);
    }

    protected abstract Object extractIdentifier(T object);

    protected abstract T instantiateNewPersistentInstance(I object, Class<? extends T> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    protected T findOrPersist(I originalObject, T persistentObject, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        T existingInstance = find((I)persistentObject);
        // cache object to persist if allowed
        storeInCache(originalObject, persistentObject, existingInstance);
        // merge existing persistent instance with the other instance
        if (existingInstance != null){
            // we merge the existing instance with the new instance if possible
            if (getIntactMerger() != null){
                T mergedObject = getIntactMerger().merge(persistentObject, existingInstance);
                // synchronize before persisting
                synchronizeProperties(mergedObject);
                return mergedObject;
            }
            // we only return the existing instance if no merge allowed
            return existingInstance;
        }
        else{
            // synchronize before persisting
            synchronizeProperties(persistentObject);
            if (persist){
                persistObject(persistentObject);
            }
            return persistentObject;
        }
    }

    protected void persistObject(T existingInstance) {
        this.entityManager.persist(existingInstance);
    }

    /**
     * Stores in cache the different states of an object if supported by the synchronizer
     * @param originalObject : the original object instance that we want to persist/update
     * @param persistentObject : the object instance which is a clone of the original object that can be persisted in the database
     * @param existingInstance : the existing persistent instance in the database that matches the original object
     */
    protected abstract void storeInCache(I originalObject, T persistentObject, T existingInstance);

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected SynchronizerContext getContext() {
        return context;
    }

    /**
     *
     * @param object
     * @return the synchronized/persistent object associated with this object which has been fetched from the
     * cache
     */
    protected abstract T fetchObjectFromCache(I object);

    /**
     *
     * @param object
     * @return true if the object has already been synchronized
     */
    protected abstract boolean isObjectStoredInCache(I object);

    /**
     *
     * @param object
     * @return true if the object has already been converted to a persistable instance but not synchronized with DB.
     * The cache used to convert an object to a persistable instance should be different from the cache used to synchronize the object
     * with existing DB instances
     */
    protected abstract boolean containsDetachedOrTransientObject(I object);

    /**
     *
     * @param object
     * @return the persistable object associated with this object. The persistable object that is returned is NOT synchronized
     * with existing instances in the database
     */
    protected abstract T fetchMatchingPersistableObject(I object);

    /**
     * This method will check that all properties of this object are persistable.
     * If not, it will replace them with persistable Hibernate entities when applicable
     * @param object
     */
    protected abstract void convertPersistableProperties(T object) throws SynchronizerException, PersisterException, FinderException;

    /**
     * Stores in a cache (different from cache used for full synchronization) the different states of an object
     * if supported by the synchronizer
     * @param originalObject : the original object instance that we want to persist/update
     * @param persistableObject : the object instance which is a clone of the original object that can be persisted in the database but is not synchronized with
     * ths database
     */
    protected abstract void storeDetachedOrTransientObjectInCache(I originalObject, T persistableObject);
}
