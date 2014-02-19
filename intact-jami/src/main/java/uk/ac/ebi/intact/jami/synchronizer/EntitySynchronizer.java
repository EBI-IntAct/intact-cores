package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.Entity;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactEntity;

/**
 * Interface for entity synchronizers
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface EntitySynchronizer<I extends Entity, T extends AbstractIntactEntity> extends IntactDbSynchronizer<I, T>{

}
