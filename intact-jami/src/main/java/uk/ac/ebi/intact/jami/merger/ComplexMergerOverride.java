package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.bridges.fetcher.InteractorFetcher;
import psidev.psi.mi.jami.enricher.*;
import psidev.psi.mi.jami.enricher.impl.full.FullComplexUpdater;
import psidev.psi.mi.jami.enricher.listener.InteractionEnricherListener;
import psidev.psi.mi.jami.enricher.listener.InteractorEnricherListener;
import psidev.psi.mi.jami.model.Complex;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;

import java.util.Iterator;
import java.util.List;

/**
 * Complex merger based on the jami interaction evidence enricher.
 * It will override properties loaded from the database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30/01/14</pre>
 */

public class ComplexMergerOverride extends InteractorBaseMergerOverride<Complex,IntactComplex> implements ComplexEnricher {

    public ComplexMergerOverride(){
        super(new FullComplexUpdater());
    }

    protected ComplexMergerOverride(ComplexEnricher interactorEnricher){
        super(interactorEnricher);
    }

    @Override
    protected ComplexEnricher getBasicEnricher() {
        return (ComplexEnricher)super.getBasicEnricher();
    }

    public ParticipantEnricher getParticipantEnricher() {
        return null;
    }

    public InteractorFetcher<Complex> getInteractorFetcher() {
        return null;
    }

    public InteractorEnricherListener<Complex> getListener() {
        return null;
    }

    public CvTermEnricher<CvTerm> getCvTermEnricher() {
        return null;
    }

    public OrganismEnricher getOrganismEnricher() {
        return null;
    }

    @Override
    public void setListener(InteractorEnricherListener<Complex> listener) {

    }

    @Override
    public void setCvTermEnricher(CvTermEnricher<CvTerm> enricher) {

    }

    @Override
    public void setOrganismEnricher(OrganismEnricher enricher) {

    }

    public InteractionEnricherListener<Complex> getInteractionEnricherListener() {
        return null;
    }

    @Override
    public void setParticipantEnricher(ParticipantEnricher enricher) {

    }

    @Override
    public void setInteractionEnricherListener(InteractionEnricherListener<Complex> listener) {

    }

    public SourceEnricher getSourceEnricher() {
        return null;
    }

    @Override
    public void setSourceEnricher(SourceEnricher enricher) {

    }

    @Override
    public IntactComplex merge(IntactComplex obj1, IntactComplex obj2) {
        // obj2 is mergedComplex
        IntactComplex mergedComplex = super.merge(obj1, obj2);

        // merge status
        if (mergedComplex.getCvStatus() == null && obj1.getCvStatus() != null){
            mergedComplex.setCvStatus(obj1.getCvStatus());
        }
        // merge curator
        if (mergedComplex.getCurrentOwner() == null && obj1.getCurrentOwner() != null){
            mergedComplex.setCurrentOwner(obj1.getCurrentOwner());
        }
        // merge reviewer
        if (mergedComplex.getCurrentReviewer() == null && obj1.getCurrentReviewer() != null){
            mergedComplex.setCurrentReviewer(obj1.getCurrentReviewer());
        }
        // merge lifecycle
        if (obj1.areLifeCycleEventsInitialized()){
            mergeLifeCycleEvents(mergedComplex.getLifecycleEvents(), obj1.getLifecycleEvents());
        }

        return mergedComplex;
    }

    private void mergeLifeCycleEvents(List<LifeCycleEvent> toEnrichEvents, List<LifeCycleEvent> sourceEvents){

        Iterator<LifeCycleEvent> eventIterator = toEnrichEvents.iterator();
        while(eventIterator.hasNext()){
            LifeCycleEvent event = eventIterator.next();
            boolean containsEvent = false;
            for (LifeCycleEvent event2 : sourceEvents){
                if (event == event2){
                    containsEvent = true;
                    break;
                }
            }
            // remove events not in second list
            if (!containsEvent){
                eventIterator.remove();
            }
        }

        eventIterator = sourceEvents.iterator();
        int index = 0;
        while(eventIterator.hasNext()){
            LifeCycleEvent event = eventIterator.next();
            boolean containsEvent = false;
            for (LifeCycleEvent event2 : toEnrichEvents){
                // identical terms
                if (event == event2){
                    containsEvent = true;
                    break;
                }
            }
            // add missing xref not in second list
            if (!containsEvent){
                toEnrichEvents.add(index, event);
            }
            index++;
        }
    }
}

