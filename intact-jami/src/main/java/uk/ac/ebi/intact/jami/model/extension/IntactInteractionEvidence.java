package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.model.Parameter;
import psidev.psi.mi.jami.model.impl.DefaultChecksum;
import psidev.psi.mi.jami.model.impl.DefaultCvTerm;
import psidev.psi.mi.jami.model.impl.DefaultXref;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import psidev.psi.mi.jami.utils.ChecksumUtils;
import psidev.psi.mi.jami.utils.CvTermUtils;
import psidev.psi.mi.jami.utils.XrefUtils;
import psidev.psi.mi.jami.utils.collection.AbstractCollectionWrapper;
import psidev.psi.mi.jami.utils.collection.AbstractListHavingProperties;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.context.IntactContext;
import uk.ac.ebi.intact.jami.model.AbstractIntactPrimaryObject;
import uk.ac.ebi.intact.jami.model.listener.InteractionExperimentListener;
import uk.ac.ebi.intact.jami.model.listener.InteractionParameterListener;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Intact implementation of interaction evidence
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17/01/14</pre>
 */
@Entity
@Table(name = "ia_interaction")
@EntityListeners(value = {InteractionExperimentListener.class, InteractionParameterListener.class})
public class IntactInteractionEvidence extends AbstractIntactPrimaryObject implements InteractionEvidence{
    private Xref imexId;
    private Experiment experiment;
    private String availability;
    private Collection<Parameter> parameters;
    private boolean isInferred = false;
    private Collection<Confidence> confidences;
    private boolean isNegative;

    private Collection<VariableParameterValueSet> variableParameterValueSets;
    private String shortName;
    private Checksum rigid;
    private Collection<Checksum> checksums;
    private InteractionIdentifierList identifiers;
    private InteractionXrefList xrefs;
    private Collection<Annotation> annotations;
    private CvTerm interactionType;
    private Collection<ParticipantEvidence> participants;

    private PersistentXrefList persistentXrefs;
    private Collection<Experiment> experiments;

    private Xref acRef;

    public IntactInteractionEvidence(){
    }

    public IntactInteractionEvidence(String shortName){
        this.shortName = shortName;
    }

    public IntactInteractionEvidence(String shortName, CvTerm type){
        this(shortName);
        this.interactionType = type;
    }

    @Override
    public void setAc(String ac) {
        super.setAc(ac);
        // only if identifiers are initialised
        if (this.acRef != null && !this.acRef.getId().equals(ac)){
            // we don't want to create a persistent xref
            Xref newRef = new DefaultXref(this.acRef.getDatabase(), ac, this.acRef.getQualifier());
            this.identifiers.removeOnly(acRef);
            this.acRef = newRef;
            this.identifiers.addOnly(acRef);
        }
    }

