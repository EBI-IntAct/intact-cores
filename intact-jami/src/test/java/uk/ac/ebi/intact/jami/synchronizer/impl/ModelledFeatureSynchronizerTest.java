package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import psidev.psi.mi.jami.model.Feature;
import psidev.psi.mi.jami.model.ModelledFeature;
import psidev.psi.mi.jami.utils.XrefUtils;
import uk.ac.ebi.intact.jami.IntactTestUtils;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.lang.reflect.InvocationTargetException;

/**
 * Unit test for ModelledFeatureSynchronizerTemplate
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/02/14</pre>
 */
public class ModelledFeatureSynchronizerTest extends FeatureSynchronizerTemplateTest{

    @Override
    protected void testDeleteOtherProperties(AbstractIntactFeature objectToTest) {
        super.testDeleteOtherProperties(objectToTest);
        Assert.assertNull(entityManager.find(ModelledFeatureXref.class, ((ModelledFeatureXref) objectToTest.getDbXrefs().iterator().next()).getAc()));
        Assert.assertNull(entityManager.find(ModelledFeatureAlias.class, ((ModelledFeatureAlias) objectToTest.getAliases().iterator().next()).getAc()));
        Assert.assertNull(entityManager.find(ModelledFeatureAnnotation.class, ((ModelledFeatureAnnotation) objectToTest.getAnnotations().iterator().next()).getAc()));
        Assert.assertNull(entityManager.find(ModelledRange.class, ((ModelledRange) objectToTest.getRanges().iterator().next()).getAc()));
    }

    @Override
    protected Feature createDefaultJamiObject() {
        ModelledFeature f = IntactTestUtils.createFullModelledFeature(Feature.BIOLOGICAL_FEATURE, Feature.BIOLOGICAL_FEATURE_MI);
        try {
            this.synchronizer.synchronize((ModelledFeature)f.getLinkedFeatures().iterator().next(), true);
        } catch (FinderException e) {
            e.printStackTrace();
        } catch (PersisterException e) {
            e.printStackTrace();
        } catch (SynchronizerException e) {
            e.printStackTrace();
        }
        return f;
    }

    @Override
    protected AbstractIntactFeature findObject(AbstractIntactFeature objectToTest) {
        return entityManager.find(IntactModelledFeature.class, objectToTest.getAc());
    }

    @Override
    protected void initSynchronizer() {
        this.synchronizer = new ModelledFeatureSynchronizer(this.context);
    }

    @Override
    protected void testPersistedProperties(AbstractIntactFeature persistedObject) {
        Assert.assertNotNull(persistedObject.getAc());
        Assert.assertNotNull(persistedObject.getType());
        Assert.assertNotNull(((IntactCvTerm)persistedObject.getType()).getAc());
        Assert.assertEquals("test feature", persistedObject.getShortName());
        Assert.assertEquals("full test feature", persistedObject.getFullName());
        Assert.assertNotNull(persistedObject.getBinds());
        Assert.assertTrue(persistedObject.getBinds() == persistedObject.getLinkedFeatures().iterator().next());
        Assert.assertTrue(persistedObject.getDbLinkedFeatures().isEmpty());
        Assert.assertEquals(1, persistedObject.getLinkedFeatures().size());
        Assert.assertEquals("interaction dependency", persistedObject.getInteractionDependency().getShortName());
        Assert.assertEquals("interaction effect", persistedObject.getInteractionEffect().getShortName());
        Assert.assertNotNull(((IntactCvTerm) persistedObject.getInteractionDependency()).getAc());
        Assert.assertNotNull(((IntactCvTerm) persistedObject.getInteractionEffect()).getAc());
        Assert.assertEquals(1, persistedObject.getIdentifiers().size());
        Assert.assertNotNull(XrefUtils.collectFirstIdentifierWithDatabase(persistedObject.getIdentifiers(), null, "intact"));
        Assert.assertEquals(1, persistedObject.getXrefs().size());
        Assert.assertNotNull(((ModelledFeatureXref)persistedObject.getXrefs().iterator().next()).getAc());
        Assert.assertEquals(1, persistedObject.getAliases().size());
        Assert.assertNotNull(((ModelledFeatureAlias)persistedObject.getAliases().iterator().next()).getAc());
        Assert.assertEquals(1, persistedObject.getAnnotations().size());
        Assert.assertNotNull(((ModelledFeatureAnnotation)persistedObject.getAnnotations().iterator().next()).getAc());
        Assert.assertEquals(1, persistedObject.getRanges().size());
        Assert.assertNotNull(((ModelledRange)persistedObject.getRanges().iterator().next()).getAc());

        AbstractIntactFeature feature2 = (AbstractIntactFeature)persistedObject.getLinkedFeatures().iterator().next();
        Assert.assertNotNull(feature2.getAc());
        Assert.assertNotNull(feature2.getType());
        Assert.assertNotNull(((IntactCvTerm)feature2.getType()).getAc());
        Assert.assertEquals("test feature 2", feature2.getShortName());
        Assert.assertEquals("full test feature 2", feature2.getFullName());
        Assert.assertNull(feature2.getBinds());
        Assert.assertTrue(feature2.getDbLinkedFeatures().isEmpty());
    }

