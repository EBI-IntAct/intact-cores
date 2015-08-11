package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import psidev.psi.mi.jami.model.Feature;
import psidev.psi.mi.jami.model.Participant;
import psidev.psi.mi.jami.model.ParticipantEvidence;
import psidev.psi.mi.jami.model.impl.DefaultStoichiometry;
import uk.ac.ebi.intact.jami.IntactTestUtils;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Unit test for FeatureEvidenceSynchronizer
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/02/14</pre>
 */
public class ParticipantEvidenceSynchronizerTest extends ParticipantSynchronizerTemplateTest {

    @Override
    protected void testDeleteOtherProperties(AbstractIntactParticipant objectToTest) {
        super.testDeleteOtherProperties(objectToTest);
        Assert.assertNull(entityManager.find(ParticipantEvidenceXref.class, ((ParticipantEvidenceXref) objectToTest.getXrefs().iterator().next()).getAc()));
        Assert.assertNull(entityManager.find(ParticipantEvidenceAlias.class, ((ParticipantEvidenceAlias) objectToTest.getAliases().iterator().next()).getAc()));
        Assert.assertNull(entityManager.find(ParticipantEvidenceAnnotation.class, ((ParticipantEvidenceAnnotation) objectToTest.getAnnotations().iterator().next()).getAc()));
    }

    @Override
    protected Participant createDefaultJamiObject() {
        ParticipantEvidence f = null;
        try {
            f = IntactTestUtils.createParticipantEvidence();
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
    protected void updatePropertieDetachedInstance(AbstractIntactParticipant objectToTest) {
        objectToTest.setBiologicalRole(IntactUtils.createMIBiologicalRole("new role", null));
        objectToTest.addFeature(IntactTestUtils.createBasicFeatureEvidence("type 2", "MI:xxx5"));
    }

    @Override
    protected AbstractIntactParticipant findObject(AbstractIntactParticipant objectToTest) {
        return entityManager.find(IntactParticipantEvidence.class, objectToTest.getAc());
    }

    @Override
    protected void initSynchronizer() {
        this.synchronizer = new ParticipantEvidenceSynchronizer(this.context);
    }

    @Override
    protected void testPersistedProperties(AbstractIntactParticipant objectToTest) {
        Assert.assertNotNull(objectToTest.getAc());
        Assert.assertNotNull(objectToTest.getInteractor());
        Assert.assertNotNull(((IntactInteractor) objectToTest.getInteractor()).getAc());
        Assert.assertNotNull(objectToTest.getBiologicalRole());
        Assert.assertNotNull(((IntactCvTerm) objectToTest.getBiologicalRole()).getAc());
        Assert.assertEquals(1, objectToTest.getFeatures().size());
        Feature f = (Feature) objectToTest.getFeatures().iterator().next();
        Assert.assertNotNull(((AbstractIntactFeature) f).getAc());
        Assert.assertEquals(new DefaultStoichiometry(2), objectToTest.getStoichiometry());
    }

    @Override
    protected void testNonPersistedProperties(AbstractIntactParticipant objectToTest) {
        Assert.assertNull(objectToTest.getAc());
        Assert.assertNotNull(objectToTest.getInteractor());
        Assert.assertNotNull(((IntactInteractor) objectToTest.getInteractor()).getAc());
        Assert.assertNotNull(objectToTest.getBiologicalRole());
        Assert.assertNotNull(((IntactCvTerm) objectToTest.getBiologicalRole()).getAc());
        Assert.assertEquals(1, objectToTest.getFeatures().size());
        Feature f = (Feature) objectToTest.getFeatures().iterator().next();
        Assert.assertNull(((AbstractIntactFeature) f).getAc());
        Assert.assertEquals(new DefaultStoichiometry(2), objectToTest.getStoichiometry());
    }

    @Override
    protected AbstractIntactParticipant createDefaultObject() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        AbstractIntactParticipant f = IntactTestUtils.createIntactParticipantEvidence();
        return f;
    }
}
