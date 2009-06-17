package uk.ac.ebi.intact.core.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.model.Annotation;

import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04-Jul-2006</pre>
 */
@Mockable
public interface AnnotationDao extends IntactObjectDao<Annotation> {

    List<Annotation> getByTextLike( String text );
}
