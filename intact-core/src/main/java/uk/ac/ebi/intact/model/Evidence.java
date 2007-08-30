/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

/**
 * TODO comments
 * TODO what to do with this ?
 *
 * @author hhe
 * @version $Id$
 */
@Deprecated
public class Evidence {

    ///////////////////////////////////////
    //attributes

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    protected String cvEvidenceTypeAc;

    /**
     * TODO comments
     */
    private String parameters;

    ///////////////////////////////////////
    // associations

    /**
     * TODO comments
     */
    private CvEvidenceType cvEvidenceType;

    ///////////////////////////////////////
    //access methods for attributes

    public String getParameters() {
        return parameters;
    }

    public void setParameters( String parameters ) {
        this.parameters = parameters;
    }

    ///////////////////////////////////////
    // access methods for associations

    public CvEvidenceType getCvEvidenceType() {
        return cvEvidenceType;
    }

    public void setCvEvidenceType( CvEvidenceType cvEvidenceType ) {
        this.cvEvidenceType = cvEvidenceType;
    }

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    public String getCvEvidenceTypeAc() {
        return this.cvEvidenceTypeAc;
    }

    public void setCvEvidenceTypeAc( String ac ) {
        this.cvEvidenceTypeAc = ac;
    }

} // end Evidence




