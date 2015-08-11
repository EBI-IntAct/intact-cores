package uk.ac.ebi.intact.jami.utils.comparator;

import psidev.psi.mi.jami.utils.comparator.participant.EntityBaseComparator;
import psidev.psi.mi.jami.utils.comparator.participant.ModelledParticipantComparator;

/**
 * Basic biological participant comparator.
 * It will compare the basic properties of a biological participant using ParticipantBaseComparator.
 *
 * If the participants are both ModelledParticipantPool, it will use ModelledParticipantPoolComparator to compare them
 *
 * This comparator will ignore all the other properties of a biological participant.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13/02/13</pre>
 */

public class IntactModelledParticipantComparator extends ModelledParticipantComparator {

    /**
     * Creates a new ComponentComparator
     */
    public IntactModelledParticipantComparator(){
        IntactInteractorComparator interactorComparator =
                new IntactInteractorComparator(new IntactComplexComparator(this));
        EntityBaseComparator baseComparator = new EntityBaseComparator(interactorComparator);
        setPoolComparator(new IntactModelledParticipantPoolComparator(baseComparator));
    }

    public IntactModelledParticipantPoolComparator getParticipantPoolComparator() {
        return (IntactModelledParticipantPoolComparator)super.getParticipantPoolComparator();
    }
}
