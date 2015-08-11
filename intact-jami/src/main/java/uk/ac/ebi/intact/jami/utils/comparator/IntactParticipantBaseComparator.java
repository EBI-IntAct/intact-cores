package uk.ac.ebi.intact.jami.utils.comparator;

import psidev.psi.mi.jami.utils.comparator.participant.EntityBaseComparator;
import psidev.psi.mi.jami.utils.comparator.participant.ParticipantBaseComparator;

/**
 * Basic participant comparator.
 * It will first compare the interactors/stoichiometry/features using EntityBaseComparator. If both interactors are the same,
 * it will compare the biological roles using AbstractCvTermComparator.
 *
 * This comparator will ignore all the other properties of a participant.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>16/01/13</pre>
 */

public class IntactParticipantBaseComparator extends ParticipantBaseComparator {

    /**
     * Creates a new IntactParticipantBaseComparator
     */
    public IntactParticipantBaseComparator(){

        super(new EntityBaseComparator(new IntactInteractorComparator()), new IntactCvTermComparator());
    }

    public IntactParticipantBaseComparator(EntityBaseComparator entityComparator){

        super(entityComparator, new IntactCvTermComparator());
    }

    public IntactCvTermComparator getCvTermComparator() {
        return (IntactCvTermComparator)super.getCvTermComparator();
    }
}
