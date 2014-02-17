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
    }

    public AbstractIntactEntity find(Entity term) throws FinderException{
        // experimental
        if (term instanceof ExperimentalEntity){
            if (term instanceof ExperimentalEntityPool){
                return getExperimentalEntityPoolSynchronizer().find((ExperimentalEntityPool)term);
            }
            else if (term instanceof ParticipantEvidence){
                return getParticipantEvidenceSynchronizer().find((ParticipantEvidence)term);
            }
            else{
                return getExperimentalEntitySynchronizer().find((ExperimentalEntity)term);
            }
        }
        // modelled
        else {
            if (term instanceof ModelledEntityPool){
                return getModelledEntityPoolSynchronizer().find((ModelledEntityPool)term);
            }
            else if (term instanceof ModelledParticipant){
                return getModelledParticipantSynchronizer().find((ModelledParticipant)term);
            }
            else{
                return getModelledEntitySynchronizer().find((ModelledEntity)term);
            }
        }
    }

    public AbstractIntactEntity persist(AbstractIntactEntity term) throws FinderException, PersisterException, SynchronizerException{
        // experimental
        if (term instanceof AbstractIntactExperimentalEntity){
            if (term instanceof IntactExperimentalEntityPool){
                return getExperimentalEntityPoolSynchronizer().persist((IntactExperimentalEntityPool)term);
            }
            else if (term instanceof IntactParticipantEvidence){
                return getParticipantEvidenceSynchronizer().persist((IntactParticipantEvidence)term);
            }
            else{
                return getExperimentalEntitySynchronizer().persist((IntactExperimentalEntity)term);
            }
        }
        // modelled
        else {
            if (term instanceof IntactModelledEntityPool){
                return getModelledEntityPoolSynchronizer().persist((IntactModelledEntityPool)term);
            }
            else if (term instanceof IntactModelledParticipant){
                return getModelledParticipantSynchronizer().persist((IntactModelledParticipant)term);
            }
            else{
                return getModelledEntitySynchronizer().persist((IntactModelledEntity)term);
            }
        }
    }

    @Override
    public AbstractIntactEntity synchronize(Entity term, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        // experimental
        if (term instanceof AbstractIntactExperimentalEntity){
            if (term instanceof IntactExperimentalEntityPool){
                return getExperimentalEntityPoolSynchronizer().synchronize((IntactExperimentalEntityPool)term, persist);
            }
            else if (term instanceof IntactParticipantEvidence){
                return getParticipantEvidenceSynchronizer().synchronize((IntactParticipantEvidence)term, persist);
            }
            else{
                return getExperimentalEntitySynchronizer().synchronize((IntactExperimentalEntity)term, persist);
            }
        }
        // modelled
        else {
            if (term instanceof IntactModelledEntityPool){
                return getModelledEntityPoolSynchronizer().synchronize((IntactModelledEntityPool)term, persist);
            }
            else if (term instanceof IntactModelledParticipant){
                return getModelledParticipantSynchronizer().synchronize((IntactModelledParticipant)term, persist);
            }
            else{
                return getModelledEntitySynchronizer().synchronize((IntactModelledEntity)term, persist);
            }
        }
    }

    public void synchronizeProperties(AbstractIntactEntity term) throws FinderException, PersisterException, SynchronizerException {
        // experimental
        if (term instanceof AbstractIntactExperimentalEntity){
            if (term instanceof IntactExperimentalEntityPool){
                getExperimentalEntityPoolSynchronizer().synchronizeProperties((IntactExperimentalEntityPool)term);
            }
            else if (term instanceof IntactParticipantEvidence){
                getParticipantEvidenceSynchronizer().synchronizeProperties((IntactParticipantEvidence)term);
            }
            else{
                getExperimentalEntitySynchronizer().synchronizeProperties((IntactExperimentalEntity)term);
            }
        }
        // modelled
        else {
            if (term instanceof IntactModelledEntityPool){
               getModelledEntityPoolSynchronizer().synchronizeProperties((IntactModelledEntityPool)term);
            }
            else if (term instanceof IntactModelledParticipant){
                getModelledParticipantSynchronizer().synchronizeProperties((IntactModelledParticipant)term);
            }
            else{
                getModelledEntitySynchronizer().synchronizeProperties((IntactModelledEntity)term);
            }
        }
    }

    public void clearCache() {
        getModelledEntitySynchronizer().clearCache();
        getModelledEntityPoolSynchronizer().clearCache();
        getModelledParticipantSynchronizer().clearCache();
        getExperimentalEntitySynchronizer().clearCache();
        getExperimentalEntityPoolSynchronizer().clearCache();
        getParticipantEvidenceSynchronizer().clearCache();
    }

    public IntactDbSynchronizer<ModelledEntity, IntactModelledEntity> getModelledEntitySynchronizer() {
        if (this.modelledEntitySynchronizer == null){
            this.modelledEntitySynchronizer = new IntactEntityBaseSynchronizer<ModelledEntity, IntactModelledEntity>(getEntityManager(), IntactModelledEntity.class);
        }
        return modelledEntitySynchronizer;
    }

    public IntactDbSynchronizer<ModelledParticipant, IntactModelledParticipant> getModelledParticipantSynchronizer() {
        if (this.modelledParticipantSynchronizer == null){
            this.modelledParticipantSynchronizer = new IntactEntityBaseSynchronizer<ModelledParticipant, IntactModelledParticipant>(getEntityManager(), IntactModelledParticipant.class);

        }
        return modelledParticipantSynchronizer;
    }

    public IntactDbSynchronizer<ModelledEntityPool, IntactModelledEntityPool> getModelledEntityPoolSynchronizer() {
        if (this.modelledEntityPoolSynchronizer == null){
            this.modelledEntityPoolSynchronizer = new IntactModelledEntityPoolSynchronizer(getEntityManager());
            ((IntactModelledEntityPoolSynchronizer)this.modelledEntityPoolSynchronizer).setEntitySynchronizer(this);
        }
        return modelledEntityPoolSynchronizer;
    }

    public IntactDbSynchronizer<ExperimentalEntity, IntactExperimentalEntity> getExperimentalEntitySynchronizer() {
        if (this.experimentalEntitySynchronizer == null){
            this.experimentalEntitySynchronizer = new IntactEntityBaseSynchronizer<ExperimentalEntity, IntactExperimentalEntity>(getEntityManager(), IntactExperimentalEntity.class);
        }
        return experimentalEntitySynchronizer;
    }

    public IntactDbSynchronizer<ParticipantEvidence, IntactParticipantEvidence> getParticipantEvidenceSynchronizer() {
        if (this.participantEvidenceSynchronizer == null){
            this.participantEvidenceSynchronizer = new IntactEntityBaseSynchronizer<ParticipantEvidence, IntactParticipantEvidence>(getEntityManager(), IntactParticipantEvidence.class);
        }
        return participantEvidenceSynchronizer;
    }

    public IntactDbSynchronizer<ExperimentalEntityPool, IntactExperimentalEntityPool> getExperimentalEntityPoolSynchronizer() {
        if (this.experimentalEntityPoolSynchronizer == null){
            this.experimentalEntityPoolSynchronizer = new IntactExperimentalEntityPoolSynchronizer(getEntityManager());
            ((IntactExperimentalEntityPoolSynchronizer)this.experimentalEntityPoolSynchronizer).setEntitySynchronizer(this);
        }
        return experimentalEntityPoolSynchronizer;
    }

    public void setModelledEntitySynchronizer(IntactDbSynchronizer<ModelledEntity, IntactModelledEntity> modelledEntitySynchronizer) {
        this.modelledEntitySynchronizer = modelledEntitySynchronizer;
    }

    public void setModelledParticipantSynchronizer(IntactDbSynchronizer<ModelledParticipant, IntactModelledParticipant> modelledParticipantSynchronizer) {
        this.modelledParticipantSynchronizer = modelledParticipantSynchronizer;
    }

    public void setModelledEntityPoolSynchronizer(IntactDbSynchronizer<ModelledEntityPool, IntactModelledEntityPool> modelledEntityPoolSynchronizer) {
        this.modelledEntityPoolSynchronizer = modelledEntityPoolSynchronizer;
    }

    public void setExperimentalEntitySynchronizer(IntactDbSynchronizer<ExperimentalEntity, IntactExperimentalEntity> experimentalEntitySynchronizer) {
        this.experimentalEntitySynchronizer = experimentalEntitySynchronizer;
    }

    public void setParticipantEvidenceSynchronizer(IntactDbSynchronizer<ParticipantEvidence, IntactParticipantEvidence> participantEvidenceSynchronizer) {
        this.participantEvidenceSynchronizer = participantEvidenceSynchronizer;
    }

    public void setExperimentalEntityPoolSynchronizer(IntactDbSynchronizer<ExperimentalEntityPool, IntactExperimentalEntityPool> experimentalEntityPoolSynchronizer) {
        this.experimentalEntityPoolSynchronizer = experimentalEntityPoolSynchronizer;
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
