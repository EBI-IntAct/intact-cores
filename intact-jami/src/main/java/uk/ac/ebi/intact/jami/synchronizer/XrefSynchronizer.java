package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactXref;

/**
 * Interface for xref synchronizers
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface XrefSynchronizer<T extends AbstractIntactXref> extends IntactDbSynchronizer<Xref, T>{

}
