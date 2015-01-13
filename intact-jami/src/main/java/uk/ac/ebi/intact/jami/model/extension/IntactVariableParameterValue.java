package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.comparator.experiment.VariableParameterValueComparator;
import uk.ac.ebi.intact.jami.model.audit.AbstractAuditable;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Intact implementation of VariableParameterValue
 *
 * NOTE: variable parameter value has the ownership of variable parameter value-variable parameter relationship so to persist the relationship, the method getVariableParameter must
 * point to the right variable parameter.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>09/01/14</pre>
 */
@Entity
@Table(name = "ia_var_parameter_value")
public class IntactVariableParameterValue extends AbstractAuditable implements VariableParameterValue {

    private String value;
    private VariableParameter variableParameter;
    private Long id;

    private int order;

    private Collection<VariableParameterValueSet> interactionParameterValues;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEFAULT_ID_SEQ")
    @SequenceGenerator(name="DEFAULT_ID_SEQ", sequenceName="DEFAULT_ID_SEQ", initialValue = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Size(max = IntactUtils.MAX_DESCRIPTION_LEN)
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

    public void setOrder(Integer order){
        if (order == null){
             this.order = 0;
        }
        this.order = order;
    }

    private void setPersistentOrder(int persistentOrder) {
        this.order = persistentOrder;
    }

    @ManyToOne( targetEntity = IntactVariableParameter.class )
    @JoinColumn( name = "parameter_id", referencedColumnName = "id" )
    @Target(IntactVariableParameter.class)
    public VariableParameter getVariableParameter() {
        return variableParameter;
    }

    public void setVariableParameter(VariableParameter variableParameter) {
        this.variableParameter = variableParameter;
    }

    @ManyToMany( mappedBy = "variableParameterValues", targetEntity = IntactVariableParameterValueSet.class)
    @Target(IntactVariableParameterValueSet.class)
    public Collection<VariableParameterValueSet> getInteractionParameterValues() {
        if (this.interactionParameterValues == null){
            this.interactionParameterValues = new ArrayList<VariableParameterValueSet>();
        }
        return interactionParameterValues;
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

    @Column(name = "variableorder")
    private int getPersistentOrder() {
        return this.order;
    }

    private void setInteractionParameterValues(Collection<VariableParameterValueSet> values){
        this.interactionParameterValues = values;
    }
}
