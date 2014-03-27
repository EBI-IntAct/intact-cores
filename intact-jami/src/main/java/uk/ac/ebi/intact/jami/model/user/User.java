package uk.ac.ebi.intact.jami.model.user;

import org.hibernate.Hibernate;
import org.hibernate.annotations.*;
import uk.ac.ebi.intact.jami.model.AbstractIntactPrimaryObject;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

/**
 * An intact user.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
@Entity
@Table(name="ia_user")
public class User extends AbstractIntactPrimaryObject {

    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    /**
     * If set to true, the user should not be able to login into the system.
     */
    private boolean disabled;
    private Date lastLogin;
    private Set<Role> roles;
    private Collection<Preference> preferences;


    //////////////////
    // Constructors

    protected User() {
        this.roles = new HashSet<Role>();
        this.preferences = new ArrayList<Preference>();
        this.disabled = false;
    }

    public User(String login, String firstName, String lastName, String email) {
        this();
        setLogin(login);
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
    }

    ///////////////////////////
    // Getters and Setters

    @Column(nullable = false, unique = true, length = IntactUtils.MAX_SHORT_LABEL_LEN)
    @Index(name = "idx_user_login")
    @Size( max = IntactUtils.MAX_SHORT_LABEL_LEN)
    @NotNull
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        if (login == null || login.trim().length() == 0) {
            throw new IllegalArgumentException("You must give a non null login");
        }
        this.login = login.trim();
    }

    @Size( max = IntactUtils.MAX_SHORT_LABEL_LEN)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(nullable = false, length = IntactUtils.MAX_SHORT_LABEL_LEN)
    @Size( max = IntactUtils.MAX_SHORT_LABEL_LEN)
    @NotNull
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (firstName == null || firstName.length() == 0) {
            throw new IllegalArgumentException("You must give a non null first name");
        }
        this.firstName = firstName;
    }

    @Column(nullable = false, length = IntactUtils.MAX_SHORT_LABEL_LEN)
    @Size( max = IntactUtils.MAX_SHORT_LABEL_LEN)
    @NotNull
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.length() == 0) {
            throw new IllegalArgumentException("You must give a non null last name");
        }
        this.lastName = lastName;
    }

    @Column(nullable = false, unique = true, length = IntactUtils.MAX_SHORT_LABEL_LEN)
    @Index(name = "idx_user_email")
    @Size( max = IntactUtils.MAX_SHORT_LABEL_LEN)
    @NotNull
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().length() == 0) {
            throw new IllegalArgumentException("You must give a non null email");
        }
        this.email = email.trim();
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    /**
     * Gives read-only access to the roles, use the corresponding addRole, removeRole to update it.
     *
     * @return
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinTable(
            name = "ia_user2role",
            joinColumns = {@JoinColumn(name = "user_ac")},
            inverseJoinColumns = {@JoinColumn(name = "role_ac")}
    )
    @ForeignKey(name = "FK_USER_ROLES", inverseName = "FK_ROLE_USER")
    @LazyCollection(LazyCollectionOption.FALSE)
    public Set<Role> getRoles() {
        return roles;
    }

    public void addRole(Role role) {
        if (role != null) {
            roles.add(role);
        }
    }

    public void removeRole(Role role) {
        if (role != null) {
            roles.remove(role);
        }
    }

    private void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public boolean hasRole(String roleName) {
        if (roleName != null) {
            return this.roles.contains(roleName.trim().toUpperCase());
        }
        return false;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @OneToMany( cascade = {CascadeType.ALL},orphanRemoval = true)
    @Cascade( value = org.hibernate.annotations.CascadeType.SAVE_UPDATE )
    @JoinColumn(name = "user_ac", referencedColumnName = "ac")
    @ForeignKey(name="FK_PREF_USER")
    @LazyCollection(LazyCollectionOption.FALSE)
    public Collection<Preference> getPreferences() {
        return preferences;
    }

    @Transient
    public boolean areRolesInitialized(){
        return Hibernate.isInitialized(getRoles());
    }

    @Transient
    public boolean arePreferencesInitialized(){
        return Hibernate.isInitialized(getPreferences());
    }

    private void setPreferences(Collection<Preference> preferences) {
        this.preferences = preferences;
    }

    public Preference getPreference(String prefKey) {
        for (Preference pref : getPreferences()) {
            if (pref.getKey().equals(prefKey)) {
                return pref;
            }
        }
        return null;
    }

    public Preference addPreference(String key, String value) {
        Preference preference = new Preference(key, value);
        addPreference(preference);
        return preference;
    }

    public Preference addPreference(Preference preference) {
        if (preference != null) {
            getPreferences().add(preference);
        }
        return preference;
    }

    public Preference addOrUpdatePreference(String key, String value) {
        for (Preference preference : getPreferences()) {
            if (key.equals(preference.getKey())) {
                preference.setValue(value);
                return preference;
            }
        }

        return addPreference(key, value);
    }

    public void removePreference(Preference preference) {
        if (preference != null) {
            preferences.remove(preference);
        }
    }

    //////////////////////////
    // Object's override

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("User");
        sb.append("{login='").append(login).append('\'');
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", lastLogin=").append(lastLogin);
        sb.append(", email='").append(email).append('\'');
        sb.append(", disabled='").append(disabled).append('\'');
        sb.append(", roles=").append(roles);
        sb.append(", preferences=").append(preferences);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof User ) ) return false;

        User user = ( User ) o;

        return login.equals(user.login);
    }

    @Override
    public int hashCode() {
        return login.hashCode();
    }
}
