package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import uk.ac.ebi.intact.jami.model.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.PublicationLifecycleEvent;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.text.ParseException;
import java.util.*;

/**
 * IntAct implementation of publication with experimental evidences.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13/01/14</pre>
 */
@Entity
@Table(name = "ia_publication")
@Cacheable
public class IntactCuratedPublication extends IntactPublication{

    private String shortLabel;
    private List<LifeCycleEvent> lifecycleEvents;
    private CvTerm status;
    private User currentOwner;
    private User currentReviewer;

    public IntactCuratedPublication(){
        super();
    }

    public IntactCuratedPublication(Xref identifier){
        super(identifier);
    }

    public IntactCuratedPublication(Xref identifier, CurationDepth curationDepth, Source source){
        super(identifier, curationDepth, source);
    }

    public IntactCuratedPublication(Xref identifier, String imexId, Source source){
        super(identifier, imexId, source);
    }

    public IntactCuratedPublication(String pubmed){
        super(pubmed);
    }

    public IntactCuratedPublication(String pubmed, CurationDepth curationDepth, Source source){
        super(pubmed, curationDepth, source);
    }

    public IntactCuratedPublication(String pubmed, String imexId, Source source){
        super(pubmed, imexId, source);
    }

    public IntactCuratedPublication(String title, String journal, Date publicationDate){
        super(title, journal, publicationDate);
    }

    public IntactCuratedPublication(String title, String journal, Date publicationDate, CurationDepth curationDepth, Source source){
        super(title, journal, publicationDate, curationDepth, source);
    }

    public IntactCuratedPublication(String title, String journal, Date publicationDate, String imexId, Source source){
        super(title, journal, publicationDate, imexId, source);
    }

    @Column( name = "fullname", length = IntactUtils.MAX_FULL_NAME_LEN )
    @Size( max = IntactUtils.MAX_FULL_NAME_LEN )
    @Override
    public String getTitle() {
        return super.getTitle();
    }


    /**
     * For backward compatibility, it is not persistent and kept as annotation.
     * In the future, should become
     * @Column( name = "journal", length = IntactUtils.MAX_FULL_NAME_LEN )
     * @Size( max = IntactUtils.MAX_FULL_NAME_LEN )
     */
    @Transient
    @Override
    public String getJournal() {
        // initialise annotations first
        getAnnotations();
        return super.getJournal();
    }

