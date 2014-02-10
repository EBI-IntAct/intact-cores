package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.bridges.fetcher.InteractorFetcher;
import psidev.psi.mi.jami.enricher.*;
import psidev.psi.mi.jami.enricher.impl.FullComplexEnricher;
import psidev.psi.mi.jami.enricher.listener.InteractionEnricherListener;
import psidev.psi.mi.jami.enricher.listener.InteractorEnricherListener;
import psidev.psi.mi.jami.model.Complex;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ModelledFeature;
import psidev.psi.mi.jami.model.ModelledParticipant;
import uk.ac.ebi.intact.jami.model.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;

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

public class IntactComplexMergerEnrichOnly extends IntactInteractorBaseMergerEnrichOnly<Complex,IntactComplex> implements ComplexEnricher {

    public IntactComplexMergerEnrichOnly(){
        super(new FullComplexEnricher());
    }

    protected IntactComplexMergerEnrichOnly(ComplexEnricher interactorEnricher){
        super(interactorEnricher);
    }

    @Override
    protected ComplexEnricher getBasicEnricher() {
        return (ComplexEnricher)super.getBasicEnricher();
    }


    public ParticipantEnricher<ModelledParticipant, ModelledFeature> getParticipantEnricher() {
        return getBasicEnricher().getParticipantEnricher();
    }

    public void setParticipantEnricher(ParticipantEnricher<ModelledParticipant, ModelledFeature> participantEnricher) {
        getBasicEnricher().setParticipantEnricher(participantEnricher);
    }

    public InteractorFetcher<Complex> getInteractorFetcher() {
        return getBasicEnricher().getInteractorFetcher();
    }

    public void setListener(InteractorEnricherListener<Complex> listener) {
        getBasicEnricher().setListener(listener);
    }

    public InteractorEnricherListener<Complex> getListener() {
        return getBasicEnricher().getListener();
    }

    public void setCvTermEnricher(CvTermEnricher cvTermEnricher) {
        getBasicEnricher().setCvTermEnricher(cvTermEnricher);
    }

    public CvTermEnricher<CvTerm> getCvTermEnricher() {
        return getBasicEnricher().getCvTermEnricher();
    }

    public void setOrganismEnricher(OrganismEnricher organismEnricher) {
        getBasicEnricher().setOrganismEnricher(organismEnricher);
    }

    public OrganismEnricher getOrganismEnricher() {
        return getBasicEnricher().getOrganismEnricher();
    }

    public InteractionEnricherListener<Complex> getInteractionEnricherListener() {
        return getBasicEnricher().getInteractionEnricherListener();
    }

    public void setInteractionEnricherListener(InteractionEnricherListener<Complex> listener) {
        getBasicEnricher().setInteractionEnricherListener(listener);
    }

    public SourceEnricher getSourceEnricher() {
        return getBasicEnricher().getSourceEnricher();
    }

    public void setSourceEnricher(SourceEnricher sourceEnricher) {
        getBasicEnricher().setSourceEnricher(sourceEnricher);
    }

    @Override
    public IntactComplex merge(IntactComplex obj1, IntactComplex obj2) {
        // obj2 is mergedComplex
        IntactComplex mergedComplex = super.merge(obj1, obj2);

        // merge status
        if (mergedComplex.getStatus() == null && obj1.getStatus() != null){
            mergedComplex.setStatus(obj1.getStatus());
        }
        // merge lifecycle
        if (obj1.areLifeCycleEventsInitialized()){
            mergeLifeCycleEvents(mergedComplex.getLifecycleEvents(), obj1.getLifecycleEvents());
        }

        return mergedComplex;
    }

    private void mergeLifeCycleEvents(List<LifeCycleEvent> toEnrichEvents, List<LifeCycleEvent> sourceEvents){

        Iterator<LifeCycleEvent> eventIterator = sourceEvents.iterator();
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

