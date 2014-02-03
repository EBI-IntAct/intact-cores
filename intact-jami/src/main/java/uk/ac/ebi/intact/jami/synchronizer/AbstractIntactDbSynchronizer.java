package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.jami.merger.IntactDbMerger;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerEnrichOnly;
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
    private Class<? extends T> intactClass;
    private static final Log log = LogFactory.getLog(AbstractIntactDbSynchronizer.class);

    private IntactDbMerger<I,T> intactMerger;

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
            // new object to synchronize with db
            return findOrPersist(newObject, persist);
        }
        else{
            T intactObject = (T)object;
            Object identifier = extractIdentifier(intactObject);
            // detached existing instance or new transient instance
            if (identifier != null && !this.entityManager.contains(intactObject)){
                // synchronize properties first
                synchronizeProperties(intactObject);
                // merge
                return this.entityManager.merge(intactObject);
            }
            // retrieve and or persist transient instance
            else if (identifier == null){
                // new object to synchronize with db
                return findOrPersist(intactObject, persist);
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

    protected void initialiseDefaultMerger() {
        this.intactMerger = new IntactDbMergerEnrichOnly<I,T>();
    }

    protected abstract Object extractIdentifier(T object);

    protected abstract T instantiateNewPersistentInstance(I object, Class<? extends T> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    protected T findOrPersist(T object, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        T existingInstance = find((I)object);
        // synchronize before persisting
        synchronizeProperties(object);
        // merge existing instance with the other instance
        if (existingInstance != null){
            // we merge the existing instance with the new instance if possible
            if (getIntactMerger() != null){
                getIntactMerger().merge(object, existingInstance);
            }
            // we only return the existing instance after merging
            return existingInstance;
        }
        else{
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
