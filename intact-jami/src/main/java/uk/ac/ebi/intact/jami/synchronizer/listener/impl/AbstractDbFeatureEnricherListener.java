package uk.ac.ebi.intact.jami.synchronizer.listener.impl;

import org.apache.commons.collections.map.IdentityMap;
import psidev.psi.mi.jami.enricher.listener.EnrichmentStatus;
import psidev.psi.mi.jami.enricher.listener.FeatureEnricherListener;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactMergerException;
import uk.ac.ebi.intact.jami.model.extension.IntactPosition;
import uk.ac.ebi.intact.jami.synchronizer.*;
import uk.ac.ebi.intact.jami.synchronizer.listener.updates.FeatureUpdates;
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

public abstract class AbstractDbFeatureEnricherListener<T extends Feature> implements FeatureEnricherListener<T> {
    private Map<Feature, FeatureUpdates<T>> featureUpdates;
    private SynchronizerContext context;
    private IntactDbSynchronizer dbSynchronizer;

    public AbstractDbFeatureEnricherListener(SynchronizerContext context, IntactDbSynchronizer dbSynchronizer) {
        if (context == null){
            throw new IllegalArgumentException("The listener needs a non null synchronizer context");
        }
        this.context = context;
        if (dbSynchronizer == null){
            throw new IllegalArgumentException("The listener needs a non null feature synchronizer");
        }
        this.dbSynchronizer = dbSynchronizer;
        this.featureUpdates = new IdentityMap();
    }

    @Override
    public void onAddedAnnotation(T interactor, Annotation annotation) {
        if (this.featureUpdates.containsKey(interactor)){
            this.featureUpdates.get(interactor).getAddedAnnotations().add(annotation);
        }
        else{
            FeatureUpdates<T> updates = new FeatureUpdates<T>();
            updates.getAddedAnnotations().add(annotation);
            this.featureUpdates.put(interactor, updates);
        }
    }

    @Override
    public void onRemovedAnnotation(T interactor, Annotation annotation) {
        // nothing to do
    }

