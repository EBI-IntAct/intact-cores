package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import psidev.psi.mi.jami.utils.ChecksumUtils;
import psidev.psi.mi.jami.utils.CvTermUtils;
import psidev.psi.mi.jami.utils.collection.AbstractCollectionWrapper;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Intact implementation of complex
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17/01/14</pre>
 */
@Entity
@DiscriminatorValue( "complex" )
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

    protected IntactComplex(){
        super();
    }

    public IntactComplex(String name, CvTerm interactorType) {
        super(name, interactorType != null ? interactorType : CvTermUtils.createComplexInteractorType());
    }

    public IntactComplex(String name, String fullName, CvTerm interactorType) {
        super(name, fullName, interactorType != null ? interactorType : CvTermUtils.createComplexInteractorType());
    }

    public IntactComplex(String name, CvTerm interactorType, Organism organism) {
        super(name, interactorType != null ? interactorType : CvTermUtils.createComplexInteractorType(), organism);
    }

    public IntactComplex(String name, String fullName, CvTerm interactorType, Organism organism) {
        super(name, fullName, interactorType != null ? interactorType : CvTermUtils.createComplexInteractorType(), organism);
    }

    public IntactComplex(String name, CvTerm interactorType, Xref uniqueId) {
        super(name, interactorType != null ? interactorType : CvTermUtils.createComplexInteractorType(), uniqueId);
    }

    public IntactComplex(String name, String fullName, CvTerm interactorType, Xref uniqueId) {
        super(name, fullName, interactorType != null ? interactorType : CvTermUtils.createComplexInteractorType(), uniqueId);
    }

    public IntactComplex(String name, CvTerm interactorType, Organism organism, Xref uniqueId) {
        super(name, interactorType != null ? interactorType : CvTermUtils.createComplexInteractorType(), organism, uniqueId);
    }

    public IntactComplex(String name, String fullName, CvTerm interactorType, Organism organism, Xref uniqueId) {
        super(name, fullName, interactorType != null ? interactorType : CvTermUtils.createComplexInteractorType(), organism, uniqueId);
    }

    public IntactComplex(String name) {
        super(name, CvTermUtils.createComplexInteractorType());
    }

    public IntactComplex(String name, String fullName) {
        super(name, fullName, CvTermUtils.createComplexInteractorType());
    }

    public IntactComplex(String name, Organism organism) {
        super(name, CvTermUtils.createComplexInteractorType(), organism);
    }

    public IntactComplex(String name, String fullName, Organism organism) {
        super(name, fullName, CvTermUtils.createComplexInteractorType(), organism);
    }

    public IntactComplex(String name, Xref uniqueId) {
        super(name, CvTermUtils.createComplexInteractorType(), uniqueId);
    }

    public IntactComplex(String name, String fullName, Xref uniqueId) {
        super(name, fullName, CvTermUtils.createComplexInteractorType(), uniqueId);
    }

    public IntactComplex(String name, Organism organism, Xref uniqueId) {
        super(name, CvTermUtils.createComplexInteractorType(), organism, uniqueId);
    }

    public IntactComplex(String name, String fullName, Organism organism, Xref uniqueId) {
        super(name, fullName, CvTermUtils.createComplexInteractorType(), organism, uniqueId);
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

    @OneToMany( mappedBy = "interaction", orphanRemoval = true,
            cascade = {CascadeType.ALL}, targetEntity = IntactModelledParticipant.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(IntactModelledParticipant.class)
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

    @OneToMany( mappedBy = "complex", orphanRemoval = true,
            cascade = {CascadeType.ALL}, targetEntity = ComplexConfidence.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(ComplexConfidence.class)
    public Collection<ModelledConfidence> getModelledConfidences() {
        if (confidences == null){
            initialiseConfidences();
        }
        return this.confidences;
    }

    @OneToMany( mappedBy = "complex", orphanRemoval = true,
            cascade = {CascadeType.ALL}, targetEntity = ComplexParameter.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(ComplexParameter.class)
    public Collection<ModelledParameter> getModelledParameters() {
        if (parameters == null){
            initialiseParameters();
        }
        return this.parameters;
    }

    @OneToMany( mappedBy = "complex", orphanRemoval = true,
            cascade = {CascadeType.ALL}, targetEntity = AbstractIntactCooperativeEffect.class)
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

    protected void clearPropertiesLinkedToAnnotations() {
        physicalProperties = null;
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
            this.rigid = new InteractorChecksum(rigidMethod, rigid);
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

    public Collection<Annotation> getAnnotations() {
        return super.getAnnotations();
    }

    public Collection<Checksum> getChecksums() {
        return super.getChecksums();
    }

    @Transient
    public Collection<Xref> getXrefs() {
        return super.getXrefs();
    }

    @Transient
    public Collection<Xref> getIdentifiers() {
        return super.getIdentifiers();
    }

    public Collection<Alias> getAliases() {
        return super.getAliases();
    }

    @Transient
    public Collection<InteractionEvidence> getInteractionEvidences() {
        if (interactionEvidences == null){
            initialiseInteractionEvidences();
        }
        return this.interactionEvidences;
    }

    @Override
    protected void initialiseDefaultInteractorType() {
        super.setInteractorType(IntactUtils.createIntactMITerm(Complex.COMPLEX, Complex.COMPLEX_MI, IntactUtils.INTERACTOR_TYPE_OBJCLASS));
    }

    @Override
    protected String getObjClass() {
        return "uk.ac.ebi.intact.model.InteractionImpl";
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

    protected void clearPropertiesLinkedToChecksums() {
        rigid = null;
    }

    protected void initialiseInteractionEvidences(){
        this.interactionEvidences = new ArrayList<InteractionEvidence>();
    }

    protected void initialiseCooperativeEffects(){
        this.cooperativeEffects = new ArrayList<CooperativeEffect>();
    }

    protected void initialiseConfidences(){
        this.confidences = new ArrayList<ModelledConfidence>();
    }

    protected void initialiseParameters(){
        this.parameters = new ArrayList<ModelledParameter>();
    }

    protected void initialiseComponents(){
        this.components = new ArrayList<ModelledParticipant>();
    }

    @Override
    protected void initialiseAnnotations() {
        super.setAnnotations(new ComplexAnnotationList(null));
        for (Annotation check : super.getAnnotations()){
            processAddedAnnotationEvent(check);
        }
    }

    @Override
    protected void setAnnotations(Collection<Annotation> annotations) {
        super.setAnnotations(new ComplexAnnotationList(annotations));
    }

    @Override
    protected void initialiseChecksums(){
        super.setChecksums(new ComplexChecksumList(null));
        for (Checksum check : super.getChecksums()){
            processAddedChecksumEvent(check);
        }
    }

    @Override
    protected void setChecksums(Collection<Checksum> checksums) {
        super.setChecksums(new ComplexChecksumList(checksums));
    }

    protected void setParticipants(Collection<ModelledParticipant> components) {
        this.components = components;
    }

    protected void setModelledConfidences(Collection<ModelledConfidence> confidences) {
        this.confidences = confidences;
    }

    protected void setModelledParameters(Collection<ModelledParameter> parameters) {
        this.parameters = parameters;
    }

    protected void setCooperativeEffects(Collection<CooperativeEffect> cooperativeEffects) {
        this.cooperativeEffects = cooperativeEffects;
    }

    protected class ComplexAnnotationList extends AbstractCollectionWrapper<Annotation> {
        public ComplexAnnotationList(Collection<Annotation> annot){
            super(annot);
        }

        @Override
        public boolean add(Annotation xref) {
            if(super.add(xref)){
                processAddedAnnotationEvent(xref);
                return true;
            }
            return false;
        }

        @Override
        public boolean remove(Object o) {
            if (super.remove(o)){
                processRemovedAnnotationEvent((Annotation) o);
                return true;
            }
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean hasChanged = false;
            for (Object annot : c){
                if (remove(annot)){
                    hasChanged = true;
                }
            }
            return hasChanged;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            List<Annotation> existingObject = new ArrayList<Annotation>(this);

            boolean removed = false;
            for (Annotation o : existingObject){
                if (!c.contains(o)){
                    if (remove(o)){
                        removed = true;
                    }
                }
            }

            return removed;
        }

        @Override
        public void clear() {
            super.clear();
            clearPropertiesLinkedToAnnotations();
        }

        @Override
        protected boolean needToPreProcessElementToAdd(Annotation added) {
            return false;
        }

        @Override
        protected Annotation processOrWrapElementToAdd(Annotation added) {
            return added;
        }
    }

    protected class ComplexChecksumList extends AbstractCollectionWrapper<Checksum> {
        public ComplexChecksumList(Collection<Checksum> checksums){
            super(checksums);
        }

        @Override
        public boolean add(Checksum xref) {
            if(super.add(xref)){
                processAddedChecksumEvent(xref);
                return true;
            }
            return false;
        }

        @Override
        public boolean remove(Object o) {
            if (super.remove(o)){
                processRemovedChecksumEvent((Checksum) o);
                return true;
            }
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean hasChanged = false;
            for (Object annot : c){
                if (remove(annot)){
                    hasChanged = true;
                }
            }
            return hasChanged;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            List<Checksum> existingObject = new ArrayList<Checksum>(this);

            boolean removed = false;
            for (Checksum o : existingObject){
                if (!c.contains(o)){
                    if (remove(o)){
                        removed = true;
                    }
                }
            }

            return removed;
        }

        @Override
        public void clear() {
            super.clear();
            clearPropertiesLinkedToChecksums();
        }

        @Override
        protected boolean needToPreProcessElementToAdd(Checksum added) {
            return false;
        }

        @Override
        protected Checksum processOrWrapElementToAdd(Checksum added) {
            return added;
        }
    }
}
