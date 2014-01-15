package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.model.Entity;
import psidev.psi.mi.jami.utils.XrefUtils;
import psidev.psi.mi.jami.utils.collection.AbstractCollectionWrapper;
import psidev.psi.mi.jami.utils.collection.AbstractListHavingProperties;
import uk.ac.ebi.intact.jami.model.AbstractIntactPrimaryObject;
import uk.ac.ebi.intact.jami.model.listener.LinkedFeatureListener;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract class for intact features
 *
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@javax.persistence.Entity
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
@Table(name = "ia_feature")
@DiscriminatorColumn(name = "category", discriminatorType = DiscriminatorType.STRING)
@EntityListeners(value = {LinkedFeatureListener.class})
public abstract class AbstractIntactFeature<P extends Entity, F extends Feature> extends AbstractIntactPrimaryObject implements Feature<P,F>{

    private String shortName;
    private String fullName;
    private Xref interpro;
    private FeatureIdentifierList identifiers;
    private FeatureXrefList xrefs;
    private Collection<Annotation> annotations;
    private Collection<Alias> aliases;
    private CvTerm type;
    private Collection<Range> ranges;

    private CvTerm interactionEffect;
    private CvTerm interactionDependency;

    private P participant;
    private Collection<F> linkedFeatures;

    private PersistentXrefList persistentXrefs;

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
     * @deprecated this is only for backward compatibility with intact core. Look at linkedFeatures instead
     */
    private F binds;

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

    @Column(name = "shortlabel", nullable = false)
    @Size( min = 1, max = IntactUtils.MAX_SHORT_LABEL_LEN )
    @NotNull
    public String getShortName() {
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
            this.interpro = new FeatureXref(interproDatabase, interpro, identityQualifier);
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

    @OneToMany( mappedBy = "parent", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = FeatureAnnotation.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(FeatureAnnotation.class)
    public Collection<Annotation> getAnnotations() {
        if (annotations == null){
            initialiseAnnotations();
        }
        return this.annotations;
    }

    @OneToMany( mappedBy = "parent", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = FeatureAlias.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(FeatureAlias.class)
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
        return this.type;
    }

    public void setType(CvTerm type) {
        this.type = type;
    }

    @OneToMany( mappedBy = "feature", orphanRemoval = true,
            cascade = {CascadeType.ALL}, targetEntity = IntactRange.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(IntactRange.class)
    public Collection<Range> getRanges() {
        if (ranges == null){
            initialiseRanges();
        }
        return this.ranges;
    }

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn( name = "interaction_effect_ac", referencedColumnName = "ac")
    @Target(IntactCvTerm.class)
    public CvTerm getInteractionEffect() {
        return this.interactionEffect;
    }

    public void setInteractionEffect(CvTerm effect) {
        this.interactionEffect = effect;
    }

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn( name = "interaction_dependency_ac", referencedColumnName = "ac")
    @Target(IntactCvTerm.class)
    public CvTerm getInteractionDependency() {
        return this.interactionDependency;
    }

    public void setInteractionDependency(CvTerm interactionDependency) {
        this.interactionDependency = interactionDependency;
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

    @Override
    public String toString() {
        return type != null ? type.toString() : (!ranges.isEmpty() ? "("+ranges.iterator().next().toString()+"...)" : " (-)");
    }

    @Transient
    /**
     * @deprecated for intact-core backward compatibility only. Use linkedFeatures instead
     */
    public F getBinds() {
        return binds;
    }

    /**
     *
     * @param binds
     * @deprecated for intact-core backward compatibility only. Use linkedFeatures instead
     */
    public void setBinds(F binds) {
        this.binds = binds;
    }

    protected void initialiseXrefs(){
        this.identifiers = new FeatureIdentifierList();
        this.xrefs = new FeatureXrefList();
        if (this.persistentXrefs != null){
            for (Xref ref : this.persistentXrefs){
                if (XrefUtils.isXrefAnIdentifier(ref)){
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
        this.linkedFeatures = new ArrayList<F>();
    }

    @OneToMany( mappedBy = "parent", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = FeatureXref.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(FeatureXref.class)
    protected Collection<Xref> getPersistentXrefs() {
        if (this.persistentXrefs == null){
            this.persistentXrefs = new PersistentXrefList(null);
        }
        return this.persistentXrefs.getWrappedList();
    }

    protected void setPersistentXrefs(Collection<Xref> persistentXrefs){
        this.persistentXrefs = new PersistentXrefList(persistentXrefs);
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

    protected void setLinkedFeatures(Collection<F> linkedFeatures) {
        this.linkedFeatures = linkedFeatures;
    }

    protected class FeatureIdentifierList extends AbstractListHavingProperties<Xref> {
        public FeatureIdentifierList(){
            super();
        }

        @Override
        protected void processAddedObjectEvent(Xref added) {
            processAddedIdentifierEvent(added);
            persistentXrefs.add(added);
        }

        @Override
        protected void processRemovedObjectEvent(Xref removed) {
            processRemovedIdentifierEvent(removed);
            persistentXrefs.remove(removed);
        }

        @Override
        protected void clearProperties() {
            clearPropertiesLinkedToIdentifiers();
            persistentXrefs.retainAll(getXrefs());
        }
    }

    protected class FeatureXrefList extends AbstractListHavingProperties<Xref> {
        public FeatureXrefList(){
            super();
        }

        @Override
        protected void processAddedObjectEvent(Xref added) {
            processAddedIdentifierEvent(added);
            persistentXrefs.add(added);
        }

        @Override
        protected void processRemovedObjectEvent(Xref removed) {
            processRemovedIdentifierEvent(removed);
            persistentXrefs.remove(removed);
        }

        @Override
        protected void clearProperties() {
            clearPropertiesLinkedToIdentifiers();
            persistentXrefs.retainAll(getIdentifiers());
        }
    }

    protected class PersistentXrefList extends AbstractCollectionWrapper<Xref> {

        public PersistentXrefList(Collection<Xref> persistentBag){
            super(persistentBag);
        }

        @Override
        protected boolean needToPreProcessElementToAdd(Xref added) {
            if (!(added instanceof FeatureXref)){
                return true;
            }
            else{
                FeatureXref termXref = (FeatureXref)added;
                if (termXref.getParent() != null && termXref.getParent() != this){
                    return true;
                }
            }
            return false;
        }

        @Override
        protected Xref processOrWrapElementToAdd(Xref added) {
            return new FeatureXref(added.getDatabase(), added.getId(), added.getVersion(), added.getQualifier());
        }
    }
}
