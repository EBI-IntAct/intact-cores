package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.ParticipantEnricher;
import psidev.psi.mi.jami.enricher.impl.full.FullParticipantEnricher;
import psidev.psi.mi.jami.model.ModelledFeature;
import psidev.psi.mi.jami.model.ModelledParticipant;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledParticipant;

/**
 * Modelled participant merger based on the jami entity enricher.
 * It will only add missing info, it does not override anything
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public class IntactModelledParticipantMergerOverride<E extends ModelledParticipant, I extends IntactModelledParticipant>
        extends IntactEntityMergerOverride<E, I, ModelledFeature> {

    public IntactModelledParticipantMergerOverride() {
        super((Class<I>)IntactModelledParticipant.class, new FullParticipantEnricher<E, ModelledFeature>());
    }

    public IntactModelledParticipantMergerOverride(ParticipantEnricher<E, ModelledFeature> basicEnricher) {
        super((Class<I>)IntactModelledParticipant.class, basicEnricher);
    }

    public IntactModelledParticipantMergerOverride(Class<I> intactClass) {
        super(intactClass, new FullParticipantEnricher<E, ModelledFeature>());
    }

    public IntactModelledParticipantMergerOverride(Class<I> intactClass, ParticipantEnricher<E, ModelledFeature> basicEnricher) {
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
