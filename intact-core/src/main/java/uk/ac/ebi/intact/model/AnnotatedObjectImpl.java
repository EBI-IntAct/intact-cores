/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import uk.ac.ebi.intact.model.util.AnnotatedObjectUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents an object with biological annotation.
 *
 * @author hhe
 */
@MappedSuperclass
public abstract class AnnotatedObjectImpl<T extends Xref, A extends Alias> extends BasicObjectImpl implements AnnotatedObject<T, A> {

    private static final Log log = LogFactory.getLog( AnnotatedObjectImpl.class );

    /////////////////////////////////
    //attributes

    //attributes used for mapping BasicObjects - project synchron
    protected String curatorAc;

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
    public Collection<T> xrefs = new ArrayList<T>();

    /**
     * Hold aliases of an Annotated object.
     * ie. alternative name for the current object.
     */
    private Collection<A> aliases = new ArrayList<A>();

    /**
     * no-arg constructor provided for compatibility with subclasses
     * that have no-arg constructors.
     */
    protected AnnotatedObjectImpl() {
        //super call sets creation time data
        super();
    }

    /**
     * Constructor for subclass use only. Ensures that AnnotatedObjects cannot be
     * created without at least a shortLabel and an owner specified.
     *
     * @param shortLabel The memorable label to identify this AnnotatedObject
     * @param owner      The Institution which owns this AnnotatedObject
     *
     * @throws NullPointerException thrown if either parameters are not specified
     */
    protected AnnotatedObjectImpl( String shortLabel, Institution owner ) {

        //super call sets creation time data
        super();

        this.shortLabel = AnnotatedObjectUtils.prepareShortLabel( shortLabel );
        setOwner( owner );
    }

    ////////////////////////////////////////
    // Event listeners
    @PrePersist
    public void onPrePersist() {
        prepareShortLabel();
    }

    @PreUpdate
    public void onPreUpdate() {
        prepareShortLabel();
    }

    protected void prepareShortLabel() {
        if (shortLabel != null && shortLabel.length() > 0) {
            shortLabel = shortLabel.toLowerCase();
        }
    }

    // Class methods

    ///////////////////////////////////////
    //access methods for attributes

    @Length( min = 1, max = MAX_SHORT_LABEL_LEN )
    @NotNull
    public String getShortLabel() {
        return shortLabel;
    }

    public void setShortLabel( String shortLabel ) {
        if ( shortLabel != null && shortLabel.length() > MAX_SHORT_LABEL_LEN ) {
            throw new FieldTooLongException( shortLabel, "shortLabel", MAX_SHORT_LABEL_LEN );
        }

        this.shortLabel = shortLabel;
    }

    @Column( length = 250 )
    public String getFullName() {
        return fullName;
    }

    public void setFullName( String fullName ) {
        this.fullName = fullName;
    }

    ///////////////////////////////////////
    // access methods for associations
    public void setAnnotations( Collection<Annotation> someAnnotation ) {
        this.annotations = someAnnotation;
    }

    /**
     * This property must be overriden so it can have proper mappings
     *
     * @return
     */
    @Transient
    public Collection<Annotation> getAnnotations() {
        return annotations;
    }

    public void addAnnotation( Annotation annotation ) {
        if ( !this.annotations.contains( annotation ) ) {
            this.annotations.add( annotation );
        }
    }

    public void removeAnnotation( Annotation annotation ) {
        this.annotations.remove( annotation );
    }

    ///////////////////
    // Xref related
    ///////////////////
    public void setXrefs( Collection<T> someXrefs ) {
        this.xrefs = someXrefs;
    }

    @Transient
    public Collection<T> getXrefs() {
        return xrefs;
    }

    /**
     * Adds an xref to the object. The xref will only be added
     * if an equivalent xref is not yet part of the object.
     */
    public void addXref( T aXref ) {
        //if( !this.xrefs.contains( aXref ) ) {
        this.xrefs.add( aXref );
        aXref.setParent( this );
        //aXref.setParentAc(this.getAc());
        //}
    }

    public void removeXref( T xref ) {
        this.xrefs.remove( xref );
    }


    ///////////////////
    // Alias related
    ///////////////////
    public void setAliases( Collection<A> someAliases ) {
        this.aliases = someAliases;
    }

    @Transient
    public Collection<A> getAliases() {
        return aliases;
    }

    /**
     * Adds an alias to the object. The alis will only be added
     * if an equivalent alias is not yet part of the object.
     */
    public void addAlias( A alias ) {
        this.aliases.add( alias );
        alias.setParent( this );
    }

    public void removeAlias( A alias ) {
        this.aliases.remove( alias );
    }

    ///////////////////////////////////////
    // instance methods

