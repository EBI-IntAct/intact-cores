package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Target;
import org.hibernate.annotations.Where;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.model.Parameter;
import psidev.psi.mi.jami.utils.collection.AbstractCollectionWrapper;
import psidev.psi.mi.jami.utils.collection.AbstractListHavingProperties;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Intact implementation of feature evidence
 *
 * NOTE: getDetectionMethods is not a persistent method annotated with hibernate annotations. All the methods present in detectionMethods
 * are persisted in the same table but with different columns for backward compatibility with intact-core. So the persistent detection methods are available with the getDbDetectionMethods and getFeatureIdentification method.
 * For HQL queries, the method getDbDetectionMethods and getFeatureIdentification should be used because are annotated with hibernate annotations.
 * However, getDbDetectionMethods and getFeatureIdentification should not be used directly to add/remove detection methods because it could mess up with the state of the object. Only the synchronizers
 * can use it this way before persistence.
 * NOTE: The features have the ownership of the relation between participant and features. It means that to persist the relationship between participant and features,
 * the property getParticipant must be pointing to the right participant.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15/01/14</pre>
 */
@Entity
@Table(name = "ia_feature")
@Where(clause = "category = 'evidence'")
public class IntactFeatureEvidence extends AbstractIntactFeature<ExperimentalEntity,FeatureEvidence> implements FeatureEvidence{

    private transient DetectionMethodList detectionMethods;
    /**
     * Only for backward compatibility with intact-core
     * @deprecated this is only for backward compatibility with intact core. Look at detectionMethods instead.
     * When intact-core is removed, getDetectionMethods could become persistent and we could remove getDbIdentificationMethods after updating
     * the database so all 'identificationMethod' are also in the dbIdentificationMethods collection.
     */
    @Deprecated
    private CvTerm identificationMethod;
    private transient PersistentDetectionMethodList persistentDetectionMethods;
    private Collection<Parameter> parameters;

    public IntactFeatureEvidence(ParticipantEvidence participant) {
        super();
        setParticipant(participant);
    }

    public IntactFeatureEvidence(ParticipantEvidence participant, String shortName, String fullName) {
        super(shortName, fullName);
        setParticipant(participant);
    }

    public IntactFeatureEvidence(ParticipantEvidence participant, CvTerm type) {
        super(type);
        setParticipant(participant);
    }

    public IntactFeatureEvidence(ParticipantEvidence participant, String shortName, String fullName, CvTerm type) {
        super(shortName, fullName, type);
        setParticipant(participant);
    }

    public IntactFeatureEvidence() {
        super();
    }

    public IntactFeatureEvidence(String shortName, String fullName) {
        super(shortName, fullName);
    }

    public IntactFeatureEvidence(CvTerm type) {
        super(type);
    }

    public IntactFeatureEvidence(String shortName, String fullName, CvTerm type) {
        super(shortName, fullName, type);
    }

    @Transient
    public boolean areParametersInitialized(){
        return Hibernate.isInitialized(getParameters());
    }

