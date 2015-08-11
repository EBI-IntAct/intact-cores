package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Feature;
import psidev.psi.mi.jami.utils.XrefUtils;
import uk.ac.ebi.intact.jami.IntactTestUtils;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactFeature;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledFeature;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Unit test for FeatureSynchronizerTemplate
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/02/14</pre>
 */
public class FeatureSynchronizerTemplateTest extends AbstractDbSynchronizerTest<Feature, AbstractIntactFeature>{

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_all() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_deleted() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        delete();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_find() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        find_local_cache(true);
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_properties() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        synchronizeProperties();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_not_persist() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        synchronize_not_persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_persist() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        synchronize_persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_merge() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        merge_test1();

    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_merge2() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        merge_test2();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_jami() throws PersisterException, FinderException, SynchronizerException {

        persist_jami();
    }

    @Override
    protected void testDeleteOtherProperties(AbstractIntactFeature objectToTest) {
        Assert.assertTrue(objectToTest.getLinkedFeatures().isEmpty());
    }

    @Override
    protected Feature createDefaultJamiObject() {
        Feature f = IntactTestUtils.createBasicModelledFeature(Feature.BIOLOGICAL_FEATURE, Feature.BIOLOGICAL_FEATURE_MI);
        try {
            this.synchronizer.synchronize(f.getLinkedFeatures().iterator().next(), true);
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
    protected void testUpdatedPropertiesAfterMerge(AbstractIntactFeature objectToTest, AbstractIntactFeature newObjToTest) {
        Assert.assertEquals(objectToTest.getAc(), newObjToTest.getAc());
        Assert.assertEquals("interaction effect 2", newObjToTest.getRole().getShortName());
        Assert.assertEquals("updated feature", newObjToTest.getShortName());
    }

    @Override
    protected void updatePropertieDetachedInstance(AbstractIntactFeature objectToTest) {
         objectToTest.setRole(IntactUtils.createMITopic("interaction effect 2", null));
         objectToTest.setShortName("updated feature");
    }

    @Override
    protected AbstractIntactFeature findObject(AbstractIntactFeature objectToTest) {
        return entityManager.find(IntactModelledFeature.class, objectToTest.getAc());
    }

    @Override
    protected void initSynchronizer() {
        this.synchronizer = new FeatureSynchronizerTemplate(this.context, IntactModelledFeature.class);
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
        Assert.assertEquals("interaction effect", persistedObject.getRole().getShortName());
        Assert.assertNotNull(((IntactCvTerm) persistedObject.getRole()).getAc());
        Assert.assertEquals(1, persistedObject.getIdentifiers().size());
        Assert.assertNotNull(XrefUtils.collectFirstIdentifierWithDatabase(persistedObject.getIdentifiers(), null, "intact"));

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
    protected void testNonPersistedProperties(AbstractIntactFeature objectToTest) {
        Assert.assertNull(objectToTest.getAc());
        Assert.assertNotNull(objectToTest.getType());
        Assert.assertNotNull(((IntactCvTerm)objectToTest.getType()).getAc());
        Assert.assertEquals("test feature", objectToTest.getShortName());
        Assert.assertEquals("full test feature", objectToTest.getFullName());
        Assert.assertNotNull(objectToTest.getBinds());
        Assert.assertTrue(objectToTest.getBinds() == objectToTest.getLinkedFeatures().iterator().next());
        Assert.assertTrue(objectToTest.getDbLinkedFeatures().isEmpty());
        Assert.assertEquals(1, objectToTest.getLinkedFeatures().size());
        Assert.assertEquals("interaction effect", objectToTest.getRole().getShortName());
        Assert.assertNotNull(((IntactCvTerm)objectToTest.getRole()).getAc());
        Assert.assertTrue(objectToTest.getIdentifiers().isEmpty());

        AbstractIntactFeature feature2 = (AbstractIntactFeature)objectToTest.getLinkedFeatures().iterator().next();
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
        AbstractIntactFeature feature = IntactTestUtils.createIntactFeature(IntactModelledFeature.class, Feature.BIOLOGICAL_FEATURE, Feature.BIOLOGICAL_FEATURE_MI);
        try {
            this.synchronizer.persist((AbstractIntactFeature)feature.getLinkedFeatures().iterator().next());
        } catch (FinderException e) {
            e.printStackTrace();
        } catch (PersisterException e) {
            e.printStackTrace();
        } catch (SynchronizerException e) {
            e.printStackTrace();
        }
        return feature;
    }
}
