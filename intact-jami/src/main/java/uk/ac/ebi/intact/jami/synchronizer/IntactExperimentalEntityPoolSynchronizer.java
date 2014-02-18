package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.Entity;
import psidev.psi.mi.jami.model.ExperimentalEntity;
import psidev.psi.mi.jami.model.ExperimentalEntityPool;
import psidev.psi.mi.jami.utils.clone.ParticipantCloner;
import uk.ac.ebi.intact.jami.merger.IntactExperimentalEntityPoolMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactEntity;
import uk.ac.ebi.intact.jami.model.extension.IntactExperimentalEntityPool;

import javax.persistence.EntityManager;
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

public class IntactExperimentalEntityPoolSynchronizer extends IntactExperimentalEntityBaseSynchronizer<ExperimentalEntityPool,IntactExperimentalEntityPool> {

    private IntactDbSynchronizer<Entity, AbstractIntactEntity> entitySynchronizer;

    public IntactExperimentalEntityPoolSynchronizer(EntityManager entityManager) {
        super(entityManager, IntactExperimentalEntityPool.class);
    }

    public IntactDbSynchronizer<Entity, AbstractIntactEntity> getEntitySynchronizer() {
        if (this.entitySynchronizer == null){
            this.entitySynchronizer = new IntActEntitySynchronizer(getEntityManager());
            ((IntActEntitySynchronizer)this.entitySynchronizer).setExperimentalEntityPoolSynchronizer(this);
        }
        return entitySynchronizer;
    }

    public void setEntitySynchronizer(IntactDbSynchronizer<Entity, AbstractIntactEntity> entitySynchronizer) {
        this.entitySynchronizer = entitySynchronizer;
    }

    @Override
    public void synchronizeProperties(IntactExperimentalEntityPool intactEntity) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizeProperties(intactEntity);

        // then synchronize subEntities if not done
        prepareEntities(intactEntity);
    }

    protected void prepareEntities(IntactExperimentalEntityPool intactEntity) throws FinderException, PersisterException, SynchronizerException {
        if (intactEntity.areEntitiesInitialized()){
            List<ExperimentalEntity> entitiesToPersist = new ArrayList<ExperimentalEntity>(intactEntity);
            for (ExperimentalEntity entity : entitiesToPersist){
                ExperimentalEntity persistentEntity = (ExperimentalEntity)getEntitySynchronizer().synchronize(entity, true);
                // we have a different instance because needed to be synchronized
                if (persistentEntity != entity){
                    intactEntity.remove(entity);
                    intactEntity.add(persistentEntity);
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
        getEntitySynchronizer().clearCache();
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactExperimentalEntityPoolMergerEnrichOnly());
    }
}
