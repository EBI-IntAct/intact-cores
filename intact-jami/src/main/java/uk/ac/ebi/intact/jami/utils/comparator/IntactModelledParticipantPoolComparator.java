package uk.ac.ebi.intact.jami.utils.comparator;

import psidev.psi.mi.jami.model.ModelledFeature;
import psidev.psi.mi.jami.utils.comparator.CollectionComparator;
import psidev.psi.mi.jami.utils.comparator.participant.EntityBaseComparator;
import psidev.psi.mi.jami.utils.comparator.participant.ModelledEntityComparator;
import psidev.psi.mi.jami.utils.comparator.participant.ModelledParticipantPoolComparator;

/**
 * Basic biological participant pool comparator.
 * It will compare the basic properties of a biological participant pool using IntactParticipantBaseComparator.
 * Then features will be compared with IntactModelledFeatureComparator.
 *
 * All the participant candidates will be compared using ModelledEntityComparator
 *
 * This comparator will ignore all the other properties of a biological participant.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13/02/13</pre>
 */

public class IntactModelledParticipantPoolComparator extends ModelledParticipantPoolComparator {

    /**
     * Creates a new ComponentComparator
     */
    public IntactModelledParticipantPoolComparator(){
        super(new IntactParticipantBaseComparator(),
                new IntactModelledFeatureComparator(),
                new ModelledEntityComparator(new EntityBaseComparator(new IntactInteractorComparator()),
                        new CollectionComparator<ModelledFeature>(new IntactModelledFeatureComparator())));
    }

    public IntactModelledParticipantPoolComparator(EntityBaseComparator interactorComparator) {
        super(new IntactParticipantBaseComparator(interactorComparator),
                new IntactModelledFeatureComparator(),
                new ModelledEntityComparator(interactorComparator, new IntactModelledFeatureComparator()));

    }

    public IntactParticipantBaseComparator getParticipantBaseComparator() {
        return (IntactParticipantBaseComparator)super.getParticipantBaseComparator();
    }
}
