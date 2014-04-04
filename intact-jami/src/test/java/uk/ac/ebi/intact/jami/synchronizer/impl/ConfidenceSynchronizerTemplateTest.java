package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Confidence;
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
 * Unit test for ConfidenceSynchronizerTemplate
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/02/14</pre>
 */
public class ConfidenceSynchronizerTemplateTest extends AbstractDbSynchronizerTest<Confidence, AbstractIntactConfidence>{

    private int testNumber = 1;
    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_all() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(ParticipantEvidenceConfidence.class);
        this.testNumber = 1;
        persist();

        this.synchronizer.setIntactClass(InteractionEvidenceConfidence.class);
        this.testNumber = 2;
        persist();

        this.synchronizer.setIntactClass(ComplexConfidence.class);
        this.testNumber = 3;
        persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_with_existing_type() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        IntactCvTerm authorScore = createExistingType();

        ParticipantEvidenceConfidence participantConfidence = IntactTestUtils.createConfidenceAuthorScore(ParticipantEvidenceConfidence.class);

        InteractionEvidenceConfidence interactionConfidence = IntactTestUtils.createConfidenceAuthorScore(InteractionEvidenceConfidence.class, "low");

        ComplexConfidence complexConfidence = IntactTestUtils.createConfidenceAuthorScore(ComplexConfidence.class,"0.5");

        this.synchronizer.setIntactClass(ParticipantEvidenceConfidence.class);
        this.synchronizer.persist(participantConfidence);

        Assert.assertNotNull(participantConfidence.getAc());
        Assert.assertNotNull(participantConfidence.getType());
        IntactCvTerm confType = (IntactCvTerm)participantConfidence.getType();
        Assert.assertNotNull(confType.getAc());
        Assert.assertTrue(participantConfidence.getType() == authorScore);
        Assert.assertEquals("high", participantConfidence.getValue());

        this.synchronizer.setIntactClass(InteractionEvidenceConfidence.class);
        this.synchronizer.persist(interactionConfidence);

        Assert.assertNotNull(interactionConfidence.getAc());
        Assert.assertNotNull(interactionConfidence.getType());
        Assert.assertEquals("low", interactionConfidence.getValue());

        this.synchronizer.setIntactClass(ComplexConfidence.class);
        this.synchronizer.persist(complexConfidence);

        Assert.assertNotNull(complexConfidence.getAc());
        Assert.assertNotNull(complexConfidence.getType());
        IntactCvTerm aliasType2 = (IntactCvTerm)complexConfidence.getType();
        Assert.assertNotNull(aliasType2.getAc());
        Assert.assertTrue(complexConfidence.getType() == authorScore);
        Assert.assertEquals("0.5", complexConfidence.getValue());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_with_detached_type() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // pre persist alias synonym
        IntactCvTerm authorScore = createExistingType();

        entityManager.detach(authorScore);

        ParticipantEvidenceConfidence participantConfidence = IntactTestUtils.createConfidenceAuthorScore(ParticipantEvidenceConfidence.class);
        participantConfidence.setType(authorScore);
        InteractionEvidenceConfidence interactionConfidence = IntactTestUtils.createConfidenceAuthorScore(InteractionEvidenceConfidence.class, "low");
        interactionConfidence.setType(authorScore);
        ComplexConfidence complexConfidence = IntactTestUtils.createConfidenceAuthorScore(ComplexConfidence.class,"0.5");
        complexConfidence.setType(authorScore);

        this.synchronizer.setIntactClass(ParticipantEvidenceConfidence.class);
        this.synchronizer.persist(participantConfidence);

        Assert.assertNotNull(participantConfidence.getType());
        IntactCvTerm confType = (IntactCvTerm)participantConfidence.getType();
        Assert.assertEquals(confType.getAc(), authorScore.getAc());
        Assert.assertEquals("high", participantConfidence.getValue());

        this.synchronizer.setIntactClass(InteractionEvidenceConfidence.class);
        this.synchronizer.persist(interactionConfidence);

        Assert.assertNotNull(interactionConfidence.getAc());
        Assert.assertNotNull(interactionConfidence.getType());
        Assert.assertEquals("low", interactionConfidence.getValue());

        this.synchronizer.setIntactClass(ComplexConfidence.class);
        this.synchronizer.persist(complexConfidence);

