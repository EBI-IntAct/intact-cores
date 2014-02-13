package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.model.extension.*;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;

/**
 * Synchronizer for all participants
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>12/02/14</pre>
 */

public class IntActEntitySynchronizer extends AbstractIntactDbSynchronizer<Entity, AbstractIntactEntity>{

    private IntactDbSynchronizer<ModelledEntity, IntactModelledEntity> modelledEntitySynchronizer;
    private IntactDbSynchronizer<ModelledParticipant, IntactModelledParticipant> modelledParticipantSynchronizer;
    private IntactDbSynchronizer<ModelledEntityPool, IntactModelledEntityPool> modelledEntityPoolSynchronizer;
    private IntactDbSynchronizer<ExperimentalEntity, IntactExperimentalEntity> experimentalEntitySynchronizer;
    private IntactDbSynchronizer<ParticipantEvidence, IntactParticipantEvidence> participantEvidenceSynchronizer;
    private IntactDbSynchronizer<ExperimentalEntityPool, IntactExperimentalEntityPool> experimentalEntityPoolSynchronizer;

    public IntActEntitySynchronizer(EntityManager entityManager){
        super(entityManager, AbstractIntactEntity.class);
        this.modelledEntitySynchronizer = new IntactEntityBaseSynchronizer<ModelledEntity, IntactModelledEntity>(entityManager, IntactModelledEntity.class);
        this.modelledEntityPoolSynchronizer = new IntactModelledEntityPoolSynchronizer(entityManager, null, null, null, null, null,
                null, null, this);
        this.modelledParticipantSynchronizer = new IntactEntityBaseSynchronizer<ModelledParticipant, IntactModelledParticipant>(entityManager, IntactModelledParticipant.class);
        this.experimentalEntitySynchronizer = new IntactEntityBaseSynchronizer<ExperimentalEntity, IntactExperimentalEntity>(entityManager, IntactExperimentalEntity.class);
        this.experimentalEntityPoolSynchronizer = new IntactExperimentalEntityPoolSynchronizer(entityManager, null, null, null, null, null,
                null, null, null, null, null, null, null, null, this);
        this.participantEvidenceSynchronizer = new IntactEntityBaseSynchronizer<ParticipantEvidence, IntactParticipantEvidence>(entityManager, IntactParticipantEvidence.class);
    }

    public IntActEntitySynchronizer(EntityManager entityManager,
                                    IntactDbSynchronizer<ModelledEntity, IntactModelledEntity> modelledEntitySynchronizer,
                                    IntactDbSynchronizer<ModelledParticipant, IntactModelledParticipant> modelledParticipantSynchronizer,
                                    IntactDbSynchronizer<ModelledEntityPool, IntactModelledEntityPool> modelledEntityPoolSynchronizer,
                                    IntactDbSynchronizer<ExperimentalEntity, IntactExperimentalEntity> experimentalEntitySynchronizer,
                                    IntactDbSynchronizer<ParticipantEvidence, IntactParticipantEvidence> participantEvidenceSynchronizer,
                                    IntactDbSynchronizer<ExperimentalEntityPool, IntactExperimentalEntityPool> experimentalEntityPoolSynchronizer){
        super(entityManager, AbstractIntactEntity.class);
        this.modelledEntitySynchronizer = modelledEntitySynchronizer != null ? modelledEntitySynchronizer : new IntactEntityBaseSynchronizer<ModelledEntity, IntactModelledEntity>(entityManager, IntactModelledEntity.class);
        this.modelledEntityPoolSynchronizer = modelledEntityPoolSynchronizer != null ? modelledEntityPoolSynchronizer : new IntactModelledEntityPoolSynchronizer(entityManager, null, null, null, null, null,
                null, null, this);
        this.modelledParticipantSynchronizer = modelledParticipantSynchronizer != null ? modelledParticipantSynchronizer : new IntactEntityBaseSynchronizer<ModelledParticipant, IntactModelledParticipant>(entityManager, IntactModelledParticipant.class);
        this.experimentalEntitySynchronizer = experimentalEntitySynchronizer != null ? experimentalEntitySynchronizer : new IntactEntityBaseSynchronizer<ExperimentalEntity, IntactExperimentalEntity>(entityManager, IntactExperimentalEntity.class);
        this.experimentalEntityPoolSynchronizer = experimentalEntityPoolSynchronizer != null ? experimentalEntityPoolSynchronizer : new IntactExperimentalEntityPoolSynchronizer(entityManager, null, null, null, null, null,
                null, null, null, null, null, null, null, null, this);
        this.participantEvidenceSynchronizer = participantEvidenceSynchronizer != null ? participantEvidenceSynchronizer : new IntactEntityBaseSynchronizer<ParticipantEvidence, IntactParticipantEvidence>(entityManager, IntactParticipantEvidence.class);
    }

    public AbstractIntactEntity find(Entity term) throws FinderException{
        // experimental
        if (term instanceof ExperimentalEntity){
            if (term instanceof ExperimentalEntityPool){
                return this.experimentalEntityPoolSynchronizer.find((ExperimentalEntityPool)term);
            }
            else if (term instanceof ParticipantEvidence){
                return this.participantEvidenceSynchronizer.find((ParticipantEvidence)term);
            }
            else{
                return this.experimentalEntitySynchronizer.find((ExperimentalEntity)term);
            }
        }
        // modelled
        else {
            if (term instanceof ModelledEntityPool){
                return this.modelledEntityPoolSynchronizer.find((ModelledEntityPool)term);
            }
            else if (term instanceof ModelledParticipant){
                return this.modelledParticipantSynchronizer.find((ModelledParticipant)term);
            }
            else{
                return this.modelledEntitySynchronizer.find((ModelledEntity)term);
            }
        }
    }

