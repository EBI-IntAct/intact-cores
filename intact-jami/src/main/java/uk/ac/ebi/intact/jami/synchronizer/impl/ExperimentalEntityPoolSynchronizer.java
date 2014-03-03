package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ExperimentalEntity;
import psidev.psi.mi.jami.model.ExperimentalEntityPool;
import psidev.psi.mi.jami.utils.clone.ParticipantCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.ExperimentalEntityPoolMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.IntactExperimentalEntityPool;
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

public class ExperimentalEntityPoolSynchronizer extends ExperimentalEntitySynchronizerTemplate<ExperimentalEntityPool,IntactExperimentalEntityPool> {

    public ExperimentalEntityPoolSynchronizer(SynchronizerContext context) {
        super(context, IntactExperimentalEntityPool.class);
    }

    @Override
    public void synchronizeProperties(IntactExperimentalEntityPool intactEntity) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizeProperties(intactEntity);

        // then synchronize subEntities if not done
        prepareEntities(intactEntity);

        CvTerm type = intactEntity.getType();
        intactEntity.setType(getContext().getInteractorTypeSynchronizer().synchronize(type, true));
    }

    protected void prepareEntities(IntactExperimentalEntityPool intactEntity) throws FinderException, PersisterException, SynchronizerException {
        if (intactEntity.areEntitiesInitialized()){
            List<ExperimentalEntity> entitiesToPersist = new ArrayList<ExperimentalEntity>(intactEntity);
            for (ExperimentalEntity entity : entitiesToPersist){
                if (entity != intactEntity){
                    ExperimentalEntity persistentEntity = (ExperimentalEntity)getContext().getEntitySynchronizer().synchronize(entity, true);
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
    protected IntactExperimentalEntityPool instantiateNewPersistentInstance( ExperimentalEntityPool object, Class<? extends IntactExperimentalEntityPool> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactExperimentalEntityPool newParticipant = new IntactExperimentalEntityPool(object.getInteractor().getShortName());
        ParticipantCloner.copyAndOverrideExperimentalEntityPoolProperties(object, newParticipant, false);
        return newParticipant;
    }

    @Override
    public void clearCache() {
        super.clearCache();
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new ExperimentalEntityPoolMergerEnrichOnly());
    }
}
