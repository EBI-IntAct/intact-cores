package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.VariableParameter;
import psidev.psi.mi.jami.model.VariableParameterValue;
import psidev.psi.mi.jami.utils.comparator.experiment.VariableParameterValueComparator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Intact implementation of VariableParameterValue
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>09/01/14</pre>
 */
@Entity
@Table(name = "ia_variable_parameter_value")
public class IntactVariableParameterValue implements VariableParameterValue {

    private String value;
    private VariableParameter variableParameter;
    private Long id;

    private int order;

    public IntactVariableParameterValue() {
    }

    public IntactVariableParameterValue(String value, VariableParameter variableParameter){
        if (value == null){
            throw new IllegalArgumentException("The value of a variableParameterValue cannot be null");
        }
        this.value = value;
        this.variableParameter = variableParameter;
        this.order = 0;
    }

    public IntactVariableParameterValue(String value, VariableParameter variableParameter, Integer order){
        if (value == null){
            throw new IllegalArgumentException("The value of a variableParameterValue cannot be null");
        }
        this.value = value;
        this.variableParameter = variableParameter;
        if (order == null){
            this.order = 0;
        }
        else{
            this.order = order;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idGenerator")
    @SequenceGenerator(name="idGenerator", sequenceName="DEFAULT_ID_SEQ", initialValue = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Size(max = 4000)
    @NotNull
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value == null){
            throw new IllegalArgumentException("The value of a variableParameterValue cannot be null");
        }
        this.value = value;
    }

    @Transient
    public Integer getOrder() {
        if (this.order != 0){
            return order;
        }
        return null;
    }

    @Column(name = "variableorder")
    @Size(max = 4000)
    private int getPersistentOrder() {
        return this.order;
    }

    private void setPersistentOrder(int persistentOrder) {
        this.order = persistentOrder;
    }

    @ManyToOne( targetEntity = IntactVariableParameter.class )
    @JoinColumn( name = "parameter_ac" )
    @Target(IntactVariableParameter.class)
    public VariableParameter getVariableParameter() {
        return variableParameter;
    }

    public void setVariableParameter(VariableParameter variableParameter) {
        this.variableParameter = variableParameter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }

        if (!(o instanceof VariableParameterValue)){
            return false;
        }

        return VariableParameterValueComparator.areEquals(this, (VariableParameterValue) o);
    }

    @Override
    public int hashCode() {
        return VariableParameterValueComparator.hashCode(this);
    }

    @Override
    public String toString() {
        return value != null ? value.toString() : super.toString();
    }
}
