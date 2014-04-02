package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

/**
 * Unit test for ModelledFeatureSynchronizerTemplate
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/02/14</pre>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/intact-jami-test.spring.xml"})
@Transactional
@TransactionConfiguration
@DirtiesContext
public class ModelledFeatureSynchronizerTest {

    @PersistenceContext(unitName = "intact-core")
    private EntityManager entityManager;
    @PersistenceUnit(unitName = "intact-core", name = "intactEntityManagerFactory")
    private EntityManagerFactory intactEntityManagerFactory;

    private ModelledFeatureSynchronizer synchronizer;
    private SynchronizerContext context;

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_all() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ModelledFeatureSynchronizer(this.context);

        IntactModelledFeature feature = new IntactModelledFeature("test feature", "full test feature");
        IntactModelledFeature feature2 = new IntactModelledFeature("test feature 2", "full test feature 2");
        feature.getLinkedFeatures().add(feature2);
        feature.setInterpro("IP-xxxx");
        feature.getXrefs().add(new ModelledFeatureXref(IntactUtils.createMIDatabase(Xref.ISOFORM_PARENT, Xref.ISOFORM_PARENT_MI), "EBI-xxx"));
        feature.getAliases().add(new ModelledFeatureAlias("test synonym"));
        feature.getAnnotations().add(new ModelledFeatureAnnotation(IntactUtils.createMITopic(Annotation.COMMENT, Annotation.COMMENT_MI), "test annotation"));
        feature.getRanges().add(new ModelledRange(new IntactPosition(1), new IntactPosition(2)));
        feature.setInteractionDependency(IntactUtils.createMITopic("test dependency", null));
        feature.setInteractionEffect(IntactUtils.createMITopic("test effect", null));

        this.synchronizer.persist(feature2);
        Assert.assertNotNull(feature2.getAc());
        Assert.assertNotNull(feature2.getType());
        Assert.assertNotNull(((IntactCvTerm) feature2.getType()).getAc());
        Assert.assertEquals("test feature 2", feature2.getShortName());
        Assert.assertEquals("full test feature 2", feature2.getFullName());
        Assert.assertNull(feature2.getBinds());
        Assert.assertTrue(feature2.getDbLinkedFeatures().isEmpty());
        this.synchronizer.persist(feature);
        Assert.assertNotNull(feature.getAc());
        Assert.assertNotNull(feature.getType());
        Assert.assertNotNull(((IntactCvTerm) feature.getType()).getAc());
        Assert.assertEquals("test feature", feature.getShortName());
        Assert.assertEquals("full test feature", feature.getFullName());
        Assert.assertNotNull(feature.getBinds());
        Assert.assertTrue(feature.getBinds() == feature2);
        Assert.assertTrue(feature.getDbLinkedFeatures().isEmpty());
        Assert.assertEquals(1, feature.getLinkedFeatures().size());
        Assert.assertNotNull(feature.getInteractionEffect());
        Assert.assertNotNull(((IntactCvTerm)feature.getInteractionEffect()).getAc());
        Assert.assertNotNull(feature.getInteractionDependency());
        Assert.assertNotNull(((IntactCvTerm) feature.getInteractionDependency()).getAc());
        Assert.assertEquals(2, feature.getIdentifiers().size());
        Assert.assertEquals(1, feature.getXrefs().size());
        Assert.assertEquals(1, feature.getAliases().size());
        Assert.assertEquals(1, feature.getAnnotations().size());
        Assert.assertEquals(1, feature.getRanges().size());
        this.entityManager.flush();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_deleted() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ModelledFeatureSynchronizer(this.context);

        IntactModelledFeature feature = new IntactModelledFeature("test feature", "full test feature");
        IntactModelledFeature feature2 = new IntactModelledFeature("test feature 2", "full test feature 2");
        feature.getLinkedFeatures().add(feature2);
        feature.setInterpro("IP-xxxx");
        feature.getXrefs().add(new ModelledFeatureXref(IntactUtils.createMIDatabase(Xref.ISOFORM_PARENT, Xref.ISOFORM_PARENT_MI), "EBI-xxx"));
        feature.getAliases().add(new ModelledFeatureAlias("test synonym"));
        feature.getAnnotations().add(new ModelledFeatureAnnotation(IntactUtils.createMITopic(Annotation.COMMENT, Annotation.COMMENT_MI), "test annotation"));
        feature.getRanges().add(new ModelledRange(new IntactPosition(1), new IntactPosition(2)));
        feature.setInteractionDependency(IntactUtils.createMITopic("test dependency", null));
        feature.setInteractionEffect(IntactUtils.createMITopic("test effect", null));

        this.synchronizer.persist(feature2);
        Assert.assertNotNull(feature2.getAc());
        this.synchronizer.persist(feature);
        Assert.assertNotNull(feature.getAc());
        this.entityManager.flush();

        this.synchronizer.delete(feature);
        Assert.assertNull(entityManager.find(IntactModelledFeature.class, feature.getAc()));
        Assert.assertNotNull(entityManager.find(IntactModelledFeature.class, feature2.getAc()));
        this.synchronizer.delete(feature2);
        Assert.assertNull(entityManager.find(IntactModelledFeature.class, feature2.getAc()));
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_find() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ModelledFeatureSynchronizer(this.context);

        IntactModelledFeature feature = new IntactModelledFeature("test feature", "full test feature");
        IntactModelledFeature feature2 = new IntactModelledFeature("test feature 2", "full test feature 2");
        feature.getLinkedFeatures().add(feature2);
        feature.setInterpro("IP-xxxx");
        feature.getXrefs().add(new ModelledFeatureXref(IntactUtils.createMIDatabase(Xref.ISOFORM_PARENT, Xref.ISOFORM_PARENT_MI), "EBI-xxx"));
        feature.getAliases().add(new ModelledFeatureAlias("test synonym"));
        feature.getAnnotations().add(new ModelledFeatureAnnotation(IntactUtils.createMITopic(Annotation.COMMENT, Annotation.COMMENT_MI), "test annotation"));
        feature.getRanges().add(new ModelledRange(new IntactPosition(1), new IntactPosition(2)));
        feature.setInteractionDependency(IntactUtils.createMITopic("test dependency", null));
        feature.setInteractionEffect(IntactUtils.createMITopic("test effect", null));
        this.synchronizer.persist(feature2);
        this.synchronizer.persist(feature);
        this.entityManager.flush();

        // cache, identity map
        Assert.assertNotNull(this.synchronizer.find(feature2));
        Assert.assertNull(this.synchronizer.find(new IntactModelledFeature("test feature 2", "full test feature 2")));
        Assert.assertNotNull(this.synchronizer.find(feature));
        Assert.assertNull(this.synchronizer.find(new IntactModelledFeature("test feature", "full test feature")));

        this.synchronizer.clearCache();
        // after clearing cache, cannot find
        Assert.assertNull(this.synchronizer.find(feature2));
        Assert.assertNull(this.synchronizer.find(feature));
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_properties() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ModelledFeatureSynchronizer(this.context);

        IntactModelledFeature feature = new IntactModelledFeature("test feature", "full test feature");
        IntactModelledFeature feature2 = new IntactModelledFeature("test feature 2", "full test feature 2");
        feature.getLinkedFeatures().add(feature2);
        feature.setInterpro("IP-xxxx");
        feature.getXrefs().add(new ModelledFeatureXref(IntactUtils.createMIDatabase(Xref.ISOFORM_PARENT, Xref.ISOFORM_PARENT_MI), "EBI-xxx"));
        feature.getAliases().add(new ModelledFeatureAlias("test synonym"));
        feature.getAnnotations().add(new ModelledFeatureAnnotation(IntactUtils.createMITopic(Annotation.COMMENT, Annotation.COMMENT_MI), "test annotation"));
        feature.getRanges().add(new ModelledRange(new IntactPosition(1), new IntactPosition(2)));
        feature.setInteractionDependency(IntactUtils.createMITopic("test dependency", null));
        feature.setInteractionEffect(IntactUtils.createMITopic("test effect", null));

        this.synchronizer.synchronizeProperties(feature2);
        Assert.assertNull(feature2.getAc());
        Assert.assertNotNull(feature2.getType());
        Assert.assertNotNull(((IntactCvTerm)feature2.getType()).getAc());
        Assert.assertEquals("test feature 2", feature2.getShortName());
        Assert.assertEquals("full test feature 2", feature2.getFullName());
        Assert.assertNull(feature2.getBinds());
        Assert.assertTrue(feature2.getDbLinkedFeatures().isEmpty());
        this.synchronizer.synchronizeProperties(feature);
        Assert.assertNull(feature.getAc());
        Assert.assertNotNull(feature.getType());
        Assert.assertNotNull(((IntactCvTerm)feature.getType()).getAc());
        Assert.assertEquals("test feature", feature.getShortName());
        Assert.assertEquals("full test feature", feature.getFullName());
        Assert.assertNotNull(feature.getBinds());
        Assert.assertTrue(feature.getBinds() == feature2);
        Assert.assertTrue(feature.getDbLinkedFeatures().isEmpty());
        Assert.assertEquals(1, feature.getLinkedFeatures().size());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_not_persist() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ModelledFeatureSynchronizer(this.context);

        IntactModelledFeature feature = new IntactModelledFeature("test feature", "full test feature");
        IntactModelledFeature feature2 = new IntactModelledFeature("test feature 2", "full test feature 2");
        feature.getLinkedFeatures().add(feature2);
        feature.setInterpro("IP-xxxx");
        feature.getXrefs().add(new ModelledFeatureXref(IntactUtils.createMIDatabase(Xref.ISOFORM_PARENT, Xref.ISOFORM_PARENT_MI), "EBI-xxx"));
        feature.getAliases().add(new ModelledFeatureAlias("test synonym"));
        feature.getAnnotations().add(new ModelledFeatureAnnotation(IntactUtils.createMITopic(Annotation.COMMENT, Annotation.COMMENT_MI), "test annotation"));
        feature.getRanges().add(new ModelledRange(new IntactPosition(1), new IntactPosition(2)));
        feature.setInteractionDependency(IntactUtils.createMITopic("test dependency", null));
        feature.setInteractionEffect(IntactUtils.createMITopic("test effect", null));

        this.synchronizer.synchronize(feature2, false);
        Assert.assertNull(feature2.getAc());
        Assert.assertNotNull(feature2.getType());
        Assert.assertNotNull(((IntactCvTerm)feature2.getType()).getAc());
        Assert.assertEquals("test feature 2", feature2.getShortName());
        Assert.assertEquals("full test feature 2", feature2.getFullName());
        Assert.assertNull(feature2.getBinds());
        Assert.assertTrue(feature2.getDbLinkedFeatures().isEmpty());
        this.synchronizer.synchronize(feature, false);
        Assert.assertNull(feature.getAc());
        Assert.assertNotNull(feature.getType());
        Assert.assertNotNull(((IntactCvTerm)feature.getType()).getAc());
        Assert.assertEquals("test feature", feature.getShortName());
        Assert.assertEquals("full test feature", feature.getFullName());
        Assert.assertNotNull(feature.getBinds());
        Assert.assertTrue(feature.getBinds() == feature2);
        Assert.assertTrue(feature.getDbLinkedFeatures().isEmpty());
        Assert.assertEquals(1, feature.getLinkedFeatures().size());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_persist() throws PersisterException, FinderException, SynchronizerException {

        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ModelledFeatureSynchronizer(this.context);

        IntactModelledFeature feature = new IntactModelledFeature("test feature", "full test feature");
        IntactModelledFeature feature2 = new IntactModelledFeature("test feature 2", "full test feature 2");
        feature.getLinkedFeatures().add(feature2);
        feature.setInterpro("IP-xxxx");
        feature.getXrefs().add(new ModelledFeatureXref(IntactUtils.createMIDatabase(Xref.ISOFORM_PARENT, Xref.ISOFORM_PARENT_MI), "EBI-xxx"));
        feature.getAliases().add(new ModelledFeatureAlias("test synonym"));
        feature.getAnnotations().add(new ModelledFeatureAnnotation(IntactUtils.createMITopic(Annotation.COMMENT, Annotation.COMMENT_MI), "test annotation"));
        feature.getRanges().add(new ModelledRange(new IntactPosition(1), new IntactPosition(2)));
        feature.setInteractionDependency(IntactUtils.createMITopic("test dependency", null));
        feature.setInteractionEffect(IntactUtils.createMITopic("test effect", null));

        this.synchronizer.synchronize(feature2, true);
        Assert.assertNotNull(feature2.getAc());
        Assert.assertNotNull(feature2.getType());
        Assert.assertNotNull(((IntactCvTerm)feature2.getType()).getAc());
        Assert.assertEquals("test feature 2", feature2.getShortName());
        Assert.assertEquals("full test feature 2", feature2.getFullName());
        Assert.assertNull(feature2.getBinds());
        Assert.assertTrue(feature2.getDbLinkedFeatures().isEmpty());
        this.synchronizer.synchronize(feature, true);
        Assert.assertNotNull(feature.getAc());
        Assert.assertNotNull(feature.getType());
        Assert.assertNotNull(((IntactCvTerm)feature.getType()).getAc());
        Assert.assertEquals("test feature", feature.getShortName());
        Assert.assertEquals("full test feature", feature.getFullName());
        Assert.assertNotNull(feature.getBinds());
        Assert.assertTrue(feature.getBinds() == feature2);
        Assert.assertTrue(feature.getDbLinkedFeatures().isEmpty());
        Assert.assertEquals(1, feature.getLinkedFeatures().size());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_merge() throws PersisterException, FinderException, SynchronizerException {

        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ModelledFeatureSynchronizer(this.context);

        IntactModelledFeature feature = new IntactModelledFeature("test feature", "full test feature");
        IntactModelledFeature feature2 = new IntactModelledFeature("test feature 2", "full test feature 2");
        feature.getLinkedFeatures().add(feature2);
        feature.setInterpro("IP-xxxx");
        feature.getXrefs().add(new ModelledFeatureXref(IntactUtils.createMIDatabase(Xref.ISOFORM_PARENT, Xref.ISOFORM_PARENT_MI), "EBI-xxx"));
        feature.getAliases().add(new ModelledFeatureAlias("test synonym"));
        feature.getAnnotations().add(new ModelledFeatureAnnotation(IntactUtils.createMITopic(Annotation.COMMENT, Annotation.COMMENT_MI), "test annotation"));
        feature.getRanges().add(new ModelledRange(new IntactPosition(1), new IntactPosition(2)));
        feature.setInteractionDependency(IntactUtils.createMITopic("test dependency", null));
        feature.setInteractionEffect(IntactUtils.createMITopic("test effect", null));

        this.synchronizer.synchronize(feature2, true);
        Assert.assertNotNull(feature2.getAc());
        this.synchronizer.synchronize(feature, true);
        Assert.assertNotNull(feature.getAc());

        this.entityManager.flush();
        this.entityManager.detach(feature2);
        this.entityManager.detach(feature);
        this.synchronizer.clearCache();

        feature.setShortName("test");
        feature2.setShortName("test2");

        IntactModelledFeature newFeature2 = (IntactModelledFeature)this.synchronizer.synchronize(feature2, true);
        Assert.assertNotNull(newFeature2.getAc());
        Assert.assertEquals("test2", newFeature2.getShortName());
        Assert.assertEquals(feature2.getAc(), newFeature2.getAc());
        IntactModelledFeature newFeature = (IntactModelledFeature)this.synchronizer.synchronize(feature, true);
        Assert.assertNotNull(newFeature.getAc());
        Assert.assertEquals("test", newFeature.getShortName());
        Assert.assertEquals(feature.getAc(), newFeature.getAc());

    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_merge2() throws PersisterException, FinderException, SynchronizerException {

        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ModelledFeatureSynchronizer(this.context);

        IntactModelledFeature feature = new IntactModelledFeature("test feature", "full test feature");
        IntactModelledFeature feature2 = new IntactModelledFeature("test feature 2", "full test feature 2");
        feature.getLinkedFeatures().add(feature2);
        feature.setInterpro("IP-xxxx");
        feature.getXrefs().add(new ModelledFeatureXref(IntactUtils.createMIDatabase(Xref.ISOFORM_PARENT, Xref.ISOFORM_PARENT_MI), "EBI-xxx"));
        feature.getAliases().add(new ModelledFeatureAlias("test synonym"));
        feature.getAnnotations().add(new ModelledFeatureAnnotation(IntactUtils.createMITopic(Annotation.COMMENT, Annotation.COMMENT_MI), "test annotation"));
        feature.getRanges().add(new ModelledRange(new IntactPosition(1), new IntactPosition(2)));
        feature.setInteractionDependency(IntactUtils.createMITopic("test dependency", null));
        feature.setInteractionEffect(IntactUtils.createMITopic("test effect", null));

        this.synchronizer.synchronize(feature2, true);
        Assert.assertNotNull(feature2.getAc());
        this.synchronizer.synchronize(feature, true);
        Assert.assertNotNull(feature.getAc());

        this.entityManager.flush();
        this.entityManager.detach(feature2);
        this.entityManager.detach(feature);
        this.synchronizer.clearCache();

        IntactModelledFeature f = entityManager.find(IntactModelledFeature.class, feature.getAc());
        f.setShortName("test");
        this.entityManager.detach(f);
        IntactModelledFeature f2 = entityManager.find(IntactModelledFeature.class, feature2.getAc());
        f2.setShortName("test2");

        IntactModelledFeature newFeature2 = this.synchronizer.synchronize(f2, true);
        Assert.assertNotNull(newFeature2.getAc());
        Assert.assertEquals("test2", newFeature2.getShortName());
        Assert.assertEquals(feature2.getAc(), newFeature2.getAc());
        IntactModelledFeature newFeature = this.synchronizer.synchronize(f, true);
        Assert.assertNotNull(newFeature.getAc());
        Assert.assertEquals("test", newFeature.getShortName());
        Assert.assertEquals(feature.getAc(), newFeature.getAc());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_jami() throws PersisterException, FinderException, SynchronizerException {

        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ModelledFeatureSynchronizer(this.context);

        IntactModelledFeature feature = new IntactModelledFeature("test feature", "full test feature");
        IntactModelledFeature feature2 = new IntactModelledFeature("test feature 2", "full test feature 2");
        feature.getLinkedFeatures().add(feature2);
        feature.setInterpro("IP-xxxx");
        feature.getXrefs().add(new ModelledFeatureXref(IntactUtils.createMIDatabase(Xref.ISOFORM_PARENT, Xref.ISOFORM_PARENT_MI), "EBI-xxx"));
        feature.getAliases().add(new ModelledFeatureAlias("test synonym"));
        feature.getAnnotations().add(new ModelledFeatureAnnotation(IntactUtils.createMITopic(Annotation.COMMENT, Annotation.COMMENT_MI), "test annotation"));
        feature.getRanges().add(new ModelledRange(new IntactPosition(1), new IntactPosition(2)));
        feature.setInteractionDependency(IntactUtils.createMITopic("test dependency", null));
        feature.setInteractionEffect(IntactUtils.createMITopic("test effect", null));

        IntactModelledFeature newFeature2 = this.synchronizer.synchronize(feature2, true);
        Assert.assertNotNull(newFeature2.getAc());
        Assert.assertNotNull(newFeature2.getType());
        Assert.assertNotNull(((IntactCvTerm)newFeature2.getType()).getAc());
        Assert.assertEquals("test feature 2", newFeature2.getShortName());
        Assert.assertEquals("full test feature 2", newFeature2.getFullName());
        Assert.assertNull(newFeature2.getBinds());
        Assert.assertTrue(newFeature2.getDbLinkedFeatures().isEmpty());
        IntactModelledFeature newFeature = this.synchronizer.synchronize(feature, true);
        Assert.assertNotNull(newFeature.getAc());
        Assert.assertNotNull(newFeature.getType());
        Assert.assertNotNull(((IntactCvTerm)newFeature.getType()).getAc());
        Assert.assertEquals("test feature", newFeature.getShortName());
        Assert.assertEquals("full test feature", newFeature.getFullName());
        Assert.assertNotNull(newFeature.getBinds());
        Assert.assertTrue(newFeature.getBinds() == newFeature2);
        Assert.assertTrue(newFeature.getDbLinkedFeatures().isEmpty());
        Assert.assertEquals(1, newFeature.getLinkedFeatures().size());
    }
}
