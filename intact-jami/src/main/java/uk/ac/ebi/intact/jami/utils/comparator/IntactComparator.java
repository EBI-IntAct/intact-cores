package uk.ac.ebi.intact.jami.utils.comparator;

import java.util.Comparator;

/**
 * Intact comparators have to take into account lazy loading and dirty objects
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/08/14</pre>
 */

public interface IntactComparator<T> extends Comparator<T> {

    /**
     *
     * @param objectToCompare
     * @return  true if the object is initialised enough to be compared with another experiment
     */
    public boolean canCompare(T objectToCompare);
}
