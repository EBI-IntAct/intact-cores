package uk.ac.ebi.intact.core.users.model;

import org.apache.commons.lang.StringUtils;

/**
 * A user favourite.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public class Favourite implements HasIdentity {

    private Long pk;
    private String type;
    private String ac;
    private String label;

    //////////////////
    // Constructors

    protected Favourite() {
    }

    public Favourite( String type, String ac ) {
        if ( StringUtils.isEmpty(type) ) {
            throw new IllegalArgumentException( "You must give a non null type" );
        }
        if ( StringUtils.isEmpty(ac) ) {
            throw new IllegalArgumentException( "You must give a non null ac" );
        }
        this.type = type;
        this.ac = ac;
    }

    public Favourite( String type, String ac, String label ) {
        this(type, ac);
        this.label = label;
    }

    ///////////////////////////
    // Getters and Setters


    public Long getPk() {
        return pk;
    }

    public void setPk( Long pk ) {
        this.pk = pk;
    }

    public String getType() {
        return type;
    }

    public String getAc() {
        return ac;
    }

    public void setAc( String ac ) {
        this.ac = ac;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel( String label ) {
        this.label = label;
    }

    //////////////////////////
    // Object's override

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Favourite ) ) return false;

        Favourite favourite = ( Favourite ) o;

        if ( !ac.equals( favourite.ac ) ) return false;
        if ( !type.equals( favourite.type ) ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + ac.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append( "Favourite" );
        sb.append( "{type='" ).append( type ).append( '\'' );
        sb.append( ", ac='" ).append( ac ).append( '\'' );
        sb.append( ", label='" ).append( label ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }
}
