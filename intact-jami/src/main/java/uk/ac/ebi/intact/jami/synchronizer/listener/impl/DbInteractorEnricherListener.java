package uk.ac.ebi.intact.jami.synchronizer.listener.impl;

import org.apache.commons.collections.map.IdentityMap;
import psidev.psi.mi.jami.enricher.listener.EnrichmentStatus;
import psidev.psi.mi.jami.enricher.listener.InteractorEnricherListener;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactMergerException;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractor;
import uk.ac.ebi.intact.jami.synchronizer.*;
import uk.ac.ebi.intact.jami.synchronizer.listener.updates.InteractorUpdates;
import uk.ac.ebi.intact.jami.utils.IntactEnricherUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Listener that will synchronize updates done to an existing object in the database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public class DbInteractorEnricherListener<T extends Interactor> implements InteractorEnricherListener<T>{
    private Map<Interactor, InteractorUpdates> interactorUpdates;
    private SynchronizerContext context;
    private InteractorSynchronizer dbSynchronizer;

    public DbInteractorEnricherListener(SynchronizerContext context, InteractorSynchronizer dbSynchronizer) {
        if (context == null){
            throw new IllegalArgumentException("The listener needs a non null synchronizer context");
        }
        this.context = context;
        if (dbSynchronizer == null){
            throw new IllegalArgumentException("The listener needs a non null interactor synchronizer");
        }
        this.dbSynchronizer = dbSynchronizer;
        this.interactorUpdates = new IdentityMap();
    }

    @Override
    public void onAddedAnnotation(T interactor, Annotation annotation) {
        if (this.interactorUpdates.containsKey(interactor)){
            this.interactorUpdates.get(interactor).getAddedAnnotations().add(annotation);
        }
        else{
            InteractorUpdates updates = new InteractorUpdates();
            updates.getAddedAnnotations().add(annotation);
            this.interactorUpdates.put(interactor, updates);
        }
    }

    @Override
    public void onRemovedAnnotation(T interactor, Annotation annotation) {
        // nothing to do
    }

    protected XrefSynchronizer getXrefSynchronizer(){
        return this.context.getInteractorXrefSynchronizer();
    }


    @Override
    public void onEnrichmentComplete(T object, EnrichmentStatus status, String message) {
        if (interactorUpdates.containsKey(object)){
            InteractorUpdates updates = interactorUpdates.get(object);
            try {
                if (!updates.getAddedXrefs().isEmpty()){

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedXrefs(),
                            getXrefSynchronizer());
                    object.getXrefs().removeAll(updates.getAddedXrefs());
                    object.getXrefs().addAll(synchronizedXrefs);
                }
                if (!updates.getAddedIdentifiers().isEmpty()){

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedIdentifiers(),
                            getXrefSynchronizer());
                    object.getIdentifiers().removeAll(updates.getAddedIdentifiers());
                    object.getIdentifiers().addAll(synchronizedXrefs);
                }
                if (!updates.getAddedAnnotations().isEmpty()){

                    List<Annotation> synchronizedAnnotations = IntactEnricherUtils.synchronizeAnnotationsToEnrich(updates.getAddedAnnotations(),
                            context.getInteractorAnnotationSynchronizer());
                    object.getAnnotations().removeAll(updates.getAddedAnnotations());
                    object.getAnnotations().addAll(synchronizedAnnotations);
                }
                if (!updates.getAddedAliases().isEmpty()){

                    List<Alias> synchronizedAliases = IntactEnricherUtils.synchronizeAliasesToEnrich(updates.getAddedAliases(),
                            context.getInteractorAliasSynchronizer());
                    object.getAliases().removeAll(updates.getAddedAliases());
                    object.getAliases().addAll(synchronizedAliases);
                }
                if (!updates.getAddedChecksums().isEmpty() && object instanceof IntactInteractor){
                    Collection<Annotation> createdAnnots = new ArrayList<Annotation>(updates.getAddedChecksums().size());
                    for (Checksum c : updates.getAddedChecksums()){
                        createdAnnots.addAll(AnnotationUtils.
                                collectAllAnnotationsHavingTopic(((IntactInteractor) object).getDbAnnotations(), c.getMethod().getMIIdentifier(), c.getMethod().getShortName()));
                    }
                    List<Annotation> synchronizedAnnotations = IntactEnricherUtils.synchronizeAnnotationsToEnrich(createdAnnots,
                            context.getInteractorAnnotationSynchronizer());
                    ((IntactInteractor) object).getDbAnnotations().removeAll(createdAnnots);
                    ((IntactInteractor) object).getDbAnnotations().addAll(synchronizedAnnotations);
                }

                processOtherUpdates(object, status, message);
                interactorUpdates.remove(object);
            } catch (PersisterException e) {
                interactorUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged interactor", e);
            } catch (FinderException e) {
                interactorUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged interactor", e);
            } catch (SynchronizerException e) {
                interactorUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged interactor", e);
            }
        }
    }

    protected void processOtherUpdates(T object, EnrichmentStatus status, String message) throws PersisterException, FinderException,
            SynchronizerException{
        // nothing to do
    }

    @Override
    public void onEnrichmentError(T object, String message, Exception e) {
        if (interactorUpdates.containsKey(object)){
            InteractorUpdates updates = interactorUpdates.get(object);
            try {
                if (!updates.getAddedXrefs().isEmpty()){

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedXrefs(),
                            getXrefSynchronizer());
                    object.getXrefs().removeAll(updates.getAddedXrefs());
                    object.getXrefs().addAll(synchronizedXrefs);
                }
                if (!updates.getAddedIdentifiers().isEmpty()){

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedIdentifiers(),
                            getXrefSynchronizer());
                    object.getIdentifiers().removeAll(updates.getAddedIdentifiers());
                    object.getIdentifiers().addAll(synchronizedXrefs);
                }
                if (!updates.getAddedAnnotations().isEmpty()){

                    List<Annotation> synchronizedAnnotations = IntactEnricherUtils.synchronizeAnnotationsToEnrich(updates.getAddedAnnotations(),
                            context.getInteractorAnnotationSynchronizer());
                    object.getAnnotations().removeAll(updates.getAddedAnnotations());
                    object.getAnnotations().addAll(synchronizedAnnotations);
                }
                if (!updates.getAddedAliases().isEmpty()){

                    List<Alias> synchronizedAliases = IntactEnricherUtils.synchronizeAliasesToEnrich(updates.getAddedAliases(),
                            context.getInteractorAliasSynchronizer());
                    object.getAliases().removeAll(updates.getAddedAliases());
                    object.getAliases().addAll(synchronizedAliases);
                }
                if (!updates.getAddedChecksums().isEmpty() && object instanceof IntactInteractor){
                    Collection<Annotation> createdAnnots = new ArrayList<Annotation>(updates.getAddedChecksums().size());
                    for (Checksum c : updates.getAddedChecksums()){
                        createdAnnots.addAll(AnnotationUtils.
                                collectAllAnnotationsHavingTopic(((IntactInteractor) object).getDbAnnotations(), c.getMethod().getMIIdentifier(), c.getMethod().getShortName()));
                    }
                    List<Annotation> synchronizedAnnotations = IntactEnricherUtils.synchronizeAnnotationsToEnrich(createdAnnots,
                            context.getInteractorAnnotationSynchronizer());
                    ((IntactInteractor) object).getDbAnnotations().removeAll(createdAnnots);
                    ((IntactInteractor) object).getDbAnnotations().addAll(synchronizedAnnotations);
                }
                processOtherUpdates(object, message, e);
                interactorUpdates.remove(object);
            } catch (PersisterException e2) {
                interactorUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged interactor", e2);
            } catch (FinderException e2) {
                interactorUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged interactor", e2);
            } catch (SynchronizerException e2) {
                interactorUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged interactor", e2);
            }
        }
    }

    protected void processOtherUpdates(T object, String message, Exception e) throws PersisterException, FinderException,
            SynchronizerException{
         // nothing to do
    }

    @Override
    public void onAddedXref(T interactor, Xref xref) {
        if (this.interactorUpdates.containsKey(interactor)){
            this.interactorUpdates.get(interactor).getAddedXrefs().add(xref);
        }
        else{
            InteractorUpdates updates = new InteractorUpdates();
            updates.getAddedXrefs().add(xref);
            this.interactorUpdates.put(interactor, updates);
        }
    }

    @Override
    public void onRemovedXref(T interactor, Xref xref) {
        // nothing to do
    }

    public Map<Interactor, InteractorUpdates> getInteractorUpdates() {
        return interactorUpdates;
    }

    @Override
    public void onShortNameUpdate(T t, String s) {
        try {
            this.dbSynchronizer.prepareAndSynchronizeShortLabel((IntactInteractor)t);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize interactor label", e);
        }
    }

    @Override
    public void onFullNameUpdate(T t, String s) {
        // nothing to do
    }

    @Override
    public void onOrganismUpdate(T t, Organism organism) {
        try {
            if (t.getOrganism() != null){
                t.setOrganism(
                        context.getOrganismSynchronizer().synchronize(t.getOrganism(), true));
            }
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize organism", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize organism", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize organism", e);
        }
    }

    @Override
    public void onInteractorTypeUpdate(T t, CvTerm cvTerm) {
        try {

            t.setInteractorType(
                    context.getInteractorTypeSynchronizer().synchronize(t.getInteractorType(), true));
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize interactor type", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize interactor type", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize interactor type", e);
        }
    }

    @Override
    public void onAddedAlias(T t, Alias alias) {
        if (this.interactorUpdates.containsKey(t)){
            this.interactorUpdates.get(t).getAddedAliases().add(alias);
        }
        else{
            InteractorUpdates updates = new InteractorUpdates();
            updates.getAddedAliases().add(alias);
            this.interactorUpdates.put(t, updates);
        }
    }

    @Override
    public void onRemovedAlias(T t, Alias alias) {
        // nothing to do
    }

    @Override
    public void onAddedChecksum(T t, Checksum checksum) {
        if (this.interactorUpdates.containsKey(t)){
            this.interactorUpdates.get(t).getAddedChecksums().add(checksum);
        }
        else{
            InteractorUpdates updates = new InteractorUpdates();
            updates.getAddedChecksums().add(checksum);
            this.interactorUpdates.put(t, updates);
        }
    }

    @Override
    public void onRemovedChecksum(T t, Checksum checksum) {
        // nothing to do
    }

    @Override
    public void onAddedIdentifier(T t, Xref xref) {
        if (this.interactorUpdates.containsKey(t)){
            this.interactorUpdates.get(t).getAddedIdentifiers().add(xref);
        }
        else{
            InteractorUpdates updates = new InteractorUpdates();
            updates.getAddedIdentifiers().add(xref);
            this.interactorUpdates.put(t, updates);
        }
    }

    @Override
    public void onRemovedIdentifier(T t, Xref xref) {
        // nothing to do
    }

    protected SynchronizerContext getContext() {
        return context;
    }

    protected InteractorSynchronizer getDbSynchronizer() {
        return dbSynchronizer;
    }
}
