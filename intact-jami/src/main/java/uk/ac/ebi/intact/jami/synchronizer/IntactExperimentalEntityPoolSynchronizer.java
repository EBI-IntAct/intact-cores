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

public class IntactExperimentalEntityPoolSynchronizer extends IntactExperimentalEntityBaseSynchronizer<ExperimentalEntityPool,IntactExperimentalEntityPool> {

    private IntactDbSynchronizer<ExperimentalEntity, IntactExperimentalEntity> entitySynchronizer;

    public IntactExperimentalEntityPoolSynchronizer(EntityManager entityManager) {
        super(entityManager, IntactExperimentalEntityPool.class);
        this.entitySynchronizer = new IntActEntitySynchronizer<ExperimentalEntity, IntactExperimentalEntity, ExperimentalEntityPool, IntactExperimentalEntityPool>(entityManager,
                IntactExperimentalEntity.class, IntactExperimentalEntityPool.class,
                new IntactExperimentalEntityBaseSynchronizer<ExperimentalEntity, IntactExperimentalEntity>(entityManager, IntactExperimentalEntity.class),
                this);
    }

    public IntactExperimentalEntityPoolSynchronizer(EntityManager entityManager,
                                                    IntactDbSynchronizer<Alias, EntityAlias> aliasSynchronizer,
                                                    IntactDbSynchronizer<Annotation, EntityAnnotation> annotationSynchronizer,
                                                    IntactDbSynchronizer<Xref, EntityXref> xrefSynchronizer,
                                                    IntactDbSynchronizer<CvTerm, IntactCvTerm> biologicalRoleSynchronizer,
                                                    IntactDbSynchronizer<Feature, AbstractIntactFeature> featureSynchronizer,
                                                    IntactDbSynchronizer<CausalRelationship,
                                                    IntactCausalRelationship> causalRelationshipSynchronizer,
                                                    IntactDbSynchronizer<Interactor, IntactInteractor> interactorSynchronizer,
                                                    IntactDbSynchronizer<ExperimentalEntity, IntactExperimentalEntity> entitySynchronizer,
                                                    IntactDbSynchronizer<Parameter, ExperimentalEntityParameter> parameterSynchronizer,
                                                    IntactDbSynchronizer<Confidence, ExperimentalEntityConfidence> confidenceSynchronizer,
                                                    IntactDbSynchronizer<CvTerm, IntactCvTerm> identificationMethodSynchronizer,
                                                    IntactDbSynchronizer<CvTerm, IntactCvTerm> experimentalPreparationSynchronizer,
                                                    IntactDbSynchronizer<CvTerm, IntactCvTerm> experimentalRoleSynchronizer,
                                                    IntactDbSynchronizer<Organism, IntactOrganism> organismSynchronizer) {
        super(entityManager, IntactExperimentalEntityPool.class, aliasSynchronizer, annotationSynchronizer, xrefSynchronizer, biologicalRoleSynchronizer,
                featureSynchronizer, causalRelationshipSynchronizer, interactorSynchronizer, parameterSynchronizer, confidenceSynchronizer, identificationMethodSynchronizer, experimentalPreparationSynchronizer,
                experimentalRoleSynchronizer, organismSynchronizer);
        this.entitySynchronizer = entitySynchronizer != null ? entitySynchronizer : new IntActEntitySynchronizer<ExperimentalEntity, IntactExperimentalEntity, ExperimentalEntityPool, IntactExperimentalEntityPool>(entityManager,
                IntactExperimentalEntity.class, IntactExperimentalEntityPool.class,
                new IntactExperimentalEntityBaseSynchronizer<ExperimentalEntity, IntactExperimentalEntity>(entityManager, IntactExperimentalEntity.class),
                this);
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
                IntactExperimentalEntity persistentEntity = this.entitySynchronizer.synchronize(entity, true);
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
        IntactExperimentalEntityPool newParticipant = new IntactExperimentalEntityPool((InteractorPool)object.getInteractor());
        ParticipantCloner.copyAndOverrideExperimentalEntityPoolProperties(object, newParticipant, false);
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
