package uk.ac.ebi.intact.model;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import uk.ac.ebi.intact.model.user.User;

import javax.persistence.*;
import java.util.Date;

/**
 * TODO document this !
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since TODO add POM version
 */
@Entity
@Table( name = "ia_lifecycle_event" )
@javax.persistence.SequenceGenerator( name="SEQ_MISC", sequenceName="misc_seq", initialValue = 1 )
public class LifecycleEvent extends IntactObjectImpl {

    private Long pk;

    private CvLifecycleEvent event;

    private User who;

    private Date when;

    private String note;

    private Publication publication;

    public LifecycleEvent() {
    }

    public LifecycleEvent( CvLifecycleEvent event, User who, Date when, String note ) {
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
    public LifecycleEvent( CvLifecycleEvent event, User who, String note ) {
        this( event, who, new Date(), note );
    }

    @ManyToOne( optional = false )
    @JoinColumn( name = "event_ac" )
    @ForeignKey(name="FK_LIFECYCLE_EVENT_EVENT")
    @Index( name = "idx_event_event" )
    public CvLifecycleEvent getEvent() {
        return event;
    }

    public void setEvent( CvLifecycleEvent event ) {
        this.event = event;
    }

    @ManyToOne( optional = false )
    @JoinColumn( name = "user_ac" )
    @ForeignKey(name="FK_LIFECYCLE_EVENT_USER")
    @Index( name = "idx_event_who" )
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
    @Type(type = "org.hibernate.type.MaterializedClobType")
    public String getNote() {
        return note;
    }

    public void setNote( String note ) {
        this.note = note;
    }

    @ManyToOne( targetEntity = Publication.class, fetch = FetchType.LAZY, optional = false )
    @JoinColumn( name = "publication_ac" )
    @ForeignKey(name="FK_LIFECYCLE_EVENT_PUBLICATION")
    public Publication getPublication() {
        return publication;
    }

    public void setPublication( Publication publication ) {
        this.publication = publication;
    }
}
