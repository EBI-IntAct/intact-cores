/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


/**
 * The role of the product.
 * example In an enzymatic reaction, one product could be the "modified protein", the other the "enzyme".
 *
 * @author hhe
 * @version $Id$
 */
@Entity
@DiscriminatorValue( "uk.ac.ebi.intact.model.CvProductRole" )
public class CvProductRole extends CvObject {

    /**
     * Cache a Vector of all shortLabels of the class, e.g. for menus.
     * This should not be here as it has no model functionality but is
     * related to eg user interfaces.
     * <p/>
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     *
     * @deprecated Use the full constructor instead
     */
//    protected static Vector menuList = null;

    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     * @deprecated Use the full constructor instead
     */
    @Deprecated
    public CvProductRole() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvProductRole instance. Requires at least a shortLabel and an
     * owner to be specified.
     *
     * @param shortLabel The memorable label to identify this CvProductRole
     * @param owner      The Institution which owns this CvProductRole
     *
     * @throws NullPointerException thrown if either parameters are not specified
     */
    public CvProductRole( Institution owner, String shortLabel ) {

        //super call sets up a valid CvObject
        super( owner, shortLabel );
    }

} // end CvProductRole




