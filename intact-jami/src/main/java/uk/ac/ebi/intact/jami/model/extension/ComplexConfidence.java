package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Intact implementation of complex confidence
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@Entity
@Table(name = "ia_complex_confidence")
public class ComplexConfidence extends AbstractIntactConfidence implements ModelledConfidence{

    private Complex parent;
    private Collection<Publication> publications;

    public ComplexConfidence() {
    }

    public ComplexConfidence(CvTerm type, String value) {
        super(type, value);
    }

    @ManyToOne( targetEntity = IntactComplex.class )
    @JoinColumn( name = "complex_ac", referencedColumnName = "ac" )
    @Target(IntactComplex.class)
    public Complex getParent() {
        return parent;
    }

    public void setParent(Complex interaction) {
        this.parent = interaction;
    }

    @Transient
    public Collection<Publication> getPublications() {
        if (this.publications == null){
            this.publications = new ArrayList<Publication>();
        }
        return this.publications;
    }
}
