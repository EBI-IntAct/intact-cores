package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.bridges.fetcher.InteractorFetcher;
import psidev.psi.mi.jami.enricher.*;
import psidev.psi.mi.jami.enricher.impl.full.FullComplexEnricher;
import psidev.psi.mi.jami.enricher.listener.InteractionEnricherListener;
import psidev.psi.mi.jami.enricher.listener.InteractorEnricherListener;
import psidev.psi.mi.jami.model.Complex;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;
import uk.ac.ebi.intact.jami.synchronizer.listener.IntactComplexEnricherListener;

import java.util.Iterator;
import java.util.List;

/**
 * Complex merger based on the jami interaction evidence enricher.
 * It will only add missing info, it does not override anything
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30/01/14</pre>
 */

public class ComplexMergerEnrichOnly extends InteractorBaseMergerEnrichOnly<Complex,IntactComplex> implements ComplexEnricher {

    public ComplexMergerEnrichOnly(){
        super(new FullComplexEnricher());
    }

    protected ComplexMergerEnrichOnly(ComplexEnricher interactorEnricher){
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
        return getBasicEnricher().getListener();
    }

    public CvTermEnricher<CvTerm> getCvTermEnricher() {
        return null;
    }

    public OrganismEnricher getOrganismEnricher() {
        return null;
    }

    @Override
    public void setListener(InteractorEnricherListener<Complex> listener) {
        getBasicEnricher().setListener(listener);
    }

    @Override
    public void setCvTermEnricher(CvTermEnricher<CvTerm> enricher) {

    }

    @Override
    public void setOrganismEnricher(OrganismEnricher enricher) {

    }

    public InteractionEnricherListener<Complex> getInteractionEnricherListener() {
        return getBasicEnricher().getInteractionEnricherListener();
    }

    @Override
    public void setParticipantEnricher(ParticipantEnricher enricher) {

    }

    @Override
    public void setInteractionEnricherListener(InteractionEnricherListener<Complex> listener) {
         getBasicEnricher().setInteractionEnricherListener(listener);
    }

    public SourceEnricher getSourceEnricher() {
        return null;
    }

    @Override
    public void setSourceEnricher(SourceEnricher enricher) {

    }

    @Override
    protected void mergeOtherProperties(IntactComplex obj1, IntactComplex obj2) {
        super.mergeOtherProperties(obj1, obj2);
        // obj2 is mergedComplex
        IntactComplex mergedComplex = obj2;

        // merge status
        if (mergedComplex.getCvStatus() == null && obj1.getCvStatus() != null){
            mergedComplex.setCvStatus(obj1.getCvStatus());
            if (getListener() instanceof IntactComplexEnricherListener){
                ((IntactComplexEnricherListener) getListener()).onStatusUpdate(mergedComplex, null);
            }
        }
        // merge curator
        if (mergedComplex.getCurrentOwner() == null && obj1.getCurrentOwner() != null){
            mergedComplex.setCurrentOwner(obj1.getCurrentOwner());
            if (getListener() instanceof IntactComplexEnricherListener){
                ((IntactComplexEnricherListener) getListener()).onCurrentOwnerUpdate(mergedComplex, null);
            }
        }
        // merge reviewer
        if (mergedComplex.getCurrentReviewer() == null && obj1.getCurrentReviewer() != null){
            mergedComplex.setCurrentReviewer(obj1.getCurrentReviewer());
            if (getListener() instanceof IntactComplexEnricherListener){
                ((IntactComplexEnricherListener) getListener()).onCurrentReviewerUpdate(mergedComplex, null);
            }
        }
        // merge lifecycle
        if (obj1.areLifeCycleEventsInitialized()){
            mergeLifeCycleEvents(mergedComplex, mergedComplex.getLifecycleEvents(), obj1.getLifecycleEvents());
        }
    }

    private void mergeLifeCycleEvents(IntactComplex mergedComplex, List<LifeCycleEvent> toEnrichEvents, List<LifeCycleEvent> sourceEvents){

        Iterator<LifeCycleEvent> eventIterator = sourceEvents.iterator();
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
                if (getListener() instanceof IntactComplexEnricherListener){
                    ((IntactComplexEnricherListener) getListener()).onAddedLifeCycleEvent(mergedComplex, null);
                }
            }
            index++;
        }
    }
}

