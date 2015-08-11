package uk.ac.ebi.intact.jami.synchronizer.listener.impl;

import org.apache.commons.collections.map.IdentityMap;
import psidev.psi.mi.jami.enricher.listener.EnrichmentStatus;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactMergerException;
import uk.ac.ebi.intact.jami.model.extension.IntactExperiment;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.IntactExperimentSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.synchronizer.listener.ExperimentUpdates;
import uk.ac.ebi.intact.jami.synchronizer.listener.IntactExperimentEnricherListener;
import uk.ac.ebi.intact.jami.utils.IntactEnricherUtils;

import java.util.List;
import java.util.Map;

/**
 * Listener that will synchronize updates done to an existing object in the database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public class DbExperimentEnricherListener implements IntactExperimentEnricherListener{
    private Map<Experiment, ExperimentUpdates> experimentUpdates;
    private SynchronizerContext context;
    private IntactExperimentSynchronizer dbSynchronizer;

    public DbExperimentEnricherListener(SynchronizerContext context, IntactExperimentSynchronizer dbSynchronizer) {
        if (context == null){
            throw new IllegalArgumentException("The listener needs a non null synchronizer context");
        }
        this.context = context;
        if (dbSynchronizer == null){
            throw new IllegalArgumentException("The listener needs a non null experiment synchronizer");
        }
        this.dbSynchronizer = dbSynchronizer;
        experimentUpdates = new IdentityMap();
    }

    @Override
    public void onShortLabelUpdate(IntactExperiment experiment, String oldLabel) {
        try {
            this.dbSynchronizer.prepareAndSynchronizeShortLabel(experiment);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize experiment label", e);
        }
    }

    @Override
    public void onParticipantDetectionMethodUpdate(IntactExperiment experiment, CvTerm oldTerm) {
        try {
            if (experiment.getParticipantIdentificationMethod() != null){
                experiment.setParticipantIdentificationMethod(
                        context.getParticipantDetectionMethodSynchronizer().synchronize(experiment.getParticipantIdentificationMethod(), true));
            }
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize participant detection method", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize participant detection method", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize participant detection method", e);
        }
    }

    @Override
    public void onAddedInteractionEvidence(Experiment experiment, InteractionEvidence added) {
        if (this.experimentUpdates.containsKey(experiment)){
            this.experimentUpdates.get(experiment).getAddedInteractions().add(added);
        }
        else{
            ExperimentUpdates updates = new ExperimentUpdates();
            updates.getAddedInteractions().add(added);
            this.experimentUpdates.put(experiment, updates);
        }
    }

    @Override
    public void onRemovedInteractionEvidence(Experiment experiment, InteractionEvidence removed) {
        // nothing to do
    }

    @Override
    public void onPublicationUpdate(Experiment experiment, Publication publication) {
        // nothing to do
    }

    @Override
    public void onInteractionDetectionMethodUpdate(Experiment experiment, CvTerm cvTerm) {
        try {
            experiment.setInteractionDetectionMethod(
                    context.getInteractionDetectionMethodSynchronizer().synchronize(experiment.getInteractionDetectionMethod(), true));
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize interaction detection method", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize interaction detection method", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize interaction detection method", e);
        }
    }

    @Override
    public void onHostOrganismUpdate(Experiment experiment, Organism organism) {
        try {
            if (experiment.getHostOrganism() != null){
                experiment.setHostOrganism(
                        context.getOrganismSynchronizer().synchronize(experiment.getHostOrganism(), true));
            }
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize Host organism", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize Host organism", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize Host organism", e);
        }
    }

    @Override
    public void onAddedVariableParameter(Experiment experiment, VariableParameter parameter) {
        if (this.experimentUpdates.containsKey(experiment)){
            this.experimentUpdates.get(experiment).getAddedVariableParameters().add(parameter);
        }
        else{
            ExperimentUpdates updates = new ExperimentUpdates();
            updates.getAddedVariableParameters().add(parameter);
            this.experimentUpdates.put(experiment, updates);
        }
    }

    @Override
    public void onRemovedVariableParameter(Experiment experiment, VariableParameter parameter) {
        // nothing to do
    }

    @Override
    public void onAddedAnnotation(Experiment experiment, Annotation annotation) {
        if (this.experimentUpdates.containsKey(experiment)){
            this.experimentUpdates.get(experiment).getAddedAnnotations().add(annotation);
        }
        else{
            ExperimentUpdates updates = new ExperimentUpdates();
            updates.getAddedAnnotations().add(annotation);
            this.experimentUpdates.put(experiment, updates);
        }
    }

    @Override
    public void onRemovedAnnotation(Experiment experiment, Annotation annotation) {
        // nothing to do
    }

    @Override
    public void onAddedConfidence(Experiment experiment, Confidence confidence) {
        // nothing to do
    }

    @Override
    public void onRemovedConfidence(Experiment experiment, Confidence confidence) {
        // nothing to do
    }

    @Override
    public void onEnrichmentComplete(Experiment object, EnrichmentStatus status, String message) {
        if (experimentUpdates.containsKey(object)){
            ExperimentUpdates updates = experimentUpdates.get(object);
            try {
                if (!updates.getAddedXrefs().isEmpty()){

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedXrefs(),
                            context.getExperimentXrefSynchronizer());
                    object.getXrefs().removeAll(updates.getAddedXrefs());
                    for (Xref obj : synchronizedXrefs){
                        if (!object.getXrefs().contains(obj)){
                            object.getXrefs().add(obj);
                        }
                    }
                }
                if (!updates.getAddedAnnotations().isEmpty()){

                    List<Annotation> synchronizedAnnotations = IntactEnricherUtils.synchronizeAnnotationsToEnrich(updates.getAddedAnnotations(),
                            context.getExperimentAnnotationSynchronizer());
                    object.getAnnotations().removeAll(updates.getAddedXrefs());
                    for (Annotation obj : synchronizedAnnotations){
                        if (!object.getAnnotations().contains(obj)){
                            object.getAnnotations().add(obj);
                        }
                    }
                }
                if (!updates.getAddedVariableParameters().isEmpty()){

                    List<VariableParameter> synchronizedParameters = IntactEnricherUtils.synchronizeVariableParametersToEnrich(updates.getAddedVariableParameters(),
                            context.getVariableParameterSynchronizer());
                    object.getVariableParameters().removeAll(updates.getAddedXrefs());
                    for (VariableParameter obj : synchronizedParameters){
                        if (!object.getVariableParameters().contains(obj)){
                            object.getVariableParameters().add(obj);
                        }
                    }
                }
                if (!updates.getAddedInteractions().isEmpty()){

                    List<InteractionEvidence> synchronizedEvidences = IntactEnricherUtils.synchronizeInteractionsToEnrich(updates.getAddedInteractions(),
                            context.getInteractionSynchronizer());
                    object.getInteractionEvidences().removeAll(updates.getAddedInteractions());
                    for (InteractionEvidence f : synchronizedEvidences){
                        if (!object.getInteractionEvidences().contains(f)){
                            object.getInteractionEvidences().add(f);
                        }
                    }
                }
                experimentUpdates.remove(object);
            } catch (PersisterException e) {
                experimentUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged experiment", e);
            } catch (FinderException e) {
                experimentUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged experiment", e);
            } catch (SynchronizerException e) {
                experimentUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged experiment", e);
            }
        }
    }

    @Override
    public void onEnrichmentError(Experiment object, String message, Exception e) {
        if (experimentUpdates.containsKey(object)){
            ExperimentUpdates updates = experimentUpdates.get(object);
            try {
                if (!updates.getAddedXrefs().isEmpty()){

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedXrefs(),
                            context.getExperimentXrefSynchronizer());
                    object.getXrefs().removeAll(updates.getAddedXrefs());
                    for (Xref obj : synchronizedXrefs){
                        if (!object.getXrefs().contains(obj)){
                            object.getXrefs().add(obj);
                        }
                    }
                }
                if (!updates.getAddedAnnotations().isEmpty()){

                    List<Annotation> synchronizedAnnotations = IntactEnricherUtils.synchronizeAnnotationsToEnrich(updates.getAddedAnnotations(),
                            context.getExperimentAnnotationSynchronizer());
                    object.getAnnotations().removeAll(updates.getAddedXrefs());
                    for (Annotation obj : synchronizedAnnotations){
                        if (!object.getAnnotations().contains(obj)){
                            object.getAnnotations().add(obj);
                        }
                    }
                }
                if (!updates.getAddedVariableParameters().isEmpty()){

                    List<VariableParameter> synchronizedParameters = IntactEnricherUtils.synchronizeVariableParametersToEnrich(updates.getAddedVariableParameters(),
                            context.getVariableParameterSynchronizer());
                    object.getVariableParameters().removeAll(updates.getAddedXrefs());
                    for (VariableParameter obj : synchronizedParameters){
                        if (!object.getVariableParameters().contains(obj)){
                            object.getVariableParameters().add(obj);
                        }
                    }
                }
                if (!updates.getAddedInteractions().isEmpty()){

                    List<InteractionEvidence> synchronizedEvidences = IntactEnricherUtils.synchronizeInteractionsToEnrich(updates.getAddedInteractions(),
                            context.getInteractionSynchronizer());
                    object.getInteractionEvidences().removeAll(updates.getAddedInteractions());
                    for (InteractionEvidence f : synchronizedEvidences){
                        if (!object.getInteractionEvidences().contains(f)){
                            object.getInteractionEvidences().add(f);
                        }
                    }
                }
                experimentUpdates.remove(object);
            } catch (PersisterException e2) {
                experimentUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged experiment", e2);
            } catch (FinderException e2) {
                experimentUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged experiment", e2);
            } catch (SynchronizerException e2) {
                experimentUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged experiment", e2);
            }
        }
    }

    @Override
    public void onAddedXref(Experiment experiment, Xref xref) {
        if (this.experimentUpdates.containsKey(experiment)){
            this.experimentUpdates.get(experiment).getAddedXrefs().add(xref);
        }
        else{
            ExperimentUpdates updates = new ExperimentUpdates();
            updates.getAddedXrefs().add(xref);
            this.experimentUpdates.put(experiment, updates);
        }
    }

    @Override
    public void onRemovedXref(Experiment experiment, Xref xref) {
        // nothing to do
    }

    public Map<Experiment, ExperimentUpdates> getExperimentUpdates() {
        return experimentUpdates;
    }
}
