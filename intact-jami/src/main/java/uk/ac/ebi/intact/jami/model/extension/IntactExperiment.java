package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.model.AbstractIntactPrimaryObject;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Intact implementation of experiment
 *
 * NOTE: confidences of an experiment are transient and cannot be used in HQL queries
 * NOTE: The interaction evidences have the ownership of the relation between experiment and interactions. It means that to persist the relationship between interaction and experiment,
 * the property getExperiment in the interaction must be pointing to the right experiment. It is then recommended to use the provided addInteractionEvidence and removeInteractionEvidence methods to add/remove interactions
 * from the experiment
 * NOTE: The variable parameters have the ownership of the relation between experiment and variable parameters. It means that to persist the relationship between variable parameter and experiment,
 * the property getExperiment in the variable parameter must be pointing to the right experiment. It is then recommended to use the provided addVariableParameter and removeVariableParameter methods to add/remove variable parameters
 * from the experiment
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>16/01/14</pre>
 */
@Entity
@Table(name = "ia_experiment")
public class IntactExperiment extends AbstractIntactPrimaryObject implements Experiment{
    private String shortLabel;
    private Publication publication;
    private Collection<Xref> xrefs;
    private Collection<Annotation> annotations;
    private CvTerm interactionDetectionMethod;
    private Organism hostOrganism;
    private Collection<InteractionEvidence> interactions;

    private Collection<Confidence> confidences;
    private Collection<VariableParameter> variableParameters;
    private CvTerm participantIdentificationMethod;

    protected IntactExperiment(){
        super();
    }

    public IntactExperiment(Publication publication){

        this.publication = publication;
    }

    public IntactExperiment(Publication publication, CvTerm interactionDetectionMethod){

        this.publication = publication;
        this.interactionDetectionMethod = interactionDetectionMethod;
    }

    public IntactExperiment(Publication publication, CvTerm interactionDetectionMethod, Organism organism){
        this(publication, interactionDetectionMethod);
        this.hostOrganism = organism;
    }

    @Column(name = "shortlabel", nullable = false, unique = true)
    @Size( min = 1, max = IntactUtils.MAX_SHORT_LABEL_LEN )
    @NotNull
    public String getShortLabel() {
        return this.shortLabel;
    }

    /**
     *
     * @param shortName
     */
    public void setShortLabel(String shortName) {
        this.shortLabel = shortName;
    }

