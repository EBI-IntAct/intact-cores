package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMerger;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerEnrichOnly;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringLocalObject;
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
    private static final Log log = LogFactory.getLog(AbstractIntactDbSynchronizer.class);

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
        // synchronize properties
        synchronizeProperties(object);

        // persist the cv
        this.entityManager.persist(object);

        return object;
    }

    public T synchronize(I object, boolean persist) throws FinderException, PersisterException, SynchronizerException {

        if (!object.getClass().isAssignableFrom(this.intactClass)){
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
                // new object to synchronize with db
                return findOrPersist(object, intactObject, persist);
            }
            else{
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
        if (!object.getClass().isAssignableFrom(this.intactClass)){
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
        // do not merge existing instance if the merger is a merger ignoring source
        if (getIntactMerger() instanceof IntactDbMergerIgnoringLocalObject){
            T reloaded = getEntityManager().find(getIntactClass(), identifier);
            if (reloaded != null){
                return reloaded;
            }
            else{
                // synchronize properties first
                synchronizeProperties(intactObject);
                // merge
                return this.entityManager.merge(intactObject);
            }
        }
        else{
            // synchronize properties first
            synchronizeProperties(intactObject);
            // merge
            return this.entityManager.merge(intactObject);
        }
    }

    protected void initialiseDefaultMerger() {
        this.intactMerger = new IntactDbMergerEnrichOnly<I,T>();
    }

    protected abstract Object extractIdentifier(T object);

    protected abstract T instantiateNewPersistentInstance(I object, Class<? extends T> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    protected T findOrPersist(I originalObject, T persistentObject, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        T existingInstance = find((I)persistentObject);
        // cache object to persist if allowed
        storeInCache(originalObject, persistentObject, existingInstance);
        // synchronize before persisting
        synchronizeProperties(persistentObject);
        // merge existing persistent instance with the other instance
        if (existingInstance != null && extractIdentifier(existingInstance) == null){
            // we merge the existing instance with the new instance if possible
            if (getIntactMerger() != null){
                getIntactMerger().merge(persistentObject, existingInstance);
            }
            // we only return the existing instance after merging
            return existingInstance;
        }
        // the existing instance has been cached but not persisted
        else if (existingInstance != null){
            if (persist){
                this.entityManager.persist(existingInstance);
            }
            return existingInstance;
        }
        else{
            if (persist){
                this.entityManager.persist(persistentObject);
            }
            return persistentObject;
        }
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
}
