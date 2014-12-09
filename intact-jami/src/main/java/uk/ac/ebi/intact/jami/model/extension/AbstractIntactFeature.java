package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.model.Entity;
import psidev.psi.mi.jami.model.impl.DefaultCvTerm;
import psidev.psi.mi.jami.model.impl.DefaultXref;
import psidev.psi.mi.jami.utils.CvTermUtils;
import psidev.psi.mi.jami.utils.XrefUtils;
import psidev.psi.mi.jami.utils.collection.AbstractCollectionWrapper;
import psidev.psi.mi.jami.utils.collection.AbstractListHavingProperties;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.context.IntactContext;
import uk.ac.ebi.intact.jami.model.AbstractIntactPrimaryObject;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract class for intact features
 *
 * NOTE: The feature ac is automatically added as an identifier in getIdentifiers but is not persisted in getDbXrefs.
 * The getIdentifiers.remove will thrown an UnsupportedOperationException if someone tries to remove the AC identifier from the list of identifiers
 * NOTE: getIdentifiers and getXrefs are not persistent methods annotated with hibernate annotations. All the xrefs present in identifiers
 * and xrefs are persisted in the same table for backward compatibility with intact-core. So the persistent xrefs are available with the getDbXrefs method.
 * For HQL queries, the method getDbXrefs should be used because is annotated with hibernate annotations.
 * However, getDbXrefs should not be used directly to add/remove xrefs because it could mess up with the state of the object. Only the synchronizers
 * can use it this way before persistence.
 * However, it is not recommended to use this method to directly add/remove xrefs as it may mess up with the state of the object.
 * NOTE: getLinkedFeatures is not a persistent method annotated with hibernate annotations. All the features present in linkedFeatures
 * are persisted in the same table but with different columns for backward compatibility with intact-core. So the persistent features are available with the getDbLinkedFeatures and getBinds method.
 * For HQL queries, the method getDbLinkedFeatures and getBinds should be used because are annotated with hibernate annotations.
 * However, getDbLinkedFeatures and getDbBinds should not be used directly to add/remove features because it could mess up with the state of the object. Only the synchronizers
 * can use it this way before persistence.
 * NOTE: The features have the ownership of the relation between participant and features. It means that to persist the relationship between participant and features,
 * the property getParticipant must be pointing to the right participant.
 * NOTE; all features are in the same table for backward compatibility with intact-core. In the future, this will be updated
 * In the meantime, because of backward compatibility issues,we use a where statement in IntActModelledFeature and IntactFeatureEvidence.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@MappedSuperclass
public abstract class AbstractIntactFeature<P extends Entity, F extends Feature> extends AbstractIntactPrimaryObject implements Feature<P,F>{

    private String shortName;
    private String fullName;
    private transient Xref interpro;
    private transient FeatureIdentifierList identifiers;
    private transient FeatureXrefList xrefs;
    private Collection<Annotation> annotations;
    private Collection<Alias> aliases;
    private CvTerm type;
    private Collection<Range> ranges;

    private CvTerm role;

    private P participant;

    private transient LinkedFeatureList linkedFeatures;
    private transient PersistentXrefList persistentXrefs;
    private transient PersistentLinkedFeatureList persistentLinkedFeatures;

    private Collection<F> relatedLinkedFeatures;
    private Collection<F> relatedBindings;
    /**
     * <p/>
     * A feature may bind to another feature, usually on a different
     * Interactor. This binding is reciprocal, the &quot;binds&quot; attribute should be
     * used on both Interactors.
     * </p>
     * <p/>
     * Deprecated special case: If a complex is assembled fromsubcomplexe, it
     * is not directly possible to represent the binding domains between the
     * subcomplexes. However, this is possible by defining domains on the
     * initial substrates, which are then used as binding domains between
     * Interactores which only interact in the second complex. As this method
     * creates ambiguities and difficult data structures, it is deprecated.
     * </p>
     * @deprecated this is only for backward compatibility with intact core. Look at linkedFeatures instead.
     * When intact-core is removed, getLinkedFeatures could become persistent and we could remove getDbLinkedFeatures after updating
     * the database so all 'binds' are also in the dbLinkedFeatures collection.
     */
    private F binds;

    private transient Xref acRef;

    public AbstractIntactFeature(){
    }

    public AbstractIntactFeature(String shortName, String fullName){
        this();
        this.shortName = shortName;
        this.fullName = fullName;
    }

    public AbstractIntactFeature(CvTerm type){
        this();
        this.type = type;
    }

    public AbstractIntactFeature(String shortName, String fullName, CvTerm type){
        this(shortName, fullName);
        this.type =type;
    }

    public AbstractIntactFeature(String shortName, String fullName, String interpro){
        this(shortName, fullName);
        setInterpro(interpro);
    }