    @ManyToOne(targetEntity = IntactPublication.class)
    @JoinColumn( name = "publication_ac", referencedColumnName = "ac")
    @Target(IntactPublication.class)
    public Publication getPublication() {
        return this.publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public void setPublicationAndAddExperiment(Publication publication) {
        if (this.publication != null){
            this.publication.removeExperiment(this);
        }

        if (publication != null){
            publication.addExperiment(this);
        }
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = ExperimentXref.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(ExperimentXref.class)
    public Collection<Xref> getXrefs() {
        if (xrefs == null){
            initialiseXrefs();
        }
        return this.xrefs;
    }

    private void setXrefs(Collection<Xref> xrefs) {
        this.xrefs = xrefs;
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = ExperimentAnnotation.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @JoinTable(
            name="ia_exp2annot",
            joinColumns = @JoinColumn( name="experiment_ac"),
            inverseJoinColumns = @JoinColumn( name="annotation_ac")
    )
    @Target(ExperimentAnnotation.class)
    /**
     * WARNING: The join table is for backward compatibility with intact-core.
     * When intact-core will be removed, the join table would disappear wnd the relation would become
     * @JoinColumn(name="parent_ac", referencedColumnName="ac")
     */
    public Collection<Annotation> getAnnotations() {
        if (annotations == null){
            initialiseAnnotations();
        }
        return this.annotations;
    }

    private void setAnnotations(Collection<Annotation> annotations) {
        this.annotations = annotations;
    }

    @Transient
    public Collection<Confidence> getConfidences() {
        if (confidences == null){
            initialiseConfidences();
        }
        return confidences;
    }

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn( name = "detectmethod_ac", referencedColumnName = "ac")
    @Target(IntactCvTerm.class)
    public CvTerm getInteractionDetectionMethod() {
        if (this.interactionDetectionMethod == null){
           this.interactionDetectionMethod = IntactUtils.createMIInteractionDetectionMethod(Experiment.UNSPECIFIED_METHOD, Experiment.UNSPECIFIED_METHOD_MI);
        }
        return this.interactionDetectionMethod;
    }

    public void setInteractionDetectionMethod(CvTerm term) {
        this.interactionDetectionMethod = term;
    }

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn( name = "identmethod_ac", referencedColumnName = "ac")
    @Target(IntactCvTerm.class)
    /**
     * This method should be avoided and use the getIdentificationMethods of each participant instead
     */
    public CvTerm getParticipantIdentificationMethod() {
        return participantIdentificationMethod;
    }

    public void setParticipantIdentificationMethod(CvTerm participantIdentificationMethod) {
        this.participantIdentificationMethod = participantIdentificationMethod;
    }

    @ManyToOne(targetEntity = IntactOrganism.class)
    @JoinColumn( name = "biosource_ac", referencedColumnName = "ac")
    @Target(IntactOrganism.class)
    public Organism getHostOrganism() {
        return this.hostOrganism;
    }

    public void setHostOrganism(Organism organism) {
        this.hostOrganism = organism;
    }

    @ManyToMany( mappedBy = "dbExperiments", targetEntity = IntactInteractionEvidence.class, cascade = CascadeType.ALL)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(IntactInteractionEvidence.class)
    /**
     * NOTE: ManyToMany relatiosnhip for backward compatibility with intact-core only. In the future, it should be oneToMany
     */
    public Collection<InteractionEvidence> getInteractionEvidences() {
        if (interactions == null){
            initialiseInteractions();
        }
        return this.interactions;
    }

    private void setInteractionEvidences(Collection<InteractionEvidence> interactions) {
        this.interactions = interactions;
    }

    public boolean addInteractionEvidence(InteractionEvidence evidence) {
        if (evidence == null){
            return false;
        }

        if (getInteractionEvidences().add(evidence)){
            evidence.setExperiment(this);
            return true;
        }
        return false;
    }

    public boolean removeInteractionEvidence(InteractionEvidence evidence) {
        if (evidence == null){
            return false;
        }

        if (getInteractionEvidences().remove(evidence)){
            evidence.setExperiment(null);
            return true;
        }
        return false;
    }

    public boolean addAllInteractionEvidences(Collection<? extends InteractionEvidence> evidences) {
        if (evidences == null){
            return false;
        }

        boolean added = false;
        for (InteractionEvidence ev : evidences){
            if (addInteractionEvidence(ev)){
                added = true;
            }
        }
        return added;
    }

    public boolean removeAllInteractionEvidences(Collection<? extends InteractionEvidence> evidences) {
        if (evidences == null){
            return false;
        }

        boolean removed = false;
        for (InteractionEvidence ev : evidences){
            if (removeInteractionEvidence(ev)){
                removed = true;
            }
        }
        return removed;
    }

    @OneToMany( mappedBy = "experiment", targetEntity = IntactVariableParameter.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(IntactVariableParameter.class)
    public Collection<VariableParameter> getVariableParameters() {
        if (variableParameters == null){
            initialiseVariableParameters();
        }
        return variableParameters;
    }

    private void setVariableParameters(Collection<VariableParameter> variableParameters) {
        this.variableParameters = variableParameters;
    }

    public boolean addVariableParameter(VariableParameter variableParameter) {
        if (variableParameter == null){
            return false;
        }

        if (getVariableParameters().add(variableParameter)){
            variableParameter.setExperiment(this);
            return true;
        }
        return false;
    }

    public boolean removeVariableParameter(VariableParameter variableParameter) {
        if (variableParameter == null){
            return false;
        }

        if (getVariableParameters().remove(variableParameter)){
            variableParameter.setExperiment(null);
            return true;
        }
        return false;
    }

    public boolean addAllVariableParameters(Collection<? extends VariableParameter> variableParameters) {
        if (variableParameters == null){
            return false;
        }

        boolean added = false;
        for (VariableParameter param : variableParameters){
            if (addVariableParameter(param)){
                added = true;
            }
        }
        return added;
    }

    public boolean removeAllVariableParameters(Collection<? extends VariableParameter> variableParameters) {
        if (variableParameters == null){
            return false;
        }

        boolean removed = false;
        for (VariableParameter param : variableParameters){
            if (removeVariableParameter(param)){
                removed = true;
            }
        }
        return removed;
    }

    @Override
    public String toString() {
        return publication.toString() + "( " + interactionDetectionMethod.toString() + (hostOrganism != null ? ", " + hostOrganism.toString() : "") + " )";
    }

    @Transient
    public boolean areXrefsInitialized() {
        return Hibernate.isInitialized(getXrefs());
    }

    @Transient
    public boolean areAnnotationsInitialized() {
        return Hibernate.isInitialized(getAnnotations());
    }

    @Transient
    public boolean areInteractionEvidencesInitialized() {
        return Hibernate.isInitialized(getInteractionEvidences());
    }

    @Transient
    public boolean areVariableParametersInitialized() {
        return Hibernate.isInitialized(getVariableParameters());
    }

    protected void initialiseXrefs() {
        this.xrefs = new ArrayList<Xref>();
    }

    protected void initialiseAnnotations() {
        this.annotations = new ArrayList<Annotation>();
    }

    protected void initialiseInteractions() {
        this.interactions = new ArrayList<InteractionEvidence>();
    }

    protected void initialiseConfidences() {
        this.confidences = new ArrayList<Confidence>();
    }

    protected void initialiseVariableParameters() {
        this.variableParameters = new ArrayList<VariableParameter>();
    }
}
