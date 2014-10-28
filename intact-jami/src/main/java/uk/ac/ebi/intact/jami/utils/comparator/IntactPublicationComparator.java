package uk.ac.ebi.intact.jami.utils.comparator;

import psidev.psi.mi.jami.model.Publication;
import psidev.psi.mi.jami.utils.comparator.publication.PublicationComparator;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;

/**
 * Intact comparator for publications
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/08/14</pre>
 */

public class IntactPublicationComparator extends PublicationComparator implements IntactComparator<Publication> {

    public IntactPublicationComparator() {
        super(new IntactIdentifiersComparator());
    }

    @Override
    public boolean canCompare(Publication pub) {
        if (pub instanceof IntactPublication){
            if (!((IntactPublication)pub).areAnnotationsInitialized() || !((IntactPublication)pub).areXrefsInitialized()){
                return false;
            }
        }

        return true;
    }
}
