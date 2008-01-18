/**
 Copyright (c) 2002-2005 The European Bioinformatics Institute, and others.
 All rights reserved. Please see the file LICENSE
 in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This describes the nature of the molecule. For example, protein, DNA etc.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
@Entity
@DiscriminatorValue( "uk.ac.ebi.intact.model.CvInteractorType" )
public class CvInteractorType extends CvDagObject {

    public static final String INTERACTION_MI_REF = "MI:0317";
    public static final String INTERACTION = "interaction";

    public static final String NUCLEIC_ACID_MI_REF = "MI:0318";
    public static final String NUCLEIC_ACID = "nucleic acid";

    public static final String DNA = "dna";
    public static final String DNA_MI_REF = " MI:0319";

    public static final String PROTEIN = "protein";
    public static final String PROTEIN_MI_REF = "MI:0326";

    public static final String SMALL_MOLECULE = "small molecule";
    public static final String SMALL_MOLECULE_MI_REF = "MI:0328";

    public static final String PEPTIDE_MI_REF = "MI:0327";

    public static final String RNA = "ribonucleic acid";
    public static final String RNA_MI_REF = "MI:0320";

    /**
     * @param mi the MI number to check
     *
     * @return true if given MI number belongs to a Protein menu item; false is
     *         returned for all other instances.
     * @deprecated Use CvObjectUtils.isProteinType() instead.
     */
    @Deprecated
    public static boolean isProteinMI( String mi ) {
        throw new UnsupportedOperationException( "DEPRECATED METHOD - please use CvObjectUtils.isProteinType() instead" );
    }

    /**
     * @param mi the MI number to check
     *
     * @return true if given MI number belongs to a NucleicAcid menu item; false is
     *         returned for all other instances.
     * @deprecated Use CvObjectUtils.isNucleicAcidType() instead.
     */
    @Deprecated
    public static boolean isNucleicAcidMI( String mi ) {
        throw new UnsupportedOperationException( "DEPRECATED METHOD - please use CvObjectUtils.isNucleicAcidType() instead" );
    }

    /**
     * @return returns an unmodifiable list consists of NucleicAcid MIs as strings.
     * @deprecated Use CvObjectUtils.isNucleicAcidType() instead.
     */
    @Deprecated
    public static List<String> getNucleicAcidMIs() {
        throw new UnsupportedOperationException( "DEPRECATED METHOD - please use CvObjectUtils.isNucleicAcidType() instead" );
    }

    /**
     * @return returns the default MI for a Protein as a string.
     */
    public static String getProteinMI() {
        return PROTEIN_MI_REF;
    }

    /**
     * @return returns an unmodifiable list consists of Protein MIs as strings.
     * @deprecated Use CvObjectUtils.getChildrenMIs() instead.
     */
    @Deprecated
    public static List<String> getProteinMIs() {
        throw new UnsupportedOperationException( "DEPRECATED METHOD - please use CvObjectUtils.getChildrenMIs() instead" );
    }

    /**
     * @return the MI number for an Interaction as a String.
     */
    public static String getInteractionMI() {
        return INTERACTION_MI_REF;
    }

    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     *
     * @deprecated Use the full constructor instead
     */
    @Deprecated
    public CvInteractorType() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvInteractorType instance. Requires at least a shortLabel and an
     * owner to be specified.
     *
     * @param shortLabel The memorable label to identify this CvInteraction
     * @param owner      The Institution which owns this CvInteraction
     *
     * @throws NullPointerException thrown if either parameters are not specified
     */
    public CvInteractorType( Institution owner, String shortLabel ) {
        //super call sets up a valid CvObject
        super( owner, shortLabel );
    }
}