    public AbstractIntactEntity persist(AbstractIntactEntity term) throws FinderException, PersisterException, SynchronizerException{
        // experimental
        if (term instanceof AbstractIntactExperimentalEntity){
            if (term instanceof IntactExperimentalEntityPool){
                return this.experimentalEntityPoolSynchronizer.persist((IntactExperimentalEntityPool)term);
            }
            else if (term instanceof IntactParticipantEvidence){
                return this.participantEvidenceSynchronizer.persist((IntactParticipantEvidence)term);
            }
            else{
                return this.experimentalEntitySynchronizer.persist((IntactExperimentalEntity)term);
            }
        }
        // modelled
        else {
            if (term instanceof IntactModelledEntityPool){
                return this.modelledEntityPoolSynchronizer.persist((IntactModelledEntityPool)term);
            }
            else if (term instanceof IntactModelledParticipant){
                return this.modelledParticipantSynchronizer.persist((IntactModelledParticipant)term);
            }
            else{
                return this.modelledEntitySynchronizer.persist((IntactModelledEntity)term);
            }
        }
    }

    @Override
    public AbstractIntactEntity synchronize(Entity term, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        // experimental
        if (term instanceof AbstractIntactExperimentalEntity){
            if (term instanceof IntactExperimentalEntityPool){
                return this.experimentalEntityPoolSynchronizer.synchronize((IntactExperimentalEntityPool)term, persist);
            }
            else if (term instanceof IntactParticipantEvidence){
                return this.participantEvidenceSynchronizer.synchronize((IntactParticipantEvidence)term, persist);
            }
            else{
                return this.experimentalEntitySynchronizer.synchronize((IntactExperimentalEntity)term, persist);
            }
        }
        // modelled
        else {
            if (term instanceof IntactModelledEntityPool){
                return this.modelledEntityPoolSynchronizer.synchronize((IntactModelledEntityPool)term, persist);
            }
            else if (term instanceof IntactModelledParticipant){
                return this.modelledParticipantSynchronizer.synchronize((IntactModelledParticipant)term, persist);
            }
            else{
                return this.modelledEntitySynchronizer.synchronize((IntactModelledEntity)term, persist);
            }
        }
    }

    public void synchronizeProperties(AbstractIntactEntity term) throws FinderException, PersisterException, SynchronizerException {
        // experimental
        if (term instanceof AbstractIntactExperimentalEntity){
            if (term instanceof IntactExperimentalEntityPool){
                this.experimentalEntityPoolSynchronizer.synchronizeProperties((IntactExperimentalEntityPool)term);
            }
            else if (term instanceof IntactParticipantEvidence){
                this.participantEvidenceSynchronizer.synchronizeProperties((IntactParticipantEvidence)term);
            }
            else{
                this.experimentalEntitySynchronizer.synchronizeProperties((IntactExperimentalEntity)term);
            }
        }
        // modelled
        else {
            if (term instanceof IntactModelledEntityPool){
               this.modelledEntityPoolSynchronizer.synchronizeProperties((IntactModelledEntityPool)term);
            }
            else if (term instanceof IntactModelledParticipant){
                this.modelledParticipantSynchronizer.synchronizeProperties((IntactModelledParticipant)term);
            }
            else{
                this.modelledEntitySynchronizer.synchronizeProperties((IntactModelledEntity)term);
            }
        }
    }

    public void clearCache() {
        this.modelledEntitySynchronizer.clearCache();
        this.modelledEntityPoolSynchronizer.clearCache();
        this.modelledParticipantSynchronizer.clearCache();
        this.experimentalEntityPoolSynchronizer.clearCache();
        this.experimentalEntitySynchronizer.clearCache();
        this.participantEvidenceSynchronizer.clearCache();
    }

    public IntactDbSynchronizer<ModelledEntity, IntactModelledEntity> getModelledEntitySynchronizer() {
        return modelledEntitySynchronizer;
    }

    public IntactDbSynchronizer<ModelledParticipant, IntactModelledParticipant> getModelledParticipantSynchronizer() {
        return modelledParticipantSynchronizer;
    }

    public IntactDbSynchronizer<ModelledEntityPool, IntactModelledEntityPool> getModelledEntityPoolSynchronizer() {
        return modelledEntityPoolSynchronizer;
    }

    public IntactDbSynchronizer<ExperimentalEntity, IntactExperimentalEntity> getExperimentalEntitySynchronizer() {
        return experimentalEntitySynchronizer;
    }

    public IntactDbSynchronizer<ParticipantEvidence, IntactParticipantEvidence> getParticipantEvidenceSynchronizer() {
        return participantEvidenceSynchronizer;
    }

    public IntactDbSynchronizer<ExperimentalEntityPool, IntactExperimentalEntityPool> getExperimentalEntityPoolSynchronizer() {
        return experimentalEntityPoolSynchronizer;
    }

    @Override
    protected Object extractIdentifier(AbstractIntactEntity object) {
        return object.getAc();
    }

    @Override
    protected AbstractIntactEntity instantiateNewPersistentInstance(Entity object, Class<? extends AbstractIntactEntity> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        throw new UnsupportedOperationException("This synchronizer relies on delegate synchronizers and cannot be used this way");
    }
}
