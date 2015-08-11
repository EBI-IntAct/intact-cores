package uk.ac.ebi.intact.jami.synchronizer.listener.impl;

import psidev.psi.mi.jami.enricher.listener.EnrichmentStatus;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactMergerException;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.synchronizer.*;
import uk.ac.ebi.intact.jami.synchronizer.listener.IntactComplexEnricherListener;
import uk.ac.ebi.intact.jami.synchronizer.listener.updates.InteractorUpdates;
import uk.ac.ebi.intact.jami.utils.IntactEnricherUtils;

import java.util.Date;
import java.util.List;

/**
 * Listener that will synchronize updates done to an existing object in the database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public class DbComplexEnricherListener extends DbInteractorEnricherListener<Complex> implements IntactComplexEnricherListener {

    public DbComplexEnricherListener(SynchronizerContext context, InteractorSynchronizer dbSynchronizer) {
        super(context, dbSynchronizer);
    }

    @Override
    protected XrefSynchronizer getXrefSynchronizer() {
        return getContext().getComplexXrefSynchronizer();
    }

    @Override
    protected void processOtherUpdates(Complex object, EnrichmentStatus status, String message) throws PersisterException, FinderException, SynchronizerException {
        if (getInteractorUpdates().containsKey(object)){
            InteractorUpdates updates = getInteractorUpdates().get(object);
            if (!updates.getAddedConfidences().isEmpty()){

                List<ModelledConfidence> synchronizedConfidences = IntactEnricherUtils.synchronizeConfidencesToEnrich(updates.getAddedConfidences(),
                        getContext().getComplexConfidenceSynchronizer());
                object.getModelledConfidences().removeAll(updates.getAddedConfidences());
                for (ModelledConfidence obj : synchronizedConfidences){
                    if (!object.getModelledConfidences().contains(obj)){
                        object.getModelledConfidences().add(obj);
                    }
                }
            }
            if (!updates.getAddedParameters().isEmpty()){

                List<ModelledParameter> synchronizedParameters = IntactEnricherUtils.synchronizeParametersToEnrich(updates.getAddedParameters(),
                        getContext().getComplexParameterSynchronizer());
                object.getModelledParameters().removeAll(updates.getAddedParameters());
                for (ModelledParameter obj : synchronizedParameters){
                    if (!object.getModelledParameters().contains(obj)){
                        object.getModelledParameters().add(obj);
                    }
                }
            }
            if (!updates.getAddedParticipants().isEmpty()){

                List<ModelledParticipant> synchronizedParticipants = IntactEnricherUtils.synchronizeParticipantsToEnrich(updates.getAddedParticipants(),
                        getContext().getModelledParticipantSynchronizer());
                object.getParticipants().removeAll(updates.getAddedParticipants());
                for (ModelledParticipant f : synchronizedParticipants){
                    if (!object.getParticipants().contains(f)){
                        object.getParticipants().add(f);
                    }
                }
            }
            if (!updates.getAddedLyfeCycleEvents().isEmpty()){
                IntactComplex intactComplex = (IntactComplex)object;
                List<LifeCycleEvent> synchronizedEvents = IntactEnricherUtils.synchronizeLifeCycleEventsToEnrich(updates.getAddedLyfeCycleEvents(),
                        getContext().getComplexLifecycleSynchronizer());
                int i=0;
                for (LifeCycleEvent evt : updates.getAddedLyfeCycleEvents()){
                    int index = intactComplex.getLifecycleEvents().indexOf(evt);
                    intactComplex.getLifecycleEvents().remove(index);
                    intactComplex.getLifecycleEvents().add(index, synchronizedEvents.get(i));
                    i++;
                }
            }
        }
    }

    @Override
    protected void processOtherUpdates(Complex object, String message, Exception e) throws PersisterException, FinderException, SynchronizerException {
        if (getInteractorUpdates().containsKey(object)){
            InteractorUpdates updates = getInteractorUpdates().get(object);
            if (!updates.getAddedConfidences().isEmpty()){

                List<ModelledConfidence> synchronizedConfidences = IntactEnricherUtils.synchronizeConfidencesToEnrich(updates.getAddedConfidences(),
                        getContext().getComplexConfidenceSynchronizer());
                object.getModelledConfidences().removeAll(updates.getAddedConfidences());
                for (ModelledConfidence obj : synchronizedConfidences){
                    if (!object.getModelledConfidences().contains(obj)){
                        object.getModelledConfidences().add(obj);
                    }
                }
            }
            if (!updates.getAddedParameters().isEmpty()){

                List<ModelledParameter> synchronizedParameters = IntactEnricherUtils.synchronizeParametersToEnrich(updates.getAddedParameters(),
                        getContext().getComplexParameterSynchronizer());
                object.getModelledParameters().removeAll(updates.getAddedParameters());
                for (ModelledParameter obj : synchronizedParameters){
                    if (!object.getModelledParameters().contains(obj)){
                        object.getModelledParameters().add(obj);
                    }
                }
            }
            if (!updates.getAddedParticipants().isEmpty()){

                List<ModelledParticipant> synchronizedParticipants = IntactEnricherUtils.synchronizeParticipantsToEnrich(updates.getAddedParticipants(),
                        getContext().getModelledParticipantSynchronizer());
                object.getParticipants().removeAll(updates.getAddedParticipants());
                for (ModelledParticipant f : synchronizedParticipants){
                    if (!object.getParticipants().contains(f)){
                        object.getParticipants().add(f);
                    }
                }
            }
            if (!updates.getAddedLyfeCycleEvents().isEmpty()){
                IntactComplex intactComplex = (IntactComplex)object;
                List<LifeCycleEvent> synchronizedEvents = IntactEnricherUtils.synchronizeLifeCycleEventsToEnrich(updates.getAddedLyfeCycleEvents(),
                        getContext().getComplexLifecycleSynchronizer());
                int i=0;
                for (LifeCycleEvent evt : updates.getAddedLyfeCycleEvents()){
                    int index = intactComplex.getLifecycleEvents().indexOf(evt);
                    intactComplex.getLifecycleEvents().remove(index);
                    intactComplex.getLifecycleEvents().add(index, synchronizedEvents.get(i));
                    i++;
                }
            }
        }
    }

    @Override
    public void onAddedCooperativeEffect(Complex complex, CooperativeEffect cooperativeEffect) {
        // nothing to do
    }

    @Override
    public void onRemovedCooperativeEffect(Complex complex, CooperativeEffect cooperativeEffect) {
        // nothing to do
    }

    @Override
    public void onAddedInteractionEvidence(Complex complex, InteractionEvidence evidence) {
        // nothing to do
    }

    @Override
    public void onRemovedInteractionEvidence(Complex complex, InteractionEvidence evidence) {
        // nothing to do
    }

    @Override
    public void onSourceUpdate(Complex complex, Source source) {
        try {
            if (complex.getSource() != null){
                complex.setSource(
                        getContext().getSourceSynchronizer().synchronize(complex.getSource(), true));
            }
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize source", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize source", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize source", e);
        }
    }

    @Override
    public void onEvidenceTypeUpdate(Complex complex, CvTerm cvTerm) {
        try {
            if (complex.getEvidenceType() != null){
                complex.setEvidenceType(
                        getContext().getDatabaseSynchronizer().synchronize(complex.getEvidenceType(), true));
            }
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize source", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize source", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize source", e);
        }
    }

    @Override
    public void onAddedConfidence(Complex complex, Confidence confidence) {
        if (getInteractorUpdates().containsKey(complex)){
            getInteractorUpdates().get(complex).getAddedConfidences().add((ModelledConfidence)confidence);
        }
        else{
            InteractorUpdates updates = new InteractorUpdates();
            updates.getAddedConfidences().add((ModelledConfidence)confidence);
            getInteractorUpdates().put(complex, updates);
        }
    }

    @Override
    public void onRemovedConfidence(Complex complex, Confidence confidence) {
        // nothing to do
    }

    @Override
    public void onUpdatedDateUpdate(Complex complex, Date date) {
        // nothing to do
    }

    @Override
    public void onCreatedDateUpdate(Complex complex, Date date) {
        // nothing to do
    }

    @Override
    public void onInteractionTypeUpdate(Complex complex, CvTerm cvTerm) {
        try {
            if (complex.getInteractionType() != null){
                complex.setInteractionType(
                        getContext().getInteractionTypeSynchronizer().synchronize(complex.getInteractionType(), true));
            }
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize type", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize type", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize type", e);
        }
    }

    @Override
    public void onAddedParticipant(Complex complex, Participant participant) {
        if (getInteractorUpdates().containsKey(complex)){
            getInteractorUpdates().get(complex).getAddedParticipants().add((ModelledParticipant)participant);
        }
        else{
            InteractorUpdates updates = new InteractorUpdates();
            updates.getAddedParticipants().add((ModelledParticipant)participant);
            getInteractorUpdates().put(complex, updates);
        }
    }

    @Override
    public void onRemovedParticipant(Complex complex, Participant participant) {
        // nothing to do
    }

    @Override
    public void onAddedParameter(Complex complex, Parameter parameter) {
        if (getInteractorUpdates().containsKey(complex)){
            getInteractorUpdates().get(complex).getAddedParameters().add((ModelledParameter) parameter);
        }
        else{
            InteractorUpdates updates = new InteractorUpdates();
            updates.getAddedParameters().add((ModelledParameter)parameter);
            getInteractorUpdates().put(complex, updates);
        }
    }

    @Override
    public void onRemovedParameter(Complex complex, Parameter parameter) {
        // nothing to do
    }

    @Override
    public void onAddedLifeCycleEvent(IntactComplex complex, LifeCycleEvent added) {
        if (getInteractorUpdates().containsKey(complex)){
            getInteractorUpdates().get(complex).getAddedLyfeCycleEvents().add(added);
        }
        else{
            InteractorUpdates updates = new InteractorUpdates();
            updates.getAddedLyfeCycleEvents().add(added);
            getInteractorUpdates().put(complex, updates);
        }
    }

    @Override
    public void onRemovedLifeCycleEvent(IntactComplex complex, LifeCycleEvent removed) {
        // nothing to do
    }

    @Override
    public void onStatusUpdate(IntactComplex complex, CvTerm oldStatus) {
        try {
            if (complex.getCvStatus() != null){
                complex.setCvStatus(
                        getContext().getLifecycleStatusSynchronizer().synchronize(complex.getCvStatus(), true));
            }
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize status", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize status", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize status", e);
        }
    }

    @Override
    public void onCurrentOwnerUpdate(IntactComplex complex, User oldUser) {
        try {
            if (complex.getCurrentOwner() != null){
                complex.setCurrentOwner(
                        getContext().getUserReadOnlySynchronizer().synchronize(complex.getCurrentOwner(), true));
            }
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize current owner", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize current owner", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize current owner", e);
        }
    }

    @Override
    public void onCurrentReviewerUpdate(IntactComplex complex, User oldUser) {
        try {
            if (complex.getCurrentReviewer() != null){
                complex.setCurrentReviewer(
                        getContext().getUserReadOnlySynchronizer().synchronize(complex.getCurrentReviewer(), true));
            }
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize current reviewer", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize current reviewer", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize current reviewer", e);
        }
    }
}
