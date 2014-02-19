package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.ModelledEntity;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledEntity;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;

/**
 * Synchronizer for experimental entities and participants
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/01/14</pre>
 */

public class ModelledEntitySynchronizerTemplate<T extends ModelledEntity, I extends IntactModelledEntity> extends EntitySynchronizerTemplate<T, I> {

    public ModelledEntitySynchronizerTemplate(SynchronizerContext context, Class<I> intactClass){
        super(context, intactClass);
    }

    @Override
    protected IntactDbSynchronizer getFeatureSynchronizer() {
        return getContext().getModelledFeatureSynchronizer();
    }
}


