package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.Checksum;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactChecksum;

/**
 * Interface for checksum synchronizers
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface ChecksumSynchronizer<T extends AbstractIntactChecksum> extends IntactDbSynchronizer<Checksum, T>{

}
