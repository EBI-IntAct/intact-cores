package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ModelledParticipant;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.validation.constraints.NotNull;

/**
 * Allostery having a molecule effector
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@DiscriminatorValue("allostery_molecule_effector")
public class IntactAllosteryWithMolecule extends IntactAllostery<IntactMoleculeEffector>{

    protected IntactAllosteryWithMolecule() {
        super();
    }

    public IntactAllosteryWithMolecule(CvTerm outcome, ModelledParticipant allostericMolecule, IntactMoleculeEffector allostericEffector) {
        super(outcome, allostericMolecule, allostericEffector);
    }

    public IntactAllosteryWithMolecule(CvTerm outcome, CvTerm response, ModelledParticipant allostericMolecule, IntactMoleculeEffector allostericEffector) {
        super(outcome, response, allostericMolecule, allostericEffector);
    }

    @Override
    @Embedded
    @NotNull
    public IntactMoleculeEffector getAllostericEffector() {
        return super.getAllostericEffector();
    }
}
