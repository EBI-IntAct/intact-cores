package uk.ac.ebi.intact.jami.model.user;

import org.hibernate.annotations.Index;
import uk.ac.ebi.intact.jami.model.AbstractIntactPrimaryObject;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A role that can be assigned to a user.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
@Entity
@Table( name="ia_role" )
public class Role extends AbstractIntactPrimaryObject {

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_REVIEWER = "REVIEWER";
    public static final String ROLE_CURATOR = "CURATOR";

    private String name;

    //////////////////
    // Constructors

    protected Role() {
    }

    public Role( String name ) {
        if ( name == null || name.trim().length() == 0 ) {
            throw new IllegalArgumentException( "You must give a non empty/null role name" );
        }

        this.name = name.trim().toUpperCase();
    }

    ///////////////////////////
    // Getters and Setters

    @Column( unique = true, nullable = false, length = IntactUtils.MAX_SHORT_LABEL_LEN)
    @Index( name = "idx_role_name" )
    @NotNull
    @Size(max = IntactUtils.MAX_SHORT_LABEL_LEN)
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        if ( name == null || name.trim().length() == 0 ) {
            throw new IllegalArgumentException( "You must give a non empty/null role name" );
        }
        this.name = name;
    }

    //////////////////////////
    // Object's override

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Role ) ) return false;

        Role role = ( Role ) o;

        return name.equals(role.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append( "Role" );
        sb.append( "{name='" ).append( name ).append('\'');
        sb.append( '}' );
        return sb.toString();
    }
}
