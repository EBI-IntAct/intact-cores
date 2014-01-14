package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ModelledParticipant;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.validation.constraints.NotNull;

/**
 * Allostery having a feature modification effector
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@DiscriminatorValue("allostery_feature_effector")
public class IntactAllosteryWithFeature extends IntactAllostery<IntactFeatureModificationEffector>{

    protected IntactAllosteryWithFeature() {
        super();
    }

    public IntactAllosteryWithFeature(CvTerm outcome, ModelledParticipant allostericMolecule, IntactFeatureModificationEffector allostericEffector) {
        super(outcome, allostericMolecule, allostericEffector);
    }

    public IntactAllosteryWithFeature(CvTerm outcome, CvTerm response, ModelledParticipant allostericMolecule, IntactFeatureModificationEffector allostericEffector) {
        super(outcome, response, allostericMolecule, allostericEffector);
    }

    @Override
    @Embedded
    @NotNull
    public IntactFeatureModificationEffector getAllostericEffector() {
        return super.getAllostericEffector();
    }
}
