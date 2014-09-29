package uk.ac.ebi.intact.jami.synchronizer;

import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMerger;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringLocalObject;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.audit.Auditable;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
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

        // when the object is dirty, we need to synchronize the properties first
        if (isObjectDirty((I)object)){
            // store object in a identity cache so no lazy properties can be called before synchronization
            storeObjectInIdentityCache((I)object, object);
            // synchronize properties
            synchronizeProperties(object);
            // remove object from identity cache as not dirty anymore
            removeObjectInstanceFromIdentityCache((I)object);
            // store in normal cache
            storeInCache((I)object, object, null);
            // persist the object
            persistObject(object);

            // then set userContext
            object.setLocalUserContext(getContext().getUserContext());

            return object;
        }
        // check cache when possible
        else if (isObjectStoredInCache((I) object)){

            T fetched = fetchObjectFromCache((I)object);
            // then set userContext
            fetched.setLocalUserContext(getContext().getUserContext());
            return fetched;
        }
        // store in cache
        storeInCache((I)object, object, null);
        // synchronize properties
        synchronizeProperties(object);
        // persist the object
        persistObject(object);

        // then set userContext
        object.setLocalUserContext(getContext().getUserContext());
        return object;
    }

    public T synchronize(I object, boolean persist) throws FinderException, PersisterException, SynchronizerException {

        if (!this.intactClass.isAssignableFrom(object.getClass())){
            T newObject = null;
            boolean needToSynchronizeProperties = true;
            // check identity cache when possible to avoid internal loops
            if (containsObjectInstance(object)){
                T fetched = fetchMatchingObjectFromIdentityCache(object);
                // then set userContext
                fetched.setLocalUserContext(getContext().getUserContext());
                return fetched;
            }
            // when the object is dirty, we need to synchronize the properties first
            else if (isObjectDirty(object)){
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
                // store object in a identity cache so no lazy properties can be called before synchronization
                storeObjectInIdentityCache(object, newObject);
                // synchronize properties
                synchronizeProperties(newObject);
                // remove object from identity cache as not dirty anymore
                removeObjectInstanceFromIdentityCache(object);
                needToSynchronizeProperties = false;
            }
            // check normal cache when possible. Only objects that are not dirty for the synchronizer can go there
            else if (isObjectStoredInCache(object)){
                T fetched = fetchObjectFromCache(object);
                // then set userContext
                fetched.setLocalUserContext(getContext().getUserContext());
                return fetched;
            }
            else{
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
            }

            // new object to synchronize with db
            return findOrPersist(object, newObject, persist, needToSynchronizeProperties);
        }
        else{
            T intactObject = (T)object;
            Object identifier = extractIdentifier(intactObject);
            boolean needToSynchronizeProperties = true;
            // check identity cache when possible to avoid internal loops
            if (containsObjectInstance(object)){
                T fetched = fetchMatchingObjectFromIdentityCache(object);
                // then set userContext
                fetched.setLocalUserContext(getContext().getUserContext());
                return fetched;
            }
            // when the object is dirty, we need to synchronize the properties first
            else if (isObjectDirty(object)){
                // store object in a identity cache so no lazy properties can be called before synchronization
                storeObjectInIdentityCache(object, intactObject);
                // synchronize properties
                synchronizeProperties(intactObject);
                // remove object from identity cache as not dirty anymore
                removeObjectInstanceFromIdentityCache(object);
                needToSynchronizeProperties = false;
            }
            // check normal cache when possible. Only objects that are not dirty for the synchronizer can go there
            else if (isObjectStoredInCache(object)){
                T fetched = fetchObjectFromCache(object);
                // then set userContext
                fetched.setLocalUserContext(getContext().getUserContext());
                return fetched;
            }

            // detached existing instance or new transient instance
            if (identifier != null && !this.entityManager.contains(intactObject)){
                T merged = mergeExistingInstanceToCurrentSession(intactObject, identifier, needToSynchronizeProperties);
                // then set userContext
                merged.setLocalUserContext(getContext().getUserContext());
                return merged;

            }
            // retrieve and or persist transient instance
            else if (identifier == null){
                // new object to synchronize with db
                return findOrPersist(object, intactObject, persist, needToSynchronizeProperties);
            }
            else{
                // cache object to persist if allowed
                storeInCache(object, intactObject, intactObject);
                // synchronize properties
                if (needToSynchronizeProperties){
                    synchronizeProperties(intactObject);
                }
                // then set userContext
                intactObject.setLocalUserContext(getContext().getUserContext());
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
                T reloadedObject = this.entityManager.getReference(getIntactClass(), identifier);
                deleteRelatedProperties(reloadedObject);
                this.entityManager.remove(reloadedObject);
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

    protected T mergeExistingInstanceToCurrentSession(T intactObject, Object identifier, boolean synchronizeProperties) throws FinderException, PersisterException, SynchronizerException {

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
            // synchronize properties first before merging
            if (synchronizeProperties){
                // cache object to persist if allowed
                storeObjectInIdentityCache((I)intactObject, intactObject);
                synchronizeProperties(intactObject);
                // remove from identity cache
                removeObjectInstanceFromIdentityCache((I) intactObject);
            }
            // merge
            T mergedObject = this.entityManager.merge(intactObject);
            // cache object to persist if allowed
            storeInCache((I)mergedObject, intactObject, mergedObject);
            // second round of snchronization in case we need to initialise collection that were not initialised
            synchronizePropertiesAfterMerge(mergedObject);

            return mergedObject;
        }
    }

    protected void synchronizePropertiesAfterMerge(T mergedObject) throws SynchronizerException, PersisterException, FinderException {
        // do nothing by default
    }

    @Override
    public T convertToPersistentObject(I object) throws SynchronizerException, PersisterException, FinderException {

        // check cache when possible
        if (containsObjectInstance(object)){
            return fetchMatchingObjectFromIdentityCache(object);
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
            storeObjectInIdentityCache(object, newObject);

            // convert properties if not done
            convertPersistableProperties(newObject);

            // new object fully persistable
            return newObject;
        }
        else{
            T intactObject = (T)object;
            // cache object
            storeObjectInIdentityCache(object, intactObject);

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

    protected T findOrPersist(I originalObject, T persistentObject, boolean persist, boolean needToSynchronizeProperties) throws FinderException, PersisterException, SynchronizerException {
        T existingInstance = find((I)persistentObject);
        // cache object to persist if allowed
        storeInCache(originalObject, persistentObject, existingInstance);
        // merge existing persistent instance with the other instance
        if (existingInstance != null){
            // we merge the existing instance with the new instance if possible
            if (getIntactMerger() != null){
                T mergedObject = getIntactMerger().merge(persistentObject, existingInstance);
                // synchronize before persisting
                if (needToSynchronizeProperties){
                    synchronizeProperties(mergedObject);
                }
                // then set userContext
                mergedObject.setLocalUserContext(getContext().getUserContext());
                return mergedObject;
            }
            // we only return the existing instance if no merge allowed
            return existingInstance;
        }
        else{
            // then set userContext
            persistentObject.setLocalUserContext(getContext().getUserContext());
            // synchronize before persisting
            if (needToSynchronizeProperties){
                synchronizeProperties(persistentObject);
            }
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
        // the flushmode commit must be set so the entity manager do not flush when creating queries
        this.entityManager.setFlushMode(FlushModeType.COMMIT);
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
     * @return true if the object instance has already been processed. This expects to find the exact same instance.
     */
    protected abstract boolean containsObjectInstance(I object);

    /**
     * Method that remove the object from the initial identity cache
     * @param object
     */
    protected abstract void removeObjectInstanceFromIdentityCache(I object);

    /**
     *
     * @param object
     * @return the persistable object associated with this object. The persistable object that is returned is NOT synchronized
     * with existing instances in the database
     */
    protected abstract T fetchMatchingObjectFromIdentityCache(I object);

    /**
     * This method will check that all properties of this object are persistable.
     * If not, it will replace them with persistable Hibernate entities when applicable
     * @param object
     */
    protected abstract void convertPersistableProperties(T object) throws SynchronizerException, PersisterException, FinderException;

    /**
     * Stores in a identity cache (onlyu relies on identity of the object instance) (different from cache used for full synchronization) the different states of an object
     * if supported by the synchronizer
     * @param originalObject : the original object instance that we want to persist/update
     * @param persistableObject : the object instance which is a clone of the original object that can be persisted in the database but is not synchronized with
     * ths database
     */
    protected abstract void storeObjectInIdentityCache(I originalObject, T persistableObject);

    /**
     *
     * @param originalObject
     * @return true if the object is dirty and needs to be synchronized before putting anything in the cache
     */
    protected abstract boolean isObjectDirty(I originalObject);
}
