package uk.ac.ebi.intact.core.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.model.Component;

import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Jul-2006</pre>
 */
@Mockable
public interface ComponentDao extends AnnotatedObjectDao<Component> {

    List<Component> getByInteractorAc( String interactorAc );
    List<Component> getByInteractionAc( String interactionAc );
}
