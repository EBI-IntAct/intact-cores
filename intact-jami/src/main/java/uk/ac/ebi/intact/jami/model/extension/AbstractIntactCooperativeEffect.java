package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.model.audit.AbstractAuditable;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract class for cooperative effect
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@Entity
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
@DiscriminatorColumn(name = "category", discriminatorType = DiscriminatorType.STRING)
public abstract class AbstractIntactCooperativeEffect extends AbstractAuditable implements CooperativeEffect{

    private Collection<CooperativityEvidence> cooperativityEvidences;
    private Collection<ModelledInteraction> affectedInteractions;
    private Collection<Annotation> annotations;
    private CvTerm outcome;
    private CvTerm response;
    private BigDecimal value;
    private Long id;

    private Complex parent;

    protected AbstractIntactCooperativeEffect(){

    }

    public AbstractIntactCooperativeEffect(CvTerm outcome){
        if (outcome == null){
            throw new IllegalArgumentException("The outcome of a CooperativeEffect cannot be null");
        }
        this.outcome = outcome;
    }

    public AbstractIntactCooperativeEffect(CvTerm outcome, CvTerm response){
        this(outcome);
        this.response = response;
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

    @OneToMany( mappedBy = "parent", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = IntactCooperativityEvidence.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(IntactCooperativityEvidence.class)
    public Collection<CooperativityEvidence> getCooperativityEvidences() {
        if (cooperativityEvidences == null){
            initialiseCooperativityEvidences();
        }
        return cooperativityEvidences;
    }

    @ManyToMany(targetEntity=IntactComplex.class)
    @JoinTable(
            name="cooperative_effect2affected_complex",
            joinColumns=@JoinColumn(name="cooperative_effect_id"),
            inverseJoinColumns=@JoinColumn(name="complex_ac")
    )
    @Target(IntactComplex.class)
    public Collection<ModelledInteraction> getAffectedInteractions() {
        if (affectedInteractions == null){
            initialiseAffectedInteractions();
        }
        return affectedInteractions;
    }

    @OneToMany( mappedBy = "parent", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = CooperativeEffectAnnotation.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(CooperativeEffectAnnotation.class)
    public Collection<Annotation> getAnnotations() {
        if (annotations == null){
            initialiseAnnotations();
        }
        return annotations;
    }

    @ManyToOne( targetEntity = IntactCvTerm.class, optional = false)
    @JoinColumn( name = "outcome_ac" )
    @Target(IntactCvTerm.class)
    @NotNull
    public CvTerm getOutCome() {
        return outcome;
    }

    public void setOutCome(CvTerm effect) {
        if (effect == null){
            throw new IllegalArgumentException("The outcome of a CooperativeEffect cannot be null");
        }
        this.outcome = effect;
    }

    @ManyToOne( targetEntity = IntactCvTerm.class)
    @JoinColumn( name = "response_ac" )
    @Target(IntactCvTerm.class)
    public CvTerm getResponse() {
        return this.response;
    }

    public void setResponse(CvTerm response) {
        this.response = response;
    }

    @Column(name = "value")
    public BigDecimal getCooperativeEffectValue() {
        return this.value;
    }

    public void setCooperativeEffectValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Outcome: "+(outcome != null ? outcome.toString() : "") + (response != null ? ", response: " + response.toString() : "");
    }

    protected void initialiseCooperativityEvidences(){
        this.cooperativityEvidences = new ArrayList<CooperativityEvidence>();
    }

    protected void initialiseAffectedInteractions(){
        this.affectedInteractions = new ArrayList<ModelledInteraction>();
    }

    protected void initialiseAnnotations(){
        this.annotations = new ArrayList<Annotation>();
    }

    private void setCooperativityEvidences(Collection<CooperativityEvidence> cooperativityEvidences) {
        this.cooperativityEvidences = cooperativityEvidences;
    }

    private void setAffectedInteractions(Collection<ModelledInteraction> affectedInteractions) {
        this.affectedInteractions = affectedInteractions;
    }

    private void setAnnotations(Collection<Annotation> annotations) {
        this.annotations = annotations;
    }
}
