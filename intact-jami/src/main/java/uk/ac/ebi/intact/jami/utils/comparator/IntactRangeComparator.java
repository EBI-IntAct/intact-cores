package uk.ac.ebi.intact.jami.utils.comparator;

import psidev.psi.mi.jami.utils.comparator.range.PositionComparator;
import psidev.psi.mi.jami.utils.comparator.range.RangeComparator;

/**
 * Simple range comparator.
 * It compares first the start Position, then the end Position using a PositionComparator,
 * If start/end positions are equals, the linked ranges will always come before the ranges that are not linked.
 * - Two ranges which are null are equals
 * - The range which is not null is before null.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/12/12</pre>
 */

public class IntactRangeComparator extends RangeComparator {

    /**
     * Creates a new IntactRangeComparator
     */
    public IntactRangeComparator(){
        super(new PositionComparator(new IntactCvTermComparator()));
    }
}
