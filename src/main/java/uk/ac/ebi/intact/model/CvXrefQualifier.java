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
 * Terms in this controlled vocabulary class qualify the association
 * between AnnotatedObject and Xref.
 * example identical
 * example homologue
 *
 * @author hhe
 * @version $Id$
 */
@Entity
@DiscriminatorValue( "uk.ac.ebi.intact.model.CvXrefQualifier" )
@EditorTopic
public class CvXrefQualifier extends CvDagObject implements Editable {

    /////////////////////////////
    // Constants

    public static final String IDENTITY = "identity";
    public static final String IDENTITY_MI_REF = "MI:0356";

    public static final String SECONDARY_AC = "secondary-ac";
    public static final String SECONDARY_AC_MI_REF = "MI:0360";

    public static final String ISOFORM_PARENT = "isoform-parent";
    public static final String ISOFORM_PARENT_MI_REF = "MI:0243";

    public static final String PRIMARY_REFERENCE = "primary-reference";
    public static final String PRIMARY_REFERENCE_MI_REF = "MI:0358";

    public static final String SEE_ALSO = "see-also";
    public static final String SEE_ALSO_MI_REF = "MI:0361";

    public static final String GO_DEFINITION_REF = "go-definition-ref";
    public static final String GO_DEFINITION_REF_MI_REF = "MI:0242";

    public static final String TARGET_SPECIES = "target-species";

    public static final String COMPONENT = "component";
    public static final String COMPONENT_MI_REF = "MI:0354";

    public static final String FUNCTION = "function";
    public static final String FUNCTION_MI_REF = "MI:0355";

    public static final String PROCESS = "process";
    public static final String PROCESS_MI_REF = "MI:0359";

    public static final String IMEX_PRIMARY = "imex-primary";
    public static final String IMEX_PRIMARY_MI_REF = "MI:0662";

    public static final String SOURCE_REFERENCE = "source reference";
    public static final String SOURCE_REFERENCE_MI_REF = "MI:0685";

    public static final String METHOD_REFERENCE = "method reference";
    public static final String METHOD_REFERENCE_MI_REF = "MI:0357";


    /**
     * Cache a Vector of all shortLabels of the class, e.g. for menus.
     * This should not be here as it has no model functionality but is
     * related to eg user interfaces.
     * <p/>
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     */
//    protected static Vector menuList = null;

    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     *
     */
    public CvXrefQualifier() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvXrefQualifier instance. Requires at least a shortLabel and an
     * owner to be specified.
     *
     * @param shortLabel The memorable label to identify this CvXrefQualifier
     * @param owner      The Institution which owns this CvXrefQualifier
     *
     * @throws NullPointerException thrown if either parameters are not specified
     */
    public CvXrefQualifier( Institution owner, String shortLabel ) {

        //super call sets up a valid CvObject
        super( owner, shortLabel );
    }

} // end CvXrefQualifier




