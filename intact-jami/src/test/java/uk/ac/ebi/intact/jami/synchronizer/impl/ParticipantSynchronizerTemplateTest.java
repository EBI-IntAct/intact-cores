package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import org.hibernate.Hibernate;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Feature;
import psidev.psi.mi.jami.model.Participant;
import psidev.psi.mi.jami.model.impl.DefaultStoichiometry;
import uk.ac.ebi.intact.jami.IntactTestUtils;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Unit test for ParticipantSynchronizerTemplate
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/02/14</pre>
 */
public class ParticipantSynchronizerTemplateTest extends AbstractDbSynchronizerTest<Participant, AbstractIntactParticipant>{

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
    protected void testDeleteOtherProperties(AbstractIntactParticipant objectToTest) {
        Assert.assertTrue(objectToTest.getFeatures().isEmpty());
    }

    @Override
    protected Participant createDefaultJamiObject() {
        try {
            return IntactTestUtils.createBasicModelledParticipant();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void testUpdatedPropertiesAfterMerge(AbstractIntactParticipant objectToTest, AbstractIntactParticipant newObjToTest) {
        Assert.assertEquals(objectToTest.getAc(), newObjToTest.getAc());
        Assert.assertEquals("new role", newObjToTest.getBiologicalRole().getShortName());
        Assert.assertNotNull(((IntactCvTerm)newObjToTest.getBiologicalRole()).getAc());
        Assert.assertEquals(2, newObjToTest.getFeatures().size());
    }

    @Override
    protected void updatePropertieDetachedInstance(AbstractIntactParticipant objectToTest) {
        objectToTest.setBiologicalRole(IntactUtils.createMIBiologicalRole("new role", null));
        objectToTest.addFeature(IntactTestUtils.createBasicModelledFeature("type 2", "MI:xxx5"));
    }

    @Override
    protected AbstractIntactParticipant findObject(AbstractIntactParticipant objectToTest) {
        return entityManager.find(IntactModelledParticipant.class, objectToTest.getAc());
    }

    @Override
    protected void initSynchronizer() {
        this.synchronizer = new ParticipantSynchronizerTemplate(this.context, IntactModelledParticipant.class);
    }

    @Override
    protected void testPersistedProperties(AbstractIntactParticipant objectToTest) {
        Assert.assertNotNull(objectToTest.getAc());
        Assert.assertNotNull(objectToTest.getInteractor());
        Assert.assertNotNull(((IntactInteractor)objectToTest.getInteractor()).getAc());
        Assert.assertNotNull(objectToTest.getBiologicalRole());
        Assert.assertNotNull(((IntactCvTerm)objectToTest.getBiologicalRole()).getAc());
        Assert.assertEquals(1, objectToTest.getFeatures().size());
        Feature f = (Feature)objectToTest.getFeatures().iterator().next();
        Assert.assertNotNull(((AbstractIntactFeature) f).getAc());
        Assert.assertEquals(new DefaultStoichiometry(2), objectToTest.getStoichiometry());
    }

    @Override
    protected void testNonPersistedProperties(AbstractIntactParticipant objectToTest) {
        Assert.assertNull(objectToTest.getAc());
        Assert.assertNotNull(objectToTest.getInteractor());
        Assert.assertNotNull(((IntactInteractor)objectToTest.getInteractor()).getAc());
        Assert.assertNotNull(objectToTest.getBiologicalRole());
        Assert.assertNotNull(((IntactCvTerm)objectToTest.getBiologicalRole()).getAc());
        Assert.assertEquals(1, objectToTest.getFeatures().size());
        Feature f = (Feature)objectToTest.getFeatures().iterator().next();
        Assert.assertNull(((AbstractIntactFeature) f).getAc());
        Assert.assertEquals(new DefaultStoichiometry(2), objectToTest.getStoichiometry());
    }

    @Override
    protected AbstractIntactParticipant createDefaultObject() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        IntactModelledParticipant part =  IntactTestUtils.createBasicIntactModelledParticipant();
        try {
            context.getExperimentalRoleSynchronizer().synchronize(IntactUtils.createMIExperimentalRole(Participant.NEUTRAL, Participant.NEUTRAL_MI), true);
        } catch (FinderException e) {
            e.printStackTrace();
        } catch (PersisterException e) {
            e.printStackTrace();
        } catch (SynchronizerException e) {
            e.printStackTrace();
        }
        return part;
    }

    @Override
    protected void initPropertiesBeforeDetaching(AbstractIntactParticipant reloadedObject){
        Hibernate.initialize(reloadedObject.getFeatures());
    }
}
