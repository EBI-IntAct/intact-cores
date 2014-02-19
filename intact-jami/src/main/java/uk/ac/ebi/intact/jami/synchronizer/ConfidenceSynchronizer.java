package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.Confidence;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactConfidence;

/**
 * Interface for alias synchronizers
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface ConfidenceSynchronizer<C extends Confidence, T extends AbstractIntactConfidence> extends IntactDbSynchronizer<C, T>{

}
