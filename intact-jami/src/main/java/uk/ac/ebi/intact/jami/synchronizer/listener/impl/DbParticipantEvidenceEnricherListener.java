package uk.ac.ebi.intact.jami.synchronizer.listener.impl;

import psidev.psi.mi.jami.enricher.listener.EnrichmentStatus;
import psidev.psi.mi.jami.enricher.listener.ParticipantEvidenceEnricherListener;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactMergerException;
import uk.ac.ebi.intact.jami.synchronizer.*;
import uk.ac.ebi.intact.jami.synchronizer.listener.updates.ParticipantUpdates;
import uk.ac.ebi.intact.jami.utils.IntactEnricherUtils;

import java.util.List;

/**
 * Listener that will synchronize updates done to an existing object in the database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public class DbParticipantEvidenceEnricherListener extends AbstractDbParticipantEnricherListener<ParticipantEvidence, FeatureEvidence>
implements ParticipantEvidenceEnricherListener<ParticipantEvidence>{

    public DbParticipantEvidenceEnricherListener(SynchronizerContext context, ParticipantSynchronizer dbSynchronizer) {
        super(context, dbSynchronizer);
    }

    @Override
    protected void processOtherUpdates(ParticipantEvidence object, EnrichmentStatus status, String message) throws PersisterException, FinderException, SynchronizerException {
        if (getParticipantUpdates().containsKey(object)) {
            ParticipantUpdates<FeatureEvidence> updates = getParticipantUpdates().get(object);
            if (!updates.getAddedExperimentalPreparations().isEmpty()) {

                List<CvTerm> synchronizedMethods = IntactEnricherUtils.synchronizeCvsToEnrich(updates.getAddedExperimentalPreparations(),
                        getContext().getExperimentalPreparationSynchronizer());
                object.getExperimentalPreparations().removeAll(updates.getAddedExperimentalPreparations());
                object.getExperimentalPreparations().addAll(synchronizedMethods);
            }
            if (!updates.getAddedIdentificationMethods().isEmpty()) {

                List<CvTerm> synchronizedMethods = IntactEnricherUtils.synchronizeCvsToEnrich(updates.getAddedIdentificationMethods(),
                        getContext().getParticipantDetectionMethodSynchronizer());
                object.getIdentificationMethods().removeAll(updates.getAddedIdentificationMethods());
                object.getIdentificationMethods().addAll(synchronizedMethods);
            }
            if (!updates.getAddedParameters().isEmpty()) {

                List<Parameter> synchronizedParameters = IntactEnricherUtils.synchronizeParametersToEnrich(updates.getAddedParameters(),
                        getContext().getParticipantEvidenceParameterSynchronizer());
                object.getParameters().removeAll(updates.getAddedParameters());
                object.getParameters().addAll(synchronizedParameters);
            }
            if (!updates.getAddedConfidences().isEmpty()) {

                List<Confidence> synchronizedParameters = IntactEnricherUtils.synchronizeConfidencesToEnrich(updates.getAddedConfidences(),
                        getContext().getParticipantEvidenceConfidenceSynchronizer());
                object.getConfidences().removeAll(updates.getAddedConfidences());
                object.getConfidences().addAll(synchronizedParameters);
            }
        }
    }

    @Override
    protected void processOtherUpdates(ParticipantEvidence object, String message, Exception e) throws PersisterException, FinderException, SynchronizerException {
        if (getParticipantUpdates().containsKey(object)) {
            ParticipantUpdates<FeatureEvidence> updates = getParticipantUpdates().get(object);
            if (!updates.getAddedExperimentalPreparations().isEmpty()) {

                List<CvTerm> synchronizedMethods = IntactEnricherUtils.synchronizeCvsToEnrich(updates.getAddedExperimentalPreparations(),
                        getContext().getExperimentalPreparationSynchronizer());
                object.getExperimentalPreparations().removeAll(updates.getAddedExperimentalPreparations());
                object.getExperimentalPreparations().addAll(synchronizedMethods);
            }
            if (!updates.getAddedIdentificationMethods().isEmpty()) {

                List<CvTerm> synchronizedMethods = IntactEnricherUtils.synchronizeCvsToEnrich(updates.getAddedIdentificationMethods(),
                        getContext().getParticipantDetectionMethodSynchronizer());
                object.getIdentificationMethods().removeAll(updates.getAddedIdentificationMethods());
                object.getIdentificationMethods().addAll(synchronizedMethods);
            }
            if (!updates.getAddedParameters().isEmpty()) {

                List<Parameter> synchronizedParameters = IntactEnricherUtils.synchronizeParametersToEnrich(updates.getAddedParameters(),
                        getContext().getParticipantEvidenceParameterSynchronizer());
                object.getParameters().removeAll(updates.getAddedParameters());
                object.getParameters().addAll(synchronizedParameters);
            }
            if (!updates.getAddedConfidences().isEmpty()) {

                List<Confidence> synchronizedParameters = IntactEnricherUtils.synchronizeConfidencesToEnrich(updates.getAddedConfidences(),
                        getContext().getParticipantEvidenceConfidenceSynchronizer());
                object.getConfidences().removeAll(updates.getAddedConfidences());
                object.getConfidences().addAll(synchronizedParameters);
            }
        }
    }

    @Override
    protected XrefSynchronizer getXrefSynchronizer() {
        return getContext().getParticipantEvidenceXrefSynchronizer();
    }

    @Override
    protected AnnotationSynchronizer getAnnotationSynchronizer() {
        return getContext().getParticipantEvidenceAnnotationSynchronizer();
    }

    @Override
    protected AliasSynchronizer getAliasSynchronizer() {
        return getContext().getParticipantEvidenceAliasSynchronizer();
    }

    @Override
    protected IntactDbSynchronizer getCausalRelationshipSynchronizer() {
        return getContext().getExperimentalCausalRelationshipSynchronizer();
    }

    @Override
    protected IntactDbSynchronizer getFeatureSynchronizer() {
        return getContext().getFeatureEvidenceSynchronizer();
    }

    @Override
    public void onExperimentalRoleUpdate(ParticipantEvidence t, CvTerm cvTerm) {
        try {
            t.setExperimentalRole(
                    getContext().getExperimentalRoleSynchronizer().synchronize(t.getExperimentalRole(), true));
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize experimental role", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize experimental role", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize experimental role", e);
        }
    }

    @Override
    public void onExpressedInUpdate(ParticipantEvidence t, Organism organism) {
        try {
            t.setExpressedInOrganism(
                    getContext().getOrganismSynchronizer().synchronize(t.getExpressedInOrganism(), true));
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize expressed in organism", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize expressed in organism", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize expressed in organism", e);
        }
    }

    @Override
    public void onAddedIdentificationMethod(ParticipantEvidence t, CvTerm cvTerm) {
        if (getParticipantUpdates().containsKey(t)){
            getParticipantUpdates().get(t).getAddedIdentificationMethods().add(cvTerm);
        }
        else{
            ParticipantUpdates<FeatureEvidence> updates = new ParticipantUpdates<FeatureEvidence>();
            updates.getAddedIdentificationMethods().add(cvTerm);
            getParticipantUpdates().put(t, updates);
        }
    }

    @Override
    public void onRemovedIdentificationMethod(ParticipantEvidence participantEvidence, CvTerm cvTerm) {
       // noting to do
    }

    @Override
    public void onAddedExperimentalPreparation(ParticipantEvidence t, CvTerm cvTerm) {
        if (getParticipantUpdates().containsKey(t)){
            getParticipantUpdates().get(t).getAddedExperimentalPreparations().add(cvTerm);
        }
        else{
            ParticipantUpdates<FeatureEvidence> updates = new ParticipantUpdates<FeatureEvidence>();
            updates.getAddedExperimentalPreparations().add(cvTerm);
            getParticipantUpdates().put(t, updates);
        }
    }

    @Override
    public void onRemovedExperimentalPreparation(ParticipantEvidence participantEvidence, CvTerm cvTerm) {
        // noting to do
    }

    @Override
    public void onAddedConfidence(ParticipantEvidence t, Confidence confidence) {
        if (getParticipantUpdates().containsKey(t)){
            getParticipantUpdates().get(t).getAddedConfidences().add(confidence);
        }
        else{
            ParticipantUpdates<FeatureEvidence> updates = new ParticipantUpdates<FeatureEvidence>();
            updates.getAddedConfidences().add(confidence);
            getParticipantUpdates().put(t, updates);
        }
    }

    @Override
    public void onRemovedConfidence(ParticipantEvidence participantEvidence, Confidence confidence) {
        // noting to do
    }

    @Override
    public void onAddedParameter(ParticipantEvidence t, Parameter parameter) {
        if (getParticipantUpdates().containsKey(t)){
            getParticipantUpdates().get(t).getAddedParameters().add(parameter);
        }
        else{
            ParticipantUpdates<FeatureEvidence> updates = new ParticipantUpdates<FeatureEvidence>();
            updates.getAddedParameters().add(parameter);
            getParticipantUpdates().put(t, updates);
        }
    }

    @Override
    public void onRemovedParameter(ParticipantEvidence participantEvidence, Parameter parameter) {
        // noting to do
    }
}
