package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.collections.CollectionUtils;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.RangeUtils;
import psidev.psi.mi.jami.utils.clone.FeatureCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.FeatureEvidenceMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.ExperimentalResultingSequence;
import uk.ac.ebi.intact.jami.model.extension.IntactFeatureEvidence;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Default finder/synchronizer for features
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class FeatureEvidenceSynchronizer extends FeatureSynchronizerTemplate<FeatureEvidence, IntactFeatureEvidence> {

    public FeatureEvidenceSynchronizer(SynchronizerContext context){
        super(context, IntactFeatureEvidence.class);
    }

    public void clearCache() {
        super.clearCache();
    }

    @Override
    public void synchronizeProperties(IntactFeatureEvidence intactFeature) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizeProperties(intactFeature);
        // then check aliases
        prepareAliases(intactFeature, true);
        // then check annotations
        prepareAnnotations(intactFeature, true);
        // then check xrefs
        prepareXrefs(intactFeature, true);
        // then detection methods
        prepareDetectionMethods(intactFeature, true);
        // then check ranges
        prepareRanges(intactFeature, true);
        // then synchronize parameters
        prepareParameters(intactFeature, true);
    }

    @Override
    public void convertPersistableProperties(IntactFeatureEvidence intactFeature) throws FinderException, PersisterException, SynchronizerException {
        super.convertPersistableProperties(intactFeature);
        // then check aliases
        prepareAliases(intactFeature, false);
        // then check annotations
        prepareAnnotations(intactFeature, false);
        // then check xrefs
        prepareXrefs(intactFeature, false);
        // then detection methods
        prepareDetectionMethods(intactFeature, false);
        // then check ranges
        prepareRanges(intactFeature, false);
        // then synchronize parameters
        prepareParameters(intactFeature, false);
    }

    private void prepareParameters(IntactFeatureEvidence intactFeature, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        if (intactFeature.areParametersInitialized()){
            List<Parameter> parametersToPersist = new ArrayList<Parameter>(intactFeature.getParameters());
            intactFeature.getParameters().clear();
            for (Parameter parameter : parametersToPersist){
                Parameter persistentParameter = enableSynchronization ?
                        getContext().getFeatureParameterSynchronizer().synchronize(parameter, false) :
                        getContext().getFeatureParameterSynchronizer().convertToPersistentObject(parameter);
                // we have a different instance because needed to be synchronized
                intactFeature.getParameters().add(persistentParameter);
            }
        }
    }

    protected void prepareRanges(IntactFeatureEvidence intactFeature, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        if (intactFeature.areRangesInitialized()){
            List<Range> rangesToPersist = new ArrayList<Range>(intactFeature.getRanges());
            intactFeature.getRanges().clear();
            for (Range range : rangesToPersist){
                // initialise resulting sequence
                if (intactFeature.getParticipant() != null){
                    Interactor interactor = intactFeature.getParticipant().getInteractor();
                    if (interactor instanceof Polymer){
                        prepareRangeResultingSequence((Polymer)interactor, range);
                    }
                }

                // do not persist or merge ranges because of cascades
                Range featureRange = enableSynchronization ?
                        getContext().getExperimentalRangeSynchronizer().synchronize(range, false) :
                        getContext().getExperimentalRangeSynchronizer().convertToPersistentObject(range);
                // we have a different instance because needed to be synchronized
                intactFeature.getRanges().add(featureRange);
            }
        }
    }

    protected void prepareRangeResultingSequence(Polymer polymer, Range range) {
        if (polymer != null){
            String sequence = polymer.getSequence();
            if (sequence != null && range.getResultingSequence() == null){
                range.setResultingSequence(new ExperimentalResultingSequence(RangeUtils.extractRangeSequence(range, sequence), null));
            }
            else if (sequence == null && range.getResultingSequence() != null && range.getResultingSequence().getOriginalSequence() != null){
                range.getResultingSequence().setOriginalSequence(null);
            }
            else if (sequence != null){
                range.getResultingSequence().setOriginalSequence(RangeUtils.extractRangeSequence(range, sequence));
            }
        }
    }

    protected void prepareDetectionMethods(IntactFeatureEvidence intactFeature, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactFeature.getFeatureIdentification() != null){
            intactFeature.setFeatureIdentification(enableSynchronization ?
                    getContext().getFeatureDetectionMethodSynchronizer().synchronize(intactFeature.getFeatureIdentification(), true) :
                    getContext().getFeatureDetectionMethodSynchronizer().convertToPersistentObject(intactFeature.getFeatureIdentification()));
        }
        if (intactFeature.areDetectionMethodsInitialized()){
            List<CvTerm> methodsToPersist = new ArrayList<CvTerm>(intactFeature.getDbDetectionMethods());
            intactFeature.getDbDetectionMethods().clear();
            for (CvTerm method : methodsToPersist){
                CvTerm featureTerm = enableSynchronization ?
                        getContext().getFeatureDetectionMethodSynchronizer().synchronize(method, true) :
                        getContext().getFeatureDetectionMethodSynchronizer().convertToPersistentObject(method);
                // we have a different instance because needed to be synchronized
                intactFeature.getDbDetectionMethods().add(featureTerm);
            }
        }
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new FeatureEvidenceMergerEnrichOnly());
    }

    @Override
    protected IntactFeatureEvidence instantiateNewPersistentInstance(FeatureEvidence object, Class<? extends IntactFeatureEvidence> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactFeatureEvidence newFeature = new IntactFeatureEvidence();
        FeatureCloner.copyAndOverrideFeatureEvidenceProperties(object, newFeature);
        newFeature.setParticipant(object.getParticipant());
        return newFeature;
    }

    protected void prepareXrefs(IntactFeatureEvidence intactFeature, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactFeature.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactFeature.getDbXrefs());
            intactFeature.getDbXrefs().clear();
            for (Xref xref : xrefsToPersist){
                // do not persist or merge xrefs because of cascades
                Xref featureXref = enableSynchronization ?
                        getContext().getFeatureEvidenceXrefSynchronizer().synchronize(xref, false) :
                        getContext().getFeatureEvidenceXrefSynchronizer().convertToPersistentObject(xref);
                // we have a different instance because needed to be synchronized
                intactFeature.getDbXrefs().add(featureXref);
            }
        }
    }

    protected void prepareAnnotations(IntactFeatureEvidence intactFeature, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactFeature.areAnnotationsInitialized()){
            List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactFeature.getAnnotations());
            intactFeature.getAnnotations().clear();
            for (Annotation annotation : annotationsToPersist){
                // do not persist or merge annotations because of cascades
                Annotation featureAnnotation = enableSynchronization ?
                        getContext().getFeatureEvidenceAnnotationSynchronizer().synchronize(annotation, false) :
                        getContext().getFeatureEvidenceAnnotationSynchronizer().convertToPersistentObject(annotation);
                // we have a different instance because needed to be synchronized
                intactFeature.getAnnotations().add(featureAnnotation);
            }
        }
    }

    protected void prepareAliases(IntactFeatureEvidence intactFeature, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactFeature.areAliasesInitialized()){
            List<Alias> aliasesToPersist = new ArrayList<Alias>(intactFeature.getAliases());
            intactFeature.getAliases().clear();
            for (Alias alias : aliasesToPersist){
                // do not persist or merge alias because of cascades
                Alias featureAlias = enableSynchronization ?
                        getContext().getFeatureEvidenceAliasSynchronizer().synchronize(alias, false) :
                        getContext().getFeatureEvidenceAliasSynchronizer().convertToPersistentObject(alias);
                // we have a different instance because needed to be synchronized
                intactFeature.getAliases().add(featureAlias);
            }
        }
    }

    @Override
    protected void synchronizePropertiesBeforeCacheMerge(IntactFeatureEvidence intactFeature, IntactFeatureEvidence originalFeature) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizePropertiesBeforeCacheMerge(intactFeature, originalFeature);
        // then check aliases
        if (!CollectionUtils.isEqualCollection(intactFeature.getAliases(), originalFeature.getAliases())){
            prepareAliases(intactFeature, true);
        }
        // then check annotations
        if (!CollectionUtils.isEqualCollection(intactFeature.getAnnotations(), originalFeature.getAnnotations())){
            prepareAnnotations(intactFeature, true);
        }
        // then check xrefs
        if (!CollectionUtils.isEqualCollection(intactFeature.getDbXrefs(), originalFeature.getDbXrefs())){
            prepareXrefs(intactFeature, true);
        }
        // then detection methods
        if (!CollectionUtils.isEqualCollection(intactFeature.getDetectionMethods(), originalFeature.getDetectionMethods())){
            prepareDetectionMethods(intactFeature, true);
        }
        // then check ranges
        if (!CollectionUtils.isEqualCollection(intactFeature.getRanges(), originalFeature.getRanges())){
            prepareRanges(intactFeature, true);
        }
        // then synchronize parameters
        if (!CollectionUtils.isEqualCollection(intactFeature.getParameters(), originalFeature.getParameters())){
            prepareParameters(intactFeature, true);
        }
    }
}
