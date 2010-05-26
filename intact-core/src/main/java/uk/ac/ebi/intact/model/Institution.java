/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * Represents the contact details for an institution.
 *
 * @author Henning Hermjakob
 * @version $Id$
 */
// TODO cf. note

@Entity
@Table(name = "ia_institution")
public class Institution extends AnnotatedObjectImpl<InstitutionXref,InstitutionAlias> implements Serializable, AnnotatedObject<InstitutionXref,InstitutionAlias> {

    //////////////////////
    // Constants

    public static final String INTACT = "intact";
    public static final String INTACT_REF = "MI:0469";

    public static final String MINT = "mint";
    public static final String MINT_REF = "MI:0471";

    public static final String DIP = "dip";
    public static final String DIP_REF = "MI:0465";

    ///////////////////////////////////////
    //attributes

    /**
     * Postal address.
     * Format: One string with line breaks.
     */
    protected String postalAddress;

    /**
     * TODO comments
     */
    protected String url;

    ///////////////////////////////////////
    // Constructors
    public Institution() {
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
    public Institution(String shortLabel) {
        setShortLabel(shortLabel);
    }

    ///////////////////////////////////////
    // entity listeners

    @Override
    protected void prepareShortLabel() {
        // do not modify institution short labels
    }

    ///////////////////////////////////////
    // access methods for attributes


    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @OneToMany( mappedBy = "parent", cascade = {CascadeType.ALL})
    public Collection<InstitutionAlias> getAliases() {
        return super.getAliases();
    }

    @ManyToMany( cascade = {CascadeType.PERSIST} )
    //@Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @JoinTable(
            name = "ia_institution2annot",
            joinColumns = {@JoinColumn( name = "institution_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "annotation_ac" )}
    )
    public Collection<Annotation> getAnnotations() {
        return super.getAnnotations();
    }

    @OneToMany( mappedBy = "parent", cascade = {CascadeType.ALL})
    public Collection<InstitutionXref> getXrefs() {
        return super.getXrefs();
    }

    @Transient
    public Institution getOwner() {
        return this;
    }


    ///////////////////////////////////////
    // instance methods

    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Institution ) ) {
            return false;
        }

        if ( !super.equals( o ) ) {
            return false;
        }

        Institution that = (Institution) o;

        if (postalAddress != null ? !postalAddress.equals(that.postalAddress) : that.postalAddress != null)
            return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (postalAddress != null ? postalAddress.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return 31*super.hashCode();
    }

    public String toString() {
        return "Institution: "+getShortLabel();
    }
} // end Institution