    public void setJournal(String journal) {
        Collection<Annotation> dbAnnots = getDbAnnotations();

        // add new journal if not null
        if (journal != null){
            CvTerm journalTopic = IntactUtils.createMITopic(Annotation.PUBLICATION_JOURNAL, Annotation.PUBLICATION_JOURNAL_MI);
            // first remove old journal if not null
            if (super.getJournal() != null){
                Annotation oldJournal = AnnotationUtils.collectFirstAnnotationWithTopicAndValue(getDbAnnotations(), Annotation.PUBLICATION_JOURNAL_MI, Annotation.PUBLICATION_JOURNAL, super.getJournal());
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
            super.setJournal(journal);
        }
        // remove all journal if the collection is not empty
        else if (!dbAnnots.isEmpty()) {
            AnnotationUtils.removeAllAnnotationsWithTopic(getDbAnnotations(), Annotation.PUBLICATION_JOURNAL_MI, Annotation.PUBLICATION_JOURNAL);
            super.setJournal(null);
        }
    }

    /**
     * For backward compatibility, it is not persistent and kept as annotation.
     * In the future, should become
     * @Temporal(TemporalType.TIMESTAMP)
     * @Column(name = "publication_date")
     */
    @Transient
    @Override
    public Date getPublicationDate() {
        // initialise annotations first
        getAnnotations();
        return super.getPublicationDate();
    }

    public void setPublicationDate(Date date) {
        Collection<Annotation> dbAnnots = getDbAnnotations();

        // add new journal if not null
        if (date != null){
            CvTerm yearTopic = IntactUtils.createMITopic(Annotation.PUBLICATION_YEAR, Annotation.PUBLICATION_YEAR_MI);
            // first remove old journal if not null
            if (super.getPublicationDate() != null){
                Annotation oldDate = AnnotationUtils.collectFirstAnnotationWithTopicAndValue(getDbAnnotations(), Annotation.PUBLICATION_YEAR_MI, Annotation.PUBLICATION_YEAR, IntactUtils.YEAR_FORMAT.format(super.getPublicationDate()));
                if (oldDate != null){
                    oldDate.setValue(IntactUtils.YEAR_FORMAT.format(super.getPublicationDate()));
                }
                else{
                    getDbAnnotations().add(new PublicationAnnotation(yearTopic, IntactUtils.YEAR_FORMAT.format(date)));
                }
            }
            else{
                getDbAnnotations().add(new PublicationAnnotation(yearTopic, IntactUtils.YEAR_FORMAT.format(date)));
            }
            super.setPublicationDate(date);
        }
        // remove all pub dates if the collection is not empty
        else if (!dbAnnots.isEmpty()) {
            AnnotationUtils.removeAllAnnotationsWithTopic(getDbAnnotations(), Annotation.PUBLICATION_YEAR_MI, Annotation.PUBLICATION_YEAR);
            super.setPublicationDate(null);
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

    @OneToMany( mappedBy = "publication", cascade = { CascadeType.ALL }, targetEntity = IntactExperiment.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(IntactExperiment.class)
    @Override
    public Collection<Experiment> getExperiments() {
        return super.getExperiments();
    }


    @Override
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
        return super.getCurationDepth();
    }

    @Override
    public void setCurationDepth(CurationDepth curationDepth) {
        Collection<Annotation> dbAnnots = getDbAnnotations();

        // add new curation depth if not null
        if (curationDepth != null && !curationDepth.equals(CurationDepth.undefined)){
            CvTerm depthTopic = IntactUtils.createMITopic(Annotation.CURATION_DEPTH, Annotation.CURATION_DEPTH_MI);
            // first remove old curation depth if not null
            if (super.getCurationDepth() != null && !super.getCurationDepth().equals(CurationDepth.undefined)){
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
            super.setCurationDepth(curationDepth);
        }
        // remove all curation depth if the collection is not empty
        else if (!dbAnnots.isEmpty()) {
            AnnotationUtils.removeAllAnnotationsWithTopic(getDbAnnotations(), Annotation.CURATION_DEPTH_MI, Annotation.CURATION_DEPTH);
            super.setCurationDepth(CurationDepth.undefined);
        }
    }

    @Override
    /**
     * For backward compatibility, it is not persistent and kept as annotation.
     * In the future, should become
     * @Temporal(TemporalType.TIMESTAMP)
     * @Column(name = "released_date")
     */
    public Date getReleasedDate() {
        // initialise lifecycle events first
        if (super.getReleasedDate() == null && !getLifecycleEvents().isEmpty()){
            initialiseReleasedDate();
        }
        return super.getReleasedDate();
    }

    @Override
    public void setReleasedDate(Date released) {
        super.setReleasedDate(released);
        for (LifeCycleEvent evt : getLifecycleEvents()){
            if (LifeCycleEvent.RELEASED.equalsIgnoreCase(evt.getEvent().getShortName())){
                evt.setWhen(released);
            }
        }
    }

    @ManyToOne(targetEntity = IntactSource.class)
    @JoinColumn( name = "owner_ac", nullable = false, referencedColumnName = "ac" )
    @Target(IntactSource.class)
    @Override
    public Source getSource() {
        return super.getSource();
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

    @OneToMany( orphanRemoval = true, cascade = CascadeType.ALL, targetEntity = PublicationLifecycleEvent.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @OrderBy("when, created")
    @Target(PublicationLifecycleEvent.class)
    public List<LifeCycleEvent> getLifecycleEvents() {
        if (this.lifecycleEvents == null){
            this.lifecycleEvents = new ArrayList<LifeCycleEvent>();
        }
        return lifecycleEvents;
    }

    @ManyToOne(targetEntity = IntactCvTerm.class)
    @JoinColumn( name = "status_ac", referencedColumnName = "ac" )
    @ForeignKey(name="FK_PUBLICATION_STATUS")
    @Target(IntactCvTerm.class)
    public CvTerm getStatus() {
        return status;
    }

    public void setStatus( CvTerm status ) {
        this.status = status;
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
    public boolean areLifecycleEventsInitialized(){
        return Hibernate.isInitialized(getLifecycleEvents());
    }

    @Override
    protected void processAddedAnnotationEvent(Annotation added) {
        super.processAddedAnnotationEvent(added);
        // journal
        if (AnnotationUtils.doesAnnotationHaveTopic(added, Annotation.PUBLICATION_JOURNAL_MI, Annotation.PUBLICATION_JOURNAL) && added.getValue() != null){
            super.setJournal(added.getValue());
        }
        // publication year
        else if (AnnotationUtils.doesAnnotationHaveTopic(added, Annotation.PUBLICATION_YEAR_MI, Annotation.PUBLICATION_YEAR) && added.getValue() != null){
            try {
                super.setPublicationDate(IntactUtils.YEAR_FORMAT.parse(added.getValue()));
            } catch (ParseException e) {
                e.printStackTrace();
                super.setPublicationDate(null);
            }
        }
        // curation depth
        else if (AnnotationUtils.doesAnnotationHaveTopic(added, Annotation.CURATION_DEPTH_MI, Annotation.CURATION_DEPTH) && added.getValue() != null){
            if (Annotation.IMEX_CURATION.equalsIgnoreCase(added.getValue())){
                super.setCurationDepth(CurationDepth.IMEx);
            }
            else if (Annotation.MIMIX_CURATION.equalsIgnoreCase(added.getValue())){
                super.setCurationDepth(CurationDepth.MIMIx);
            }
            else if (Annotation.RAPID_CURATION.equalsIgnoreCase(added.getValue())){
                super.setCurationDepth(CurationDepth.rapid_curation);
            }
            else{
                super.setCurationDepth(CurationDepth.undefined);
            }
        }
    }

    @Override
    protected void clearPropertiesLinkedToAnnotations() {
        Annotation publicationJournal = AnnotationUtils.collectFirstAnnotationWithTopic(getDbAnnotations(), Annotation.PUBLICATION_JOURNAL_MI, Annotation.PUBLICATION_JOURNAL);
        Annotation publicationYear = AnnotationUtils.collectFirstAnnotationWithTopic(getDbAnnotations(), Annotation.PUBLICATION_YEAR_MI, Annotation.PUBLICATION_YEAR);
        Annotation curationDepth = AnnotationUtils.collectFirstAnnotationWithTopic(getDbAnnotations(), Annotation.CURATION_DEPTH_MI, Annotation.CURATION_DEPTH);

        super.clearPropertiesLinkedToAnnotations();

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
            if (LifeCycleEvent.RELEASED.equalsIgnoreCase(evt.getEvent().getShortName())){
                super.setReleasedDate(evt.getWhen());
            }
        }
    }

    private void setLifecycleEvents( List<LifeCycleEvent> lifecycleEvents ) {
        this.lifecycleEvents = lifecycleEvents;
    }
}
