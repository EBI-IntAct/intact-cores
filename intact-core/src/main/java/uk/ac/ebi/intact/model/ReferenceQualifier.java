/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

/**
 * This class qualifies the association between an AnnotatedObject and a
 * Reference.
 *
 * @author hhe
 * @version $Id$
 */
// Who owns it ? Owner needed ? Is this persistent ? if so needs to have an AC
public class ReferenceQualifier {

    ///////////////////////////////////////
    // associations

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    public String referenceAc;
    public String cvReferenceQualifierAc;

    /**
     * TODO comments
     */
    private Reference reference;

    /**
     * TODO comments
     */
    private CvReferenceQualifier cvReferenceQualifier;

    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     *
     * @deprecated Use the full constructor instead
     */
    @Deprecated
    public ReferenceQualifier() {
        super();
    }

    public ReferenceQualifier( Reference reference, CvReferenceQualifier cvReferenceQualifier ) {

        if ( reference == null )
            throw new NullPointerException( "valid ReferenceQualifier must have a non-null reference!" );
        if ( cvReferenceQualifier == null )
            throw new NullPointerException( "valid ReferenceQualifier must have a non-null cvReferenceQualifier!" );

        this.reference = reference;
        this.cvReferenceQualifier = cvReferenceQualifier;
    }

    ///////////////////////////////////////
    // access methods for associations

    public Reference getReference() {
        return reference;
    }

    public void setReference( Reference reference ) {
        this.reference = reference;
    }

    public CvReferenceQualifier getCvReferenceQualifier() {
        return cvReferenceQualifier;
    }

    public void setCvReferenceQualifier( CvReferenceQualifier cvReferenceQualifier ) {
        this.cvReferenceQualifier = cvReferenceQualifier;
    }


    /**
     * Equality for ReferenceQualifiers is currently based on equality for
     * <code>CvReferenceQualifiers</code> and <code>References</code>.
     *
     * @param o The object to check
     *
     * @return true if the parameter equals this object, false otherwise
     *
     * @see uk.ac.ebi.intact.model.Reference
     * @see uk.ac.ebi.intact.model.CvReferenceQualifier
     */
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof ReferenceQualifier ) ) return false;

        final ReferenceQualifier referenceQualifier = ( ReferenceQualifier ) o;

        //TODO Needs to be more readable later (auto-generated)
        if ( cvReferenceQualifier != null ? !cvReferenceQualifier.equals( referenceQualifier.cvReferenceQualifier ) : referenceQualifier.cvReferenceQualifier != null )
            return false;
        if ( reference != null ? !reference.equals( referenceQualifier.reference ) : referenceQualifier.reference != null )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;

        //TODO Needs to be more readable later (auto-generated)
        result = ( reference != null ? reference.hashCode() : 0 );
        result = 29 * result + ( cvReferenceQualifier != null ? cvReferenceQualifier.hashCode() : 0 );
        return result;
    }

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    public String getReferenceAc() {
        return referenceAc;
    }

    public void setReferenceAc( String ac ) {
        this.referenceAc = ac;
    }

    public String getCvReferenceQualifierAc() {
        return cvReferenceQualifierAc;
    }

    public void setCvReferenceQualifierAc( String ac ) {
        this.cvReferenceQualifierAc = ac;
    }

} // end ReferenceQualifier




