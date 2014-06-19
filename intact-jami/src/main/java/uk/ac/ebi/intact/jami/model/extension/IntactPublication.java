package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.*;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.model.Source;
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
import uk.ac.ebi.intact.jami.model.lifecycle.*;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.text.ParseException;
import java.util.*;

/**
 * IntAct implementation of publication.
 *
 * NOTE: The publication ac is automatically added as an identifier in getIdentifiers but is not persisted in getDbXrefs.
 * The getIdentifiers.remove will thrown an UnsupportedOperationException if someone tries to remove the AC identifier from the list of identifiers
 * NOTE: for backward compatibility, journal, publication date and released date are not persistent. When intact-core is removed, they should become persistent and
 * removed from the persistentAnnotations
 * NOTE: getIdentifiers and getXrefs are not persistent methods annotated with hibernate annotations. All the xrefs present in identifiers
 * and xrefs are persisted in the same table for backward compatibility with intact-core. So the persistent xrefs are available with the getDbXrefs method.
 * For HQL queries, the method getDbXrefs should be used because is annotated with hibernate annotations.
 * However, getDbXrefs should not be used directly to add/remove xrefs because it could mess up with the state of the object. Only the synchronizers
 * can use it this way before persistence.
 * NOTE: getAnnotations is not persistent. For HQL queries, the method getDbAnnotations should be used because is annotated with hibernate annotations.
 * However, getDbAnnotations should not be used directly to add/remove annotations because it could mess up with the state of the object. Only the synchronizers
 * can use it this way before persistence. The access type of DbAnnotations is private as it does not have to be used by the synchronizers neither.
 * NOTE: authors are stored as an annotation
 *
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13/01/14</pre>
 */
@Entity
@Table(name = "ia_publication")
@Cacheable
public class IntactPublication extends AbstractIntactPrimaryObject implements Publication, Releasable{
    private String title;
    private String journal;
    private Date publicationDate;
    private List<String> authors;
    private PublicationIdentifierList identifiers;
    private PublicationXrefList xrefs;
    private Collection<Experiment> experiments;
    private CurationDepth curationDepth;
    private Date releasedDate;
    private Source source;
    private PublicationAnnotationList annotations;

    private Xref pubmedId;
    private Xref doi;
    private Xref imexId;

    private PersistentXrefList persistentXrefs;
    private PersistentAnnotationList persistentAnnotations;

    private Xref acRef;

    private String shortLabel;
    private List<LifeCycleEvent> lifecycleEvents;
    private LifeCycleStatus status;
    private User currentOwner;
    private User currentReviewer;

    private CvTerm cvStatus;

