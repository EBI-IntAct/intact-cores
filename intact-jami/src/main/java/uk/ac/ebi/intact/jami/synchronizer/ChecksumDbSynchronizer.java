package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.Checksum;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactChecksum;

/**
 * Db synchornizers for checksums
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface ChecksumDbSynchronizer<A extends AbstractIntactChecksum> extends IntactDbSynchronizer<Checksum, A> {

    public CvTermDbSynchronizer getMethodSynchronizer();

    public ChecksumDbSynchronizer<A> setMethodSynchronizer(CvTermDbSynchronizer topicSynchronizer);
}
