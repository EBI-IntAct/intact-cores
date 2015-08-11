package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.CooperativeEffect;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactCooperativeEffect;

/**
 * Interface for cooperativeEffect synchronizers
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface CooperativeEffectSynchronizer<C extends CooperativeEffect, T extends AbstractIntactCooperativeEffect> extends IntactDbSynchronizer<C, T>{

}
