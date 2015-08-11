package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.Source;
import uk.ac.ebi.intact.jami.model.extension.IntactSource;

/**
 * Interface for source synchronizers
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface IntactSourceSynchronizer extends IntactDbSynchronizer<Source, IntactSource>{
    public void prepareAndSynchronizeShortLabel(IntactSource cv) throws SynchronizerException ;

}
