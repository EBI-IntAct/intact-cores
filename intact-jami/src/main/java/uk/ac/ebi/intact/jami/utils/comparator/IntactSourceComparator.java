package uk.ac.ebi.intact.jami.utils.comparator;

import psidev.psi.mi.jami.model.Source;
import uk.ac.ebi.intact.jami.model.extension.IntactSource;

/**
 * Comparator for IntAct source
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class IntactSourceComparator implements IntactComparator<Source> {

    private static IntactSourceComparator intactCvTermComparator;

    private IntactCvTermComparator delegate;

    /**
     * Creates a new CvTermComparator with UnambiguousExternalIdentifierComparator
     *
     */
    public IntactSourceComparator() {
        delegate = new IntactCvTermComparator();
    }

    /**
     * Use UnambiguousCvTermComparator to know if two CvTerms are equals.
     * @param cv1
     * @param cv2
     * @return true if the two CvTerms are equal
     */
    public static boolean areEquals(Source cv1, Source cv2){
        if (intactCvTermComparator == null){
            intactCvTermComparator = new IntactSourceComparator();
        }

        return intactCvTermComparator.compare(cv1, cv2) == 0;
    }

    @Override
    public boolean canCompare(Source objectToCompare) {
        if (objectToCompare instanceof IntactSource){
            if (!((IntactSource)objectToCompare).areXrefsInitialized()){
                 return false;
            }
        }
        return true;
    }

    @Override
    public int compare(Source source, Source source2) {
        return this.delegate.compare(source, source2);
    }
}
