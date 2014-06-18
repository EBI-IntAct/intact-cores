package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import psidev.psi.mi.jami.model.Feature;
import psidev.psi.mi.jami.model.ModelledParticipant;
import psidev.psi.mi.jami.model.Participant;
import psidev.psi.mi.jami.model.impl.DefaultStoichiometry;
import uk.ac.ebi.intact.jami.IntactTestUtils;
import uk.ac.ebi.intact.jami.model.extension.*;

import java.lang.reflect.InvocationTargetException;

/**
 * Unit test for ModelledFeatureSynchronizerTemplate
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/02/14</pre>
 */
public class ModelledParticipantSynchronizerTest extends ParticipantSynchronizerTemplateTest{

    @Override
    protected void testDeleteOtherProperties(AbstractIntactParticipant objectToTest) {
        super.testDeleteOtherProperties(objectToTest);
        Assert.assertNull(entityManager.find(ModelledParticipantXref.class, ((ModelledParticipantXref) objectToTest.getXrefs().iterator().next()).getAc()));
        Assert.assertNull(entityManager.find(ModelledParticipantAlias.class, ((ModelledParticipantAlias) objectToTest.getAliases().iterator().next()).getAc()));
        Assert.assertNull(entityManager.find(ModelledParticipantAnnotation.class, ((ModelledParticipantAnnotation) objectToTest.getAnnotations().iterator().next()).getAc()));
    }

    @Override
    protected Participant createDefaultJamiObject() {
        ModelledParticipant f = null;
        try {
            f = IntactTestUtils.createModelledParticipant();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return f;
    }

    @Override
    protected AbstractIntactParticipant findObject(AbstractIntactParticipant objectToTest) {
        return entityManager.find(IntactModelledParticipant.class, objectToTest.getAc());
    }

    @Override
    protected void initSynchronizer() {
        this.synchronizer = new ModelledParticipantSynchronizer(this.context);
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
        AbstractIntactParticipant f= IntactTestUtils.createIntactModelledParticipant();
        return f;
    }
}
