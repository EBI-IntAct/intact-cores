package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.audit.Auditable;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.List;

/**
 * Basic interface for intact DAO
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>20/01/14</pre>
 */

public interface IntactBaseDao<T extends Auditable> {

    public EntityManager getEntityManager();

    public void flush();

    public List<T> getAll();

    public List<T> getAll(int firstResult, int maxResults);

    /**
     * Returns all the objects, sorted by the chosen property
     *
     * @param firstResult  The first result (index) to get
     * @param maxResults   The maximum results per page
     * @param sortProperty The property to use when sorting
     * @param ascendant    The order of the sort. If true, the sorting is ascendant
     * @return The objects, sorted
     */
    public List<T> getAllSorted(int firstResult, int maxResults, String sortProperty, boolean ascendant);

    public long countAll();

    public T update(T objToUpdate) throws FinderException, SynchronizerException, PersisterException;

    public void persist(T objToPersist) throws FinderException, SynchronizerException, PersisterException;

    public void persistAll(Collection<T> objsToPersist) throws FinderException, SynchronizerException, PersisterException;

    public void delete(T objToDelete) throws FinderException, SynchronizerException, PersisterException;

    public void deleteAll(Collection<T> objsToDelete) throws FinderException, SynchronizerException, PersisterException;

    public int deleteAll() throws FinderException, SynchronizerException, PersisterException;

    public void refresh(T obj);

    public void detach(T objToEvict);

    public void merge(T objToReplicate) throws FinderException, SynchronizerException, PersisterException;

    public boolean isTransient(T object);

    public Class<T> getEntityClass();
}
