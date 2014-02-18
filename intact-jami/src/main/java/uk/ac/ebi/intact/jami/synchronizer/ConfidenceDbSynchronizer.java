package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.Confidence;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactConfidence;

/**
 * Db synchornizers for confidences
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface ConfidenceDbSynchronizer<C extends Confidence, A extends AbstractIntactConfidence> extends IntactDbSynchronizer<C, A> {

    public CvTermDbSynchronizer getTypeSynchronizer();

    public ConfidenceDbSynchronizer<C,A> setTypeSynchronizer(CvTermDbSynchronizer topicSynchronizer);
}
