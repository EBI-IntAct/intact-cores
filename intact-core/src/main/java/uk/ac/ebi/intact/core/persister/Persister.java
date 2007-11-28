package uk.ac.ebi.intact.core.persister;

import uk.ac.ebi.intact.model.AnnotatedObject;

/**
 * Defines how to persist an intact object.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.8.0
 */
public interface Persister {

    public void saveOrUpdate( AnnotatedObject ao );
}