    public IntactPublication(){
        this.curationDepth = CurationDepth.undefined;
        this.status = LifeCycleStatus.NEW;
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
        this.status = LifeCycleStatus.NEW;

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
        this.status = LifeCycleStatus.NEW;
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


    /**
     * For backward compatibility, it is not persistent and kept as annotation.
     * In the future, should become
     * @Column( name = "journal", length = IntactUtils.MAX_FULL_NAME_LEN )
     * @Size( max = IntactUtils.MAX_FULL_NAME_LEN )
     */
    @Transient
    public String getJournal() {
        // initialise annotations first
        getAnnotations();
        return this.journal;
    }

    public void setJournal(String journal) {
        Collection<Annotation> dbAnnots = getDbAnnotations();

        // add new journal if not null
        if (journal != null){
            CvTerm journalTopic = IntactUtils.createMITopic(Annotation.PUBLICATION_JOURNAL, Annotation.PUBLICATION_JOURNAL_MI);
            // first remove old journal if not null
            if (getJournal() != null){
                Annotation oldJournal = AnnotationUtils.collectFirstAnnotationWithTopicAndValue(getDbAnnotations(), Annotation.PUBLICATION_JOURNAL_MI, Annotation.PUBLICATION_JOURNAL, this.journal);
                if (oldJournal != null){
                    oldJournal.setValue(journal);
                }
                else{
                    getDbAnnotations().add(new PublicationAnnotation(journalTopic, journal));
                }
            }
            else{
                getDbAnnotations().add(new PublicationAnnotation(journalTopic, journal));
            }
            this.journal = journal;
        }
        // remove all journal if the collection is not empty
        else if (!dbAnnots.isEmpty()) {
            AnnotationUtils.removeAllAnnotationsWithTopic(getDbAnnotations(), Annotation.PUBLICATION_JOURNAL_MI, Annotation.PUBLICATION_JOURNAL);
            this.journal = null;
        }
    }

    /**
     * For backward compatibility, it is not persistent and kept as annotation.
     * In the future, should become
     * @Temporal(TemporalType.TIMESTAMP)
     * @Column(name = "publication_date")
     */
    @Transient
    public Date getPublicationDate() {
        // initialise annotations first
        getAnnotations();
        return this.publicationDate;
    }

    public void setPublicationDate(Date date) {
        Collection<Annotation> dbAnnots = getDbAnnotations();

        // add new journal if not null
        if (date != null){
            CvTerm yearTopic = IntactUtils.createMITopic(Annotation.PUBLICATION_YEAR, Annotation.PUBLICATION_YEAR_MI);
            // first remove old journal if not null
            if (getPublicationDate() != null){
                Annotation oldDate = AnnotationUtils.collectFirstAnnotationWithTopicAndValue(getDbAnnotations(), Annotation.PUBLICATION_YEAR_MI, Annotation.PUBLICATION_YEAR, IntactUtils.YEAR_FORMAT.format(this.publicationDate));
                if (oldDate != null){
                    oldDate.setValue(IntactUtils.YEAR_FORMAT.format(this.publicationDate));
                }
                else{
                    getDbAnnotations().add(new PublicationAnnotation(yearTopic, IntactUtils.YEAR_FORMAT.format(date)));
                }
            }
            else{
                getDbAnnotations().add(new PublicationAnnotation(yearTopic, IntactUtils.YEAR_FORMAT.format(date)));
            }
            this.publicationDate = date;
        }
        // remove all pub dates if the collection is not empty
        else if (!dbAnnots.isEmpty()) {
            AnnotationUtils.removeAllAnnotationsWithTopic(getDbAnnotations(), Annotation.PUBLICATION_YEAR_MI, Annotation.PUBLICATION_YEAR);
            this.publicationDate = null;
        }
    }

    @Column(name = "shortLabel", nullable = false, unique = true)
    @Size( min = 1, max = IntactUtils.MAX_SHORT_LABEL_LEN )
    @NotNull
    /**
     * @deprecated the publication shortLabel is deprecated. We should use getPubmedId or getDoi or getIdentifiers
     */
    @Deprecated
    public String getShortLabel() {
        return shortLabel;
    }

    /**
     * Set the shortlabel
     * @param shortLabel
     * @deprecated the shortlabel is deprecated and getPubmedId/getDOI should be used instead
     */
    @Deprecated
    public void setShortLabel( String shortLabel ) {
        if (shortLabel == null){
            throw new IllegalArgumentException("The short name cannot be null");
        }
        this.shortLabel = shortLabel.trim().toLowerCase();
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

    @OneToMany( mappedBy = "publication", cascade = { CascadeType.ALL }, targetEntity = IntactExperiment.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(IntactExperiment.class)
    public Collection<Experiment> getExperiments() {
        if (experiments == null){
            initialiseExperiments();
        }
        return this.experiments;
    }

    /**
     * For backward compatibility, it is not persistent and kept as annotation.
     * In the future, should become
     *  @Enumerated(EnumType.STRING)
     * @Column(name = "curation_depth", length = IntactUtils.MAX_SHORT_LABEL_LEN)
     */
    @Transient
    public CurationDepth getCurationDepth() {
        // initialise annotations first
        getAnnotations();
        if (this.curationDepth == null){
            this.curationDepth = CurationDepth.undefined;
        }
        return this.curationDepth;
    }

    public void setCurationDepth(CurationDepth curationDepth) {

        Collection<Annotation> dbAnnots = getDbAnnotations();

        // add new curation depth if not null
        if (curationDepth != null && !curationDepth.equals(CurationDepth.undefined)){
            CvTerm depthTopic = IntactUtils.createMITopic(Annotation.CURATION_DEPTH, Annotation.CURATION_DEPTH_MI);
            // first remove old curation depth if not null
            if (getCurationDepth() != null && !getCurationDepth().equals(CurationDepth.undefined)){
                Annotation oldDepth = AnnotationUtils.collectFirstAnnotationWithTopic(getDbAnnotations(), Annotation.CURATION_DEPTH_MI, Annotation.CURATION_DEPTH);
                if (oldDepth != null){
                    switch (curationDepth){
                        case IMEx:
                            oldDepth.setValue(Annotation.IMEX_CURATION);
                            break;
                        case MIMIx:
                            oldDepth.setValue(Annotation.MIMIX_CURATION);
                            break;
                        case rapid_curation:
                            oldDepth.setValue(Annotation.RAPID_CURATION);
                            break;
                        default:
                            getDbAnnotations().remove(oldDepth);
                    }
                }
                else{
                    switch (curationDepth){
                        case IMEx:
                            getDbAnnotations().add(new PublicationAnnotation(depthTopic, Annotation.IMEX_CURATION));
                            break;
                        case MIMIx:
                            getDbAnnotations().add(new PublicationAnnotation(depthTopic, Annotation.MIMIX_CURATION));
                            break;
                        case rapid_curation:
                            getDbAnnotations().add(new PublicationAnnotation(depthTopic, Annotation.CURATION_DEPTH));
                            break;
                        default:
                            break;
                    }
                }
            }
            else{
                switch (curationDepth){
                    case IMEx:
                        getDbAnnotations().add(new PublicationAnnotation(depthTopic, Annotation.IMEX_CURATION));
                        break;
                    case MIMIx:
                        getDbAnnotations().add(new PublicationAnnotation(depthTopic, Annotation.MIMIX_CURATION));
                        break;
                    case rapid_curation:
                        getDbAnnotations().add(new PublicationAnnotation(depthTopic, Annotation.CURATION_DEPTH));
                        break;
                    default:
                        break;
                }
            }
            this.curationDepth = curationDepth;
        }
        // remove all curation depth if the collection is not empty
        else if (!dbAnnots.isEmpty()) {
            AnnotationUtils.removeAllAnnotationsWithTopic(getDbAnnotations(), Annotation.CURATION_DEPTH_MI, Annotation.CURATION_DEPTH);
            this.curationDepth = CurationDepth.undefined;
        }
    }

    @Transient
    /**
     * For backward compatibility, it is not persistent and kept as annotation.
     * In the future, should become
     * @Temporal(TemporalType.TIMESTAMP)
     * @Column(name = "released_date")
     */
    public Date getReleasedDate() {
        // initialise lifecycle events first
        if (this.releasedDate == null && !getLifecycleEvents().isEmpty()){
            initialiseReleasedDate();
        }
        return this.releasedDate;
    }

    public void setReleasedDate(Date released) {
        this.releasedDate = released;
        for (LifeCycleEvent evt : getLifecycleEvents()){
            if (LifeCycleEventType.RELEASED.equals(evt.getEvent())){
                evt.setWhen(released);
            }
        }
    }

    @ManyToOne(targetEntity = IntactSource.class)
    @JoinColumn( name = "owner_ac", nullable = false, referencedColumnName = "ac" )
    @Target(IntactSource.class)
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

    @OneToMany( orphanRemoval = true, cascade = CascadeType.ALL, targetEntity = PublicationLifeCycleEvent.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @OrderBy("when, created")
    @Target(PublicationLifeCycleEvent.class)
    public List<LifeCycleEvent> getLifecycleEvents() {
        if (this.lifecycleEvents == null){
            this.lifecycleEvents = new ArrayList<LifeCycleEvent>();
        }
        return lifecycleEvents;
    }

    @Transient
    /**
     * NOTE: in the future, should be persisted and cvStatus should be removed
     */
    public LifeCycleStatus getStatus() {
        if (this.status == null){
            this.status = LifeCycleStatus.NEW;
        }
        return status;
    }

    public void setStatus( LifeCycleStatus status ) {
        this.status = status != null ? status : LifeCycleStatus.NEW;
        this.cvStatus = this.status.toCvTerm();
    }

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn( name = "status_ac", referencedColumnName = "ac" )
    @ForeignKey(name="FK_PUBLICATION_STATUS")
    @Target(IntactCvTerm.class)
    /**
     * NOTE: in the future, should be persisted and cvStatus should be removed
     * @deprecated use getStatus instead
     */
    @Deprecated
    public CvTerm getCvStatus() {
        if (this.cvStatus == null){
            this.cvStatus = getStatus().toCvTerm();
        }
        return this.cvStatus;
    }

    /**
     *
     * @param status
     * @deprecated use setStatus instead
     */
    @Deprecated
    public void setCvStatus( CvTerm status ) {
        this.cvStatus = status;
        this.status = LifeCycleStatus.toLifeCycleStatus(status);
    }

    @Override
    public void onReleased() {
        AnnotationUtils.removeAllAnnotationsWithTopic(getAnnotations(), null, "on-hold");
    }

    @Override
    public void onHold(String message) {
        Annotation onHold = AnnotationUtils.collectFirstAnnotationWithTopic(getAnnotations(), null, "on-hold");
        if (onHold != null){
            onHold.setValue(message);
        }
        else{
            getAnnotations().add(new PublicationAnnotation(IntactUtils.createMITopic("on-hold", null), message));
        }
    }

    @Override
    @Transient
    public boolean isOnHold() {
        return AnnotationUtils.collectFirstAnnotationWithTopic(getAnnotations(), null, "on-hold") != null;
    }

    @ManyToOne( targetEntity = User.class )
    @JoinColumn( name = "owner_pk", referencedColumnName = "ac" )
    @ForeignKey(name="FK_PUBLICATION_OWNER")
    @Target(User.class)
    public User getCurrentOwner() {
        return currentOwner;
    }

    public void setCurrentOwner( User currentOwner ) {
        this.currentOwner = currentOwner;
    }

    @ManyToOne( targetEntity = User.class )
    @JoinColumn( name = "reviewer_pk", referencedColumnName = "ac" )
    @ForeignKey(name="FK_PUBLICATION_REVIEWER")
    @Target(User.class)
    public User getCurrentReviewer() {
        return currentReviewer;
    }

    public void setCurrentReviewer( User currentReviewer ) {
        this.currentReviewer = currentReviewer;
    }

    @Transient
    public boolean areLifeCycleEventsInitialized(){
        return Hibernate.isInitialized(getLifecycleEvents());
    }

    @Override
    public String toString() {
        return (imexId != null ? imexId.getId() : (pubmedId != null ? pubmedId.getId() : (doi != null ? doi.getId() : (title != null ? title : "-"))));
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = PublicationAnnotation.class)
    @JoinTable(
            name="ia_pub2annot",
            joinColumns = @JoinColumn( name="publication_ac"),
            inverseJoinColumns = @JoinColumn( name="annotation_ac")
    )
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(PublicationAnnotation.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    /**
     * WARNING: The join table is for backward compatibility with intact-core.
     * When intact-core will be removed, the join table would disappear wnd the relation would become
     * @JoinColumn(name="parent_ac", referencedColumnName="ac")
     */
    public Collection<Annotation> getDbAnnotations() {
        if (this.persistentAnnotations == null){
            this.persistentAnnotations = new PersistentAnnotationList(null);
        }
        return this.persistentAnnotations.getWrappedList();
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
    public boolean areExperimentsInitialized(){
        return Hibernate.isInitialized(getExperiments());
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = PublicationXref.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(PublicationXref.class)
    public Collection<Xref> getDbXrefs() {
        if (this.persistentXrefs == null){
            this.persistentXrefs = new PersistentXrefList(null);
        }
        return this.persistentXrefs.getWrappedList();
    }

    protected void initialiseXrefs(){
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

    protected void initialiseExperiments(){
        this.experiments = new ArrayList<Experiment>();
    }

    protected void processAddedIdentifierEvent(Xref added) {

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

    protected void processRemovedIdentifierEvent(Xref removed) {
        // the removed identifier is pubmed
        if (pubmedId != null && pubmedId.equals(removed)){
            pubmedId = XrefUtils.collectFirstIdentifierWithDatabase(getIdentifiers(), Xref.PUBMED_MI, Xref.PUBMED);
        }
        // the removed identifier is doi
        else if (doi != null && doi.equals(removed)){
            doi = XrefUtils.collectFirstIdentifierWithDatabase(getIdentifiers(), Xref.DOI_MI, Xref.DOI);
        }
    }

    protected void clearPropertiesLinkedToIdentifiers() {
        pubmedId = null;
        doi = null;
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

    protected void clearPropertiesLinkedToXrefs() {
        imexId = null;
    }

    protected void initialiseAuthors(){
        this.authors = new ArrayList<String>();
    }

    protected void setDbXrefs(Collection<Xref> persistentXrefs){
        if (persistentXrefs instanceof PersistentXrefList){
            this.persistentXrefs = (PersistentXrefList)persistentXrefs;
        }
        else{
            this.persistentXrefs = new PersistentXrefList(persistentXrefs);
        }
    }

    protected void initialiseAnnotations() {
        this.annotations = new PublicationAnnotationList();
        this.authors = new ArrayList<String>();

        // initialise persistent annot and content
        if (this.persistentAnnotations != null){
            for (Annotation annot : this.persistentAnnotations){
                 processAddedAnnotationEvent(annot);
            }
        }
        else{
            this.persistentAnnotations = new PersistentAnnotationList(null);
        }
    }

    protected void processAddedAnnotationEvent(Annotation added) {
        if (AnnotationUtils.doesAnnotationHaveTopic(added, Annotation.AUTHOR_MI, Annotation.AUTHOR) && added.getValue() != null){
            if (added.getValue().contains(", ")){
                getAuthors().addAll(Arrays.asList(added.getValue().split(", ")));
            }
            else{
                getAuthors().add(added.getValue());
            }
        }
        // journal
        else if (AnnotationUtils.doesAnnotationHaveTopic(added, Annotation.PUBLICATION_JOURNAL_MI, Annotation.PUBLICATION_JOURNAL) && added.getValue() != null){
            this.journal = added.getValue();
        }
        // publication year
        else if (AnnotationUtils.doesAnnotationHaveTopic(added, Annotation.PUBLICATION_YEAR_MI, Annotation.PUBLICATION_YEAR) && added.getValue() != null){
            try {
                this.publicationDate = IntactUtils.YEAR_FORMAT.parse(added.getValue());
            } catch (ParseException e) {
                e.printStackTrace();
                this.publicationDate = null;
            }
        }
        // curation depth
        else if (AnnotationUtils.doesAnnotationHaveTopic(added, Annotation.CURATION_DEPTH_MI, Annotation.CURATION_DEPTH) && added.getValue() != null){
            if (Annotation.IMEX_CURATION.equalsIgnoreCase(added.getValue())){
                this.curationDepth = CurationDepth.IMEx;
            }
            else if (Annotation.MIMIX_CURATION.equalsIgnoreCase(added.getValue())){
                this.curationDepth = CurationDepth.MIMIx;
            }
            else if (Annotation.RAPID_CURATION.equalsIgnoreCase(added.getValue())){
                this.curationDepth = CurationDepth.rapid_curation;
            }
            else{
                this.curationDepth = CurationDepth.undefined;
            }
        }
    }

    protected void clearPropertiesLinkedToAnnotations() {
        Annotation authorList = AnnotationUtils.collectFirstAnnotationWithTopic(getDbAnnotations(), Annotation.AUTHOR_MI, Annotation.AUTHOR);
        this.persistentAnnotations.clear();
        if (authorList != null){
            this.persistentAnnotations.add(authorList);
        }

        Annotation publicationJournal = AnnotationUtils.collectFirstAnnotationWithTopic(getDbAnnotations(), Annotation.PUBLICATION_JOURNAL_MI, Annotation.PUBLICATION_JOURNAL);
        Annotation publicationYear = AnnotationUtils.collectFirstAnnotationWithTopic(getDbAnnotations(), Annotation.PUBLICATION_YEAR_MI, Annotation.PUBLICATION_YEAR);
        Annotation curationDepth = AnnotationUtils.collectFirstAnnotationWithTopic(getDbAnnotations(), Annotation.CURATION_DEPTH_MI, Annotation.CURATION_DEPTH);

        if (publicationJournal != null){
            getDbAnnotations().add(publicationJournal);
        }
        if (publicationYear != null){
            getDbAnnotations().add(publicationYear);
        }
        if (curationDepth != null){
            getDbAnnotations().add(curationDepth);
        }
    }

    private void initialiseReleasedDate() {
        for (LifeCycleEvent evt : getLifecycleEvents()){
            if (LifeCycleEventType.RELEASED.equals(evt.getEvent())){
                this.releasedDate = evt.getWhen();
            }
        }
    }

    private void setLifecycleEvents( List<LifeCycleEvent> lifecycleEvents ) {
        this.lifecycleEvents = lifecycleEvents;
    }

    protected void setExperiments(Collection<Experiment> experiments) {
        this.experiments = experiments;
    }

    protected void setDbAnnotations(Collection<Annotation> annotations) {
        if (annotations instanceof PersistentAnnotationList){
            this.persistentAnnotations = (PersistentAnnotationList)annotations;
            this.annotations = null;
            this.authors = null;
        }
        else{
            this.persistentAnnotations = new PersistentAnnotationList(annotations);
            this.annotations = null;
            this.authors = null;
        }
    }

    protected class PublicationIdentifierList extends AbstractListHavingProperties<Xref> {
        public PublicationIdentifierList(){
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
                throw new UnsupportedOperationException("Cannot remove the database accession of a Publication object from its list of identifiers.");
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

    protected class PublicationXrefList extends AbstractListHavingProperties<Xref> {
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

    protected class PublicationAnnotationList extends AbstractListHavingProperties<Annotation> {
        public PublicationAnnotationList(){
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
            clearPropertiesLinkedToAnnotations();
        }
    }

    protected class PersistentAnnotationList extends AbstractCollectionWrapper<Annotation> {
        public PersistentAnnotationList(Collection<Annotation> annots){
            super(annots);
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
            // nothing to do
        }

        @Override
        protected boolean needToPreProcessElementToRemove(Object o) {
            return false;
        }
    }
}
