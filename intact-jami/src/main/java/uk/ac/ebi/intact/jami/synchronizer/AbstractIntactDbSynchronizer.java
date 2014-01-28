package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;

/**
 * Abstract synchronizer for intact objects
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public abstract class AbstractIntactDbSynchronizer<I, T> implements IntactDbSynchronizer<I,T> {

    private EntityManager entityManager;
    private Class<? extends T> intactClass;
    private static final Log log = LogFactory.getLog(AbstractIntactDbSynchronizer.class);

    public AbstractIntactDbSynchronizer(EntityManager entityManager, Class<? extends T> intactClass){
        if (entityManager == null){
            throw new IllegalArgumentException("An IntAct database synchronizer needs a non null entity manager");
        }
        this.entityManager = entityManager;
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

            return findOrPersist(newObject, persist);
        }
        else{
            T intactObject = (T)object;
            Object identifier = extractIdentifier(intactObject);
            // detached existing instance or new transient instance
            if (identifier != null && !this.entityManager.contains(intactObject)){
                return this.entityManager.find(this.intactClass, identifier);
            }
            // retrieve and or persist transient instance
            else if (identifier == null){
                return findOrPersist(intactObject, persist);
            }
            else{
                // synchronize properties
                synchronizeProperties(intactObject);
                return intactObject;
            }
        }
    }

    protected abstract Object extractIdentifier(T object);

    protected abstract T instantiateNewPersistentInstance(I object, Class<? extends T> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    protected T findOrPersist(T object, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        T existingInstance = find((I)object);
        if (existingInstance != null){
            return existingInstance;
        }
        else{
            // synchronize before persisting
            synchronizeProperties(object);
            if (persist){
                this.entityManager.persist(object);
            }
            return object;
        }
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected Class<? extends T> getIntactClass() {
        return intactClass;
    }
}
