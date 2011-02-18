package uk.ac.ebi.intact.core.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Annotation;

import java.util.Collection;
import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04-Jul-2006</pre>
 */
@Mockable
public interface AnnotationDao extends IntactObjectDao<Annotation> {

    List<Annotation> getByTextLike( String text );

    /**
     * Finds the parent annotated objects that contain the annotation with the provided AC.
     * This method has been created to detect those annotations "shared" by multiple annotated objects, which can
     * pose a risk in other tools. In practice, we try to avoid this situation.
     * @param annotationAc the accession of the annotation
     * @return the annotated objects that contain such annotation
     */
    List<AnnotatedObject> getParentsWithAnnotationAc(String annotationAc);

    /**
     * Fetches the annotations using its parent AC.
     * @param ac The ac for the parent
     * @return The annotations, if any
     * @since 2.4.0
     */
    Collection<Annotation> getByParentAc(Class<? extends AnnotatedObject> parentClass, String parentAc);
}
