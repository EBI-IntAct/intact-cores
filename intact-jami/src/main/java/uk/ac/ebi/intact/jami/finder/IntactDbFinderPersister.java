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

public interface IntactDbFinderPersister<T> {

    public T find(T object) throws FinderException;

    public T persist(T object) throws FinderException;

    public void synchronizeProperties(T object) throws FinderException;

    public T synchronize(T object) throws FinderException;

    public void clearCache();
}