    @Override
    public void onEnrichmentComplete(T object, EnrichmentStatus status, String message) {
        if (featureUpdates.containsKey(object)) {
            FeatureUpdates<T> updates = featureUpdates.get(object);
            try {
                if (!updates.getAddedXrefs().isEmpty() && getXrefSynchronizer() != null) {

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedXrefs(),
                            getXrefSynchronizer());
                    object.getXrefs().removeAll(updates.getAddedXrefs());
                    object.getXrefs().addAll(synchronizedXrefs);
                }
                if (!updates.getAddedIdentifiers().isEmpty() && getXrefSynchronizer() != null) {

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedIdentifiers(),
                            getXrefSynchronizer());
                    object.getIdentifiers().removeAll(updates.getAddedIdentifiers());
                    object.getIdentifiers().addAll(synchronizedXrefs);
                }
                if (!updates.getAddedAnnotations().isEmpty() && getAnnotationSynchronizer() != null) {

                    List<Annotation> synchronizedAnnotations = IntactEnricherUtils.synchronizeAnnotationsToEnrich(updates.getAddedAnnotations(),
                            getAnnotationSynchronizer());
                    object.getAnnotations().removeAll(updates.getAddedAnnotations());
                    object.getAnnotations().addAll(synchronizedAnnotations);
                }
                if (!updates.getAddedAliases().isEmpty() && getAliasSynchronizer() != null) {

                    List<Alias> synchronizedAliases = IntactEnricherUtils.synchronizeAliasesToEnrich(updates.getAddedAliases(),
                            getAliasSynchronizer());
                    object.getAliases().removeAll(updates.getAddedAliases());
                    object.getAliases().addAll(synchronizedAliases);
                }
                if (!updates.getAddedRanges().isEmpty() && getRangeSynchronizer() != null) {

                    List<Range> synchronizedRanges = IntactEnricherUtils.synchronizeRangesToEnrich(updates.getAddedRanges(),
                            getRangeSynchronizer());
                    object.getRanges().removeAll(updates.getAddedRanges());
                    object.getRanges().addAll(synchronizedRanges);
                }
                if (!updates.getAddedLinkedFeatures().isEmpty()) {

                    List<T> synchronizedFeatures = IntactEnricherUtils.synchronizeFeaturesToEnrich(updates.getAddedLinkedFeatures(),
                            getDbSynchronizer());
                    object.getLinkedFeatures().removeAll(updates.getAddedLinkedFeatures());
                    for (T f : synchronizedFeatures){
                        if (!object.getLinkedFeatures().contains(f)){
                            object.getLinkedFeatures().add(f);
                        }
                    }
                }
                processOtherUpdates(object, status, message);
                featureUpdates.remove(object);
            } catch (PersisterException e) {
                featureUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged feature", e);
            } catch (FinderException e) {
                featureUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged feature", e);
            } catch (SynchronizerException e) {
                featureUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged feature", e);
            }
        }
    }

    protected abstract void processOtherUpdates(T object, EnrichmentStatus status, String message) throws PersisterException, FinderException,
            SynchronizerException;

    @Override
    public void onEnrichmentError(T object, String message, Exception e) {
        if (featureUpdates.containsKey(object)) {
            FeatureUpdates<T> updates = featureUpdates.get(object);
            try {
                if (!updates.getAddedXrefs().isEmpty()) {

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedXrefs(),
                            getXrefSynchronizer());
                    object.getXrefs().removeAll(updates.getAddedXrefs());
                    object.getXrefs().addAll(synchronizedXrefs);
                }
                if (!updates.getAddedIdentifiers().isEmpty()) {

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedIdentifiers(),
                            getXrefSynchronizer());
                    object.getIdentifiers().removeAll(updates.getAddedIdentifiers());
                    object.getIdentifiers().addAll(synchronizedXrefs);
                }
                if (!updates.getAddedAnnotations().isEmpty()) {

                    List<Annotation> synchronizedAnnotations = IntactEnricherUtils.synchronizeAnnotationsToEnrich(updates.getAddedAnnotations(),
                            getAnnotationSynchronizer());
                    object.getAnnotations().removeAll(updates.getAddedAnnotations());
                    object.getAnnotations().addAll(synchronizedAnnotations);
                }
                if (!updates.getAddedAliases().isEmpty()) {

                    List<Alias> synchronizedAliases = IntactEnricherUtils.synchronizeAliasesToEnrich(updates.getAddedAliases(),
                            getAliasSynchronizer());
                    object.getAliases().removeAll(updates.getAddedAliases());
                    object.getAliases().addAll(synchronizedAliases);
                }
                if (!updates.getAddedRanges().isEmpty()) {

                    List<Range> synchronizedRanges = IntactEnricherUtils.synchronizeRangesToEnrich(updates.getAddedRanges(),
                            getRangeSynchronizer());
                    object.getRanges().removeAll(updates.getAddedRanges());
                    object.getRanges().addAll(synchronizedRanges);
                }
                if (!updates.getAddedLinkedFeatures().isEmpty()) {

                    List<T> synchronizedFeatures = IntactEnricherUtils.synchronizeFeaturesToEnrich(updates.getAddedLinkedFeatures(),
                            getDbSynchronizer());
                    object.getLinkedFeatures().removeAll(updates.getAddedLinkedFeatures());
                    for (T f : synchronizedFeatures){
                        if (!object.getLinkedFeatures().contains(f)){
                            object.getLinkedFeatures().add(f);
                        }
                    }
                }
                processOtherUpdates(object, message, e);
                featureUpdates.remove(object);
            } catch (PersisterException e2) {
                featureUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged feature", e2);
            } catch (FinderException e2) {
                featureUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged feature", e2);
            } catch (SynchronizerException e2) {
                featureUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged feature", e2);
            }
        }
    }

    protected abstract void processOtherUpdates(T object, String message, Exception e) throws PersisterException, FinderException,
            SynchronizerException;

    @Override
    public void onAddedXref(T interactor, Xref xref) {
        if (this.featureUpdates.containsKey(interactor)){
            this.featureUpdates.get(interactor).getAddedXrefs().add(xref);
        }
        else{
            FeatureUpdates<T> updates = new FeatureUpdates<T>();
            updates.getAddedXrefs().add(xref);
            this.featureUpdates.put(interactor, updates);
        }
    }

    @Override
    public void onRemovedXref(T interactor, Xref xref) {
        // nothing to do
    }

    public Map<Feature, FeatureUpdates<T>> getFeatureUpdates() {
        return featureUpdates;
    }

    @Override
    public void onShortNameUpdate(T t, String s) {
        // nothing to do
    }

    @Override
    public void onFullNameUpdate(T t, String s) {
        // nothing to do
    }

    @Override
    public void onInterproUpdate(T t, String s) {
        // nothing to do
    }

    @Override
    public void onTypeUpdate(T t, CvTerm cvTerm) {
        try {
            if (t.getType() != null){
                t.setType(
                        context.getFeatureTypeSynchronizer().synchronize(t.getType(), true));
            }
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize feature type", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize feature type", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize feature type", e);
        }
    }

    @Override
    public void onAddedRange(T t, Range range) {
        if (this.featureUpdates.containsKey(t)){
            this.featureUpdates.get(t).getAddedRanges().add(range);
        }
        else{
            FeatureUpdates<T> updates = new FeatureUpdates<T>();
            updates.getAddedRanges().add(range);
            this.featureUpdates.put(t, updates);
        }
    }

    @Override
    public void onRemovedRange(T t, Range range) {
         // nothing to do
    }

    @Override
    public void onUpdatedRangePositions(T t, Range range, Position position, Position position2) {
        try {
            ((IntactPosition)range.getStart()).setStatus(
                    context.getRangeStatusSynchronizer().synchronize(range.getStart().getStatus(), true));
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize feature range start position", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize feature range start position", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize feature range start position", e);
        }
        try {
            ((IntactPosition)range.getEnd()).setStatus(
                    context.getRangeStatusSynchronizer().synchronize(range.getEnd().getStatus(), true));
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize feature range end position", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize feature range end position", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize feature range end position", e);
        }
    }

    @Override
    public void onRoleUpdate(T t, CvTerm cvTerm) {
        try {
            if (t.getRole() != null){
                t.setRole(
                        context.getTopicSynchronizer().synchronize(t.getRole(), true));
            }
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize feature role", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize feature role", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize feature role", e);
        }
    }

    @Override
    public void onAddedLinkedFeature(T t, T t2) {
        if (this.featureUpdates.containsKey(t)){
            this.featureUpdates.get(t).getAddedLinkedFeatures().add(t2);
        }
        else{
            FeatureUpdates<T> updates = new FeatureUpdates<T>();
            updates.getAddedLinkedFeatures().add(t2);
            this.featureUpdates.put(t, updates);
        }
    }

    @Override
    public void onRemovedLinkedFeature(T t, T t2) {
         // nothing to do
    }

    @Override
    public void onAddedAlias(T t, Alias alias) {
        if (this.featureUpdates.containsKey(t)){
            this.featureUpdates.get(t).getAddedAliases().add(alias);
        }
        else{
            FeatureUpdates<T> updates = new FeatureUpdates<T>();
            updates.getAddedAliases().add(alias);
            this.featureUpdates.put(t, updates);
        }
    }

    @Override
    public void onRemovedAlias(T t, Alias alias) {
        // nothing to do
    }

    @Override
    public void onAddedIdentifier(T t, Xref xref) {
        if (this.featureUpdates.containsKey(t)){
            this.featureUpdates.get(t).getAddedIdentifiers().add(xref);
        }
        else{
            FeatureUpdates<T> updates = new FeatureUpdates<T>();
            updates.getAddedIdentifiers().add(xref);
            this.featureUpdates.put(t, updates);
        }
    }

    @Override
    public void onRemovedIdentifier(T t, Xref xref) {
        // nothing to do
    }

    protected SynchronizerContext getContext() {
        return context;
    }

    protected IntactDbSynchronizer getDbSynchronizer() {
        return dbSynchronizer;
    }

    protected abstract XrefSynchronizer getXrefSynchronizer();
    protected abstract AnnotationSynchronizer getAnnotationSynchronizer();
    protected abstract AliasSynchronizer getAliasSynchronizer();
    protected abstract IntactDbSynchronizer getRangeSynchronizer();
}
