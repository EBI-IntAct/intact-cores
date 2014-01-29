package uk.ac.ebi.intact.jami.merger;

import uk.ac.ebi.intact.jami.model.audit.AbstractAuditable;

/**
 * This IntAct merger, override all properties of object loaded from the database with properties of object source
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class IntactDbMergerOverride<A extends AbstractAuditable> extends AbstractIntactDbMerger<A> {
    @Override
    protected void mergeOtherProperties(A obj1, A obj2) {
        // nothing to do
    }

    @Override
    protected void mergeCreator(A obj1, A obj2) {
        obj2.setCreator(obj1.getCreator());
    }

    @Override
    protected void mergeCreatedDate(A obj1, A obj2) {
        obj2.setCreated(obj1.getCreated());
    }

    @Override
    protected void mergeUpdateDate(A obj1, A obj2) {
        obj2.setUpdated(obj1.getUpdated());
    }

    @Override
    protected void mergeUpdator(A obj1, A obj2) {
        obj2.setUpdator(obj1.getUpdator());
    }
}
