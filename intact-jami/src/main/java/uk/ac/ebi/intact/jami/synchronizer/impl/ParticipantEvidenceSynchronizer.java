package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.ParticipantCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.ParticipantEvidenceMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.IntactParticipantEvidence;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactEnricherUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Synchronizer for experimental entities and participants
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/01/14</pre>
 */

public class ParticipantEvidenceSynchronizer extends ParticipantSynchronizerTemplate<ParticipantEvidence, IntactParticipantEvidence> {

    public ParticipantEvidenceSynchronizer(SynchronizerContext context){
        super(context, IntactParticipantEvidence.class);
    }

    public void synchronizeProperties(IntactParticipantEvidence intactEntity) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizeProperties(intactEntity);
        // then check aliases
        prepareAliases(intactEntity, true);
        // then check annotations
        prepareAnnotations(intactEntity, true);
        // then check xrefs
        prepareXrefs(intactEntity, true);
        // then check causal relationships
        prepareCausalRelationships(intactEntity, true);
        // check expressed in organism
        prepareOrganism(intactEntity, true);
        // then check experimentalRole
        prepareExperimentalRole(intactEntity, true);
        // then check participant identification methods
        prepareIdentificationMethods(intactEntity, true);
        // then check experimental preparations
        prepareExperimentalPreparations(intactEntity, true);
        // then check confidences
        prepareConfidences(intactEntity, true);
        // then check parameters
        prepareParameters(intactEntity, true);
    }

    public void convertPersistableProperties(IntactParticipantEvidence intactEntity) throws FinderException, PersisterException, SynchronizerException {
        super.convertPersistableProperties(intactEntity);
        // then check aliases
        prepareAliases(intactEntity, false);
        // then check annotations
        prepareAnnotations(intactEntity, false);
        // then check xrefs
        prepareXrefs(intactEntity, false);
        // then check causal relationships
        prepareCausalRelationships(intactEntity, false);
        // check expressed in organism
        prepareOrganism(intactEntity, false);
        // then check experimentalRole
        prepareExperimentalRole(intactEntity, false);
        // then check participant identification methods
        prepareIdentificationMethods(intactEntity, false);
        // then check experimental preparations
        prepareExperimentalPreparations(intactEntity, false);
        // then check confidences
        prepareConfidences(intactEntity, false);
        // then check parameters
        prepareParameters(intactEntity, false);
    }

    @Override
    protected IntactParticipantEvidence instantiateNewPersistentInstance(ParticipantEvidence object, Class<? extends IntactParticipantEvidence> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactParticipantEvidence newParticipant = new IntactParticipantEvidence(object.getInteractor());
        ParticipantCloner.copyAndOverrideParticipantEvidenceProperties(object, newParticipant, false);
        return newParticipant;
    }

    protected void prepareCausalRelationships(IntactParticipantEvidence intactEntity, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        if (intactEntity.areCausalRelationshipsInitialized()){
            List<CausalRelationship> relationshipsToPersist = new ArrayList<CausalRelationship>(intactEntity.getCausalRelationships());
            intactEntity.getCausalRelationships().clear();
            for (CausalRelationship causalRelationship : relationshipsToPersist){
                // do not persist or merge causalRelationship because of cascades
                CausalRelationship persistentRelationship = enableSynchronization ?
                        getContext().getExperimentalCausalRelationshipSynchronizer().synchronize(causalRelationship, false) :
                        getContext().getExperimentalCausalRelationshipSynchronizer().convertToPersistentObject(causalRelationship);
                // we have a different instance because needed to be synchronized
                intactEntity.getCausalRelationships().add(persistentRelationship);
            }
        }
    }

    protected void prepareOrganism(IntactParticipantEvidence intactEntity, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        Organism organism = intactEntity.getExpressedInOrganism();
        if (organism != null){
            intactEntity.setExpressedInOrganism(enableSynchronization ?
                    getContext().getOrganismSynchronizer().synchronize(organism, true) :
                    getContext().getOrganismSynchronizer().convertToPersistentObject(organism));
        }
    }

    protected void prepareConfidences(IntactParticipantEvidence intactEntity, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        if (intactEntity.areConfidencesInitialized()){
            List<Confidence> confidencesToPersist = new ArrayList<Confidence>(intactEntity.getConfidences());
            intactEntity.getConfidences().clear();
            for (Confidence confidence : confidencesToPersist){
                // do not persist or merge confidences because of cascades
                Confidence persistentConfidence = enableSynchronization ?
                        getContext().getParticipantEvidenceConfidenceSynchronizer().synchronize(confidence, false) :
                        getContext().getParticipantEvidenceConfidenceSynchronizer().convertToPersistentObject(confidence);
                // we have a different instance because needed to be synchronized
                intactEntity.getConfidences().add(persistentConfidence);

            }
        }
    }

    protected void prepareExperimentalPreparations(IntactParticipantEvidence intactEntity, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        if (intactEntity.areExperimentalPreparationsInitialized()){
            List<CvTerm> preparationsToPersist = new ArrayList<CvTerm>(intactEntity.getExperimentalPreparations());
            intactEntity.getExperimentalPreparations().clear();
            for (CvTerm preparation : preparationsToPersist){
                CvTerm persistentPreparation = enableSynchronization ?
                        getContext().getExperimentalPreparationSynchronizer().synchronize(preparation, true) :
                        getContext().getExperimentalPreparationSynchronizer().convertToPersistentObject(preparation);
                // we have a different instance because needed to be synchronized
                intactEntity.getExperimentalPreparations().add(persistentPreparation);

            }
        }
    }

    protected void prepareParameters(IntactParticipantEvidence intactEntity, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactEntity.areParametersInitialized()){
            List<Parameter> parametersToPersist = new ArrayList<Parameter>(intactEntity.getParameters());
            intactEntity.getParameters().clear();
            for (Parameter parameter : parametersToPersist){
                Parameter persistentParameter = enableSynchronization ?
                        getContext().getParticipantEvidenceParameterSynchronizer().synchronize(parameter, false) :
                        getContext().getParticipantEvidenceParameterSynchronizer().convertToPersistentObject(parameter);
                // we have a different instance because needed to be synchronized
               intactEntity.getParameters().add(persistentParameter);

            }
        }
    }

    protected void prepareIdentificationMethods(IntactParticipantEvidence intactEntity, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        if (intactEntity.areIdentificationMethodsInitialized()){
            List<CvTerm> methodsToPersist = new ArrayList<CvTerm>(intactEntity.getDbIdentificationMethods());
            intactEntity.getDbIdentificationMethods().clear();
            for (CvTerm term : methodsToPersist){
                CvTerm persistentTerm = enableSynchronization ?
                        getContext().getParticipantDetectionMethodSynchronizer().synchronize(term, true) :
                        getContext().getParticipantDetectionMethodSynchronizer().convertToPersistentObject(term);
                // we have a different instance because needed to be synchronized
                intactEntity.getDbIdentificationMethods().add(persistentTerm);

            }
        }
    }

    protected void prepareXrefs(IntactParticipantEvidence intactEntity, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactEntity.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactEntity.getXrefs());
            intactEntity.getXrefs().clear();
            for (Xref xref : xrefsToPersist){
                // do not persist or merge xrefs because of cascades
                Xref persistentXref = enableSynchronization ?
                        getContext().getParticipantEvidenceXrefSynchronizer().synchronize(xref, false) :
                        getContext().getParticipantEvidenceXrefSynchronizer().convertToPersistentObject(xref);
                // we have a different instance because needed to be synchronized
                intactEntity.getXrefs().add(persistentXref);
            }
        }
    }

    protected void prepareAnnotations(IntactParticipantEvidence intactEntity, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactEntity.areAnnotationsInitialized()){
            List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactEntity.getAnnotations());
            intactEntity.getAnnotations().clear();
            for (Annotation annotation : annotationsToPersist){
                // do not persist or merge annotations because of cascades
                Annotation persistentAnnotation = enableSynchronization ?
                        getContext().getParticipantEvidenceAnnotationSynchronizer().synchronize(annotation, false) :
                        getContext().getParticipantEvidenceAnnotationSynchronizer().convertToPersistentObject(annotation);
                // we have a different instance because needed to be synchronized
                intactEntity.getAnnotations().add(persistentAnnotation);
            }
        }
    }

    protected void prepareAliases(IntactParticipantEvidence intactEntity, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactEntity.areAliasesInitialized()){
            List<Alias> aliasesToPersist = new ArrayList<Alias>(intactEntity.getAliases());
            intactEntity.getAliases().clear();
            for (Alias alias : aliasesToPersist){
                // do not persist or merge alias because of cascades
                Alias persistentAlias = enableSynchronization ?
                        getContext().getParticipantEvidenceAliasSynchronizer().synchronize(alias, false) :
                        getContext().getParticipantEvidenceAliasSynchronizer().convertToPersistentObject(alias);
                // we have a different instance because needed to be synchronized
                intactEntity.getAliases().add(persistentAlias);
            }
        }
    }

    protected void prepareExperimentalRole(IntactParticipantEvidence intactParticipant, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        CvTerm role = intactParticipant.getExperimentalRole();
        intactParticipant.setExperimentalRole(enableSynchronization ?
                getContext().getExperimentalRoleSynchronizer().synchronize(role, true) :
                getContext().getExperimentalRoleSynchronizer().convertToPersistentObject(role));
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new ParticipantEvidenceMergerEnrichOnly<ParticipantEvidence, IntactParticipantEvidence>());
    }

    @Override
    protected IntactDbSynchronizer getFeatureSynchronizer() {
        return getContext().getFeatureEvidenceSynchronizer();
    }

    @Override
    public void deleteRelatedProperties(IntactParticipantEvidence intactParticipant){
        super.deleteRelatedProperties(intactParticipant);
        for (CausalRelationship f : intactParticipant.getRelatedCausalRelationships()){
            getContext().getExperimentalCausalRelationshipSynchronizer().delete(f);
        }
        intactParticipant.getRelatedCausalRelationships().clear();
        for (Range f : intactParticipant.getRelatedRanges()){
            f.setParticipant(null);
        }
        intactParticipant.getRelatedRanges().clear();
    }

    @Override
    protected void persistObject(IntactParticipantEvidence existingInstance) {
        // first remove all dependencies to other participants to avoid cycle dependencies when persisting the objects
        Collection<CausalRelationship> relationships = new ArrayList<CausalRelationship>(existingInstance.getCausalRelationships());
        existingInstance.getCausalRelationships().clear();

        super.persistObject(existingInstance);

        // after persistence, re-attach dependent objects to avoid internal loops when participants are called by each other
        existingInstance.getCausalRelationships().addAll(relationships);
    }

    @Override
    protected void synchronizePropertiesBeforeCacheMerge(IntactParticipantEvidence objectInCache, IntactParticipantEvidence originalParticipant) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizePropertiesBeforeCacheMerge(objectInCache, originalParticipant);
        // then check aliases
        IntactEnricherUtils.synchronizeAliasesToEnrich(originalParticipant.getAliases(),
                objectInCache.getAliases(),
                getContext().getParticipantEvidenceAliasSynchronizer());

        // then check annotations
        IntactEnricherUtils.synchronizeAnnotationsToEnrich(originalParticipant.getAnnotations(),
                objectInCache.getAnnotations(),
                getContext().getParticipantEvidenceAnnotationSynchronizer());

        // then check xrefs
        IntactEnricherUtils.synchronizeXrefsToEnrich(originalParticipant.getXrefs(),
                objectInCache.getXrefs(),
                getContext().getParticipantEvidenceXrefSynchronizer());

        // then check causal relationships
        IntactEnricherUtils.synchronizeCausalRelationshipsToEnrich(originalParticipant.getCausalRelationships(),
                objectInCache.getCausalRelationships(),
                getContext().getExperimentalCausalRelationshipSynchronizer());

        // then check participant identification methods
        IntactEnricherUtils.synchronizeCvsToEnrich(originalParticipant.getIdentificationMethods(),
                objectInCache.getIdentificationMethods(),
                getContext().getParticipantDetectionMethodSynchronizer());

        // then check experimental preparations
        IntactEnricherUtils.synchronizeCvsToEnrich(originalParticipant.getExperimentalPreparations(),
                objectInCache.getExperimentalPreparations(),
                getContext().getExperimentalPreparationSynchronizer());

        // then check confidences
        IntactEnricherUtils.synchronizeConfidencesToEnrich(originalParticipant.getConfidences(),
                objectInCache.getConfidences(),
                getContext().getParticipantEvidenceConfidenceSynchronizer());

        // then check parameters
        IntactEnricherUtils.synchronizeParametersToEnrich(originalParticipant.getParameters(),
                objectInCache.getParameters(),
                getContext().getParticipantEvidenceParameterSynchronizer());
    }
}


