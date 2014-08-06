package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.*;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.model.Parameter;
import psidev.psi.mi.jami.utils.collection.AbstractListHavingProperties;
import uk.ac.ebi.intact.jami.model.listener.ParticipantParameterListener;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.CascadeType;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Intact implementation of participant evidence
 *
 * NOTE: if the participant is not a direct participant of an interaction but is part of a participantSet,
 * the interaction back reference will not be persistent. Only getDbParentPool will be persisted and getDbParentInteraction will return null
 * even if the participant has a back reference to the interaction.
 * NOTE: For backward compatibility with intact-core, a method getDbExperimentalRoles (deprecated) is present but only protected as getExperimentalRole should always be used instead.
 * This method should never be used in any applications.
 * NOTE: getIdentificationMethods is not persistent and getDbIdentificationMethods should be used in HQL queries when we want to check identification methods in the participant which
 * override the identification method in the experiment. The method getDbIdentificationMethods only contain the identification methods that override the one in the experiment if any.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15/01/14</pre>
 */
@EntityListeners(value = {ParticipantParameterListener.class})
@Entity
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
@Table(name = "ia_component")
@DiscriminatorColumn(name = "category", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("participant_evidence")
@Where(clause = "category = 'participant_evidence'")
public class IntactParticipantEvidence extends AbstractIntactParticipant<InteractionEvidence, FeatureEvidence> implements ParticipantEvidence{

    private Collection<CvTerm> persistentIdentificationMethods;
    private Collection<CvTerm> identificationMethods;
    private Collection<CvTerm> experimentalPreparations;
    private Organism expressedIn;
    private Collection<Confidence> confidences;
    private Collection<Parameter> parameters;
    /**
     * @deprecated
     */
    @Deprecated
    private List<CvTerm> experimentalRoles;

    private Collection<CausalRelationship> relatedCausalRelationships;
    private Collection<Range> relatedRanges;

    protected IntactParticipantEvidence() {
    }

    public IntactParticipantEvidence(Interactor interactor, CvTerm participantIdentificationMethod) {
        super(interactor);
        if (participantIdentificationMethod != null){
            getIdentificationMethods().add(participantIdentificationMethod);
        }
    }

    public IntactParticipantEvidence(Interactor interactor, CvTerm bioRole, CvTerm participantIdentificationMethod) {
        super(interactor, bioRole);
        if (participantIdentificationMethod != null){
            getIdentificationMethods().add(participantIdentificationMethod);
        }
    }

    public IntactParticipantEvidence(Interactor interactor, Stoichiometry stoichiometry, CvTerm participantIdentificationMethod) {
        super(interactor, stoichiometry);
        if (participantIdentificationMethod != null){
            getIdentificationMethods().add(participantIdentificationMethod);
        }
    }

    public IntactParticipantEvidence(Interactor interactor, CvTerm bioRole, CvTerm expRole, CvTerm participantIdentificationMethod) {
        super(interactor, bioRole);
        setExperimentalRole(expRole);

        if (participantIdentificationMethod != null){
            getIdentificationMethods().add(participantIdentificationMethod);
        }
    }

    public IntactParticipantEvidence(Interactor interactor, CvTerm bioRole, CvTerm expRole, Stoichiometry stoichiometry, CvTerm participantIdentificationMethod) {
        super(interactor, bioRole, stoichiometry);
        setExperimentalRole(expRole);

        if (participantIdentificationMethod != null){
            getIdentificationMethods().add(participantIdentificationMethod);
        }
    }

    public IntactParticipantEvidence(Interactor interactor, CvTerm bioRole, CvTerm expRole, Organism expressedIn, CvTerm participantIdentificationMethod) {
        super(interactor, bioRole);
        setExperimentalRole(expRole);

        this.expressedIn = expressedIn;
        if (participantIdentificationMethod != null){
            getIdentificationMethods().add(participantIdentificationMethod);
        }
    }

    public IntactParticipantEvidence(Interactor interactor, CvTerm bioRole, CvTerm expRole, Stoichiometry stoichiometry, Organism expressedIn, CvTerm participantIdentificationMethod) {
        super(interactor, bioRole, stoichiometry);
        setExperimentalRole(expRole);

        this.expressedIn = expressedIn;
        if (participantIdentificationMethod != null){
            getIdentificationMethods().add(participantIdentificationMethod);
        }
    }

    public IntactParticipantEvidence(Interactor interactor) {
        super(interactor);
    }

    public IntactParticipantEvidence(Interactor interactor, Stoichiometry stoichiometry) {
        super(interactor, stoichiometry);
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = ParticipantEvidenceXref.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(ParticipantEvidenceXref.class)
    public Collection<Xref> getXrefs() {
        return super.getXrefs();
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = ParticipantEvidenceAnnotation.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @JoinTable(
            name="ia_component2annot",
            joinColumns = @JoinColumn( name="component_ac"),
            inverseJoinColumns = @JoinColumn( name="annotation_ac")
    )
    @Target(ParticipantEvidenceAnnotation.class)
    /**
     * WARNING: The join table is for backward compatibility with intact-core.
     * When intact-core will be removed, the join table would disappear wnd the relation would become
     * @JoinColumn(name="parent_ac", referencedColumnName="ac")
     */
    public Collection<Annotation> getAnnotations() {
        return super.getAnnotations();
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = ParticipantEvidenceAlias.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(ParticipantEvidenceAlias.class)
    public Collection<Alias> getAliases() {
        return super.getAliases();
    }

    @Override
    @OneToMany( mappedBy = "participant", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = IntactFeatureEvidence.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(IntactFeatureEvidence.class)
    public Collection<FeatureEvidence> getFeatures() {
        return super.getFeatures();
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = ExperimentalCausalRelationship.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @JoinColumn(name="source_ac", referencedColumnName="ac")
    @Target(ExperimentalCausalRelationship.class)
    @Override
    public Collection<CausalRelationship> getCausalRelationships() {
        return super.getCausalRelationships();
    }

    @OneToMany( mappedBy = "experimentalTarget", targetEntity = ExperimentalCausalRelationship.class)
    @Target(ExperimentalCausalRelationship.class)
    /**
     * List of experimental causal relationships having this participant as target
     */
    public Collection<CausalRelationship> getRelatedCausalRelationships(){
        if (this.relatedCausalRelationships == null){
            this.relatedCausalRelationships = new ArrayList<CausalRelationship>();
        }
        return this.relatedCausalRelationships;
    }

    @OneToMany( mappedBy = "experimentalParticipant", targetEntity = ExperimentalRange.class)
    @Target(ExperimentalRange.class)
    /**
     * List of experimental ranges pointing to this participant
     */
    public Collection<Range> getRelatedRanges(){
        if (this.relatedRanges == null){
            this.relatedRanges = new ArrayList<Range>();
        }
        return this.relatedRanges;
    }

    @Transient
    public CvTerm getExperimentalRole() {
        // the experimental role list is never empty
        if (getDbExperimentalRoles().isEmpty()){
            this.experimentalRoles.add(IntactUtils.createMIExperimentalRole(Participant.UNSPECIFIED_ROLE, Participant.UNSPECIFIED_ROLE_MI));
        }
        return this.experimentalRoles.iterator().next();
    }

    public void setExperimentalRole(CvTerm expRole) {
        if (!getDbExperimentalRoles().isEmpty()){
             this.experimentalRoles.remove(0);
        }

        if (expRole == null){
            this.experimentalRoles.add(0, IntactUtils.createMIExperimentalRole(Participant.UNSPECIFIED_ROLE, Participant.UNSPECIFIED_ROLE_MI));
        }
        else{
            this.experimentalRoles.add(0, expRole);
        }
    }

    @Transient
    public Collection<CvTerm> getIdentificationMethods() {
        if (identificationMethods == null){
            initialiseIdentificationMethods();
        }
        return this.identificationMethods;
    }

    @ManyToMany(targetEntity = IntactCvTerm.class)
    @JoinTable(
            name = "ia_component2exp_preps",
            joinColumns = {@JoinColumn( name = "component_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "cvobject_ac" )}
    )
    @Target(IntactCvTerm.class)
    public Collection<CvTerm> getExperimentalPreparations() {
        if (experimentalPreparations == null){
            initialiseExperimentalPreparations();
        }
        return this.experimentalPreparations;
    }

    @ManyToOne(targetEntity = IntactOrganism.class)
    @JoinColumn( name = "expressedin_ac", referencedColumnName = "ac")
    @Target(IntactOrganism.class)
    public Organism getExpressedInOrganism() {
        return this.expressedIn;
    }

    public void setExpressedInOrganism(Organism organism) {
        this.expressedIn = organism;
    }

    @OneToMany( orphanRemoval = true,
            cascade = {CascadeType.ALL}, targetEntity = ParticipantEvidenceConfidence.class)
    @JoinColumn(name="component_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(ParticipantEvidenceConfidence.class)
    public Collection<Confidence> getConfidences() {
        if (confidences == null){
            initialiseConfidences();
        }
        return this.confidences;
    }

    @OneToMany( orphanRemoval = true,
            cascade = {CascadeType.ALL}, targetEntity = ParticipantEvidenceParameter.class)
    @JoinColumn(name="component_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(ParticipantEvidenceParameter.class)
    public Collection<Parameter> getParameters() {
        if (parameters == null){
            initialiseParameters();
        }
        return this.parameters;
    }

    @Override
    public String toString() {
        return super.toString() + getExperimentalRole() + (expressedIn != null ? ", " + expressedIn.toString() : "");
    }

    @Transient
    public boolean areConfidencesInitialized(){
        return Hibernate.isInitialized(getConfidences());
    }

    @Transient
    public boolean areParametersInitialized(){
        return Hibernate.isInitialized(getParameters());
    }

    @Transient
    public boolean areIdentificationMethodsInitialized(){
        return Hibernate.isInitialized(getDbIdentificationMethods());
    }

    @Transient
    public boolean areExperimentalPreparationsInitialized(){
        return Hibernate.isInitialized(getExperimentalPreparations());
    }

    @ManyToOne( targetEntity = IntactInteractionEvidence.class )
    @JoinColumn( name = "interaction_ac" )
    @Target(IntactInteractionEvidence.class)
    @Override
    protected InteractionEvidence getDbParentInteraction() {
        return super.getDbParentInteraction();
    }

    @ManyToMany(targetEntity = IntactCvTerm.class)
    @JoinTable(
            name = "ia_component2exprole",
            joinColumns = {@JoinColumn( name = "component_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "experimentalrole_ac" )}
    )
    @Target(IntactCvTerm.class)
    /**
     * @deprecated this method is for backward compatibility only. Use experimentalRole instead
     */
    @Deprecated
    @LazyCollection(LazyCollectionOption.FALSE)
    protected List<CvTerm> getDbExperimentalRoles() {
        if (this.experimentalRoles == null){
            this.experimentalRoles = new ArrayList<CvTerm>();
        }
        return this.experimentalRoles;
    }

    protected void initialiseExperimentalPreparations() {
        this.experimentalPreparations = new ArrayList<CvTerm>();
    }

    protected void initialiseConfidences() {
        this.confidences = new ArrayList<Confidence>();
    }

    protected void initialiseParameters() {
        this.parameters = new ArrayList<Parameter>();
    }

    protected void setDbExperimentalRoles(List<CvTerm> experimentalRoles) {
        this.experimentalRoles = experimentalRoles;
    }

    protected void setIdentificationMethods(Collection<CvTerm> identificationMethods) {
        this.identificationMethods = identificationMethods;
    }

    protected void setExperimentalPreparations(Collection<CvTerm> experimentalPreparations) {
        this.experimentalPreparations = experimentalPreparations;
    }

    protected void setConfidences(Collection<Confidence> confidences) {
        this.confidences = confidences;
    }

    protected void setParameters(Collection<Parameter> parameters) {
        this.parameters = parameters;
    }

    protected void initialiseIdentificationMethods(){
        setIdentificationMethods(new IdentificationMethodList());
        ((IdentificationMethodList)getIdentificationMethods()).addAllOnly(getDbIdentificationMethods());
        if (getInteraction() != null && getInteraction().getExperiment() instanceof IntactExperiment){
            IntactExperiment intactExperiment = (IntactExperiment) getInteraction().getExperiment();
            if (intactExperiment.getParticipantIdentificationMethod() != null){
                ((IdentificationMethodList)getIdentificationMethods()).addOnly(intactExperiment.getParticipantIdentificationMethod());
            }
        }
    }

    protected void setDbIdentificationMethods(Collection<CvTerm> persistentIdentificationMethods) {
        this.persistentIdentificationMethods = persistentIdentificationMethods;
    }

    @ManyToMany(targetEntity = IntactCvTerm.class)
    @JoinTable(
            name = "ia_component2part_detect",
            joinColumns = {@JoinColumn( name = "component_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "cvobject_ac" )}
    )
    @Target(IntactCvTerm.class)
    /**
     * Only this method is persistent because we want to exclude the participant identification method which is persisted at the experiment
     * level
     */
    protected Collection<CvTerm> getDbIdentificationMethods() {
        if (persistentIdentificationMethods == null){
            persistentIdentificationMethods = new ArrayList<CvTerm>(getIdentificationMethods());
            if (getInteraction() != null && getInteraction().getExperiment() instanceof IntactExperiment){
                IntactExperiment intactExperiment = (IntactExperiment) getInteraction().getExperiment();
                persistentIdentificationMethods.remove(intactExperiment.getParticipantIdentificationMethod());
            }
        }
        return persistentIdentificationMethods;
    }

    protected void setRelatedCausalRelationships(Collection<CausalRelationship> relatedCausalRelationships) {
        this.relatedCausalRelationships = relatedCausalRelationships;
    }

    protected void setRelatedRanges(Collection<Range> relatedRanges) {
        this.relatedRanges = relatedRanges;
    }

    protected class IdentificationMethodList extends AbstractListHavingProperties<CvTerm> {
        public IdentificationMethodList(){
            super();
        }

        @Override
        protected void processAddedObjectEvent(CvTerm added) {
            if (getInteraction() != null && getInteraction().getExperiment() instanceof IntactExperiment){
                IntactExperiment intactExperiment = (IntactExperiment) getInteraction().getExperiment();
                if (intactExperiment.getParticipantIdentificationMethod() != null && !added.equals(intactExperiment.getParticipantIdentificationMethod())){
                    getDbIdentificationMethods().add(added);
                }
            }
        }

        @Override
        protected void processRemovedObjectEvent(CvTerm removed) {
            getDbIdentificationMethods().remove(removed);
        }

        @Override
        protected void clearProperties() {
            getDbIdentificationMethods().clear();
        }
    }
}
