package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.Complex;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.InteractionEvidence;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Intact implementation of complex confidence
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@Entity
@Table(name = "ia_complex_confidence")
public class ComplexConfidence extends AbstractIntactConfidence{

    private Complex interaction;

    public ComplexConfidence() {
    }

    public ComplexConfidence(CvTerm type, String value) {
        super(type, value);
    }

    @ManyToOne( targetEntity = IntactComplex.class )
    @JoinColumn( name = "complex_ac" )
    @Target(IntactComplex.class)
    public Complex getInteraction() {
        return interaction;
    }

    public void setInteraction(Complex interaction) {
        this.interaction = interaction;
    }
}