    @Override
    protected void testNonPersistedProperties(AbstractIntactFeature persistedObject) {
        Assert.assertNull(persistedObject.getAc());
        Assert.assertNotNull(persistedObject.getType());
        Assert.assertNotNull(((IntactCvTerm)persistedObject.getType()).getAc());
        Assert.assertEquals("test feature", persistedObject.getShortName());
        Assert.assertEquals("full test feature", persistedObject.getFullName());
        Assert.assertNotNull(persistedObject.getBinds());
        Assert.assertTrue(persistedObject.getBinds() == persistedObject.getLinkedFeatures().iterator().next());
        Assert.assertTrue(persistedObject.getDbLinkedFeatures().isEmpty());
        Assert.assertEquals(1, persistedObject.getLinkedFeatures().size());
        Assert.assertEquals("interaction dependency", persistedObject.getInteractionDependency().getShortName());
        Assert.assertEquals("interaction effect", persistedObject.getInteractionEffect().getShortName());
        Assert.assertNotNull(((IntactCvTerm) persistedObject.getInteractionDependency()).getAc());
        Assert.assertNotNull(((IntactCvTerm) persistedObject.getInteractionEffect()).getAc());
        Assert.assertEquals(0, persistedObject.getIdentifiers().size());
        Assert.assertEquals(1, persistedObject.getXrefs().size());
        Assert.assertNull(((ModelledFeatureXref)persistedObject.getXrefs().iterator().next()).getAc());
        Assert.assertEquals(1, persistedObject.getAliases().size());
        Assert.assertNull(((ModelledFeatureAlias)persistedObject.getAliases().iterator().next()).getAc());
        Assert.assertEquals(1, persistedObject.getAnnotations().size());
        Assert.assertNull(((ModelledFeatureAnnotation)persistedObject.getAnnotations().iterator().next()).getAc());
        Assert.assertEquals(1, persistedObject.getRanges().size());
        Assert.assertNull(((ModelledRange)persistedObject.getRanges().iterator().next()).getAc());

        AbstractIntactFeature feature2 = (AbstractIntactFeature)persistedObject.getLinkedFeatures().iterator().next();
        Assert.assertNotNull(feature2.getAc());
        Assert.assertNotNull(feature2.getType());
        Assert.assertNotNull(((IntactCvTerm)feature2.getType()).getAc());
        Assert.assertEquals("test feature 2", feature2.getShortName());
        Assert.assertEquals("full test feature 2", feature2.getFullName());
        Assert.assertNull(feature2.getBinds());
        Assert.assertTrue(feature2.getDbLinkedFeatures().isEmpty());
    }

    @Override
    protected AbstractIntactFeature createDefaultObject() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        AbstractIntactFeature f= IntactTestUtils.createFullModelledFeatureWithRanges(Feature.BIOLOGICAL_FEATURE, Feature.BIOLOGICAL_FEATURE_MI);
        try {
            this.synchronizer.persist((AbstractIntactFeature)f.getLinkedFeatures().iterator().next());
        } catch (FinderException e) {
            e.printStackTrace();
        } catch (PersisterException e) {
            e.printStackTrace();
        } catch (SynchronizerException e) {
            e.printStackTrace();
        }
        return f;
    }
}
