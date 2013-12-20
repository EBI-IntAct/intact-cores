package uk.ac.ebi.intact.jami.model.user;

import org.hibernate.annotations.ForeignKey;
import uk.ac.ebi.intact.jami.model.BackwardCompatibleObjectWithAc;
import uk.ac.ebi.intact.jami.model.audit.AbstractAuditable;
import uk.ac.ebi.intact.jami.model.listener.BackwardCompatibleAcEventListener;

import javax.persistence.*;

/**
 * A user preference.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
@Entity
@Table( name="ia_preference" )
@EntityListeners(value = {BackwardCompatibleAcEventListener.class})
public class Preference extends AbstractAuditable implements BackwardCompatibleObjectWithAc {

    public static final String KEY_INSTITUTION_AC = "editor.institution.ac";
    public static final String KEY_INSTITUTION_NAME = "editor.institution.name";
    public static final String KEY_REVIEWER_AVAILABILITY = "reviewer.availability";
    public static final String KEY_MENTOR_REVIEWER = "reviewer.mentor";

    private PreferenceId preferenceId;
    private String value;
    private User user;
    private String ac;

    //////////////////
    // Constructors

    public Preference() {
    }

    public Preference( User user, String key ) {

        this.preferenceId = new PreferenceId(user.getAc(), key);
        this.user = user;
    }

    public Preference( User user, String key, String value ) {
        this(user, key);
        this.value = value;
    }

    ///////////////////////////
    // Getters and Setters

    @Column(name = "ac", unique = true, updatable = false)
    public String getAc(){
        return this.ac;
    }

    /**
     * This method should not be used by applications, as the AC is a primary key which is auto-generated. If we move to
     * an application server it may then be needed.
     *
     * @param ac
     */
    public void setAc( String ac ) {
        this.ac = ac;
    }

    @EmbeddedId
    public PreferenceId getPreferenceId() {
        return this.preferenceId;
    }

    public void setPreferenceId( PreferenceId key ) {
        this.preferenceId = key;
    }

    @Lob
    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    @ManyToOne
    @JoinColumn( name="user_ac", referencedColumnName = "ac")
    @ForeignKey(name="FK_PREF_USER")
    public User getUser() {
        return user;
    }

    public void setUser( User user ) {
        this.user = user;
    }

    //////////////////////////
    // Object's override

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(  this.preferenceId != null ? this.preferenceId.getKey() : "" ).append( "=" );
        sb.append( value );
        return sb.toString();
    }
}
