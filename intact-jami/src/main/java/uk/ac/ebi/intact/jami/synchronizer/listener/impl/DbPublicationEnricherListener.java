package uk.ac.ebi.intact.jami.synchronizer.listener.impl;

import org.apache.commons.collections.map.IdentityMap;
import psidev.psi.mi.jami.enricher.listener.EnrichmentStatus;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactMergerException;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.IntactPublicationSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.synchronizer.listener.IntactPublicationEnricherListener;
import uk.ac.ebi.intact.jami.synchronizer.listener.updates.PublicationUpdates;
import uk.ac.ebi.intact.jami.utils.IntactEnricherUtils;

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

public class DbPublicationEnricherListener implements IntactPublicationEnricherListener{
    private Map<Publication, PublicationUpdates> publicationUpdates;
    private SynchronizerContext context;
    private IntactPublicationSynchronizer dbSynchronizer;

    public DbPublicationEnricherListener(SynchronizerContext context, IntactPublicationSynchronizer dbSynchronizer) {
        if (context == null){
            throw new IllegalArgumentException("The listener needs a non null synchronizer context");
        }
        this.context = context;
        if (dbSynchronizer == null){
            throw new IllegalArgumentException("The listener needs a non null publication synchronizer");
        }
        this.dbSynchronizer = dbSynchronizer;
        this.publicationUpdates = new IdentityMap();
    }

    @Override
    public void onAddedAnnotation(Publication interactor, Annotation annotation) {
        if (this.publicationUpdates.containsKey(interactor)){
            this.publicationUpdates.get(interactor).getAddedAnnotations().add(annotation);
        }
        else{
            PublicationUpdates updates = new PublicationUpdates();
            updates.getAddedAnnotations().add(annotation);
            this.publicationUpdates.put(interactor, updates);
        }
    }

    @Override
    public void onRemovedAnnotation(Publication interactor, Annotation annotation) {
        // nothing to do
    }