    /**
     * Equality for AnnotatedObjects is currently based on equality for
     * <code>Xrefs</code>, shortLabels and fullNames.
     *
     * @param o The object to check
     *
     * @return true if the parameter equals this object, false otherwise
     *
     * @see uk.ac.ebi.intact.model.Xref
     */
    @Override
    public boolean equals( Object o ) {
        // TODO: the reviewed version of the intact model will provide a better implementation
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof AnnotatedObject ) ) {
            return false;
        }

        //YUK! This ends up calling the java Object 'equals', which compares
        //on identity - NOT what we want....
//        if (!super.equals(o)) {
//            return false;
//        }

        final AnnotatedObject annotatedObject = ( AnnotatedObject ) o;

        if ( annotatedObject.getAc() != null & ac != null ) {
            return ac.equals( annotatedObject.getAc() );
        }

        //short label and full name (if it exists) must be equal also..
        if ( shortLabel != null ) {
            if ( !shortLabel.equals( annotatedObject.getShortLabel() ) ) {
                return false;
            }
        } else {
            if ( annotatedObject.getShortLabel() != null ) {
                return false;
            }
        }

        if ( fullName != null ) {
            if ( !fullName.equals( annotatedObject.getFullName() ) ) {
                return false;
            }
        } else {
            if ( annotatedObject.getFullName() != null ) {
                return false;
            }
        }

        if (!CollectionUtils.isEqualCollection( getXrefs(), annotatedObject.getXrefs() )) {
            return false;
        }
        if (!CollectionUtils.isEqualCollection( getAnnotations(), annotatedObject.getAnnotations() )) {
            return false;
        }
        if (!CollectionUtils.isEqualCollection( getAliases(), annotatedObject.getAliases() )) {
            return false;
        }

        return true;
    }

    /**
     * This class overwrites equals. To ensure proper functioning of HashTable,
     * hashCode must be overwritten, too.
     *
     * @return hash code of the object.
     */
    @Override
    public int hashCode() {

        //YUK AGAIN! ends up as a call to Object's hashcode, which is
        //based on indentity. Not require here...
        //int code = super.hashCode();

        int code = 29;

        if ( ac != null ) {
            return code * ac.hashCode();
        }

        for ( Xref xref : xrefs ) {
            if ( xref.getPrimaryId() != null ) {
                code = 29 * code + xref.getPrimaryId().hashCode();
            }
        }

        for ( Annotation annotation : annotations ) {
            if(annotation.getAnnotationText()!=null){
                code = 29 * code + annotation.getAnnotationText().hashCode();
            }    
        }

        for ( Alias alias : aliases ) {
            if ( alias.getName() != null ) {
                code = 29 * code + alias.getName().hashCode();
            }
        }

        if ( null != shortLabel ) {
            code = 29 * code + shortLabel.hashCode();
        }
        if ( null != fullName ) {
            code = 29 * code + fullName.hashCode();
        }

        return code;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        AnnotatedObjectImpl copy = ( AnnotatedObjectImpl ) super.clone();

        Collection<T> xrefs = getXrefs();

        Collection<A> aliases = getAliases();

        // Append the "-x" to the short label.
        if( copy.shortLabel.length() <= MAX_SHORT_LABEL_LEN - 2 ) {
            // only append '-x' if it is not going to make the shortlabel longer than 20 chars.
            copy.shortLabel += "-x";
        } else {
            log.error( "Could not append '-x' to this " + this.getClass().getSimpleName() + " shorltabel("+
                       copy.shortLabel +") as it would make it longer than the maximum supported ("+
                       MAX_SHORT_LABEL_LEN+" chars).");
        }

        // Clone annotations; can't use annotations.clone here as annoatations
        // type is shown as a ListProxy (ClassCastException)
        copy.annotations = new ArrayList<Annotation>( annotations.size() );
        for ( Annotation annotation : annotations ) {
            copy.annotations.add( ( Annotation ) annotation.clone() );
        }

        // Clone xrefs.
        Collection<Xref> copiedXrefs = new ArrayList<Xref>( xrefs.size() );
        // Make deep copies.
        for ( Xref xref : getXrefs() ) {
            Xref xrefClone = ( Xref ) xref.clone();
            xrefClone.setParent( copy );
            copiedXrefs.add( xrefClone );
        }
        copy.setXrefs( copiedXrefs );

        Collection<Alias> copiedAliases = new ArrayList<Alias>( aliases.size() );
        for ( Alias alias : getAliases() ) {
            Alias aliasClone = ( Alias ) alias.clone();
            aliasClone.setParent( copy );
            copiedAliases.add( ( Alias ) alias.clone() );
        }
        copy.setAliases( copiedAliases );

        return copy;
    }

    @Override
    public String toString() {
        return this.getAc() + "; owner=" + this.getOwner().getAc()
               + "; name=" + this.shortLabel + "; fullname=" + fullName;
    }
}