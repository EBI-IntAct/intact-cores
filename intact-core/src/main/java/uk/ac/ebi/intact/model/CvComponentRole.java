/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.annotation.EditorTopic;

/**
 * Deprecated since version 1.6, where the component role was splitted in two CVs: 
 * experimental and biological roles
 *
 * @author hhe, Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
@Deprecated
public class CvComponentRole extends CvExperimentalRole {

    /**
     * Necessary for hibernate, yet set to private as it should not be used for any other purpose.
     */
    public CvComponentRole() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvComponentRole instance. Requires at least a shortLabel and an owner to be specified.
     *
     * @param shortLabel The memorable label to identify this CvComponentRole
     * @param owner      The Institution which owns this CvComponentRole
     *
     * @throws NullPointerException thrown if either parameters are not specified
     */
    public CvComponentRole( Institution owner, String shortLabel ) {

        //super call sets up a valid CvObject
        super( owner, shortLabel );
    }

}