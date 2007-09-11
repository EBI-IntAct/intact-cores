/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;

/**
 * An alternative name for the object.
 * <p/>
 * <p/>
 * Currently, the name of the Alias is set to lowercase.
 * </p>
 *
 * @author hhe, Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @see uk.ac.ebi.intact.model.CvAliasType
 */
@Entity
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS )
public abstract class Alias extends BasicObjectImpl {

    protected static final int MAX_ALIAS_NAME_LEN = 30;

    ///////////////////////////////////////
    //attributes

    /**
     * Alternative name for the object.
     */
    private String name;

    ///////////////////////////////////////
    // associations

    /**
     * the type of that alias.
     */
    private CvAliasType cvAliasType;

    /**
     * Accession id to the Object to which refers that alias.
     */
    private String parentAc;

    private AnnotatedObject parent;

    /**
     */
    public Alias() {
        super();
    }

    /**
     * Create a new Alias for the given Annotated object
     *
     * @param anOwner         The institution owning this Alias
     * @param annotatedObject the object to which we'll add a new Alias
     * @param cvAliasType     the CvAliasType (may be null)
     * @param name            the name of the alias (namy be null)
     *
     * @see uk.ac.ebi.intact.model.CvAliasType
     * @see uk.ac.ebi.intact.model.AnnotatedObject
     */
    public Alias( Institution anOwner, AnnotatedObject annotatedObject, CvAliasType cvAliasType, String name ) {
        setOwner( anOwner );
        setParentAc( annotatedObject.getAc() );
        setCvAliasType( cvAliasType );
        setName( name );
    }

    ///////////////////////////////////////
    //access methods for attributes
    @Column( length = 30 )
    public String getName() {
        return name;
    }

    public void setName( String name ) {

        if ( name != null ) {

            // delete leading and trailing spaces.
            name = name.trim();

            if ( name.length() >= MAX_ALIAS_NAME_LEN ) {
                name = name.substring( 0, MAX_ALIAS_NAME_LEN );
            }
        }

        this.name = name;
    }

    @Transient
    public String getParentAc() {
        if ( parent != null ) {
            parentAc = parent.getAc();
        }
        return parentAc;
    }

    public void setParentAc( String parentAc ) {
        this.parentAc = parentAc;
    }

    @Transient
    public AnnotatedObject getParent() {
        return parent;
    }

    public void setParent( AnnotatedObject parent ) {
        this.parent = parent;
    }

    ///////////////////////////////////////
    // access methods for associations

    @ManyToOne(cascade = CascadeType.PERSIST)
    @Cascade (value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn( name = "aliastype_ac" )
    public CvAliasType getCvAliasType() {
        return cvAliasType;
    }

    public void setCvAliasType( CvAliasType cvAliasType ) {
        this.cvAliasType = cvAliasType;
    }

    /**
     * Equality for Aliases is currently based on equality for
     * <code>CvAliasTypes</code> and names.
     *
     * @param o The object to check
     *
     * @return true if the parameter equals this object, false otherwise
     *
     * @see uk.ac.ebi.intact.model.CvAliasType
     */
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Alias ) ) return false;

        //NO! BasicObject's equals is the Java Object one!!
        //if ( !super.equals ( o ) ) return false;

        final Alias alias = ( Alias ) o;

        //NB according to the constructor, cvAliasType and name may be null,
        //so need to handle this here....
        if ( cvAliasType != null ) {
            if ( !cvAliasType.equals( alias.cvAliasType ) ) return false;
        } else {
            if ( alias.cvAliasType != null ) return false;
        }

        if ( name != null ) {
            if ( !name.equals( alias.name ) ) return false;
        } else
            return alias.name == null;

        return true;
    }

    @Override
    public int hashCode() {
        int result = 29;
        if ( name != null ) result = 29 * result + name.hashCode();
        if ( cvAliasType != null ) result = 29 * result + cvAliasType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Alias[name: " + name + ", type: " +
               ( cvAliasType != null ? cvAliasType.getShortLabel() : "" ) + "]";
    }

    /**
     * Clone a an alias
     *
     * @return an alias setting it ac, parentAc and parent to null.
     *
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        Alias copy = ( Alias ) super.clone();
        // Reset the parent ac.
        copy.parentAc = null;
        copy.parent = null;
        return copy;
    }

} // end Alias




