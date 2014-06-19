package uk.ac.ebi.intact.jami.model.lifecycle;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.model.AbstractIntactPrimaryObject;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Abstract class for publication lifecycle
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 */
@Entity
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS )
public abstract class AbstractLifeCycleEvent extends AbstractIntactPrimaryObject implements LifeCycleEvent {

    private LifeCycleEventType event;

    private User who;

    private Date when;

    private String note;

    protected AbstractLifeCycleEvent() {
        this.event = LifeCycleEventType.CREATED;
    }

    public AbstractLifeCycleEvent(LifeCycleEventType event, User who, Date when, String note) {
        this.event = event != null ? event : LifeCycleEventType.CREATED;
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
    public AbstractLifeCycleEvent(LifeCycleEventType event, User who, String note) {
        this( event, who, new Date(), note );
    }

    @Transient
    /**
     * NOTE: in the future, should be persisted and cvEvent should be removed
     */
    public LifeCycleEventType getEvent() {
        return event;
    }

    public void setEvent( LifeCycleEventType event ) {
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

    @ManyToOne( targetEntity = IntactCvTerm.class, optional = false)
    @JoinColumn( name = "event_ac", referencedColumnName = "ac")
    @ForeignKey(name="FK_LIFECYCLE_EVENT_EVENT")
    @Index( name = "idx_event_event" )
    @Target(IntactCvTerm.class)
    @Deprecated
    @NotNull
    /**
     * NOTE: in the future, should be persisted and cvEvent should be removed
     * @deprecated use getEvent instead
     */
    public CvTerm getCvEvent() {
        return this.event.toCvTerm();
    }

    @Deprecated
    /**
     * @deprecated use setEvent instead
     */
    public void setCvEvent( CvTerm event ) {
        if (event.getShortName().equals(LifeCycleEventType.ACCEPTED.shortLabel())){
            this.event = LifeCycleEventType.ACCEPTED;
        }
        else if (event.getShortName().equals(LifeCycleEventType.ASSIGNED.shortLabel())){
            this.event = LifeCycleEventType.ASSIGNED;
        }
        else if (event.getShortName().equals(LifeCycleEventType.ASSIGNMENT_DECLINED.shortLabel())){
            this.event = LifeCycleEventType.ASSIGNMENT_DECLINED;
        }
        else if (event.getShortName().equals(LifeCycleEventType.CREATED.shortLabel())){
            this.event = LifeCycleEventType.CREATED;
        }
        else if (event.getShortName().equals(LifeCycleEventType.CURATION_STARTED.shortLabel())){
            this.event = LifeCycleEventType.CURATION_STARTED;
        }
        else if (event.getShortName().equals(LifeCycleEventType.DISCARDED.shortLabel())){
            this.event = LifeCycleEventType.DISCARDED;
        }
        else if (event.getShortName().equals(LifeCycleEventType.OWNER_CHANGED.shortLabel())){
            this.event = LifeCycleEventType.OWNER_CHANGED;
        }
        else if (event.getShortName().equals(LifeCycleEventType.PUT_ON_HOLD.shortLabel())){
            this.event = LifeCycleEventType.PUT_ON_HOLD;
        }
        else if (event.getShortName().equals(LifeCycleEventType.READY_FOR_CHECKING.shortLabel())){
            this.event = LifeCycleEventType.READY_FOR_CHECKING;
        }
        else if (event.getShortName().equals(LifeCycleEventType.READY_FOR_RELEASE.shortLabel())){
            this.event = LifeCycleEventType.READY_FOR_RELEASE;
        }
        else if (event.getShortName().equals(LifeCycleEventType.REJECTED.shortLabel())){
            this.event = LifeCycleEventType.REJECTED;
        }
        else if (event.getShortName().equals(LifeCycleEventType.RELEASED.shortLabel())){
            this.event = LifeCycleEventType.RELEASED;
        }
        else if (event.getShortName().equals(LifeCycleEventType.RESERVED.shortLabel())){
            this.event = LifeCycleEventType.RESERVED;
        }
        else if (event.getShortName().equals(LifeCycleEventType.REVIEWER_CHANGED.shortLabel())){
            this.event = LifeCycleEventType.REVIEWER_CHANGED;
        }
        else if (event.getShortName().equals(LifeCycleEventType.SANITY_CHECK_FAILED.shortLabel())){
            this.event = LifeCycleEventType.SANITY_CHECK_FAILED;
        }
        else if (event.getShortName().equals(LifeCycleEventType.SELF_ASSIGNED.shortLabel())){
            this.event = LifeCycleEventType.SELF_ASSIGNED;
        }
        else{
            this.event = LifeCycleEventType.LIFECYCLE_EVENT;
        }
        this.event.initCvTerm(event);
    }
}
