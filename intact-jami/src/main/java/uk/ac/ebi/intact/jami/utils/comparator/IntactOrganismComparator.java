package uk.ac.ebi.intact.jami.utils.comparator;

import psidev.psi.mi.jami.model.Organism;
import psidev.psi.mi.jami.utils.comparator.organism.OrganismComparator;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;

/**
 * IntactComparator for organism
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/08/14</pre>
 */

public class IntactOrganismComparator extends OrganismComparator implements IntactComparator<Organism>{

    public IntactOrganismComparator() {
        super(new IntactCvTermComparator());
    }

    @Override
    public boolean canCompare(Organism objectToCompare) {
        // check cellType
        if (objectToCompare.getCellType() != null && objectToCompare.getCellType() instanceof IntactCvTerm){
            if (!((IntactCvTerm)objectToCompare.getCellType()).areXrefsInitialized()){
                return false;
            }
        }
        // check tissue
        if (objectToCompare.getTissue() != null && objectToCompare.getTissue() instanceof IntactCvTerm){
            if (!((IntactCvTerm)objectToCompare.getTissue()).areXrefsInitialized()){
                return false;
            }
        }

        return true;
    }
}
