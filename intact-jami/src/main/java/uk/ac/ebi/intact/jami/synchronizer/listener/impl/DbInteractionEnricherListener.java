package uk.ac.ebi.intact.jami.synchronizer.listener.impl;

import org.apache.commons.collections.map.IdentityMap;
import psidev.psi.mi.jami.enricher.listener.EnrichmentStatus;
import psidev.psi.mi.jami.enricher.listener.InteractionEvidenceEnricherListener;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactMergerException;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractionEvidence;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.IntactInteractionSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.synchronizer.listener.InteractionEvidenceUpdates;
import uk.ac.ebi.intact.jami.utils.IntactEnricherUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Listener that will synchronize updates done to an existing object in the database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public class DbInteractionEnricherListener implements InteractionEvidenceEnricherListener{
    private Map<InteractionEvidence, InteractionEvidenceUpdates> interactionUpdates;
    private SynchronizerContext context;
    private IntactInteractionSynchronizer dbSynchronizer;

    public DbInteractionEnricherListener(SynchronizerContext context, IntactInteractionSynchronizer dbSynchronizer) {
        if (context == null){
            throw new IllegalArgumentException("The listener needs a non null synchronizer context");
        }
        this.context = context;
        if (dbSynchronizer == null){
            throw new IllegalArgumentException("The listener needs a non null interaction synchronizer");
        }
        this.dbSynchronizer = dbSynchronizer;
        this.interactionUpdates = new IdentityMap();
    }

    @Override
    public void onAddedAnnotation(InteractionEvidence interactor, Annotation annotation) {
        if (this.interactionUpdates.containsKey(interactor)){
            this.interactionUpdates.get(interactor).getAddedAnnotations().add(annotation);
        }
        else{
            InteractionEvidenceUpdates updates = new InteractionEvidenceUpdates();
            updates.getAddedAnnotations().add(annotation);
            this.interactionUpdates.put(interactor, updates);
        }
    }

    @Override
    public void onRemovedAnnotation(InteractionEvidence interactor, Annotation annotation) {
        // nothing to do
    }

    @Override
    public void onEnrichmentComplete(InteractionEvidence object, EnrichmentStatus status, String message) {
        if (interactionUpdates.containsKey(object)){
            InteractionEvidenceUpdates updates = interactionUpdates.get(object);
            try {
                if (!updates.getAddedXrefs().isEmpty()){

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedXrefs(),
                            getContext().getInteractionXrefSynchronizer());
                    object.getXrefs().removeAll(updates.getAddedXrefs());
                    object.getXrefs().addAll(synchronizedXrefs);
                }
                if (!updates.getAddedIdentifiers().isEmpty()){

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedIdentifiers(),
                            getContext().getInteractionXrefSynchronizer());
                    object.getIdentifiers().removeAll(updates.getAddedIdentifiers());
                    object.getIdentifiers().addAll(synchronizedXrefs);
                }
                if (!updates.getAddedAnnotations().isEmpty()){

                    List<Annotation> synchronizedAnnotations = IntactEnricherUtils.synchronizeAnnotationsToEnrich(updates.getAddedAnnotations(),
                            context.getInteractionAnnotationSynchronizer());
                    object.getAnnotations().removeAll(updates.getAddedAnnotations());
                    object.getAnnotations().addAll(synchronizedAnnotations);
                }
                if (!updates.getAddedNegative().isEmpty()){

                    List<Annotation> synchronizedAnnotations = IntactEnricherUtils.synchronizeAnnotationsToEnrich(updates.getAddedNegative(),
                            context.getInteractionAnnotationSynchronizer());
                    ((IntactInteractionEvidence)object).getDbAnnotations().removeAll(updates.getAddedNegative());
                    ((IntactInteractionEvidence)object).getDbAnnotations().addAll(synchronizedAnnotations);
                }
                if (!updates.getAddedConfidences().isEmpty()){

                    List<Confidence> synchronizedConfidences = IntactEnricherUtils.synchronizeConfidencesToEnrich(updates.getAddedConfidences(),
                            context.getInteractionConfidenceSynchronizer());
                    object.getConfidences().removeAll(updates.getAddedConfidences());
                    object.getConfidences().addAll(synchronizedConfidences);
                }
                if (!updates.getAddedParameters().isEmpty()){

                    List<Parameter> synchronizedParameters = IntactEnricherUtils.synchronizeParametersToEnrich(updates.getAddedParameters(),
                            context.getInteractionParameterSynchronizer());
                    object.getParameters().removeAll(updates.getAddedParameters());
                    object.getParameters().addAll(synchronizedParameters);
                }
                if (!updates.getAddedParticipants().isEmpty()){

                    List<ParticipantEvidence> synchronizedParticipants = IntactEnricherUtils.synchronizeParticipantsToEnrich(updates.getAddedParticipants(),
                            context.getParticipantEvidenceSynchronizer());
                    object.getParticipants().removeAll(updates.getAddedParticipants());
                    for (ParticipantEvidence f : synchronizedParticipants){
                        if (!object.getParticipants().contains(f)){
                            object.getParticipants().add(f);
                        }
                    }
                }
                if (!updates.getAddedVariableParameterSets().isEmpty()){

                    List<VariableParameterValueSet> synchronizedValues = IntactEnricherUtils.synchronizeVariableParameterValuesToEnrich(updates.getAddedVariableParameterSets(),
                            context.getVariableParameterValueSetSynchronizer());
                    object.getVariableParameterValues().removeAll(updates.getAddedVariableParameterSets());
                    object.getVariableParameterValues().addAll(synchronizedValues);
                }

                interactionUpdates.remove(object);
            } catch (PersisterException e) {
                interactionUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged interaction", e);
            } catch (FinderException e) {
                interactionUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged interaction", e);
            } catch (SynchronizerException e) {
                interactionUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged interaction", e);
            }
        }
    }

    @Override
    public void onEnrichmentError(InteractionEvidence object, String message, Exception e) {
        if (interactionUpdates.containsKey(object)){
            InteractionEvidenceUpdates updates = interactionUpdates.get(object);
            try {
                if (!updates.getAddedXrefs().isEmpty()){

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedXrefs(),
                            getContext().getInteractionXrefSynchronizer());
                    object.getXrefs().removeAll(updates.getAddedXrefs());
                    object.getXrefs().addAll(synchronizedXrefs);
                }
                if (!updates.getAddedIdentifiers().isEmpty()){

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedIdentifiers(),
                            getContext().getInteractionXrefSynchronizer());
                    object.getIdentifiers().removeAll(updates.getAddedIdentifiers());
                    object.getIdentifiers().addAll(synchronizedXrefs);
                }
                if (!updates.getAddedAnnotations().isEmpty()){

                    List<Annotation> synchronizedAnnotations = IntactEnricherUtils.synchronizeAnnotationsToEnrich(updates.getAddedAnnotations(),
                            context.getInteractionAnnotationSynchronizer());
                    object.getAnnotations().removeAll(updates.getAddedAnnotations());
                    object.getAnnotations().addAll(synchronizedAnnotations);
                }
                if (!updates.getAddedNegative().isEmpty()){

                    List<Annotation> synchronizedAnnotations = IntactEnricherUtils.synchronizeAnnotationsToEnrich(updates.getAddedNegative(),
                            context.getInteractionAnnotationSynchronizer());
                    ((IntactInteractionEvidence)object).getDbAnnotations().removeAll(updates.getAddedNegative());
                    ((IntactInteractionEvidence)object).getDbAnnotations().addAll(synchronizedAnnotations);
                }
                if (!updates.getAddedConfidences().isEmpty()){

                    List<Confidence> synchronizedConfidences = IntactEnricherUtils.synchronizeConfidencesToEnrich(updates.getAddedConfidences(),
                            context.getInteractionConfidenceSynchronizer());
                    object.getConfidences().removeAll(updates.getAddedConfidences());
                    object.getConfidences().addAll(synchronizedConfidences);
                }
                if (!updates.getAddedParameters().isEmpty()){

                    List<Parameter> synchronizedParameters = IntactEnricherUtils.synchronizeParametersToEnrich(updates.getAddedParameters(),
                            context.getInteractionParameterSynchronizer());
                    object.getParameters().removeAll(updates.getAddedParameters());
                    object.getParameters().addAll(synchronizedParameters);
                }
                if (!updates.getAddedParticipants().isEmpty()){

                    List<ParticipantEvidence> synchronizedParticipants = IntactEnricherUtils.synchronizeParticipantsToEnrich(updates.getAddedParticipants(),
                            context.getParticipantEvidenceSynchronizer());
                    object.getParticipants().removeAll(updates.getAddedParticipants());
                    for (ParticipantEvidence f : synchronizedParticipants){
                        if (!object.getParticipants().contains(f)){
                            object.getParticipants().add(f);
                        }
                    }
                }
                if (!updates.getAddedVariableParameterSets().isEmpty()){

                    List<VariableParameterValueSet> synchronizedValues = IntactEnricherUtils.synchronizeVariableParameterValuesToEnrich(updates.getAddedVariableParameterSets(),
                            context.getVariableParameterValueSetSynchronizer());
                    object.getVariableParameterValues().removeAll(updates.getAddedVariableParameterSets());
                    object.getVariableParameterValues().addAll(synchronizedValues);
                }

                interactionUpdates.remove(object);
            } catch (PersisterException e2) {
                interactionUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged interaction", e2);
            } catch (FinderException e2) {
                interactionUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged interaction", e2);
            } catch (SynchronizerException e2) {
                interactionUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged interaction", e2);
            }
        }
    }
    @Override
    public void onAddedXref(InteractionEvidence interactor, Xref xref) {
        if (this.interactionUpdates.containsKey(interactor)){
            this.interactionUpdates.get(interactor).getAddedXrefs().add(xref);
        }
        else{
            InteractionEvidenceUpdates updates = new InteractionEvidenceUpdates();
            updates.getAddedXrefs().add(xref);
            this.interactionUpdates.put(interactor, updates);
        }
    }

    @Override
    public void onRemovedXref(InteractionEvidence interactor, Xref xref) {
        // nothing to do
    }

    public Map<InteractionEvidence, InteractionEvidenceUpdates> getInteractionUpdates() {
        return interactionUpdates;
    }

    @Override
    public void onShortNameUpdate(InteractionEvidence t, String s) {
        try {
            this.dbSynchronizer.prepareAndSynchronizeShortLabel((IntactInteractionEvidence)t);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize interaction label", e);
        }
    }

    @Override
    public void onUpdatedDateUpdate(InteractionEvidence evidence, Date date) {
        // nothing to do
    }

    @Override
    public void onCreatedDateUpdate(InteractionEvidence evidence, Date date) {
        // nothing to do
    }

    @Override
    public void onInteractionTypeUpdate(InteractionEvidence evidence, CvTerm cvTerm) {
        try {
            if (evidence.getInteractionType() != null){
                evidence.setInteractionType(
                        getContext().getInteractionTypeSynchronizer().synchronize(evidence.getInteractionType(), true));
            }
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize interaction type", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize interaction type", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize interaction type", e);
        }
    }

    @Override
    public void onAddedParticipant(InteractionEvidence evidence, Participant participant) {
        if (this.interactionUpdates.containsKey(evidence)){
            this.interactionUpdates.get(evidence).getAddedParticipants().add((ParticipantEvidence)participant);
        }
        else{
            InteractionEvidenceUpdates updates = new InteractionEvidenceUpdates();
            updates.getAddedParticipants().add((ParticipantEvidence)participant);
            this.interactionUpdates.put(evidence, updates);
        }
    }

    @Override
    public void onRemovedParticipant(InteractionEvidence evidence, Participant participant) {
        // nothing to do
    }

    @Override
    public void onAddedChecksum(InteractionEvidence t, Checksum checksum) {
        // nothing to do
    }

    @Override
    public void onRemovedChecksum(InteractionEvidence t, Checksum checksum) {
        // nothing to do
    }

    @Override
    public void onAddedIdentifier(InteractionEvidence t, Xref xref) {
        if (this.interactionUpdates.containsKey(t)){
            this.interactionUpdates.get(t).getAddedIdentifiers().add(xref);
        }
        else{
            InteractionEvidenceUpdates updates = new InteractionEvidenceUpdates();
            updates.getAddedIdentifiers().add(xref);
            this.interactionUpdates.put(t, updates);
        }
    }

    @Override
    public void onRemovedIdentifier(InteractionEvidence t, Xref xref) {
        // nothing to do
    }

    protected SynchronizerContext getContext() {
        return context;
    }

    protected IntactInteractionSynchronizer getDbSynchronizer() {
        return dbSynchronizer;
    }

    @Override
    public void onExperimentUpdate(InteractionEvidence evidence, Experiment experiment) {
        // nothing to do
    }

    @Override
    public void onAddedVariableParameterValues(InteractionEvidence t, VariableParameterValueSet variableParameterValues) {
        if (this.interactionUpdates.containsKey(t)){
            this.interactionUpdates.get(t).getAddedVariableParameterSets().add(variableParameterValues);
        }
        else{
            InteractionEvidenceUpdates updates = new InteractionEvidenceUpdates();
            updates.getAddedVariableParameterSets().add(variableParameterValues);
            this.interactionUpdates.put(t, updates);
        }
    }

    @Override
    public void onRemovedVariableParameterValues(InteractionEvidence evidence, VariableParameterValueSet variableParameterValues) {
        // nothing to do
    }

    @Override
    public void onInferredPropertyUpdate(InteractionEvidence evidence, boolean b) {
        // nothing to do
    }

    @Override
    public void onNegativePropertyUpdate(InteractionEvidence evidence, boolean b) {
        if (evidence instanceof IntactInteractionEvidence && evidence.isNegative()){
            IntactInteractionEvidence ev = (IntactInteractionEvidence)evidence;
            Collection<Annotation> negativeAnnots = AnnotationUtils.collectAllAnnotationsHavingTopic(ev.getDbAnnotations(), null, "negative");
            if (this.interactionUpdates.containsKey(ev)){
                this.interactionUpdates.get(ev).getAddedNegative().addAll(negativeAnnots);
            }
            else{
                InteractionEvidenceUpdates updates = new InteractionEvidenceUpdates();
                updates.getAddedNegative().addAll(negativeAnnots);
                this.interactionUpdates.put(ev, updates);
            }
        }
    }

    @Override
    public void onAddedConfidence(InteractionEvidence t, Confidence confidence) {
        if (this.interactionUpdates.containsKey(t)){
            this.interactionUpdates.get(t).getAddedConfidences().add(confidence);
        }
        else{
            InteractionEvidenceUpdates updates = new InteractionEvidenceUpdates();
            updates.getAddedConfidences().add(confidence);
            this.interactionUpdates.put(t, updates);
        }
    }

    @Override
    public void onRemovedConfidence(InteractionEvidence evidence, Confidence confidence) {
        // nothing to do
    }

    @Override
    public void onAddedParameter(InteractionEvidence t, Parameter parameter) {
        if (this.interactionUpdates.containsKey(t)){
            this.interactionUpdates.get(t).getAddedParameters().add(parameter);
        }
        else{
            InteractionEvidenceUpdates updates = new InteractionEvidenceUpdates();
            updates.getAddedParameters().add(parameter);
            this.interactionUpdates.put(t, updates);
        }
    }

    @Override
    public void onRemovedParameter(InteractionEvidence evidence, Parameter parameter) {
        // nothing to do
    }
}
