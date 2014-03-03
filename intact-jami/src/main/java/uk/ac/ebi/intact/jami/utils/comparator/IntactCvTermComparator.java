package uk.ac.ebi.intact.jami.utils.comparator;

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

        int comp = super.compare(cvTerm1, cvTerm2);
        if (comp != 0){
            return comp;
        }

        if (cvTerm1 instanceof IntactCvTerm && cvTerm2 instanceof IntactCvTerm){
            String objClass1 = ((IntactCvTerm)cvTerm1).getObjClass();
            String objClass2 = ((IntactCvTerm)cvTerm2).getObjClass();

            if (objClass1 != null && objClass2 != null){
                return objClass1.compareTo(objClass2);
            }
        }
        return comp;
    }
}
