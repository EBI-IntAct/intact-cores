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
 * Parameter for enzymatic or binding kinetic studies.
 *
 * @author Bruno Aranda
 * @version $Id$
 *
 * @since 1.8.0
 */
@Entity
@DiscriminatorValue( "uk.ac.ebi.intact.model.CvParameterType" )
@EditorTopic
public class CvParameterType extends CvDagObject {

    public CvParameterType() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvParameterType instance. Requires at least a shortLabel and an
     * owner to be specified.
     *
     * @param shortLabel The memorable label to identify this CvEvidenceType
     * @param owner      The Institution which owns this CvEvidenceType
     *
     * @throws NullPointerException thrown if either parameters are not specified
     */
    public CvParameterType( Institution owner, String shortLabel ) {

        //super call sets up a valid CvObject
        super( owner, shortLabel );
    }

}