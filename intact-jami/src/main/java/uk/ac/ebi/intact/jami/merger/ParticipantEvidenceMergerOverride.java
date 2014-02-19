package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.ParticipantEvidenceEnricher;
import psidev.psi.mi.jami.model.ParticipantEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactParticipantEvidence;

/**
 * participant evidence merger based on the jami entity enricher.
 * It will only add missing info, it does not override anything
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class ParticipantEvidenceMergerOverride<E extends ParticipantEvidence, I extends IntactParticipantEvidence>
        extends ExperimentalEntityMergerOverride<E, I> {

    public ParticipantEvidenceMergerOverride() {
        super((Class<I>)IntactParticipantEvidence.class);
    }

    public ParticipantEvidenceMergerOverride(ParticipantEvidenceEnricher<E> basicEnricher) {
        super((Class<I>)IntactParticipantEvidence.class, basicEnricher);
    }

    public ParticipantEvidenceMergerOverride(Class<I> intactClass) {
        super(intactClass);
    }

    public ParticipantEvidenceMergerOverride(Class<I> intactClass, ParticipantEvidenceEnricher<E> basicEnricher) {
        super(intactClass, basicEnricher);
    }

    @Override
    public I merge(I exp1, I exp2) {

        // reset parent
        exp2.setInteraction(exp1.getInteraction());

        // obj2 is mergedExp
        return super.merge(exp1, exp2);
    }
}
