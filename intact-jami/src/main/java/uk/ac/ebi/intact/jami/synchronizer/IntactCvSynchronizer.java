package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;

/**
 * Interface for cv synchronizers
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface IntactCvSynchronizer extends IntactDbSynchronizer<CvTerm, IntactCvTerm>{
    public void prepareAndSynchronizeShortLabel(IntactCvTerm cv) throws SynchronizerException ;

}
