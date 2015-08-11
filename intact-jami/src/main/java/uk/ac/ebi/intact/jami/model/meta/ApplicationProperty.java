package uk.ac.ebi.intact.jami.model.meta;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import uk.ac.ebi.intact.jami.model.AbstractIntactPrimaryObject;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * An application property.
 *
 */
@Entity
@Table( name = "ia_application_prop" )
public class ApplicationProperty extends AbstractIntactPrimaryObject {

    private String key;

    private String value;


    //////////////////
    // Constructors

    public ApplicationProperty() {
    }

    public ApplicationProperty(String key, String value) {
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
