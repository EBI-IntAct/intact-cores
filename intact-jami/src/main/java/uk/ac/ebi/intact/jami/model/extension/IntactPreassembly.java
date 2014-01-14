package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Preassembly;

import javax.persistence.DiscriminatorValue;

/**
 * Intact implementation of preassembly
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@DiscriminatorValue("pre-assembly")
public class IntactPreassembly extends AbstractIntactCooperativeEffect implements Preassembly{

    public IntactPreassembly() {
    }

    public IntactPreassembly(CvTerm outcome) {
        super(outcome);
    }

    public IntactPreassembly(CvTerm outcome, CvTerm response) {
        super(outcome, response);
    }
}
