package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.InteractionEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractionEvidence;

/**
 * Interface for alias synchronizers
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface IntactInteractionSynchronizer extends IntactDbSynchronizer<InteractionEvidence, IntactInteractionEvidence>{

    public void prepareAndSynchronizeShortLabel(IntactInteractionEvidence interaction) throws SynchronizerException ;
}
