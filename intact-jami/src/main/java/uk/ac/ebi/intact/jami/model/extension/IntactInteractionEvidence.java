package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.*;
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
import uk.ac.ebi.intact.jami.model.listener.InteractionParameterListener;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

/**
 * Intact implementation of interaction evidence
 *
 * NOTE: for backward compatibility with intact-core, interaction evidences are stored in same table as interactors.
 * When intact-core is removed, it may be good to move all interaction evidences to a separate table, remove the property 'category'
 * and remove the where clause attached to this entity
 * NOTE: for backward compatibility with intact-core, getObjClass is a property that cannot be inserted nor updated. When intact-core is removed, this property can be removed as well
 * NOTE: checksums are not persistent and cannot be used in HQL queries
 * NOTE: getIdentifiers and getXrefs are not persistent methods annotated with hibernate annotations. All the xrefs present in identifiers
 * and xrefs are persisted in the same table for backward compatibility with intact-core. So the persistent xrefs are available with the getDbXrefs method.
 * For HQL queries, the method getDbXrefs should be used because is annotated with hibernate annotations.
 * However, getDbXrefs should not be used directly to add/remove xrefs because it could mess up with the state of the object. Only the synchronizers
 * can use it this way before persistence.
 * NOTE: getCreatedDate and getUpdatedDate are transient methods as the AbstractIntactAudit parent class contains the relevant persistent audit methods.
 * NOTE: The participants have the ownership of the relation between participant and interaction. It means that to persist the relationship between participant and interaction,
 * the property getInteraction in the participant must be pointing to the right interaction. It is then recommended to use the provided addParticipant and removeParticipant methods to add/remove participants
 * from the interaction
 * NOTE: The interaction evidences have the ownership of the relation between experiment and interactions. It means that to persist the relationship between interaction and experiment,
 * the property getExperiment in the interaction must be pointing to the right experiment. It is then recommended to use the provided addInteractionEvidence and removeInteractionEvidence methods to add/remove interactions
 * from the experiment
 * NOTE: getAvailability is transient and cannot be used in HQL
 * NOTE: isInferred is transient and cannot be used in HQL
 * NOTE: getAnnotations is not persistent. For HQL queries, the method getDbAnnotations should be used because is annotated with hibernate annotations.
 * However, getDbAnnotations should not be used directly to add/remove annotations because it could mess up with the state of the object. Only the synchronizers
 * can use it this way before persistence. The access type of DbAnnotations is private as it does not have to be used by the synchronizers neither.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17/01/14</pre>
 */
@Entity
@Table(name = "ia_interactor")
@EntityListeners(value = {InteractionParameterListener.class})
@Where(clause = "category = 'interaction_evidence'")
public class IntactInteractionEvidence extends AbstractIntactPrimaryObject implements InteractionEvidence{
    private Xref imexId;
    private String availability;
    private Collection<Parameter> parameters;
    private boolean isInferred = false;
    private Collection<Confidence> confidences;
    private Annotation isNegative;

    private Collection<VariableParameterValueSet> variableParameterValueSets;
    private String shortName;
    private Checksum rigid;
    private InteractionChecksumList checksums;
    private InteractionIdentifierList identifiers;
    private InteractionXrefList xrefs;
    private InteractionAnnotationList annotations;
    private CvTerm interactionType;
    private Collection<ParticipantEvidence> participants;

    private PersistentXrefList persistentXrefs;
    private PersistentAnnotationList persistentAnnotations;

    /**
     * @deprecated
     */
    @Deprecated
    private List<Experiment> experiments;

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

    @Transient
    /**
     * WARNING: The property is transient for backward compatibility with intact-core. When it is removed, getDbAnnotations and getAnnotations should be the same
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

    @OneToMany( mappedBy = "dbParentInteraction", orphanRemoval = true,
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

    /**
     * NOTE: it is transient for backward compatibility with intact-core. In the future, it should be
     * @ManyToOne(targetEntity = IntactExperiment.class)
     * @JoinColumn( name = "experiment_ac", referencedColumnName = "ac")
     * @Target(IntactExperiment.class)
     */
    @Transient
    public Experiment getExperiment() {
        // the experimental role list is never empty
        if (getDbExperiments().isEmpty()){
            return null;
        }
        return this.experiments.iterator().next();
    }

