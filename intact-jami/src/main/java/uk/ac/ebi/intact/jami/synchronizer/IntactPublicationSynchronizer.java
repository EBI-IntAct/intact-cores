package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.Publication;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;

/**
 * Interface for alias synchronizers
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface IntactPublicationSynchronizer extends IntactDbSynchronizer<Publication, IntactPublication>{

    public void prepareAndSynchronizeShortLabel(IntactPublication publication) throws SynchronizerException ;
}
