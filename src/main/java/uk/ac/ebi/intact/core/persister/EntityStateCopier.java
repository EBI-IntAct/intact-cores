package uk.ac.ebi.intact.core.persister;

import uk.ac.ebi.intact.model.AnnotatedObject;

/**
 * Utility that copies the properties of one annotated object onto an other one.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.8.0
 */
public interface EntityStateCopier {

    /**
     * Copy properties from the source to the target.
     *
     * @param source template from which the modification arise
     * @param target object that has to be updated from the source
     * @return whether attributes have bean really copied or not (if not, it means that source and target where equals)
     */
     boolean copy( AnnotatedObject source, AnnotatedObject target );

}
