/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */

package uk.ac.ebi.intact.modelt.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.InteractionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for <code>InteractionUtilsTest</code>
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id:InteractionUtilsTest.java 5965 2006-08-23 14:35:46 +0100 (Wed, 23 Aug 2006) baranda $
 * @since 08/22/2006
 */
public class InteractionUtilsTest extends TestCase {

    public InteractionUtilsTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testIsBinaryInteraction() throws Exception {
        assertTrue( InteractionUtils.isBinaryInteraction( createBinaryInteraction() ) );
        assertTrue( InteractionUtils.isBinaryInteraction( createSelfBinaryInteraction() ) );
    }

    public void testIsSelfInteraction() throws Exception {
        assertTrue( InteractionUtils.isSelfBinaryInteraction( createSelfBinaryInteraction() ) );
        assertFalse( InteractionUtils.isSelfBinaryInteraction( createBinaryInteraction() ) );
    }

    public void testIsSelfInteraction2() throws Exception {
        assertTrue( InteractionUtils.isSelfBinaryInteraction( createSelfInteraction2() ) );
    }

    public void testContainsNonProteinInteractors() throws Exception {
        assertTrue( InteractionUtils.containsNonProteinInteractors( createBinaryInteractionWithNonProtein() ) );
        assertFalse( InteractionUtils.containsNonProteinInteractors( createBinaryInteraction() ) );
    }

    public void testIsUnaryInteraction() {

        // true unary
        assertTrue( InteractionUtils.isUnaryInteraction( createUnaryInteraction( 0f ) ) );
        assertTrue( InteractionUtils.isUnaryInteraction( createUnaryInteraction( 0.5f ) ) );
        assertTrue( InteractionUtils.isUnaryInteraction( createUnaryInteraction( 1.0f ) ) );

        // false unary
        assertFalse( InteractionUtils.isUnaryInteraction( createUnaryInteraction( 1.0001f ) ) );
        assertFalse( InteractionUtils.isUnaryInteraction( createUnaryInteraction( 1.5f ) ) );
        assertFalse( InteractionUtils.isUnaryInteraction( createUnaryInteraction( 99f ) ) );

        // true binary and self
        assertFalse( InteractionUtils.isUnaryInteraction( createBinaryInteraction() ) );
        assertFalse( InteractionUtils.isUnaryInteraction( createBinaryInteractionWithNonProtein() ) );
        assertFalse( InteractionUtils.isUnaryInteraction( createSelfBinaryInteraction() ) );
        assertFalse( InteractionUtils.isUnaryInteraction( createSelfInteraction2() ) );
    }

    public static Test suite() {
        return new TestSuite( InteractionUtilsTest.class );
    }

    private InteractionImpl createBinaryInteraction() {
        List<Component> comps = new ArrayList<Component>();
        comps.add( createProteinComponent( "EBI-1" ) );
        comps.add( createProteinComponent( "EBI-2" ) );

        return new InteractionImpl( new ArrayList(), comps, null, null, "Int-" + System.currentTimeMillis(), null );
    }

    private InteractionImpl createBinaryInteractionWithNonProtein() {
        List<Component> comps = new ArrayList<Component>();
        comps.add( createProteinComponent( "EBI-1" ) );
        comps.add( createNonProteinComponent() );

        return new InteractionImpl( new ArrayList(), comps, null, null, "Int-" + System.currentTimeMillis(), null );
    }

    private InteractionImpl createSelfBinaryInteraction() {
        List<Component> comps = new ArrayList<Component>();

        Component c = createProteinComponent( "EBI-1" );
        c.setStoichiometry( 2.0f );
        CvExperimentalRole role = new CvExperimentalRole();
        role.setShortLabel( CvExperimentalRole.SELF );
        role.addXref( new CvObjectXref( null, null, CvExperimentalRole.SELF_PSI_REF, null ) );
        c.setCvExperimentalRole( role );
        comps.add( c );

        return new InteractionImpl( new ArrayList(), comps, null, null, "Int-" + System.currentTimeMillis(), null );
    }

    private InteractionImpl createSelfInteraction2() {
        List<Component> comps = new ArrayList<Component>();

        Component c = createProteinComponent( "EBI-1" );
        CvExperimentalRole role = new CvExperimentalRole();
        //role.setShortLabel(CvComponentRole.SELF);
        //role.addXref(new CvObjectXref(null, new CvDatabase(), CvComponentRole.SELF_PSI_REF, new CvXrefQualifier()));
        c.setCvExperimentalRole( role );
        comps.add( c );
        comps.add( c );

        return new InteractionImpl( new ArrayList(), comps, null, null, "Int-" + System.currentTimeMillis(), null );
    }

    private Component createProteinComponent( String ac ) {
        ProteinImpl prot = new ProteinImpl( null, null, "Prot-" + System.currentTimeMillis(), null );
        prot.setAc( ac );
        Component c = new Component( null, new InteractionImpl(), prot, new CvExperimentalRole(), new CvBiologicalRole() );
        c.setInteractorAc( ac );

        return c;
    }

    private Component createNonProteinComponent() {
        SmallMoleculeImpl sm = new SmallMoleculeImpl( "SmallMol-" + System.currentTimeMillis(), null, null );
        return new Component( null, new InteractionImpl(), sm, new CvExperimentalRole(), new CvBiologicalRole( ) );
    }

    private InteractionImpl createUnaryInteraction( float stoichiometry ) {
        List<Component> comps = new ArrayList<Component>();
        InteractionImpl interaction = new InteractionImpl( new ArrayList(), comps, null, null, "Int-" + System.currentTimeMillis(), null );

        Component c = createUnaryComponent( stoichiometry, interaction );
        interaction.addComponent( c );

        return interaction;
    }

    private Component createUnaryComponent( float stoichiometry, Interaction interaction ) {
        SmallMoleculeImpl sm = new SmallMoleculeImpl( "SmallMol-" + System.currentTimeMillis(), null, null );
        Component component = new Component( null, interaction, sm, new CvExperimentalRole(), new CvBiologicalRole( ) );
        component.setStoichiometry( stoichiometry );
        return component;
    }
}
