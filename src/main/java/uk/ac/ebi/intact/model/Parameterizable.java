package uk.ac.ebi.intact.model;

import java.util.Collection;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public interface Parameterizable<P extends Parameter> {

    void setParameters( Collection<P> interactionParameters );

    void addParameter( P interactionParameter );

    void removeParameter( P interactionParameter );

    Collection<P> getParameters();

}
