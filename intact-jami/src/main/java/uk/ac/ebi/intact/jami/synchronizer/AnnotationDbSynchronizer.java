package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.Annotation;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactAnnotation;

/**
 * Db synchornizers for annotations
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface AnnotationDbSynchronizer<A extends AbstractIntactAnnotation> extends IntactDbSynchronizer<Annotation, A> {

    public CvTermDbSynchronizer getTopicSynchronizer();

    public AnnotationDbSynchronizer<A> setTopicSynchronizer(CvTermDbSynchronizer topicSynchronizer);
}
