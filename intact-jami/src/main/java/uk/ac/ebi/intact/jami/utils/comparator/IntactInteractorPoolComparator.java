package uk.ac.ebi.intact.jami.utils.comparator;

import psidev.psi.mi.jami.model.Interactor;
import psidev.psi.mi.jami.model.InteractorPool;
import psidev.psi.mi.jami.model.Organism;
import psidev.psi.mi.jami.utils.comparator.interactor.InteractorPoolComparator;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractor;

/**
 * Comparator for IntAct experiments that take into account annotations and
 * participant identification methods
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class IntactInteractorPoolComparator extends InteractorPoolComparator
implements IntactComparator<InteractorPool>{

    public IntactInteractorPoolComparator() {
        super(new IntactInteractorComparator());
    }

    public IntactInteractorPoolComparator(IntactInteractorComparator interactorComparator) {
        super(interactorComparator);
    }

    @Override
    /**
     * @return true if the object has some properties necessary for the comparator that are not lazy loaded
     */
    public boolean canCompare(InteractorPool objectToCompare) {
        // first check interactor type
        if (objectToCompare.getInteractorType() != null){
            if (objectToCompare.getInteractorType() instanceof IntactCvTerm){
                if (!((IntactCvTerm)objectToCompare.getInteractorType()).areXrefsInitialized()){
                    return false;
                }
            }
        }

        // then check organism
        if (objectToCompare.getOrganism() != null){
            Organism host = objectToCompare.getOrganism();
            // check cellType
            if (host.getCellType() != null && host.getCellType() instanceof IntactCvTerm){
                if (!((IntactCvTerm)host.getCellType()).areXrefsInitialized()){
                    return false;
                }
            }
            // check tissue
            if (host.getTissue() != null && host.getTissue() instanceof IntactCvTerm){
                if (!((IntactCvTerm)host.getTissue()).areXrefsInitialized()){
                    return false;
                }
            }
        }

        // then check identifiers
        if (objectToCompare instanceof IntactInteractor){
            IntactInteractor intactInteractor = (IntactInteractor)objectToCompare;
            if (!intactInteractor.areXrefsInitialized()){
                return false;
            }

            if (!intactInteractor.areAliasesInitialized()){
                return false;
            }
        }

        for (Interactor i : objectToCompare){
            if (!IntactInteractorBaseComparator.canCompareAllProperties(i)){
                return false;
            }
        }

        // then check
        return true;
    }
}
