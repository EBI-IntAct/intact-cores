package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Experiment;
import psidev.psi.mi.jami.model.VariableParameter;
import psidev.psi.mi.jami.model.VariableParameterValue;
import psidev.psi.mi.jami.utils.clone.CvTermCloner;
import psidev.psi.mi.jami.utils.comparator.experiment.UnambiguousVariableParameterComparator;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Intact implementation of variable parameter
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>09/01/14</pre>
 */
@Entity
@Table(name = "ia_variable_parameter")
public class IntactVariableParameter implements VariableParameter {

    private String description;
    private CvTerm unit;
    private Collection<VariableParameterValue> variableValues;

    private Experiment experiment;

    private Long id;

    protected IntactVariableParameter(){

    }

    public IntactVariableParameter(String description){
        if (description == null){
            throw new IllegalArgumentException("The description of the variableParameter is required and cannot be null.");
        }
        this.description = description;
    }

    public IntactVariableParameter(String description, Experiment experiment){
        this(description);
        this.experiment = experiment;
    }

    public IntactVariableParameter(String description, CvTerm unit){
        this(description);
        this.unit = unit;
    }

    public IntactVariableParameter(String description, Experiment experiment, CvTerm unit){
        this(description, experiment);
        this.unit = unit;
    }

    @PrePersist
    @PreUpdate
    public void prePersistAndUpdate() {
        // check if unit possible to persist
        if (unit != null && !(unit instanceof IntactCvTerm)){
            IntactCvTerm clone = new IntactCvTerm(this.unit.getShortName());
            clone.setObjClass(IntactUtils.UNIT_OBJCLASS);
            CvTermCloner.copyAndOverrideCvTermProperties(this.unit, clone);
            this.unit = clone;
        }
        // check if all variable parameter values are possible to persist
        if (this.variableValues != null && Hibernate.isInitialized(this.variableValues) && !this.variableValues.isEmpty()){
            Collection<VariableParameterValue> values = new ArrayList<VariableParameterValue>(this.variableValues);
            for (VariableParameterValue value : values){
                if (!(value instanceof IntactVariableParameterValue) && value.getVariableParameter() != null && value.getVariableParameter() != this){
                    IntactVariableParameterValue clone = new IntactVariableParameterValue(value.getValue(), value.getVariableParameter(), value.getOrder());
                    this.variableValues.remove(value);
                    this.variableValues.add(clone);
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

    @Size(max = 4000)
    @NotNull
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        if (description == null){
            throw new IllegalArgumentException("The description cannot be null");
        }
        this.description = description;
    }

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn(name = "unit_ac")
    @Target(IntactCvTerm.class)
    public CvTerm getUnit() {
        return this.unit;
    }

    public void setUnit(CvTerm unit) {
        this.unit = unit;
    }

    @OneToMany( mappedBy = "variableParameter", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},targetEntity = CvTermAlias.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(CvTermAlias.class)
    public Collection<VariableParameterValue> getVariableValues() {
        if (variableValues == null){
            initialiseVatiableParameterValues();
        }
        return this.variableValues;
    }

    @ManyToOne(targetEntity = IntactExperiment.class)
    @JoinColumn(name = "experiment_ac")
    @Target(IntactExperiment.class)
    public Experiment getExperiment() {
        return this.experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public void setExperimentAndAddVariableParameter(Experiment experiment) {
        if (this.experiment != null){
            this.experiment.removeVariableParameter(this);
        }
        if (experiment != null){
            experiment.addVariableParameter(this);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }

        if (!(o instanceof VariableParameter)){
            return false;
        }

        return UnambiguousVariableParameterComparator.areEquals(this, (VariableParameter) o);
    }

    @Override
    public int hashCode() {
        return UnambiguousVariableParameterComparator.hashCode(this);
    }

    @Override
    public String toString() {
        return description.toString() + (unit != null ? "(unit: "+unit.toString()+")":"");
    }

    private void initialiseVatiableParameterValues(){
        this.variableValues = new ArrayList<VariableParameterValue>();
    }

    private void setVariableValues(Collection<VariableParameterValue> variableValues) {
        this.variableValues = variableValues;
    }
}