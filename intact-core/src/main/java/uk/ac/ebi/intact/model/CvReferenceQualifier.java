/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


/**
 * Terms in this controlled vocabulary qualify the association between an
 * AnnotatedObject and a Reference.
 * <p/>
 * example states
 * <p/>
 * example disputes
 * example supports
 * example describes
 *
 * @author hhe
 * @version $Id$
 */
@Entity
@DiscriminatorValue( "uk.ac.ebi.intact.model.CvReferenceQualifier" )
public class CvReferenceQualifier extends CvDagObject {

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
    public CvReferenceQualifier() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvReferenceQualifier instance. Requires at least a shortLabel and an
     * owner to be specified.
     *
     * @param shortLabel The memorable label to identify this CvReferenceQualifier
     * @param owner      The Institution which owns this CvReferenceQualifier
     *
     * @throws NullPointerException thrown if either parameters are not specified
     */
    public CvReferenceQualifier( String shortLabel, Institution owner ) {

        //super call sets up a valid CvObject
        super( owner, shortLabel );
    }

} // end CvReferenceQualifier




