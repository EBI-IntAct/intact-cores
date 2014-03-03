package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.Annotation;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactAnnotation;

/**
 * Interface for annotation synchronizers
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface AnnotationSynchronizer<T extends AbstractIntactAnnotation> extends IntactDbSynchronizer<Annotation, T>{

    public boolean isAnnotationTopicSynchronizationEnabled();

    public void setAnnotationTopicSynchronizationEnabled(boolean enabled);
}
