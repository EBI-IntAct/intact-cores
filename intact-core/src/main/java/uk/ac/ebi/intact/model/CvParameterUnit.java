/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.annotation.EditorTopic;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


/**
 * Controlled vocabulary for kinetic constant units.
 *
 * @author Bruno Aranda
 * @version $Id: CvParameterUnit.java 7540 2007-02-19 10:30:57Z skerrien $
 *
 * @since 1.8.0
 */
@Entity
@DiscriminatorValue( "uk.ac.ebi.intact.model.CvParameterUnit" )
@EditorTopic
public class CvParameterUnit extends CvDagObject {

    public CvParameterUnit() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvCvParameterUnit instance. Requires at least a shortLabel and an
     * owner to be specified.
     *
     * @param shortLabel The memorable label to identify this CvEvidenceType
     * @param owner      The Institution which owns this CvEvidenceType
     *
     * @throws NullPointerException thrown if either parameters are not specified
     */
    public CvParameterUnit( Institution owner, String shortLabel ) {

        //super call sets up a valid CvObject
        super( owner, shortLabel );
    }

}