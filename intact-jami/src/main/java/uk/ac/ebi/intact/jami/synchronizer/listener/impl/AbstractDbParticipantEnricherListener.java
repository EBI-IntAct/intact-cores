package uk.ac.ebi.intact.jami.synchronizer.listener.impl;

import org.apache.commons.collections.map.IdentityMap;
import psidev.psi.mi.jami.enricher.listener.EnrichmentStatus;
import psidev.psi.mi.jami.enricher.listener.ParticipantEnricherListener;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactMergerException;
import uk.ac.ebi.intact.jami.model.extension.IntactStoichiometry;
import uk.ac.ebi.intact.jami.synchronizer.*;
import uk.ac.ebi.intact.jami.synchronizer.listener.updates.ParticipantUpdates;
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

public abstract class AbstractDbParticipantEnricherListener<T extends Participant, F extends Feature> implements ParticipantEnricherListener<T> {
    private Map<Participant, ParticipantUpdates<F>> participantUpdates;
    private SynchronizerContext context;
    private IntactDbSynchronizer dbSynchronizer;

    public AbstractDbParticipantEnricherListener(SynchronizerContext context, IntactDbSynchronizer dbSynchronizer) {
        if (context == null){
            throw new IllegalArgumentException("The listener needs a non null synchronizer context");
        }
        this.context = context;
        if (dbSynchronizer == null){
            throw new IllegalArgumentException("The listener needs a non null participant synchronizer");
        }
        this.dbSynchronizer = dbSynchronizer;
        this.participantUpdates = new IdentityMap();
    }

    @Override
    public void onAddedAnnotation(T interactor, Annotation annotation) {
        if (this.participantUpdates.containsKey(interactor)){
            this.participantUpdates.get(interactor).getAddedAnnotations().add(annotation);
        }
        else{
            ParticipantUpdates<F> updates = new ParticipantUpdates<F>();
            updates.getAddedAnnotations().add(annotation);
            this.participantUpdates.put(interactor, updates);
        }
    }

    @Override
    public void onRemovedAnnotation(T interactor, Annotation annotation) {
        // nothing to do
    }

