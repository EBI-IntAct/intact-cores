package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.*;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.model.Source;
import psidev.psi.mi.jami.model.impl.DefaultChecksum;
import psidev.psi.mi.jami.utils.AliasUtils;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import psidev.psi.mi.jami.utils.ChecksumUtils;
import psidev.psi.mi.jami.utils.collection.AbstractListHavingProperties;
import uk.ac.ebi.intact.jami.model.ComplexLifecycleEvent;
import uk.ac.ebi.intact.jami.model.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.listener.ComplexParameterListener;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.CascadeType;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Intact implementation of complex
 *
 * NOTE: The participants have the ownership of the relation between complex and participants. It means that to persist the relationship between interaction and participants,
 * the property getInteraction in the participant must be pointing to the right complex. It is then recommended to use the provided addParticipant and removeParticipant methods to add/remove participants
 * from the complex
 * NOTE: the method getExperiments has been kept only for backward compatibility with intact-core. As soon as intact-core is removed, the getExperiments can be removed.
 * This method should never been used in any applications
 * NOTE: getCreatedDate and getUpdatedDate are transient methods as the AbstractIntactAudit parent class contains the relevant persistent audit methods.
 * NOTE: checksums are not persistent for complexes
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17/01/14</pre>
 */
@Entity
@DiscriminatorValue( "complex" )
@EntityListeners(value = {ComplexParameterListener.class})
@Where(clause = "category = 'complex'")
public class IntactComplex extends IntactInteractor implements Complex{
    private Collection<InteractionEvidence> interactionEvidences;
    private Collection<ModelledParticipant> components;
    private Annotation physicalProperties;
    private Collection<ModelledConfidence> confidences;
    private Collection<ModelledParameter> parameters;

    private Source source;
    private Collection<CooperativeEffect> cooperativeEffects;
    private Checksum rigid;
    private CvTerm interactionType;

    private Alias recommendedName;
    private Alias systematicName;
    private Collection<Experiment> experiments;
    private List<LifeCycleEvent> lifecycleEvents;
    private CvTerm status;
    private CvTerm evidenceType;
    private User currentOwner;
    private User currentReviewer;

    protected IntactComplex(){
        super();
    }

    public IntactComplex(String name, CvTerm interactorType) {
        super(name, interactorType);
    }

    public IntactComplex(String name, String fullName, CvTerm interactorType) {
        super(name, fullName, interactorType);
    }

    public IntactComplex(String name, CvTerm interactorType, Organism organism) {
        super(name, interactorType, organism);
    }

    public IntactComplex(String name, String fullName, CvTerm interactorType, Organism organism) {
        super(name, fullName, interactorType, organism);
    }

    public IntactComplex(String name, CvTerm interactorType, Xref uniqueId) {
        super(name, interactorType, uniqueId);
    }

    public IntactComplex(String name, String fullName, CvTerm interactorType, Xref uniqueId) {
        super(name, fullName, interactorType, uniqueId);
    }

    public IntactComplex(String name, CvTerm interactorType, Organism organism, Xref uniqueId) {
        super(name, interactorType, organism, uniqueId);
    }

    public IntactComplex(String name, String fullName, CvTerm interactorType, Organism organism, Xref uniqueId) {
        super(name, fullName, interactorType, organism, uniqueId);
    }

    public IntactComplex(String name) {
        super(name);
    }

    public IntactComplex(String name, String fullName) {
        super(name, fullName);
    }

    public IntactComplex(String name, Organism organism) {
        super(name, organism);
    }

    public IntactComplex(String name, String fullName, Organism organism) {
        super(name, fullName, organism);
    }

    public IntactComplex(String name, Xref uniqueId) {
        super(name, uniqueId);
    }

    public IntactComplex(String name, String fullName, Xref uniqueId) {
        super(name, fullName, uniqueId);
    }

    public IntactComplex(String name, Organism organism, Xref uniqueId) {
        super(name, organism, uniqueId);
    }

    public IntactComplex(String name, String fullName, Organism organism, Xref uniqueId) {
        super(name, fullName, organism, uniqueId);
    }

    @Override
    @Transient
    public Collection<Annotation> getAnnotations() {
        return super.getAnnotations();
    }

    @Override
    @Transient
    public Collection<Checksum> getChecksums() {
        return super.getChecksums();
    }