    @Override
    public void onEnrichmentComplete(Publication object, EnrichmentStatus status, String message) {
        if (publicationUpdates.containsKey(object)){
            PublicationUpdates updates = publicationUpdates.get(object);
            try {
                if (!updates.getAddedXrefs().isEmpty()){

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedXrefs(),
                            getContext().getPublicationXrefSynchronizer());
                    object.getXrefs().removeAll(updates.getAddedXrefs());
                    object.getXrefs().addAll(synchronizedXrefs);
                }
                if (!updates.getAddedIdentifiers().isEmpty()){

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedIdentifiers(),
                            getContext().getPublicationXrefSynchronizer());
                    object.getIdentifiers().removeAll(updates.getAddedIdentifiers());
                    object.getIdentifiers().addAll(synchronizedXrefs);
                }
                if (!updates.getAddedAnnotations().isEmpty()){

                    List<Annotation> synchronizedAnnotations = IntactEnricherUtils.synchronizeAnnotationsToEnrich(updates.getAddedAnnotations(),
                            context.getPublicationAnnotationSynchronizer());
                    object.getAnnotations().removeAll(updates.getAddedAnnotations());
                    object.getAnnotations().addAll(synchronizedAnnotations);
                }
                if (!updates.getAddedExperiments().isEmpty()){

                    List<Experiment> synchronizedExperiments = IntactEnricherUtils.synchronizeExperimentsToEnrich(updates.getAddedExperiments(),
                            context.getExperimentSynchronizer());
                    object.getExperiments().removeAll(updates.getAddedExperiments());
                    for (Experiment f : synchronizedExperiments){
                        if (!object.getExperiments().contains(f)){
                            object.getExperiments().add(f);
                        }
                    }
                }
                if (!updates.getAddedOtherDbAnnotations().isEmpty()){

                    List<Annotation> synchronizedAnnotations = IntactEnricherUtils.synchronizeAnnotationsToEnrich(updates.getAddedOtherDbAnnotations(),
                            context.getPublicationAnnotationSynchronizer());
                    ((IntactPublication)object).getDbAnnotations().removeAll(updates.getAddedOtherDbAnnotations());
                    ((IntactPublication)object).getDbAnnotations().addAll(synchronizedAnnotations);
                }
                if (!updates.getAddedLifeCycleEvents().isEmpty()){

                    IntactPublication intactPublication = (IntactPublication)object;
                    List<LifeCycleEvent> synchronizedEvents = IntactEnricherUtils.synchronizeLifeCycleEventsToEnrich(updates.getAddedLifeCycleEvents(),
                            getContext().getPublicationLifecycleSynchronizer());
                    int i=0;
                    for (LifeCycleEvent evt : updates.getAddedLifeCycleEvents()){
                        int index = intactPublication.getLifecycleEvents().indexOf(evt);
                        intactPublication.getLifecycleEvents().remove(index);
                        intactPublication.getLifecycleEvents().add(index, synchronizedEvents.get(i));
                        i++;
                    }
                }

                publicationUpdates.remove(object);
            } catch (PersisterException e) {
                publicationUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged publication", e);
            } catch (FinderException e) {
                publicationUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged publication", e);
            } catch (SynchronizerException e) {
                publicationUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged publication", e);
            }
        }
    }

    @Override
    public void onEnrichmentError(Publication object, String message, Exception e) {
        if (publicationUpdates.containsKey(object)){
            PublicationUpdates updates = publicationUpdates.get(object);
            try {
                if (!updates.getAddedXrefs().isEmpty()){

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedXrefs(),
                            getContext().getPublicationXrefSynchronizer());
                    object.getXrefs().removeAll(updates.getAddedXrefs());
                    object.getXrefs().addAll(synchronizedXrefs);
                }
                if (!updates.getAddedIdentifiers().isEmpty()){

                    List<Xref> synchronizedXrefs = IntactEnricherUtils.synchronizeXrefsToEnrich(updates.getAddedIdentifiers(),
                            getContext().getPublicationXrefSynchronizer());
                    object.getIdentifiers().removeAll(updates.getAddedIdentifiers());
                    object.getIdentifiers().addAll(synchronizedXrefs);
                }
                if (!updates.getAddedAnnotations().isEmpty()){

                    List<Annotation> synchronizedAnnotations = IntactEnricherUtils.synchronizeAnnotationsToEnrich(updates.getAddedAnnotations(),
                            context.getPublicationAnnotationSynchronizer());
                    object.getAnnotations().removeAll(updates.getAddedAnnotations());
                    object.getAnnotations().addAll(synchronizedAnnotations);
                }
                if (!updates.getAddedExperiments().isEmpty()){

                    List<Experiment> synchronizedExperiments = IntactEnricherUtils.synchronizeExperimentsToEnrich(updates.getAddedExperiments(),
                            context.getExperimentSynchronizer());
                    object.getExperiments().removeAll(updates.getAddedExperiments());
                    for (Experiment f : synchronizedExperiments){
                        if (!object.getExperiments().contains(f)){
                            object.getExperiments().add(f);
                        }
                    }
                }
                if (!updates.getAddedOtherDbAnnotations().isEmpty()){

                    List<Annotation> synchronizedAnnotations = IntactEnricherUtils.synchronizeAnnotationsToEnrich(updates.getAddedOtherDbAnnotations(),
                            context.getPublicationAnnotationSynchronizer());
                    ((IntactPublication)object).getDbAnnotations().removeAll(updates.getAddedOtherDbAnnotations());
                    ((IntactPublication)object).getDbAnnotations().addAll(synchronizedAnnotations);
                }
                if (!updates.getAddedLifeCycleEvents().isEmpty()){

                    IntactPublication intactPublication = (IntactPublication)object;
                    List<LifeCycleEvent> synchronizedEvents = IntactEnricherUtils.synchronizeLifeCycleEventsToEnrich(updates.getAddedLifeCycleEvents(),
                            getContext().getPublicationLifecycleSynchronizer());
                    int i=0;
                    for (LifeCycleEvent evt : updates.getAddedLifeCycleEvents()){
                        int index = intactPublication.getLifecycleEvents().indexOf(evt);
                        intactPublication.getLifecycleEvents().remove(index);
                        intactPublication.getLifecycleEvents().add(index, synchronizedEvents.get(i));
                        i++;
                    }
                }

                publicationUpdates.remove(object);
            } catch (PersisterException e2) {
                publicationUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged publication", e2);
            } catch (FinderException e2) {
                publicationUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged publication", e2);
            } catch (SynchronizerException e2) {
                publicationUpdates.remove(object);
                throw new IntactMergerException("Cannot synchronize merged publication", e2);
            }
        }
    }
    @Override
    public void onAddedXref(Publication interactor, Xref xref) {
        if (this.publicationUpdates.containsKey(interactor)){
            this.publicationUpdates.get(interactor).getAddedXrefs().add(xref);
        }
        else{
            PublicationUpdates updates = new PublicationUpdates();
            updates.getAddedXrefs().add(xref);
            this.publicationUpdates.put(interactor, updates);
        }
    }

    @Override
    public void onRemovedXref(Publication interactor, Xref xref) {
        // nothing to do
    }

    public Map<Publication, PublicationUpdates> getPublicationUpdates() {
        return publicationUpdates;
    }

    @Override
    public void onAddedIdentifier(Publication t, Xref xref) {
        if (this.publicationUpdates.containsKey(t)){
            this.publicationUpdates.get(t).getAddedIdentifiers().add(xref);
        }
        else{
            PublicationUpdates updates = new PublicationUpdates();
            updates.getAddedIdentifiers().add(xref);
            this.publicationUpdates.put(t, updates);
        }
    }

    @Override
    public void onRemovedIdentifier(Publication t, Xref xref) {
        // nothing to do
    }

    protected SynchronizerContext getContext() {
        return context;
    }

    protected IntactPublicationSynchronizer getDbSynchronizer() {
        return dbSynchronizer;
    }

    @Override
    public void onAddedLifeCycleEvent(IntactPublication t, LifeCycleEvent added) {
        if (this.publicationUpdates.containsKey(t)){
            this.publicationUpdates.get(t).getAddedLifeCycleEvents().add(added);
        }
        else{
            PublicationUpdates updates = new PublicationUpdates();
            updates.getAddedLifeCycleEvents().add(added);
            this.publicationUpdates.put(t, updates);
        }
    }

    @Override
    public void onRemovedLifeCycleEvent(IntactPublication complex, LifeCycleEvent removed) {
         // nothing to do
    }

    @Override
    public void onStatusUpdate(IntactPublication complex, CvTerm oldStatus) {
        try {
            if (complex.getCvStatus() != null){
                complex.setCvStatus(
                        getContext().getLifecycleStatusSynchronizer().synchronize(complex.getCvStatus(), true));
            }
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize status", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize status", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize status", e);
        }
    }

    @Override
    public void onCurrentOwnerUpdate(IntactPublication complex, User oldUser) {
        try {
            if (complex.getCurrentOwner() != null){
                complex.setCurrentOwner(
                        getContext().getUserReadOnlySynchronizer().synchronize(complex.getCurrentOwner(), true));
            }
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize current owner", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize current owner", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize current owner", e);
        }
    }

    @Override
    public void onCurrentReviewerUpdate(IntactPublication complex, User oldUser) {
        try {
            if (complex.getCurrentReviewer() != null){
                complex.setCurrentReviewer(
                        getContext().getUserReadOnlySynchronizer().synchronize(complex.getCurrentReviewer(), true));
            }
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize current reviewer", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize current reviewer", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize current reviewer", e);
        }
    }

    @Override
    public void onAddedExperiment(Publication t, Experiment added) {
        if (this.publicationUpdates.containsKey(t)){
            this.publicationUpdates.get(t).getAddedExperiments().add(added);
        }
        else{
            PublicationUpdates updates = new PublicationUpdates();
            updates.getAddedExperiments().add(added);
            this.publicationUpdates.put(t, updates);
        }
    }

    @Override
    public void onRemovedExperiment(Publication complex, Experiment removed) {
        // nothing to do
    }

    @Override
    public void onPubmedIdUpdate(Publication publication, String s) {
        // nothing to do
    }

    @Override
    public void onDoiUpdate(Publication publication, String s) {
       // nothing to do
    }

    @Override
    public void onImexIdentifierUpdate(Publication publication, Xref xref) {
         // nothing to do
    }

    @Override
    public void onTitleUpdated(Publication publication, String s) {
         // nothing to do
    }

    @Override
    public void onJournalUpdated(Publication t, String s) {
        if (t.getJournal() != null && t instanceof IntactPublication){
            if (this.publicationUpdates.containsKey(t)){
                this.publicationUpdates.get(t).getAddedOtherDbAnnotations().addAll(
                        AnnotationUtils.collectAllAnnotationsHavingTopic(((IntactPublication) t).getDbAnnotations(),
                                Annotation.PUBLICATION_JOURNAL_MI, Annotation.PUBLICATION_JOURNAL));
            }
            else{
                PublicationUpdates updates = new PublicationUpdates();
                updates.getAddedOtherDbAnnotations().addAll(AnnotationUtils.collectAllAnnotationsHavingTopic(((IntactPublication) t).getDbAnnotations(),
                        Annotation.PUBLICATION_JOURNAL_MI, Annotation.PUBLICATION_JOURNAL));
                this.publicationUpdates.put(t, updates);
            }
        }
    }

    @Override
    public void onCurationDepthUpdate(Publication t, CurationDepth curationDepth) {
        if (t.getJournal() != null && t instanceof IntactPublication){
            if (this.publicationUpdates.containsKey(t)){
                this.publicationUpdates.get(t).getAddedOtherDbAnnotations().addAll(
                        AnnotationUtils.collectAllAnnotationsHavingTopic(((IntactPublication) t).getDbAnnotations(),
                                Annotation.CURATION_DEPTH_MI, Annotation.CURATION_DEPTH));
            }
            else{
                PublicationUpdates updates = new PublicationUpdates();
                updates.getAddedOtherDbAnnotations().addAll(AnnotationUtils.collectAllAnnotationsHavingTopic(((IntactPublication) t).getDbAnnotations(),
                        Annotation.CURATION_DEPTH_MI, Annotation.CURATION_DEPTH));
                this.publicationUpdates.put(t, updates);
            }
        }
    }

    @Override
    public void onPublicationDateUpdated(Publication t, Date date) {
        if (t.getJournal() != null && t instanceof IntactPublication){
            if (this.publicationUpdates.containsKey(t)){
                this.publicationUpdates.get(t).getAddedOtherDbAnnotations().addAll(
                        AnnotationUtils.collectAllAnnotationsHavingTopic(((IntactPublication) t).getDbAnnotations(),
                                Annotation.PUBLICATION_YEAR_MI, Annotation.PUBLICATION_YEAR));
            }
            else{
                PublicationUpdates updates = new PublicationUpdates();
                updates.getAddedOtherDbAnnotations().addAll(AnnotationUtils.collectAllAnnotationsHavingTopic(((IntactPublication) t).getDbAnnotations(),
                        Annotation.PUBLICATION_YEAR_MI, Annotation.PUBLICATION_YEAR));
                this.publicationUpdates.put(t, updates);
            }
        }
    }

    @Override
    public void onAuthorAdded(Publication t, String s) {
        if (t.getJournal() != null && t instanceof IntactPublication){
            if (this.publicationUpdates.containsKey(t)){
                this.publicationUpdates.get(t).getAddedOtherDbAnnotations().addAll(
                        AnnotationUtils.collectAllAnnotationsHavingTopic(((IntactPublication) t).getDbAnnotations(),
                                Annotation.AUTHOR_MI, Annotation.AUTHOR));
            }
            else{
                PublicationUpdates updates = new PublicationUpdates();
                updates.getAddedOtherDbAnnotations().addAll(AnnotationUtils.collectAllAnnotationsHavingTopic(((IntactPublication) t).getDbAnnotations(),
                        Annotation.AUTHOR_MI, Annotation.AUTHOR));
                this.publicationUpdates.put(t, updates);
            }
        }
    }

    @Override
    public void onAuthorRemoved(Publication publication, String s) {
        // nothing to do
    }

    @Override
    public void onReleaseDateUpdated(Publication publication, Date date) {
         // nothing to do
    }

    @Override
    public void onSourceUpdated(Publication publication, Source source) {
        try {
            if (publication.getSource() != null){
                publication.setSource(
                        getContext().getSourceSynchronizer().synchronize(publication.getSource(), true));
            }
        } catch (FinderException e) {
            throw new IntactMergerException("Cannot synchronize source", e);
        } catch (PersisterException e) {
            throw new IntactMergerException("Cannot synchronize source", e);
        } catch (SynchronizerException e) {
            throw new IntactMergerException("Cannot synchronize source", e);
        }
    }
}
