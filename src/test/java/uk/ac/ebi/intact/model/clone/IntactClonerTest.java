package uk.ac.ebi.intact.model.clone;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persister.finder.DefaultFinder;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.visitor.BaseIntactVisitor;
import uk.ac.ebi.intact.model.visitor.DefaultTraverser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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

    private <T extends IntactObject> T clone( T io ) throws IntactClonerException {
        final T clone = cloner.clone( io );
        Assert.assertNotSame( io, clone );
        Assert.assertEquals( io, clone );

        return clone;
    }

    @Test
    public void cloneInteraction() throws Exception {
        clone( getMockBuilder().createDeterministicInteraction() );
    }

    @Test
    public void cloneInteraction_identicalComponents() throws Exception {
        Interaction interaction = getMockBuilder().createDeterministicInteraction();
        interaction.getComponents().clear();

        Assert.assertEquals(0, interaction.getComponents().size());

        final Protein prot = getMockBuilder().createProtein("P12345", "lala");
        Component a = getMockBuilder().createComponentNeutral(interaction, prot);

        interaction.getComponents().add(a);

        Assert.assertEquals(2, interaction.getComponents().size());

        Interaction clone = clone(interaction);

        Assert.assertEquals(2, clone.getComponents().size());
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

    private class EditorFinder extends DefaultFinder {

        public String findAc( AnnotatedObject annotatedObject ) {

            String ac;

            if ( annotatedObject.getAc() != null ) {
                return annotatedObject.getAc();
            }

            if ( annotatedObject instanceof Institution ) {
                ac = findAcForInstitution( ( Institution ) annotatedObject );
            } else if ( annotatedObject instanceof Publication ) {
                ac = findAcForPublication( ( Publication ) annotatedObject );
            } else if ( annotatedObject instanceof CvObject ) {
                ac = findAcForCvObject( ( CvObject ) annotatedObject );
            } else if ( annotatedObject instanceof Experiment ) {
                ac = null;
            } else if ( annotatedObject instanceof Interaction ) {
                ac = null;
            } else if ( annotatedObject instanceof Interactor ) {
                ac = findAcForInteractor( ( InteractorImpl ) annotatedObject );
            } else if ( annotatedObject instanceof BioSource ) {
                ac = findAcForBioSource( ( BioSource ) annotatedObject );
            } else if ( annotatedObject instanceof Component ) {
                ac = findAcForComponent( ( Component ) annotatedObject );
            } else if ( annotatedObject instanceof Feature ) {
                ac = findAcForFeature( ( Feature ) annotatedObject );
            } else {
                throw new IllegalArgumentException( "Cannot find Ac for type: " + annotatedObject.getClass().getName() );
            }

            return ac;
        }
    }

    @Test
    public void cloneInteractionWithMultipleFeature_multipleRanges() throws Exception {

        final Interaction interaction = getMockBuilder().createDeterministicInteraction();
        interaction.setShortLabel( "ptp61f-dock-1" );

        final Iterator<Component> iterator = interaction.getComponents().iterator();

        Component c1 = iterator.next();
        c1.setShortLabel( "c1" );
        c1.getBindingDomains().clear();
        addFeature( c1, CvFeatureType.SUFFICIENT_FOR_BINDING, CvFeatureType.SUFFICIENT_FOR_BINDING_MI_REF, 0, 0, true, "region" );
        addFeature( c1, CvFeatureType.EXPERIMENTAL_FEATURE, CvFeatureType.EXPERIMENTAL_FEATURE_MI_REF, 234, 256, false, "x" );
        addFeature( c1, CvFeatureType.EXPERIMENTAL_FEATURE, CvFeatureType.EXPERIMENTAL_FEATURE_MI_REF, 234, 256, false, "x" );

        Component c2 = iterator.next();
        c2.setShortLabel( "c2" );
        c2.getBindingDomains().clear();
        Feature f = addFeature( c2, CvFeatureType.MUTATION, CvFeatureType.MUTATION_MI_REF, 235, 235, false, "lys235thr-ser283thr" );
        final Range range = createRange(283, 283, false);
        f.addRange( range );
        addFeature( c2, CvFeatureType.MUTATION, CvFeatureType.MUTATION_MI_REF, 5632, 5632, false, "lys5632thr" );

        final Interaction clone = cloner.clone( interaction );

        for ( Component component : clone.getComponents() ) {
            if( component.getShortLabel().equals( "c1" ) ) {

                Assert.assertEquals( 2, component.getBindingDomains().size() );
                Assert.assertTrue( "Component c1 is lacking at least one feature: ?-?",
                                   hasFeature( component, "region", CvFeatureType.SUFFICIENT_FOR_BINDING, 0, 0 )
                                    );

                Assert.assertTrue( "Component c1 is lacking at least one feature: 234-256",
                                   hasFeature( component, "x", CvFeatureType.EXPERIMENTAL_FEATURE, 234, 256 ) );

            } else if( component.getShortLabel().equals( "c2" ) ) {

                Assert.assertEquals( 2, component.getBindingDomains().size() );

                Assert.assertTrue( "Component c1 is lacking at least one feature: 235-235",
                        hasFeature( component, "lys235thr-ser283thr", CvFeatureType.MUTATION, 235, 235 ));

                Assert.assertTrue( "Component c1 is lacking at least one feature: 283-283",
                        hasFeature( component, "lys235thr-ser283thr", CvFeatureType.MUTATION, 283, 283 ) );

                Assert.assertTrue( "Component c1 is lacking at least one feature: 5632-5632",
                        hasFeature( component, "lys5632thr", CvFeatureType.MUTATION, 5632, 5632 ) );
            } else {
                Assert.fail();
            }
        }

        clone.setShortLabel( "Cloned version of ptp61f-dock-1" );

        IntactContext.getCurrentInstance().getConfig().setAutoUpdateExperimentLabel(false);


//        CorePersister corePersister = new CorePersister();
//        corePersister.setFinder( new EditorFinder() );
//        getPersisterHelper().save( corePersister, clone );
        
        getPersisterHelper().save( clone );

        final List<InteractionImpl> all = getDaoFactory().getInteractionDao().getAll();
        Assert.assertEquals( 1, all.size() );
        Interaction reloaded = all.iterator().next();
        Assert.assertNotNull( reloaded );

        for ( Component component : reloaded.getComponents() ) {
            if( component.getShortLabel().equals( "c1" ) ) {

                Assert.assertEquals( 2, component.getBindingDomains().size() );

                Assert.assertTrue( "Component c1 is lacking at least one feature: ?-?",
                                   hasFeature( component, "region", CvFeatureType.SUFFICIENT_FOR_BINDING, 0, 0 ));

                Assert.assertTrue( "Component c1 is lacking at least one feature: 234-256",
                                   hasFeature( component, "x", CvFeatureType.EXPERIMENTAL_FEATURE, 234, 256 ) );

            } else if( component.getShortLabel().equals( "c2" ) ) {

                Assert.assertEquals( 2, component.getBindingDomains().size() );

                Assert.assertTrue( "Component c1 is lacking at least one feature: 235-235",
                        hasFeature( component, "lys235thr-ser283thr", CvFeatureType.MUTATION, 235, 235 ));

                Assert.assertTrue( "Component c1 is lacking at least one feature: 283-283",
                        hasFeature( component, "lys235thr-ser283thr", CvFeatureType.MUTATION, 283, 283 ) );

                Assert.assertTrue( "Component c1 is lacking at least one feature: 5632-5632",
                        hasFeature( component, "lys5632thr", CvFeatureType.MUTATION, 5632, 5632 ) );
            } else {
                Assert.fail();
            }
        }
    }

    @Test
    public void cloneInteractionWithMultipleFeature_linked() throws Exception {

        final Interaction interaction = getMockBuilder().createDeterministicInteraction();
        interaction.setShortLabel( "ptp61f-dock-1" );

        Assert.assertEquals( 2, interaction.getComponents().size() );
        final Iterator<Component> iterator = interaction.getComponents().iterator();

        Component c1 = iterator.next();
        c1.setShortLabel( "c1" );
        c1.getBindingDomains().clear();
        Feature f1 = addFeature( c1, CvFeatureType.EXPERIMENTAL_FEATURE, CvFeatureType.EXPERIMENTAL_FEATURE_MI_REF, 234, 256, false, "x" );

        Component c2 = iterator.next();
        c2.setShortLabel( "c2" );
        c2.getBindingDomains().clear();
        Feature f2 = addFeature( c2, CvFeatureType.MUTATION, CvFeatureType.MUTATION_MI_REF, 235, 235, false, "lys235thr-ser283thr" );
        final Range range = createRange(283, 283, false);
        f2.addRange( range );

        // Linking the features
        f1.setBoundDomain( f2 );
        f2.setBoundDomain( f1 );
        
        final Interaction clone = cloner.clone( interaction );
        Assert.assertEquals( 2, clone.getComponents().size() );

        for ( Component component : clone.getComponents() ) {
            if( component.getShortLabel().equals( "c1" ) ) {

                Assert.assertEquals( 1, component.getBindingDomains().size() );

                Assert.assertTrue( "Component c1 is lacking at least one feature: 234-256",
                                   hasFeature( component, "x", CvFeatureType.EXPERIMENTAL_FEATURE, 234, 256 ) );

            } else if( component.getShortLabel().equals( "c2" ) ) {

                Assert.assertEquals( 1, component.getBindingDomains().size() );

                Assert.assertTrue( "Component c1 is lacking at least one feature: 235-235",
                        hasFeature( component, "lys235thr-ser283thr", CvFeatureType.MUTATION, 235, 235 ));

            } else {
                Assert.fail( "Expected to find a component with label c1 or c2, instead found: " + component.getShortLabel() );
            }
        }

        clone.setShortLabel( "Cloned version of ptp61f-dock-1" );

        IntactContext.getCurrentInstance().getConfig().setAutoUpdateExperimentLabel(false);

        getPersisterHelper().save( clone );

        final List<InteractionImpl> all = getDaoFactory().getInteractionDao().getAll();
        Assert.assertEquals( 1, all.size() );
        Interaction reloaded = all.iterator().next();
        Assert.assertNotNull( reloaded );

        for ( Component component : reloaded.getComponents() ) {
            if( component.getShortLabel().equals( "c1" ) ) {

                Assert.assertEquals( 1, component.getBindingDomains().size() );

                Assert.assertTrue( "Component c1 is lacking at least one feature: 234-256",
                                   hasFeature( component, "x", CvFeatureType.EXPERIMENTAL_FEATURE, 234, 256 ) );

            } else if( component.getShortLabel().equals( "c2" ) ) {

                Assert.assertEquals( 1, component.getBindingDomains().size() );

                Assert.assertTrue( "Component c1 is lacking at least one feature: 235-235",
                        hasFeature( component, "lys235thr-ser283thr", CvFeatureType.MUTATION, 235, 235 ));

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
                    boolean foundRange = false;
                    for (Range range : feature.getRanges()) {
                        if( range.getFromIntervalStart() == start && range.getToIntervalStart() == stop ) {
                            foundRange = true;
                        }
                    }
                    if( ! foundRange ){
                        System.out.println("Failed on range" );
                    } else {
                        return true;
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

    private Feature addFeature( Component component, String type, String typeMi, int start, int stop, boolean undertermined, String shortlabel ) {
        CvFeatureType featureType = getMockBuilder().createCvObject( CvFeatureType.class, typeMi, type );
        Feature feature = getMockBuilder().createFeature( shortlabel, featureType );
        feature.setComponent(null);

        Range range = createRange( start, stop, undertermined );
        feature.addRange( range );
        component.getBindingDomains().add( feature );
        return feature;
    }

    private Range createRange( int start, int stop, boolean undertermined ) {
        Range range = getMockBuilder().createRange( start, start, stop, stop );
        range.setUndetermined( undertermined );
        if( undertermined ) {
            final CvFuzzyType fuzzy = getMockBuilder().createCvObject(CvFuzzyType.class, CvFuzzyType.UNDETERMINED, CvFuzzyType.UNDETERMINED_MI_REF);
            range.setFromCvFuzzyType( fuzzy );
            range.setToCvFuzzyType( fuzzy );
        }
        range.setLinked( false );
        return range;
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
        getPersisterHelper().save( baitNeutralComponent );

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

    private void assertRespectEqualsContract( Object o1, Object o2 ) {
        Assert.assertTrue( o1.equals( o1 ) );
        Assert.assertTrue( o1.equals( o2 ) );
        Assert.assertTrue( o2.equals( o1 ) );
    }

    private void assertRespectHashCodeContract( Object o1, Object o2 ) {
        Assert.assertEquals( o1.hashCode(), o1.hashCode() );
        Assert.assertEquals( o1.hashCode(), o2.hashCode() );
    }

    @Test
    public void clone_equality_experiment() throws Exception {
        final Experiment e = getMockBuilder().createDeterministicExperiment();
        final Experiment clone = new IntactCloner().clone(e);
        assertRespectEqualsContract( e, clone);
    }

    @Test
    public void clone_equality_interaction() throws Exception {
        final Interaction i = getMockBuilder().createDeterministicInteraction();
        final Interaction clone = new IntactCloner().clone(i);
        assertRespectEqualsContract( i, clone);
        assertRespectHashCodeContract( i, clone);
    }

    @Test
    public void clone_equality_Component() throws Exception {
        final Component c = getMockBuilder().createDeterministicInteraction().getComponents().iterator().next();
        final Component clone = new IntactCloner().clone(c);
        assertRespectEqualsContract( c, clone);
        assertRespectHashCodeContract( c, clone);
    }

    @Test
    public void clone_equality_Feature_noRange() throws Exception {
        final Feature f = getMockBuilder().createFeatureRandom();
        f.getRanges().clear();
        final Feature clone = new IntactCloner().clone(f);
        assertRespectEqualsContract( f, clone);
        assertRespectHashCodeContract( f, clone);
    }

    @Test
    public void clone_equality_Feature_withRange() throws Exception {
        final Feature f = getMockBuilder().createFeatureRandom();
        f.getRanges().clear();
        f.addRange( getMockBuilder().createRangeCTerminal() );
        final Feature clone = new IntactCloner().clone(f);
        assertRespectEqualsContract( f, clone);
        assertRespectHashCodeContract( f, clone);
    }

    @Test
    public void clone_equality_RangeCTerminus() throws Exception {
        final Range r = getMockBuilder().createRangeCTerminal();
        final Range clone = new IntactCloner().clone(r);
        assertRespectEqualsContract( r, clone);
        assertRespectHashCodeContract( r, clone);
    }

    @Test
    public void clone_equality_RangeUndertermined() throws Exception {
        final Range r = getMockBuilder().createRangeUndetermined();
        final Range clone = new IntactCloner().clone(r);
        assertRespectEqualsContract( r, clone);
        assertRespectHashCodeContract( r, clone);
    }

    @Test
    public void clone_equality_Biosource() throws Exception {
        final BioSource b = getMockBuilder().createBioSource( 9606, "human" );
        final BioSource clone = new IntactCloner().clone(b);
        assertRespectEqualsContract( b, clone);
        assertRespectHashCodeContract( b, clone);
    }

    @Test
    public void clone_equality_cv() throws Exception {
        final CvXrefQualifier cv = getMockBuilder().createCvObject( CvXrefQualifier.class, "MI:xxxx", "test" );
        final CvXrefQualifier clone = new IntactCloner().clone(cv);
        assertRespectEqualsContract( cv, clone);
        assertRespectHashCodeContract( cv, clone);
    }

    @Test
    public void clone_equality_parameter() throws Exception {
        final Parameter p = getMockBuilder().createDeterministicInteractionParameter();
        final Parameter clone = new IntactCloner().clone(p);
        assertRespectEqualsContract( p, clone);
        assertRespectHashCodeContract( p, clone);
    }
}
