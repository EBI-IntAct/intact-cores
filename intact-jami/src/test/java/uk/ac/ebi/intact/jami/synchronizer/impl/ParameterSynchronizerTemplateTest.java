package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Parameter;
import psidev.psi.mi.jami.model.ParameterValue;
import psidev.psi.mi.jami.model.Xref;
import psidev.psi.mi.jami.model.impl.DefaultModelledParameter;
import psidev.psi.mi.jami.model.impl.DefaultParameter;
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
import java.math.BigDecimal;

/**
 * Unit test for ParameterSynchronizerTemplate
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
public class ParameterSynchronizerTemplateTest {

    @PersistenceContext(unitName = "intact-core")
    private EntityManager entityManager;
    @PersistenceUnit(unitName = "intact-core", name = "intactEntityManagerFactory")
    private EntityManagerFactory intactEntityManagerFactory;

    private ParameterSynchronizerTemplate synchronizer;
    private SynchronizerContext context;

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_all() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ParameterSynchronizerTemplate(this.context, AbstractIntactParameter.class);

        ParticipantEvidenceParameter participantParameter = new ParticipantEvidenceParameter(IntactUtils.createMIParameterType("kd", "MI:xxx1"), new ParameterValue(new BigDecimal(3)));

        InteractionEvidenceParameter interactionParameter = new InteractionEvidenceParameter(IntactUtils.createMIParameterType("kd", "MI:xxx1"), new ParameterValue(new BigDecimal(5)),
                IntactUtils.createMIUnit("molar", "MI:xxx3"));

        ComplexParameter complexParameter = new ComplexParameter(IntactUtils.createMIParameterType("molecular weight", "MI:xxx2"), new ParameterValue(new BigDecimal(6)));

        this.synchronizer.setIntactClass(ParticipantEvidenceParameter.class);
        this.synchronizer.persist(participantParameter);

        Assert.assertNotNull(participantParameter.getAc());
        Assert.assertNull(participantParameter.getUnit());
        Assert.assertNotNull(participantParameter.getType());
        IntactCvTerm paramType = (IntactCvTerm)participantParameter.getType();
        Assert.assertNotNull(paramType.getAc());
        Assert.assertEquals(participantParameter.getType(),IntactUtils.createMIParameterType("kd", "MI:xxx1"));
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
        IntactCvTerm paramType2 = (IntactCvTerm)complexParameter.getType();
        Assert.assertNotNull(paramType2.getAc());
        Assert.assertTrue(complexParameter.getType() == paramType2);
        Assert.assertEquals(new ParameterValue(new BigDecimal(6)), complexParameter.getValue());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_with_existing_type() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ParameterSynchronizerTemplate(this.context, AbstractIntactParameter.class);

        IntactCvTerm kdType = createExistingType();

        ParticipantEvidenceParameter participantParameter = new ParticipantEvidenceParameter(IntactUtils.createMIParameterType("kd", "MI:xxx1"), new ParameterValue(new BigDecimal(3)));

        InteractionEvidenceParameter interactionParameter = new InteractionEvidenceParameter(IntactUtils.createMIParameterType("kd", "MI:xxx1"), new ParameterValue(new BigDecimal(5)),
                IntactUtils.createMIUnit("molar", "MI:xxx3"));

        ComplexParameter complexParameter = new ComplexParameter(IntactUtils.createMIParameterType("molecular weight", "MI:xxx2"), new ParameterValue(new BigDecimal(6)));
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
    public void test_persist_with_detached_type() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ParameterSynchronizerTemplate(this.context, AbstractIntactParameter.class);

        // pre persist kd
        IntactCvTerm kdType = createExistingType();

        entityManager.detach(kdType);

        ParticipantEvidenceParameter participantParameter = new ParticipantEvidenceParameter(kdType, new ParameterValue(new BigDecimal(3)));

        InteractionEvidenceParameter interactionParameter = new InteractionEvidenceParameter(kdType, new ParameterValue(new BigDecimal(5)),
                IntactUtils.createMIUnit("molar", "MI:xxx3"));

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
    public void test_confidence_deleted() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ParameterSynchronizerTemplate(this.context, AbstractIntactParameter.class);

        // pre persist kd
        IntactCvTerm kd = createExistingType();

        entityManager.detach(kd);

        ParticipantEvidenceParameter participantParameter = new ParticipantEvidenceParameter(kd, new ParameterValue(new BigDecimal(3)));

        this.synchronizer.setIntactClass(ParticipantEvidenceParameter.class);
        this.synchronizer.persist(participantParameter);

        Assert.assertNotNull(participantParameter.getType());
        IntactCvTerm paramType = (IntactCvTerm)participantParameter.getType();
        Assert.assertEquals(paramType.getAc(), kd.getAc());

        entityManager.flush();
        System.out.println("flush");

        this.synchronizer.delete(participantParameter);

        Assert.assertNull(entityManager.find(ParticipantEvidenceParameter.class, participantParameter.getAc()));
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_find() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ParameterSynchronizerTemplate(this.context, AbstractIntactParameter.class);

        ParticipantEvidenceParameter participantConfidenceNotPersisted = new ParticipantEvidenceParameter(IntactUtils.createMIParameterType("kd", "MI:xxx1"), new ParameterValue(new BigDecimal(3)));
        ParticipantEvidenceParameter participantConfidencePersisted = new ParticipantEvidenceParameter(IntactUtils.createMIParameterType("kd", "MI:xxx1"), new ParameterValue(new BigDecimal(3)));
        this.synchronizer.setIntactClass(ParticipantEvidenceParameter.class);
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
        this.synchronizer = new ParameterSynchronizerTemplate(this.context, AbstractIntactParameter.class);

        ParticipantEvidenceParameter participantParameter = new ParticipantEvidenceParameter(IntactUtils.createMIParameterType("kd", "MI:xxx1"), new ParameterValue(new BigDecimal(3)));

        InteractionEvidenceParameter interactionParameter = new InteractionEvidenceParameter(IntactUtils.createMIParameterType("kd", "MI:xxx1"), new ParameterValue(new BigDecimal(5)),
                IntactUtils.createMIUnit("molar", "MI:xxx3"));

        ComplexParameter complexParameter = new ComplexParameter(IntactUtils.createMIParameterType("molecular weight", "MI:xxx2"), new ParameterValue(new BigDecimal(6)));

        this.synchronizer.setIntactClass(ParticipantEvidenceParameter.class);
        this.synchronizer.synchronizeProperties(participantParameter);

        Assert.assertNull(participantParameter.getAc());
        Assert.assertNotNull(participantParameter.getType());
        IntactCvTerm confType = (IntactCvTerm)participantParameter.getType();
        Assert.assertNotNull(confType.getAc());
        Assert.assertEquals(participantParameter.getType(), IntactUtils.createMIParameterType("kd", "MI:xxx1"));
        Assert.assertEquals(new ParameterValue(new BigDecimal(3)), participantParameter.getValue());

        this.synchronizer.setIntactClass(InteractionEvidenceParameter.class);
        this.synchronizer.synchronizeProperties(interactionParameter);

        Assert.assertNull(interactionParameter.getAc());
        Assert.assertNotNull(interactionParameter.getType());
        IntactCvTerm paramUnit = (IntactCvTerm)interactionParameter.getUnit();
        Assert.assertNotNull(paramUnit.getAc());
        Assert.assertEquals(interactionParameter.getUnit(),IntactUtils.createMIUnit("molar", "MI:xxx3"));
        Assert.assertEquals(new ParameterValue(new BigDecimal(5)), interactionParameter.getValue());

        this.synchronizer.setIntactClass(ComplexParameter.class);
        this.synchronizer.synchronizeProperties(complexParameter);

        Assert.assertNull(complexParameter.getAc());
        Assert.assertNotNull(complexParameter.getType());
        IntactCvTerm confType2 = (IntactCvTerm)complexParameter.getType();
        Assert.assertNotNull(confType2.getAc());
        Assert.assertTrue(complexParameter.getType() == confType2);
        Assert.assertEquals(new ParameterValue(new BigDecimal(6)), complexParameter.getValue());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_not_persist() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ParameterSynchronizerTemplate(this.context, AbstractIntactParameter.class);

        ParticipantEvidenceParameter participantParameter = new ParticipantEvidenceParameter(IntactUtils.createMIParameterType("kd", "MI:xxx1"), new ParameterValue(new BigDecimal(3)));

        InteractionEvidenceParameter interactionParameter = new InteractionEvidenceParameter(IntactUtils.createMIParameterType("kd", "MI:xxx1"), new ParameterValue(new BigDecimal(5)),
                IntactUtils.createMIUnit("molar", "MI:xxx3"));

        ComplexParameter complexParameter = new ComplexParameter(IntactUtils.createMIParameterType("molecular weight", "MI:xxx2"), new ParameterValue(new BigDecimal(6)));

        this.synchronizer.setIntactClass(ParticipantEvidenceParameter.class);
        this.synchronizer.synchronize(participantParameter, false);

        Assert.assertNull(participantParameter.getAc());
        Assert.assertNotNull(participantParameter.getType());
        IntactCvTerm aliasType = (IntactCvTerm)participantParameter.getType();
        Assert.assertNotNull(aliasType.getAc());
        Assert.assertEquals(participantParameter.getType(), IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"));
        Assert.assertEquals(new ParameterValue(new BigDecimal(3)), participantParameter.getValue());

        this.synchronizer.setIntactClass(InteractionEvidenceParameter.class);
        this.synchronizer.synchronize(interactionParameter, false);

        Assert.assertNull(interactionParameter.getAc());
        Assert.assertNotNull(interactionParameter.getType());
        Assert.assertNotNull(interactionParameter.getType());
        IntactCvTerm paramUnit = (IntactCvTerm)interactionParameter.getUnit();
        Assert.assertNotNull(paramUnit.getAc());
        Assert.assertEquals(interactionParameter.getUnit(),IntactUtils.createMIUnit("molar", "MI:xxx3"));
        Assert.assertEquals(new ParameterValue(new BigDecimal(5)), interactionParameter.getValue());

        this.synchronizer.setIntactClass(ComplexParameter.class);
        this.synchronizer.synchronize(complexParameter, false);

        Assert.assertNull(complexParameter.getAc());
        Assert.assertNotNull(complexParameter.getType());
        IntactCvTerm confType2 = (IntactCvTerm)complexParameter.getType();
        Assert.assertNotNull(confType2.getAc());
        Assert.assertTrue(complexParameter.getType() == confType2);
        Assert.assertEquals(new ParameterValue(new BigDecimal(6)), complexParameter.getValue());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_persist() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ParameterSynchronizerTemplate(this.context, AbstractIntactParameter.class);

        ParticipantEvidenceParameter participantParameter = new ParticipantEvidenceParameter(IntactUtils.createMIParameterType("kd", "MI:xxx1"), new ParameterValue(new BigDecimal(3)));

        InteractionEvidenceParameter interactionParameter = new InteractionEvidenceParameter(IntactUtils.createMIParameterType("kd", "MI:xxx1"), new ParameterValue(new BigDecimal(5)),
                IntactUtils.createMIUnit("molar", "MI:xxx3"));

        ComplexParameter complexParameter = new ComplexParameter(IntactUtils.createMIParameterType("molecular weight", "MI:xxx2"), new ParameterValue(new BigDecimal(6)));

        this.synchronizer.setIntactClass(ParticipantEvidenceParameter.class);
        this.synchronizer.synchronize(participantParameter, true);

        Assert.assertNotNull(participantParameter.getAc());
        Assert.assertNotNull(participantParameter.getType());
        IntactCvTerm aliasType = (IntactCvTerm)participantParameter.getType();
        Assert.assertNotNull(aliasType.getAc());
        Assert.assertEquals(participantParameter.getType(), IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"));
        Assert.assertEquals(new ParameterValue(new BigDecimal(3)), participantParameter.getValue());

        this.synchronizer.setIntactClass(InteractionEvidenceParameter.class);
        this.synchronizer.synchronize(interactionParameter, true);

        Assert.assertNotNull(interactionParameter.getAc());
        Assert.assertNotNull(interactionParameter.getType());
        Assert.assertNotNull(interactionParameter.getType());
        IntactCvTerm paramUnit = (IntactCvTerm)interactionParameter.getUnit();
        Assert.assertNotNull(paramUnit.getAc());
        Assert.assertEquals(interactionParameter.getUnit(),IntactUtils.createMIUnit("molar", "MI:xxx3"));
        Assert.assertEquals(new ParameterValue(new BigDecimal(5)), interactionParameter.getValue());

        this.synchronizer.setIntactClass(ComplexParameter.class);
        this.synchronizer.synchronize(complexParameter, true);

        Assert.assertNotNull(complexParameter.getAc());
        Assert.assertNotNull(complexParameter.getType());
        IntactCvTerm confType2 = (IntactCvTerm)complexParameter.getType();
        Assert.assertNotNull(confType2.getAc());
        Assert.assertTrue(complexParameter.getType() == confType2);
        Assert.assertEquals(new ParameterValue(new BigDecimal(6)), complexParameter.getValue());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_jami() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ParameterSynchronizerTemplate(this.context, AbstractIntactParameter.class);

        Parameter participantParameter = new DefaultParameter(IntactUtils.createMIParameterType("kd", "MI:xxx1"), new ParameterValue(new BigDecimal(3)));

        Parameter interactionParameter = new DefaultParameter(IntactUtils.createMIParameterType("kd", "MI:xxx1"), new ParameterValue(new BigDecimal(5)),
                IntactUtils.createMIUnit("molar", "MI:xxx3"));

        Parameter complexParameter = new DefaultModelledParameter(IntactUtils.createMIParameterType("molecular weight", "MI:xxx2"), new ParameterValue(new BigDecimal(6)));

        this.synchronizer.setIntactClass(ParticipantEvidenceParameter.class);
        ParticipantEvidenceParameter newConf = (ParticipantEvidenceParameter)this.synchronizer.synchronize(participantParameter, true);

        Assert.assertNotNull(newConf.getAc());
        Assert.assertNotNull(newConf.getType());
        IntactCvTerm aliasType = (IntactCvTerm)newConf.getType();
        Assert.assertNotNull(aliasType.getAc());
        Assert.assertEquals(participantParameter.getType(), IntactUtils.createMIConfidenceType("author-score", "MI:xxx1"));
        Assert.assertEquals(new ParameterValue(new BigDecimal(3)), newConf.getValue());

        this.synchronizer.setIntactClass(InteractionEvidenceParameter.class);
        InteractionEvidenceParameter newConf2 = (InteractionEvidenceParameter)this.synchronizer.synchronize(interactionParameter, true);

        Assert.assertNotNull(newConf2.getAc());
        Assert.assertNotNull(newConf2.getType());
        Assert.assertNotNull(interactionParameter.getType());
        IntactCvTerm paramUnit = (IntactCvTerm)interactionParameter.getUnit();
        Assert.assertNotNull(paramUnit.getAc());
        Assert.assertEquals(interactionParameter.getUnit(),IntactUtils.createMIUnit("molar", "MI:xxx3"));
        Assert.assertEquals(new ParameterValue(new BigDecimal(5)), newConf2.getValue());

        this.synchronizer.setIntactClass(ComplexParameter.class);
        ComplexParameter newConf3 = (ComplexParameter)this.synchronizer.synchronize(complexParameter, true);

        Assert.assertNotNull(newConf3.getAc());
        Assert.assertNotNull(newConf3.getType());
        IntactCvTerm confType2 = (IntactCvTerm)newConf3.getType();
        Assert.assertNotNull(confType2.getAc());
        Assert.assertTrue(newConf3.getType() == confType2);
        Assert.assertEquals(new ParameterValue(new BigDecimal(6)), newConf3.getValue());
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
}
