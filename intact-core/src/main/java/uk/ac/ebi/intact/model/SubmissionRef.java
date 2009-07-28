/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.Date;

/**
 * TODO Represents ...
 *
 * @author Henning Hermjakob
 * @version $Id$
 */
public class SubmissionRef extends Reference {

    ///////////////////////////////////////
    //attributes

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    protected String referenceAc;

    /**
     * todo Represents ...
     */
    private Date holdDate;

    ///////////////////////////////////////
    // associations

    /**
     * TODO comments
     */
    private Reference reference;

    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     *
     * @deprecated Use the full constructor instead
     */
    @Deprecated
    public SubmissionRef() {
        super();
    }

    public SubmissionRef( Institution owner, String title, String authors, Reference reference, Date holdDate ) {
        super( owner, title, authors );

        // TODO null ?
        this.reference = reference;
        this.holdDate = holdDate;
    }

    ///////////////////////////////////////
    //access methods for attributes

    public Date getHoldDate() {
        return holdDate;
    }

    public void setHoldDate( Date holdDate ) {
        this.holdDate = holdDate;
    }

    ///////////////////////////////////////
    // access methods for associations

    public Reference getReference() {
        return reference;
    }

    public void setReference( Reference reference ) {
        if ( this.reference != reference ) {
            this.reference = reference;
            if ( reference != null ) reference.setSubmissionRef( this );
        }
    }


    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    public String getReferenceAc() {
        return referenceAc;
    }

    public void setReferenceAc( String ac ) {
        this.referenceAc = ac;
    }

} // end SubmissionRef




