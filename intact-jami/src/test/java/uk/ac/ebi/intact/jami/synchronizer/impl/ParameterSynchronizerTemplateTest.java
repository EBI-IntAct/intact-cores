package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Parameter;
import psidev.psi.mi.jami.model.ParameterValue;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.IntactTestUtils;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

/**
 * Unit test for ParameterSynchronizerTemplate
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/02/14</pre>
 */
public class ParameterSynchronizerTemplateTest extends AbstractDbSynchronizerTest<Parameter, AbstractIntactParameter>{

    private int testNumber = 1;

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_all() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(ParticipantEvidenceParameter.class);
        this.testNumber = 1;
        persist();

        this.synchronizer.setIntactClass(InteractionEvidenceParameter.class);
        this.testNumber = 2;
        persist();

        this.synchronizer.setIntactClass(ComplexParameter.class);
        this.testNumber = 3;
        persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_with_existing_type() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        IntactCvTerm kdType = createExistingType();

        ParticipantEvidenceParameter participantParameter = IntactTestUtils.
                createKdParameterNoUnit(ParticipantEvidenceParameter.class);

        InteractionEvidenceParameter interactionParameter = IntactTestUtils.
                createKdParameter(InteractionEvidenceParameter.class);

        ComplexParameter complexParameter = IntactTestUtils.
                createParameterNoUnit(ComplexParameter.class, "molecular weight", "MI:xxx2", 6);
        this.synchronizer.setIntactClass(ParticipantEvidenceParameter.class);
        this.synchronizer.persist(participantParameter);

        Assert.assertNotNull(participantParameter.getAc());
        Assert.assertNull(participantParameter.getUnit());
        Assert.assertNotNull(participantParameter.getType());
        IntactCvTerm confType = (IntactCvTerm)participantParameter.getType();
        Assert.assertNotNull(confType.getAc());
        Assert.assertTrue(participantParameter.getType() == kdType);
        Assert.assertEquals(new ParameterValue(new BigDecimal(3)), participantParameter.getValue());

        this.synchronizer.setIntactClass(InteractionEvidenceParameter.class);
        this.synchronizer.persist(interactionParameter);

        Assert.assertNotNull(interactionParameter.getAc());
        Assert.assertNotNull(interactionParameter.getType());
        Assert.assertNotNull(interactionParameter.getUnit());
        IntactCvTerm paramUnit = (IntactCvTerm)interactionParameter.getUnit();
        Assert.assertNotNull(paramUnit.getAc());
        Assert.assertEquals(interactionParameter.getUnit(),IntactUtils.createMIUnit("molar", "MI:xxx3"));
        Assert.assertEquals(new ParameterValue(new BigDecimal(5)), interactionParameter.getValue());

        this.synchronizer.setIntactClass(ComplexParameter.class);
        this.synchronizer.persist(complexParameter);

        Assert.assertNotNull(complexParameter.getAc());
        Assert.assertNotNull(complexParameter.getType());
        IntactCvTerm aliasType2 = (IntactCvTerm)complexParameter.getType();
        Assert.assertNotNull(aliasType2.getAc());
        Assert.assertTrue(complexParameter.getType() != kdType);
        Assert.assertEquals(new ParameterValue(new BigDecimal(6)), complexParameter.getValue());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_with_detached_type() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // pre persist kd
        IntactCvTerm kdType = createExistingType();

        entityManager.detach(kdType);

        ParticipantEvidenceParameter participantParameter = IntactTestUtils.
                createKdParameterNoUnit(ParticipantEvidenceParameter.class);
        participantParameter.setType(kdType);

        InteractionEvidenceParameter interactionParameter = IntactTestUtils.
                createKdParameter(InteractionEvidenceParameter.class);
        interactionParameter.setType(kdType);

        this.synchronizer.setIntactClass(ParticipantEvidenceParameter.class);
        this.synchronizer.persist(participantParameter);

        Assert.assertNotNull(participantParameter.getType());
        Assert.assertNull(participantParameter.getUnit());
        IntactCvTerm confType = (IntactCvTerm)participantParameter.getType();
        Assert.assertEquals(confType.getAc(), kdType.getAc());
        Assert.assertEquals(new ParameterValue(new BigDecimal(3)), participantParameter.getValue());

        this.synchronizer.setIntactClass(InteractionEvidenceParameter.class);
        this.synchronizer.persist(interactionParameter);

        Assert.assertNotNull(interactionParameter.getAc());
        IntactCvTerm paramUnit = (IntactCvTerm)interactionParameter.getUnit();
        Assert.assertNotNull(paramUnit.getAc());
        Assert.assertEquals(interactionParameter.getUnit(),IntactUtils.createMIUnit("molar", "MI:xxx3"));
        Assert.assertNotNull(interactionParameter.getType());
        Assert.assertEquals(new ParameterValue(new BigDecimal(5)), interactionParameter.getValue());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_confidence_deleted() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(ParticipantEvidenceParameter.class);
        this.testNumber = 1;
        delete();

