package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.*;

import java.lang.reflect.InvocationTargetException;

/**
 * Synchronizer for all participants
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>12/02/14</pre>
 */

public class CompositeEntitySynchronizer extends AbstractIntactDbSynchronizer<Entity, AbstractIntactEntity>
implements EntitySynchronizer<Entity, AbstractIntactEntity>{

    public CompositeEntitySynchronizer(SynchronizerContext context){
        super(context, AbstractIntactEntity.class);
    }

    public AbstractIntactEntity find(Entity term) throws FinderException {
        // experimental
        if (term instanceof ExperimentalEntity){
            if (term instanceof ExperimentalEntityPool){
                return getContext().getExperimentalEntityPoolSynchronizer().find((ExperimentalEntityPool)term);
            }
            else if (term instanceof ParticipantEvidence){
                return getContext().getParticipantEvidenceSynchronizer().find((ParticipantEvidence)term);
            }
            else{
                return getContext().getExperimentalEntitySynchronizer().find((ExperimentalEntity)term);
            }
        }
        // modelled
        else {
            if (term instanceof ModelledEntityPool){
                return getContext().getModelledEntityPoolSynchronizer().find((ModelledEntityPool)term);
            }
            else if (term instanceof ModelledParticipant){
                return getContext().getModelledParticipantSynchronizer().find((ModelledParticipant)term);
            }
            else{
                return getContext().getModelledEntitySynchronizer().find((ModelledEntity)term);
            }
        }
    }

    public AbstractIntactEntity persist(AbstractIntactEntity term) throws FinderException, PersisterException, SynchronizerException {
        // experimental
        if (term instanceof AbstractIntactExperimentalEntity){
            if (term instanceof IntactExperimentalEntityPool){
                return getContext().getExperimentalEntityPoolSynchronizer().persist((IntactExperimentalEntityPool)term);
            }
            else if (term instanceof IntactParticipantEvidence){
                return getContext().getParticipantEvidenceSynchronizer().persist((IntactParticipantEvidence)term);
            }
            else{
                return getContext().getExperimentalEntitySynchronizer().persist((IntactExperimentalEntity)term);
            }
        }
        // modelled
        else {
            if (term instanceof IntactModelledEntityPool){
                return getContext().getModelledEntityPoolSynchronizer().persist((IntactModelledEntityPool)term);
            }
            else if (term instanceof IntactModelledParticipant){
                return getContext().getModelledParticipantSynchronizer().persist((IntactModelledParticipant)term);
            }
            else{
                return getContext().getModelledEntitySynchronizer().persist((IntactModelledEntity)term);
            }
        }
    }

    @Override
    public AbstractIntactEntity synchronize(Entity term, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        // experimental
        if (term instanceof AbstractIntactExperimentalEntity){
            if (term instanceof IntactExperimentalEntityPool){
                return getContext().getExperimentalEntityPoolSynchronizer().synchronize((IntactExperimentalEntityPool)term, persist);
            }
            else if (term instanceof IntactParticipantEvidence){
                return getContext().getParticipantEvidenceSynchronizer().synchronize((IntactParticipantEvidence)term, persist);
            }
            else{
                return getContext().getExperimentalEntitySynchronizer().synchronize((IntactExperimentalEntity)term, persist);
            }
        }
        // modelled
        else {
            if (term instanceof IntactModelledEntityPool){
                return getContext().getModelledEntityPoolSynchronizer().synchronize((IntactModelledEntityPool)term, persist);
            }
            else if (term instanceof IntactModelledParticipant){
                return getContext().getModelledParticipantSynchronizer().synchronize((IntactModelledParticipant)term, persist);
            }
            else{
                return getContext().getModelledEntitySynchronizer().synchronize((IntactModelledEntity)term, persist);
            }
        }
    }

    public void synchronizeProperties(AbstractIntactEntity term) throws FinderException, PersisterException, SynchronizerException {
        // experimental
        if (term instanceof AbstractIntactExperimentalEntity){
            if (term instanceof IntactExperimentalEntityPool){
                getContext().getExperimentalEntityPoolSynchronizer().synchronizeProperties((IntactExperimentalEntityPool)term);
            }
            else if (term instanceof IntactParticipantEvidence){
                getContext().getParticipantEvidenceSynchronizer().synchronizeProperties((IntactParticipantEvidence)term);
            }
            else{
                getContext().getExperimentalEntitySynchronizer().synchronizeProperties((IntactExperimentalEntity)term);
            }
        }
        // modelled
        else {
            if (term instanceof IntactModelledEntityPool){
                getContext().getModelledEntityPoolSynchronizer().synchronizeProperties((IntactModelledEntityPool)term);
            }
            else if (term instanceof IntactModelledParticipant){
                getContext().getModelledParticipantSynchronizer().synchronizeProperties((IntactModelledParticipant)term);
            }
            else{
                getContext().getModelledEntitySynchronizer().synchronizeProperties((IntactModelledEntity)term);
            }
        }
    }

    public void clearCache() {
        // nothing to do
    }

    @Override
    protected Object extractIdentifier(AbstractIntactEntity object) {
        return object.getAc();
    }

    @Override
    protected AbstractIntactEntity instantiateNewPersistentInstance(Entity object, Class<? extends AbstractIntactEntity> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        throw new UnsupportedOperationException("This synchronizer relies on delegate synchronizers and cannot be used this way");
    }

    @Override
    protected void storeInCache(Entity originalObject, AbstractIntactEntity persistentObject, AbstractIntactEntity existingInstance) {
        // nothing to do
    }
}
