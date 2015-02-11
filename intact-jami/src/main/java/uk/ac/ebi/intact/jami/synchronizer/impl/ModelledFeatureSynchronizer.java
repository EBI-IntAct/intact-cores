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
import uk.ac.ebi.intact.jami.synchronizer.listener.impl.AbstractDbFeatureEnricherListener;
import uk.ac.ebi.intact.jami.synchronizer.listener.impl.DbModelledFeatureEnricherListener;

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

    protected AbstractDbFeatureEnricherListener<ModelledFeature> instantiateDefaultEnricherlistener() {
        return new DbModelledFeatureEnricherListener(getContext(), this);
    }

    @Override
    public void synchronizeProperties(IntactModelledFeature intactFeature) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizeProperties(intactFeature);
        // then check aliases
        prepareAliases(intactFeature, true);
        // then check annotations
        prepareAnnotations(intactFeature, true);
        // then check xrefs
        prepareXrefs(intactFeature, true);
        // then check ranges
        prepareRanges(intactFeature, true);
    }

    @Override
    public void convertPersistableProperties(IntactModelledFeature intactFeature) throws FinderException, PersisterException, SynchronizerException {
        super.convertPersistableProperties(intactFeature);
        // then check aliases
        prepareAliases(intactFeature, false);
        // then check annotations
        prepareAnnotations(intactFeature, false);
        // then check xrefs
        prepareXrefs(intactFeature, false);
        // then check ranges
        prepareRanges(intactFeature, false);
    }

    @Override
    protected IntactModelledFeature instantiateNewPersistentInstance(ModelledFeature object, Class<? extends IntactModelledFeature> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactModelledFeature newFeature = new IntactModelledFeature();
        FeatureCloner.copyAndOverrideModelledFeaturesProperties(object, newFeature);
        newFeature.setParticipant(object.getParticipant());
        return newFeature;
    }

    protected void prepareRanges(IntactModelledFeature intactFeature, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        if (intactFeature.areRangesInitialized()){
            List<Range> rangesToPersist = new ArrayList<Range>(intactFeature.getRanges());
            intactFeature.getRanges().clear();
            int index = 0;
            try{
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
                            getContext().getModelledRangeSynchronizer().synchronize(range, false) :
                            getContext().getModelledRangeSynchronizer().convertToPersistentObject(range);
                    // we have a different instance because needed to be synchronized
                    intactFeature.getRanges().add(featureRange);
                    index++;
                }
            }
            finally {
                // always add previous properties in case of exception
                if (index < rangesToPersist.size() - 1) {
                    for (int i = index; i < rangesToPersist.size(); i++) {
                        intactFeature.getRanges().add(rangesToPersist.get(i));
                    }
                }
            }
        }
    }

    protected void prepareRangeResultingSequence(Polymer polymer, Range range) {
        if (polymer != null){
            String sequence = polymer.getSequence();
            if (sequence != null && range.getResultingSequence() == null){
                range.setResultingSequence(new ModelledResultingSequence(RangeUtils.extractRangeSequence(range, sequence), null));
            }
            else if (sequence == null && range.getResultingSequence() != null && range.getResultingSequence().getOriginalSequence() != null){
                range.getResultingSequence().setOriginalSequence(null);
            }
            else if (sequence != null){
                range.getResultingSequence().setOriginalSequence(RangeUtils.extractRangeSequence(range, sequence));
            }
        }
    }

    protected void prepareXrefs(IntactModelledFeature intactFeature, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactFeature.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactFeature.getDbXrefs());
            intactFeature.getDbXrefs().clear();
            int index = 0;
            try{
                for (Xref xref : xrefsToPersist){
                    // do not persist or merge xrefs because of cascades
                    Xref featureXref = enableSynchronization ?
                            getContext().getModelledFeatureXrefSynchronizer().synchronize(xref, false) :
                            getContext().getModelledFeatureXrefSynchronizer().convertToPersistentObject(xref);
                    // we have a different instance because needed to be synchronized
                    intactFeature.getDbXrefs().add(featureXref);
                    index++;
                }
            }
            finally {
                // always add previous properties in case of exception
                if (index < xrefsToPersist.size() - 1) {
                    for (int i = index; i < xrefsToPersist.size(); i++) {
                        intactFeature.getDbXrefs().add(xrefsToPersist.get(i));
                    }
                }
            }
        }
    }

    protected void prepareAnnotations(IntactModelledFeature intactFeature, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactFeature.areAnnotationsInitialized()){
            List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactFeature.getAnnotations());
            intactFeature.getAnnotations().clear();
            int index = 0;
            try{
                for (Annotation annotation : annotationsToPersist){
                    // do not persist or merge annotations because of cascades
                    Annotation featureAnnotation = enableSynchronization ?
                            getContext().getModelledFeatureAnnotationSynchronizer().synchronize(annotation, false) :
                            getContext().getModelledFeatureAnnotationSynchronizer().convertToPersistentObject(annotation);
                    // we have a different instance because needed to be synchronized
                    intactFeature.getAnnotations().add(featureAnnotation);
                    index++;
                }
            }
            finally {
                // always add previous properties in case of exception
                if (index < annotationsToPersist.size() - 1) {
                    for (int i = index; i < annotationsToPersist.size(); i++) {
                        intactFeature.getAnnotations().add(annotationsToPersist.get(i));
                    }
                }
            }
        }
    }

    protected void prepareAliases(IntactModelledFeature intactFeature, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactFeature.areAliasesInitialized()){
            List<Alias> aliasesToPersist = new ArrayList<Alias>(intactFeature.getAliases());
            intactFeature.getAliases().clear();
            int index = 0;
            try{
                for (Alias alias : aliasesToPersist){
                    // do not persist or merge alias because of cascades
                    Alias featureAlias = enableSynchronization ?
                            getContext().getModelledFeatureAliasSynchronizer().synchronize(alias, false) :
                            getContext().getModelledFeatureAliasSynchronizer().convertToPersistentObject(alias);
                    // we have a different instance because needed to be synchronized
                    intactFeature.getAliases().add(featureAlias);
                    index++;
                }
            }
            finally {
                // always add previous properties in case of exception
                if (index < aliasesToPersist.size() - 1) {
                    for (int i = index; i < aliasesToPersist.size(); i++) {
                        intactFeature.getAliases().add(aliasesToPersist.get(i));
                    }
                }
            }
        }
    }
}
