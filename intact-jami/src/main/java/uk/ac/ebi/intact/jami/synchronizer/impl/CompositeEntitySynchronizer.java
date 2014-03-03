package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMerger;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.EntitySynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

/**
 * Synchronizer for all participants
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>12/02/14</pre>
 */

public class CompositeEntitySynchronizer implements EntitySynchronizer<Entity, AbstractIntactEntity>{

    private SynchronizerContext context;

    public CompositeEntitySynchronizer(SynchronizerContext context){
        if (context == null){
            throw new IllegalArgumentException("An IntAct database synchronizer needs a non null synchronizer context");
        }
        this.context = context;
    }

    public AbstractIntactEntity find(Entity term) throws FinderException {
        // experimental
        if (term instanceof ExperimentalEntity){
            if (term instanceof ExperimentalEntityPool){
                return this.context.getExperimentalEntityPoolSynchronizer().find((ExperimentalEntityPool)term);
            }
            else if (term instanceof ParticipantEvidence){
                return this.context.getParticipantEvidenceSynchronizer().find((ParticipantEvidence)term);
            }
            else{
                return this.context.getExperimentalEntitySynchronizer().find((ExperimentalEntity)term);
            }
        }
        // modelled
        else {
            if (term instanceof ModelledEntityPool){
                return this.context.getModelledEntityPoolSynchronizer().find((ModelledEntityPool)term);
            }
            else if (term instanceof ModelledParticipant){
                return this.context.getModelledParticipantSynchronizer().find((ModelledParticipant)term);
            }
            else{
                return this.context.getModelledEntitySynchronizer().find((ModelledEntity)term);
            }
        }
    }

    public AbstractIntactEntity persist(AbstractIntactEntity term) throws FinderException, PersisterException, SynchronizerException {
        // experimental
        if (term instanceof AbstractIntactExperimentalEntity){
            if (term instanceof IntactExperimentalEntityPool){
                return this.context.getExperimentalEntityPoolSynchronizer().persist((IntactExperimentalEntityPool)term);
            }
            else if (term instanceof IntactParticipantEvidence){
                return this.context.getParticipantEvidenceSynchronizer().persist((IntactParticipantEvidence)term);
            }
            else{
                return this.context.getExperimentalEntitySynchronizer().persist((IntactExperimentalEntity)term);
            }
        }
        // modelled
        else {
            if (term instanceof IntactModelledEntityPool){
                return this.context.getModelledEntityPoolSynchronizer().persist((IntactModelledEntityPool)term);
            }
            else if (term instanceof IntactModelledParticipant){
                return this.context.getModelledParticipantSynchronizer().persist((IntactModelledParticipant)term);
            }
            else{
                return this.context.getModelledEntitySynchronizer().persist((IntactModelledEntity)term);
            }
        }
    }

    public AbstractIntactEntity synchronize(Entity term, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        // experimental
        if (term instanceof ExperimentalEntity){
            if (term instanceof ExperimentalEntityPool){
                return this.context.getExperimentalEntityPoolSynchronizer().synchronize((ExperimentalEntityPool)term, persist);
            }
            else if (term instanceof ParticipantEvidence){
                return this.context.getParticipantEvidenceSynchronizer().synchronize((ParticipantEvidence)term, persist);
            }
            else{
                return this.context.getExperimentalEntitySynchronizer().synchronize((ExperimentalEntity)term, persist);
            }
        }
        // modelled
        else {
            if (term instanceof ModelledEntityPool){
                return this.context.getModelledEntityPoolSynchronizer().synchronize((ModelledEntityPool)term, persist);
            }
            else if (term instanceof ModelledParticipant){
                return this.context.getModelledParticipantSynchronizer().synchronize((ModelledParticipant)term, persist);
            }
            else{
                return this.context.getModelledEntitySynchronizer().synchronize((ModelledEntity)term, persist);
            }
        }
    }

    public void synchronizeProperties(AbstractIntactEntity term) throws FinderException, PersisterException, SynchronizerException {
        // experimental
        if (term instanceof AbstractIntactExperimentalEntity){
            if (term instanceof IntactExperimentalEntityPool){
                this.context.getExperimentalEntityPoolSynchronizer().synchronizeProperties((IntactExperimentalEntityPool)term);
            }
            else if (term instanceof IntactParticipantEvidence){
                this.context.getParticipantEvidenceSynchronizer().synchronizeProperties((IntactParticipantEvidence)term);
            }
            else{
                this.context.getExperimentalEntitySynchronizer().synchronizeProperties((IntactExperimentalEntity)term);
            }
        }
        // modelled
        else {
            if (term instanceof IntactModelledEntityPool){
                this.context.getModelledEntityPoolSynchronizer().synchronizeProperties((IntactModelledEntityPool)term);
            }
            else if (term instanceof IntactModelledParticipant){
                this.context.getModelledParticipantSynchronizer().synchronizeProperties((IntactModelledParticipant)term);
            }
            else{
                this.context.getModelledEntitySynchronizer().synchronizeProperties((IntactModelledEntity)term);
            }
        }
    }

    public void clearCache() {
        // nothing to do
    }

    public IntactDbMerger<Entity, AbstractIntactEntity> getIntactMerger() {
        return null;
    }

    public void setIntactMerger(IntactDbMerger<Entity, AbstractIntactEntity> intactMerger) {
        throw new UnsupportedOperationException("The entity synchronizer does not support this method as it is a composite synchronizer");
    }

    public Class<? extends AbstractIntactEntity> getIntactClass() {
        return AbstractIntactEntity.class;
    }

    public void setIntactClass(Class<? extends AbstractIntactEntity> intactClass) {
        throw new UnsupportedOperationException("The entity synchronizer does not support this method as it is a composite synchronizer");
    }

    public boolean delete(Entity term) {
        // experimental
        if (term instanceof ExperimentalEntity){
            if (term instanceof ExperimentalEntityPool){
                return this.context.getExperimentalEntityPoolSynchronizer().delete((ExperimentalEntityPool)term);
            }
            else if (term instanceof ParticipantEvidence){
                return this.context.getParticipantEvidenceSynchronizer().delete((ParticipantEvidence)term);
            }
            else{
                return this.context.getExperimentalEntitySynchronizer().delete((ExperimentalEntity)term);
            }
        }
        // modelled
        else {
            if (term instanceof ModelledEntityPool){
                return this.context.getModelledEntityPoolSynchronizer().delete((ModelledEntityPool)term);
            }
            else if (term instanceof ModelledParticipant){
                return this.context.getModelledParticipantSynchronizer().delete((ModelledParticipant)term);
            }
            else{
                return this.context.getModelledEntitySynchronizer().delete((ModelledEntity)term);
            }
        }
    }
}
