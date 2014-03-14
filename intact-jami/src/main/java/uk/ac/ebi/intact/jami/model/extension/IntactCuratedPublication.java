package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.model.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.PublicationLifecycleEvent;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * IntAct implementation of publication with experimental evidences.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13/01/14</pre>
 */
@Entity
@DiscriminatorValue("curated_publication")
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

    @Enumerated(EnumType.STRING)
    @Column(name = "curation_depth", length = IntactUtils.MAX_SHORT_LABEL_LEN)
    @Override
    public CurationDepth getCurationDepth() {
        return super.getCurationDepth();
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "released_date")
    @Override
    public Date getReleasedDate() {
        return super.getReleasedDate();
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

    private void setLifecycleEvents( List<LifeCycleEvent> lifecycleEvents ) {
        this.lifecycleEvents = lifecycleEvents;
    }
}
