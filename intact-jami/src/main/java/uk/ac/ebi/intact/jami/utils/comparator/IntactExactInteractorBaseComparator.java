package uk.ac.ebi.intact.jami.utils.comparator;

import psidev.psi.mi.jami.model.Interactor;
import psidev.psi.mi.jami.model.InteractorPool;
import psidev.psi.mi.jami.model.Organism;
import psidev.psi.mi.jami.utils.comparator.interactor.ExactInteractorBaseComparator;
import psidev.psi.mi.jami.utils.comparator.organism.OrganismTaxIdComparator;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractor;

/**
 * Comparator for IntAct interactors
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class IntactExactInteractorBaseComparator extends ExactInteractorBaseComparator
implements IntactComparator<Interactor>{

    public IntactExactInteractorBaseComparator() {
        super(new OrganismTaxIdComparator(), new IntactCvTermComparator(), new IntactInteractorBaseComparator());
    }

    @Override
    /**
     * @return true if the object has some properties necessary for the comparator that are not lazy loaded
     */
    public boolean canCompare(Interactor objectToCompare) {
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

        // then check
        return true;
    }

    public static boolean canCompareAllProperties(Interactor objectToCompare) {
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

            if (!intactInteractor.areAnnotationsInitialized()){
                return false;
            }
        }

        // check if a pool
        if (objectToCompare instanceof InteractorPool){
            InteractorPool pool = (InteractorPool)objectToCompare;
            for (Interactor i : pool){
                if (!canCompareAllProperties(i)){
                    return false;
                }
            }
        }

        // then check
        return true;
    }
}
