package uk.ac.ebi.intact.model.user;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Type;
import uk.ac.ebi.intact.model.IntactObjectImpl;

import javax.persistence.*;

/**
 * A user preference.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
@Entity
@Table(
        name = "ia_preference",
        indexes = { @Index(name = "idx_preference_key", columnList = "key") })
public class Preference extends IntactObjectImpl {

    public static final String KEY_INSTITUTION_AC = "editor.institution.ac";
    public static final String KEY_INSTITUTION_NAME = "editor.institution.name";
    public static final String KEY_REVIEWER_AVAILABILITY = "reviewer.availability";
    public static final String KEY_MENTOR_REVIEWER = "reviewer.mentor";

    private String key;

    private String value;

    private User user;

    //////////////////
    // Constructors

    public Preference() {
    }

    public Preference( User user, String key ) {
        if ( StringUtils.isEmpty(key) ) {
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

    public String getKey() {
        return key;
    }

    public void setKey( String key ) {
        this.key = key;
    }

    @Type(type = "org.hibernate.type.TextType")
    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_ac", foreignKey = @ForeignKey(name="FK_PREF_USER"))
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

        if ( !key.equals( that.key ) ) return false;

        return true;
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
