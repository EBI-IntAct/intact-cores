package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.Interactor;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractor;

/**
 * Interface for interactor synchronizers
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface InteractorSynchronizer<I extends Interactor, T extends IntactInteractor> extends IntactDbSynchronizer<I, T>{

}
