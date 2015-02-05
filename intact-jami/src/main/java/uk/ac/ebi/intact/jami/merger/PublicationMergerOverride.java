package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.bridges.fetcher.PublicationFetcher;
import psidev.psi.mi.jami.enricher.CuratedPublicationEnricher;
import psidev.psi.mi.jami.enricher.PublicationEnricher;
import psidev.psi.mi.jami.enricher.SourceEnricher;
import psidev.psi.mi.jami.enricher.exception.EnricherException;
import psidev.psi.mi.jami.enricher.impl.full.FullPublicationUpdater;
import psidev.psi.mi.jami.enricher.listener.PublicationEnricherListener;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Experiment;
import psidev.psi.mi.jami.model.Publication;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.synchronizer.impl.PublicationSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.listener.IntactPublicationEnricherListener;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Publication merger based on the jami publication enricher.
 * It will only add missing info, it does not override anything
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class PublicationMergerOverride extends IntactDbMergerOverride<Publication, IntactPublication> implements CuratedPublicationEnricher {

    public PublicationMergerOverride(PublicationSynchronizer intactSynchronizer){
        super(IntactPublication.class, new FullPublicationUpdater(intactSynchronizer));
    }

    @Override
    protected PublicationEnricher getBasicEnricher() {
        return (PublicationEnricher) super.getBasicEnricher();
    }

    public PublicationFetcher getPublicationFetcher() {
        return getBasicEnricher().getPublicationFetcher();
    }

    public SourceEnricher getSourceEnricher() {
        return null;
    }

    @Override
    public void setSourceEnricher(SourceEnricher enricher) {

    }

    public PublicationEnricherListener getPublicationEnricherListener() {
        return getBasicEnricher().getPublicationEnricherListener();
    }

    @Override
    public void setPublicationEnricherListener(PublicationEnricherListener listener) {
        getBasicEnricher().setPublicationEnricherListener(listener);
    }

    @Override
    protected void mergeOtherProperties(IntactPublication obj1, IntactPublication obj2) {
        super.mergeOtherProperties(obj1, obj2);
        IntactPublication mergedPub = obj2;

        // merge curator
        if (mergedPub.getCurrentOwner() != obj1.getCurrentOwner()){
            User old = mergedPub.getCurrentOwner();
            mergedPub.setCurrentOwner(obj1.getCurrentOwner());
            if (getPublicationEnricherListener() instanceof IntactPublicationEnricherListener){
                ((IntactPublicationEnricherListener) getPublicationEnricherListener()).onCurrentOwnerUpdate(mergedPub, old);
            }
        }
        // merge reviewer
        if (mergedPub.getCurrentReviewer() != obj1.getCurrentReviewer()){
            User old = mergedPub.getCurrentReviewer();
            mergedPub.setCurrentReviewer(obj1.getCurrentReviewer());
            if (getPublicationEnricherListener() instanceof IntactPublicationEnricherListener){
                ((IntactPublicationEnricherListener) getPublicationEnricherListener()).onCurrentReviewerUpdate(mergedPub, old);
            }
        }
        // merge status
        if (mergedPub.getCvStatus() != obj1.getCvStatus()){
            CvTerm old = mergedPub.getCvStatus();
            mergedPub.setCvStatus(obj1.getCvStatus());
            if (getPublicationEnricherListener() instanceof IntactPublicationEnricherListener){
                ((IntactPublicationEnricherListener) getPublicationEnricherListener()).onStatusUpdate(mergedPub, old);
            }
        }
        // merge lifecycle
        if (obj1.areLifeCycleEventsInitialized()){
            mergeLifeCycleEvents(mergedPub, mergedPub.getLifecycleEvents(), obj1.getLifecycleEvents());
        }
        //merge experiments
        if (obj1.areExperimentsInitialized()){
            mergeExperiments(mergedPub, mergedPub.getExperiments(), obj1.getExperiments());
        }
    }

    private void mergeExperiments(Publication mergedPub, Collection<Experiment> toEnrichExperiments, Collection<Experiment> sourceExperiments) {

        Iterator<Experiment> experimentIterator = toEnrichExperiments.iterator();
        while(experimentIterator.hasNext()){
            Experiment experiment = experimentIterator.next();
            boolean containsExperiment = false;
            for (Experiment experiment2 : sourceExperiments){
                if (experiment == experiment2){
                    containsExperiment = true;
                    break;
                }
            }
            // remove events not in second list
            if (!containsExperiment){
                experimentIterator.remove();
                if (getPublicationEnricherListener() instanceof IntactPublicationEnricherListener){
                    ((IntactPublicationEnricherListener) getPublicationEnricherListener()).onRemovedExperiment(mergedPub, experiment);
                }
            }
        }
        experimentIterator = sourceExperiments.iterator();
        while(experimentIterator.hasNext()){
            Experiment experiment = experimentIterator.next();
            boolean containsExperiment = false;
            for (Experiment experiment2 : toEnrichExperiments){
                // identical terms
                if (experiment == experiment2){
                    containsExperiment = true;
                    break;
                }
            }
            // add missing xref not in second list
            if (!containsExperiment){
                mergedPub.addExperiment(experiment);
                if (getPublicationEnricherListener() instanceof IntactPublicationEnricherListener){
                    ((IntactPublicationEnricherListener) getPublicationEnricherListener()).onAddedExperiment(mergedPub, experiment);
                }
            }
        }
    }

    /**
     * Takes two lists of lifecycle events and produces a list of those to add and those to remove.
     *
     * It will add all events from fetchedTerms that are not in collection to enrich. It will remove events in toEnrichEvents that are not in
     * sourceEvents
     *
     */
    private void mergeLifeCycleEvents(IntactPublication merged, List<LifeCycleEvent> toEnrichEvents, List<LifeCycleEvent> sourceEvents){


        Iterator<LifeCycleEvent> eventIterator = toEnrichEvents.iterator();
        while(eventIterator.hasNext()){
            LifeCycleEvent event = eventIterator.next();
            boolean containsEvent = false;
            for (LifeCycleEvent event2 : sourceEvents){
                if (event.equals(event2)){
                    containsEvent = true;
                    break;
                }
            }
            // remove events not in second list
            if (!containsEvent){
                eventIterator.remove();
                if (getPublicationEnricherListener() instanceof IntactPublicationEnricherListener){
                    ((IntactPublicationEnricherListener) getPublicationEnricherListener()).onRemovedLifeCycleEvent(merged, event);
                }
            }
        }

        eventIterator = sourceEvents.iterator();
        int index = 0;
        while(eventIterator.hasNext()){
            LifeCycleEvent event = eventIterator.next();
            boolean containsEvent = false;
            for (LifeCycleEvent event2 : toEnrichEvents){
                // identical terms
                if (event.equals(event2)){
                    containsEvent = true;
                    break;
                }
            }
            // add missing xref not in second list
            if (!containsEvent){
                toEnrichEvents.add(index, event);
                if (getPublicationEnricherListener() instanceof IntactPublicationEnricherListener){
                    ((IntactPublicationEnricherListener) getPublicationEnricherListener()).onAddedLifeCycleEvent(merged, event);
                }
            }
            index++;
        }
    }

    @Override
    protected void enrichBasicProperties(Publication objectToEnrich, Publication objectSource) throws EnricherException {
        super.enrichBasicProperties(objectToEnrich, objectSource);
        mergeExperiments(objectToEnrich, objectToEnrich.getExperiments(), objectSource.getExperiments());
    }
}
