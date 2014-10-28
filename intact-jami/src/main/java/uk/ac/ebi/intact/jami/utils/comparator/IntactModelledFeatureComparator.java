package uk.ac.ebi.intact.jami.utils.comparator;

import psidev.psi.mi.jami.utils.comparator.feature.ModelledFeatureComparator;

/**
 * Basic ModelledFeature comparator.
 * It will use a AbstractFeatureBaseComparator to compare basic properties of a feature.
 *
 * This comparator will ignore all the other properties of a biological feature.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>16/01/13</pre>
 */

public class IntactModelledFeatureComparator extends ModelledFeatureComparator {

    /**
     * Creates a new IntactModelledFeatureComparator.
     */
    public IntactModelledFeatureComparator(){
        super(new IntactFeatureBaseComparator());
    }

    public IntactFeatureBaseComparator getFeatureComparator() {
        return (IntactFeatureBaseComparator)super.getFeatureComparator();
    }
}
