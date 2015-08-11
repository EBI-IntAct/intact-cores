package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.MIEnricher;
import uk.ac.ebi.intact.jami.model.audit.Auditable;

/**
 * Interface for merging two IntAct objects :
 * - obj1 containing some updates
 * - obj2 loaded from the database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public interface IntactDbMerger<I,T extends Auditable> extends MIEnricher<I>{

    /**
     * Merge obj1 with obj2 and return the resulting object.
     * @param obj1 : first object
     * @param obj2 : second object loaded from the database
     * @return
     */
    public T merge(T obj1, T obj2);
}
