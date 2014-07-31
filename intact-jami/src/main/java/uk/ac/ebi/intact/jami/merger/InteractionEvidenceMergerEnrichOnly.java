package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.CvTermEnricher;
import psidev.psi.mi.jami.enricher.ExperimentEnricher;
import psidev.psi.mi.jami.enricher.InteractionEvidenceEnricher;
import psidev.psi.mi.jami.enricher.ParticipantEnricher;
import psidev.psi.mi.jami.enricher.impl.full.FullInteractionEvidenceEnricher;
import psidev.psi.mi.jami.enricher.listener.InteractionEnricherListener;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.InteractionEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractionEvidence;

/**
 * Interaction evidence merger based on the jami interaction evidence enricher.
 * It will only add missing info, it does not override anything
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30/01/14</pre>
 */

public class InteractionEvidenceMergerEnrichOnly extends IntactDbMergerEnrichOnly<InteractionEvidence,IntactInteractionEvidence> implements InteractionEvidenceEnricher {

    public InteractionEvidenceMergerEnrichOnly(){
        super(new FullInteractionEvidenceEnricher());
    }

    protected InteractionEvidenceMergerEnrichOnly(InteractionEvidenceEnricher interactorEnricher){
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
    public IntactInteractionEvidence merge(IntactInteractionEvidence int1, IntactInteractionEvidence int2) {
        // reset parent to source parent
        int2.setExperiment(int1.getExperiment());

        return super.merge(int1, int2);
    }

    public ParticipantEnricher getParticipantEnricher() {
        return null;
    }

    public CvTermEnricher<CvTerm> getCvTermEnricher() {
        return null;
    }

    public InteractionEnricherListener<InteractionEvidence> getInteractionEnricherListener() {
        return null;
    }
}

