package uk.ac.ebi.intact.core.users.model;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.util.*;

/**
 * An intact user.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
@Entity
@Table( name = "ia_user" )
@javax.persistence.SequenceGenerator( name = "SEQ_USER", sequenceName = "users_seq", initialValue = 1 )
public class User implements HasIdentity {

    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "SEQ_USER" )
    private Long pk;

    @Column( nullable = false, unique = true )
    @Index( name = "idx_user_login" )
    private String login;

    private String password;

    @Column( nullable = false )
    private String firstName;

    @Column( nullable = false )
    private String lastName;

    @Column( nullable = false, unique = true )
    @Index( name = "idx_user_email" )
    private String email;

    private String openIdUrl;

    /**
     * If set to true, the user should not be able to login into the system.
     */
    private boolean disabled;

    @Temporal( TemporalType.TIMESTAMP )
    private Date lastLogin;

    @ManyToMany( cascade = CascadeType.PERSIST, fetch = FetchType.EAGER )
    @Cascade( org.hibernate.annotations.CascadeType.SAVE_UPDATE )
    @JoinTable(
            name = "ia_user2role",
            joinColumns = {@JoinColumn( name = "user_id" )},
            inverseJoinColumns = {@JoinColumn( name = "role_id" )}
    )
    @ForeignKey(name = "FK_USER", inverseName = "FK_ROLE")
    private Set<Role> roles;

    @OneToMany( mappedBy = "user" )
    @Cascade( value = org.hibernate.annotations.CascadeType.SAVE_UPDATE )
    private Collection<Preference> preferences;

//    private Collection<Favourite> favourites;

    //////////////////
    // Constructors

    public User() {
        this.roles = new HashSet<Role>();
        this.disabled = false;
    }

    public User( String login, String firstName, String lastName, String email ) {
        this();
        setLogin( login );
        setFirstName( firstName );
        setLastName( lastName );
        setEmail( email );
    }

    ///////////////////////////
    // Getters and Setters

    public Long getPk() {
        return pk;
    }

    public void setPk( Long pk ) {
        this.pk = pk;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin( String login ) {
        if ( login == null || login.trim().length() == 0 ) {
            throw new IllegalArgumentException( "You must give a non null login" );
        }
        this.login = login.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName( String firstName ) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName( String lastName ) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        if ( email == null || email.trim().length() == 0 ) {
            throw new IllegalArgumentException( "You must give a non null email" );
        }
        this.email = email;
    }

    public String getOpenIdUrl() {
        return openIdUrl;
    }

    public void setOpenIdUrl( String openIdUrl ) {
        this.openIdUrl = openIdUrl;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin( Date lastLogin ) {
        this.lastLogin = lastLogin;
    }

    /**
     * Gives read-only access to the roles, use the corresponding addRole, removeRole to update it.
     * @return
     */
    public Set<Role> getRoles() {
        return Collections.unmodifiableSet( roles );
    }

    public void addRole( Role role ) {
        if ( role == null ) {
            throw new IllegalArgumentException( "You must give a non null role" );
        }
        roles.add( role );
    }

    public void removeRole( Role role ) {
        if ( role == null ) {
            throw new IllegalArgumentException( "You must give a non null role" );
        }
        roles.remove( role );
    }

    public boolean hasRole( String roleName ) {
        if( roleName != null ) {
            roleName = roleName.trim();

            for ( Role role : roles ) {
                if( role.getName().equalsIgnoreCase( roleName ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled( boolean disabled ) {
        this.disabled = disabled;
    }

    public Collection<Preference> getPreferences() {
        return preferences;
    }

    public void setPreferences( Collection<Preference> preferences ) {
        this.preferences = preferences;
    }

    //////////////////////////
    // Object's override

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof User ) ) return false;

        User user = ( User ) o;

        if ( !login.equals( user.login ) ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return login.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append( "User" );
        sb.append( "{login='" ).append( login ).append( '\'' );
        sb.append( ", firstName='" ).append( firstName ).append( '\'' );
        sb.append( ", lastName='" ).append( lastName ).append( '\'' );
        sb.append( ", lastLogin=" ).append( lastLogin );
        sb.append( ", email='" ).append( email ).append( '\'' );
        sb.append( ", openIdUrl='" ).append( openIdUrl ).append( '\'' );
        sb.append( ", disabled='" ).append( disabled ).append( '\'' );
        sb.append( ", roles=" ).append( roles );
        sb.append( ", preferences=" ).append( preferences );
        sb.append( '}' );
        return sb.toString();
    }
}
