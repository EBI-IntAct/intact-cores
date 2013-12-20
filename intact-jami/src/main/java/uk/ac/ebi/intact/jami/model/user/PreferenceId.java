package uk.ac.ebi.intact.jami.model.user;

import org.hibernate.annotations.Index;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * A preference id is an primary id for a preference
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>20/12/13</pre>
 */
@Embeddable
public class PreferenceId implements Serializable{
    private String key;
    private String value;
    private String userAc;

    public PreferenceId( String userAc, String key ) {
        if ( key == null || key.trim().length() == 0) {
            throw new IllegalArgumentException( "You must give a non empty/null key" );
        }

        this.key = key;
        this.userAc = userAc;
    }

    public PreferenceId( String userAc, String key, String value ) {
        this(userAc, key);
        this.value = value;
    }

    @Index( name="idx_preference_key" )
    public String getKey() {
        return key;
    }

    public void setKey( String key ) {
        if ( key == null || key.trim().length() == 0) {
            throw new IllegalArgumentException( "You must give a non empty/null key" );
        }
        this.key = key;
    }

    @Column( name="user_ac" )
    public String getUserAc() {
        return userAc;
    }

    public void setUserAc( String userAc ) {
        this.userAc = userAc;
    }

    //////////////////////////
    // Object's override

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof PreferenceId ) ) return false;

        PreferenceId preferredId = ( PreferenceId) o;

        if ( !this.key.equals(preferredId.getKey()) ) return false;
        if (this.userAc != null && !this.userAc.equals(preferredId.getUserAc())){
            return false;
        }
        else if (userAc == null && preferredId != null){
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = key.hashCode();
        hash+= 29*hash + userAc != null ? userAc.hashCode() : 0;
        return hash;
    }
}
