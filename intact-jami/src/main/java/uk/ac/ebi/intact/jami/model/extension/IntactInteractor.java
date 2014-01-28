package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
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
    private PersistentChecksumList checksums;
    private InteractorXrefList xrefs;
    private PersistentAnnotationList annotations;
    private PersistentAliasList aliases;
    private Organism organism;
    private CvTerm interactorType;
    private PersistentXrefList persistentXrefs;
    private Collection<Entity> activeInstances;

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

    @OneToMany( mappedBy = "interactor", targetEntity = AbstractIntactEntity.class)
    @Target(AbstractIntactEntity.class)
    public Collection<Entity> getActiveInstances() {
        if (this.activeInstances == null){
            this.activeInstances = new ArrayList<Entity>();
        }
        return activeInstances;
    }

    @Override
    public String toString() {
        return shortName + (organism != null ? ", " + organism.toString() : "") + (interactorType != null ? ", " + interactorType.toString() : "")  ;
    }

    @OneToMany( mappedBy = "parent", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = InteractorXref.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(InteractorXref.class)
    public Collection<Xref> getPersistentXrefs() {
        if (persistentXrefs == null){
            persistentXrefs = new PersistentXrefList(null);
        }
        return persistentXrefs.getWrappedList();
    }

    @OneToMany( mappedBy = "parent", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = InteractorChecksum.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(InteractorChecksum.class)
    public Collection<Checksum> getPersistentChecksums() {
        if (checksums == null){
            checksums = new PersistentChecksumList(null);
        }
        return this.checksums.getWrappedList();
    }

    @OneToMany( mappedBy = "parent", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = InteractorAnnotation.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(InteractorAnnotation.class)
    public Collection<Annotation> getPersistentAnnotations() {
        if (annotations == null){
            annotations = new PersistentAnnotationList(null);
        }
        return this.annotations.getWrappedList();
    }

    @OneToMany( mappedBy = "parent", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = InteractorAlias.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(InteractorAlias.class)
    public Collection<Alias> getPersistentAliases() {
        if (aliases == null){
            aliases = new PersistentAliasList(null);
        }
        return this.aliases.getWrappedList();
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

    @Transient
    public boolean areChecksumsInitialized(){
        return Hibernate.isInitialized(getPersistentChecksums());
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
        this.annotations = new PersistentAnnotationList(null);
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
            IntactContext intactContext = ApplicationContextProvider.getBean(IntactContext.class);
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
        this.aliases = new PersistentAliasList(null);
    }

    protected void initialiseChecksums(){
        this.checksums = new PersistentChecksumList(null);
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
    }

    protected void setPersistentAliases(Collection<Alias> aliases) {
        if (aliases instanceof PersistentAliasList){
            this.aliases = (PersistentAliasList)aliases;
        }
        else{
            this.aliases = new PersistentAliasList(aliases);
        }
    }

    protected void setPersistentAnnotations(Collection<Annotation> annotations) {
        if (annotations instanceof PersistentAnnotationList){
            this.annotations = (PersistentAnnotationList)annotations;
        }
        else{
            this.annotations = new PersistentAnnotationList(annotations);
        }
    }

    protected void setPersistentChecksums(Collection<Checksum> checksums) {
        if (checksums instanceof PersistentChecksumList){
            this.checksums = (PersistentChecksumList)checksums;
        }
        else{
            this.checksums = new PersistentChecksumList(checksums);
        }
    }

    protected void initialiseDefaultInteractorType() {
        this.interactorType = IntactUtils.createIntactMITerm(Interactor.UNKNOWN_INTERACTOR, Interactor.UNKNOWN_INTERACTOR_MI, IntactUtils.INTERACTOR_TYPE_OBJCLASS);
    }

    protected void setActiveInstances(Collection<Entity> activeInstances) {
        this.activeInstances = activeInstances;
    }

    protected class InteractorIdentifierList extends AbstractListHavingProperties<Xref> {
        public InteractorIdentifierList(){
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

    protected class InteractorXrefList extends AbstractListHavingProperties<Xref> {
        public InteractorXrefList(){
            super();
        }

        @Override
        protected void processAddedObjectEvent(Xref added) {
            Xref persistentRef = added;
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
    }

    protected class PersistentChecksumList extends AbstractCollectionWrapper<Checksum> {

        public PersistentChecksumList(Collection<Checksum> persistentBag){
            super(persistentBag);
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
    }
}
