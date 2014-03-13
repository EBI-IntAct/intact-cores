package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.*;
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
import java.util.ArrayList;
import java.util.Collection;

/**
 * Intact basic implementation of interactors
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>16/01/14</pre>
 */
@javax.persistence.Entity
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
@Table(name = "ia_molecule")
@DiscriminatorColumn(name = "category", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue( "interactor" )
@Cacheable
public class IntactInteractor extends AbstractIntactPrimaryObject implements Interactor{

    private String shortName;
    private String fullName;
    private InteractorIdentifierList identifiers;
    private Collection<Checksum> checksums;
    private InteractorXrefList xrefs;
    private Collection<Annotation> annotations;
    private Collection<Alias> aliases;
    private Organism organism;
    private CvTerm interactorType;

    private PersistentAnnotationList persistentAnnotations;
    private PersistentAliasList persistentAliases;
    private PersistentXrefList persistentXrefs;
    private Collection<Participant> activeInstances;

    private Xref acRef;

    protected IntactInteractor(){
        super();
    }

    public IntactInteractor(String name, CvTerm type){
        if (name == null || (name != null && name.length() == 0)){
            throw new IllegalArgumentException("The short name cannot be null or empty.");
        }
        this.shortName = name;
        this.interactorType = type;
    }

    public IntactInteractor(String name, String fullName, CvTerm type){
        this(name, type);
        this.fullName = fullName;
    }

    public IntactInteractor(String name, CvTerm type, Organism organism){
        this(name, type);
        this.organism = organism;
    }

    public IntactInteractor(String name, String fullName, CvTerm type, Organism organism){
        this(name, fullName, type);
        this.organism = organism;
    }

    public IntactInteractor(String name, CvTerm type, Xref uniqueId){
        this(name, type);
        getIdentifiers().add(uniqueId);
    }

    public IntactInteractor(String name, String fullName, CvTerm type, Xref uniqueId){
        this(name, fullName, type);
        getIdentifiers().add(uniqueId);
    }

    public IntactInteractor(String name, CvTerm type, Organism organism, Xref uniqueId){
        this(name, type, organism);
        getIdentifiers().add(uniqueId);
    }

    public IntactInteractor(String name, String fullName, CvTerm type, Organism organism, Xref uniqueId){
        this(name, fullName, type, organism);
        getIdentifiers().add(uniqueId);
    }

    public IntactInteractor(String name){
        if (name == null || (name != null && name.length() == 0)){
            throw new IllegalArgumentException("The short name cannot be null or empty.");
        }
        this.shortName = name;
    }

    public IntactInteractor(String name, String fullName){
        this(name);
        this.fullName = fullName;
    }

    public IntactInteractor(String name, Organism organism){
        this(name);
        this.organism = organism;
    }

    public IntactInteractor(String name, String fullName, Organism organism){
        this(name, fullName);
        this.organism = organism;
    }

    public IntactInteractor(String name, Xref uniqueId){
        this(name);
        getIdentifiers().add(uniqueId);
    }

    public IntactInteractor(String name, String fullName, Xref uniqueId){
        this(name, fullName);
        getIdentifiers().add(uniqueId);
    }

    public IntactInteractor(String name, Organism organism, Xref uniqueId){
        this(name, organism);
        getIdentifiers().add(uniqueId);
    }

    public IntactInteractor(String name, String fullName, Organism organism, Xref uniqueId){
        this(name, fullName, organism);
        getIdentifiers().add(uniqueId);
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
        if (name == null || (name != null && name.length() == 0)){
            throw new IllegalArgumentException("The short name cannot be null or empty.");
        }
        this.shortName = name.trim().toLowerCase();
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

    /**
     *
     * @return the first identifier in the list of identifiers or null if the list is empty
     */
    @Transient
    public Xref getPreferredIdentifier() {
        return !getIdentifiers().isEmpty() ? getIdentifiers().iterator().next() : null;
    }

    @Transient
    public Collection<Checksum> getChecksums() {
        if (checksums == null){
            initialiseChecksums();
        }
        return this.checksums;
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
        if (aliases == null){
            initialiseAliases();
        }
        return this.aliases;
    }

    @ManyToOne(targetEntity = IntactOrganism.class)
    @JoinColumn( name = "biosource_ac", referencedColumnName = "ac")
    @Target(IntactOrganism.class)
    public Organism getOrganism() {
        return this.organism;
    }

    public void setOrganism(Organism organism) {
        this.organism = organism;
    }

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn( name = "interactortype_ac" )
    @Target(IntactCvTerm.class)
    public CvTerm getInteractorType() {
        if (this.interactorType == null){
            initialiseDefaultInteractorType();
        }
        return this.interactorType;
    }

    public void setInteractorType(CvTerm interactorType) {
        this.interactorType = interactorType;
    }

    @OneToMany( mappedBy = "interactor", targetEntity = IntactParticipantEvidence.class)
    @Target(IntactParticipantEvidence.class)
    public Collection<Participant> getActiveInstances() {
        if (this.activeInstances == null){
            this.activeInstances = new ArrayList<Participant>();
        }
        return activeInstances;
    }

    @Override
    public String toString() {
        return shortName + (organism != null ? ", " + organism.toString() : "") + (interactorType != null ? ", " + interactorType.toString() : "")  ;
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = InteractorXref.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(InteractorXref.class)
    public Collection<Xref> getPersistentXrefs() {
        if (persistentXrefs == null){
            persistentXrefs = new PersistentXrefList(null);
        }
        return persistentXrefs.getWrappedList();
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = InteractorAnnotation.class)
    @JoinTable(
            name="ia_int2annot",
            joinColumns = @JoinColumn( name="interactor_ac"),
            inverseJoinColumns = @JoinColumn( name="annotation_ac")
    )
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(InteractorAnnotation.class)
    /**
     * WARNING: The join table is for backward compatibility with intact-core.
     * When intact-core will be removed, the join table would disappear wnd the relation would become
     * @JoinColumn(name="parent_ac", referencedColumnName="ac")
     */
    public Collection<Annotation> getPersistentAnnotations() {
        if (persistentAnnotations == null){
            persistentAnnotations = new PersistentAnnotationList(null);
        }
        return this.persistentAnnotations.getWrappedList();
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = InteractorAlias.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(InteractorAlias.class)
    public Collection<Alias> getPersistentAliases() {
        if (persistentAliases == null){
            persistentAliases = new PersistentAliasList(null);
        }
        return this.persistentAliases.getWrappedList();
    }

    @Transient
    public boolean areXrefsInitialized(){
        return Hibernate.isInitialized(getPersistentXrefs());
    }

    @Transient
    public boolean areAliasesInitialized(){
        return Hibernate.isInitialized(getPersistentAliases());
    }

    @Transient
    public boolean areAnnotationsInitialized(){
        return Hibernate.isInitialized(getPersistentAnnotations());
    }

    @Column(name = "objclass", nullable = false, insertable = false, updatable = false)
    @NotNull
    protected String getObjClass(){
        return "uk.ac.ebi.intact.model.InteractorImpl";
    }

    protected void setObjClass(String objClass){
        // nothing to do
    }

    protected void initialiseAnnotations(){
        this.annotations = getPersistentAnnotations();
    }

    protected void initialiseXrefs(){
        this.identifiers = new InteractorIdentifierList();
        this.xrefs = new InteractorXrefList();
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

    protected void initialiseAliases(){
        this.aliases = getPersistentAliases();
    }

    protected void initialiseAnnotationsWith(Collection<Annotation> annotations){
        if (annotations == null){
            this.annotations = new ArrayList<Annotation>();
        }
        else{
            this.annotations = annotations;
        }
    }

    protected void initialiseChecksums(){
        this.checksums = new ArrayList<Checksum>();
    }

    protected void initialiseChecksumsWith(Collection<Checksum> checksum){
        if (checksum == null){
            this.checksums = new ArrayList<Checksum>();
        }
        else{
            this.checksums = checksum;
        }
    }

    protected void processAddedIdentifierEvent(Xref added) {
        // nothing
    }

    protected void processRemovedIdentifierEvent(Xref removed) {
        // nothing
    }

    protected void clearPropertiesLinkedToIdentifiers() {
        // nothing
    }

    protected void setPersistentXrefs(Collection<Xref> persistentXrefs) {
        if (persistentXrefs instanceof PersistentXrefList){
            this.persistentXrefs = (PersistentXrefList)persistentXrefs;
        }
        else{
            this.persistentXrefs = new PersistentXrefList(persistentXrefs);
        }
        initialiseXrefs();
    }

    protected void setPersistentAliases(Collection<Alias> aliases) {
        if (aliases instanceof PersistentAliasList){
            this.persistentAliases = (PersistentAliasList)aliases;
        }
        else{
            this.persistentAliases = new PersistentAliasList(aliases);
        }
        initialiseAliases();
    }

    protected void setPersistentAnnotations(Collection<Annotation> annotations) {
        if (annotations instanceof PersistentAnnotationList){
            this.persistentAnnotations = (PersistentAnnotationList)annotations;
        }
        else{
            this.persistentAnnotations = new PersistentAnnotationList(annotations);
        }
        initialiseAnnotations();
    }

    protected void initialiseDefaultInteractorType() {
        this.interactorType = IntactUtils.createIntactMITerm(Interactor.UNKNOWN_INTERACTOR, Interactor.UNKNOWN_INTERACTOR_MI, IntactUtils.INTERACTOR_TYPE_OBJCLASS);
    }

    protected void setActiveInstances(Collection<Participant> activeInstances) {
        this.activeInstances = activeInstances;
    }

    protected class InteractorIdentifierList extends AbstractListHavingProperties<Xref> {
        public InteractorIdentifierList(){
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
        }
    }

    protected class InteractorXrefList extends AbstractListHavingProperties<Xref> {
        public InteractorXrefList(){
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

    protected class PersistentAnnotationList extends AbstractCollectionWrapper<Annotation> {

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

    protected class PersistentAliasList extends AbstractCollectionWrapper<Alias> {

        public PersistentAliasList(Collection<Alias> persistentBag){
            super(persistentBag);
        }

        @Override
        protected boolean needToPreProcessElementToAdd(Alias added) {
            return false;
        }

        @Override
        protected Alias processOrWrapElementToAdd(Alias added) {
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
            // do nothing
        }

        @Override
        protected boolean needToPreProcessElementToRemove(Object o) {
            return false;
        }
    }
}
