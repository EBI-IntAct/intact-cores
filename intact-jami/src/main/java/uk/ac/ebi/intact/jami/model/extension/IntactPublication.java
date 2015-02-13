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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    private transient String title;
    private transient String journal;
    private transient Date publicationDate;
    private transient List<String> authors;
    private transient PublicationIdentifierList identifiers;
    private transient PublicationXrefList xrefs;
    private Collection<Experiment> experiments;
    private transient CurationDepth curationDepth;
    private transient Date releasedDate;
    private Source source;
    private transient PublicationAnnotationList annotations;

    private transient Xref pubmedId;
    private transient Xref doi;
    private transient Xref imexId;

    private transient PersistentXrefList persistentXrefs;
    private transient PersistentAnnotationList persistentAnnotations;

    private transient Xref acRef;

    private String shortLabel;
    private List<LifeCycleEvent> lifecycleEvents;
    private transient LifeCycleStatus status;
    private User currentOwner;
    private User currentReviewer;

    private transient CvTerm cvStatus;

    private transient Annotation toBeReviewed;
    private transient Annotation onHold;
    private transient Annotation accepted;
    private transient Annotation correctionComment;

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
            if (this.pubmedId != null && !pubmedId.equals(this.pubmedId)){
                if (this.pubmedId instanceof AbstractIntactXref){
                    ((AbstractIntactXref) this.pubmedId).setId(pubmedId);
                }
                else{
                    identifiers.remove(this.pubmedId);
                    this.pubmedId = new PublicationXref(pubmedDatabase, pubmedId, identityQualifier);
                    identifiers.add(this.pubmedId);
                }
            }
            else if (this.pubmedId == null){
                this.pubmedId = new PublicationXref(pubmedDatabase, pubmedId, identityQualifier);
                identifiers.add(this.pubmedId);
            }
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

            if (this.doi != null && !doi.equals(this.doi)){
                if (this.doi instanceof AbstractIntactXref){
                    ((AbstractIntactXref) this.doi).setId(doi);
                }
                else{
                    identifiers.remove(this.doi);
                    this.doi = new PublicationXref(doiDatabase, doi, identityQualifier);
                    identifiers.add(this.doi);
                }
            }
            else if (this.doi == null){
                this.doi = new PublicationXref(doiDatabase, doi, identityQualifier);
                identifiers.add(this.doi);
            }
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
            if (this.imexId != null && !identifier.equals(this.imexId)){
                if (this.imexId instanceof AbstractIntactXref){
                    ((AbstractIntactXref) this.imexId).setId(identifier);
                }
                else{
                    xrefs.remove(this.imexId);
                    this.imexId = new InteractionXref(imexDatabase, identifier, imexPrimaryQualifier);
                    xrefs.add(this.imexId);
                }
            }
            else if (this.imexId == null){
                this.imexId = new InteractionXref(imexDatabase, identifier, imexPrimaryQualifier);
                xrefs.add(this.imexId);
            }

            setCurationDepth(CurationDepth.IMEx);
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
    @JoinColumn(name="publication_ac", referencedColumnName="ac")
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
            initialiseStatus();
        }
        return status;
    }

    private void initialiseStatus() {
        if (this.cvStatus == null){
            this.status = LifeCycleStatus.NEW;
        }
        else{
            this.status = LifeCycleStatus.toLifeCycleStatus(this.cvStatus);
        }
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
        this.status = null;
    }

    @Override
    @Transient
    public String getOnHoldComment() {
        // initialise annotations if necessary
        getAnnotations();
        return onHold != null ? onHold.getValue() : null;
    }

    @Override
    @Transient
    public String getToBeReviewedComment() {
        // initialise annotations if necessary
        getAnnotations();
        return toBeReviewed != null ? toBeReviewed.getValue() : null;
    }

    @Override
    @Transient
    public String getAcceptedComment() {
        // initialise annotations if necessary
        getAnnotations();
        return accepted != null ? accepted.getValue() : null;
    }

    @Override
    public void onToBeReviewed(String message) {
        Collection<Annotation> complexAnnotationList = getAnnotations();

        if (toBeReviewed != null){
            this.toBeReviewed.setValue(message);
        }
        else  {
            CvTerm toBeReviewedTopic = IntactUtils.createMITopic(Releasable.TO_BE_REVIEWED, null);
            this.toBeReviewed = new InteractorAnnotation(toBeReviewedTopic, message);
            complexAnnotationList.add(this.toBeReviewed);
        }
    }

    @Override
    public void onAccepted(String message) {
        Collection<Annotation> complexAnnotationList = getAnnotations();

        if (accepted != null){
            this.accepted.setValue(message);
        }
        else  {
            CvTerm acceptedTopic = IntactUtils.createMITopic(Releasable.ACCEPTED, null);
            this.accepted = new InteractorAnnotation(acceptedTopic, message);
            complexAnnotationList.add(this.accepted);
        }
    }

    @Override
    @Transient
    public boolean isAccepted() {
        // initialise annotations if necessary
        getAnnotations();
        return accepted != null;
    }

    @Override
    public void removeAccepted() {
        AnnotationUtils.removeAllAnnotationsWithTopic(getAnnotations(), null, Releasable.ACCEPTED);
    }

    @Override
    @Transient
    public boolean isToBeReviewed() {
        // initialise annotations if necessary
        getAnnotations();
        return toBeReviewed != null;
    }

    @Override
    public void removeToBeReviewed() {
        AnnotationUtils.removeAllAnnotationsWithTopic(getAnnotations(), null, Releasable.TO_BE_REVIEWED);
    }

    @Override
    public void onHold(String message) {
        Collection<Annotation> complexAnnotationList = getAnnotations();

        if (onHold != null){
            this.onHold.setValue(message);
        }
        else  {
            CvTerm onHoldTopic = IntactUtils.createMITopic(Releasable.ON_HOLD, null);
            this.onHold = new InteractorAnnotation(onHoldTopic, message);
            complexAnnotationList.add(this.onHold);
        }
    }

    @Override
    public void removeCorrectionComment() {
        AnnotationUtils.removeAllAnnotationsWithTopic(getAnnotations(), null, Releasable.CORRECTION_COMMENT);
    }

    @Override
    @Transient
    public String getCorrectionComment() {
        return this.correctionComment != null ? this.correctionComment.getValue() : null;
    }

    @Override
    public void onCorrectionComment(String message) {
        Collection<Annotation> complexAnnotationList = getAnnotations();

        if (correctionComment != null){
            this.correctionComment.setValue(message);
        }
        else  {
            CvTerm onHoldTopic = IntactUtils.createMITopic(Releasable.CORRECTION_COMMENT, null);
            this.correctionComment = new InteractorAnnotation(onHoldTopic, message);
            complexAnnotationList.add(this.correctionComment);
        }
    }

    @Override
    @Transient
    public boolean hasCorrectionComment() {
        return this.correctionComment != null;
    }

    @Override
    @Transient
    public boolean isOnHold() {
        // initialise annotations if necessary
        getAnnotations();
        return onHold != null;
    }

    @Override
    public void removeOnHold() {
        AnnotationUtils.removeAllAnnotationsWithTopic(getAnnotations(), null, Releasable.ON_HOLD);
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
                if (XrefUtils.isXrefAnIdentifier(ref) || XrefUtils.doesXrefHaveQualifier(ref, Xref.PRIMARY_MI, Xref.PRIMARY)
                        || XrefUtils.doesXrefHaveQualifier(ref, null, "intact-secondary")){
                    this.identifiers.addOnly(ref);
                    processAddedIdentifierEvent(ref);
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
            Collection<Xref> existingImex = XrefUtils.collectAllXrefsHavingDatabaseAndQualifier(getXrefs(), Xref.IMEX_MI, Xref.IMEX, Xref.IMEX_PRIMARY_MI, Xref.IMEX_PRIMARY);
            if (!existingImex.isEmpty()){
                imexId = existingImex.iterator().next();
            }
        }
    }

    protected void clearPropertiesLinkedToXrefs() {
        imexId = null;
    }

    protected void initialiseAuthors(){
        // initialise annotations first
        initialiseAnnotations();
    }

    protected void setDbXrefs(Collection<Xref> persistentXrefs){
        if (persistentXrefs instanceof PersistentXrefList){
            this.persistentXrefs = (PersistentXrefList)persistentXrefs;
            resetXrefs();
        }
        else{
            this.persistentXrefs = new PersistentXrefList(persistentXrefs);
            resetXrefs();
        }
    }

    protected void resetXrefs(){
        this.identifiers = null;
        this.xrefs = null;
        this.pubmedId = null;
        this.doi = null;
        this.imexId = null;
    }

    protected void initialiseAnnotations() {
        this.annotations = new PublicationAnnotationList();
        this.authors = new ArrayList<String>();
        this.curationDepth = CurationDepth.undefined;
        this.journal = null;
        this.publicationDate = null;

        // initialise persistent annot and content
        if (this.persistentAnnotations != null){
            for (Annotation annot : this.persistentAnnotations){
                if (!processAddedDbAnnotationEvent(annot)){
                    this.annotations.addOnly(annot);
                }
            }
        }
        else{
            this.persistentAnnotations = new PersistentAnnotationList(null);
        }
    }

    protected boolean processAddedDbAnnotationEvent(Annotation added) {
        if (AnnotationUtils.doesAnnotationHaveTopic(added, Annotation.AUTHOR_MI, Annotation.AUTHOR) && added.getValue() != null){
            if (added.getValue().contains(", ")){
                getAuthors().addAll(Arrays.asList(added.getValue().split(", ")));
            }
            else{
                getAuthors().add(added.getValue());
            }
            return true;
        }
        // journal
        else if (AnnotationUtils.doesAnnotationHaveTopic(added, Annotation.PUBLICATION_JOURNAL_MI, Annotation.PUBLICATION_JOURNAL) && added.getValue() != null){
            this.journal = added.getValue();
            return true;
        }
        // publication year
        else if (AnnotationUtils.doesAnnotationHaveTopic(added, Annotation.PUBLICATION_YEAR_MI, Annotation.PUBLICATION_YEAR) && added.getValue() != null){
            try {
                this.publicationDate = IntactUtils.YEAR_FORMAT.parse(added.getValue());
                return true;
            } catch (ParseException e) {
                e.printStackTrace();
                this.publicationDate = null;
                return false;
            }
        }
        // curation depth
        else if (AnnotationUtils.doesAnnotationHaveTopic(added, Annotation.CURATION_DEPTH_MI, Annotation.CURATION_DEPTH) && added.getValue() != null){
            if (Annotation.IMEX_CURATION.equalsIgnoreCase(added.getValue())){
                this.curationDepth = CurationDepth.IMEx;
                return true;
            }
            else if (Annotation.MIMIX_CURATION.equalsIgnoreCase(added.getValue())){
                this.curationDepth = CurationDepth.MIMIx;
                return true;
            }
            else if (Annotation.RAPID_CURATION.equalsIgnoreCase(added.getValue())){
                this.curationDepth = CurationDepth.rapid_curation;
                return true;
            }
            else{
                this.curationDepth = CurationDepth.undefined;
                return true;
            }
        }
        else{
            return false;
        }
    }

    protected void processAddedAnnotation(Annotation added) {
        if (toBeReviewed == null &&
                AnnotationUtils.doesAnnotationHaveTopic(added, null, Releasable.TO_BE_REVIEWED)){
            toBeReviewed = added;
        }
        else if (accepted == null &&
                AnnotationUtils.doesAnnotationHaveTopic(added, null, Releasable.ACCEPTED)){
            accepted = added;
        }
        else if (onHold == null &&
                AnnotationUtils.doesAnnotationHaveTopic(added, null, Releasable.ON_HOLD)){
            onHold = added;
        }
        else if (correctionComment == null &&
                AnnotationUtils.doesAnnotationHaveTopic(added, null, Releasable.CORRECTION_COMMENT)){
            correctionComment = added;
        }
    }

    protected void processRemovedAnnotation(Annotation removed) {
        if (toBeReviewed != null && toBeReviewed.equals(removed)){
            toBeReviewed = AnnotationUtils.collectFirstAnnotationWithTopic(getAnnotations(),
                    null, Releasable.TO_BE_REVIEWED);
        }
        if (accepted != null && accepted.equals(removed)){
            accepted = AnnotationUtils.collectFirstAnnotationWithTopic(getAnnotations(),
                    null, Releasable.ACCEPTED);
        }
        if (onHold != null && onHold.equals(removed)){
            onHold = AnnotationUtils.collectFirstAnnotationWithTopic(getAnnotations(),
                    null, Releasable.ON_HOLD);
        }
        if (correctionComment != null && correctionComment.equals(removed)){
            correctionComment = AnnotationUtils.collectFirstAnnotationWithTopic(getAnnotations(),
                    null, Releasable.CORRECTION_COMMENT);
        }
    }

    protected void clearPropertiesLinkedToAnnotations() {
        Annotation authorList = AnnotationUtils.collectFirstAnnotationWithTopic(getDbAnnotations(), Annotation.AUTHOR_MI, Annotation.AUTHOR);
        Annotation publicationJournal = AnnotationUtils.collectFirstAnnotationWithTopic(getDbAnnotations(), Annotation.PUBLICATION_JOURNAL_MI, Annotation.PUBLICATION_JOURNAL);
        Annotation publicationYear = AnnotationUtils.collectFirstAnnotationWithTopic(getDbAnnotations(), Annotation.PUBLICATION_YEAR_MI, Annotation.PUBLICATION_YEAR);
        Annotation curationDepth = AnnotationUtils.collectFirstAnnotationWithTopic(getDbAnnotations(), Annotation.CURATION_DEPTH_MI, Annotation.CURATION_DEPTH);
        this.persistentAnnotations.clear();

        if (authorList != null){
            this.persistentAnnotations.add(authorList);
        }
        if (publicationJournal != null){
            this.persistentAnnotations.add(publicationJournal);
        }
        if (publicationYear != null){
            this.persistentAnnotations.add(publicationYear);
        }
        if (curationDepth != null){
            this.persistentAnnotations.add(curationDepth);
        }

        this.onHold = null;
        this.toBeReviewed = null;
        this.accepted = null;
        this.correctionComment = null;
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
        // write the annotations
        oos.writeObject(getDbAnnotations());
        // write the status
        oos.writeObject(getCvStatus());
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
        // read default annotations
        setDbAnnotations((Collection<Annotation>)ois.readObject());
        // read default status
        setCvStatus((CvTerm)ois.readObject());
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
            processAddedAnnotation(added);
            persistentAnnotations.add(added);
        }

        @Override
        protected void processRemovedObjectEvent(Annotation removed) {
            processRemovedAnnotation(removed);
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
