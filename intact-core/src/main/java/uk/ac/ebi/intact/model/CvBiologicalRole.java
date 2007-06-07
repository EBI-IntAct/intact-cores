/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.annotation.EditorTopic;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

/**
 * The biological role of the specific substrate in the interaction.
 * <p/>
 * example enzyme, target, electron donor...
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.6
 */
@Entity
@DiscriminatorValue( "uk.ac.ebi.intact.model.CvBiologicalRole" )
@EditorTopic
public class CvBiologicalRole extends CvObject implements Editable, Searchable {

    //////////////////////
    // Constants

    public static final String UNSPECIFIED = "unspecified role";
    public static final String UNSPECIFIED_PSI_REF = "MI:0499";    

    public static final String ENZYME = "enzyme";
    public static final String ENZYME_PSI_REF = "MI:0501";

    public static final String ENZYME_TARGET = "enzyme target";
    public static final String ENZYME_TARGET_PSI_REF = "MI:0502";    

    public static final String SELF = "self";
    public static final String SELF_PSI_REF = "MI:0503";

    public static final String ELECTRON_DONOR = "electron donor";
    public static final String ELECTRON_DONOR_MI_REF = "MI:0579";

    public static final String ELECTRON_ACCEPTOR = "electron acceptor";
    public static final String ELECTRON_ACCEPTOR_MI_REF = "MI:0580";

    public static final String INHIBITOR = "inhibitor";
    public static final String INHIBITOR_MI_REF = "MI:0586";

    public static final String COFACTOR = "cofactor";
    public static final String COFACTOR_MI_REF = "MI:0682";

    public static final String STIMULATOR = "stimulator";
    public static final String STIMULATOR_MI_REF = "MI:0840";

    public static final String FLUROPHORE_DONOR = "donor fluorophore";
    public static final String FLUROPHORE_DONOR_MI_REF = "MI:0583";

    public static final String FLUROPHORE_ACCEPTOR = "acceptor fluorophore";
    public static final String FLUROPHORE_ACCEPTOR_MI_REF = "MI:0584";


    //////////////////////
    // Constructor

    /**
     * Necessary for hibernate, yet set to private as it should not be used for any other purpose.
     */
    public CvBiologicalRole() {
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
    public CvBiologicalRole( Institution owner, String shortLabel ) {

        //super call sets up a valid CvObject
        super( owner, shortLabel );
    }

}