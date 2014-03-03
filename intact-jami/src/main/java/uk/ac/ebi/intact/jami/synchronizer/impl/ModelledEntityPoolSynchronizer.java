package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ModelledEntity;
import psidev.psi.mi.jami.model.ModelledEntityPool;
import psidev.psi.mi.jami.utils.clone.ParticipantCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.ModelledEntityPoolMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledEntityPool;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Synchronizer for IntAct modelled entity pools
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/01/14</pre>
 */

public class ModelledEntityPoolSynchronizer extends ModelledEntitySynchronizerTemplate<ModelledEntityPool,IntactModelledEntityPool> {

    public ModelledEntityPoolSynchronizer(SynchronizerContext context) {
        super(context, IntactModelledEntityPool.class);
    }

    @Override
    public void synchronizeProperties(IntactModelledEntityPool intactEntity) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizeProperties(intactEntity);

        // then synchronize subEntities if not done
        prepareEntities(intactEntity);

        CvTerm type = intactEntity.getType();
        intactEntity.setType(getContext().getInteractorTypeSynchronizer().synchronize(type, true));
    }

    protected void prepareEntities(IntactModelledEntityPool intactEntity) throws FinderException, PersisterException, SynchronizerException {
        if (intactEntity.areEntitiesInitialized()){
            List<ModelledEntity> entitiesToPersist = new ArrayList<ModelledEntity>(intactEntity);
            for (ModelledEntity entity : entitiesToPersist){
                if (intactEntity != entity){
                    ModelledEntity persistentEntity = (ModelledEntity) getContext().getEntitySynchronizer().synchronize(entity, true);
                    // we have a different instance because needed to be synchronized
                    if (persistentEntity != entity){
                        intactEntity.remove(entity);
                        intactEntity.add(persistentEntity);
                    }
                }
            }
        }
    }

    @Override
    protected IntactModelledEntityPool instantiateNewPersistentInstance( ModelledEntityPool object, Class<? extends IntactModelledEntityPool> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactModelledEntityPool newParticipant = new IntactModelledEntityPool(object.getInteractor().getShortName());
        ParticipantCloner.copyAndOverrideParticipantPoolProperties(object, newParticipant, false);
        return newParticipant;
    }

    @Override
    public void clearCache() {
        super.clearCache();
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new ModelledEntityPoolMergerEnrichOnly());
    }
}
