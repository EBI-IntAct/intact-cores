package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import psidev.psi.mi.jami.model.Range;
import psidev.psi.mi.jami.model.impl.DefaultPosition;
import psidev.psi.mi.jami.model.impl.DefaultResultingSequence;
import uk.ac.ebi.intact.jami.IntactTestUtils;
import uk.ac.ebi.intact.jami.model.extension.*;

import java.lang.reflect.InvocationTargetException;

/**
 * Unit test for ModelledRangeSynchronizer
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/02/14</pre>
 */
public class ModelledRangeSynchronizerTest extends RangeSynchronizerTemplateTest{

    @Override
    protected void testUpdatedPropertiesAfterMerge(AbstractIntactRange objectToTest, AbstractIntactRange newObjToTest) {
        super.testUpdatedPropertiesAfterMerge(objectToTest, newObjToTest);
        Assert.assertEquals(new DefaultResultingSequence("AAGA", "TCGA"), newObjToTest.getResultingSequence());
    }

    @Override
    protected void updatePropertieDetachedInstance(AbstractIntactRange objectToTest) {
        super.updatePropertieDetachedInstance(objectToTest);
        objectToTest.setResultingSequence(new ModelledResultingSequence("AAGA", "TCGA"));
    }

    @Override
    protected AbstractIntactRange findObject(AbstractIntactRange objectToTest) {
        return entityManager.find(ModelledRange.class, objectToTest.getAc());
    }

    @Override
    protected void initSynchronizer() {
        this.synchronizer = new ModelledRangeSynchronizer(this.context);
    }

    @Override
    protected Range createDefaultJamiObject() {
        return IntactTestUtils.createCertainRangeWithResultingSequence();
    }

    @Override
    protected void testPersistedProperties(AbstractIntactRange persistedObject) {
        Assert.assertNotNull(persistedObject.getAc());
        Assert.assertEquals(new DefaultPosition(1), persistedObject.getStart());
        Assert.assertEquals(new DefaultPosition(2), persistedObject.getEnd());
        Assert.assertFalse(persistedObject.isLink());
        Assert.assertNotNull(persistedObject.getResultingSequence());
        Assert.assertEquals("AAGA", persistedObject.getResultingSequence().getOriginalSequence());
        Assert.assertEquals("ACGA", persistedObject.getResultingSequence().getNewSequence());
        Assert.assertNotNull(((IntactCvTerm) persistedObject.getStart().getStatus()).getAc());
        Assert.assertNotNull(((IntactCvTerm) persistedObject.getEnd().getStatus()).getAc());
    }

    @Override
    protected void testNonPersistedProperties(AbstractIntactRange objectToTest) {
        Assert.assertNull(objectToTest.getAc());
        Assert.assertEquals(new DefaultPosition(1), objectToTest.getStart());
        Assert.assertEquals(new DefaultPosition(2), objectToTest.getEnd());
        Assert.assertFalse(objectToTest.isLink());
        Assert.assertNotNull(objectToTest.getResultingSequence());
        Assert.assertEquals("AAGA", objectToTest.getResultingSequence().getOriginalSequence());
        Assert.assertEquals("ACGA", objectToTest.getResultingSequence().getNewSequence());
        Assert.assertNotNull(((IntactCvTerm) objectToTest.getStart().getStatus()).getAc());
        Assert.assertNotNull(((IntactCvTerm) objectToTest.getEnd().getStatus()).getAc());
    }

    @Override
    protected AbstractIntactRange createDefaultObject() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return IntactTestUtils.createModelledRangeWithResultingSequence();
    }
}