        this.synchronizer.setIntactClass(InteractionEvidenceParameter.class);
        this.testNumber = 2;
        delete();

        this.synchronizer.setIntactClass(ComplexParameter.class);
        this.testNumber = 3;
        delete();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_find() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(ParticipantEvidenceParameter.class);
        this.testNumber = 1;
        find_no_cache();

        this.synchronizer.setIntactClass(InteractionEvidenceParameter.class);
        this.testNumber = 2;
        find_no_cache();

        this.synchronizer.setIntactClass(ComplexParameter.class);
        this.testNumber = 3;
        find_no_cache();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_properties() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(ParticipantEvidenceParameter.class);
        this.testNumber = 1;
        synchronizeProperties();

        this.synchronizer.setIntactClass(InteractionEvidenceParameter.class);
        this.testNumber = 2;
        synchronizeProperties();

        this.synchronizer.setIntactClass(ComplexParameter.class);
        this.testNumber = 3;
        synchronizeProperties();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_not_persist() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(ParticipantEvidenceParameter.class);
        this.testNumber = 1;
        synchronize_not_persist();

        this.synchronizer.setIntactClass(InteractionEvidenceParameter.class);
        this.testNumber = 2;
        synchronize_not_persist();

        this.synchronizer.setIntactClass(ComplexParameter.class);
        this.testNumber = 3;
        synchronize_not_persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_persist() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(ParticipantEvidenceParameter.class);
        this.testNumber = 1;
        synchronize_persist();

        this.synchronizer.setIntactClass(InteractionEvidenceParameter.class);
        this.testNumber = 2;
        synchronize_persist();

        this.synchronizer.setIntactClass(ComplexParameter.class);
        this.testNumber = 3;
        synchronize_persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_jami() throws PersisterException, FinderException, SynchronizerException {
        this.synchronizer.setIntactClass(ParticipantEvidenceParameter.class);
        this.testNumber = 1;
        persist_jami();

        this.synchronizer.setIntactClass(InteractionEvidenceParameter.class);
        this.testNumber = 2;
        persist_jami();

        this.synchronizer.setIntactClass(ComplexParameter.class);
        this.testNumber = 3;
        persist_jami();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_merge1() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(ParticipantEvidenceParameter.class);
        this.testNumber = 1;
        merge_test1();

        this.synchronizer.setIntactClass(InteractionEvidenceParameter.class);
        this.testNumber = 2;
        merge_test1();

        this.synchronizer.setIntactClass(ComplexParameter.class);
        this.testNumber = 3;
        merge_test1();
    }


    @Transactional
    @Test
    @DirtiesContext
    public void test_merge2() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(ParticipantEvidenceParameter.class);
        this.testNumber = 1;
        merge_test2();

        this.synchronizer.setIntactClass(InteractionEvidenceParameter.class);
        this.testNumber = 2;
        merge_test2();

