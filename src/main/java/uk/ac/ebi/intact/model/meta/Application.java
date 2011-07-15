package uk.ac.ebi.intact.model.meta;

import org.hibernate.annotations.Cascade;
import uk.ac.ebi.intact.model.IntactObjectImpl;

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
@Table( name = "ia_application" )
public class Application extends IntactObjectImpl  {

    private String key;

    private String description;

    private Collection<ApplicationProperty> properties;

//    private Collection<Favourite> favourites;

    //////////////////
    // Constructors

    public Application() {
        properties = new ArrayList<ApplicationProperty>();
    }

    public Application(String key, String description) {
        this();
        this.key = key;
        this.description = description;
    }

    ///////////////////////////
    // Getters and Setters

    @Column( nullable = false, unique = true )
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Lob
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @OneToMany( mappedBy = "application", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.EAGER )
    @Cascade( value = org.hibernate.annotations.CascadeType.SAVE_UPDATE )
    public Collection<ApplicationProperty> getProperties() {
        return properties;
    }

    public void setProperties(Collection<ApplicationProperty> properties) {
        this.properties = properties;
    }

    public ApplicationProperty getProperty(String propKey) {
        for (ApplicationProperty appProperty : getProperties()) {
            if (appProperty.getKey().equals(propKey)) {
                return appProperty;
            }
        }
        return null;
    }

    public void addProperty(ApplicationProperty property) {
        getProperties().add(property);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Application that = (Application) o;

        if (key != null ? !key.equals(that.key) : that.key != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (key != null ? key.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Application{" +
                "ac='" + getAc() + "', " +
                "key='" + key + "'" +
                "}";
    }
}
