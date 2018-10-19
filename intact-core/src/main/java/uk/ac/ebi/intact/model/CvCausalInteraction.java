/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


/**
 * The type of interaction.
 * <p/>
 * example binary interaction
 * example phosphorylation
 *
 * @author hhe
 * @version $Id$
 */
@Entity
@DiscriminatorValue( "uk.ac.ebi.intact.model.CvCausalInteraction" )
public class CvCausalInteraction extends CvDagObject {

    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     *
     * @deprecated Use the full constructor instead
     */
    @Deprecated
    public CvCausalInteraction() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvCausalInteraction instance. Requires at least a shortLabel and an
     * owner to be specified.
     *
     * @param shortLabel The memorable label to identify this CvCausalInteraction
     * @param owner      The Institution which owns this CvCausalInteraction
     *
     * @throws NullPointerException thrown if either parameters are not specified
     */
    public CvCausalInteraction(Institution owner, String shortLabel ) {

        //super call sets up a valid CvObject
        super( owner, shortLabel );
    }

} // end CvInteractionType




