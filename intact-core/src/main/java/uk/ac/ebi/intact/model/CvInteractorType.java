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

    /**
     * The MI number for an Interaction.
     */
    private static final String ourInteractionMI = "MI:0317";

    /**
     * The MI number for a Protein.
     */
    private static final String ourProteinMI = "MI:0326";

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
    /**
     * A list of MI numbers for Nucleic acid type
     */
    private static final List<String> ourNucleicAcidMIs = Arrays.asList( new String[]{
            "MI:0318", "MI:0319", "MI:0320", "MI:0321", "MI:0322", "MI:0323",
            "MI:0324", "MI:0325", "MI:0607", "MI:0608", "MI:0609", "MI:0610", "MI:0611", "MI:0679", "MI:0680", "MI:0681"} );

    /**
     * A list of MI numbers for a Protein type.
     */
    private static final List<String> ourProteinMIs = Arrays.asList( new String[]{
            ourProteinMI, "MI:0327"} );

    /**
     * @param mi the MI number to check
     *
     * @return true if given MI number belongs to a Protein menu item; false is
     *         returned for all other instances.
     */
    public static boolean isProteinMI( String mi ) {
        return ourProteinMIs.contains( mi );
    }

    /**
     * @param mi the MI number to check
     *
     * @return true if given MI number belongs to a NucleicAcid menu item; false is
     *         returned for all other instances.
     */
    public static boolean isNucleicAcidMI( String mi ) {
        return ourNucleicAcidMIs.contains( mi );
    }

    /**
     * @return returns an unmodifiable list consists of NucleicAcid MIs as strings.
     */
    public static List<String> getNucleicAcidMIs() {
        return Collections.unmodifiableList( ourNucleicAcidMIs );
    }

    /**
     * @return returns the default MI for a Protein as a string.
     */
    public static String getProteinMI() {
        return ourProteinMI;
    }

    /**
     * @return returns an unmodifiable list consists of Protein MIs as strings.
     */
    public static List<String> getProteinMIs() {
        return Collections.unmodifiableList( ourProteinMIs );
    }

    /**
     * @return the MI number for an Interaction as a String.
     */
    public static String getInteractionMI() {
        return ourInteractionMI;
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