    @Override
    public void onEnrichmentComplete(T object, EnrichmentStatus status, String message) {
        if (participantUpdates.containsKey(object)) {
            ParticipantUpdates<F> updates = participantUpdates.get(object);
            try {
                if (!updates.getAddedXrefs().isEmpty() && getXrefSynchronizer() != null) {

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedXrefs(),
                            getXrefSynchronizer());
                    object.getXrefs().removeAll(updates.getAddedXrefs());
                    for (Xref obj : synchronizedXrefs){
                        if (!object.getXrefs().contains(obj)){
                            object.getXrefs().add(obj);
                        }
                    }
                }
                if (!updates.getAddedAnnotations().isEmpty() && getAnnotationSynchronizer() != null) {

                    List<Annotation> synchronizedAnnotations = IntactEnricherUtils.synchronizeAnnotationsToEnrich(updates.getAddedAnnotations(),
                            getAnnotationSynchronizer());
                    object.getAnnotations().removeAll(updates.getAddedAnnotations());
                    for (Annotation obj : synchronizedAnnotations){
                        if (!object.getAnnotations().contains(obj)){
                            object.getAnnotations().add(obj);
                        }
                    }                }
                if (!updates.getAddedAliases().isEmpty() && getAliasSynchronizer() != null) {

                    List<Alias> synchronizedAliases = IntactEnricherUtils.synchronizeAliasesToEnrich(updates.getAddedAliases(),
                            getAliasSynchronizer());
                    object.getAliases().removeAll(updates.getAddedAliases());
                    for (Alias obj : synchronizedAliases){
                        if (!object.getAliases().contains(obj)){
                            object.getAliases().add(obj);
                        }
                    }
                }
                if (!updates.getAddedCausalRelationships().isEmpty() && getCausalRelationshipSynchronizer() != null) {

                    List<CausalRelationship> synchronizedRelationships = IntactEnricherUtils.synchronizeCausalRelationshipsToEnrich(updates.getAddedCausalRelationships(),
                            getCausalRelationshipSynchronizer());
                    object.getCausalRelationships().removeAll(updates.getAddedCausalRelationships());
                    for (CausalRelationship f : synchronizedRelationships){
                        if (!object.getCausalRelationships().contains(f)){
                            object.getCausalRelationships().add(f);
                        }
                    }
                }
                if (!updates.getAddedFeatures().isEmpty()) {

                    List<F> synchronizedFeatures = IntactEnricherUtils.synchronizeFeaturesToEnrich(updates.getAddedFeatures(),
                            getFeatureSynchronizer());
                    object.getFeatures().removeAll(updates.getAddedFeatures());
                    for (F f : synchronizedFeatures){
                        if (!object.getFeatures().contains(f)){
                            object.getFeatures().add(f);
                        }
                    }
                }
                processOtherUpdates(object, status, message);
                participantUpdates.remove(object);
            } catch (PersisterException e) {
                participantUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged participant", e);
            } catch (FinderException e) {
                participantUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged participant", e);
            } catch (SynchronizerException e) {
                participantUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged participant", e);
            }
        }
    }

    protected abstract void processOtherUpdates(T object, EnrichmentStatus status, String message) throws PersisterException, FinderException,
            SynchronizerException;

    @Override
    public void onEnrichmentError(T object, String message, Exception e) {
        if (participantUpdates.containsKey(object)) {
            ParticipantUpdates<F> updates = participantUpdates.get(object);
            try {
                if (!updates.getAddedXrefs().isEmpty() && getXrefSynchronizer() != null) {

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedXrefs(),
                            getXrefSynchronizer());
                    object.getXrefs().removeAll(updates.getAddedXrefs());
                    for (Xref obj : synchronizedXrefs){
                        if (!object.getXrefs().contains(obj)){
                            object.getXrefs().add(obj);
                        }
                    }
                }
                if (!updates.getAddedAnnotations().isEmpty() && getAnnotationSynchronizer() != null) {

                    List<Annotation> synchronizedAnnotations = IntactEnricherUtils.synchronizeAnnotationsToEnrich(updates.getAddedAnnotations(),
                            getAnnotationSynchronizer());
                    object.getAnnotations().removeAll(updates.getAddedAnnotations());
                    for (Annotation obj : synchronizedAnnotations){
                        if (!object.getAnnotations().contains(obj)){
                            object.getAnnotations().add(obj);
                        }
                    }                }
                if (!updates.getAddedAliases().isEmpty() && getAliasSynchronizer() != null) {

                    List<Alias> synchronizedAliases = IntactEnricherUtils.synchronizeAliasesToEnrich(updates.getAddedAliases(),
                            getAliasSynchronizer());
                    object.getAliases().removeAll(updates.getAddedAliases());
                    for (Alias obj : synchronizedAliases){
                        if (!object.getAliases().contains(obj)){
                            object.getAliases().add(obj);
                        }
                    }
                }
                if (!updates.getAddedCausalRelationships().isEmpty() && getCausalRelationshipSynchronizer() != null) {

                    List<CausalRelationship> synchronizedRelationships = IntactEnricherUtils.synchronizeCausalRelationshipsToEnrich(updates.getAddedCausalRelationships(),
                            getCausalRelationshipSynchronizer());
                    object.getCausalRelationships().removeAll(updates.getAddedCausalRelationships());
                    for (CausalRelationship f : synchronizedRelationships){
                        if (!object.getCausalRelationships().contains(f)){
                            object.getCausalRelationships().add(f);
                        }
                    }
                }
                if (!updates.getAddedFeatures().isEmpty()) {

                    List<F> synchronizedFeatures = IntactEnricherUtils.synchronizeFeaturesToEnrich(updates.getAddedFeatures(),
                            getFeatureSynchronizer());
                    object.getFeatures().removeAll(updates.getAddedFeatures());
                    for (F f : synchronizedFeatures){
                        if (!object.getFeatures().contains(f)){
                            object.getFeatures().add(f);
                        }
                    }
                }
                processOtherUpdates(object, message, e);
                participantUpdates.remove(object);
            } catch (PersisterException e2) {
                participantUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged participant", e2);
            } catch (FinderException e2) {
                participantUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged participant", e2);
            } catch (SynchronizerException e2) {
                participantUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged participant", e2);
            }
        }
    }

    protected abstract void processOtherUpdates(T object, String message, Exception e) throws PersisterException, FinderException,
            SynchronizerException;

    @Override
    public void onAddedXref(T interactor, Xref xref) {
        if (this.participantUpdates.containsKey(interactor)){
            this.participantUpdates.get(interactor).getAddedXrefs().add(xref);
        }
        else{
            ParticipantUpdates<F> updates = new ParticipantUpdates<F>();
            updates.getAddedXrefs().add(xref);
            this.participantUpdates.put(interactor, updates);
        }
    }

    @Override
    public void onRemovedXref(T interactor, Xref xref) {
        // nothing to do
    }

    public Map<Participant, ParticipantUpdates<F>> getParticipantUpdates() {
        return participantUpdates;
    }

    @Override
    public void onBiologicalRoleUpdate(T t, CvTerm cvTerm) {
        try {
            t.setBiologicalRole(
                    context.getBiologicalRoleSynchronizer().synchronize(t.getBiologicalRole(), true));
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize biological role", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize biological role", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize biological role", e);
        }
    }

    @Override
    public void onStoichiometryUpdate(T t, Stoichiometry stoichiometry) {
        Stoichiometry stc = t.getStoichiometry();
        if (stc != null && !(stc instanceof IntactStoichiometry)){
            t.setStoichiometry(new IntactStoichiometry(stc.getMinValue(), stc.getMaxValue()));
        }
    }

    @Override
    public void onAddedCausalRelationship(T t, CausalRelationship causalRelationship) {
        if (this.participantUpdates.containsKey(t)){
            this.participantUpdates.get(t).getAddedCausalRelationships().add(causalRelationship);
        }
        else{
            ParticipantUpdates<F> updates = new ParticipantUpdates<F>();
            updates.getAddedCausalRelationships().add(causalRelationship);
            this.participantUpdates.put(t, updates);
        }
    }

    @Override
    public void onRemovedCausalRelationship(T t, CausalRelationship causalRelationship) {
        // nothing to do
    }

    @Override
    public void onAddedFeature(T t, Feature feature) {
        if (this.participantUpdates.containsKey(t)){
            this.participantUpdates.get(t).getAddedFeatures().add((F)feature);
        }
        else{
            ParticipantUpdates<F> updates = new ParticipantUpdates<F>();
            updates.getAddedFeatures().add((F)feature);
            this.participantUpdates.put(t, updates);
        }
    }

    @Override
    public void onRemovedFeature(T t, Feature feature) {
        // nothing to do
    }

    @Override
    public void onInteractorUpdate(Entity t, Interactor interactor) {
        try {
            t.setInteractor(
                    context.getInteractorSynchronizer().synchronize(t.getInteractor(), true));
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize interactor", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize interactor", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize interactor", e);
        }
    }

    @Override
    public void onAddedAlias(T t, Alias alias) {
        if (this.participantUpdates.containsKey(t)){
            this.participantUpdates.get(t).getAddedAliases().add(alias);
        }
        else{
            ParticipantUpdates<F> updates = new ParticipantUpdates<F>();
            updates.getAddedAliases().add(alias);
            this.participantUpdates.put(t, updates);
        }
    }

    @Override
    public void onRemovedAlias(T t, Alias alias) {
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
    protected abstract IntactDbSynchronizer getCausalRelationshipSynchronizer();
    protected abstract IntactDbSynchronizer getFeatureSynchronizer();
}
