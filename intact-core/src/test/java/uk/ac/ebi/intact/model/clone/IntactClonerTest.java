package uk.ac.ebi.intact.model.clone;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.visitor.BaseIntactVisitor;
import uk.ac.ebi.intact.model.visitor.DefaultTraverser;

import java.util.Collection;
import java.util.ArrayList;

/**
 * IntactCloner Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.7.2
 */
public class IntactClonerTest extends IntactBasicTestCase {

    IntactCloner cloner;

    @Before
    public void init() {
        cloner = new IntactCloner();
    }

    private void clone( IntactObject io ) throws IntactClonerException {
        final IntactObject clone = cloner.clone( io );
        Assert.assertNotSame( io, clone );
        Assert.assertEquals( io, clone );
    }

    @Test
    public void cloneInteraction() throws Exception {
        clone( getMockBuilder().createDeterministicInteraction() );
    }

    @Test
    public void cloneProtein() throws Exception {
        clone( getMockBuilder().createProteinRandom() );
    }

    @Test
    public void cloneEmptyExperiment() throws Exception {
        clone( getMockBuilder().createExperimentEmpty( "123456789" ) );
    }

    @Test
    public void cloneBioSource() throws Exception {
        clone( getMockBuilder().createBioSource( 9606, "human" ) );
    }

    @Test
    public void cloneXref() throws Exception {
        final Protein prot = getMockBuilder().createProteinRandom();
        clone( prot.getXrefs().iterator().next() );
    }

    @Test
    public void cloneAlias() throws Exception {
        final Protein prot = getMockBuilder().createProteinRandom();
        clone( prot.getAliases().iterator().next() );
    }

    @Test
    public void cloneAnnotation() throws Exception {
        clone( getMockBuilder().createAnnotationRandom() );
    }

    @Test
    public void cloneCvObject() throws Exception {
        clone( getMockBuilder().createCvObject( CvTopic.class, "MI:0001", "lala" ) );
    }

    @Test
    public void cloneInstitution() throws Exception {
        final Institution institution = getMockBuilder().getInstitution();
        clone( institution );
    }

    @Test
    public void clonePublication() throws Exception {
        clone( getMockBuilder().createPublication( "1" ) );
    }

    @Test
    public void cloneFeature() throws Exception {
        clone( getMockBuilder().createFeatureRandom() );
    }

    @Test
    public void cloneComponent() throws Exception {
        clone( getMockBuilder().createComponentRandom() );
    }

    @Test
    public void cloneComponent_ExperimentalRoles() throws Exception {
        CvExperimentalRole baitExperimentalRole = getMockBuilder().createCvObject( CvExperimentalRole.class, CvExperimentalRole.BAIT_PSI_REF, CvExperimentalRole.BAIT );
        CvExperimentalRole neutralExperimentalRole = getMockBuilder().createCvObject( CvExperimentalRole.class, CvExperimentalRole.NEUTRAL_PSI_REF, CvExperimentalRole.NEUTRAL );

        Collection<CvExperimentalRole> baitNeutralExperimentalRoles = new ArrayList<CvExperimentalRole>();
        baitNeutralExperimentalRoles.add( baitExperimentalRole );
        baitNeutralExperimentalRoles.add( neutralExperimentalRole );

        Component baitNeutralComponent = getMockBuilder().createComponentBait( getMockBuilder().createDeterministicProtein( "P1", "baaa" ) );
        baitNeutralComponent.setExperimentalRoles( baitNeutralExperimentalRoles );
        PersisterHelper.saveOrUpdate( baitNeutralComponent );

        final Component clonedComponent = new IntactCloner().clone( baitNeutralComponent );
        Assert.assertNotNull( clonedComponent.getExperimentalRoles() );
        Assert.assertEquals( 2, clonedComponent.getExperimentalRoles().size() );
    }


    @Test
    public void clone_cloneCvObjectTree() throws Exception {
        CvDatabase citation = getMockBuilder().createCvObject( CvDatabase.class, "MI:0444", "database citation" );
        CvDatabase psiMi = getMockBuilder().createCvObject( CvDatabase.class, CvDatabase.PSI_MI_MI_REF, CvDatabase.PSI_MI );

        citation.addChild( psiMi );

        IntactCloner cloner = new IntactCloner();
        cloner.setCloneCvObjectTree( true );

        CvDatabase citationClone = cloner.clone( citation );

        Assert.assertEquals( 1, citationClone.getChildren().size() );
    }

    @Test
    public void clone_excludeAcs() throws Exception {
        Experiment exp = getMockBuilder().createExperimentRandom( 2 );

        IntactCloner cloner = new IntactCloner();
        cloner.setExcludeACs( true );

        DefaultTraverser traverser = new DefaultTraverser();
        traverser.traverse( exp, new BaseIntactVisitor() {
            @Override
            public void visitIntactObject( IntactObject intactObject ) {
                if ( intactObject.getAc() != null ) {
                    Assert.fail( "Found an AC in " + intactObject.getClass().getSimpleName() );
                }
            }
        } );
    }

    @Test
    public void cloneConfidence() throws Exception {
        clone( getMockBuilder().createConfidenceRandom() );
    }

    @Test
    public void cloneInteractionParameter() throws Exception {
        clone( getMockBuilder().createDeterministicInteractionParameter() );
    }

    @Test
    public void cloneComponentParameter() throws Exception {
        clone( getMockBuilder().createDeterministicComponentParameter() );
    }
}
