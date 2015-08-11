package uk.ac.ebi.intact.model.util.filter;

import uk.ac.ebi.intact.model.IntactObject;

/**
 * Filter for IntactObjects
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public interface IntactObjectFilter<T extends IntactObject> {

    boolean accept(T io);
}
