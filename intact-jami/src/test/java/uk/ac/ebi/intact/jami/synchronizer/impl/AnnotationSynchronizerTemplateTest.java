package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.IntactTestUtils;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Unit test for AnnotationSynchronizerTemplate
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/02/14</pre>
 */
public class AnnotationSynchronizerTemplateTest extends AbstractDbSynchronizerTest<Annotation, AbstractIntactAnnotation>{

    private int testNumber = 1;

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_all() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(CvTermAnnotation.class);
        this.testNumber = 1;
        persist();

        this.synchronizer.setIntactClass(InteractionAnnotation.class);
        this.testNumber = 2;
        persist();

        this.synchronizer.setIntactClass(InteractorAnnotation.class);
        this.testNumber = 3;
        persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_with_existing_topic() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

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
        this.synchronizer.setIntactClass(CvTermAnnotation.class);
        this.testNumber = 1;
        delete();

        this.synchronizer.setIntactClass(InteractionAnnotation.class);
        this.testNumber = 2;
        delete();

        this.synchronizer.setIntactClass(InteractorAnnotation.class);
        this.testNumber = 3;
        delete();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_with_detached_topic() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
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
        this.synchronizer.setIntactClass(CvTermAnnotation.class);
        this.testNumber = 1;
        find_no_cache();

        this.synchronizer.setIntactClass(InteractionAnnotation.class);
        this.testNumber = 2;
        find_no_cache();

        this.synchronizer.setIntactClass(InteractorAnnotation.class);
        this.testNumber = 3;
        find_no_cache();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_properties() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(CvTermAnnotation.class);
        this.testNumber = 1;
        synchronizeProperties();

        this.synchronizer.setIntactClass(InteractionAnnotation.class);
        this.testNumber = 2;
        synchronizeProperties();

        this.synchronizer.setIntactClass(InteractorAnnotation.class);
        this.testNumber = 3;
        synchronizeProperties();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_not_persist() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(CvTermAnnotation.class);
        this.testNumber = 1;
        synchronize_not_persist();

        this.synchronizer.setIntactClass(InteractionAnnotation.class);
        this.testNumber = 2;
        synchronize_not_persist();

        this.synchronizer.setIntactClass(InteractorAnnotation.class);
        this.testNumber = 3;
        synchronize_not_persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_persist() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(CvTermAnnotation.class);
        this.testNumber = 1;
        synchronize_persist();

        this.synchronizer.setIntactClass(InteractionAnnotation.class);
        this.testNumber = 2;
        synchronize_persist();

