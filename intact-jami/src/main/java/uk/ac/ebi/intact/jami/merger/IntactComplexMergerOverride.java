package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.bridges.fetcher.InteractorFetcher;
import psidev.psi.mi.jami.enricher.*;
import psidev.psi.mi.jami.enricher.impl.FullComplexUpdater;
import psidev.psi.mi.jami.enricher.listener.InteractionEnricherListener;
import psidev.psi.mi.jami.enricher.listener.InteractorEnricherListener;
import psidev.psi.mi.jami.model.Complex;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ModelledFeature;
import psidev.psi.mi.jami.model.ModelledParticipant;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;

/**
 * Complex merger based on the jami interaction evidence enricher.
 * It will override properties loaded from the database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30/01/14</pre>
 */

public class IntactComplexMergerOverride extends IntactInteractorBaseMergerOverride<Complex,IntactComplex> implements ComplexEnricher {

    public IntactComplexMergerOverride(){
        super(new FullComplexUpdater());
    }

    protected IntactComplexMergerOverride(ComplexEnricher interactorEnricher){
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
}

