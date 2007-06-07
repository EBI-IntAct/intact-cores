package uk.ac.ebi.intact.persistence.dao;

import uk.ac.ebi.intact.model.Component;

import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Jul-2006</pre>
 */
public interface ComponentDao extends AnnotatedObjectDao<Component> {

    List<Component> getByInteractorAc( String interactorAc );
}
