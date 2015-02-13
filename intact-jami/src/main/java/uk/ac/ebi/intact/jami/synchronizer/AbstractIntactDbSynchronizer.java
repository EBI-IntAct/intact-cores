package uk.ac.ebi.intact.jami.synchronizer;

import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMerger;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringLocalObject;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.IntactPrimaryObject;
import uk.ac.ebi.intact.jami.model.audit.Auditable;
import uk.ac.ebi.intact.jami.synchronizer.listener.DbSynchronizerListener;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

/**
 * Abstract synchronizer for intact objects
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public abstract class AbstractIntactDbSynchronizer<I, T extends Auditable> implements IntactDbSynchronizer<I,T> {
    private static final Logger LOGGER = Logger.getLogger("AbstractIntactDbSynchronizer");

    private EntityManager entityManager;
    private SynchronizerContext context;
    private Class<? extends T> intactClass;

    private IntactDbMerger<I,T> intactMerger;

    private DbSynchronizerListener listener;

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
        // set flush mode to commit so queries do not trigger flush
        FlushModeType mode = initialiseEntityManagerFlushType();

        // when the object is dirty, we need to synchronize the properties first
        if (isObjectPartiallyInitialised((I) object)){
            // synchronise properties of dirty object
            synchronizePartiallyInitialisedProperties((I) object, object);
            // store in normal cache
            storeInCache((I)object, object, null);
            // then set userContext
            object.setLocalUserContext(getContext().getUserContext());
            // persist the object
            persistObject(object);

            // reset entity manager flushMode
            resetEntityManagerFlushType(mode);
            return object;
        }
        // check cache when possible
        else if (isObjectStoredInCache((I) object)){
            // process object from cache
            return processCachedObject((I)object, object, mode, true);
        }

        // store in cache
        storeInCache((I)object, object, null);
        // synchronize properties
        synchronizePartiallyInitialisedProperties((I)object, object);
        // then set userContext
        object.setLocalUserContext(getContext().getUserContext());
        // persist the object
        persistObject(object);

        // reset entity manager flushMode
        resetEntityManagerFlushType(mode);
        return object;
    }

    public T synchronize(I object, boolean persist) throws FinderException, PersisterException, SynchronizerException {

        // set flush mode to commit so queries do not trigger flush
        FlushModeType mode = initialiseEntityManagerFlushType();

        // check identity cache when possible to avoid internal loops.
        // This can happen if an object refers to itself in one of its properties
        if (containsObjectInstance(object)){
            return processCachedObjectInstance(object, mode);
        }
        // check that the object to synchronize is a proper hibernate entity supported by this synchronizer. This allows to persist basic JAMI
        // objects.
        // If not, create a new hibernate entity and clone properties
        else if (!this.intactClass.isAssignableFrom(object.getClass())){
            // create a new hibernate entity from the object to synchronize
            T newObject = createIntactEntityFrom(object);
            // boolean value to know if properties need to be synchronized as well
            boolean needToSynchronizeProperties = true;
            // when the object is partially initialised (some objects and collections are detached and not initialised), we need to synchronize the properties first
            if (isObjectPartiallyInitialised(object)){
                // synchronize the properties of the object which is partially initialised
                synchronizePartiallyInitialisedProperties(object, newObject);
                // no needs to synchronize again the properties
                needToSynchronizeProperties = false;
            }

            // check business cache when possible. Only objects that are not partially initialised for the synchronizer can go there
            if (isObjectStoredInCache(object)){
                // retrieve object in cache and merge it with current object if necessary
                return processCachedObject(object,newObject, mode, needToSynchronizeProperties);
            }

            // no cached object, process the transient instance and synchronize with database
            return processTransientObject(object, persist, mode, newObject, needToSynchronizeProperties);
        }
        else{
            // the object to synchronize is already a managed entity
            T intactObject = (T)object;
            // get db identifier for this object
            Object identifier = extractIdentifier(intactObject);
            // boolean value to know if properties need to be synchronized as well
            boolean needToSynchronizeProperties = true;
            // when the object is dirty, we need to synchronize the properties first
            if (isObjectPartiallyInitialised(object)){
                // synchronize the properties of the dirty object
                synchronizePartiallyInitialisedProperties(object, intactObject);
                // no needs to synchronize again the properties
                needToSynchronizeProperties = false;
            }

            // detached existing instance which need to be reattached to the session
            if (identifier != null && !this.entityManager.contains(intactObject)){
                // merge and re-attach to existing session
                T merged = mergeExistingInstanceToCurrentSession(intactObject, identifier, needToSynchronizeProperties, mode, persist);
                // then set userContext
                merged.setLocalUserContext(getContext().getUserContext());

                // reinit flushmode
                resetEntityManagerFlushType(mode);
                return merged;

            }
            // check normal cache when possible. Only objects that are not dirty for the synchronizer can go there
            else if (isObjectStoredInCache(object)){
                return processCachedObject(object, intactObject, mode, needToSynchronizeProperties);
            }
            // retrieve and/or persist transient instance
            else if (identifier == null){
                // no cached object, process the transient instance and synchronize with database
                return processTransientObject(object, persist, mode, intactObject, needToSynchronizeProperties);
            }
            // persisted instance attached to the session
            else{
                // cache object to persist if allowed
                storeInCache(object, intactObject, intactObject);
                // synchronize properties if needed
                if (needToSynchronizeProperties){
                    synchronizePartiallyInitialisedProperties(object, intactObject);
                }
                // then set userContext
                intactObject.setLocalUserContext(getContext().getUserContext());

                // reinit flushmode
                resetEntityManagerFlushType(mode);
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
        // the object is not even an instance from the database so we do nothing
        if (!this.intactClass.isAssignableFrom(object.getClass())){
            return false;
        }
        // the object is a managed entity which needs to be processed for deletion
        else{
            T intactObject = (T)object;
            // get database identifier for this object
            Object identifier = extractIdentifier(intactObject);
            // detached existing instance
            if (identifier != null && !this.entityManager.contains(intactObject)){
                // retrieve existing instance
                T reloadedObject = this.entityManager.find(getIntactClass(), identifier);
                // delete entity and process its properties
                processEntityToDelete(reloadedObject);
                return true;

            }
            // the object is not even an instance persisted in the database so we do nothing
            else if (identifier == null){
                return false;
            }
            // the object is attached to a session
            else{
                // delete entity and process its properties
                processEntityToDelete(intactObject);
                return true;
            }
        }
    }

    @Override
    public T convertToPersistentObject(I object) throws SynchronizerException, PersisterException, FinderException {

        // check cache when possible
        if (containsObjectInstance(object)){
            return fetchMatchingObjectFromIdentityCache(object);
        }

        // check if object is manageable by this synchronizer
        if (!this.intactClass.isAssignableFrom(object.getClass())){
            // create a new copy manageable by this synchronizer
            T newObject = createIntactEntityFrom(object);
            // process persistable object and convert its properties
            convertProperties(object, newObject);
            // new object fully persistable
            return newObject;
        }
        // the object is managed by this synchronizer
        else{
            T intactObject = (T)object;
            // process persistable object and convert its properties
            convertProperties(object, intactObject);
            return intactObject;
        }
    }

    /**
     * Method called on object attached to a session and to delete.
     * The method will process the properties of the object before deleting it.
     * @param persistedObject : object to delete attached to the session
     */
    protected void processEntityToDelete(T persistedObject) {
        // process related properties
        deleteRelatedProperties(persistedObject);
        // remove instance to be deleted
        this.entityManager.remove(persistedObject);
        if (listener != null
                && persistedObject instanceof IntactPrimaryObject){
            listener.onDeleted((IntactPrimaryObject)persistedObject);
        }
    }

    /**
     * Process properties of an object about to be deleted
     * @param intactObject : object to delete attached to the session
     */
    protected void deleteRelatedProperties(T intactObject){
        // does nothing
    }

    /**
     *
     * @param intactObject : db entity which is detached from the session
     * @param identifier : db identifier for this entty
     * @param synchronizeProperties : true if the properties need to be synchronized
     * @param persist: true if the object is still transient and needs to be persisted
     * @return  the merged entity attached to the session
     * @throws FinderException
     * @throws PersisterException
     * @throws SynchronizerException
     */
    protected T mergeExistingInstanceToCurrentSession(T intactObject, Object identifier, boolean synchronizeProperties, FlushModeType mode,
                                                      boolean persist) throws FinderException,
            PersisterException, SynchronizerException {

        // reload existing instance from DB
        T reloaded = getEntityManager().find(getIntactClass(), identifier);
        // do not merge existing instance with db instance if the merger is a merger ignoring source. Just return the existing instance in the DB
        if (getIntactMerger() instanceof IntactDbMergerIgnoringLocalObject){
            // the reloaded object is not null and is the one that should be used in the cache because we ignore local changes
            if (reloaded != null){
                // cache object to persist if allowed
                storeInCache((I)intactObject, intactObject, reloaded);
                if (listener != null
                        && intactObject instanceof IntactPrimaryObject){
                    listener.onMerged((IntactPrimaryObject)intactObject, (IntactPrimaryObject)reloaded);
                }
                return reloaded;
            }
            // the reloaded object does not exist which means the object is in fact transient
            else{
                LOGGER.log(java.util.logging.Level.WARNING, "The persistent entity "+intactObject.getClass() + " has an identifier "+identifier
                        +" but cannot be found in the database. It is considered as transient and will be persisted");
                // no cached object, process the transient instance and synchronize with database
                // WARNING, we need ti reset the id to null
                resetObjectIdentifier(intactObject);
                if (isObjectStoredInCache((I)intactObject)){
                    return processCachedObject((I)intactObject, intactObject, mode, synchronizeProperties);
                }
                else {
                    return processTransientObject((I)intactObject, persist, mode, intactObject, synchronizeProperties);
                }
            }
        }
        // merge existing instance with whatever exists in the database
        else{
            // synchronize properties first before merging
            if (synchronizeProperties){
                // cache object to persist if allowed
                synchronizePartiallyInitialisedProperties((I) intactObject, intactObject);
            }
            // merge or persist
            T mergedObject = intactObject;
            if (reloaded != null){
                mergedObject = this.entityManager.merge(intactObject);
            }
            else{
                LOGGER.log(java.util.logging.Level.WARNING, "The persistent entity "+intactObject.getClass() + " has an identifier "+identifier
                        +" but cannot be found in the database. It is considered as transient and will be persisted");
                // no cached object, process the transient instance and synchronize with database
                // WARNING, we need ti reset the id to null
                resetObjectIdentifier(intactObject);
                if (isObjectStoredInCache((I)intactObject)){
                    return processCachedObject((I)intactObject, intactObject, mode, synchronizeProperties);
                }
                else {
                    return processTransientObject((I)intactObject, persist, mode, intactObject, synchronizeProperties);
                }
            }

            if (listener != null
                    && intactObject instanceof IntactPrimaryObject){
                listener.onMerged((IntactPrimaryObject)intactObject, (IntactPrimaryObject)mergedObject);
            }
            // cache object to persist if allowed
            storeInCache((I)mergedObject, intactObject, mergedObject);
            // second round of synchronization in case we need to initialise collection that were not initialised
            synchronizePropertiesAfterMerge(mergedObject);

            return mergedObject;
        }
    }

    /**
     * Reset object identity to null
     * @param intactObject
     */
    protected abstract void resetObjectIdentifier(T intactObject);

    /**
     * Synchronize the properties which need to be synchronized after a merge with an existing instance
     * @param mergedObject : the merged object attached to the session
     * @throws SynchronizerException
     * @throws PersisterException
     * @throws FinderException
     */
    protected void synchronizePropertiesAfterMerge(T mergedObject) throws SynchronizerException, PersisterException, FinderException {
        // do nothing by default
    }

    /**
     * Method that will convert the properties of an object not persisted in the database
     * @param object : original object to convert
     * @param newObject : matching persistable entity
     * @throws SynchronizerException
     * @throws PersisterException
     * @throws FinderException
     */
    protected void convertProperties(I object, T newObject) throws SynchronizerException, PersisterException, FinderException {
        // cache
        storeObjectInIdentityCache(object, newObject);
        // convert properties if not done
        convertPersistableProperties(newObject);
    }

    /**
     * Create default intact db merger
     */
    protected void initialiseDefaultMerger() {
        this.intactMerger = new IntactDbMergerIgnoringPersistentObject<I, T>(this);
    }

    /**
     * Method which find an existing intact instance matching the new object or persist the new object
     * @param originalObject : original object o find and persist
     * @param persistentObject : cloned entity of the original object that can be persisted/merged
     * @param persist : true if we want to persist the object in the db
     * @param needToSynchronizeProperties : true if it needs to synchronize the properties of the object
     * @return the object which is in sync with the database (existing instance found and merged or new instance persisted if allowed)
     * @throws FinderException
     * @throws PersisterException
     * @throws SynchronizerException
     */
    protected T findOrPersist(I originalObject, T persistentObject, boolean persist, boolean needToSynchronizeProperties)
            throws FinderException, PersisterException, SynchronizerException {
        // find existing instance in the database
        T existingInstance = find((I)persistentObject);
        // cache object to persist if necessary
        storeInCache(originalObject, persistentObject, existingInstance);
        // the existing instance has been found in the DB and we need to merge existing persistent instance with the other instance
        if (existingInstance != null){
            // we merge the existing instance with the new instance if possible
            if (getIntactMerger() != null){
                // store object and intact object in a identity cache so no lazy properties can be called before synchronization
                registerObjectBeforeProcessing(originalObject, persistentObject, existingInstance);

                // merge
                T mergedObject = getIntactMerger().merge(persistentObject, existingInstance);

                // remove object and intact object from identity cache as not dirty anymore
                unregisterObjectAfterProcessing(originalObject, persistentObject, existingInstance);

                // cache object to persist if allowed
                storeInCache((I)mergedObject, persistentObject, mergedObject);

                // then set userContext
                mergedObject.setLocalUserContext(getContext().getUserContext());

                if (listener != null
                        && persistentObject instanceof IntactPrimaryObject){
                    if (((IntactPrimaryObject)persistentObject).getAc() == null
                            && ((IntactPrimaryObject)existingInstance).getAc() != null){
                        listener.onTransientMergedWithDbInstance((IntactPrimaryObject)persistentObject, (IntactPrimaryObject)existingInstance);
                    }
                }

                return mergedObject;
            }
            if (listener != null
                    && persistentObject instanceof IntactPrimaryObject){
                listener.onReplacedWithDbInstance((IntactPrimaryObject)persistentObject, (IntactPrimaryObject)existingInstance);
            }
            // we only return the existing instance if no merge allowed
            return existingInstance;
        }
        // no existing instances in the DB could be found
        else{
            // then set userContext
            persistentObject.setLocalUserContext(getContext().getUserContext());
            // synchronize before persisting
            if (needToSynchronizeProperties){
                synchronizePartiallyInitialisedProperties(originalObject, persistentObject);
            }
            // persist object if allowed
            if (persist){
                persistObject(persistentObject);
            }
            return persistentObject;
        }
    }

    private void unregisterObjectAfterProcessing(I originalObject, T persistentObject, T existingInstance) {
        removeObjectInstanceFromIdentityCache(originalObject);
        if (originalObject != existingInstance){
            removeObjectInstanceFromIdentityCache((I)existingInstance);
        }
        if (originalObject != persistentObject){
            removeObjectInstanceFromIdentityCache((I)persistentObject);
        }
    }

    /**
     * Persist the object in the DB
     * @param existingInstance : instance to persist
     */
    protected void persistObject(T existingInstance) {
        this.entityManager.persist(existingInstance);

        if (this.listener != null && existingInstance instanceof IntactPrimaryObject){
           this.listener.onPersisted((IntactPrimaryObject)existingInstance);
        }
    }

    /**
     * This method will merge properties with object from cache if necessary
     * @param object   : original object
     * @param intactEntity: intact entity
     * @param existingInstance : existing instance from cache/database
     * @return  the object merged with the cache if necessary, the fetched object otherwise
     */
    protected T mergeWithCachedObject(I object, T intactEntity, T existingInstance, boolean needToSynchronizeProperties) throws PersisterException,
            FinderException, SynchronizerException {
        // merge only if object instances are different
        if (object != existingInstance && getIntactMerger() != null){

            // merge cached instance with original object
            // store object and intact object in a identity cache so no lazy properties can be called before synchronization
            registerObjectBeforeProcessing(object, intactEntity, existingInstance);
            T merged = getIntactMerger().merge(intactEntity, existingInstance);
            // remove object and intact object from identity cache as not dirty anymore
            unregisterObjectAfterProcessing(object, intactEntity, existingInstance);
            // cache object to persist if allowed
            storeInCache((I)merged, intactEntity, merged);

            // then set userContext
            existingInstance.setLocalUserContext(getContext().getUserContext());
            return merged;
        }
        return existingInstance;
    }

    private void registerObjectBeforeProcessing(I object, T intactEntity, T existingInstance) {
        storeObjectInIdentityCache(object, existingInstance);
        if (object != existingInstance){
            storeObjectInIdentityCache((I)existingInstance, existingInstance);
        }
        if (object != intactEntity){
            storeObjectInIdentityCache((I)intactEntity, existingInstance);
        }
    }

    /**
     * This method will synchronize the properties of the object which may be partially initialised
     * @param object : object (lazy initialised collections, object maybe detached from session)
     * @param intactObject : intact instance
     * @throws FinderException
     * @throws PersisterException
     * @throws SynchronizerException
     */
    protected void synchronizePartiallyInitialisedProperties(I object, T intactObject) throws FinderException, PersisterException, SynchronizerException {
        // store object and intact object in a identity cache so no lazy properties can be called before synchronization
        registerObjectBeforeProcessing(object, intactObject, intactObject);
        // synchronize properties
        synchronizeProperties(intactObject);
        // remove object and intact object from identity cache as not dirty anymore
        unregisterObjectAfterProcessing(object, intactObject, intactObject);
    }

    /**
     * If the flushmode of the entity manager is not null, it will set the mode to COMMIT to avoid flushing the changes when doing queries.
     * The objects need to be fully synchronized before the entity manager can be flushed
     * @return the original flushmode of the entity manager
     */
    protected FlushModeType initialiseEntityManagerFlushType() {
        // set flush mode to commit so queries do not trigger flush
        FlushModeType mode = getEntityManager().getFlushMode();
        // set flushmode if not readonly. Do not set flushmode if readonly because of hibernate assertion failure!!!!
        if (mode != null){
            getEntityManager().setFlushMode(FlushModeType.COMMIT);
        }
        return mode;
    }

    /**
     * Reset the fushmode type of the entity manager if the provided mode is not null
     * @param mode
     */
    protected void resetEntityManagerFlushType(FlushModeType mode) {
        if (mode != null){
            getEntityManager().setFlushMode(mode);
        }
    }

    /**
     * This method will convert an object in its matching IntAct entity which can then be persisted
     * @param object : object implementing JAMI interfaces but not entity managed by this synchronizer
     * @return the copy of this object which can be managed by this synchronizer
     * @throws SynchronizerException
     */
    protected T createIntactEntityFrom(I object) throws SynchronizerException {
        T newObject;
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
        return newObject;
    }

    /**
     * Retrieve an object instance from the identity cache and sets the user context.
     * This method should not synchronize any properties while retrieving the object instance.
     * This method will reset the entity manager flush mode before returning the object in the identity cache
     * @param object : object that have been cached in an identity cache
     * @param mode: flush mode type of the entity manager
     * @return the matching object in the identity cache
     */
    protected T processCachedObjectInstance(I object, FlushModeType mode) {
        // get object from identity cache
        T fetched = fetchMatchingObjectFromIdentityCache(object);
        // then set userContext
        fetched.setLocalUserContext(getContext().getUserContext());

        // reinit flushmode
        resetEntityManagerFlushType(mode);
        return fetched;
    }

    /**
     * Method to process an object which already exists in the cache based on a business key
     * @param object : existing object
     * @param mode : entity manager flush type
     * @param needToSynchronize : boolean value to know if the object needs to be synchronized again
     * @return the cached object merged with the properties of the original object
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    protected T processCachedObject(I object, T intactEntity, FlushModeType mode, boolean needToSynchronize) throws PersisterException, FinderException, SynchronizerException {
        // get cached instance from business cache
        T fetched = fetchObjectFromCache(object);
        // then merge properties if not done yet. The object is in a business key map, it may have more properties that could be merged to the original object in the cache
        T merged = mergeWithCachedObject(object, intactEntity, fetched, needToSynchronize);

        // reset entity manager flushMode
        resetEntityManagerFlushType(mode);
        return merged;
    }

    /**
     * Find or persist a transient instance which does not exist in the database
     * @param object
     * @param persist
     * @param mode
     * @param newObject
     * @param needToSynchronizeProperties
     * @return
     * @throws FinderException
     * @throws PersisterException
     * @throws SynchronizerException
     */
    protected T processTransientObject(I object, boolean persist, FlushModeType mode, T newObject, boolean needToSynchronizeProperties) throws FinderException,
            PersisterException, SynchronizerException {
        // synchronize with db if nothing in the cache
        T fetched= findOrPersist(object, newObject, persist, needToSynchronizeProperties);
        // reinit flushmode
        resetEntityManagerFlushType(mode);
        return fetched;
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

    protected abstract Object extractIdentifier(T object);

    /**
     * Instantiate the intact entity and initialise the properties from the original object
     * @param object : original object to clone
     * @param intactClass : matching intact class to instantiate
     * @return the intact entity which is a clone of the original object
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    protected abstract T instantiateNewPersistentInstance(I object, Class<? extends T> intactClass) throws InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException;

    /**
     * Method to fetch an object from a cache based on a business key.
     *It can return null if the synchronizer does not need a cache based on a business key
     * @param object : object
     * @return the synchronized/persistent object associated with this object which has been fetched from the
     * cache
     */
    protected abstract T fetchObjectFromCache(I object);

    /**
     * Method to know if an object exists in a cache based on a business key.
     * @param object which is fully initialised and not lazy
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
     * This methods return the object matching the given object in a identity cache.
     * It can be null if the object is not in the identity cache or if the synchronizer do not need to cache objects
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
     * Method to know if an object is partially initialised (contains properties and collections that are needed to compare this object with others in a map but cannot because they are detached
     * and/or partially initialised)
     * @param originalObject : the object
     * @return true if the object is partially initialised and needs to be synchronized before putting anything in the cache
     */
    protected abstract boolean isObjectPartiallyInitialised(I originalObject);

    @Override
    public void flush() {
        getEntityManager().flush();
        clearCache();
    }

    @Override
    public DbSynchronizerListener getListener() {
        return this.listener;
    }

    @Override
    public void setListener(DbSynchronizerListener listener) {
        this.listener = listener;
    }
}
