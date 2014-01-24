package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.exception.IllegalParameterException;
import psidev.psi.mi.jami.model.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Intact implementation of parameter for modelled interactions
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13/01/14</pre>
 */
@javax.persistence.Entity
@Table( name = "ia_complex_parameter" )
public class ComplexParameter extends AbstractIntactParameter implements ModelledParameter{
    private Complex parent;
    private Collection<Publication> publications;

    protected ComplexParameter() {
        super();
    }

    public ComplexParameter(CvTerm type, ParameterValue value) {
        super(type, value);
    }

    public ComplexParameter(CvTerm type, ParameterValue value, CvTerm unit) {
        super(type, value, unit);
    }

    public ComplexParameter(CvTerm type, ParameterValue value, CvTerm unit, BigDecimal uncertainty) {
        super(type, value, unit, uncertainty);
    }

    public ComplexParameter(CvTerm type, ParameterValue value, BigDecimal uncertainty) {
        super(type, value, uncertainty);
    }

    public ComplexParameter(CvTerm type, String value) throws IllegalParameterException {
        super(type, value);
    }

    public ComplexParameter(CvTerm type, String value, CvTerm unit) throws IllegalParameterException {
        super(type, value, unit);
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
