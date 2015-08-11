package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.Experiment;
import uk.ac.ebi.intact.jami.model.extension.IntactExperiment;

/**
 * Interface for alias synchronizers
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface IntactExperimentSynchronizer extends IntactDbSynchronizer<Experiment, IntactExperiment>{

    public void prepareAndSynchronizeShortLabel(IntactExperiment experiment) throws SynchronizerException ;
}