    @Override
    @Transient
    public Collection<Xref> getXrefs() {
        return super.getXrefs();
    }

    @Override
    @Transient
    public Collection<Xref> getIdentifiers() {
        return super.getIdentifiers();
    }

    @Override
    @Transient
    public Collection<Alias> getAliases() {
        return super.getAliases();
    }

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn( name = "status_ac", referencedColumnName = "ac" )
    @ForeignKey(name="FK_COMPLEX_STATUS")
    @Target(IntactCvTerm.class)
    public CvTerm getStatus() {
        return status;
    }

    public void setStatus( CvTerm status ) {
        this.status = status;
    }

    @ManyToOne( targetEntity = User.class )
    @JoinColumn( name = "owner_pk", referencedColumnName = "ac" )
    @ForeignKey(name="FK_COMPLEX_OWNER")
    @Target(User.class)
    public User getCurrentOwner() {
        return currentOwner;
    }

    public void setCurrentOwner( User currentOwner ) {
        this.currentOwner = currentOwner;
    }

    @ManyToOne( targetEntity = User.class )
    @JoinColumn( name = "reviewer_pk", referencedColumnName = "ac" )
    @ForeignKey(name="FK_COMPLEX_REVIEWER")
    @Target(User.class)
    public User getCurrentReviewer() {
        return currentReviewer;
    }

    public void setCurrentReviewer( User currentReviewer ) {
        this.currentReviewer = currentReviewer;
    }

    @OneToMany( orphanRemoval = true, cascade = CascadeType.ALL, targetEntity = ComplexLifecycleEvent.class)
    @JoinColumn(name="complex_ac", referencedColumnName="ac")
    @ForeignKey(name="FK_LIFECYCLE_EVENT_COMPLEX")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @OrderBy("when, created")
    @Target(ComplexLifecycleEvent.class)
    public List<LifeCycleEvent> getLifecycleEvents() {
        if (this.lifecycleEvents == null){
            this.lifecycleEvents = new ArrayList<LifeCycleEvent>();
        }
        return lifecycleEvents;
    }

