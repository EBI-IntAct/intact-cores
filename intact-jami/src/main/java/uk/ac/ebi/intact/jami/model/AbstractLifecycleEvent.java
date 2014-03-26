package uk.ac.ebi.intact.jami.model;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.user.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Abstract class for publication lifecycle
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 */
@Entity
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS )
public abstract class AbstractLifecycleEvent extends AbstractIntactPrimaryObject implements LifeCycleEvent {

    private CvTerm event;

    private User who;

    private Date when;

    private String note;

    protected AbstractLifecycleEvent() {
    }

    public AbstractLifecycleEvent(CvTerm event, User who, Date when, String note) {
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
    public AbstractLifecycleEvent(CvTerm event, User who, String note) {
        this( event, who, new Date(), note );
    }

    @ManyToOne( targetEntity = IntactCvTerm.class, optional = false)
    @JoinColumn( name = "event_ac", referencedColumnName = "ac")
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
    @JoinColumn( name = "user_ac", referencedColumnName = "ac")
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
}