        Assert.assertNotNull(complexConfidence.getAc());
        Assert.assertNotNull(complexConfidence.getType());
        IntactCvTerm confType2 = (IntactCvTerm)complexConfidence.getType();
        Assert.assertEquals(confType2.getAc(), authorScore.getAc());
        Assert.assertEquals("0.5", complexConfidence.getValue());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_confidence_deleted() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(ParticipantEvidenceConfidence.class);
        this.testNumber = 1;
        delete();

        this.synchronizer.setIntactClass(InteractionEvidenceConfidence.class);
        this.testNumber = 2;
        delete();

        this.synchronizer.setIntactClass(ComplexConfidence.class);
        this.testNumber = 3;
        delete();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_find() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(ParticipantEvidenceConfidence.class);
        this.testNumber = 1;
        find_no_cache();

        this.synchronizer.setIntactClass(InteractionEvidenceConfidence.class);
        this.testNumber = 2;
        find_no_cache();

        this.synchronizer.setIntactClass(ComplexConfidence.class);
        this.testNumber = 3;
        find_no_cache();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_properties() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(ParticipantEvidenceConfidence.class);
        this.testNumber = 1;
        synchronizeProperties();

        this.synchronizer.setIntactClass(InteractionEvidenceConfidence.class);
        this.testNumber = 2;
        synchronizeProperties();

        this.synchronizer.setIntactClass(ComplexConfidence.class);
        this.testNumber = 3;
        synchronizeProperties();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_not_persist() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(ParticipantEvidenceConfidence.class);
        this.testNumber = 1;
        synchronize_not_persist();

        this.synchronizer.setIntactClass(InteractionEvidenceConfidence.class);
        this.testNumber = 2;
        synchronize_not_persist();

        this.synchronizer.setIntactClass(ComplexConfidence.class);
        this.testNumber = 3;
        synchronize_not_persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_persist() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(ParticipantEvidenceConfidence.class);
        this.testNumber = 1;
        synchronize_persist();

        this.synchronizer.setIntactClass(InteractionEvidenceConfidence.class);
        this.testNumber = 2;
        synchronize_persist();

        this.synchronizer.setIntactClass(ComplexConfidence.class);
        this.testNumber = 3;
        synchronize_persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_jami() throws PersisterException, FinderException, SynchronizerException {
        this.synchronizer.setIntactClass(ParticipantEvidenceConfidence.class);
        this.testNumber = 1;
        persist_jami();

        this.synchronizer.setIntactClass(InteractionEvidenceConfidence.class);
        this.testNumber = 2;
        persist_jami();

        this.synchronizer.setIntactClass(ComplexConfidence.class);
        this.testNumber = 3;
        persist_jami();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_merge1() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(ParticipantEvidenceConfidence.class);
        this.testNumber = 1;
        merge_test1();

        this.synchronizer.setIntactClass(InteractionEvidenceConfidence.class);
        this.testNumber = 2;
        merge_test1();

        this.synchronizer.setIntactClass(ComplexConfidence.class);
        this.testNumber = 3;
        merge_test1();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_merge2() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(ParticipantEvidenceConfidence.class);
        this.testNumber = 1;
        merge_test2();

        this.synchronizer.setIntactClass(InteractionEvidenceConfidence.class);
        this.testNumber = 2;
        merge_test2();

        this.synchronizer.setIntactClass(ComplexConfidence.class);
        this.testNumber = 3;
        merge_test2();
    }

    private IntactCvTerm createExistingType() {
        // pre persist alias synonym
        IntactCvTerm aliasSynonym = new IntactCvTerm("author-score");
        aliasSynonym.setObjClass(IntactUtils.CONFIDENCE_TYPE_OBJCLASS);
        entityManager.persist(aliasSynonym);
        IntactCvTerm psimi = new IntactCvTerm(CvTerm.PSI_MI);
        psimi.setObjClass(IntactUtils.DATABASE_OBJCLASS);
        entityManager.persist(psimi);
        IntactCvTerm identity = new IntactCvTerm(Xref.IDENTITY);
        identity.setObjClass(IntactUtils.QUALIFIER_OBJCLASS);
        entityManager.persist(identity);

        CvTermXref ref1 = new CvTermXref(psimi, "MI:xxx1", identity);
        aliasSynonym.getDbXrefs().add(ref1);
        CvTermXref ref2 = new CvTermXref(psimi, CvTerm.PSI_MI, identity);
        psimi.getDbXrefs().add(ref2);
        CvTermXref ref3 = new CvTermXref(psimi, Xref.IDENTITY_MI, identity);
        identity.getDbXrefs().add(ref3);
        entityManager.flush();
        this.context.clearCache();
        return aliasSynonym;
    }

