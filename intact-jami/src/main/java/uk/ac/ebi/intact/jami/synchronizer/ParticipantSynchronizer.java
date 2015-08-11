package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.Participant;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactParticipant;

/**
 * Interface for participant synchronizers
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface ParticipantSynchronizer<I extends Participant, T extends AbstractIntactParticipant> extends IntactDbSynchronizer<I, T>{

}
