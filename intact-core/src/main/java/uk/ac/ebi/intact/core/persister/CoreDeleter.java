package uk.ac.ebi.intact.core.persister;

import uk.ac.ebi.intact.model.IntactObject;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public interface CoreDeleter {

    void delete(IntactObject intactObject);
}
