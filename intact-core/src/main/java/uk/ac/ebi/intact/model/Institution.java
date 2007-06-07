/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Represents the contact details for an institution.
 *
 * @author Henning Hermjakob
 * @version $Id$
 */
// TODO cf. note

@Entity
@Table( name = "ia_institution" )
public class Institution extends IntactObjectImpl implements Serializable {

    ///////////////////////////////////////
    //attributes

    /**
     * The name of the institution.
     */
    protected String shortLabel;

    /**
     * Postal address.
     * Format: One string with line breaks.
     */
    protected String postalAddress;

    /**
     * TODO comments
     */
    protected String fullName;

    /**
     * TODO comments
     */
    protected String url;


    ///////////////////////////////////////
    // Constructors
    public Institution() {
        super();
    }

    /**
     * This constructor ensures creation of a valid Institution. Specifically
     * it must have at least a shortLabel defined since this is indexed in persistent store.
     * Note that a side-effect of this constructor is to set the <code>created</code> and
     * <code>updated</code> fields of the instance to the current time.
     *
     * @param shortLabel The short label used to refer to this Institution.
     *
     * @throws NullPointerException if an attempt is made to create an Instiution without
     *                              defining a shortLabel.
     */
    public Institution( String shortLabel ) {
        this();

        this.shortLabel = prepareLabel( shortLabel );
    }

    ///////////////////////////////////////
    // access methods for attributes

    public String getShortLabel() {
        return shortLabel;
    }

    public void setShortLabel( String shortLabel ) {
        this.shortLabel = shortLabel;
    }

    private String prepareLabel( String shortLabel ) {
        if ( shortLabel == null ) {
            throw new NullPointerException( "Must define a short label to create an Institution!" );
        }

        // delete leading and trailing spaces.
        shortLabel = shortLabel.trim();

        if ( "".equals( shortLabel ) ) {
            throw new IllegalArgumentException( "Must define a short label to create an Institution!" );
        }

        if ( shortLabel.length() >= AnnotatedObject.MAX_SHORT_LABEL_LEN ) {
            shortLabel = shortLabel.substring( 0, AnnotatedObject.MAX_SHORT_LABEL_LEN );
        }

        return shortLabel;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress( String postalAddress ) {
        this.postalAddress = postalAddress;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName( String fullName ) {
        this.fullName = fullName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl( String url ) {
        this.url = url;
    }

    ///////////////////////////////////////
    // instance methods

    /**
     * Equality for Institutions is currently based on equal shortLabels and fullNames.
     *
     * @param o The object to check
     *
     * @return true if the parameter equlas this object, false otherwise
     */
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Institution ) ) return false;

        final Institution institution = ( Institution ) o;

        if ( !shortLabel.equals( institution.shortLabel ) ) {
            return false;
        }

        if ( fullName != null ) {
            return ( fullName.equals( institution.fullName ) );
        }

        return institution.fullName == null;
    }

    /**
     * This class overwrites equals. To ensure proper functioning of HashTable,
     * hashCode must be overwritten, too.
     *
     * @return hash code of the object.
     */
    @Override
    public int hashCode() {

        int code = 29;

        //still need shortLabel check as we still have no-arg constructor..
        code = 29 * code + shortLabel.hashCode();
        if ( null != fullName ) code = 29 * code + fullName.hashCode();

        return code;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer( 64 );

        sb.append( "ShortLabel:" ).append( shortLabel );
        sb.append( " Fullname:" ).append( fullName );

        return sb.toString();
    }
} // end Institution