    @ManyToOne(targetEntity = IntactSource.class)
    @JoinColumn( name = "owner_ac", referencedColumnName = "ac", nullable = false )
    @Target(IntactSource.class)
    public Source getSource() {
        return this.source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn( name = "evidence_type_ac", referencedColumnName = "ac", nullable = false )
    @Target(IntactCvTerm.class)
    public CvTerm getEvidenceType() {
        return evidenceType;
    }

    public void setEvidenceType(CvTerm evidenceType) {
        this.evidenceType = evidenceType;
    }

    @OneToMany( mappedBy = "dbParentInteraction", orphanRemoval = true,
            cascade = {CascadeType.ALL}, targetEntity = IntactModelledParticipant.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(IntactModelledParticipant.class)
    /**
    * NOTE: The participants have the ownership of the relation between complex and participants. It means that to persist the relationship between interaction and participants,
    * the property getInteraction in the participant must be pointing to the right complex. It is then recommended to use the provided addParticipant and removeParticipant methods to add/remove participants
    * from the complex
    **/
    public Collection<ModelledParticipant> getParticipants() {
        if (components == null){
            initialiseComponents();
        }
        return this.components;
    }

    public boolean addParticipant(ModelledParticipant part) {
        if (part == null){
            return false;
        }
        if (components == null){
            initialiseComponents();
        }
        part.setInteraction(this);
        return components.add(part);
    }

    public boolean removeParticipant(ModelledParticipant part) {
        if (part == null){
            return false;
        }
        if (components == null){
            initialiseComponents();
        }
        part.setInteraction(null);
        if (components.remove(part)){
            return true;
        }
        return false;
    }

    public boolean addAllParticipants(Collection<? extends ModelledParticipant> participants) {
        if (participants == null){
            return false;
        }

        boolean added = false;
        for (ModelledParticipant p : participants){
            if (addParticipant(p)){
                added = true;
            }
        }
        return added;
    }

    public boolean removeAllParticipants(Collection<? extends ModelledParticipant> participants) {
        if (participants == null){
            return false;
        }

        boolean removed = false;
        for (ModelledParticipant p : participants){
            if (removeParticipant(p)){
                removed = true;
            }
        }
        return removed;
    }

    @OneToMany( orphanRemoval = true,
            cascade = {CascadeType.ALL}, targetEntity = ComplexConfidence.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(ComplexConfidence.class)
    public Collection<ModelledConfidence> getModelledConfidences() {
        if (confidences == null){
            initialiseConfidences();
        }
        return this.confidences;
    }

    @OneToMany( orphanRemoval = true,
            cascade = {CascadeType.ALL}, targetEntity = ComplexParameter.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(ComplexParameter.class)
    public Collection<ModelledParameter> getModelledParameters() {
        if (parameters == null){
            initialiseParameters();
        }
        return this.parameters;
    }

    @OneToMany( orphanRemoval = true,
            cascade = {CascadeType.ALL}, targetEntity = AbstractIntactCooperativeEffect.class)
    @JoinColumn(name="complex_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(AbstractIntactCooperativeEffect.class)
    public Collection<CooperativeEffect> getCooperativeEffects() {
        if (cooperativeEffects == null){
            initialiseCooperativeEffects();
        }
        return this.cooperativeEffects;
    }

    @Transient
    public String getPhysicalProperties() {
        // initialise annotations if necessary
        getAnnotations();
        return this.physicalProperties != null ? this.physicalProperties.getValue() : null;
    }

    public void setPhysicalProperties(String properties) {
        Collection<Annotation> complexAnnotationList = getAnnotations();

        // add new physical properties if not null
        if (properties != null){

            CvTerm complexPhysicalProperties = IntactUtils.createMITopic(Annotation.COMPLEX_PROPERTIES, Annotation.COMPLEX_PROPERTIES_MI);
            // first remove old physical property if not null
            if (this.physicalProperties != null){
                complexAnnotationList.remove(this.physicalProperties);
            }
            this.physicalProperties = new InteractorAnnotation(complexPhysicalProperties, properties);
            complexAnnotationList.add(this.physicalProperties);
        }
        // remove all physical properties if the collection is not empty
        else if (!complexAnnotationList.isEmpty()) {
            AnnotationUtils.removeAllAnnotationsWithTopic(complexAnnotationList, Annotation.COMPLEX_PROPERTIES_MI, Annotation.COMPLEX_PROPERTIES);
            physicalProperties = null;
        }
    }

    @Transient
    public String getRecommendedName() {
        // initialise aliases if necessary
        getAliases();
        return this.recommendedName != null ? this.recommendedName.getName() : null;
    }

    public void setRecommendedName(String name) {
        Collection<Alias> complexAliasList = getAliases();

        // add new recommended name if not null
        if (name != null){

            CvTerm recommendedName = IntactUtils.createMIAliasType(Alias.COMPLEX_RECOMMENDED_NAME, Alias.COMPLEX_RECOMMENDED_NAME_MI);
            // first remove old recommended name if not null
            if (this.recommendedName != null){
                complexAliasList.remove(this.recommendedName);
            }
            this.recommendedName = new InteractorAlias(recommendedName, name);
            complexAliasList.add(this.recommendedName);
        }
        // remove all recommended name if the collection is not empty
        else if (!complexAliasList.isEmpty()) {
            AliasUtils.removeAllAliasesWithType(complexAliasList, Alias.COMPLEX_RECOMMENDED_NAME_MI, Alias.COMPLEX_RECOMMENDED_NAME);
            recommendedName = null;
        }
    }

    @Transient
    public String getSystematicName() {
        // initialise aliases if necessary
        getAliases();
        return this.systematicName != null ? this.systematicName.getName() : null;
    }

    public void setSystematicName(String name) {
        Collection<Alias> complexAliasList = getAliases();

        // add new systematic name if not null
        if (name != null){

            CvTerm systematicName = IntactUtils.createMIAliasType(Alias.COMPLEX_SYSTEMATIC_NAME, Alias.COMPLEX_SYSTEMATIC_NAME_MI);
            // first remove systematic name  if not null
            if (this.systematicName != null){
                complexAliasList.remove(this.systematicName);
            }
            this.systematicName = new InteractorAlias(systematicName, name);
            complexAliasList.add(this.systematicName);
        }
        // remove all systematic name  if the collection is not empty
        else if (!complexAliasList.isEmpty()) {
            AliasUtils.removeAllAliasesWithType(complexAliasList, Alias.COMPLEX_SYSTEMATIC_NAME_MI, Alias.COMPLEX_SYSTEMATIC_NAME);
            systematicName = null;
        }
    }

    @ManyToMany(targetEntity = IntactExperiment.class)
    @JoinTable(
            name = "ia_int2exp",
            joinColumns = {@JoinColumn( name = "interaction_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "experiment_ac" )}
    )
    @Target(IntactExperiment.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    @Deprecated
    /**
     * This method should not be used in any applications. It is only for synchronization with the database and backward compatibility
     * with intact-core
     * @deprecated Only kept for backward compatibility with intact core. Complexes should not have experiments
     */
    public Collection<Experiment> getExperiments() {
        if (experiments == null){
            experiments = new ArrayList<Experiment>();
        }
        return experiments;
    }

    @Transient
    public boolean areInteractionEvidencesInitialized(){
        return Hibernate.isInitialized(getInteractionEvidences());
    }

    protected void processAddedAnnotationEvent(Annotation added) {
        if (physicalProperties == null && AnnotationUtils.doesAnnotationHaveTopic(added, Annotation.COMPLEX_PROPERTIES_MI, Annotation.COMPLEX_PROPERTIES)){
            physicalProperties = added;
        }
    }

    protected void processRemovedAnnotationEvent(Annotation removed) {
        if (physicalProperties != null && physicalProperties.equals(removed)){
            physicalProperties = AnnotationUtils.collectFirstAnnotationWithTopic(getAnnotations(), Annotation.COMPLEX_PROPERTIES_MI, Annotation.COMPLEX_PROPERTIES);
        }
    }

    protected void processAddedAliasEvent(Alias added) {
        if (recommendedName == null && AliasUtils.doesAliasHaveType(added, Alias.COMPLEX_RECOMMENDED_NAME_MI, Alias.COMPLEX_RECOMMENDED_NAME)){
            recommendedName = added;
        }
        else if (systematicName == null && AliasUtils.doesAliasHaveType(added, Alias.COMPLEX_SYSTEMATIC_NAME_MI, Alias.COMPLEX_SYSTEMATIC_NAME)){
            systematicName = added;
        }
    }

    protected void processRemovedAliasEvent(Alias removed) {
        if (recommendedName != null && recommendedName.equals(removed)){
            recommendedName = AliasUtils.collectFirstAliasWithType(getAliases(), Alias.COMPLEX_RECOMMENDED_NAME_MI, Alias.COMPLEX_RECOMMENDED_NAME);
        }
        else if (systematicName != null && systematicName.equals(removed)){
            systematicName = AliasUtils.collectFirstAliasWithType(getAliases(), Alias.COMPLEX_SYSTEMATIC_NAME_MI, Alias.COMPLEX_SYSTEMATIC_NAME);
        }
    }

    @Transient
    public String getRigid() {
        // initialise checksum if necessary
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

    @ManyToMany(targetEntity = IntactInteractionEvidence.class)
    @JoinTable(
            name = "ia_complex2evidence",
            joinColumns = {@JoinColumn( name = "complex_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "evidence_ac" )}
    )
    @Target(IntactInteractionEvidence.class)
    public Collection<InteractionEvidence> getInteractionEvidences() {
        if (interactionEvidences == null){
            initialiseInteractionEvidences();
        }
        return this.interactionEvidences;
    }

    @Transient
    public boolean areParticipantsInitialized(){
        return Hibernate.isInitialized(getParticipants());
    }

    @Transient
    public boolean areLifeCycleEventsInitialized(){
        return Hibernate.isInitialized(getLifecycleEvents());
    }

    @Transient
    public boolean areCooperativeEffectsInitialized(){
        return Hibernate.isInitialized(getCooperativeEffects());
    }

    @Transient
    public boolean areParametersInitialized(){
        return Hibernate.isInitialized(getModelledParameters());
    }

    @Transient
    public boolean areConfidencesInitialized(){
        return Hibernate.isInitialized(getModelledConfidences());
    }

    @Transient
    public boolean areExperimentsInitialized(){
        return Hibernate.isInitialized(getExperiments());
    }

    @Override
    protected void initialiseDefaultInteractorType() {
        super.setInteractorType(IntactUtils.createIntactMITerm(Complex.COMPLEX, Complex.COMPLEX_MI, IntactUtils.INTERACTOR_TYPE_OBJCLASS));
    }

    @Override
    @Column(name = "objclass", nullable = false, insertable = false, updatable = false)
    @NotNull
    protected String getObjClass() {
        return "uk.ac.ebi.intact.model.InteractionImpl";
    }

    @Override
    protected void initialiseAnnotations() {
        super.initialiseAnnotationsWith(new ComplexAnnotationList(null));
        for (Annotation check : super.getDbAnnotations()){
            processAddedAnnotationEvent(check);
        }
    }

    @Override
    protected void setDbAnnotations(Collection<Annotation> annotations) {
        super.setDbAnnotations(new ComplexAnnotationList(annotations));
    }

    @Override
    protected void initialiseChecksums(){
        super.initialiseChecksumsWith(new ComplexChecksumList());
    }

    @Override
    protected void setDbAliases(Collection<Alias> aliases) {
        super.setDbAliases(new ComplexAliasList(aliases));
    }

    @Override
    protected void initialiseAliases(){
        super.initialiseAliasesWith(new ComplexAliasList(null));
        // initialise persistent aliases and content
        for (Alias alias : getDbAliases()){
            processAddedAliasEvent(alias);
        }
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

    private void initialiseInteractionEvidences(){
        this.interactionEvidences = new ArrayList<InteractionEvidence>();
    }

    private void initialiseCooperativeEffects(){
        this.cooperativeEffects = new ArrayList<CooperativeEffect>();
    }

    private void initialiseConfidences(){
        this.confidences = new ArrayList<ModelledConfidence>();
    }

    private void initialiseParameters(){
        this.parameters = new ArrayList<ModelledParameter>();
    }

    private void initialiseComponents(){
        this.components = new ArrayList<ModelledParticipant>();
    }

    private void clearPropertiesLinkedToChecksums() {
        this.rigid = null;
    }

    private void setParticipants(Collection<ModelledParticipant> components) {
        this.components = components;
    }

    private void setModelledConfidences(Collection<ModelledConfidence> confidences) {
        this.confidences = confidences;
    }

    private void setModelledParameters(Collection<ModelledParameter> parameters) {
        this.parameters = parameters;
    }

    private void setCooperativeEffects(Collection<CooperativeEffect> cooperativeEffects) {
        this.cooperativeEffects = cooperativeEffects;
    }

    private void setExperiments(Collection<Experiment> experiments) {
        this.experiments = experiments;
    }

    private void setLifecycleEvents( List<LifeCycleEvent> lifecycleEvents ) {
        this.lifecycleEvents = lifecycleEvents;
    }

    private void setInteractionEvidences(Collection<InteractionEvidence> interactionEvidences) {
        this.interactionEvidences = interactionEvidences;
    }

    private class ComplexAnnotationList extends PersistentAnnotationList {
        public ComplexAnnotationList(Collection<Annotation> annot){
            super(annot);
        }

        @Override
        protected boolean needToPreProcessElementToAdd(Annotation added) {
            return true;
        }

        @Override
        protected Annotation processOrWrapElementToAdd(Annotation added) {
            processAddedAnnotationEvent(added);
            return added;
        }

        @Override
        protected void processElementToRemove(Object o) {
            processRemovedAnnotationEvent((Annotation)o);
        }

        @Override
        protected boolean needToPreProcessElementToRemove(Object o) {
            return o instanceof Annotation;
        }
    }

    private class ComplexChecksumList extends AbstractListHavingProperties<Checksum> {
        public ComplexChecksumList(){
            super();
        }

        @Override
        protected void processAddedObjectEvent(Checksum checksum) {
            processAddedChecksumEvent(checksum);
        }

        @Override
        protected void processRemovedObjectEvent(Checksum checksum) {
            processRemovedChecksumEvent(checksum);
        }

        @Override
        protected void clearProperties() {
            clearPropertiesLinkedToChecksums();
        }
    }

    private class ComplexAliasList extends PersistentAliasList {
        public ComplexAliasList(Collection<Alias> aliases){
            super(aliases);
        }

        @Override
        protected boolean needToPreProcessElementToAdd(Alias added) {
            return true;
        }

        @Override
        protected Alias processOrWrapElementToAdd(Alias added) {
            processAddedAliasEvent(added);
            return added;
        }

        @Override
        protected void processElementToRemove(Object o) {
            processRemovedAliasEvent((Alias)o);
        }

        @Override
        protected boolean needToPreProcessElementToRemove(Object o) {
            return o instanceof Alias;
        }
    }
}
