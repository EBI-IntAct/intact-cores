package uk.ac.ebi.intact.jami.utils.comparator;

import psidev.psi.mi.jami.utils.comparator.feature.FeatureBaseComparator;
import psidev.psi.mi.jami.utils.comparator.range.RangeCollectionComparator;

/**
 * feature comparator.
 * It will look first at the feature shortnames (case insensitive). Then, it will compare the feature types using a UnambiguousCvTermComparator. If the feature types are the same,
 * it will compare interactionEffect and then interactionDependency using UnambiguousCvTermComparator. Then it will compare interpro identifier and if the features do not have an interpro identifier,
 * it will look for at the identifiers in the feature identifiers using UnambiguousIdentifierComparator.
 * Finally, it will look at the ranges using UnambiguousRangeComparator.
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>16/01/13</pre>
 */

public class IntactFeatureBaseComparator extends FeatureBaseComparator {

    public IntactFeatureBaseComparator() {
        super(new IntactCvTermComparator(),
                new IntactIdentifiersComparator(),
                new RangeCollectionComparator(new IntactRangeComparator()));
    }

    public IntactIdentifiersComparator getExternalIdentifierCollectionComparator() {
        return (IntactIdentifiersComparator)super.getExternalIdentifierCollectionComparator();
    }

    public IntactCvTermComparator getCvTermComparator() {
        return (IntactCvTermComparator)super.getCvTermComparator();
    }

}
