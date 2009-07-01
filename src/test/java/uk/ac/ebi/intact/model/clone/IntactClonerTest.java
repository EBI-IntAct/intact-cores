package uk.ac.ebi.intact.model.clone;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.visitor.BaseIntactVisitor;
import uk.ac.ebi.intact.model.visitor.DefaultTraverser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * IntactCloner Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.7.2
 */
public class IntactClonerTest extends IntactBasicTestCase {

    @Autowired
    private IntactCloner cloner;

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
    public void cloneInteractionWithMultipleFeature() throws Exception {
        final Interaction interaction = getMockBuilder().createDeterministicInteraction();

        final Iterator<Component> iterator = interaction.getComponents().iterator();

        Component c1 = iterator.next();
        c1.setShortLabel( "c1" );
        c1.getBindingDomains().clear();
        addFeature( c1, CvFeatureType.MUTATION_DECREASING, CvFeatureType.MUTATION_DECREASING_MI_REF, 10, 50, false, "region" );
        addFeature( c1, CvFeatureType.EXPERIMENTAL_FEATURE, CvFeatureType.EXPERIMENTAL_FEATURE_MI_REF, 20, 25, false, "region" );

        Component c2 = iterator.next();
        c2.setShortLabel( "c2" );
        c2.getBindingDomains().clear();
        addFeature( c2, CvFeatureType.MUTATION_DECREASING, CvFeatureType.MUTATION_DECREASING_MI_REF, 10, 50, false, "region" );
        addFeature( c2, CvFeatureType.MUTATION_DISRUPTING, CvFeatureType.MUTATION_DISRUPTING_MI_REF, 10, 55, false, "region" );

        final Interaction clone = cloner.clone( interaction );
//        final Interaction clone = new InteractionIntactCloner().clone( interaction );

        for ( Component component : clone.getComponents() ) {
            if( component.getShortLabel().equals( "c1" ) ) {

                Assert.assertEquals(2, component.getBindingDomains().size());
                Assert.assertTrue( "Component c1 is lacking at least one feature",
                                   hasFeature( component, "region", CvFeatureType.MUTATION_DECREASING, 10, 50 )
                                   || hasFeature( component, "region", CvFeatureType.EXPERIMENTAL_FEATURE, 20, 25 ) );

            } else if( component.getShortLabel().equals( "c2" ) ) {

                Assert.assertEquals(2, component.getBindingDomains().size());
                Assert.assertTrue( "Component c2 is lacking at least one feature",
                                   hasFeature( component, "region", CvFeatureType.MUTATION_DECREASING, 10, 50 )
                                   || hasFeature( component, "region", CvFeatureType.MUTATION_DISRUPTING, 10, 55 ) );
            } else {
                Assert.fail();
            }
        }
    }

    @Test
    public void cloneInteractionWithMultipleFeature_underterminedRanges() throws Exception {
        final Interaction interaction = getMockBuilder().createDeterministicInteraction();

        final Iterator<Component> iterator = interaction.getComponents().iterator();

        final boolean undetermined = true;

        Component c1 = iterator.next();
        c1.setShortLabel( "c1" );
        c1.getBindingDomains().clear();
        addFeature( c1, CvFeatureType.MUTATION_DECREASING, CvFeatureType.MUTATION_DECREASING_MI_REF, 0, 0, undetermined, "region" );
        addFeature( c1, CvFeatureType.EXPERIMENTAL_FEATURE, CvFeatureType.EXPERIMENTAL_FEATURE_MI_REF, 0, 0, undetermined, "region" );

        Component c2 = iterator.next();
        c2.setShortLabel( "c2" );
        c2.getBindingDomains().clear();
        addFeature( c2, CvFeatureType.MUTATION_DECREASING, CvFeatureType.MUTATION_DECREASING_MI_REF, 0, 0, undetermined, "region" );
        addFeature( c2, CvFeatureType.MUTATION_DISRUPTING, CvFeatureType.MUTATION_DISRUPTING_MI_REF, 0, 0, undetermined, "region" );

        final Interaction clone = cloner.clone( interaction );

        for ( Component component : clone.getComponents() ) {
            if( component.getShortLabel().equals( "c1" ) ) {

                Assert.assertEquals(2, component.getBindingDomains().size());
                Assert.assertTrue( "Component c1 is lacking at least one feature",
                                   hasFeature( component, "region", CvFeatureType.MUTATION_DECREASING, 0, 0 )
                                   || hasFeature( component, "region", CvFeatureType.EXPERIMENTAL_FEATURE, 0, 0 ) );

            } else if( component.getShortLabel().equals( "c2" ) ) {

                Assert.assertEquals(2, component.getBindingDomains().size());
                Assert.assertTrue( "Component c2 is lacking at least one feature",
                                   hasFeature( component, "region", CvFeatureType.MUTATION_DECREASING, 0, 0 )
                                   || hasFeature( component, "region", CvFeatureType.MUTATION_DISRUPTING, 0, 0 ) );
            } else {
                Assert.fail();
            }
        }
    }

    private boolean hasFeature( Component component, String label, String type, int start, int stop ) {
        boolean foundByLabel = false;
        for ( Feature feature : component.getBindingDomains() ) {
            if( label.equals( feature.getShortLabel() ) ) {
                foundByLabel = true;
                if( type.equals( feature.getCvFeatureType().getShortLabel() ) ) {
                    final Range range = feature.getRanges().iterator().next();
                    if( range.getFromIntervalStart() == start && range.getToIntervalStart() == stop ) {
                        return true;
                    } else {
                        System.out.println( "Failed on range" );
                    }
                } else {
                    System.out.println( "Failed on type" );
                }
            }
        }

        if( !foundByLabel ) {
            System.out.println( "Failed on Label" );
        }

        return false;
    }

    private void addFeature( Component component, String type, String typeMi, int start, int stop, boolean undertermined, String shortlabel ) {
        CvFeatureType featureType = getMockBuilder().createCvObject( CvFeatureType.class, typeMi, type );
        Feature feature = getMockBuilder().createFeature( shortlabel, featureType );
        feature.setComponent(null);

        Range range = getMockBuilder().createRange( start, start, stop, stop );
        range.setUndetermined( undertermined );
        range.setLinked( false );
        feature.addRange(range);
        component.getBindingDomains().add( feature );
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

        cloner.setCloneCvObjectTree( true );

        CvDatabase citationClone = cloner.clone( citation );

        Assert.assertEquals( 1, citationClone.getChildren().size() );
    }

    @Test
    public void clone_excludeAcs() throws Exception {
        Experiment exp = getMockBuilder().createExperimentRandom( 2 );

        cloner.setExcludeACs( true );

        Experiment clonedExp = cloner.cloneExperiment(exp);

        DefaultTraverser traverser = new DefaultTraverser();
        traverser.traverse( clonedExp, new BaseIntactVisitor() {
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