    public void setExperiment(Experiment experiment) {
        if (!getDbExperiments().isEmpty()){
            getDbExperiments().remove(0);
        }

        if (experiment != null){
            this.experiments.add(0, experiment);
        }
    }

    public void setExperimentAndAddInteractionEvidence(Experiment experiment) {
        if (getExperiment() != null){
            getExperiment().removeInteractionEvidence(this);
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
    @JoinColumn(name="interaction_ac", referencedColumnName="ac")
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

    @Transient
    public boolean isNegative() {
        if (this.annotations == null){
            initialiseAnnotations();
        }
        return this.isNegative != null ? true : false;
    }

    public void setNegative(boolean negative) {
        if (!negative){
            AnnotationUtils.removeAllAnnotationsWithTopic(getDbAnnotations(), null, "negative");
            this.isNegative = null;
        }
        else if (this.isNegative == null){
            this.isNegative = new InteractionAnnotation(IntactUtils.createMITopic("negative", null));
            getDbAnnotations().add(this.isNegative);
        }
    }

    @OneToMany( orphanRemoval = true,
            cascade = {CascadeType.ALL}, targetEntity = InteractionEvidenceParameter.class)
    @JoinColumn(name="interaction_ac", referencedColumnName="ac")
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
        return Hibernate.isInitialized(getDbXrefs());
    }

    @Transient
    public boolean areAnnotationsInitialized(){
        return Hibernate.isInitialized(getDbAnnotations());
    }

    @Transient
    public boolean areParticipantsInitialized(){
        return Hibernate.isInitialized(getParticipants());
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = InteractionXref.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(InteractionXref.class)
    public Collection<Xref> getDbXrefs() {
        if (persistentXrefs == null){
            persistentXrefs = new PersistentXrefList(null);
        }
        return persistentXrefs.getWrappedList();
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
    public Collection<Annotation> getDbAnnotations() {
        if (persistentAnnotations == null){
            this.persistentAnnotations = new PersistentAnnotationList(null);
        }
        return this.persistentAnnotations;
    }

    @Column(name = "category", nullable = false, updatable = false)
    @NotNull
    protected String getCategory() {
        return "interaction_evidence";
    }

    @Column(name = "objclass", nullable = false, updatable = false)
    @NotNull
    protected String getObjClass() {
        return "uk.ac.ebi.intact.model.InteractionImpl";
    }

    @ManyToMany(targetEntity = IntactExperiment.class)
    @JoinTable(
            name = "ia_int2exp",
            joinColumns = {@JoinColumn( name = "interaction_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "experiment_ac" )}
    )
    @Target(IntactExperiment.class)
    @Deprecated
    @LazyCollection(LazyCollectionOption.FALSE)
    /**
     * @deprecated see getExperiment instead. Only kept for backward compatibility with intact core
     */
    protected List<Experiment> getDbExperiments() {
        if (experiments == null){
            experiments = new ArrayList<Experiment>();
        }
        return experiments;
    }

    private void processAddedChecksumEvent(Checksum added) {
        if (rigid == null && ChecksumUtils.doesChecksumHaveMethod(added, Checksum.RIGID_MI, Checksum.RIGID)){
            // the rigid is not set, we can set the rigid
            rigid = added;
        }
    }

    private void processRemovedChecksumEvent(Checksum removed) {
        if (rigid == removed){
            rigid = ChecksumUtils.collectFirstChecksumWithMethod(getChecksums(), Checksum.RIGID_MI, Checksum.RIGID);
        }
    }

    private void clearPropertiesLinkedToChecksums() {
        this.rigid = null;
    }

    private void initialiseExperimentalConfidences(){
        this.confidences = new ArrayList<Confidence>();
    }

    private void initialiseVariableParameterValueSets(){
        this.variableParameterValueSets = new ArrayList<VariableParameterValueSet>();
    }

    private void initialiseExperimentalParameters(){
        this.parameters = new ArrayList<Parameter>();
    }

    private void processAddedXrefEvent(Xref added) {

        // the added identifier is imex and the current imex is not set
        if (imexId == null && XrefUtils.isXrefFromDatabase(added, Xref.IMEX_MI, Xref.IMEX)){
            // the added xref is imex-primary
            if (XrefUtils.doesXrefHaveQualifier(added, Xref.IMEX_PRIMARY_MI, Xref.IMEX_PRIMARY)){
                imexId = added;
            }
        }
    }

    private void processRemovedXrefEvent(Xref removed) {
        // the removed identifier is pubmed
        if (imexId != null && imexId.equals(removed)){
            imexId = null;
        }
    }

    private void clearPropertiesLinkedToXrefs() {
        imexId = null;
    }

    private void initialiseAnnotations(){
        this.annotations = new InteractionAnnotationList();
        this.isNegative = null;
        if (this.persistentAnnotations != null){
            for (Annotation annot : this.persistentAnnotations){
                if (AnnotationUtils.doesAnnotationHaveTopic(annot, null, "negative")){
                    isNegative = annot;
                }
                else{
                    this.annotations.addOnly(annot);
                }
            }
        }
        else{
            this.persistentAnnotations = new PersistentAnnotationList(null);
        }
    }

    private void initialiseParticipants(){
        this.participants = new ArrayList<ParticipantEvidence>();
    }

    private void initialiseChecksums(){
        this.checksums = new InteractionChecksumList();
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
            IntactContext intactContext = ApplicationContextProvider.getBean("intactJamiContext", IntactContext.class);
            if (intactContext != null){
                this.acRef = new DefaultXref(intactContext.getIntactConfiguration().getDefaultInstitution(), getAc(), CvTermUtils.createIdentityQualifier());
            }
            else{
                this.acRef = new DefaultXref(new DefaultCvTerm("unknwon"), getAc(), CvTermUtils.createIdentityQualifier());
            }
            this.identifiers.addOnly(this.acRef);
        }
    }

    private void setObjClass(String value){
        // nothing to do
    }

    private void setCategory(String value){
        // nothing to do
    }

    private void setDbXrefs(Collection<Xref> persistentXrefs) {
        if (persistentXrefs instanceof PersistentXrefList){
            this.persistentXrefs = (PersistentXrefList)persistentXrefs;
        }
        else{
            this.persistentXrefs = new PersistentXrefList(persistentXrefs);
        }
        this.identifiers = null;
        this.xrefs = null;
    }

    private void setDbAnnotations(Collection<Annotation> annotations) {
        if (annotations instanceof PersistentAnnotationList){
            this.persistentAnnotations = (PersistentAnnotationList)annotations;
        }
        else{
            this.persistentAnnotations = new PersistentAnnotationList(annotations);
        }
        this.annotations = null;
        this.isNegative = null;
    }

    private void setDbExperiments(List<Experiment> experiments) {
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
            if (acRef != null){
                super.addOnly(acRef);
            }

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

    private class InteractionAnnotationList extends AbstractListHavingProperties<Annotation> {
        public InteractionAnnotationList(){
            super();
        }

        @Override
        protected void processAddedObjectEvent(Annotation added) {
            persistentAnnotations.add(added);
        }

        @Override
        protected void processRemovedObjectEvent(Annotation removed) {
            persistentAnnotations.remove(removed);
        }

        @Override
        protected void clearProperties() {
            if (isNegative == null){
                persistentAnnotations.clear();
            }
            else{
                persistentAnnotations.retainAll(Collections.singleton(isNegative));
            }
        }
    }

    private class PersistentAnnotationList extends AbstractCollectionWrapper<Annotation> {

        public PersistentAnnotationList(Collection<Annotation> persistentBag){
            super(persistentBag);
        }

        @Override
        protected boolean needToPreProcessElementToAdd(Annotation added) {
            return false;
        }

        @Override
        protected Annotation processOrWrapElementToAdd(Annotation added) {
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

    private class InteractionChecksumList extends AbstractListHavingProperties<Checksum> {
        public InteractionChecksumList(){
            super();
        }

        @Override
        protected void processAddedObjectEvent(Checksum added) {
            processAddedChecksumEvent(added);
        }

        @Override
        protected void processRemovedObjectEvent(Checksum removed) {
            processRemovedChecksumEvent(removed);
        }

        @Override
        protected void clearProperties() {
            clearPropertiesLinkedToChecksums();
        }
    }
}
