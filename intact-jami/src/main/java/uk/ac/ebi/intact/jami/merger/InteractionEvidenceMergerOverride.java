package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.CvTermEnricher;
import psidev.psi.mi.jami.enricher.ExperimentEnricher;
import psidev.psi.mi.jami.enricher.InteractionEvidenceEnricher;
import psidev.psi.mi.jami.enricher.ParticipantEnricher;
import psidev.psi.mi.jami.enricher.impl.full.FullInteractionEvidenceUpdater;
import psidev.psi.mi.jami.enricher.listener.InteractionEnricherListener;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.InteractionEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractionEvidence;

/**
 * Interaction evidence merger based on the jami interaction evidence enricher.
 * It will override properties loaded from the database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30/01/14</pre>
 */

public class InteractionEvidenceMergerOverride extends IntactDbMergerOverride<InteractionEvidence,IntactInteractionEvidence> implements InteractionEvidenceEnricher {

    public InteractionEvidenceMergerOverride(){
        super(new FullInteractionEvidenceUpdater());
    }

    protected InteractionEvidenceMergerOverride(InteractionEvidenceEnricher interactorEnricher){
        super(interactorEnricher);
    }

    @Override
    protected InteractionEvidenceEnricher getBasicEnricher() {
        return (InteractionEvidenceEnricher)super.getBasicEnricher();
    }

    public ExperimentEnricher getExperimentEnricher() {
        return getBasicEnricher().getExperimentEnricher();
    }

    @Override
    public void setExperimentEnricher(ExperimentEnricher enricher) {

    }

    @Override
    protected void mergeOtherProperties(IntactInteractionEvidence obj1, IntactInteractionEvidence obj2) {
        super.mergeOtherProperties(obj1, obj2);
        // reset parent to source parent
        obj2.setExperiment(obj1.getExperiment());
    }

    public ParticipantEnricher getParticipantEnricher() {
        return null;
    }

    public CvTermEnricher<CvTerm> getCvTermEnricher() {
        return null;
    }

    public InteractionEnricherListener<InteractionEvidence> getInteractionEnricherListener() {
        return getBasicEnricher().getInteractionEnricherListener();
    }

    @Override
    public void setParticipantEnricher(ParticipantEnricher enricher) {

    }

    @Override
    public void setCvTermEnricher(CvTermEnricher<CvTerm> enricher) {

    }

    @Override
    public void setInteractionEnricherListener(InteractionEnricherListener<InteractionEvidence> listener) {
        getBasicEnricher().setInteractionEnricherListener(listener);
    }
}

