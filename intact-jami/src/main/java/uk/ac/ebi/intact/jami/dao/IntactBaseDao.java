package uk.ac.ebi.intact.jami.dao;

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

public interface IntactBaseDao<T> {

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

    public T update(T objToUpdate);

    public void persist(T objToPersist);

    public void persistAll(Collection<T> objsToPersist);

    public void delete(T objToDelete);

    public void deleteAll(Collection<T> objsToDelete);

    public int deleteAll();

    public void refresh(T obj);

    public void detach(T objToEvict);

    public void merge(T objToReplicate);

    public boolean isTransient(T object);

    public void setEntityClass(Class<T> entityClass);

    public Class<T> getEntityClass();
}
