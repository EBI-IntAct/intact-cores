package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.Parameter;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactParameter;

/**
 * Interface for parameter synchronizers
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface ParameterSynchronizer<P extends Parameter, T extends AbstractIntactParameter> extends IntactDbSynchronizer<P, T>{

}