    @OneToMany( orphanRemoval = true,
            cascade = {CascadeType.ALL}, targetEntity = ExperimentalRange.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @JoinColumn(name = "feature_ac", referencedColumnName = "ac")
    @Target(ExperimentalRange.class)
    @Override
    public Collection<Range> getRanges() {
        return super.getRanges();
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = FeatureEvidenceXref.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @JoinColumn(name = "parent_ac", referencedColumnName = "ac")
    @Target(FeatureEvidenceXref.class)
    @Override
    /**
     * This method give direct access to the persistent collection of xrefs (identifiers and xrefs all together) for this object.
     * WARNING: It should not be used to add/remove objects as it may mess up with the state of the object (only used this way by the synchronizers).
     */
    public Collection<Xref> getDbXrefs() {
        return super.getDbXrefs();
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = FeatureEvidenceAnnotation.class)
    @JoinTable(
            name="ia_feature2annot",
            joinColumns = @JoinColumn( name="feature_ac"),
            inverseJoinColumns = @JoinColumn( name="annotation_ac")
    )
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(FeatureEvidenceAnnotation.class)
    @Override
    /**
     * WARNING: The join table is for backward compatibility with intact-core.
     * When intact-core will be removed, the join table would disappear wnd the relation would become
     *     @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = FeatureEvidenceAnnotation.class)
     * @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
     * @JoinColumn(name = "parent_ac", referencedColumnName = "ac")
     * @Target(FeatureEvidenceAnnotation.class)
     * **/
    public Collection<Annotation> getAnnotations() {
        return super.getAnnotations();
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = FeatureEvidenceAlias.class)
    @JoinColumn(name = "parent_ac", referencedColumnName = "ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(FeatureEvidenceAlias.class)
    @Override
    public Collection<Alias> getAliases() {
        return super.getAliases();
    }

    @Transient
    /**
     *  NOTE: getDetectionMethods is not a persistent method annotated with hibernate annotations. All the methods present in detectionMethods
     * are persisted in the same table but with different columns for backward compatibility with intact-core. So the persistent detection methods are available with the getDbDetectionMethods and getFeatureIdentification method.
     * For HQL queries, the method getDbDetectionMethods and getFeatureIdentification should be used because are annotated with hibernate annotations.
     * However, getDbDetectionMethods and getFeatureIdentification should not be used directly to add/remove detection methods because it could mess up with the state of the object. Only the synchronizers
     * can use it this way before persistence.
     */
    public Collection<CvTerm> getDetectionMethods() {
        if (detectionMethods == null){
            initialiseDetectionMethods();
        }
        return this.detectionMethods;
    }

    @ManyToMany( targetEntity = IntactCvTerm.class)
    @JoinTable(
            name="ia_feature2method",
            joinColumns = @JoinColumn( name="feature_ac"),
            inverseJoinColumns = @JoinColumn( name="method_ac")
    )
    @Target(IntactCvTerm.class)
    @Deprecated
    /**
     * @deprecated use getLinkedFeatures instead
     */
    public Collection<CvTerm> getDbDetectionMethods() {
        if (persistentDetectionMethods == null){
            persistentDetectionMethods = new PersistentDetectionMethodList(null);
        }
        return this.persistentDetectionMethods.getWrappedList();
    }

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn( name = "identification_ac", referencedColumnName = "ac")
    @Target(IntactCvTerm.class)
    /**
     * @deprecated only for intact-core backward compatibility. Use detectionMethods instead
     */
    @Deprecated
    public CvTerm getFeatureIdentification() {
        return identificationMethod;
    }

    /**
     * @deprecated only for intact-core backward compatibility. Use detectionMethods instead
     */
    @Deprecated
    public void setFeatureIdentification( CvTerm cvFeatureIdentification ) {
        this.identificationMethod = cvFeatureIdentification;
        this.detectionMethods = null;
    }

    @Override
    @ManyToOne(targetEntity = IntactParticipantEvidence.class)
    @JoinColumn( name = "component_ac", referencedColumnName = "ac" )
    @Target(IntactParticipantEvidence.class)
    public ExperimentalEntity getParticipant() {
        return super.getParticipant();
    }

    @Override
    @ManyToMany( targetEntity = IntactFeatureEvidence.class)
    @JoinTable(
            name="ia_expfeature2feature",
            joinColumns = @JoinColumn( name="feature_evidence_ac"),
            inverseJoinColumns = @JoinColumn( name="linked_feature_ac")
    )
    @Target(IntactFeatureEvidence.class)
    public Collection<FeatureEvidence> getDbLinkedFeatures() {
        return super.getDbLinkedFeatures();
    }

    @Override
    @ManyToOne(targetEntity = IntactFeatureEvidence.class)
    @JoinColumn( name = "linkedfeature_ac", referencedColumnName = "ac" )
    @Target(IntactFeatureEvidence.class)
    public FeatureEvidence getBinds() {
        return super.getBinds();
    }

    @ManyToMany( mappedBy = "dbLinkedFeatures", targetEntity = IntactFeatureEvidence.class)
    @Target(IntactFeatureEvidence.class)
    @Override
    /**
     * The collection of features that have this feature in their dbLinkedFeatures collection
     */
    public Collection<FeatureEvidence> getRelatedLinkedFeatures() {
        return super.getRelatedLinkedFeatures();
    }

    @OneToMany( mappedBy = "binds", targetEntity = IntactFeatureEvidence.class)
    @Target(IntactFeatureEvidence.class)
    @Override
    /**
     * The collection of features that have this feature in their binds property
     */
    public Collection<FeatureEvidence> getRelatedBindings() {
        return super.getRelatedBindings();
    }

    @Transient
    public boolean areDetectionMethodsInitialized(){
        return Hibernate.isInitialized(getDbDetectionMethods());
    }

    @Column(name = "category", nullable = false, updatable = false)
    @NotNull
    protected String getCategory() {
        return "evidence";
    }

    /**
     * Overrides serialization for xrefs and annotations (inner classes not serializable)
     * @param oos
     * @throws java.io.IOException
     */
    private void writeObject(ObjectOutputStream oos)
            throws IOException {
        // default serialization
        oos.defaultWriteObject();
        // write the methods
        oos.writeObject(getDbDetectionMethods());
    }

    /**
     * Overrides serialization for xrefs and annotations (inner classes not serializable)
     * @param ois
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        // default deserialization
        ois.defaultReadObject();
        // read default methods
        setDbDetectionMethods((Collection<CvTerm>)ois.readObject());
    }

    private void initialiseDetectionMethods(){
        this.detectionMethods = new DetectionMethodList();
        // add binds if not null
        if (this.identificationMethod != null){
            this.detectionMethods.addOnly(this.identificationMethod);
        }
        // initialise persistent feature and content
        if (this.persistentDetectionMethods != null){
            for (CvTerm method : this.persistentDetectionMethods){
                this.detectionMethods.addOnly(method);
            }
        }
        else{
            this.persistentDetectionMethods = new PersistentDetectionMethodList(null);
        }
    }

    private void setDbDetectionMethods(Collection<CvTerm> detectionMethods) {
        if (detectionMethods instanceof PersistentDetectionMethodList){
            this.persistentDetectionMethods = (PersistentDetectionMethodList)detectionMethods;
            this.detectionMethods = null;
        }
        else{
            this.persistentDetectionMethods = new PersistentDetectionMethodList(detectionMethods);
            this.detectionMethods = null;
        }
    }

    private void setCategory(String value){
        // nothing to do
    }

    @OneToMany( orphanRemoval = true,
            cascade = {CascadeType.ALL}, targetEntity = FeatureEvidenceParameter.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(FeatureEvidenceParameter.class)
    public Collection<Parameter> getParameters() {
        if (parameters == null){
            this.parameters = new ArrayList<Parameter>();
        }
        return this.parameters;
    }

    private void setParameters(Collection<Parameter> parameters) {
        this.parameters = parameters;
    }

    private class DetectionMethodList extends AbstractListHavingProperties<CvTerm> {
        public DetectionMethodList(){
            super();
        }

        @Override
        protected void processAddedObjectEvent(CvTerm added) {
            if (identificationMethod == null){
                identificationMethod = added;
            }
            else{
                persistentDetectionMethods.add(added);
            }
        }

        @Override
        protected void processRemovedObjectEvent(CvTerm removed) {
            if (identificationMethod == removed){
                identificationMethod = null;
                if (!persistentDetectionMethods.isEmpty()){
                    identificationMethod = persistentDetectionMethods.iterator().next();
                    persistentDetectionMethods.remove(identificationMethod);
                }
            }
            else{
                persistentDetectionMethods.remove(removed);
            }
        }

        @Override
        protected void clearProperties() {
            identificationMethod = null;
            persistentDetectionMethods.clear();
        }
    }

    private class PersistentDetectionMethodList extends AbstractCollectionWrapper<CvTerm> {

        public PersistentDetectionMethodList(Collection<CvTerm> persistentBag){
            super(persistentBag);
        }

        @Override
        protected boolean needToPreProcessElementToAdd(CvTerm added) {
            return false;
        }

        @Override
        protected CvTerm processOrWrapElementToAdd(CvTerm added) {
            return added;
        }

        @Override
        protected void processElementToRemove(Object o) {
            // nothing to do
        }

        @Override
        protected boolean needToPreProcessElementToRemove(Object o) {
            return false;
        }
    }
}
