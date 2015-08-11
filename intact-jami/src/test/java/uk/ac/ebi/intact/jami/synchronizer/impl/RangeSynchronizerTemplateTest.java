package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Range;
import psidev.psi.mi.jami.model.impl.DefaultPosition;
import uk.ac.ebi.intact.jami.IntactTestUtils;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactRange;
import uk.ac.ebi.intact.jami.model.extension.ExperimentalRange;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactPosition;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.lang.reflect.InvocationTargetException;

/**
 * Unit test for RangeSynchronizerTemplate
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/02/14</pre>
 */
public class RangeSynchronizerTemplateTest extends AbstractDbSynchronizerTest<Range, AbstractIntactRange>{

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

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_jami() throws PersisterException, FinderException, SynchronizerException {
        persist_jami();
    }

    @Override
    protected void testDeleteOtherProperties(AbstractIntactRange objectToTest) {
         // nothing to do
    }

    @Override
    protected Range createDefaultJamiObject() {
        return IntactTestUtils.createCertainRangeNoResultingSequence();
    }

    @Override
    protected void testUpdatedPropertiesAfterMerge(AbstractIntactRange objectToTest, AbstractIntactRange newObjToTest) {
        Assert.assertEquals(objectToTest.getAc(), newObjToTest.getAc());
        Assert.assertEquals(new DefaultPosition(2), newObjToTest.getStart());
        Assert.assertEquals(new DefaultPosition(3), newObjToTest.getEnd());
    }

    @Override
    protected void updatePropertieDetachedInstance(AbstractIntactRange objectToTest) {
        objectToTest.setPositions(new IntactPosition(2), new IntactPosition(3));
    }

    @Override
    protected AbstractIntactRange findObject(AbstractIntactRange objectToTest) {
        return entityManager.find(ExperimentalRange.class, objectToTest.getAc());
    }

    @Override
    protected void initSynchronizer() {
        this.synchronizer = new RangeSynchronizerTemplate(this.context, ExperimentalRange.class);
    }

    @Override
    protected void testPersistedProperties(AbstractIntactRange persistedObject) {
        Assert.assertNotNull(persistedObject.getAc());
        Assert.assertEquals(new DefaultPosition(1), persistedObject.getStart());
        Assert.assertEquals(new DefaultPosition(2), persistedObject.getEnd());
        Assert.assertFalse(persistedObject.isLink());
        Assert.assertNull(persistedObject.getResultingSequence());
        Assert.assertNotNull(((IntactCvTerm) persistedObject.getStart().getStatus()).getAc());
        Assert.assertNotNull(((IntactCvTerm) persistedObject.getEnd().getStatus()).getAc());
    }

    @Override
    protected void testNonPersistedProperties(AbstractIntactRange objectToTest) {
        Assert.assertNull(objectToTest.getAc());
        Assert.assertEquals(new DefaultPosition(1), objectToTest.getStart());
        Assert.assertEquals(new DefaultPosition(2), objectToTest.getEnd());
        Assert.assertFalse(objectToTest.isLink());
        Assert.assertNull(objectToTest.getResultingSequence());
        Assert.assertNotNull(((IntactCvTerm) objectToTest.getStart().getStatus()).getAc());
        Assert.assertNotNull(((IntactCvTerm) objectToTest.getEnd().getStatus()).getAc());
    }

    @Override
    protected AbstractIntactRange createDefaultObject() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return IntactTestUtils.createCertainRangeNoResultingSequence(ExperimentalRange.class);
    }
}
