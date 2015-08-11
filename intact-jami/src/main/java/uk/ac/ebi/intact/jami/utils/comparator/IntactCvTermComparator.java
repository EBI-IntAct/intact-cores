package uk.ac.ebi.intact.jami.utils.comparator;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.utils.comparator.cv.CvTermComparator;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;

/**
 * Comparator for IntAct cv terms that take into account objClass and always look at the shortlabel first
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class IntactCvTermComparator extends CvTermComparator implements IntactComparator<CvTerm> {

    private static IntactCvTermComparator intactCvTermComparator;

    /**
     * Creates a new CvTermComparator with UnambiguousExternalIdentifierComparator
     *
     */
    public IntactCvTermComparator() {
        super(new IntactCvIdentifiersComparator());
    }

    @Override
    public IntactCvIdentifiersComparator getIdentifierCollectionComparator() {
        return (IntactCvIdentifiersComparator) super.getIdentifierCollectionComparator();
    }

    /**
     * Use UnambiguousCvTermComparator to know if two CvTerms are equals.
     * @param cv1
     * @param cv2
     * @return true if the two CvTerms are equal
     */
    public static boolean areEquals(CvTerm cv1, CvTerm cv2){
        if (intactCvTermComparator == null){
            intactCvTermComparator = new IntactCvTermComparator();
        }

        return intactCvTermComparator.compare(cv1, cv2) == 0;
    }

    @Override
    public boolean canCompare(CvTerm objectToCompare) {
        if (objectToCompare instanceof IntactCvTerm){
            if (!((IntactCvTerm)objectToCompare).areXrefsInitialized()){
                 return false;
            }
        }
        return true;
    }
}
