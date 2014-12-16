package uk.ac.ebi.intact.jami.model.meta;

import org.hibernate.Hibernate;
import org.hibernate.annotations.*;
import uk.ac.ebi.intact.jami.model.AbstractIntactPrimaryObject;

import javax.persistence.CascadeType;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The persistent application details.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
@Entity
@Table( name = "ia_application" )
public class Application extends AbstractIntactPrimaryObject {

    private String key;

    private String description;

    private Collection<ApplicationProperty> properties;

//    private Collection<Favourite> favourites;

    //////////////////
    // Constructors

    public Application() {

    }

    public Application(String key, String description) {
        this();
        this.key = key;
        this.description = description;
    }

    ///////////////////////////
    // Getters and Setters

    @Column( nullable = false, unique = true )
    @NotNull
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name="application_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @LazyCollection(LazyCollectionOption.FALSE)
    @ForeignKey(name="FK_PROP_APPLICATION")
    public Collection<ApplicationProperty> getProperties() {
        return properties;
    }

    private void setProperties(Collection<ApplicationProperty> properties) {
        this.properties = properties;
    }

    public ApplicationProperty getProperty(String propKey) {
        if (this.properties == null){
            this.properties = new ArrayList<ApplicationProperty>();
        }
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

    public boolean areApplicationPropertiesInitialised(){
        return Hibernate.isInitialized(getProperties());
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
