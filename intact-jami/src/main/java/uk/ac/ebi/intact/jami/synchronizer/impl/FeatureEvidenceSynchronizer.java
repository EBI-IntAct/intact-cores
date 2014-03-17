package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.FeatureCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.FeatureEvidenceMergerEnrichOnly;
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
        prepareAliases(intactFeature);
        // then check annotations
        prepareAnnotations(intactFeature);
        // then check xrefs
        prepareXrefs(intactFeature);
        // then detection methods
        prepareDetectionMethods(intactFeature);
        // then check ranges
        prepareRanges(intactFeature);
    }

    protected void prepareRanges(IntactFeatureEvidence intactFeature) throws PersisterException, FinderException, SynchronizerException {
        if (intactFeature.areRangesInitialized()){
            List<Range> rangesToPersist = new ArrayList<Range>(intactFeature.getRanges());
            for (Range range : rangesToPersist){
                // do not persist or merge ranges because of cascades
                Range featureRange = getContext().getExperimentalRangeSynchronizer().synchronize(range, false);
                // we have a different instance because needed to be synchronized
                if (featureRange != range){
                    intactFeature.getRanges().remove(range);
                    intactFeature.getRanges().add(featureRange);
                }
            }
        }
    }

    protected void prepareDetectionMethods(IntactFeatureEvidence intactFeature) throws FinderException, PersisterException, SynchronizerException {
        if (intactFeature.getFeatureIdentification() != null){
            intactFeature.setFeatureIdentification(getContext().getFeatureDetectionMethodSynchronizer().synchronize(intactFeature.getFeatureIdentification(), true));
        }
        if (intactFeature.areDetectionMethodsInitialized()){
            List<CvTerm> methodsToPersist = new ArrayList<CvTerm>(intactFeature.getDetectionMethods());
            for (CvTerm method : methodsToPersist){
                CvTerm featureTerm = getContext().getFeatureDetectionMethodSynchronizer().synchronize(method, true);
                // we have a different instance because needed to be synchronized
                if (featureTerm != method){
                    intactFeature.getDetectionMethods().remove(method);
                    intactFeature.getDetectionMethods().add(featureTerm);
                }
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

    protected void prepareXrefs(IntactFeatureEvidence intactFeature) throws FinderException, PersisterException, SynchronizerException {
        if (intactFeature.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactFeature.getDbXrefs());
            for (Xref xref : xrefsToPersist){
                // do not persist or merge xrefs because of cascades
                Xref featureXref = getContext().getFeatureEvidenceXrefSynchronizer().synchronize(xref, false);
                // we have a different instance because needed to be synchronized
                if (featureXref != xref){
                    intactFeature.getDbXrefs().remove(xref);
                    intactFeature.getDbXrefs().add(featureXref);
                }
            }
        }
    }

    protected void prepareAnnotations(IntactFeatureEvidence intactFeature) throws FinderException, PersisterException, SynchronizerException {
        if (intactFeature.areAnnotationsInitialized()){
            List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactFeature.getAnnotations());
            for (Annotation annotation : annotationsToPersist){
                // do not persist or merge annotations because of cascades
                Annotation featureAnnotation = getContext().getFeatureEvidenceAnnotationSynchronizer().synchronize(annotation, false);
                // we have a different instance because needed to be synchronized
                if (featureAnnotation != annotation){
                    intactFeature.getAnnotations().remove(annotation);
                    intactFeature.getAnnotations().add(featureAnnotation);
                }
            }
        }
    }

    protected void prepareAliases(IntactFeatureEvidence intactFeature) throws FinderException, PersisterException, SynchronizerException {
        if (intactFeature.areAliasesInitialized()){
            List<Alias> aliasesToPersist = new ArrayList<Alias>(intactFeature.getAliases());
            for (Alias alias : aliasesToPersist){
                // do not persist or merge alias because of cascades
                Alias featureAlias = getContext().getFeatureEvidenceAliasSynchronizer().synchronize(alias, false);
                // we have a different instance because needed to be synchronized
                if (featureAlias != alias){
                    intactFeature.getAliases().remove(alias);
                    intactFeature.getAliases().add(featureAlias);
                }
            }
        }
    }
}
