package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.VariableParameterValue;
import psidev.psi.mi.jami.model.VariableParameterValueSet;
import psidev.psi.mi.jami.utils.comparator.experiment.VariableParameterValueSetComparator;
import uk.ac.ebi.intact.jami.model.audit.AbstractAuditable;

import javax.persistence.*;
import java.util.*;

/**
 * Intact implementation of VariableParameterValueSet
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>09/01/14</pre>
 */
@Entity
@Table(name = "ia_interaction_var_parameters")
public class IntactVariableParameterValueSet extends AbstractAuditable implements VariableParameterValueSet {

    private Long id;
    private Set<VariableParameterValue> variableParameterValues;

    public IntactVariableParameterValueSet(){
    }

    @PrePersist
    @PreUpdate
    public void prePersistAndUpdate() {
        // check if all variable parameter values are possible to persist
        if (variableParameterValues != null && Hibernate.isInitialized(variableParameterValues) && !variableParameterValues.isEmpty()){
            Collection<VariableParameterValue> values = new ArrayList<VariableParameterValue>(variableParameterValues);
            for (VariableParameterValue value : values){
                if (!(value instanceof IntactVariableParameterValue)){
                    IntactVariableParameterValue clone = new IntactVariableParameterValue(value.getValue(), value.getVariableParameter(), value.getOrder());
                    this.variableParameterValues.remove(value);
                    this.variableParameterValues.add(clone);
                }
            }
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

    public int size() {
        return getVariableParameterValues().size();
    }

    @Transient
    public boolean isEmpty() {
        return getVariableParameterValues().isEmpty();
    }

    public boolean contains(Object o) {
        return getVariableParameterValues().contains(o);
    }

    public Iterator<VariableParameterValue> iterator() {
        return getVariableParameterValues().iterator();
    }

    public Object[] toArray() {
        return getVariableParameterValues().toArray();
    }

    public <T> T[] toArray(T[] ts) {
        return getVariableParameterValues().toArray(ts);
    }

    public boolean add(VariableParameterValue variableParameterValue) {
        return getVariableParameterValues().add(variableParameterValue);
    }

    public boolean remove(Object o) {
        return getVariableParameterValues().remove(o);
    }

    public boolean containsAll(Collection<?> objects) {
        return getVariableParameterValues().containsAll(objects);
    }

    public boolean addAll(Collection<? extends VariableParameterValue> variableParameterValues) {
        return getVariableParameterValues().addAll(variableParameterValues);
    }

    public boolean retainAll(Collection<?> objects) {
        return getVariableParameterValues().retainAll(objects);
    }

    public boolean removeAll(Collection<?> objects) {
        return getVariableParameterValues().removeAll(objects);
    }

    public void clear() {
        getVariableParameterValues().clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }

        if (!(o instanceof VariableParameterValueSet)){
            return false;
        }

        return VariableParameterValueSetComparator.areEquals(this, (VariableParameterValueSet) o);
    }

    @Override
    public int hashCode() {
        return VariableParameterValueSetComparator.hashCode(this);
    }

    @ManyToMany( targetEntity = IntactVariableParameterValue.class, fetch = FetchType.EAGER)
    @JoinTable(
            name="ia_varset2parametervalue",
            joinColumns = @JoinColumn( name="varset_id"),
            inverseJoinColumns = @JoinColumn( name="parametervalue_id")
    )
    @Target(IntactVariableParameterValue.class)
    private Set<VariableParameterValue> getVariableParameterValues() {
        if (this.variableParameterValues == null){
            this.variableParameterValues = new HashSet<VariableParameterValue>();
        }
        return variableParameterValues;
    }

    private void setVariableParameterValues(Set<VariableParameterValue> variableParameterValues) {
        this.variableParameterValues = variableParameterValues;
    }
}
