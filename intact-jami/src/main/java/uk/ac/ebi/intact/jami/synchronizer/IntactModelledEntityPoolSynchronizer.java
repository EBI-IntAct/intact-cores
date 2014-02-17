package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.ParticipantCloner;
import uk.ac.ebi.intact.jami.merger.IntactEntityMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.*;

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
        this.entitySynchronizer = new IntActEntitySynchronizer(entityManager, null, null, this, null, null, null);
    }

    public IntactModelledEntityPoolSynchronizer(EntityManager entityManager,
                                                IntactDbSynchronizer<Alias, EntityAlias> aliasSynchronizer,
                                                IntactDbSynchronizer<Annotation, EntityAnnotation> annotationSynchronizer,
                                                IntactDbSynchronizer<Xref, EntityXref> xrefSynchronizer,
                                                IntactDbSynchronizer<CvTerm, IntactCvTerm> biologicalRoleSynchronizer,
                                                IntactDbSynchronizer<Feature, AbstractIntactFeature> featureSynchronizer,
                                                IntactDbSynchronizer<CausalRelationship,
                                                        IntactCausalRelationship> causalRelationshipSynchronizer,
                                                IntactDbSynchronizer<Interactor, IntactInteractor> interactorSynchronizer,
                                                IntactDbSynchronizer<Entity, AbstractIntactEntity> entitySynchronizer) {
        super(entityManager, IntactModelledEntityPool.class, aliasSynchronizer, annotationSynchronizer, xrefSynchronizer, biologicalRoleSynchronizer, featureSynchronizer, causalRelationshipSynchronizer, interactorSynchronizer);
        this.entitySynchronizer = entitySynchronizer != null ? entitySynchronizer : new IntActEntitySynchronizer(entityManager, null, null, this, null, null, null);
    }

    @Override
    public void synchronizeProperties(IntactModelledEntityPool intactEntity) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizeProperties(intactEntity);

        // then synchronize subEntities if not done
        prepareEntities(intactEntity);
    }

    public IntactDbSynchronizer<Entity, AbstractIntactEntity> getEntitySynchronizer() {
        return entitySynchronizer;
    }

    public void setEntitySynchronizer(IntactDbSynchronizer<Entity, AbstractIntactEntity> entitySynchronizer) {
        this.entitySynchronizer = entitySynchronizer;
    }

    protected void prepareEntities(IntactModelledEntityPool intactEntity) throws FinderException, PersisterException, SynchronizerException {
        if (intactEntity.areEntitiesInitialized()){
            List<ModelledEntity> entitiesToPersist = new ArrayList<ModelledEntity>(intactEntity);
            for (ModelledEntity entity : entitiesToPersist){
                ModelledEntity persistentEntity = (ModelledEntity) this.entitySynchronizer.synchronize(entity, true);
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
        IntactModelledEntityPool newParticipant = new IntactModelledEntityPool((InteractorPool)object.getInteractor());
        ParticipantCloner.copyAndOverrideParticipantPoolProperties(object, newParticipant, false);
        return newParticipant;
    }

    @Override
    public void clearCache() {
        super.clearCache();
        this.entitySynchronizer.clearCache();
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactEntityMergerEnrichOnly<T,I,Feature>());
    }
}
