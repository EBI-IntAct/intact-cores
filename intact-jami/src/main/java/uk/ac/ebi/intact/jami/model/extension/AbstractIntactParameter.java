package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.exception.IllegalParameterException;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Parameter;
import psidev.psi.mi.jami.model.ParameterValue;
import psidev.psi.mi.jami.utils.ParameterUtils;
import psidev.psi.mi.jami.utils.comparator.parameter.UnambiguousParameterComparator;
import uk.ac.ebi.intact.jami.model.AbstractIntactPrimaryObject;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Abstract class for Intact parameters
 * Note: this implementation was chosen because parameters do not make sense without their parents and are not shared by different entities
 * It is then better to have several parameter tables, one for each entity rather than one big parameter table and x join tables.
 *
 * It would be better to never query for a parameter without involving its parent.
 *
 * Future improvements: this class would become an entity with Inheritance=TABLE_PER_CLASS and all subclasses would be a different table.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13/01/14</pre>
 */
@MappedSuperclass
public abstract class AbstractIntactParameter extends AbstractIntactPrimaryObject implements Parameter{

    private CvTerm type;
    private BigDecimal uncertainty;
    private CvTerm unit;
    private ParameterValue value;
    private int base;
    private int exponent;
    private double factor;

    protected AbstractIntactParameter(){
        super();
        this.base = 10;
        this.exponent = 0;
        this.factor = 0;
    }

    public AbstractIntactParameter(CvTerm type, ParameterValue value){
        super();
        if (type == null){
            throw new IllegalArgumentException("The parameter type is required and cannot be null");
        }
        this.type = type;
        if (value == null){
            throw new IllegalArgumentException("The parameter value is required and cannot be null");
        }
        this.value = value;
        this.base = (int)this.value.getBase();
        this.exponent = (int)this.value.getExponent();
        this.factor = this.value.getFactor().doubleValue();
    }

    public AbstractIntactParameter(CvTerm type, ParameterValue value, CvTerm unit){
        this(type, value);
        this.unit = unit;
    }

    public AbstractIntactParameter(CvTerm type, ParameterValue value, CvTerm unit, BigDecimal uncertainty){
        this(type, value, unit);
        this.uncertainty = uncertainty;
    }

    public AbstractIntactParameter(CvTerm type, ParameterValue value, BigDecimal uncertainty){
        this(type, value);
        this.uncertainty = uncertainty;
    }

    public AbstractIntactParameter(CvTerm type, String value) throws IllegalParameterException {
        super();
        if (type == null){
            throw new IllegalArgumentException("The parameter type is required and cannot be null");
        }
        this.type = type;

        Parameter param = ParameterUtils.createParameterFromString(type, value);
        this.value = param.getValue();
        this.base = (int)this.value.getBase();
        this.exponent = (int)this.value.getExponent();
        this.factor = this.value.getFactor().doubleValue();
        this.uncertainty = param.getUncertainty();
    }

    public AbstractIntactParameter(CvTerm type, String value, CvTerm unit) throws IllegalParameterException {
        this(type, value);
        this.unit = unit;
    }

    @ManyToOne(targetEntity = IntactCvTerm.class, optional = false)
    @JoinColumn( name = "parametertype_ac", referencedColumnName = "ac")
    @NotNull
    @Target(IntactCvTerm.class)
    public CvTerm getType() {
        return this.type;
    }

    public void setType(CvTerm type) {
        if (type == null){
            throw new IllegalArgumentException("The parameter type is required and cannot be null");
        }
        this.type = type;
    }

    @Transient
    public BigDecimal getUncertainty() {
        return this.uncertainty;
    }

    public void setUncertainty(BigDecimal uncertainty) {
        this.uncertainty = uncertainty;
    }

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn( name = "parameterunit_ac", referencedColumnName = "ac" )
    @Target(IntactCvTerm.class)
    public CvTerm getUnit() {
        return this.unit;
    }

    public void setUnit(CvTerm unit) {
        this.unit = unit;
    }

    @Transient
    public ParameterValue getValue() {
        if (this.value == null){
           this.value = new ParameterValue(new BigDecimal(this.factor), (short)this.base, (short)this.exponent);
        }
        return this.value;
    }

    public void setValue(ParameterValue value) {
        if (value == null){
            throw new IllegalArgumentException("The parameter value is required and cannot be null");
        }
        this.value = value;
        this.base = (int)this.value.getBase();
        this.exponent = (int)this.value.getExponent();
        this.factor = this.value.getFactor().doubleValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }

        if (!(o instanceof Parameter)){
            return false;
        }

        return UnambiguousParameterComparator.areEquals(this, (Parameter) o);
    }

    @Override
    public String toString() {
        return type.toString() + ": " + value  + (uncertainty != null ? " ~" + uncertainty.toString() : "" + (unit != null ? "("+unit.toString()+")" : ""));
    }

    @Override
    public int hashCode() {
        return UnambiguousParameterComparator.hashCode(this);
    }

    @Column(name = "base")
    protected int getBase() {
        return this.base;
    }

    @Column(name = "exponent")
    protected int getExponent() {
        return this.exponent;
    }

    @Column(name = "factor")
    protected double getFactor() {
        return this.factor;
    }

    protected void setBase(int base){
        this.base = base;
        this.value = null;
    }

    protected void setExponent(int exponent){
        this.exponent = exponent;
        this.value = null;
    }

    protected void setFactor(double factor){
        this.factor = factor;
        this.value = null;
    }

    @Column(name = "uncertainty")
    protected Double getDbUncertainty() {
        return this.uncertainty != null ? this.uncertainty.doubleValue() : null;
    }

    protected void setDbUncertainty(Double uncertainty) {
        this.uncertainty = uncertainty != null ? new BigDecimal(uncertainty) : null;
    }
}
