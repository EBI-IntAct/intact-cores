package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.RangeUtils;
import psidev.psi.mi.jami.utils.clone.FeatureCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledFeature;
import uk.ac.ebi.intact.jami.model.extension.ModelledResultingSequence;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Default finder/synchronizer for modelled features
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class ModelledFeatureSynchronizer extends FeatureSynchronizerTemplate<ModelledFeature, IntactModelledFeature> {

    public ModelledFeatureSynchronizer(SynchronizerContext context){
        super(context, IntactModelledFeature.class);
    }

    public void clearCache() {
        super.clearCache();
    }

    @Override
    public void synchronizeProperties(IntactModelledFeature intactFeature) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizeProperties(intactFeature);
        // then check aliases
        prepareAliases(intactFeature);
        // then check annotations
        prepareAnnotations(intactFeature);
        // then check xrefs
        prepareXrefs(intactFeature);
        // then check ranges
        prepareRanges(intactFeature);
    }

    @Override
    protected IntactModelledFeature instantiateNewPersistentInstance(ModelledFeature object, Class<? extends IntactModelledFeature> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactModelledFeature newFeature = new IntactModelledFeature();
        FeatureCloner.copyAndOverrideModelledFeaturesProperties(object, newFeature);
        newFeature.setParticipant(object.getParticipant());
        return newFeature;
    }

    protected void prepareRanges(IntactModelledFeature intactFeature) throws PersisterException, FinderException, SynchronizerException {
        if (intactFeature.areRangesInitialized()){
            List<Range> rangesToPersist = new ArrayList<Range>(intactFeature.getRanges());
            for (Range range : rangesToPersist){
                // initialise resulting sequence
                if (intactFeature.getParticipant() != null){
                    Interactor interactor = intactFeature.getParticipant().getInteractor();
                    if (interactor instanceof Polymer){
                        prepareRangeResultingSequence((Polymer)interactor, range);
                    }
                }
                // do not persist or merge ranges because of cascades
                Range featureRange = getContext().getModelledRangeSynchronizer().synchronize(range, false);
                // we have a different instance because needed to be synchronized
                if (featureRange != range){
                    intactFeature.getRanges().remove(range);
                    intactFeature.getRanges().add(featureRange);
                }
            }
        }
    }

    protected void prepareRangeResultingSequence(Polymer polymer, Range range) {
        if (polymer != null){
            String sequence = polymer.getSequence();
            if (range.getResultingSequence() == null){
                range.setResultingSequence(new ModelledResultingSequence(RangeUtils.extractRangeSequence(range, sequence), null));
            }
            else{
                range.getResultingSequence().setOriginalSequence(RangeUtils.extractRangeSequence(range, sequence));
            }
        }
    }

    protected void prepareXrefs(IntactModelledFeature intactFeature) throws FinderException, PersisterException, SynchronizerException {
        if (intactFeature.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactFeature.getDbXrefs());
            for (Xref xref : xrefsToPersist){
                // do not persist or merge xrefs because of cascades
                Xref featureXref = getContext().getModelledFeatureXrefSynchronizer().synchronize(xref, false);
                // we have a different instance because needed to be synchronized
                if (featureXref != xref){
                    intactFeature.getDbXrefs().remove(xref);
                    intactFeature.getDbXrefs().add(featureXref);
                }
            }
        }
    }

    protected void prepareAnnotations(IntactModelledFeature intactFeature) throws FinderException, PersisterException, SynchronizerException {
        if (intactFeature.areAnnotationsInitialized()){
            List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactFeature.getAnnotations());
            for (Annotation annotation : annotationsToPersist){
                // do not persist or merge annotations because of cascades
                Annotation featureAnnotation = getContext().getModelledFeatureAnnotationSynchronizer().synchronize(annotation, false);
                // we have a different instance because needed to be synchronized
                if (featureAnnotation != annotation){
                    intactFeature.getAnnotations().remove(annotation);
                    intactFeature.getAnnotations().add(featureAnnotation);
                }
            }
        }
    }

    protected void prepareAliases(IntactModelledFeature intactFeature) throws FinderException, PersisterException, SynchronizerException {
        if (intactFeature.areAliasesInitialized()){
            List<Alias> aliasesToPersist = new ArrayList<Alias>(intactFeature.getAliases());
            for (Alias alias : aliasesToPersist){
                // do not persist or merge alias because of cascades
                Alias featureAlias = getContext().getModelledFeatureAliasSynchronizer().synchronize(alias, false);
                // we have a different instance because needed to be synchronized
                if (featureAlias != alias){
                    intactFeature.getAliases().remove(alias);
                    intactFeature.getAliases().add(featureAlias);
                }
            }
        }
    }
}
