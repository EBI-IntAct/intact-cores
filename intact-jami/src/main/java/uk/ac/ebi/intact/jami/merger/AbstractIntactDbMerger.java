package uk.ac.ebi.intact.jami.merger;

import uk.ac.ebi.intact.jami.model.audit.Auditable;

/**
 * Abstract class for IntAct merger of Auditable objects in IntAct
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public abstract class AbstractIntactDbMerger<A extends Auditable> implements IntactDbMerger<A> {

    public A merge(A obj1, A obj2) {
        if (obj2 == null){
            return obj1;
        }
        else if (obj1 == null){
            return obj2;
        }
        else{
            mergeCreatedDate(obj1, obj2);
            mergeUpdateDate(obj1, obj2);
            mergeCreator(obj1, obj2);
            mergeUpdator(obj1, obj2);

            mergeOtherProperties(obj1, obj2);

            return obj2;
        }
    }

    protected abstract void mergeOtherProperties(A obj1, A obj2);

    protected abstract void mergeCreator(A obj1, A obj2);

    protected abstract void mergeCreatedDate(A obj1, A obj2);

    protected abstract void mergeUpdateDate(A obj1, A obj2);

    protected abstract void mergeUpdator(A obj1, A obj2);
}
