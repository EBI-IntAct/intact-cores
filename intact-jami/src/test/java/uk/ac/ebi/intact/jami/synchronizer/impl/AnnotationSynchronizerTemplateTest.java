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
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.IntactTestUtils;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.AnnotationSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.lang.reflect.InvocationTargetException;

/**
 * Unit test for AnnotationSynchronizerTemplate
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/02/14</pre>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/intact-jami-test.spring.xml"})
@Transactional
@TransactionConfiguration
public class AnnotationSynchronizerTemplateTest {

    @PersistenceContext(unitName = "intact-core")
    private EntityManager entityManager;
    @PersistenceUnit(unitName = "intact-core", name = "intactEntityManagerFactory")
    private EntityManagerFactory intactEntityManagerFactory;

    private AnnotationSynchronizer synchronizer;
    private SynchronizerContext context;

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_all() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new AnnotationSynchronizerTemplate(this.context, AbstractIntactAnnotation.class);

        CvTermAnnotation cvAnnotation = IntactTestUtils.createAnnotationComment(CvTermAnnotation.class);

        InteractionAnnotation interactionAnnotation = IntactTestUtils.createAnnotationNoDescription(InteractionAnnotation.class);

        InteractorAnnotation interactorAnnotation = IntactTestUtils.createAnnotationComment(InteractorAnnotation.class, "test comment 2");

        this.synchronizer.setIntactClass(CvTermAnnotation.class);
        this.synchronizer.persist(cvAnnotation);

        Assert.assertNotNull(cvAnnotation.getAc());
        Assert.assertNotNull(cvAnnotation.getTopic());
        IntactCvTerm aliasType = (IntactCvTerm)cvAnnotation.getTopic();
        Assert.assertNotNull(aliasType.getAc());
        Assert.assertEquals(cvAnnotation.getTopic(), IntactUtils.createMITopic(Annotation.COMMENT, Annotation.COMMENT_MI));
        Assert.assertEquals("test comment", cvAnnotation.getValue());

        this.synchronizer.setIntactClass(InteractionAnnotation.class);
        this.synchronizer.persist(interactionAnnotation);

        Assert.assertNotNull(interactionAnnotation.getAc());
        Assert.assertNotNull(interactionAnnotation.getTopic());
        Assert.assertFalse(cvAnnotation.getTopic() == interactionAnnotation.getTopic());
        Assert.assertNull(interactionAnnotation.getValue());

        this.synchronizer.setIntactClass(InteractorAnnotation.class);
        this.synchronizer.persist(interactorAnnotation);

        Assert.assertNotNull(interactorAnnotation.getAc());
        Assert.assertNotNull(interactorAnnotation.getTopic());
        IntactCvTerm aliasType2 = (IntactCvTerm)interactorAnnotation.getTopic();
        Assert.assertNotNull(aliasType2.getAc());
        Assert.assertTrue(cvAnnotation.getTopic() == aliasType2);
        Assert.assertEquals("test comment 2", interactorAnnotation.getValue());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_with_existing_topic() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new AnnotationSynchronizerTemplate(this.context, AbstractIntactAnnotation.class);

        IntactCvTerm annotationTopic = createExistingTopic();

        CvTermAnnotation cvAnnotation = IntactTestUtils.createAnnotationComment(CvTermAnnotation.class);

        InteractionAnnotation interactionAnnotation = IntactTestUtils.createAnnotationNoDescription(InteractionAnnotation.class);

        InteractorAnnotation interactorAnnotation = IntactTestUtils.createAnnotationComment(InteractorAnnotation.class, "test comment 2");

        this.synchronizer.setIntactClass(CvTermAnnotation.class);
        this.synchronizer.persist(cvAnnotation);

        Assert.assertNotNull(cvAnnotation.getTopic());
        IntactCvTerm aliasType = (IntactCvTerm)cvAnnotation.getTopic();
        Assert.assertEquals(aliasType.getAc(), annotationTopic.getAc());
        Assert.assertEquals("test comment", cvAnnotation.getValue());

        this.synchronizer.setIntactClass(InteractionAnnotation.class);
        this.synchronizer.persist(interactionAnnotation);

        Assert.assertNotNull(interactionAnnotation.getAc());
        Assert.assertNotNull(interactionAnnotation.getTopic());
        IntactCvTerm aliasType3 = (IntactCvTerm)interactionAnnotation.getTopic();
        Assert.assertNotSame(aliasType3.getAc(), annotationTopic.getAc());
        Assert.assertNull(interactionAnnotation.getValue());

        this.synchronizer.setIntactClass(InteractorAnnotation.class);
        this.synchronizer.persist(interactorAnnotation);

        Assert.assertNotNull(interactorAnnotation.getAc());
        Assert.assertNotNull(interactorAnnotation.getTopic());
        IntactCvTerm aliasType2 = (IntactCvTerm)interactorAnnotation.getTopic();
        Assert.assertEquals(aliasType2.getAc(), annotationTopic.getAc());
        Assert.assertEquals("test comment 2", interactorAnnotation.getValue());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_delete() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new AnnotationSynchronizerTemplate(this.context, AbstractIntactAnnotation.class);

        IntactCvTerm annotationTopic = createExistingTopic();

        CvTermAnnotation cvAnnotation = IntactTestUtils.createAnnotationComment(CvTermAnnotation.class);

        this.synchronizer.setIntactClass(CvTermAnnotation.class);
        this.synchronizer.persist(cvAnnotation);

        Assert.assertNotNull(cvAnnotation.getTopic());
        IntactCvTerm aliasType = (IntactCvTerm)cvAnnotation.getTopic();
        Assert.assertEquals(aliasType.getAc(), annotationTopic.getAc());

        entityManager.flush();

        this.synchronizer.delete(cvAnnotation);
        Assert.assertNull(entityManager.find(CvTermAnnotation.class, cvAnnotation.getAc()));
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_with_detached_topic() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new AnnotationSynchronizerTemplate(this.context, AbstractIntactAnnotation.class);

        // pre persist annotationTopic
        IntactCvTerm existingTopic = createExistingTopic();

        entityManager.detach(existingTopic);

        CvTermAnnotation cvAnnotation = IntactTestUtils.createAnnotationComment(CvTermAnnotation.class);
        cvAnnotation.setTopic(existingTopic);

        InteractionAnnotation interactionAnnotation = IntactTestUtils.createAnnotationNoDescription(InteractionAnnotation.class);

        InteractorAnnotation interactorAnnotation = IntactTestUtils.createAnnotationComment(InteractorAnnotation.class, "test comment 2");
        interactorAnnotation.setTopic(existingTopic);

        this.synchronizer.setIntactClass(CvTermAnnotation.class);
        this.synchronizer.persist(cvAnnotation);

        Assert.assertNotNull(cvAnnotation.getTopic());
        IntactCvTerm aliasType = (IntactCvTerm)cvAnnotation.getTopic();
        Assert.assertEquals(aliasType.getAc(), existingTopic.getAc());
        Assert.assertEquals("test comment", cvAnnotation.getValue());

        this.synchronizer.setIntactClass(InteractionAnnotation.class);
        this.synchronizer.persist(interactionAnnotation);

        Assert.assertNotNull(interactionAnnotation.getAc());
        Assert.assertNotNull(interactionAnnotation.getTopic());
        IntactCvTerm aliasType3 = (IntactCvTerm)interactionAnnotation.getTopic();
        Assert.assertNotSame(aliasType3.getAc(), existingTopic.getAc());
        Assert.assertNull(interactionAnnotation.getValue());

        this.synchronizer.setIntactClass(InteractorAnnotation.class);
        this.synchronizer.persist(interactorAnnotation);

        Assert.assertNotNull(interactorAnnotation.getAc());
        Assert.assertNotNull(interactorAnnotation.getTopic());
        IntactCvTerm aliasType2 = (IntactCvTerm)interactorAnnotation.getTopic();
        Assert.assertEquals(aliasType2.getAc(), existingTopic.getAc());
        Assert.assertEquals("test comment 2", interactorAnnotation.getValue());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_find() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new AnnotationSynchronizerTemplate(this.context, AbstractIntactAnnotation.class);

        CvTermAnnotation cvAnnotationNotPersisted = IntactTestUtils.createAnnotationComment(CvTermAnnotation.class);
        CvTermAnnotation cvAnnotationPersisted = IntactTestUtils.createAnnotationComment(CvTermAnnotation.class, "test comemnt 2");

        this.synchronizer.persist(cvAnnotationNotPersisted);
        entityManager.flush();
        this.context.clearCache();

        System.out.println("flush");

        Assert.assertNull(this.synchronizer.find(cvAnnotationNotPersisted));
        Assert.assertNull(this.synchronizer.find(cvAnnotationPersisted));
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_properties() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new AnnotationSynchronizerTemplate(this.context, AbstractIntactAnnotation.class);

        CvTermAnnotation cvAnnotation = IntactTestUtils.createAnnotationComment(CvTermAnnotation.class);

        InteractionAnnotation interactionAnnotation = IntactTestUtils.createAnnotationNoDescription(InteractionAnnotation.class);

        InteractorAnnotation interactorAnnotation = IntactTestUtils.createAnnotationComment(InteractorAnnotation.class, "test comment 2");

        this.synchronizer.setIntactClass(CvTermAnnotation.class);
        this.synchronizer.synchronizeProperties(cvAnnotation);

        Assert.assertNull(cvAnnotation.getAc());
        Assert.assertNotNull(cvAnnotation.getTopic());
        IntactCvTerm aliasType = (IntactCvTerm)cvAnnotation.getTopic();
        Assert.assertNotNull(aliasType.getAc());
        Assert.assertEquals(cvAnnotation.getTopic(), IntactUtils.createMITopic(Annotation.COMMENT, Annotation.COMMENT_MI));
        Assert.assertEquals("test comment", cvAnnotation.getValue());

        this.synchronizer.setIntactClass(InteractionAnnotation.class);
        this.synchronizer.synchronizeProperties(interactionAnnotation);

        Assert.assertNull(interactionAnnotation.getAc());
        Assert.assertNotNull(interactionAnnotation.getTopic());
        Assert.assertFalse(cvAnnotation.getTopic() == interactionAnnotation.getTopic());
        Assert.assertNull(interactionAnnotation.getValue());

        this.synchronizer.setIntactClass(InteractorAnnotation.class);
        this.synchronizer.synchronizeProperties(interactorAnnotation);

        Assert.assertNull(interactorAnnotation.getAc());
        Assert.assertNotNull(interactorAnnotation.getTopic());
        IntactCvTerm aliasType2 = (IntactCvTerm)interactorAnnotation.getTopic();
        Assert.assertNotNull(aliasType2.getAc());
        Assert.assertTrue(cvAnnotation.getTopic() == aliasType2);
        Assert.assertEquals("test comment 2", interactorAnnotation.getValue());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_not_persist() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new AnnotationSynchronizerTemplate(this.context, AbstractIntactAnnotation.class);

        CvTermAnnotation cvAnnotation = IntactTestUtils.createAnnotationComment(CvTermAnnotation.class);

        InteractionAnnotation interactionAnnotation = IntactTestUtils.createAnnotationNoDescription(InteractionAnnotation.class);

        InteractorAnnotation interactorAnnotation = IntactTestUtils.createAnnotationComment(InteractorAnnotation.class, "test comment 2");

        this.synchronizer.setIntactClass(CvTermAnnotation.class);
        this.synchronizer.synchronize(cvAnnotation, false);

        Assert.assertNull(cvAnnotation.getAc());
        Assert.assertNotNull(cvAnnotation.getTopic());
        IntactCvTerm aliasType = (IntactCvTerm)cvAnnotation.getTopic();
        Assert.assertNotNull(aliasType.getAc());
        Assert.assertEquals(cvAnnotation.getTopic(), IntactUtils.createMITopic(Annotation.COMMENT, Annotation.COMMENT_MI));
        Assert.assertEquals("test comment", cvAnnotation.getValue());

        this.synchronizer.setIntactClass(InteractionAnnotation.class);
        this.synchronizer.synchronize(interactionAnnotation, false);

        Assert.assertNull(interactionAnnotation.getAc());
        Assert.assertNotNull(interactionAnnotation.getTopic());
        Assert.assertFalse(cvAnnotation.getTopic() == interactionAnnotation.getTopic());
        Assert.assertNull(interactionAnnotation.getValue());

        this.synchronizer.setIntactClass(InteractorAnnotation.class);
        this.synchronizer.synchronize(interactorAnnotation, false);

        Assert.assertNull(interactorAnnotation.getAc());
        Assert.assertNotNull(interactorAnnotation.getTopic());
        IntactCvTerm aliasType2 = (IntactCvTerm)interactorAnnotation.getTopic();
        Assert.assertNotNull(aliasType2.getAc());
        Assert.assertTrue(cvAnnotation.getTopic() == aliasType2);
        Assert.assertEquals("test comment 2", interactorAnnotation.getValue());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_persist() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new AnnotationSynchronizerTemplate(this.context, AbstractIntactAnnotation.class);

        CvTermAnnotation cvAnnotation = IntactTestUtils.createAnnotationComment(CvTermAnnotation.class);

        InteractionAnnotation interactionAnnotation = IntactTestUtils.createAnnotationNoDescription(InteractionAnnotation.class);

        InteractorAnnotation interactorAnnotation = IntactTestUtils.createAnnotationComment(InteractorAnnotation.class, "test comment 2");

        this.synchronizer.setIntactClass(CvTermAnnotation.class);
        this.synchronizer.synchronize(cvAnnotation, true);

        Assert.assertNotNull(cvAnnotation.getAc());
        Assert.assertNotNull(cvAnnotation.getTopic());
        IntactCvTerm aliasType = (IntactCvTerm)cvAnnotation.getTopic();
        Assert.assertNotNull(aliasType.getAc());
        Assert.assertEquals(cvAnnotation.getTopic(), IntactUtils.createMITopic(Annotation.COMMENT, Annotation.COMMENT_MI));
        Assert.assertEquals("test comment", cvAnnotation.getValue());

        this.synchronizer.setIntactClass(InteractionAnnotation.class);
        this.synchronizer.synchronize(interactionAnnotation, true);

        Assert.assertNotNull(interactionAnnotation.getAc());
        Assert.assertNotNull(interactionAnnotation.getTopic());
        Assert.assertFalse(cvAnnotation.getTopic() == interactionAnnotation.getTopic());
        Assert.assertNull(interactionAnnotation.getValue());

        this.synchronizer.setIntactClass(InteractorAnnotation.class);
        this.synchronizer.synchronize(interactorAnnotation, true);

        Assert.assertNotNull(interactorAnnotation.getAc());
        Assert.assertNotNull(interactorAnnotation.getTopic());
        IntactCvTerm aliasType2 = (IntactCvTerm)interactorAnnotation.getTopic();
        Assert.assertNotNull(aliasType2.getAc());
        Assert.assertTrue(cvAnnotation.getTopic() == aliasType2);
        Assert.assertEquals("test comment 2", interactorAnnotation.getValue());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_jami() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new AnnotationSynchronizerTemplate(this.context, AbstractIntactAnnotation.class);

        Annotation cvAnnotation = IntactTestUtils.createAnnotationComment();

        Annotation interactionAnnotation = IntactTestUtils.createAnnotationNoDescription();

        Annotation interactorAnnotation = IntactTestUtils.createAnnotationComment("test comment 2");

        this.synchronizer.setIntactClass(CvTermAnnotation.class);
        CvTermAnnotation newAnnot = (CvTermAnnotation)this.synchronizer.synchronize(cvAnnotation, true);

        Assert.assertNotNull(newAnnot.getAc());
        Assert.assertNotNull(newAnnot.getTopic());
        IntactCvTerm aliasType = (IntactCvTerm)newAnnot.getTopic();
        Assert.assertNotNull(aliasType.getAc());
        Assert.assertEquals(cvAnnotation.getTopic(), IntactUtils.createMITopic(Annotation.COMMENT, Annotation.COMMENT_MI));
        Assert.assertEquals("test comment", newAnnot.getValue());

        this.synchronizer.setIntactClass(InteractionAnnotation.class);
        InteractionAnnotation newAnnot2 = (InteractionAnnotation)this.synchronizer.synchronize(interactionAnnotation, true);

        Assert.assertNotNull(newAnnot2.getAc());
        Assert.assertNotNull(newAnnot2.getTopic());
        Assert.assertFalse(newAnnot.getTopic() == newAnnot2.getTopic());
        Assert.assertNull(interactionAnnotation.getValue());

        this.synchronizer.setIntactClass(InteractorAnnotation.class);
        InteractorAnnotation newAnnot3 = (InteractorAnnotation)this.synchronizer.synchronize(interactorAnnotation, true);

        Assert.assertNotNull(newAnnot3.getAc());
        Assert.assertNotNull(newAnnot3.getTopic());
        IntactCvTerm aliasType2 = (IntactCvTerm)newAnnot3.getTopic();
        Assert.assertNotNull(aliasType2.getAc());
        Assert.assertTrue(newAnnot.getTopic() == aliasType2);
        Assert.assertEquals("test comment 2", newAnnot3.getValue());
    }

    private IntactCvTerm createExistingTopic() {
        // pre persist alias synonym
        IntactCvTerm comment = new IntactCvTerm(Annotation.COMMENT);
        comment.setObjClass(IntactUtils.TOPIC_OBJCLASS);
        entityManager.persist(comment);
        IntactCvTerm psimi = new IntactCvTerm(CvTerm.PSI_MI);
        psimi.setObjClass(IntactUtils.DATABASE_OBJCLASS);
        entityManager.persist(psimi);
        IntactCvTerm identity = new IntactCvTerm(Xref.IDENTITY);
        identity.setObjClass(IntactUtils.QUALIFIER_OBJCLASS);
        entityManager.persist(identity);

        CvTermXref ref1 = new CvTermXref(psimi, Annotation.COMMENT_MI, identity);
        comment.getDbXrefs().add(ref1);
        CvTermXref ref2 = new CvTermXref(psimi, CvTerm.PSI_MI, identity);
        psimi.getDbXrefs().add(ref2);
        CvTermXref ref3 = new CvTermXref(psimi, Xref.IDENTITY_MI, identity);
        identity.getDbXrefs().add(ref3);
        entityManager.flush();
        this.context.clearCache();
        return comment;
    }
}
