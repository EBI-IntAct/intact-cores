/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
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
public class Institution extends IntactObjectImpl implements Serializable, AnnotatedObject<InstitutionXref,InstitutionAlias> {

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

    /**
     * Short name for the object, not necessarily unique. To be used for example
     * in minimised displays of the object.
     */

    protected String shortLabel;

    /**
     * The full name or a minimal description of the object.
     */
    protected String fullName;

    ///////////////////////////////////////
    // associations

    /**
     *
     */

    public Collection<Annotation> annotations = new ArrayList<Annotation>();

    /**
     *
     */

    public Collection<InstitutionXref> xrefs = new ArrayList<InstitutionXref>();

    /**
     * Hold aliases of an Annotated object.
     * ie. alternative name for the current object.
     */
    private Collection<InstitutionAlias> aliases = new ArrayList<InstitutionAlias>();


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
        this.shortLabel = prepareLabel(shortLabel);
    }

    private String prepareLabel(String shortLabel) {
        if (shortLabel == null) {
            throw new NullPointerException("Must define a short label to create an Institution!");
        }

        // delete leading and trailing spaces.
        shortLabel = shortLabel.trim();

        if ("".equals(shortLabel)) {
            throw new IllegalArgumentException("Must define a short label to create an Institution!");
        }

        if (shortLabel.length() >= AnnotatedObject.MAX_SHORT_LABEL_LEN) {
            shortLabel = shortLabel.substring(0, AnnotatedObject.MAX_SHORT_LABEL_LEN);
        }

        return shortLabel;
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

    @OneToMany( mappedBy = "parent", cascade = {CascadeType.ALL} )
    public Collection<InstitutionAlias> getAliases() {
        if (aliases == null) {
            aliases = new ArrayList<InstitutionAlias>();
        }
        return aliases;
    }

    public void addAlias(InstitutionAlias alias) {
        getAliases().add(alias);
    }

    public void removeAlias(InstitutionAlias alias) {
        getAliases().remove(alias);
    }

    public void setAliases(Collection<InstitutionAlias> aliases) {
        this.aliases = aliases;
    }

    @ManyToMany( cascade = {CascadeType.PERSIST} )
    //@Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @JoinTable(
            name = "ia_institution2annot",
            joinColumns = {@JoinColumn( name = "institution_ac" )},
            inverseJoinColumns = {@JoinColumn( name = "annotation_ac" )}
    )
    public Collection<Annotation> getAnnotations() {
        if (annotations == null) {
            annotations = new ArrayList<Annotation>();
        }
        return annotations;
    }

    public void setAnnotations(Collection<Annotation> annotations) {
        this.annotations = annotations;
    }

    public void addAnnotation(Annotation annotation) {
        getAnnotations().add(annotation);
    }

    public void removeAnnotation(Annotation annotation) {
        getAnnotations().remove(annotation);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Length( min = 1, max = MAX_SHORT_LABEL_LEN )
    @NotNull
    public String getShortLabel() {
        return shortLabel;
    }

    public void setShortLabel(String shortLabel) {
        this.shortLabel = shortLabel;
    }

    @OneToMany( mappedBy = "parent", cascade = {CascadeType.ALL} )
    public Collection<InstitutionXref> getXrefs() {
        if (xrefs == null) {
            xrefs = new ArrayList<InstitutionXref>();
        }
        return xrefs;
    }

    public void addXref(InstitutionXref aXref) {
        getXrefs().add(aXref);
    }

    public void removeXref(InstitutionXref xref) {
        getXrefs().remove(xref);
    }

    public void setXrefs(Collection<InstitutionXref> xrefs) {
        this.xrefs = xrefs;
    }

    @Transient
    public Institution getOwner() {
        return this;
    }

    @Deprecated
    public void setOwner(Institution institution) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Transient
    public String getOwnerAc() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public void setOwnerAc(String ac) {
        throw new UnsupportedOperationException();
    }

    ///////////////////////////////////////
    // instance methods

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Institution that = (Institution) o;

        if (aliases != null ? !aliases.equals(that.aliases) : that.aliases != null) return false;
        if (annotations != null ? !annotations.equals(that.annotations) : that.annotations != null) return false;
        if (fullName != null ? !fullName.equals(that.fullName) : that.fullName != null) return false;
        if (postalAddress != null ? !postalAddress.equals(that.postalAddress) : that.postalAddress != null)
            return false;
        if (shortLabel != null ? !shortLabel.equals(that.shortLabel) : that.shortLabel != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (xrefs != null ? !xrefs.equals(that.xrefs) : that.xrefs != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (postalAddress != null ? postalAddress.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (shortLabel != null ? shortLabel.hashCode() : 0);
        result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
        result = 31 * result + (annotations != null ? annotations.hashCode() : 0);
        result = 31 * result + (xrefs != null ? xrefs.hashCode() : 0);
        result = 31 * result + (aliases != null ? aliases.hashCode() : 0);
        return result;
    }

    public String toString() {
        return shortLabel;
    }
} // end Institution





