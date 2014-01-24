package uk.ac.ebi.intact.jami.finder;

/**
 * Interface for finders that can retrieve existing instances in the DB given an object
 * that can be transient.
 * It can also persist new Intact objects
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface IntactDbSynchronizer<T> {


    /**
     * Finds an existing object in the database based on a business key
     * @param object
     * @return
     * @throws FinderException
     */
    public T find(T object) throws FinderException;

    /**
     * Persist this object instance (MUST be an instance annotated with hibernate annotations) and
     * synchronize its properties when needed
     * @param object
     * @return the persisted instance
     * @throws FinderException
     * @throws PersisterException
     * @throws SynchronizerException
     */
    public T persist(T object) throws FinderException,PersisterException,SynchronizerException;

    /**
     * Synchronize all the properties of this object with the database.
     * Th object instance does not have to be annotated with hibernate annotations as it does not persist this object, only
     * synchronize its properties with what exist in the database
     * @param object
     * @throws FinderException
     * @throws PersisterException
     * @throws SynchronizerException
     */
    public void synchronizeProperties(T object) throws FinderException,PersisterException,SynchronizerException;

    /**
     * Synchronize the given instance and its properties with the database,
     * If the object is not annotated with hibernate annotations, a new persistable object may be created and synchronized with the database.
     * @param object
     * @return the object synchronized. It may not be persisted as we don't call the entityManager.persist method. However, the object is ready for persistent/update
     * @throws FinderException
     * @throws PersisterException
     * @throws SynchronizerException
     */
    public T synchronize(T object) throws FinderException,PersisterException,SynchronizerException;

    /**
     * Clear cached objects
     */
    public void clearCache();
}
