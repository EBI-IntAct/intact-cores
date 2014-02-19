package uk.ac.ebi.intact.jami.utils;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.utils.comparator.cv.UnambiguousCvTermComparator;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;

/**
 * Comparator for IntAct cv terms that take into account objClass
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class IntactCvTermComparator extends UnambiguousCvTermComparator{

    @Override
    public int compare(CvTerm cvTerm1, CvTerm cvTerm2) {
        if (cvTerm1 == cvTerm2){
            return 0;
        }

        int comp = super.compare(cvTerm1, cvTerm2);
        if (comp != 0){
            return comp;
        }

        String objClass1 = ((IntactCvTerm)cvTerm1).getObjClass();
        String objClass2 = ((IntactCvTerm)cvTerm2).getObjClass();
        int EQUAL = 0;
        int BEFORE = -1;
        int AFTER = 1;

        if (objClass1 == null && objClass2 == null){
            return EQUAL;
        }
        else if (objClass1 == null){
            return AFTER;
        }
        else if (objClass2 == null){
            return BEFORE;
        }
        else {
            return objClass1.compareTo(objClass2);
        }
    }
}
