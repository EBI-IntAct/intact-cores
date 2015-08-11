package uk.ac.ebi.intact.core.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.model.Feature;

import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Jul-2006</pre>
 */
@Mockable
public interface FeatureDao extends AnnotatedObjectDao<Feature> {
    List<Feature> getByComponentAc( String componentAc );
}
