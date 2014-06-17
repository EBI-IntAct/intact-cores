package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.jami.IntactTestUtils;
import uk.ac.ebi.intact.jami.model.AbstractLifecycleEvent;
import uk.ac.ebi.intact.jami.model.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.PublicationLifecycleEvent;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.lang.reflect.InvocationTargetException;

/**
 * Unit test for LifeCycleSynchronizerTemplate
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/02/14</pre>
 */
public class LifeCycleSynchronizerTemplateTest extends AbstractDbSynchronizerTest<LifeCycleEvent,AbstractLifecycleEvent>{

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
    public void test_synchronize_merge() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        merge_test1();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_merge2() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        merge_test2();
    }

    @Override
    protected void testDeleteOtherProperties(AbstractLifecycleEvent objectToTest) {
        // nothing to do here
    }

    @Override
    protected LifeCycleEvent createDefaultJamiObject() {
        return null;
    }

    @Override
    protected void testUpdatedPropertiesAfterMerge(AbstractLifecycleEvent objectToTest, AbstractLifecycleEvent newObjToTest) {
        Assert.assertEquals(objectToTest.getAc(), newObjToTest.getAc());
        Assert.assertEquals("new note", newObjToTest.getNote());
        Assert.assertEquals("new last name", newObjToTest.getWho().getLastName());
    }

    @Override
    protected void updatePropertieDetachedInstance(AbstractLifecycleEvent objectToTest) {
        objectToTest.setNote("new note");
        objectToTest.getWho().setLastName("new last name");
    }

    @Override
    protected AbstractLifecycleEvent findObject(AbstractLifecycleEvent objectToTest) {
        return entityManager.find(PublicationLifecycleEvent.class, objectToTest.getAc());
    }

    @Override
    protected void initSynchronizer() {
        this.synchronizer = new LifeCycleSynchronizerTemplate(this.context, PublicationLifecycleEvent.class);
    }

    @Override
    protected void testPersistedProperties(AbstractLifecycleEvent persistedObject) {
        Assert.assertNotNull(persistedObject.getAc());
        Assert.assertEquals("new", persistedObject.getEvent().getShortName());
        Assert.assertNotNull(((IntactCvTerm) persistedObject.getEvent()).getAc());
        Assert.assertEquals("default", persistedObject.getWho().getLogin());
        Assert.assertNotNull(persistedObject.getWho().getAc());
        Assert.assertEquals("new event", persistedObject.getNote());
        Assert.assertNotNull(persistedObject.getWhen());
    }

    @Override
    protected void testNonPersistedProperties(AbstractLifecycleEvent persistedObject) {
        Assert.assertNull(persistedObject.getAc());
        Assert.assertEquals("new", persistedObject.getEvent().getShortName());
        Assert.assertNotNull(((IntactCvTerm) persistedObject.getEvent()).getAc());
        Assert.assertEquals("default", persistedObject.getWho().getLogin());
        Assert.assertNotNull(persistedObject.getWho().getAc());
        Assert.assertEquals("new event", persistedObject.getNote());
        Assert.assertNotNull(persistedObject.getWhen());
    }

    @Override
    protected AbstractLifecycleEvent createDefaultObject() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        try {
            this.context.getUserSynchronizer().persist(IntactTestUtils.createCuratorUser());
        } catch (FinderException e) {
            e.printStackTrace();
        } catch (PersisterException e) {
            e.printStackTrace();
        } catch (SynchronizerException e) {
            e.printStackTrace();
        }
        return IntactTestUtils.createIntactNewLifeCycleEvent(PublicationLifecycleEvent.class);
    }
}