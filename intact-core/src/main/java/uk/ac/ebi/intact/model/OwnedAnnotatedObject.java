/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

/**
 * Represents an object with biological annotation.
 *
 * @author hhe
 */
@MappedSuperclass
public abstract class OwnedAnnotatedObject<T extends Xref, A extends Alias> extends AnnotatedObjectImpl<T,A> implements OwnedObject {

    private static final Log log = LogFactory.getLog( OwnedAnnotatedObject.class );

    /////////////////////////////////
    //attributes

    private Institution owner;

    /**
     * no-arg constructor provided for compatibility with subclasses
     * that have no-arg constructors.
     */
    protected OwnedAnnotatedObject() {
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
    protected OwnedAnnotatedObject( String shortLabel, Institution owner ) {
        //super call sets creation time data
        super(shortLabel);
        setOwner( owner );
    }

    @ManyToOne
    @JoinColumn( name = "owner_ac", nullable = false )
    public Institution getOwner() {
        return owner;
    }

    public void setOwner( Institution institution ) {
        this.owner = institution;
    }
}