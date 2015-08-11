package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ModelledEntity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * Allostery having a feature modification effector
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@DiscriminatorValue("allostery_feature")
@Entity
public class IntactAllosteryWithFeature extends AbstractIntactAllostery<IntactFeatureModificationEffector>{

    protected IntactAllosteryWithFeature() {
        super();
    }

    public IntactAllosteryWithFeature(CvTerm outcome, ModelledEntity allostericMolecule, IntactFeatureModificationEffector allostericEffector) {
        super(outcome, allostericMolecule, allostericEffector);
    }

    public IntactAllosteryWithFeature(CvTerm outcome, CvTerm response, ModelledEntity allostericMolecule, IntactFeatureModificationEffector allostericEffector) {
        super(outcome, response, allostericMolecule, allostericEffector);
    }

    @Override
    @Embedded
    @NotNull
    public IntactFeatureModificationEffector getAllostericEffector() {
        return super.getAllostericEffector();
    }

    public void setAllostericEffector(IntactFeatureModificationEffector effector) {
        super.setAllostericEffector(effector);
    }
}
