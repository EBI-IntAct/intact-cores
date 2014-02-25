package uk.ac.ebi.intact.jami.model.extension;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.model.impl.DefaultCvTerm;
import psidev.psi.mi.jami.model.impl.DefaultXref;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import psidev.psi.mi.jami.utils.CvTermUtils;
import psidev.psi.mi.jami.utils.XrefUtils;
import psidev.psi.mi.jami.utils.collection.AbstractCollectionWrapper;
import psidev.psi.mi.jami.utils.collection.AbstractListHavingProperties;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.context.IntactContext;
import uk.ac.ebi.intact.jami.model.AbstractIntactPrimaryObject;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.*;

/**
 * IntAct implementation of publication.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13/01/14</pre>
 */
@javax.persistence.Entity
@Table( name = "ia_publication" )
@Cacheable
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
@DiscriminatorValue("simple_publication")
public class IntactPublication extends AbstractIntactPrimaryObject implements Publication{
    private String title;
    private String journal;
    private Date publicationDate;
    private List<String> authors;
    private PublicationIdentifierList identifiers;
    private PublicationXrefList xrefs;
    private PublicationAnnotationList annotations;
    private Collection<Experiment> experiments;
    private CurationDepth curationDepth;
    private Date releasedDate;
    private Source source;

    private Xref pubmedId;
    private Xref doi;
    private Xref imexId;

    private PersistentXrefList persistentXrefs;

    private Xref acRef;

    public IntactPublication(){
        this.curationDepth = CurationDepth.undefined;
    }

    public IntactPublication(Xref identifier){
        this();

        if (identifier != null){
            getIdentifiers().add(identifier);
        }
    }

    public IntactPublication(Xref identifier, CurationDepth curationDepth, Source source){
        this(identifier);
        if (curationDepth != null){
            this.curationDepth = curationDepth;
        }
        this.source = source;
    }

    public IntactPublication(Xref identifier, String imexId, Source source){
        this(identifier, CurationDepth.IMEx, source);
        assignImexId(imexId);
    }

    public IntactPublication(String pubmed){
        this.curationDepth = CurationDepth.undefined;

        if (pubmed != null){
            setPubmedId(pubmed);
        }
    }

    public IntactPublication(String pubmed, CurationDepth curationDepth, Source source){
        this(pubmed);
        if (curationDepth != null){
            this.curationDepth = curationDepth;
        }
        this.source = source;
    }

    public IntactPublication(String pubmed, String imexId, Source source){
        this(pubmed, CurationDepth.IMEx, source);
        assignImexId(imexId);
    }

    public IntactPublication(String title, String journal, Date publicationDate){
        this.title = title;
        this.journal = journal;
        this.publicationDate = publicationDate;
        this.curationDepth = CurationDepth.undefined;
    }

    public IntactPublication(String title, String journal, Date publicationDate, CurationDepth curationDepth, Source source){
        this(title, journal, publicationDate);
        if (curationDepth != null){
            this.curationDepth = curationDepth;
        }
        this.source = source;
    }

    public IntactPublication(String title, String journal, Date publicationDate, String imexId, Source source){
        this(title, journal, publicationDate, CurationDepth.IMEx, source);
        assignImexId(imexId);
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

    @Transient
    public String getPubmedId() {
        if (this.identifiers == null){
            initialiseXrefs();
        }
        return this.pubmedId != null ? this.pubmedId.getId() : null;
    }

    public void setPubmedId(String pubmedId) {
        PublicationIdentifierList identifiers = (PublicationIdentifierList)getIdentifiers();

        // add new pubmed if not null
        if (pubmedId != null){
            CvTerm pubmedDatabase = IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI);
            CvTerm identityQualifier = IntactUtils.createMIQualifier(Xref.PRIMARY, Xref.PRIMARY_MI);
            // first remove old pubmed if not null
            if (this.pubmedId != null){
                identifiers.remove(this.pubmedId);
            }
            this.pubmedId = new PublicationXref(pubmedDatabase, pubmedId, identityQualifier);
            identifiers.add(this.pubmedId);
        }
        // remove all pubmed if the collection is not empty
        else if (!identifiers.isEmpty()) {
            XrefUtils.removeAllXrefsWithDatabase(identifiers, Xref.PUBMED_MI, Xref.PUBMED);
            this.pubmedId = null;
        }
    }

