package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.model.impl.DefaultXref;
import psidev.psi.mi.jami.utils.CvTermUtils;
import psidev.psi.mi.jami.utils.XrefUtils;
import psidev.psi.mi.jami.utils.collection.AbstractListHavingProperties;
import uk.ac.ebi.intact.jami.model.AbstractIntactPrimaryObject;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * IntAct implementation of publication.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13/01/14</pre>
 */
@javax.persistence.Entity
@Table( name = "ia_publication" )
public class IntactPublication extends AbstractIntactPrimaryObject implements Publication{
    private String title;
    private String journal;
    private Date publicationDate;
    private List<String> authors;
    private Collection<Xref> identifiers;
    private Collection<Xref> xrefs;
    private Collection<Annotation> annotations;
    private Collection<Experiment> experiments;
    private CurationDepth curationDepth;
    private Date releasedDate;
    private Source source;

    private Xref pubmedId;
    private Xref doi;
    private Xref imexId;

    private String shortLabel;

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

    @PrePersist
    @PreUpdate
    public void prePersistAndUpdate() {
        // set shortlabel if not done yet
        if (this.shortLabel == null){
            if (this.pubmedId != null){
                this.shortLabel = this.pubmedId.getId().toLowerCase().trim();
            }
            else if (this.doi != null){
                this.shortLabel = this.doi.getId().toLowerCase().trim();
            }
            else if (this.identifiers != null && !this.identifiers.isEmpty()){
                this.shortLabel = this.identifiers.iterator().next().getId().toLowerCase().trim();
            }
        }
    }

    /**
     * Set the shortlabel
     * @param shortLabel
     * @deprecated the shortlabel is deprecated and getPubmedId/getDOI should be used instead
     */
    public void setShortLabel( String shortLabel ) {
        if (shortLabel == null){
            throw new IllegalArgumentException("The short name cannot be null");
        }
        this.shortLabel = shortLabel.trim().toLowerCase();
    }

    @Transient
    public String getPubmedId() {
        return this.pubmedId != null ? this.pubmedId.getId() : null;
    }

    public void setPubmedId(String pubmedId) {
        PublicationIdentifierList identifiers = (PublicationIdentifierList)getIdentifiers();

        // add new pubmed if not null
        if (pubmedId != null){
            CvTerm pubmedDatabase = CvTermUtils.createPubmedDatabase();
            CvTerm identityQualifier = CvTermUtils.createIdentityQualifier();
            // first remove old pubmed if not null
            if (this.pubmedId != null){
                identifiers.removeOnly(this.pubmedId);
            }
            this.pubmedId = new DefaultXref(pubmedDatabase, pubmedId, identityQualifier);
            identifiers.addOnly(this.pubmedId);
        }
        // remove all pubmed if the collection is not empty
        else if (!identifiers.isEmpty()) {
            XrefUtils.removeAllXrefsWithDatabase(identifiers, Xref.PUBMED_MI, Xref.PUBMED);
            this.pubmedId = null;
        }
    }

    @Transient
    public String getDoi() {
        return this.doi != null ? this.doi.getId() : null;
    }

    public void setDoi(String doi) {
        PublicationIdentifierList identifiers = (PublicationIdentifierList)getIdentifiers();
        // add new doi if not null
        if (doi != null){
            CvTerm doiDatabase = CvTermUtils.createDoiDatabase();
            CvTerm identityQualifier = CvTermUtils.createIdentityQualifier();
            // first remove old doi if not null
            if (this.doi != null){
                identifiers.removeOnly(this.doi);
            }
            this.doi = new DefaultXref(doiDatabase, doi, identityQualifier);
            identifiers.addOnly(this.doi);
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
            initialiseIdentifiers();
        }
        return this.identifiers;
    }

    @Transient
    public String getImexId() {
        return this.imexId != null ? this.imexId.getId() : null;
    }

    public void assignImexId(String identifier) {
        PublicationXrefList xrefs = (PublicationXrefList)getXrefs();
        // add new imex if not null
        if (identifier != null){
            CvTerm imexDatabase = CvTermUtils.createImexDatabase();
            CvTerm imexPrimaryQualifier = CvTermUtils.createImexPrimaryQualifier();
            // first remove old imex if not null
            if (this.imexId != null){
                xrefs.removeOnly(this.imexId);
            }
            this.imexId = new DefaultXref(imexDatabase, identifier, imexPrimaryQualifier);
            xrefs.addOnly(this.imexId);

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

    public List<String> getAuthors() {
        if (authors == null){
            initialiseAuthors();
        }
        return this.authors;
    }

    public Collection<Xref> getXrefs() {
        if (xrefs == null){
            initialiseXrefs();
        }
        return this.xrefs;
    }

    public Collection<Annotation> getAnnotations() {
        if (annotations == null){
            initialiseAnnotations();
        }
        return this.annotations;
    }

    public Collection<Experiment> getExperiments() {
        if (experiments == null){
            initialiseExperiments();
        }
        return this.experiments;
    }

    public CurationDepth getCurationDepth() {
        return this.curationDepth;
    }

    public void setCurationDepth(CurationDepth curationDepth) {

        if (imexId != null && curationDepth != null && !curationDepth.equals(CurationDepth.IMEx)){
            throw new IllegalArgumentException("The curationDepth " + curationDepth.toString() + " is not allowed because the publication has an IMEx id so it has IMEx curation depth.");
        }
        else if (imexId != null && curationDepth == null){
            throw new IllegalArgumentException("The curationDepth cannot be null/not specified because the publication has an IMEx id so it has IMEx curation depth.");
        }

        if (curationDepth == null) {
            this.curationDepth = CurationDepth.undefined;
        }
        else {
            this.curationDepth = curationDepth;
        }
    }

    public Date getReleasedDate() {
        return this.releasedDate;
    }

    public void setReleasedDate(Date released) {
        this.releasedDate = released;
    }

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

    protected void initialiseXrefs(){
        this.xrefs = new PublicationXrefList();
    }

    protected void initialiseAnnotations(){
        this.annotations = new ArrayList<Annotation>();
    }

    protected void initialiseExperiments(){
        this.experiments = new ArrayList<Experiment>();
    }

    protected void initialiseIdentifiers(){
        this.identifiers = new PublicationIdentifierList();
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

    @Column(name = "shortLabel", nullable = false, unique = true)
    @Size( min = 1, max = IntactUtils.MAX_SHORT_LABEL_LEN )
    @NotNull
    /**
     * @deprecated the publication shortLabel is deprecated. We should use getPubmedId or getDoi or getIdentifiers
     */
    private String getShortLabel() {
        return shortLabel;
    }

    private class PublicationIdentifierList extends AbstractListHavingProperties<Xref> {
        public PublicationIdentifierList(){
            super();
        }

        @Override
        protected void processAddedObjectEvent(Xref added) {

            processAddedIdentifierEvent(added);
        }

        @Override
        protected void processRemovedObjectEvent(Xref removed) {
            processRemovedIdentifierEvent(removed);
        }

        @Override
        protected void clearProperties() {
            clearPropertiesLinkedToIdentifiers();
        }
    }

    private class PublicationXrefList extends AbstractListHavingProperties<Xref> {
        public PublicationXrefList(){
            super();
        }

        @Override
        protected void processAddedObjectEvent(Xref added) {

            processAddedXrefEvent(added);
        }

        @Override
        protected void processRemovedObjectEvent(Xref removed) {
            processRemovedXrefEvent(removed);
        }

        @Override
        protected void clearProperties() {
            clearPropertiesLinkedToXrefs();
        }
    }
}
