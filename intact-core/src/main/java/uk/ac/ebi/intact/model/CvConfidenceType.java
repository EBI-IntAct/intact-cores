/**
 * Copyright 2007 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.annotation.EditorTopic;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * CvObject to represent a confidence scoring method.
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version $Id$
 * @since 1.8.0
 */
@Entity
@DiscriminatorValue( "uk.ac.ebi.intact.model.CvConfidenceType" )
@EditorTopic
public class CvConfidenceType extends CvObject{
     /**
     * The constant for undetermined.
     */
//    public static final String UNDETERMINED = "undetermined";
//    public static final String UNDETERMINED_MI_REF = "MI:0339";

    public static final String INTACT_CONFIDENCE_SCORE="intact confidence";



     /**
     * This constructor should <b>not</b> be used as it could result in objects with invalid state. It is here for
     * object mapping purposes only and if possible will be made private.
     */
    public CvConfidenceType() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvConfidenceType instance. Requires at least a shortLabel and an owner to be specified.
     *
     * @param shortLabel The memorable label to identify this CvConfidence
     * @param owner      The Institution which owns this CvConfidence
     */
    public CvConfidenceType( Institution owner, String shortLabel ) {

        //super call sets up a valid CvObject
        super( owner, shortLabel );
    }
}
