package uk.ac.ebi.intact.model.meta;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import uk.ac.ebi.intact.model.IntactObjectImpl;
import uk.ac.ebi.intact.model.user.User;

import javax.persistence.*;

/**
 * A user preference.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
@Entity
@Table( name = "ia_application_prop" )
public class ApplicationProperty extends IntactObjectImpl {

    private String key;

    private String value;

    private Application application;

    //////////////////
    // Constructors

    public ApplicationProperty() {
    }

    public ApplicationProperty(Application application, String key, String value) {
        this.application = application;
        this.key = key;
        this.value = value;
    }

    ///////////////////////////
    // Getters and Setters

    @Index( name = "idx_app_prop_key" )
    public String getKey() {
        return key;
    }

    public void setKey( String key ) {
        this.key = key;
    }

    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    @ManyToOne
    @JoinColumn( name = "application_ac" )
    @ForeignKey(name="FK_PROP_APPLICATION")
    public Application getApplication() {
        return application;
    }

    public void setApplication( Application application ) {
        this.application = application;
    }

    //////////////////////////
    // Object's override
    
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof ApplicationProperty) ) return false;

        ApplicationProperty that = (ApplicationProperty) o;

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
        sb.append( "Preference" );
        sb.append( "{key='" ).append( key ).append( '\'' );
        sb.append( ", value='" ).append( value ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }
}
