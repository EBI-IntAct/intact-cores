package uk.ac.ebi.intact.core.unit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.model.util.XrefUtils;

/**
 * IntactMockBuilder tester.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactMockBuilderTest {
    private IntactMockBuilder mockBuilder;

    @Before
    public void before() {
        mockBuilder = new IntactMockBuilder();
    }

    @After
    public void after() {
        mockBuilder = null;
    }

    @Test
    public void createInstitution() throws Exception {
        Institution institution = mockBuilder.createInstitution( Institution.MINT_REF, Institution.MINT );

        InstitutionXref xref = XrefUtils.getPsiMiIdentityXref( institution );
        String primaryId = xref.getPrimaryId();

        Assert.assertEquals( Institution.MINT_REF, primaryId );

        Assert.assertNotNull( xref.getOwner() );
        Assert.assertNotNull( xref.getCvDatabase().getOwner() );
        Assert.assertNotNull( xref.getCvXrefQualifier().getOwner() );
    }

    @Test
    public void randomString_default() throws Exception {
        String randomString = mockBuilder.randomString( 10 );
        Assert.assertNotNull( randomString );
        Assert.assertEquals( 10, randomString.length() );
    }

    @Test
    public void createInteractionRandomBinary() throws Exception {
        Interaction interaction = mockBuilder.createInteractionRandomBinary();

        Assert.assertNotNull( interaction );
        Assert.assertNotNull( interaction.getShortLabel() );
        Assert.assertEquals( 2, interaction.getComponents().size() );
    }

    @Test
    public void createProteinRandom() throws Exception {
        Protein protein = mockBuilder.createProteinRandom();

        Assert.assertNotNull( protein );

        Assert.assertNotNull( protein.getCvInteractorType() );
        Assert.assertTrue( CvObjectUtils.hasIdentity( protein.getCvInteractorType(), CvInteractorType.PROTEIN_MI_REF ) );

        Assert.assertFalse( protein.getXrefs().isEmpty() );
        Assert.assertEquals( 1, protein.getXrefs().size() );
        final Xref xref = protein.getXrefs().iterator().next();
        Assert.assertTrue( CvObjectUtils.hasIdentity( xref.getCvDatabase(), CvDatabase.UNIPROT_MI_REF ) );

        Assert.assertNotNull( protein.getSequence() );
        Assert.assertFalse( protein.getXrefs().isEmpty() );
        Assert.assertNotNull( protein.getCrc64() );
    }

    @Test
    public void createBioSourceRandom() throws Exception {
        BioSource bioSource = mockBuilder.createBioSourceRandom();

        Assert.assertNotNull( bioSource );

        String taxId = bioSource.getTaxId();

        Assert.assertNotNull( taxId );
        Assert.assertFalse( bioSource.getXrefs().isEmpty() );
        final BioSourceXref xref = bioSource.getXrefs().iterator().next();
        Assert.assertEquals( taxId, xref.getPrimaryId() );
        Assert.assertTrue( CvObjectUtils.hasIdentity( xref.getCvDatabase(), CvDatabase.NEWT_MI_REF ) );
    }

    @Test
    public void createComponentRandom() throws Exception {
        Component component = mockBuilder.createComponentRandom();

        Assert.assertNotNull( component.getCvExperimentalRole() );
        Assert.assertNotNull( component.getCvBiologicalRole() );
        Assert.assertFalse( component.getParticipantDetectionMethods().isEmpty() );
        Assert.assertFalse( component.getExperimentalPreparations().isEmpty() );

        for ( CvExperimentalPreparation experimentalPrep : component.getExperimentalPreparations() ) {
            Assert.assertNotNull( experimentalPrep.getOwner() );
        }

    }

    @Test
    public void createSmallMoleculeRandom() {
        SmallMolecule sm = mockBuilder.createSmallMoleculeRandom();

        Assert.assertNotNull( sm );

        Assert.assertNotNull( sm.getCvInteractorType() );
        Assert.assertTrue( CvObjectUtils.hasIdentity( sm.getCvInteractorType(), CvInteractorType.SMALL_MOLECULE_MI_REF ) );

        Assert.assertFalse( sm.getXrefs().isEmpty() );
        Assert.assertEquals( 1, sm.getXrefs().size() );
        final Xref xref = sm.getXrefs().iterator().next();
        Assert.assertTrue( CvObjectUtils.hasIdentity( xref.getCvDatabase(), CvDatabase.CHEBI_MI_REF ) );

        Assert.assertFalse( sm.getAliases().isEmpty() );
        Assert.assertEquals( sm.getShortLabel().toUpperCase(), sm.getAliases().iterator().next().getName() );
    }

    @Test
    public void createComponentBait() {
        SmallMolecule sm = mockBuilder.createSmallMoleculeRandom();
        Component component = mockBuilder.createComponentBait( sm );
        Assert.assertNotNull( component );
        Assert.assertNotNull( component.getInteraction() );
        Assert.assertEquals( 1, component.getInteraction().getComponents().size() );
    }

    @Test
    public void createComponentPrey() {
        SmallMolecule sm = mockBuilder.createSmallMoleculeRandom();
        Component component = mockBuilder.createComponentPrey( sm );
        Assert.assertNotNull( component );
        Assert.assertNotNull( component.getInteraction() );
        Assert.assertEquals( 1, component.getInteraction().getComponents().size() );
    }
}
