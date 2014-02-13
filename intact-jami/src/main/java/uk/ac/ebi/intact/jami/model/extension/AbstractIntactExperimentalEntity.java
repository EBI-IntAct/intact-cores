package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.model.Parameter;
import uk.ac.ebi.intact.jami.model.listener.ExperimentalRolesListener;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract class for experimental entity
 * NOTE: this class allows to have a common parent for all experimental entities and experimental participants
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>16/01/14</pre>
 */
@MappedSuperclass
@Entity
@EntityListeners(value = {ExperimentalRolesListener.class})
public abstract class AbstractIntactExperimentalEntity extends AbstractIntactEntity<FeatureEvidence> implements ExperimentalEntity{
    private CvTerm experimentalRole;
    private Collection<CvTerm> identificationMethods;
    private Collection<CvTerm> experimentalPreparations;
    private Organism expressedIn;
    private Collection<Confidence> confidences;
    private Collection<Parameter> parameters;
    private Collection<CvTerm> experimentalRoles;

    protected AbstractIntactExperimentalEntity() {
        super();
    }

    public AbstractIntactExperimentalEntity(Interactor interactor, CvTerm participantIdentificationMethod) {
        super(interactor);
        if (participantIdentificationMethod != null){
            getIdentificationMethods().add(participantIdentificationMethod);
        }
    }

    public AbstractIntactExperimentalEntity(Interactor interactor, CvTerm bioRole, CvTerm participantIdentificationMethod) {
        super(interactor, bioRole);
        if (participantIdentificationMethod != null){
            getIdentificationMethods().add(participantIdentificationMethod);
        }
    }

    public AbstractIntactExperimentalEntity(Interactor interactor, Stoichiometry stoichiometry, CvTerm participantIdentificationMethod) {
        super(interactor, stoichiometry);
        if (participantIdentificationMethod != null){
            getIdentificationMethods().add(participantIdentificationMethod);
        }
    }

    public AbstractIntactExperimentalEntity(Interactor interactor, CvTerm bioRole, CvTerm expRole, CvTerm participantIdentificationMethod) {
        super(interactor, bioRole);
        this.experimentalRole = expRole;

        if (participantIdentificationMethod != null){
            getIdentificationMethods().add(participantIdentificationMethod);
        }
    }

    public AbstractIntactExperimentalEntity(Interactor interactor, CvTerm bioRole, CvTerm expRole, Stoichiometry stoichiometry, CvTerm participantIdentificationMethod) {
        super(interactor, bioRole, stoichiometry);
        this.experimentalRole = expRole;

        if (participantIdentificationMethod != null){
            getIdentificationMethods().add(participantIdentificationMethod);
        }
    }

    public AbstractIntactExperimentalEntity(Interactor interactor, CvTerm bioRole, CvTerm expRole, Organism expressedIn, CvTerm participantIdentificationMethod) {
        super(interactor, bioRole);
        this.experimentalRole = expRole;

        this.expressedIn = expressedIn;
        if (participantIdentificationMethod != null){
            getIdentificationMethods().add(participantIdentificationMethod);
        }
    }

    public AbstractIntactExperimentalEntity(Interactor interactor, CvTerm bioRole, CvTerm expRole, Stoichiometry stoichiometry, Organism expressedIn, CvTerm participantIdentificationMethod) {
        super(interactor, bioRole, stoichiometry);
        this.experimentalRole = expRole;

        this.expressedIn = expressedIn;
        if (participantIdentificationMethod != null){
            getIdentificationMethods().add(participantIdentificationMethod);
        }
    }

    public AbstractIntactExperimentalEntity(Interactor interactor) {
        super(interactor);
    }

    public AbstractIntactExperimentalEntity(Interactor interactor, Stoichiometry stoichiometry) {
        super(interactor, stoichiometry);
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
    // TODO fetch proper cv term
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

    @OneToMany( mappedBy = "parent", orphanRemoval = true,
            cascade = {CascadeType.ALL}, targetEntity = ExperimentalEntityConfidence.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(ExperimentalEntityConfidence.class)
    public Collection<Confidence> getConfidences() {
        if (confidences == null){
            initialiseConfidences();
        }
        return this.confidences;
    }

    @OneToMany( mappedBy = "parent", orphanRemoval = true,
            cascade = {CascadeType.ALL}, targetEntity = ExperimentalEntityParameter.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(ExperimentalEntityParameter.class)
    public Collection<Parameter> getParameters() {
        if (parameters == null){
            initialiseParameters();
        }
        return this.parameters;
    }

    @ManyToMany(targetEntity = IntactCvTerm.class, fetch = FetchType.EAGER)
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

    @Override
    protected void setFeatures(Collection<FeatureEvidence> features) {
        super.setFeatures(features);
    }
}