    @Transient
    public String getDoi() {
        if (this.identifiers == null){
            initialiseXrefs();
        }
        return this.doi != null ? this.doi.getId() : null;
    }

    public void setDoi(String doi) {
        PublicationIdentifierList identifiers = (PublicationIdentifierList)getIdentifiers();
        // add new doi if not null
        if (doi != null){
            CvTerm doiDatabase = IntactUtils.createMIDatabase(Xref.DOI, Xref.DOI_MI);
            CvTerm identityQualifier = IntactUtils.createMIQualifier(Xref.PRIMARY, Xref.PRIMARY_MI);
            // first remove old doi if not null
            if (this.doi != null){
                identifiers.remove(this.doi);
            }
            this.doi = new PublicationXref(doiDatabase, doi, identityQualifier);
            identifiers.add(this.doi);
        }
        // remove all doi if the collection is not empty
        else if (!identifiers.isEmpty()) {
            XrefUtils.removeAllXrefsWithDatabase(identifiers, Xref.DOI_MI, Xref.DOI);
            this.doi = null;
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
    public String getImexId() {
        if (this.xrefs == null){
            initialiseXrefs();
        }
        return this.imexId != null ? this.imexId.getId() : null;
    }

    public void assignImexId(String identifier) {
        PublicationXrefList xrefs = (PublicationXrefList)getXrefs();
        // add new imex if not null
        if (identifier != null){
            CvTerm imexDatabase = IntactUtils.createMIDatabase(Xref.IMEX, Xref.IMEX_MI);
            CvTerm imexPrimaryQualifier = IntactUtils.createMIQualifier(Xref.IMEX_PRIMARY, Xref.IMEX_PRIMARY_MI);
            // first remove old imex if not null
            if (this.imexId != null){
                xrefs.remove(this.imexId);
            }
            this.imexId = new DefaultXref(imexDatabase, identifier, imexPrimaryQualifier);
            xrefs.add(this.imexId);

            this.curationDepth = CurationDepth.IMEx;
        }
        else if (this.imexId != null){
            throw new IllegalArgumentException("The imex id has to be non null.");
        }
    }

    @Column( name = "fullname", length = IntactUtils.MAX_FULL_NAME_LEN )
    @Size( max = IntactUtils.MAX_FULL_NAME_LEN )
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column( name = "journal", length = IntactUtils.MAX_FULL_NAME_LEN )
    @Size( max = IntactUtils.MAX_FULL_NAME_LEN )
    public String getJournal() {
        return this.journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "publication_date")
    public Date getPublicationDate() {
        return this.publicationDate;
    }

    public void setPublicationDate(Date date) {
        this.publicationDate = date;
    }

    @Transient
    public List<String> getAuthors() {
        if (authors == null){
            initialiseAuthors();
        }
        return this.authors;
    }

    @Transient
    public Collection<Annotation> getAnnotations() {
        if (annotations == null){
            initialiseAnnotations();
        }
        return this.annotations;
    }

    @Transient
    public Collection<Xref> getXrefs() {
        if (xrefs == null){
            initialiseXrefs();
        }
        return this.xrefs;
    }

    @Transient
    public Collection<Experiment> getExperiments() {
        if (experiments == null){
            initialiseExperiments();
        }
        return this.experiments;
    }

    @Transient
    public CurationDepth getCurationDepth() {
        if (this.curationDepth == null){
            this.curationDepth = CurationDepth.undefined;
        }
        return this.curationDepth;
    }

    public void setCurationDepth(CurationDepth curationDepth) {

        if (curationDepth == null && imexId != null) {
            this.curationDepth = CurationDepth.IMEx;
        }
        else if (curationDepth == null) {
            this.curationDepth = CurationDepth.undefined;
        }
        else {
            this.curationDepth = curationDepth;
        }
    }

    @Transient
    public Date getReleasedDate() {
        return this.releasedDate;
    }

    public void setReleasedDate(Date released) {
        this.releasedDate = released;
    }

    @Transient
    public Source getSource() {
        return this.source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public boolean addExperiment(Experiment exp) {
        if (exp == null){
            return false;
        }
        else {
            if (getExperiments().add(exp)){
                exp.setPublication(this);
                return true;
            }
            return false;
        }
    }

    public boolean removeExperiment(Experiment exp) {
        if (exp == null){
            return false;
        }
        else {
            if (getExperiments().remove(exp)){
                exp.setPublication(null);
                return true;
            }
            return false;
        }
    }

    public boolean addAllExperiments(Collection<? extends Experiment> exps) {
        if (exps == null){
            return false;
        }
        else {
            boolean added = false;

            for (Experiment exp : exps){
                if (addExperiment(exp)){
                    added = true;
                }
            }
            return added;
        }
    }

    public boolean removeAllExperiments(Collection<? extends Experiment> exps) {
        if (exps == null){
            return false;
        }
        else {
            boolean removed = false;

            for (Experiment exp : exps){
                if (removeExperiment(exp)){
                    removed = true;
                }
            }
            return removed;
        }
    }

    @Override
    public String toString() {
        return (imexId != null ? imexId.getId() : (pubmedId != null ? pubmedId.getId() : (doi != null ? doi.getId() : (title != null ? title : "-"))));
    }

    @OneToMany( mappedBy = "parent", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = PublicationAnnotation.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(PublicationAnnotation.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    public Collection<Annotation> getPersistentAnnotations() {
        if (this.annotations == null){
            this.annotations = new PublicationAnnotationList(null);
        }
        return this.annotations.getWrappedList();
    }

    @Transient
    public boolean areXrefsInitialized(){
        return Hibernate.isInitialized(getPersistentXrefs());
    }

    @Transient
    public boolean areAnnotationsInitialized(){
        return Hibernate.isInitialized(getPersistentAnnotations());
    }

    @Transient
    public boolean areExperimentsInitialized(){
        return Hibernate.isInitialized(getExperiments());
    }

    @OneToMany( mappedBy = "parent", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = PublicationXref.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(PublicationXref.class)
    public Collection<Xref> getPersistentXrefs() {
        if (this.persistentXrefs == null){
            this.persistentXrefs = new PersistentXrefList(null);
        }
        return this.persistentXrefs.getWrappedList();
    }

    private void initialiseXrefs(){
        this.identifiers = new PublicationIdentifierList();
        this.xrefs = new PublicationXrefList();
        if (this.persistentXrefs != null){
            for (Xref ref : this.persistentXrefs){
                if (XrefUtils.isXrefAnIdentifier(ref) || XrefUtils.doesXrefHaveQualifier(ref, Xref.PRIMARY_MI, Xref.PRIMARY)){
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

    private void initialiseExperiments(){
        this.experiments = new ArrayList<Experiment>();
    }

    private void processAddedIdentifierEvent(Xref added) {

        // the added identifier is pubmed and it is not the current pubmed identifier
        if (pubmedId != added && XrefUtils.isXrefFromDatabase(added, Xref.PUBMED_MI, Xref.PUBMED)){
            // the current pubmed identifier is not identity, we may want to set pubmed Identifier
            if (!XrefUtils.doesXrefHaveQualifier(pubmedId, Xref.IDENTITY_MI, Xref.IDENTITY) && !XrefUtils.doesXrefHaveQualifier(pubmedId, Xref.PRIMARY_MI, Xref.PRIMARY)){
                // the pubmed identifier is not set, we can set the pubmed
                if (pubmedId == null){
                    pubmedId = added;
                }
                else if (XrefUtils.doesXrefHaveQualifier(added, Xref.IDENTITY_MI, Xref.IDENTITY) || XrefUtils.doesXrefHaveQualifier(added, Xref.PRIMARY_MI, Xref.PRIMARY)){
                    pubmedId = added;
                }
                // the added xref is secondary object and the current pubmed is not a secondary object, we reset pubmed identifier
                else if (!XrefUtils.doesXrefHaveQualifier(pubmedId, Xref.SECONDARY_MI, Xref.SECONDARY)
                        && XrefUtils.doesXrefHaveQualifier(added, Xref.SECONDARY_MI, Xref.SECONDARY)){
                    pubmedId = added;
                }
            }
        }
        // the added identifier is doi and it is not the current doi identifier
        else if (doi != added && XrefUtils.isXrefFromDatabase(added, Xref.DOI_MI, Xref.DOI)){
            // the current doi identifier is not identity, we may want to set doi
            if (!XrefUtils.doesXrefHaveQualifier(doi, Xref.IDENTITY_MI, Xref.IDENTITY) && !XrefUtils.doesXrefHaveQualifier(doi, Xref.PRIMARY_MI, Xref.PRIMARY)){
                // the doi is not set, we can set the doi
                if (doi == null){
                    doi = added;
                }
                else if (XrefUtils.doesXrefHaveQualifier(added, Xref.IDENTITY_MI, Xref.IDENTITY) || XrefUtils.doesXrefHaveQualifier(added, Xref.PRIMARY_MI, Xref.PRIMARY)){
                    doi = added;
                }
                // the added xref is secondary object and the current doi is not a secondary object, we reset doi
                else if (!XrefUtils.doesXrefHaveQualifier(doi, Xref.SECONDARY_MI, Xref.SECONDARY)
                        && XrefUtils.doesXrefHaveQualifier(added, Xref.SECONDARY_MI, Xref.SECONDARY)){
                    doi = added;
                }
            }
        }
    }

    private void processRemovedIdentifierEvent(Xref removed) {
        // the removed identifier is pubmed
        if (pubmedId != null && pubmedId.equals(removed)){
            pubmedId = XrefUtils.collectFirstIdentifierWithDatabase(getIdentifiers(), Xref.PUBMED_MI, Xref.PUBMED);
        }
        // the removed identifier is doi
        else if (doi != null && doi.equals(removed)){
            doi = XrefUtils.collectFirstIdentifierWithDatabase(getIdentifiers(), Xref.DOI_MI, Xref.DOI);
        }
    }

    private void clearPropertiesLinkedToIdentifiers() {
        pubmedId = null;
        doi = null;
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

    protected void initialiseAuthors(){
        this.authors = new ArrayList<String>();
    }

    private void setPersistentXrefs(Collection<Xref> persistentXrefs){
        if (persistentXrefs instanceof PersistentXrefList){
            this.persistentXrefs = (PersistentXrefList)persistentXrefs;
        }
        else{
            this.persistentXrefs = new PersistentXrefList(persistentXrefs);
        }
    }

    private void initialiseAnnotations() {
        this.annotations = new PublicationAnnotationList(null);
        for (Annotation annot : this.annotations){
            processAddedAnnotationEvent(annot);
        }
    }

    private void processAddedAnnotationEvent(Annotation added) {
        if (added.getValue() != null && getAuthors().isEmpty() && AnnotationUtils.doesAnnotationHaveTopic(added, Annotation.AUTHOR_MI, Annotation.AUTHOR)){
            if (added.getValue().contains(", ")){
                getAuthors().addAll(Arrays.asList(added.getValue().split(", ")));
            }
            else{
                getAuthors().add(added.getValue());
            }
        }
    }

    private void processRemovedAnnotationEvent(Annotation removed) {
        if (!getAuthors().isEmpty() && AnnotationUtils.doesAnnotationHaveTopic(removed, Annotation.AUTHOR_MI, Annotation.AUTHOR)){
            String author = StringUtils.join(getAuthors(), ", ");
            if (author.equalsIgnoreCase(removed.getValue())){
                getAuthors().clear();
            }
        }
    }

    protected void clearPropertiesLinkedToAnnotations() {
        getAuthors().clear();
    }

    protected void setExperiments(Collection<Experiment> experiments) {
        this.experiments = experiments;
    }

    private void setPersistentAnnotations(Collection<Annotation> annotations) {
        this.annotations = new PublicationAnnotationList(annotations);
    }

    private class PublicationIdentifierList extends AbstractListHavingProperties<Xref> {
        public PublicationIdentifierList(){
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

    private class PublicationXrefList extends AbstractListHavingProperties<Xref> {
        public PublicationXrefList(){
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
    }

    private class PublicationAnnotationList extends AbstractCollectionWrapper<Annotation> {
        public PublicationAnnotationList(Collection<Annotation> annots){
            super(annots);
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
                processRemovedAnnotationEvent((Annotation)o);
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
}
