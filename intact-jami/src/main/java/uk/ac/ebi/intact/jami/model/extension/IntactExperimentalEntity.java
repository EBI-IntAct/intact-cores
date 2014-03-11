package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.*;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.model.Parameter;
import uk.ac.ebi.intact.jami.model.listener.ExperimentalRolesListener;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.CascadeType;
import javax.persistence.*;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Intact implementation of experimental entity
 *
 * NOTE: for backward compatibility, modelled entities and experimental entities are in the same table ia_component.
 * In the future, we will update that so we have two different tables : one for modelled entities and one for experimental entities.
 * When this is done, we would be able to remove the where clause attached to this entity.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15/01/14</pre>
 */
@EntityListeners(value = {ExperimentalRolesListener.class})
@javax.persistence.Entity
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
@Table(name = "ia_component")
@DiscriminatorColumn(name = "category", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("experimental_entity")
@Where(clause = "category = 'experimental_entity' or category = 'participant_evidence' or category = 'experimental_entity_pool'")
public class IntactExperimentalEntity extends AbstractIntactEntity<FeatureEvidence> implements ExperimentalEntity{

    private CvTerm experimentalRole;
    private Collection<CvTerm> identificationMethods;
    private Collection<CvTerm> experimentalPreparations;
    private Organism expressedIn;
    private Collection<Confidence> confidences;
    private Collection<Parameter> parameters;
    private Collection<CvTerm> experimentalRoles;

    protected IntactExperimentalEntity() {
        super();
    }

    public IntactExperimentalEntity(Interactor interactor, CvTerm participantIdentificationMethod) {
        super(interactor);
        if (participantIdentificationMethod != null){
            getIdentificationMethods().add(participantIdentificationMethod);
        }
    }

    public IntactExperimentalEntity(Interactor interactor, CvTerm bioRole, CvTerm participantIdentificationMethod) {
        super(interactor, bioRole);
        if (participantIdentificationMethod != null){
            getIdentificationMethods().add(participantIdentificationMethod);
        }
    }

    public IntactExperimentalEntity(Interactor interactor, Stoichiometry stoichiometry, CvTerm participantIdentificationMethod) {
        super(interactor, stoichiometry);
        if (participantIdentificationMethod != null){
            getIdentificationMethods().add(participantIdentificationMethod);
        }
    }

    public IntactExperimentalEntity(Interactor interactor, CvTerm bioRole, CvTerm expRole, CvTerm participantIdentificationMethod) {
        super(interactor, bioRole);
        this.experimentalRole = expRole;

        if (participantIdentificationMethod != null){
            getIdentificationMethods().add(participantIdentificationMethod);
        }
    }

    public IntactExperimentalEntity(Interactor interactor, CvTerm bioRole, CvTerm expRole, Stoichiometry stoichiometry, CvTerm participantIdentificationMethod) {
        super(interactor, bioRole, stoichiometry);
        this.experimentalRole = expRole;

        if (participantIdentificationMethod != null){
            getIdentificationMethods().add(participantIdentificationMethod);
        }
    }

    public IntactExperimentalEntity(Interactor interactor, CvTerm bioRole, CvTerm expRole, Organism expressedIn, CvTerm participantIdentificationMethod) {
        super(interactor, bioRole);
        this.experimentalRole = expRole;

        this.expressedIn = expressedIn;
        if (participantIdentificationMethod != null){
            getIdentificationMethods().add(participantIdentificationMethod);
        }
    }

    public IntactExperimentalEntity(Interactor interactor, CvTerm bioRole, CvTerm expRole, Stoichiometry stoichiometry, Organism expressedIn, CvTerm participantIdentificationMethod) {
        super(interactor, bioRole, stoichiometry);
        this.experimentalRole = expRole;

        this.expressedIn = expressedIn;
        if (participantIdentificationMethod != null){
            getIdentificationMethods().add(participantIdentificationMethod);
        }
    }

    public IntactExperimentalEntity(Interactor interactor) {
        super(interactor);
    }

    public IntactExperimentalEntity(Interactor interactor, Stoichiometry stoichiometry) {
        super(interactor, stoichiometry);
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = ExperimentalEntityXref.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(ExperimentalEntityXref.class)
    public Collection<Xref> getXrefs() {
        return super.getXrefs();
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = ExperimentalEntityAnnotation.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @JoinTable(
            name="ia_component2annot",
            joinColumns = @JoinColumn( name="component_ac"),
            inverseJoinColumns = @JoinColumn( name="annotation_ac")
    )
    @Target(ExperimentalEntityAnnotation.class)
    /**
     * WARNING: The join table is for backward compatibility with intact-core.
     * When intact-core will be removed, the join table would disappear wnd the relation would become
     * @JoinColumn(name="parent_ac", referencedColumnName="ac")
     */
    public Collection<Annotation> getAnnotations() {
        return super.getAnnotations();
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = ExperimentalEntityAlias.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(ExperimentalEntityAlias.class)
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

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn(name = "ia_experimentalrole_ac", referencedColumnName = "ac")
    @Target(IntactCvTerm.class)
    public CvTerm getExperimentalRole() {
        if (this.experimentalRole == null){
            this.experimentalRole = IntactUtils.createMIExperimentalRole(Participant.UNSPECIFIED_ROLE, Participant.UNSPECIFIED_ROLE_MI);
        }
        return this.experimentalRole;
    }

    public void setExperimentalRole(CvTerm expRole) {
        this.experimentalRole = expRole;
    }

    @ManyToMany(targetEntity = IntactCvTerm.class)
    @JoinTable(
            name = "ia_component2part_detect",
            joinColumns = {@JoinColumn( name = "component_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "cvobject_ac" )}
    )
    @Target(IntactCvTerm.class)
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
            cascade = {CascadeType.ALL}, targetEntity = ExperimentalEntityConfidence.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(ExperimentalEntityConfidence.class)
    public Collection<Confidence> getConfidences() {
        if (confidences == null){
            initialiseConfidences();
        }
        return this.confidences;
    }

    @OneToMany( orphanRemoval = true,
            cascade = {CascadeType.ALL}, targetEntity = ExperimentalEntityParameter.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(ExperimentalEntityParameter.class)
    public Collection<Parameter> getParameters() {
        if (parameters == null){
            initialiseParameters();
        }
        return this.parameters;
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
    public Collection<CvTerm> getExperimentalRoles() {
        if (this.experimentalRoles == null){
            this.experimentalRoles =  new ArrayList<CvTerm>();
        }
        return experimentalRoles;
    }

    @Override
    public String toString() {
        return super.toString() + (experimentalRole != null ? ", " + experimentalRole.toString() : "") + (expressedIn != null ? ", " + expressedIn.toString() : "");
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
        return Hibernate.isInitialized(getIdentificationMethods());
    }

    @Transient
    public boolean areExperimentalPreparationsInitialized(){
        return Hibernate.isInitialized(getExperimentalPreparations());
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

    protected void initialiseIdentificationMethods(){
        this.identificationMethods = new ArrayList<CvTerm>();
    }

    protected void setExperimentalRoles(Collection<CvTerm> experimentalRoles) {
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
}