    public AbstractIntactFeature(CvTerm type, String interpro){
        this(type);
        setInterpro(interpro);
    }

    public AbstractIntactFeature(String shortName, String fullName, CvTerm type, String interpro){
        this(shortName, fullName, type);
        setInterpro(interpro);
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

    @Column(name = "shortlabel", nullable = false)
    @Size( min = 1, max = IntactUtils.MAX_SHORT_LABEL_LEN )
    @NotNull
    public String getShortName() {
        if (this.shortName == null){
            this.shortName = "N/A";
        }
        return this.shortName;
    }

    public void setShortName(String name) {
        this.shortName = name;
    }

    @Column( length = IntactUtils.MAX_FULL_NAME_LEN )
    @Size( max = IntactUtils.MAX_FULL_NAME_LEN )
    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String name) {
        this.fullName = name;
    }

    @Transient
    public String getInterpro() {
        // initialise identifiers if not done yet
        if (identifiers == null){
            initialiseXrefs();
        }
        return this.interpro != null ? this.interpro.getId() : null;
    }

    public void setInterpro(String interpro) {
        Collection<Xref> featureIdentifiers = getIdentifiers();

        // add new interpro if not null
        if (interpro != null){
            CvTerm interproDatabase = IntactUtils.createMIDatabase(Xref.INTERPRO, Xref.INTERPRO_MI);
            CvTerm identityQualifier = IntactUtils.createMIQualifier(Xref.IDENTITY, Xref.IDENTITY_MI);
            // first remove old chebi if not null
            if (this.interpro != null){
                featureIdentifiers.remove(this.interpro);
            }
            this.interpro = new FeatureEvidenceXref(interproDatabase, interpro, identityQualifier);
            featureIdentifiers.add(this.interpro);
        }
        // remove all interpro if the collection is not empty
        else if (!featureIdentifiers.isEmpty()) {
            XrefUtils.removeAllXrefsWithDatabase(featureIdentifiers, Xref.INTERPRO_MI, Xref.INTERPRO);
            this.interpro = null;
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
    public Collection<Annotation> getAnnotations() {
        if (annotations == null){
            initialiseAnnotations();
        }
        return this.annotations;
    }

    @Transient
    public Collection<Alias> getAliases() {
        if (this.aliases == null){
            initialiseAliases();
        }
        return aliases;
    }

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn( name = "featuretype_ac", referencedColumnName = "ac")
    @Target(IntactCvTerm.class)
    public CvTerm getType() {
        if (this.type == null){
           initialiseDefaultType();
        }
        return this.type;
    }

    public void setType(CvTerm type) {
        this.type = type;
    }

    @Transient
    public Collection<Range> getRanges() {
        if (ranges == null){
            initialiseRanges();
        }
        return this.ranges;
    }

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn( name = "role_ac", referencedColumnName = "ac")
    @Target(IntactCvTerm.class)
    public CvTerm getRole() {
        return this.role;
    }

    public void setRole(CvTerm effect) {
        this.role = effect;
    }

    @Transient
    public P getParticipant() {
        return this.participant;
    }

    public void setParticipant(P participant) {
        this.participant = participant;
    }

    public void setParticipantAndAddFeature(P participant) {
        if (this.participant != null){
            this.participant.removeFeature(this);
        }

        if (participant != null){
            participant.addFeature(this);
        }
    }

    @Transient
    public Collection<F> getLinkedFeatures() {
        if(linkedFeatures == null){
            initialiseLinkedFeatures();
        }
        return this.linkedFeatures;
    }

    @Transient
    @Deprecated
    /**
     * @deprecated use getLinkedFeatures instead
     */
    public Collection<F> getDbLinkedFeatures() {
        if(persistentLinkedFeatures == null){
            this.persistentLinkedFeatures = new PersistentLinkedFeatureList(null);
        }
        return this.persistentLinkedFeatures.getWrappedList();
    }

    @Override
    public String toString() {
        return type != null ? type.toString() : (!ranges.isEmpty() ? "("+ranges.iterator().next().toString()+"...)" : " (-)");
    }

    @Transient
    public Collection<Xref> getDbXrefs() {
        if (this.persistentXrefs == null){
            this.persistentXrefs = new PersistentXrefList(null);
        }
        return this.persistentXrefs.getWrappedList();
    }

    @Transient
    public boolean areXrefsInitialized(){
        return Hibernate.isInitialized(getDbXrefs());
    }

    @Transient
    public boolean areAliasesInitialized(){
        return Hibernate.isInitialized(getAliases());
    }

    @Transient
    public boolean areAnnotationsInitialized(){
        return Hibernate.isInitialized(getAnnotations());
    }

    @Transient
    public boolean areRangesInitialized(){
        return Hibernate.isInitialized(getRanges());
    }

    @Transient
    public boolean areLinkedFeaturesInitialized(){
        return Hibernate.isInitialized(getDbLinkedFeatures());
    }

    @Transient
    /**
     * @deprecated for intact-core backward compatibility only. Use linkedFeatures instead
     */
    @Deprecated
    public F getBinds() {
        return binds;
    }

    /**
     *
     * @param binds
     * @deprecated for intact-core backward compatibility only. Use linkedFeatures instead
     */
    @Deprecated
    public void setBinds(F binds) {
        this.binds = binds;
        this.linkedFeatures = null;
    }

    @Transient
    public Collection<F> getRelatedLinkedFeatures() {
        if (this.relatedLinkedFeatures == null){
            this.relatedLinkedFeatures = new ArrayList<F>();
        }
        return this.relatedLinkedFeatures;
    }

    @Transient
    /**
     * The collection of features that have this feature in their binds property
     */
    public Collection<F> getRelatedBindings() {
        if (this.relatedBindings == null){
            this.relatedBindings = new ArrayList<F>();
        }
        return this.relatedBindings;
    }

    protected void initialiseDefaultType(){
        // by default do not initialise default type
    }

    protected void initialiseXrefs(){
        this.identifiers = new FeatureIdentifierList();
        this.xrefs = new FeatureXrefList();
        if (this.persistentXrefs != null){
            for (Xref ref : this.persistentXrefs){
                if (XrefUtils.isXrefAnIdentifier(ref) || XrefUtils.doesXrefHaveQualifier(ref, null, "intact-secondary")){
                    this.identifiers.addOnly(ref);
                    processAddedIdentifierEvent(ref);
                }
                else{
                    this.xrefs.addOnly(ref);
                }
            }
        }
        else{
            this.persistentXrefs = new PersistentXrefList(null);
        }
        // initialise ac
        if (getAc() != null){
            IntactContext intactContext = ApplicationContextProvider.getBean("intactJamiContext");
            if (intactContext != null){
                this.acRef = new DefaultXref(intactContext.getIntactConfiguration().getDefaultInstitution(), getAc(), CvTermUtils.createIdentityQualifier());
            }
            else{
                this.acRef = new DefaultXref(new DefaultCvTerm("unknwon"), getAc(), CvTermUtils.createIdentityQualifier());
            }
            this.identifiers.addOnly(this.acRef);
        }
    }

    protected void processAddedIdentifierEvent(Xref added) {
        // the added identifier is interpro and it is not the current interpro identifier
        if (interpro != added && XrefUtils.isXrefFromDatabase(added, Xref.INTERPRO_MI, Xref.INTERPRO)){
            // the current interpro identifier is not identity, we may want to set interpro Identifier
            if (!XrefUtils.doesXrefHaveQualifier(interpro, Xref.IDENTITY_MI, Xref.IDENTITY)){
                // the interpro identifier is not set, we can set the interpro identifier
                if (interpro == null){
                    interpro = added;
                }
                else if (XrefUtils.doesXrefHaveQualifier(added, Xref.IDENTITY_MI, Xref.IDENTITY)){
                    interpro = added;
                }
                // the added xref is secondary object and the current interpro identifier is not a secondary object, we reset interpro identifier
                else if (!XrefUtils.doesXrefHaveQualifier(interpro, Xref.SECONDARY_MI, Xref.SECONDARY)
                        && XrefUtils.doesXrefHaveQualifier(added, Xref.SECONDARY_MI, Xref.SECONDARY)){
                    interpro = added;
                }
            }
        }
    }

    protected void processRemovedIdentifierEvent(Xref removed) {
        if (interpro != null && interpro.equals(removed)){
            interpro = XrefUtils.collectFirstIdentifierWithDatabase(getIdentifiers(), Xref.INTERPRO_MI, Xref.INTERPRO);
        }
    }

    protected void clearPropertiesLinkedToIdentifiers() {
        interpro = null;
    }

    protected void initialiseAnnotations(){
        this.annotations = new ArrayList<Annotation>();
    }

    protected void initialiseRanges(){
        this.ranges = new ArrayList<Range>();
    }

    protected void initialiseLinkedFeatures(){
        this.linkedFeatures = new LinkedFeatureList();
        // add binds if not null
        if (this.binds != null){
            this.linkedFeatures.addOnly(this.binds);
        }
        // initialise persistent feature and content
        if (this.persistentLinkedFeatures != null){
            for (F linked : this.persistentLinkedFeatures){
                this.linkedFeatures.addOnly(linked);
            }
        }
        else{
            this.persistentLinkedFeatures = new PersistentLinkedFeatureList(null);
        }
    }

    protected void setDbXrefs(Collection<Xref> persistentXrefs){
        if (persistentXrefs instanceof AbstractIntactFeature.PersistentXrefList){
            this.persistentXrefs = (PersistentXrefList)persistentXrefs;
            this.identifiers = null;
            this.xrefs = null;
        }
        else{
            this.persistentXrefs = new PersistentXrefList(persistentXrefs);
            this.identifiers = null;
            this.xrefs = null;
        }
    }

    protected void setAnnotations(Collection<Annotation> annotations) {
        this.annotations = annotations;
    }

    protected void initialiseAliases(){
        this.aliases = new ArrayList<Alias>();
    }

    protected void setAliases(Collection<Alias> aliases) {
        this.aliases = aliases;
    }

    protected void setRanges(Collection<Range> ranges) {
        this.ranges = ranges;
    }

    protected void setDbLinkedFeatures(Collection<F> linkedFeatures) {
        if (linkedFeatures instanceof AbstractIntactFeature.PersistentLinkedFeatureList){
            this.persistentLinkedFeatures = (PersistentLinkedFeatureList)linkedFeatures;
            this.linkedFeatures = null;
        }
        else{
            this.persistentLinkedFeatures = new PersistentLinkedFeatureList(linkedFeatures);
            this.linkedFeatures = null;
        }
    }

    protected void setRelatedLinkedFeatures(Collection<F> relatedLinkedFeatures) {
        this.relatedLinkedFeatures = relatedLinkedFeatures;
    }

    protected void setRelatedBindings(Collection<F> relatedBindings) {
        this.relatedBindings = relatedBindings;
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
        // write the xrefs
        oos.writeObject(getDbXrefs());
        // write the linked features
        oos.writeObject(getDbLinkedFeatures());
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
        // read default xrefs
        setDbXrefs((Collection<Xref>)ois.readObject());
        // read default linked features
        setDbLinkedFeatures((Collection<F>)ois.readObject());
    }

    protected class FeatureIdentifierList extends AbstractListHavingProperties<Xref> {
        public FeatureIdentifierList(){
            super();
        }

        @Override
        protected void processAddedObjectEvent(Xref added) {
            if (!added.equals(acRef)){
                processAddedIdentifierEvent(added);
                persistentXrefs.add(added);
            }
        }

        @Override
        protected void processRemovedObjectEvent(Xref removed) {
            if (!removed.equals(acRef)){
                processRemovedIdentifierEvent(removed);
                persistentXrefs.remove(removed);
            }
            else{
                super.addOnly(acRef);
                throw new UnsupportedOperationException("Cannot remove the database accession of a Feature object from its list of identifiers.");
            }
        }

        @Override
        protected void clearProperties() {
            clearPropertiesLinkedToIdentifiers();
            persistentXrefs.retainAll(getXrefs());
            if (acRef != null){
                super.addOnly(acRef);
            }
        }
    }

    protected class FeatureXrefList extends AbstractListHavingProperties<Xref> {
        public FeatureXrefList(){
            super();
        }

        @Override
        protected void processAddedObjectEvent(Xref added) {
            persistentXrefs.add(added);
        }

        @Override
        protected void processRemovedObjectEvent(Xref removed) {
            persistentXrefs.remove(removed);
        }

        @Override
        protected void clearProperties() {
            persistentXrefs.retainAll(getIdentifiers());
        }
    }

    protected class PersistentXrefList extends AbstractCollectionWrapper<Xref> {

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
            // nothing to do
        }

        @Override
        protected boolean needToPreProcessElementToRemove(Object o) {
            return false;
        }
    }

    protected class LinkedFeatureList extends AbstractListHavingProperties<F> {
        public LinkedFeatureList(){
            super();
        }

        @Override
        protected void processAddedObjectEvent(F added) {
            if (binds == null){
                binds = added;
            }
            else{
                persistentLinkedFeatures.add(added);
            }
        }

        @Override
        protected void processRemovedObjectEvent(F removed) {
            if (binds == removed){
                binds = null;
                if (!persistentLinkedFeatures.isEmpty()){
                    binds = persistentLinkedFeatures.iterator().next();
                }
            }
            else{
                persistentLinkedFeatures.remove(removed);
            }
        }

        @Override
        protected void clearProperties() {
            binds = null;
            persistentLinkedFeatures.clear();
        }
    }

    protected class PersistentLinkedFeatureList extends AbstractCollectionWrapper<F> {

        public PersistentLinkedFeatureList(Collection<F> persistentBag){
            super(persistentBag);
        }

        @Override
        protected boolean needToPreProcessElementToAdd(F added) {
            return false;
        }

        @Override
        protected F processOrWrapElementToAdd(F added) {
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
