package uk.ac.ebi.intact.core.persister;

import uk.ac.ebi.intact.model.AnnotatedObject;

/**
 * Decides how to react to a transient object.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.8.0
 */
public interface TransientObjectHandler {
    
    /**
     * Apply behaviour on the given transient object.
     *
     * @param ao the object to he handled.
     * @return the resulting object after handling.
     */
    public AnnotatedObject handle( AnnotatedObject ao );
}
