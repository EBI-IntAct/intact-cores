package uk.ac.ebi.intact.model.user;

import org.hibernate.annotations.Index;
import uk.ac.ebi.intact.model.IntactObjectImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * A role that can be assigned to a user.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
@Entity
@Table( name = "ia_role" )
public class Role extends IntactObjectImpl {

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_REVIEWER = "REVIEWER";
    public static final String ROLE_CURATOR = "CURATOR";

    private String name;

    //////////////////
    // Constructors

    public Role() {
    }

    public Role( String name ) {
        if ( name == null || name.trim().length() == 0 ) {
            throw new IllegalArgumentException( "You must give a non empty/null role name" );
        }

        this.name = name.trim().toUpperCase();
    }

    ///////////////////////////
    // Getters and Setters

    @Column( unique = true, nullable = false )
    @Index( name = "idx_role_name" )
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    //////////////////////////
    // Object's override

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Role ) ) return false;

        Role role = ( Role ) o;

        if ( !name.equals( role.name ) ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append( "Role" );
        sb.append( "{name='" ).append( name ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }
}
