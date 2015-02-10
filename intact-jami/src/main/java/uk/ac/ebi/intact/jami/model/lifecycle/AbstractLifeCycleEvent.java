package uk.ac.ebi.intact.jami.model.lifecycle;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Target;
import org.hibernate.annotations.Type;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.model.AbstractIntactPrimaryObject;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    private transient LifeCycleEventType event;

    private User who;

    private Date when;

    private String note;

    private transient CvTerm cvEvent;

    protected AbstractLifeCycleEvent() {
    }

    public AbstractLifeCycleEvent(LifeCycleEventType event, User who, Date when, String note) {
        setEvent(event);
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
        if (this.event == null){
            initialiseEvent();
        }
        return event;
    }

    private void initialiseEvent() {
        if (this.cvEvent == null){
            this.event = LifeCycleEventType.CREATED;
        }
        else{
            this.event = LifeCycleEventType.toLifeCycleEventType(this.cvEvent);
        }
    }

    public void setEvent( LifeCycleEventType event ) {
        this.event = event != null ? event : LifeCycleEventType.CREATED;
        this.cvEvent = this.event.toCvTerm();
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
    @Type(type = "org.hibernate.type.StringClobType")
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
        if (this.cvEvent == null){
            this.cvEvent = getEvent().toCvTerm();
        }
        return this.cvEvent;
    }

    @Deprecated
    /**
     * @deprecated use setEvent instead
     */
    public void setCvEvent( CvTerm event ) {
        this.cvEvent = event;
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
        // write the event
        oos.writeObject(getCvEvent());
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
        setCvEvent((CvTerm)ois.readObject());
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof LifeCycleEvent ) ) return false;

        LifeCycleEvent evt = ( LifeCycleEvent ) o;

        if (evt.getEvent() != this.event){
            return false;
        }
        else if ((evt.getNote() == null && this.note != null) || (evt.getNote() == null && this.note != null)){
            return false;
        }
        else if (!evt.getNote().equalsIgnoreCase(this.note)){
             return false;
        }
        else if (!evt.getWho().equals(this.who)){
            return false;
        }
        else if (!evt.getWhen().equals(this.when)){
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hashcode = 31;
        hashcode = 31*hashcode + getEvent().hashCode();
        if (this.note != null){
            hashcode = 31*hashcode + this.note.hashCode();
        }
        hashcode = 31*hashcode + this.who.hashCode();
        hashcode = 31*hashcode + this.when.hashCode();

        return hashcode;
    }
}
