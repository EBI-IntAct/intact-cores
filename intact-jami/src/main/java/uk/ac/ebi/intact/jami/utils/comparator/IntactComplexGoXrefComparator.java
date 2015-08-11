package uk.ac.ebi.intact.jami.utils.comparator;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import psidev.psi.mi.jami.utils.comparator.xref.UnambiguousXrefComparator;
import uk.ac.ebi.intact.jami.model.extension.ComplexGOXref;

/**
 * Comparator for xrefs having a mix GO and normal xrefs
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>12/02/15</pre>
 */

public class IntactComplexGoXrefComparator extends UnambiguousXrefComparator {

    @Override
    public int compare(Xref xref1, Xref xref2) {
        int comp = super.compare(xref1, xref2);
        if (comp != 0){
            return comp;
        }

        if (xref1 instanceof ComplexGOXref && xref2 instanceof ComplexGOXref){
            String pubmed1 = ((ComplexGOXref) xref1).getPubmed();
            String pubmed2 = ((ComplexGOXref) xref2).getPubmed();

            if (pubmed1 != null && pubmed2 != null){
                comp = pubmed1.compareTo(pubmed2);
                if (comp != 0){
                    return comp;
                }
            }
            else if (pubmed1 != null){
                return 1;
            }
            else if (pubmed2 != null){
                return -1;
            }

            CvTerm ecoRef1 = ((ComplexGOXref) xref1).getEvidenceType();
            CvTerm ecoRef2 = ((ComplexGOXref) xref2).getEvidenceType();

            if (ecoRef1 != null && ecoRef2 != null){
                return ecoRef1.getShortName().compareTo(ecoRef2.getShortName());
            }
            else if (ecoRef1 != null){
                return 1;
            }
            else if (ecoRef2 != null){
                return -1;
            }

            return 0;
        }
        else if (xref1 instanceof ComplexGOXref){
            return -1;
        }
        else if (xref2 instanceof ComplexGOXref){
            return 1;
        }
        else{
            return 0;
        }
    }
}