        this.synchronizer.setIntactClass(ComplexParameter.class);
        this.testNumber = 3;
        merge_test2();
    }


    private IntactCvTerm createExistingType() {
        // pre persist kd
        IntactCvTerm kd = new IntactCvTerm("kd");
        kd.setObjClass(IntactUtils.PARAMETER_TYPE_OBJCLASS);
        entityManager.persist(kd);
        IntactCvTerm psimi = new IntactCvTerm(CvTerm.PSI_MI);
        psimi.setObjClass(IntactUtils.DATABASE_OBJCLASS);
        entityManager.persist(psimi);
        IntactCvTerm identity = new IntactCvTerm(Xref.IDENTITY);
        identity.setObjClass(IntactUtils.QUALIFIER_OBJCLASS);
        entityManager.persist(identity);

        CvTermXref ref1 = new CvTermXref(psimi, "MI:xxx1", identity);
        kd.getDbXrefs().add(ref1);
        CvTermXref ref2 = new CvTermXref(psimi, CvTerm.PSI_MI, identity);
        psimi.getDbXrefs().add(ref2);
        CvTermXref ref3 = new CvTermXref(psimi, Xref.IDENTITY_MI, identity);
        identity.getDbXrefs().add(ref3);
        entityManager.flush();
        this.context.clearCache();
        return kd;
    }

    @Override
    protected void testDeleteOtherProperties(AbstractIntactParameter objectToTest) {
        // nothing to do
    }

    @Override
    protected Parameter createDefaultJamiObject() {
        if (testNumber == 1){
            return IntactTestUtils.
                    createKdParameterNoUnit();
        }
        else if (testNumber == 2){
            return IntactTestUtils.
                    createKdParameter();
        }
        else{
            return IntactTestUtils.
                    createParameterNoUnit("molecular weight", "MI:xxx2", 6);
        }
    }

    @Override
    protected void testUpdatedPropertiesAfterMerge(AbstractIntactParameter objectToTest, AbstractIntactParameter newObjToTest) {
        Assert.assertEquals(objectToTest.getAc(), newObjToTest.getAc());
        Assert.assertEquals(new BigDecimal(3), newObjToTest.getUncertainty());
    }

    @Override
    protected void updatePropertieDetachedInstance(AbstractIntactParameter objectToTest) {
        objectToTest.setUncertainty(new BigDecimal(3));
    }

    @Override
    protected AbstractIntactParameter findObject(AbstractIntactParameter objectToTest) {
        if (testNumber == 1){
            return entityManager.find(ParticipantEvidenceParameter.class, objectToTest.getAc());
        }
        else if (testNumber == 2){
            return entityManager.find(InteractionEvidenceParameter.class, objectToTest.getAc());
        }
        else{
            return entityManager.find(ComplexParameter.class, objectToTest.getAc());
        }
    }

    @Override
    protected void initSynchronizer() {
        this.synchronizer = new ParameterSynchronizerTemplate(this.context, AbstractIntactParameter.class);
    }

    @Override
    protected void testPersistedProperties(AbstractIntactParameter persistedObject) {
        if (testNumber == 1){
            Assert.assertNotNull(persistedObject.getAc());
            Assert.assertNull(persistedObject.getUnit());
            Assert.assertNotNull(persistedObject.getType());
            IntactCvTerm paramType = (IntactCvTerm)persistedObject.getType();
            Assert.assertNotNull(paramType.getAc());
            Assert.assertEquals(persistedObject.getType(),IntactUtils.createMIParameterType("kd", "MI:xxx1"));
            Assert.assertEquals(new ParameterValue(new BigDecimal(3)), persistedObject.getValue());
        }
        else if (testNumber == 2){
            Assert.assertNotNull(persistedObject.getAc());
            Assert.assertNotNull(persistedObject.getType());
            Assert.assertNotNull(persistedObject.getUnit());
            IntactCvTerm paramUnit = (IntactCvTerm)persistedObject.getUnit();
            Assert.assertNotNull(paramUnit.getAc());
            Assert.assertEquals(persistedObject.getUnit(),IntactUtils.createMIUnit("molar", "MI:xxx3"));
            Assert.assertEquals(new ParameterValue(new BigDecimal(5)), persistedObject.getValue());
        }
        else{
            Assert.assertNotNull(persistedObject.getAc());
            Assert.assertNotNull(persistedObject.getType());
            IntactCvTerm paramType2 = (IntactCvTerm)persistedObject.getType();
            Assert.assertNotNull(paramType2.getAc());
            Assert.assertEquals(persistedObject.getType(), IntactUtils.createMIParameterType("molecular weight", "MI:xxx2"));
            Assert.assertEquals(new ParameterValue(new BigDecimal(6)), persistedObject.getValue());
        }
    }

    @Override
    protected void testNonPersistedProperties(AbstractIntactParameter persistedObject) {
        if (testNumber == 1){
            Assert.assertNull(persistedObject.getAc());
            Assert.assertNull(persistedObject.getUnit());
            Assert.assertNotNull(persistedObject.getType());
            IntactCvTerm paramType = (IntactCvTerm)persistedObject.getType();
            Assert.assertNotNull(paramType.getAc());
            Assert.assertEquals(persistedObject.getType(),IntactUtils.createMIParameterType("kd", "MI:xxx1"));
            Assert.assertEquals(new ParameterValue(new BigDecimal(3)), persistedObject.getValue());
        }
        else if (testNumber == 2){
            Assert.assertNull(persistedObject.getAc());
            Assert.assertNotNull(persistedObject.getType());
            Assert.assertNotNull(persistedObject.getUnit());
            IntactCvTerm paramUnit = (IntactCvTerm)persistedObject.getUnit();
            Assert.assertNotNull(paramUnit.getAc());
            Assert.assertEquals(persistedObject.getUnit(),IntactUtils.createMIUnit("molar", "MI:xxx3"));
            Assert.assertEquals(new ParameterValue(new BigDecimal(5)), persistedObject.getValue());
        }
        else{
            Assert.assertNull(persistedObject.getAc());
            Assert.assertNotNull(persistedObject.getType());
            IntactCvTerm paramType2 = (IntactCvTerm)persistedObject.getType();
            Assert.assertNotNull(paramType2.getAc());
            Assert.assertEquals(persistedObject.getType(),IntactUtils.createMIParameterType("molecular weight", "MI:xxx2"));
            Assert.assertEquals(new ParameterValue(new BigDecimal(6)), persistedObject.getValue());
        }
    }

    @Override
    protected AbstractIntactParameter createDefaultObject() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (testNumber == 1){
            return IntactTestUtils.
                    createKdParameterNoUnit(ParticipantEvidenceParameter.class);
        }
        else if (testNumber == 2){
            return IntactTestUtils.
                    createKdParameter(InteractionEvidenceParameter.class);
        }
        else{
            return IntactTestUtils.
                    createParameterNoUnit(ComplexParameter.class, "molecular weight", "MI:xxx2", 6);
        }
    }
}
