package uk.ac.ebi.intact.jami.merger;

/**
 * This merger will always ignore the updates in obj1 and keep the loaded object from the database.
 * If the object loaded from the database is null, it will return the firs object.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class IntactMergerIgnoringSource<T extends Object> implements IntactDbMerger<T>{
    /**
     * This merger will always ignore the updates in obj1 and keep the loaded object from the database.
     * If the object loaded from the database is null, it will return the firs object.
     * @param obj1 : first object
     * @param obj2 : second object loaded from the database
     * @return
     */
    public T merge(T obj1, T obj2) {
        return obj2 != null ? obj2 : obj1;
    }
}
