package uk.ac.ebi.intact.jami.model;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Publication;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;
import uk.ac.ebi.intact.jami.model.user.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Publication lifecycle event
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 */
@Entity
@Table( name = "ia_lifecycle_event" )
public class LifecycleEvent extends AbstractIntactPrimaryObject {

    private CvTerm event;

    private User who;

    private Date when;

    private String note;

    private Publication publication;

    public LifecycleEvent() {
    }

    public LifecycleEvent(CvTerm event, User who, Date when, String note) {
        this.event = event;
        this.who = who;
        this.when = when;
        this.note = note;
    }

    /**
     * Date is set to current time.
     * @param event
     * @param who
     * @param note
     */
    public LifecycleEvent(CvTerm event, User who, String note) {
        this( event, who, new Date(), note );
    }

    @ManyToOne( targetEntity = IntactCvTerm.class, optional = false)
    @JoinColumn( name = "event_ac" )
    @ForeignKey(name="FK_LIFECYCLE_EVENT_EVENT")
    @Index( name = "idx_event_event" )
    @Target(IntactCvTerm.class)
    public CvTerm getEvent() {
        return event;
    }

    public void setEvent( CvTerm event ) {
        this.event = event;
    }

    @ManyToOne( optional = false, targetEntity = User.class)
    @JoinColumn( name = "user_ac" )
    @ForeignKey(name="FK_LIFECYCLE_EVENT_USER")
    @Index( name = "idx_event_who" )
    @Target(User.class)
    public User getWho() {
        return who;
    }

    public void setWho( User who ) {
        this.who = who;
    }

    @Column(name = "when_date")
    @Temporal( TemporalType.TIMESTAMP )
    public Date getWhen() {
        return when;
    }

    public void setWhen( Date when ) {
        this.when = when;
    }

    @Lob
    public String getNote() {
        return note;
    }

    public void setNote( String note ) {
        this.note = note;
    }

    @ManyToOne( targetEntity = IntactPublication.class, fetch = FetchType.LAZY, optional = false )
    @JoinColumn( name = "publication_ac" )
    @ForeignKey(name="FK_LIFECYCLE_EVENT_PUBLICATION")
    @Target(IntactPublication.class)
    public Publication getPublication() {
        return publication;
    }

    public void setPublication( Publication publication ) {
        this.publication = publication;
    }
}
