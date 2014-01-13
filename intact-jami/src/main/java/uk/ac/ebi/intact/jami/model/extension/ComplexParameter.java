package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.exception.IllegalParameterException;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.model.listener.InteractionParameterListener;

import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
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
    private Complex complex;

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
    @JoinColumn( name = "complex_ac" )
    @Target(IntactComplex.class)
    public Complex getComplex() {
        return complex;
    }

    public void setComplex(Complex interaction) {
        this.complex = interaction;
    }

    public <P extends Publication> Collection<P> getPublications() {
        return null;
    }
}