    @Column(name = "shortlabel", nullable = false, unique = true)
    @Size( min = 1, max = IntactUtils.MAX_SHORT_LABEL_LEN )
    @NotNull
    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String name) {
        this.shortName = name != null ? name.toLowerCase().trim() : name;
    }

    @Transient
    public String getRigid() {
        // initialise checksums if not done
        getChecksums();
        return this.rigid != null ? this.rigid.getValue() : null;
    }

    public void setRigid(String rigid) {
        Collection<Checksum> checksums = getChecksums();
        if (rigid != null){
            CvTerm rigidMethod = IntactUtils.createMITopic(Checksum.RIGID, null);
            // first remove old rigid
            if (this.rigid != null){
                checksums.remove(this.rigid);
            }
            this.rigid = new DefaultChecksum(rigidMethod, rigid);
            checksums.add(this.rigid);
        }
        // remove all smiles if the collection is not empty
        else if (!checksums.isEmpty()) {
            ChecksumUtils.removeAllChecksumWithMethod(checksums, Checksum.RIGID_MI, Checksum.RIGID);
            this.rigid = null;
        }
    }

    @Transient
    public Collection<Xref> getIdentifiers() {
        if (identifiers == null){
            initialiseXrefs();
        }
        return this.identifiers;
    }

    @Transient
    public Collection<Xref> getXrefs() {
        if (xrefs == null){
            initialiseXrefs();
        }
        return this.xrefs;
    }

    @Transient
    public Collection<Checksum> getChecksums() {
        if (checksums == null){
            initialiseChecksums();
        }
        return this.checksums;
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = InteractionAnnotation.class)
    @JoinTable(
            name="ia_int2annot",
            joinColumns = @JoinColumn( name="interactor_ac"),
            inverseJoinColumns = @JoinColumn( name="annotation_ac")
    )
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(InteractionAnnotation.class)
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

    @Transient
    public Date getUpdatedDate() {
        return getUpdated();
    }

    public void setUpdatedDate(Date updated) {
        setUpdated(updated);
    }

    @Transient
    public Date getCreatedDate() {
        return getCreated();
    }

    public void setCreatedDate(Date created) {
        setCreated(created);
    }

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn( name = "interactiontype_ac", referencedColumnName = "ac")
    @Target(IntactCvTerm.class)
    public CvTerm getInteractionType() {
        return this.interactionType;
    }

    public void setInteractionType(CvTerm term) {
        this.interactionType = term;
    }

    @OneToMany( mappedBy = "interaction", orphanRemoval = true,
            cascade = {CascadeType.ALL}, targetEntity = IntactParticipantEvidence.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(IntactParticipantEvidence.class)
    public Collection<ParticipantEvidence> getParticipants() {
        if (participants == null){
            initialiseParticipants();
        }
        return participants;
    }

    public boolean addParticipant(ParticipantEvidence part) {
        if (part == null){
            return false;
        }
        if (getParticipants().add(part)){
            part.setInteraction(this);
            return true;
        }
        return false;
    }

    public boolean removeParticipant(ParticipantEvidence part) {
        if (part == null){
            return false;
        }
        if (getParticipants().remove(part)){
            part.setInteraction(null);
            return true;
        }
        return false;
    }

    public boolean addAllParticipants(Collection<? extends ParticipantEvidence> participants) {
        if (participants == null){
            return false;
        }

        boolean added = false;
        for (ParticipantEvidence p : participants){
            if (addParticipant(p)){
                added = true;
            }
        }
        return added;
    }

    public boolean removeAllParticipants(Collection<? extends ParticipantEvidence> participants) {
        if (participants == null){
            return false;
        }

        boolean removed = false;
        for (ParticipantEvidence p : participants){
            if (removeParticipant(p)){
                removed = true;
            }
        }
        return removed;
    }

    @Override
    public String toString() {
        return (shortName != null ? shortName+", " : "") + (interactionType != null ? interactionType.toString() : "");
    }

    @Transient
    public String getImexId() {
        // initialise xrefs if not done yet
        getXrefs();
        return this.imexId != null ? this.imexId.getId() : null;
    }

    public void assignImexId(String identifier) {
        // add new imex if not null
        if (identifier != null){
            Collection<Xref> interactionXrefs = getXrefs();
            CvTerm imexDatabase = IntactUtils.createMIDatabase(Xref.IMEX, Xref.IMEX_MI);
            CvTerm imexPrimaryQualifier = IntactUtils.createMIQualifier(Xref.IMEX_PRIMARY, Xref.IMEX_PRIMARY_MI);
            // first remove old doi if not null
            if (this.imexId != null){
                interactionXrefs.remove(this.imexId);
            }
            this.imexId = new InteractionXref(imexDatabase, identifier, imexPrimaryQualifier);
            interactionXrefs.add(this.imexId);
        }
        else {
            throw new IllegalArgumentException("The imex id has to be non null.");
        }
    }

    @ManyToOne(targetEntity = IntactExperiment.class)
    @JoinColumn( name = "experiment_ac", referencedColumnName = "ac")
    @Target(IntactExperiment.class)
    public Experiment getExperiment() {
        return this.experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public void setExperimentAndAddInteractionEvidence(Experiment experiment) {
        if (this.experiment != null){
            this.experiment.removeInteractionEvidence(this);
        }

        if (experiment != null){
            experiment.addInteractionEvidence(this);
        }
    }

    @OneToMany(targetEntity=IntactVariableParameterValueSet.class, cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(IntactVariableParameterValueSet.class)
    public Collection<VariableParameterValueSet> getVariableParameterValues() {

        if (variableParameterValueSets == null){
            initialiseVariableParameterValueSets();
        }
        return this.variableParameterValueSets;
    }

    @OneToMany( orphanRemoval = true,
            cascade = {CascadeType.ALL}, targetEntity = InteractionEvidenceConfidence.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(InteractionEvidenceConfidence.class)
    public Collection<Confidence> getConfidences() {
        if (confidences == null){
            initialiseExperimentalConfidences();
        }
        return this.confidences;
    }

    @Transient
    public String getAvailability() {
        return this.availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public boolean isNegative() {
        return this.isNegative;
    }

    public void setNegative(boolean negative) {
        this.isNegative = negative;

        if (!isNegative()){
            AnnotationUtils.removeAllAnnotationsWithTopic(getAnnotations(), null, "negative");
        }
        else{
            getAnnotations().add(new InteractionAnnotation(IntactUtils.createMITopic("negative", null)));
        }
    }

    @OneToMany( orphanRemoval = true,
            cascade = {CascadeType.ALL}, targetEntity = InteractionEvidenceParameter.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(InteractionEvidenceParameter.class)
    public Collection<Parameter> getParameters() {
        if (parameters == null){
            initialiseExperimentalParameters();
        }
        return this.parameters;
    }

    @Transient
    public boolean isInferred() {
        return this.isInferred;
    }

    public void setInferred(boolean inferred) {
        this.isInferred = inferred;
    }

    @ManyToMany(targetEntity = IntactExperiment.class)
    @JoinTable(
            name = "ia_int2exp",
            joinColumns = {@JoinColumn( name = "interaction_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "experiment_ac" )}
    )
    @Target(IntactExperiment.class)
    @Deprecated
    /**
     * @deprecated see getExperiment instead. Only kept for backward compatibility with intact core
     */
    public Collection<Experiment> getExperiments() {
        if (experiments == null){
            experiments = new ArrayList<Experiment>();
        }
        return experiments;
    }

    @Transient
    public boolean areVariableParameterValuesInitialized(){
        return Hibernate.isInitialized(getVariableParameterValues());
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
    public boolean areXrefsInitialized(){
        return Hibernate.isInitialized(getPersistentXrefs());
    }

    @Transient
    public boolean areAnnotationsInitialized(){
        return Hibernate.isInitialized(getAnnotations());
    }

    @Transient
    public boolean areParticipantsInitialized(){
        return Hibernate.isInitialized(getParticipants());
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = InteractionXref.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(InteractionXref.class)
    public Collection<Xref> getPersistentXrefs() {
        if (persistentXrefs == null){
            persistentXrefs = new PersistentXrefList(null);
        }
        return persistentXrefs.getWrappedList();
    }

    protected void processAddedChecksumEvent(Checksum added) {
        if (rigid == null && ChecksumUtils.doesChecksumHaveMethod(added, Checksum.RIGID_MI, Checksum.RIGID)){
            // the rigid is not set, we can set the rigid
            rigid = added;
        }
    }

    protected void processRemovedChecksumEvent(Checksum removed) {
        if (rigid == removed){
            rigid = ChecksumUtils.collectFirstChecksumWithMethod(getChecksums(), Checksum.RIGID_MI, Checksum.RIGID);
        }
    }

    protected void initialiseExperimentalConfidences(){
        this.confidences = new ArrayList<Confidence>();
    }

    protected void initialiseVariableParameterValueSets(){
        this.variableParameterValueSets = new ArrayList<VariableParameterValueSet>();
    }

    protected void initialiseExperimentalParameters(){
        this.parameters = new ArrayList<Parameter>();
    }

    protected void processAddedXrefEvent(Xref added) {

        // the added identifier is imex and the current imex is not set
        if (imexId == null && XrefUtils.isXrefFromDatabase(added, Xref.IMEX_MI, Xref.IMEX)){
            // the added xref is imex-primary
            if (XrefUtils.doesXrefHaveQualifier(added, Xref.IMEX_PRIMARY_MI, Xref.IMEX_PRIMARY)){
                imexId = added;
            }
        }
    }

    protected void processRemovedXrefEvent(Xref removed) {
        // the removed identifier is pubmed
        if (imexId != null && imexId.equals(removed)){
            imexId = null;
        }
    }

    private void clearPropertiesLinkedToXrefs() {
        imexId = null;
    }

    private void initialiseAnnotations(){
        this.annotations = new ArrayList<Annotation>();
    }

    private void initialiseParticipants(){
        this.participants = new ArrayList<ParticipantEvidence>();
    }

    private void initialiseChecksums(){
        this.checksums = new ArrayList<Checksum>();
    }

    private void initialiseXrefs(){
        this.identifiers = new InteractionIdentifierList();
        this.xrefs = new InteractionXrefList();
        if (this.persistentXrefs != null){
            for (Xref ref : this.persistentXrefs){
                if (XrefUtils.isXrefAnIdentifier(ref)){
                    this.identifiers.addOnly(ref);
                }
                else{
                    this.xrefs.addOnly(ref);
                    processAddedXrefEvent(ref);
                }
            }
        }
        else{
            this.persistentXrefs = new PersistentXrefList(null);
        }

        // initialise ac
        if (getAc() != null){
            IntactContext intactContext = ApplicationContextProvider.getBean("intactContext");
            if (intactContext != null){
                this.acRef = new DefaultXref(intactContext.getConfig().getDefaultInstitution(), getAc(), CvTermUtils.createIdentityQualifier());
            }
            else{
                this.acRef = new DefaultXref(new DefaultCvTerm("unknwon"), getAc(), CvTermUtils.createIdentityQualifier());
            }
            this.identifiers.addOnly(this.acRef);
        }
    }

    @Column(name = "objclass", nullable = false, insertable = false, updatable = false)
    @NotNull
    private String getObjClass() {
        return "uk.ac.ebi.intact.model.InteractionImpl";
    }

    private void setObjClass(String value){
        // nothing to do
    }

    private void setPersistentXrefs(Collection<Xref> persistentXrefs) {
        this.persistentXrefs = new PersistentXrefList(persistentXrefs);
    }

    private void setAnnotations(Collection<Annotation> annotations) {
        this.annotations = annotations;
    }

    private void setExperiments(Collection<Experiment> experiments) {
        this.experiments = experiments;
    }

    private void setVariableParameterValues(Collection<VariableParameterValueSet> variableParameterValueSets) {
        this.variableParameterValueSets = variableParameterValueSets;
    }

    private void setConfidences(Collection<Confidence> confidences) {
        this.confidences = confidences;
    }

    private void setParameters(Collection<Parameter> parameters) {
        this.parameters = parameters;
    }

    private void setParticipants(Collection<ParticipantEvidence> participants) {
        this.participants = participants;
    }

    /**
     * Experimental interaction identifier list
     */
    private class InteractionIdentifierList extends AbstractListHavingProperties<Xref> {
        public InteractionIdentifierList(){
            super();
        }

        @Override
        protected void processAddedObjectEvent(Xref added) {
            if (!added.equals(acRef)){
                persistentXrefs.add(added);
            }
        }

        @Override
        protected void processRemovedObjectEvent(Xref removed) {
            if (!removed.equals(acRef)){
                persistentXrefs.remove(removed);
            }
            else{
                super.addOnly(acRef);
                throw new UnsupportedOperationException("Cannot remove the database accession of an Interaction object from its list of identifiers.");
            }
        }

        @Override
        protected void clearProperties() {
            persistentXrefs.retainAll(getXrefs());

        }
    }

    /**
     * Experimental interaction Xref list
     */
    private class InteractionXrefList extends AbstractListHavingProperties<Xref> {
        public InteractionXrefList(){
            super();
        }

        @Override
        protected void processAddedObjectEvent(Xref added) {

            processAddedXrefEvent(added);
            persistentXrefs.add(added);
        }

        @Override
        protected void processRemovedObjectEvent(Xref removed) {
            processRemovedXrefEvent(removed);
            persistentXrefs.remove(removed);
        }

        @Override
        protected void clearProperties() {
            clearPropertiesLinkedToXrefs();
            persistentXrefs.retainAll(getIdentifiers());
        }
    }

    private class PersistentXrefList extends AbstractCollectionWrapper<Xref> {

        public PersistentXrefList(Collection<Xref> persistentBag){
            super(persistentBag);
        }

        @Override
        protected boolean needToPreProcessElementToAdd(Xref added) {
            return false;
        }

        @Override
        protected Xref processOrWrapElementToAdd(Xref added) {
            return added;
        }

        @Override
        protected void processElementToRemove(Object o) {
            // do nothing
        }

        @Override
        protected boolean needToPreProcessElementToRemove(Object o) {
            return false;
        }
    }

}
