/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


/**
 * TODO comments
 *
 * @author hhe
 * @version $Id$
 */
@Entity
@DiscriminatorValue( "uk.ac.ebi.intact.model.CvFeatureType" )
public class CvFeatureType extends CvDagObject {

    public static final String SUFFICIENT_FOR_BINDING = "sufficient for binding";
    public static final String SUFFICIENT_FOR_BINDING_MI_REF = "MI:0442";

    public static final String EXPERIMENTAL_FEATURE = "experimental feature";
    public static final String EXPERIMENTAL_FEATURE_MI_REF = "MI:0505";

    public static final String MUTATION_DECREASING = "mutation decreasing";
    public static final String MUTATION_DECREASING_MI_REF = "MI:0119";

    public static final String MUTATION_INCREASING = "mutation increasing";
    public static final String MUTATION_INCREASING_MI_REF = "MI:0382";

    public static final String MUTATION = "mutation";
    public static final String MUTATION_MI_REF = "MI:0118";

    public static final String MUTATION_DISRUPTING = "mutation";
    public static final String MUTATION_DISRUPTING_MI_REF = "MI:0573";


    

    /**
     * Cache a Vector of all shortLabels of the class, e.g. for menus. This should not be here as it has no model
     * functionality but is related to eg user interfaces.
     * <p/>
     * This constructor should <b>not</b> be used as it could result in objects with invalid state. It is here for
     * object mapping purposes only and if possible will be made private.
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
    public CvFeatureType() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvFeatureType instance. Requires at least a shortLabel and an owner to be specified.
     *
     * @param shortLabel The memorable label to identify this CvFeatureType
     * @param owner      The Institution which owns this CvFeatureType
     *
     * @throws NullPointerException thrown if either parameters are not specified
     */
    public CvFeatureType( Institution owner, String shortLabel ) {

        //super call sets up a valid CvObject
        super( owner, shortLabel );
    }

} // end CvFeatureType




