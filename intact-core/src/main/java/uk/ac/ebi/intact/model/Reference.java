/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO COMMENTS
 *
 * @author hhe
 * @version $Id$
 */
@Deprecated
public class Reference extends BasicObjectImpl {

    ///////////////////////////////////////
    //attributes

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    protected String submissionRefAc;
    protected String xrefAc;


    /**
     * TODO COMMENTS
     */
    private String title;

    /**
     * TODO COMMENTS
     */
    private String authors;

    ///////////////////////////////////////
    // associations

    /**
     * TODO COMMENTS
     */
    private Collection<AnnotatedObject> annotatedObjects = new ArrayList<AnnotatedObject>();

    /**
     * TODO COMMENTS
     */
    private SubmissionRef submissionRef;

    /**
     * TODO COMMENTS
     * TODO is it used ?
     */
    private Xref xref;

    /**
     * This constructor exists only for the benefit of subclasses so that
     * they can call the BasicObject no-arg constructor. This is an expcetion to
     * the general rule as this is the only concrete class which itself is already
     * subclassed.
     */
    public Reference() {
        super();
    }


    public Reference( Institution owner, String title, String authors ) {
        super( owner );
        if ( title == null ) throw new NullPointerException( "valid Reference must have a non-null title!" );
        if ( authors == null ) throw new NullPointerException( "valid Reference must have a non-null authors!" );

        this.title = title;
        this.authors = authors;
    }

    ///////////////////////////////////////
    //access methods for attributes

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors( String authors ) {
        this.authors = authors;
    }

    ///////////////////////////////////////
    // access methods for associations

    public Collection<? extends AnnotatedObject> getAnnotatedObjects() {
        return annotatedObjects;
    }

    public void addAnnotatedObject( AnnotatedObject annotatedObject ) {
        if ( !this.annotatedObjects.contains( annotatedObject ) ) {
            this.annotatedObjects.add( annotatedObject );
        }
    }

    public void removeAnnotatedObject( AnnotatedObject annotatedObject ) {
        this.annotatedObjects.remove( annotatedObject );
    }

    public SubmissionRef getSubmissionRef() {
        return submissionRef;
    }

    public void setSubmissionRef( SubmissionRef submissionRef ) {
        if ( this.submissionRef != submissionRef ) {
            this.submissionRef = submissionRef;
            if ( submissionRef != null ) submissionRef.setReference( this );
        }
    }

    /**
     * Equality for References is currently based on equality for
     * author, title and <code>Xrefs</code>.
     *
     * @param o The object to check
     *
     * @return true if the parameter equals this object, false otherwise
     *
     * @see uk.ac.ebi.intact.model.Xref
     */
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Reference ) ) return false;

        final Reference reference = ( Reference ) o;
        //TODO Auto-generated - needs to be readable when we use the class...
        // Bear in mind that if you test SubmissionRef you could get a cycle ...

        if ( authors != null ? !authors.equals( reference.authors ) : reference.authors != null ) return false;
        if ( title != null ? !title.equals( reference.title ) : reference.title != null ) return false;
        if ( xref != null ? !xref.equals( reference.xref ) : reference.xref != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;

        //TODO Auto-generated - needs to be readable when we use the class...
        result = ( title != null ? title.hashCode() : 0 );
        result = 29 * result + ( authors != null ? authors.hashCode() : 0 );
        result = 29 * result + ( submissionRef != null ? submissionRef.hashCode() : 0 );
        result = 29 * result + ( xref != null ? xref.hashCode() : 0 );
        return result;
    }


    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    public String getSubmissionRefAc() {
        return this.submissionRefAc;
    }

    public void setSubmissionRefAc( String ac ) {
        this.submissionRefAc = ac;
    }

    public String getXrefAc() {
        return this.xrefAc;
    }

    public void setXrefAc( String ac ) {
        this.xrefAc = ac;
    }

} // end Reference




