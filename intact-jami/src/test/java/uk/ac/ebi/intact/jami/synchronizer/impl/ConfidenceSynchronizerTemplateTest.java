package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Confidence;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import psidev.psi.mi.jami.model.impl.DefaultConfidence;
import psidev.psi.mi.jami.model.impl.DefaultModelledConfidence;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

/**
 * Unit test for ConfidenceSynchronizerTemplate
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/02/14</pre>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/intact-jami-test.spring.xml"})
@Transactional
@TransactionConfiguration
@DirtiesContext
public class ConfidenceSynchronizerTemplateTest {

    @PersistenceContext(unitName = "intact-core")
    private EntityManager entityManager;
    @PersistenceUnit(unitName = "intact-core", name = "intactEntityManagerFactory")
    private EntityManagerFactory intactEntityManagerFactory;

    private ConfidenceSynchronizerTemplate synchronizer;
    private SynchronizerContext context;

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_all() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ConfidenceSynchronizerTemplate(this.context, AbstractIntactConfidence.class);

        ParticipantEvidenceConfidence participantConfidence = new ParticipantEvidenceConfidence(IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"), "high");

        InteractionEvidenceConfidence interactionConfidence = new InteractionEvidenceConfidence(IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"), "low");

        ComplexConfidence complexConfidence = new ComplexConfidence(IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"), "0.5");

        this.synchronizer.setIntactClass(ParticipantEvidenceConfidence.class);
        this.synchronizer.persist(participantConfidence);

        Assert.assertNotNull(participantConfidence.getAc());
        Assert.assertNotNull(participantConfidence.getType());
        IntactCvTerm confType = (IntactCvTerm)participantConfidence.getType();
        Assert.assertNotNull(confType.getAc());
        Assert.assertEquals(participantConfidence.getType(),IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"));
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
        Assert.assertTrue(complexConfidence.getType() == aliasType2);
        Assert.assertEquals("0.5", complexConfidence.getValue());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_with_existing_type() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ConfidenceSynchronizerTemplate(this.context, AbstractIntactConfidence.class);

        IntactCvTerm authorScore = createExistingType();

        ParticipantEvidenceConfidence participantConfidence = new ParticipantEvidenceConfidence(IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"), "high");

        InteractionEvidenceConfidence interactionConfidence = new InteractionEvidenceConfidence(IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"), "low");

        ComplexConfidence complexConfidence = new ComplexConfidence(IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"), "0.5");

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
    public void test_persist_with_detached_type() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ConfidenceSynchronizerTemplate(this.context, AbstractIntactConfidence.class);

        // pre persist alias synonym
        IntactCvTerm authorScore = createExistingType();

        entityManager.detach(authorScore);

        ParticipantEvidenceConfidence participantConfidence = new ParticipantEvidenceConfidence(authorScore, "high");

        InteractionEvidenceConfidence interactionConfidence = new InteractionEvidenceConfidence(authorScore, "low");

        ComplexConfidence complexConfidence = new ComplexConfidence(authorScore, "0.5");

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
    public void test_confidence_deleted() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ConfidenceSynchronizerTemplate(this.context, AbstractIntactConfidence.class);

        // pre persist author score
        IntactCvTerm authorScore = createExistingType();

        entityManager.detach(authorScore);

        ParticipantEvidenceConfidence participantConfidence = new ParticipantEvidenceConfidence(authorScore, "high");

        this.synchronizer.setIntactClass(ParticipantEvidenceConfidence.class);
        this.synchronizer.persist(participantConfidence);

        Assert.assertNotNull(participantConfidence.getType());
        IntactCvTerm aliasType = (IntactCvTerm)participantConfidence.getType();
        Assert.assertEquals(aliasType.getAc(), authorScore.getAc());

        entityManager.flush();
        System.out.println("flush");

        this.synchronizer.delete(participantConfidence);

        Assert.assertNull(entityManager.find(ParticipantEvidenceConfidence.class, participantConfidence.getAc()));
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_find() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ConfidenceSynchronizerTemplate(this.context, AbstractIntactConfidence.class);

        ParticipantEvidenceConfidence participantConfidenceNotPersisted = new ParticipantEvidenceConfidence(IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"), "high");
        ParticipantEvidenceConfidence participantConfidencePersisted = new ParticipantEvidenceConfidence(IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"), "high");
        this.synchronizer.persist(participantConfidencePersisted);
        entityManager.flush();
        this.context.clearCache();

        Assert.assertNull(this.synchronizer.find(participantConfidenceNotPersisted));
        Assert.assertNull(this.synchronizer.find(participantConfidencePersisted));
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_properties() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ConfidenceSynchronizerTemplate(this.context, AbstractIntactConfidence.class);

        ParticipantEvidenceConfidence participantConfidence = new ParticipantEvidenceConfidence(IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"), "high");

        InteractionEvidenceConfidence interactionConfidence = new InteractionEvidenceConfidence(IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"), "low");

        ComplexConfidence complexConfidence = new ComplexConfidence(IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"), "0.5");

        this.synchronizer.setIntactClass(ParticipantEvidenceConfidence.class);
        this.synchronizer.synchronizeProperties(participantConfidence);

        Assert.assertNull(participantConfidence.getAc());
        Assert.assertNotNull(participantConfidence.getType());
        IntactCvTerm confType = (IntactCvTerm)participantConfidence.getType();
        Assert.assertNotNull(confType.getAc());
        Assert.assertEquals(participantConfidence.getType(), IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"));
        Assert.assertEquals("high", participantConfidence.getValue());

        this.synchronizer.setIntactClass(InteractionEvidenceConfidence.class);
        this.synchronizer.synchronizeProperties(interactionConfidence);

        Assert.assertNull(interactionConfidence.getAc());
        Assert.assertNotNull(interactionConfidence.getType());
        Assert.assertEquals("low", interactionConfidence.getValue());

        this.synchronizer.setIntactClass(ComplexConfidence.class);
        this.synchronizer.synchronizeProperties(complexConfidence);

        Assert.assertNull(complexConfidence.getAc());
        Assert.assertNotNull(complexConfidence.getType());
        IntactCvTerm confType2 = (IntactCvTerm)complexConfidence.getType();
        Assert.assertNotNull(confType2.getAc());
        Assert.assertTrue(complexConfidence.getType() == confType2);
        Assert.assertEquals("0.5", complexConfidence.getValue());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_not_persist() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ConfidenceSynchronizerTemplate(this.context, AbstractIntactConfidence.class);

        ParticipantEvidenceConfidence participantConfidence = new ParticipantEvidenceConfidence(IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"), "high");

        InteractionEvidenceConfidence interactionConfidence = new InteractionEvidenceConfidence(IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"), "low");

        ComplexConfidence complexConfidence = new ComplexConfidence(IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"), "0.5");

        this.synchronizer.setIntactClass(ParticipantEvidenceConfidence.class);
        this.synchronizer.synchronize(participantConfidence, false);

        Assert.assertNull(participantConfidence.getAc());
        Assert.assertNotNull(participantConfidence.getType());
        IntactCvTerm aliasType = (IntactCvTerm)participantConfidence.getType();
        Assert.assertNotNull(aliasType.getAc());
        Assert.assertEquals(participantConfidence.getType(), IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"));
        Assert.assertEquals("high", participantConfidence.getValue());

        this.synchronizer.setIntactClass(InteractionEvidenceConfidence.class);
        this.synchronizer.synchronize(interactionConfidence, false);

        Assert.assertNull(interactionConfidence.getAc());
        Assert.assertNotNull(interactionConfidence.getType());
        Assert.assertEquals("low", interactionConfidence.getValue());

        this.synchronizer.setIntactClass(ComplexConfidence.class);
        this.synchronizer.synchronize(complexConfidence, false);

        Assert.assertNull(complexConfidence.getAc());
        Assert.assertNotNull(complexConfidence.getType());
        IntactCvTerm confType2 = (IntactCvTerm)complexConfidence.getType();
        Assert.assertNotNull(confType2.getAc());
        Assert.assertTrue(complexConfidence.getType() == confType2);
        Assert.assertEquals("0.5", complexConfidence.getValue());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_persist() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ConfidenceSynchronizerTemplate(this.context, AbstractIntactConfidence.class);

        ParticipantEvidenceConfidence participantConfidence = new ParticipantEvidenceConfidence(IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"), "high");

        InteractionEvidenceConfidence interactionConfidence = new InteractionEvidenceConfidence(IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"), "low");

        ComplexConfidence complexConfidence = new ComplexConfidence(IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"), "0.5");

        this.synchronizer.setIntactClass(ParticipantEvidenceConfidence.class);
        this.synchronizer.synchronize(participantConfidence, true);

        Assert.assertNotNull(participantConfidence.getAc());
        Assert.assertNotNull(participantConfidence.getType());
        IntactCvTerm aliasType = (IntactCvTerm)participantConfidence.getType();
        Assert.assertNotNull(aliasType.getAc());
        Assert.assertEquals(participantConfidence.getType(), IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"));
        Assert.assertEquals("high", participantConfidence.getValue());

        this.synchronizer.setIntactClass(InteractionEvidenceConfidence.class);
        this.synchronizer.synchronize(interactionConfidence, true);

        Assert.assertNotNull(interactionConfidence.getAc());
        Assert.assertNotNull(interactionConfidence.getType());
        Assert.assertEquals("low", interactionConfidence.getValue());

        this.synchronizer.setIntactClass(ComplexConfidence.class);
        this.synchronizer.synchronize(complexConfidence, true);

        Assert.assertNotNull(complexConfidence.getAc());
        Assert.assertNotNull(complexConfidence.getType());
        IntactCvTerm confType2 = (IntactCvTerm)complexConfidence.getType();
        Assert.assertNotNull(confType2.getAc());
        Assert.assertTrue(complexConfidence.getType() == confType2);
        Assert.assertEquals("0.5", complexConfidence.getValue());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_jami() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ConfidenceSynchronizerTemplate(this.context, AbstractIntactConfidence.class);

        Confidence participantConfidence = new DefaultConfidence(IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"), "high");

        Confidence interactionConfidence = new DefaultConfidence(IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"), "low");

        Confidence complexConfidence = new DefaultModelledConfidence(IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"), "0.5");

        this.synchronizer.setIntactClass(ParticipantEvidenceConfidence.class);
        ParticipantEvidenceConfidence newConf = (ParticipantEvidenceConfidence)this.synchronizer.synchronize(participantConfidence, true);

        Assert.assertNotNull(newConf.getAc());
        Assert.assertNotNull(newConf.getType());
        IntactCvTerm aliasType = (IntactCvTerm)newConf.getType();
        Assert.assertNotNull(aliasType.getAc());
        Assert.assertEquals(participantConfidence.getType(), IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"));
        Assert.assertEquals("high", newConf.getValue());

        this.synchronizer.setIntactClass(InteractionEvidenceConfidence.class);
        InteractionEvidenceConfidence newConf2 = (InteractionEvidenceConfidence)this.synchronizer.synchronize(interactionConfidence, true);

        Assert.assertNotNull(newConf2.getAc());
        Assert.assertNotNull(newConf2.getType());
        Assert.assertEquals("low", newConf2.getValue());

        this.synchronizer.setIntactClass(ComplexConfidence.class);
        ComplexConfidence newConf3 = (ComplexConfidence)this.synchronizer.synchronize(complexConfidence, true);

        Assert.assertNotNull(newConf3.getAc());
        Assert.assertNotNull(newConf3.getType());
        IntactCvTerm confType2 = (IntactCvTerm)newConf3.getType();
        Assert.assertNotNull(confType2.getAc());
        Assert.assertTrue(newConf3.getType() == confType2);
        Assert.assertEquals("0.5", newConf3.getValue());
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
}
