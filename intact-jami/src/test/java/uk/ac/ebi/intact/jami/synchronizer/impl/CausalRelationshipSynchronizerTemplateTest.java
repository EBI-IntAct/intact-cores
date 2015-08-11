package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.CausalRelationship;
import psidev.psi.mi.jami.model.ParticipantEvidence;
import uk.ac.ebi.intact.jami.IntactTestUtils;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Unit test for ParameterSynchronizerTemplate
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/02/14</pre>
 */
public class CausalRelationshipSynchronizerTemplateTest extends AbstractDbSynchronizerTest<CausalRelationship, AbstractIntactCausalRelationship>{

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
        find_no_cache();
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
    public void test_synchronize_jami() throws PersisterException, FinderException, SynchronizerException {
        persist_jami();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_merge1() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        merge_test1();
    }


    @Transactional
    @Test
    @DirtiesContext
    public void test_merge2() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        merge_test2();
    }

    @Override
    protected void testDeleteOtherProperties(AbstractIntactCausalRelationship objectToTest) {
        Assert.assertNotNull(entityManager.find(IntactParticipantEvidence.class, ((AbstractIntactParticipant)objectToTest.getTarget()).getAc()));
    }

    @Override
    protected CausalRelationship createDefaultJamiObject() {
        try {
            CausalRelationship causal = IntactTestUtils.createCausalRelationship();
            this.context.getParticipantEvidenceSynchronizer().synchronize((ParticipantEvidence)causal.getTarget(), true);

            return causal;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (FinderException e) {
            e.printStackTrace();
        } catch (SynchronizerException e) {
            e.printStackTrace();
        } catch (PersisterException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void testUpdatedPropertiesAfterMerge(AbstractIntactCausalRelationship objectToTest, AbstractIntactCausalRelationship newObjToTest) {
        Assert.assertEquals(objectToTest.getId(), newObjToTest.getId());
        Assert.assertNotNull(((IntactCvTerm)newObjToTest.getRelationType()).getAc());
        Assert.assertEquals("new relationship", newObjToTest.getRelationType().getShortName());
    }

    @Override
    protected void updatePropertieDetachedInstance(AbstractIntactCausalRelationship objectToTest) {
        objectToTest.setRelationType(IntactUtils.createMITopic("new relationship", null));
    }

    @Override
    protected AbstractIntactCausalRelationship findObject(AbstractIntactCausalRelationship objectToTest) {
        return entityManager.find(ExperimentalCausalRelationship.class, objectToTest.getId());
    }

    @Override
    protected void initSynchronizer() {
        this.synchronizer = new CausalRelationchipSynchronizerTemplate(this.context, ExperimentalCausalRelationship.class);
    }

    @Override
    protected void testPersistedProperties(AbstractIntactCausalRelationship persistedObject) {
        Assert.assertNotNull(persistedObject.getId());
        Assert.assertNotNull(persistedObject.getRelationType());
        IntactCvTerm paramType = (IntactCvTerm)persistedObject.getRelationType();
        Assert.assertNotNull(paramType.getAc());
        Assert.assertNotNull(persistedObject.getTarget());
        Assert.assertNotNull(((AbstractIntactParticipant) persistedObject.getTarget()).getAc());
    }

    @Override
    protected void testNonPersistedProperties(AbstractIntactCausalRelationship persistedObject) {
        Assert.assertNull(persistedObject.getId());
        Assert.assertNotNull(persistedObject.getRelationType());
        IntactCvTerm paramType = (IntactCvTerm)persistedObject.getRelationType();
        Assert.assertNotNull(paramType.getAc());
        Assert.assertNotNull(persistedObject.getTarget());
        Assert.assertNotNull(((AbstractIntactParticipant) persistedObject.getTarget()).getAc());
    }

    @Override
    protected AbstractIntactCausalRelationship createDefaultObject() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        AbstractIntactCausalRelationship causal = IntactTestUtils.createExperimentalCausalRelationship();
        try {
            this.context.getParticipantEvidenceSynchronizer().synchronize((ParticipantEvidence)causal.getTarget(), true);
        } catch (FinderException e) {
            e.printStackTrace();
        } catch (PersisterException e) {
            e.printStackTrace();
        } catch (SynchronizerException e) {
            e.printStackTrace();
        }
        return causal;
    }
}