    @Override
    protected Confidence createDefaultJamiObject() {
        if (testNumber == 1){
            return IntactTestUtils.createConfidenceAuthorScore();
        }
        else if (testNumber == 2){
            return IntactTestUtils.createConfidenceAuthorScore("low");
        }
        else{
            return IntactTestUtils.createConfidenceAuthorScore("0.5");
        }
    }

    @Override
    protected void testUpdatedPropertiesAfterMerge(AbstractIntactConfidence objectToTest, AbstractIntactConfidence newObjToTest) {
        Assert.assertEquals(objectToTest.getAc(), newObjToTest.getAc());
        Assert.assertEquals("value2", newObjToTest.getValue());
    }

    @Override
    protected void updatePropertieDetachedInstance(AbstractIntactConfidence objectToTest) {
        objectToTest.setValue("value2");
    }

    @Override
    protected AbstractIntactConfidence findObject(AbstractIntactConfidence objectToTest) {
        if (testNumber == 1){
            return entityManager.find(ParticipantEvidenceConfidence.class, objectToTest.getAc());
        }
        else if (testNumber == 2){
            return entityManager.find(InteractionEvidenceConfidence.class, objectToTest.getAc());
        }
        else{
            return entityManager.find(ComplexConfidence.class, objectToTest.getAc());
        }
    }

    @Override
    protected void initSynchronizer() {
        this.synchronizer = new ConfidenceSynchronizerTemplate(this.context, AbstractIntactConfidence.class);
    }

    @Override
    protected void testPersistedProperties(AbstractIntactConfidence objectToTest) {
        if (testNumber == 1){
            Assert.assertNotNull(objectToTest.getAc());
            Assert.assertNotNull(objectToTest.getType());
            IntactCvTerm confType = (IntactCvTerm)objectToTest.getType();
            Assert.assertNotNull(confType.getAc());
            Assert.assertEquals(objectToTest.getType(),IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"));
            Assert.assertEquals("high", objectToTest.getValue());
        }
        else if (testNumber == 2){
            Assert.assertNotNull(objectToTest.getAc());
            Assert.assertNotNull(objectToTest.getType());
            Assert.assertEquals("low", objectToTest.getValue());
        }
        else{
            Assert.assertNotNull(objectToTest.getAc());
            Assert.assertNotNull(objectToTest.getType());
            IntactCvTerm aliasType2 = (IntactCvTerm)objectToTest.getType();
            Assert.assertNotNull(aliasType2.getAc());
            Assert.assertEquals(objectToTest.getType(),IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"));
            Assert.assertEquals("0.5", objectToTest.getValue());
        }
    }

    @Override
    protected void testNonPersistedProperties(AbstractIntactConfidence objectToTest) {
        if (testNumber == 1){
            Assert.assertNull(objectToTest.getAc());
            Assert.assertNotNull(objectToTest.getType());
            IntactCvTerm confType = (IntactCvTerm)objectToTest.getType();
            Assert.assertNotNull(confType.getAc());
            Assert.assertEquals(objectToTest.getType(),IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"));
            Assert.assertEquals("high", objectToTest.getValue());
        }
        else if (testNumber == 2){
            Assert.assertNull(objectToTest.getAc());
            Assert.assertNotNull(objectToTest.getType());
            Assert.assertEquals("low", objectToTest.getValue());
        }
        else{
            Assert.assertNull(objectToTest.getAc());
            Assert.assertNotNull(objectToTest.getType());
            IntactCvTerm aliasType2 = (IntactCvTerm)objectToTest.getType();
            Assert.assertNotNull(aliasType2.getAc());
            Assert.assertEquals(objectToTest.getType(), IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"));
            Assert.assertEquals("0.5", objectToTest.getValue());
        }
    }

    @Override
    protected AbstractIntactConfidence createDefaultObject() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (testNumber == 1){
            return IntactTestUtils.createConfidenceAuthorScore(ParticipantEvidenceConfidence.class);
        }
        else if (testNumber == 2){
            return IntactTestUtils.createConfidenceAuthorScore(InteractionEvidenceConfidence.class, "low");
        }
        else{
            return IntactTestUtils.createConfidenceAuthorScore(ComplexConfidence.class,"0.5");
        }
    }
}