        this.synchronizer.setIntactClass(InteractorAnnotation.class);
        this.testNumber = 3;
        synchronize_persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_jami() throws PersisterException, FinderException, SynchronizerException {
        this.synchronizer.setIntactClass(CvTermAnnotation.class);
        this.testNumber = 1;
        persist_jami();

        this.synchronizer.setIntactClass(InteractionAnnotation.class);
        this.testNumber = 2;
        persist_jami();

        this.synchronizer.setIntactClass(InteractorAnnotation.class);
        this.testNumber = 3;
        persist_jami();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_merge1() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(CvTermAnnotation.class);
        this.testNumber = 1;
        merge_test1();

        this.synchronizer.setIntactClass(InteractionAnnotation.class);
        this.testNumber = 2;
        merge_test1();

        this.synchronizer.setIntactClass(InteractorAnnotation.class);
        this.testNumber = 3;
        merge_test1();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_merge2() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(CvTermAnnotation.class);
        this.testNumber = 1;
        merge_test2();

        this.synchronizer.setIntactClass(InteractionAnnotation.class);
        this.testNumber = 2;
        merge_test2();

        this.synchronizer.setIntactClass(InteractorAnnotation.class);
        this.testNumber = 3;
        merge_test2();
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

    @Override
    protected void testDeleteOtherProperties(AbstractIntactAnnotation objectToTest) {
        // nothing to do
    }

    @Override
    protected Annotation createDefaultJamiObject() {
        if (testNumber == 1){
            return IntactTestUtils.createAnnotationComment();
        }
        else if(testNumber == 2){
            return IntactTestUtils.createAnnotationNoDescription();
        }
        else{
            return IntactTestUtils.createAnnotationComment("test comment 2");
        }
    }

    @Override
    protected void testUpdatedPropertiesAfterMerge(AbstractIntactAnnotation objectToTest, AbstractIntactAnnotation newObjToTest) {
        Assert.assertEquals(objectToTest.getAc(), newObjToTest.getAc());
        Assert.assertEquals("value2", newObjToTest.getValue());
    }

    @Override
    protected void updatePropertieDetachedInstance(AbstractIntactAnnotation objectToTest) {
         objectToTest.setValue("value2");
    }

    @Override
    protected AbstractIntactAnnotation findObject(AbstractIntactAnnotation objectToTest) {
        if (testNumber == 1){
            return entityManager.find(CvTermAnnotation.class, objectToTest.getAc());
        }
        else if(testNumber == 2){
            return entityManager.find(InteractionAnnotation.class, objectToTest.getAc());
        }
        else{
            return entityManager.find(InteractorAnnotation.class, objectToTest.getAc());
        }
    }

    @Override
    protected void initSynchronizer() {
        this.synchronizer = new AnnotationSynchronizerTemplate(this.context, AbstractIntactAnnotation.class);
    }

    @Override
    protected void testPersistedProperties(AbstractIntactAnnotation persistedObject) {
        if (testNumber == 1){
            Assert.assertNotNull(persistedObject.getAc());
            Assert.assertNotNull(persistedObject.getTopic());
            IntactCvTerm aliasType = (IntactCvTerm)persistedObject.getTopic();
            Assert.assertNotNull(aliasType.getAc());
            Assert.assertEquals(persistedObject.getTopic(), IntactUtils.createMITopic(Annotation.COMMENT, Annotation.COMMENT_MI));
            Assert.assertEquals("test comment", persistedObject.getValue());
        }
        else if (testNumber == 2){
            Assert.assertNotNull(persistedObject.getAc());
            Assert.assertNotNull(persistedObject.getTopic());
            Assert.assertEquals(persistedObject.getTopic(), IntactUtils.createMITopic(Annotation.CAUTION, Annotation.CAUTION_MI));
            Assert.assertNull(persistedObject.getValue());
        }
        else{
            Assert.assertNotNull(persistedObject.getAc());
            Assert.assertNotNull(persistedObject.getTopic());
            IntactCvTerm aliasType2 = (IntactCvTerm)persistedObject.getTopic();
            Assert.assertNotNull(aliasType2.getAc());
            Assert.assertEquals(persistedObject.getTopic(), IntactUtils.createMITopic(Annotation.COMMENT, Annotation.COMMENT_MI));
            Assert.assertEquals("test comment 2", persistedObject.getValue());
        }
    }

    @Override
    protected void testNonPersistedProperties(AbstractIntactAnnotation objectToTest) {
        if (testNumber == 1){
            Assert.assertNull(objectToTest.getAc());
            Assert.assertNotNull(objectToTest.getTopic());
            IntactCvTerm aliasType = (IntactCvTerm)objectToTest.getTopic();
            Assert.assertNotNull(aliasType.getAc());
            Assert.assertEquals(objectToTest.getTopic(), IntactUtils.createMITopic(Annotation.COMMENT, Annotation.COMMENT_MI));
            Assert.assertEquals("test comment", objectToTest.getValue());
        }
        else if (testNumber == 2){
            Assert.assertNull(objectToTest.getAc());
            Assert.assertNotNull(objectToTest.getTopic());
            Assert.assertEquals(objectToTest.getTopic(), IntactUtils.createMITopic(Annotation.CAUTION, Annotation.CAUTION_MI));
            Assert.assertNull(objectToTest.getValue());
        }
        else{
            Assert.assertNull(objectToTest.getAc());
            Assert.assertNotNull(objectToTest.getTopic());
            IntactCvTerm aliasType2 = (IntactCvTerm)objectToTest.getTopic();
            Assert.assertNotNull(aliasType2.getAc());
            Assert.assertEquals(objectToTest.getTopic(), IntactUtils.createMITopic(Annotation.COMMENT, Annotation.COMMENT_MI));
            Assert.assertEquals("test comment 2", objectToTest.getValue());
        }
    }

    @Override
    protected AbstractIntactAnnotation createDefaultObject() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (testNumber == 1){
            return IntactTestUtils.createAnnotationComment(CvTermAnnotation.class);
        }
        else if(testNumber == 2){
            return IntactTestUtils.createAnnotationNoDescription(InteractionAnnotation.class);
        }
        else{
            return IntactTestUtils.createAnnotationComment(InteractorAnnotation.class, "test comment 2");
        }
    }
}
