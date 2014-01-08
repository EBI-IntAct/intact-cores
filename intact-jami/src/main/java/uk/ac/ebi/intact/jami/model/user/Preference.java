package uk.ac.ebi.intact.jami.model.user;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import uk.ac.ebi.intact.jami.model.AbstractIntactPrimaryObject;

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
public class Preference extends AbstractIntactPrimaryObject {

    public static final String KEY_INSTITUTION_AC = "editor.institution.ac";
    public static final String KEY_INSTITUTION_NAME = "editor.institution.name";
    public static final String KEY_REVIEWER_AVAILABILITY = "reviewer.availability";
    public static final String KEY_MENTOR_REVIEWER = "reviewer.mentor";

    private String key;

    private String value;

    private User user;

    //////////////////
    // Constructors

    protected Preference() {
    }

    public Preference( User user, String key ) {
        if ( key == null || key.trim().length() == 0 ) {
            throw new IllegalArgumentException( "You must give a non empty/null key" );
        }

        this.key = key;
        this.user = user;
    }

    public Preference( User user, String key, String value ) {
        this(user, key);
        this.value = value;
    }

    ///////////////////////////
    // Getters and Setters

    @Index( name = "idx_preference_key")
    @Column(nullable = false)
    public String getKey() {
        return key;
    }

    public void setKey( String key ) {

        if ( key == null || key.trim().length() == 0 ) {
            throw new IllegalArgumentException( "You must give a non empty/null key" );
        }
        this.key = key;
    }

    @Lob
    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    @ManyToOne
    @JoinColumn( name = "user_ac" )
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
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Preference ) ) return false;

        Preference that = ( Preference ) o;

        return key.equals( that.key );
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(  key ).append( "=" );
        sb.append( value );
        return sb.toString();
    }
}
