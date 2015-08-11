package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.Organism;
import uk.ac.ebi.intact.jami.model.extension.IntactOrganism;

/**
 * Interface for source synchronizers
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface IntactOrganismSynchronizer extends IntactDbSynchronizer<Organism, IntactOrganism>{
    public void prepareAndSynchronizeCommonName(IntactOrganism cv) throws SynchronizerException ;

}
