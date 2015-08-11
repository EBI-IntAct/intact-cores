package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMerger;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.ParticipantSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.synchronizer.listener.DbSynchronizerListener;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Synchronizer for all participants
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>12/02/14</pre>
 */

public class CompositeParticipantSynchronizer implements ParticipantSynchronizer<Participant, AbstractIntactParticipant> {

    private SynchronizerContext context;

    public CompositeParticipantSynchronizer(SynchronizerContext context){
        if (context == null){
            throw new IllegalArgumentException("An IntAct database synchronizer needs a non null synchronizer context");
        }
        this.context = context;
    }

    public AbstractIntactParticipant find(Participant term) throws FinderException {
        // experimental
        if (term instanceof ParticipantEvidence){
            return this.context.getParticipantEvidenceSynchronizer().find((ParticipantEvidence) term);
        }
        // modelled
        else {
            return this.context.getModelledParticipantSynchronizer().find((ModelledParticipant) term);
        }
    }

    @Override
    public Collection<AbstractIntactParticipant> findAll(Participant term) {
        // experimental
        if (term instanceof ParticipantEvidence){
            return new ArrayList<AbstractIntactParticipant>(this.context.getParticipantEvidenceSynchronizer().findAll((ParticipantEvidence)term));
        }
        // modelled
        else {
            return new ArrayList<AbstractIntactParticipant>(this.context.getModelledParticipantSynchronizer().findAll((ModelledParticipant) term));
        }
    }

    @Override
    public Collection<String> findAllMatchingAcs(Participant term) {
        // experimental
        if (term instanceof ParticipantEvidence){
            return this.context.getParticipantEvidenceSynchronizer().findAllMatchingAcs((ParticipantEvidence) term);
        }
        // modelled
        else {
            return this.context.getModelledParticipantSynchronizer().findAllMatchingAcs((ModelledParticipant) term);
        }
    }

    public AbstractIntactParticipant persist(AbstractIntactParticipant term) throws FinderException, PersisterException, SynchronizerException {
        // experimental
        if (term instanceof IntactParticipantEvidence){
            return this.context.getParticipantEvidenceSynchronizer().persist((IntactParticipantEvidence)term);
        }
        // modelled
        else {
            return this.context.getModelledParticipantSynchronizer().persist((IntactModelledParticipant)term);
        }
    }

    public AbstractIntactParticipant synchronize(Participant term, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        // experimental
        if (term instanceof ParticipantEvidence){
            return this.context.getParticipantEvidenceSynchronizer().synchronize((ParticipantEvidence)term, persist);
        }
        // modelled
        else {
            return this.context.getModelledParticipantSynchronizer().synchronize((ModelledParticipant)term, persist);
        }
    }

    public void synchronizeProperties(AbstractIntactParticipant term) throws FinderException, PersisterException, SynchronizerException {
        // experimental
        if (term instanceof IntactParticipantEvidence){
            this.context.getParticipantEvidenceSynchronizer().synchronizeProperties((IntactParticipantEvidence)term);
        }
        // modelled
        else {
            this.context.getModelledParticipantSynchronizer().synchronizeProperties((IntactModelledParticipant)term);
        }
    }

    public void clearCache() {
        // nothing to do
    }

    public IntactDbMerger<Participant, AbstractIntactParticipant> getIntactMerger() {
        return null;
    }

    public void setIntactMerger(IntactDbMerger<Participant, AbstractIntactParticipant> intactMerger) {
        throw new UnsupportedOperationException("The entity synchronizer does not support this method as it is a composite synchronizer");
    }

    public Class<? extends AbstractIntactParticipant> getIntactClass() {
        return AbstractIntactParticipant.class;
    }

    public void setIntactClass(Class<? extends AbstractIntactParticipant> intactClass) {
        throw new UnsupportedOperationException("The entity synchronizer does not support this method as it is a composite synchronizer");
    }

    public boolean delete(Participant term) {
        // experimental
        if (term instanceof ParticipantEvidence){
            return this.context.getParticipantEvidenceSynchronizer().delete((ParticipantEvidence)term);
        }
        // modelled
        else {
            return this.context.getModelledParticipantSynchronizer().delete((ModelledParticipant)term);
        }
    }

    @Override
    public AbstractIntactParticipant convertToPersistentObject(Participant term) throws SynchronizerException, PersisterException, FinderException {
        // experimental
        if (term instanceof ParticipantEvidence){
            return this.context.getParticipantEvidenceSynchronizer().convertToPersistentObject((ParticipantEvidence)term);
        }
        // modelled
        else {
            return this.context.getModelledParticipantSynchronizer().convertToPersistentObject((ModelledParticipant)term);
        }
    }

    @Override
    public void flush() {
        this.context.getModelledParticipantSynchronizer().flush();
        this.context.getParticipantEvidenceSynchronizer().flush();
    }

    @Override
    public DbSynchronizerListener getListener() {
        return this.context.getSynchronizerListener();
    }

    @Override
    public void setListener(DbSynchronizerListener listener) {
        this.context.getModelledParticipantSynchronizer().setListener(listener);
        this.context.getParticipantEvidenceSynchronizer().setListener(listener);
    }
}
