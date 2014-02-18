package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.Entity;
import psidev.psi.mi.jami.model.ModelledEntity;
import psidev.psi.mi.jami.model.ModelledEntityPool;
import psidev.psi.mi.jami.utils.clone.ParticipantCloner;
import uk.ac.ebi.intact.jami.merger.IntactModelledEntityPoolMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactEntity;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledEntityPool;

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

public class IntactModelledEntityPoolSynchronizer extends IntactEntityBaseSynchronizer<ModelledEntityPool,IntactModelledEntityPool> {

    private IntactDbSynchronizer<Entity, AbstractIntactEntity> entitySynchronizer;

    public IntactModelledEntityPoolSynchronizer(EntityManager entityManager) {
        super(entityManager, IntactModelledEntityPool.class);
    }

    @Override
    public void synchronizeProperties(IntactModelledEntityPool intactEntity) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizeProperties(intactEntity);

        // then synchronize subEntities if not done
        prepareEntities(intactEntity);
    }

    public IntactDbSynchronizer<Entity, AbstractIntactEntity> getEntitySynchronizer() {
        if (this.entitySynchronizer == null){
            this.entitySynchronizer = new IntActEntitySynchronizer(getEntityManager());
            ((IntActEntitySynchronizer)this.entitySynchronizer).setModelledEntityPoolSynchronizer(this);
        }
        return entitySynchronizer;
    }

    public void setEntitySynchronizer(IntactDbSynchronizer<Entity, AbstractIntactEntity> entitySynchronizer) {
        this.entitySynchronizer = entitySynchronizer;
    }

    protected void prepareEntities(IntactModelledEntityPool intactEntity) throws FinderException, PersisterException, SynchronizerException {
        if (intactEntity.areEntitiesInitialized()){
            List<ModelledEntity> entitiesToPersist = new ArrayList<ModelledEntity>(intactEntity);
            for (ModelledEntity entity : entitiesToPersist){
                ModelledEntity persistentEntity = (ModelledEntity) getEntitySynchronizer().synchronize(entity, true);
                // we have a different instance because needed to be synchronized
                if (persistentEntity != entity){
                    intactEntity.remove(entity);
                    intactEntity.add(persistentEntity);
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
        getEntitySynchronizer().clearCache();
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactModelledEntityPoolMergerEnrichOnly());
    }
}
