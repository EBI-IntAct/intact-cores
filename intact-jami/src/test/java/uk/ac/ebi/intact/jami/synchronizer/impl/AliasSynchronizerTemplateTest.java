package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import psidev.psi.mi.jami.model.impl.DefaultAlias;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.AliasSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

/**
 * Unit test for AliasSynchronizerTemplate
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
public class AliasSynchronizerTemplateTest {

    @PersistenceContext(unitName = "intact-core")
    private EntityManager entityManager;
    @PersistenceUnit(unitName = "intact-core", name = "intactEntityManagerFactory")
    private EntityManagerFactory intactEntityManagerFactory;

    private AliasSynchronizer synchronizer;
    private SynchronizerContext context;

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_all() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new AliasSynchronizerTemplate(this.context, AbstractIntactAlias.class);

        CvTermAlias cvAliasWithType = new CvTermAlias(IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI), "test synonym");

        OrganismAlias organismAlias = new OrganismAlias("test synonym 2");

        InteractorAlias interactorAlias = new InteractorAlias(IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI), "test synonym 3");

        this.synchronizer.setIntactClass(CvTermAlias.class);
        this.synchronizer.persist(cvAliasWithType);

        Assert.assertNotNull(cvAliasWithType.getAc());
        Assert.assertNotNull(cvAliasWithType.getType());
        IntactCvTerm aliasType = (IntactCvTerm)cvAliasWithType.getType();
        Assert.assertNotNull(aliasType.getAc());
        Assert.assertEquals(cvAliasWithType.getType(), IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI));
        Assert.assertEquals("test synonym", cvAliasWithType.getName());

        this.synchronizer.setIntactClass(OrganismAlias.class);
        this.synchronizer.persist(organismAlias);

        Assert.assertNotNull(organismAlias.getAc());
        Assert.assertNull(organismAlias.getType());
        Assert.assertEquals("test synonym 2", organismAlias.getName());

        this.synchronizer.setIntactClass(InteractorAlias.class);
        this.synchronizer.persist(interactorAlias);

        Assert.assertNotNull(interactorAlias.getAc());
        Assert.assertNotNull(interactorAlias.getType());
        IntactCvTerm aliasType2 = (IntactCvTerm)interactorAlias.getType();
        Assert.assertNotNull(aliasType2.getAc());
        Assert.assertTrue(cvAliasWithType.getType() == aliasType2);
        Assert.assertEquals("test synonym 3", interactorAlias.getName());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_with_existing_type() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new AliasSynchronizerTemplate(this.context, AbstractIntactAlias.class);

        IntactCvTerm aliasSynonym = createExistingType();

        CvTermAlias cvAliasWithType = new CvTermAlias(IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI), "test synonym");

        OrganismAlias organismAlias = new OrganismAlias("test synonym 2");

        InteractorAlias interactorAlias = new InteractorAlias(IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI), "test synonym 3");

        this.synchronizer.setIntactClass(CvTermAlias.class);
        this.synchronizer.persist(cvAliasWithType);

        Assert.assertNotNull(cvAliasWithType.getType());
        IntactCvTerm aliasType = (IntactCvTerm)cvAliasWithType.getType();
        Assert.assertEquals(aliasType.getAc(), aliasSynonym.getAc());
        Assert.assertEquals("test synonym", cvAliasWithType.getName());

        this.synchronizer.setIntactClass(OrganismAlias.class);
        this.synchronizer.persist(organismAlias);

        Assert.assertNotNull(organismAlias.getAc());
        Assert.assertNull(organismAlias.getType());
        Assert.assertEquals("test synonym 2", organismAlias.getName());

        this.synchronizer.setIntactClass(InteractorAlias.class);
        this.synchronizer.persist(interactorAlias);

        Assert.assertNotNull(interactorAlias.getAc());
        Assert.assertNotNull(interactorAlias.getType());
        IntactCvTerm aliasType2 = (IntactCvTerm)interactorAlias.getType();
        Assert.assertEquals(aliasType2.getAc(), aliasSynonym.getAc());
        Assert.assertEquals("test synonym 3", interactorAlias.getName());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_with_detached_type() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new AliasSynchronizerTemplate(this.context, AbstractIntactAlias.class);

        // pre persist alias synonym
        IntactCvTerm aliasSynonym = createExistingType();

        entityManager.detach(aliasSynonym);

        CvTermAlias cvAliasWithType = new CvTermAlias(aliasSynonym, "test synonym");

        OrganismAlias organismAlias = new OrganismAlias("test synonym 2");

        InteractorAlias interactorAlias = new InteractorAlias(aliasSynonym, "test synonym 3");

        this.synchronizer.setIntactClass(CvTermAlias.class);
        this.synchronizer.persist(cvAliasWithType);

        Assert.assertNotNull(cvAliasWithType.getType());
        IntactCvTerm aliasType = (IntactCvTerm)cvAliasWithType.getType();
        Assert.assertEquals(aliasType.getAc(), aliasSynonym.getAc());
        Assert.assertEquals("test synonym", cvAliasWithType.getName());

        this.synchronizer.setIntactClass(OrganismAlias.class);
        this.synchronizer.persist(organismAlias);

        Assert.assertNotNull(organismAlias.getAc());
        Assert.assertNull(organismAlias.getType());
        Assert.assertEquals("test synonym 2", organismAlias.getName());

        this.synchronizer.setIntactClass(InteractorAlias.class);
        this.synchronizer.persist(interactorAlias);

        Assert.assertNotNull(interactorAlias.getAc());
        Assert.assertNotNull(interactorAlias.getType());
        IntactCvTerm aliasType2 = (IntactCvTerm)interactorAlias.getType();
        Assert.assertEquals(aliasType2.getAc(), aliasSynonym.getAc());
        Assert.assertEquals("test synonym 3", interactorAlias.getName());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_alias_deleted() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new AliasSynchronizerTemplate(this.context, AbstractIntactAlias.class);

        // pre persist alias synonym
        IntactCvTerm aliasSynonym = createExistingType();

        entityManager.detach(aliasSynonym);

        CvTermAlias cvAliasWithType = new CvTermAlias(aliasSynonym, "test synonym");

        this.synchronizer.setIntactClass(CvTermAlias.class);
        this.synchronizer.persist(cvAliasWithType);

        Assert.assertNotNull(cvAliasWithType.getType());
        IntactCvTerm aliasType = (IntactCvTerm)cvAliasWithType.getType();
        Assert.assertEquals(aliasType.getAc(), aliasSynonym.getAc());

        entityManager.flush();
        System.out.println("flush");

        this.synchronizer.delete(cvAliasWithType);

        Assert.assertNull(entityManager.find(CvTermAlias.class, cvAliasWithType.getAc()));
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_find() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new AliasSynchronizerTemplate(this.context, AbstractIntactAlias.class);

        CvTermAlias cvAliasNotPersisted = new CvTermAlias(IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI), "test synonym");
        CvTermAlias cvAliasPersisted = new CvTermAlias(IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI), "test synonym 2");
        this.synchronizer.persist(cvAliasNotPersisted);
        entityManager.flush();
        this.context.clearCache();

        Assert.assertNull(this.synchronizer.find(cvAliasNotPersisted));
        Assert.assertNull(this.synchronizer.find(cvAliasPersisted));
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_properties() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new AliasSynchronizerTemplate(this.context, AbstractIntactAlias.class);

        CvTermAlias cvAliasWithType = new CvTermAlias(IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI), "test synonym");

        OrganismAlias organismAlias = new OrganismAlias("test synonym 2");

        InteractorAlias interactorAlias = new InteractorAlias(IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI), "test synonym 3");

        this.synchronizer.setIntactClass(CvTermAlias.class);
        this.synchronizer.synchronizeProperties(cvAliasWithType);

        Assert.assertNull(cvAliasWithType.getAc());
        Assert.assertNotNull(cvAliasWithType.getType());
        IntactCvTerm aliasType = (IntactCvTerm)cvAliasWithType.getType();
        Assert.assertNotNull(aliasType.getAc());
        Assert.assertEquals(cvAliasWithType.getType(), IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI));
        Assert.assertEquals("test synonym", cvAliasWithType.getName());

        this.synchronizer.setIntactClass(OrganismAlias.class);
        this.synchronizer.synchronizeProperties(organismAlias);

        Assert.assertNull(organismAlias.getAc());
        Assert.assertNull(organismAlias.getType());
        Assert.assertEquals("test synonym 2", organismAlias.getName());

        this.synchronizer.setIntactClass(InteractorAlias.class);
        this.synchronizer.synchronizeProperties(interactorAlias);

        Assert.assertNull(interactorAlias.getAc());
        Assert.assertNotNull(interactorAlias.getType());
        IntactCvTerm aliasType2 = (IntactCvTerm)interactorAlias.getType();
        Assert.assertNotNull(aliasType2.getAc());
        Assert.assertTrue(cvAliasWithType.getType() == aliasType2);
        Assert.assertEquals("test synonym 3", interactorAlias.getName());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_not_persist() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new AliasSynchronizerTemplate(this.context, AbstractIntactAlias.class);

        CvTermAlias cvAliasWithType = new CvTermAlias(IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI), "test synonym");

        OrganismAlias organismAlias = new OrganismAlias("test synonym 2");

        InteractorAlias interactorAlias = new InteractorAlias(IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI), "test synonym 3");

        this.synchronizer.setIntactClass(CvTermAlias.class);
        this.synchronizer.synchronize(cvAliasWithType, false);

        Assert.assertNull(cvAliasWithType.getAc());
        Assert.assertNotNull(cvAliasWithType.getType());
        IntactCvTerm aliasType = (IntactCvTerm)cvAliasWithType.getType();
        Assert.assertNotNull(aliasType.getAc());
        Assert.assertEquals(cvAliasWithType.getType(), IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI));
        Assert.assertEquals("test synonym", cvAliasWithType.getName());

        this.synchronizer.setIntactClass(OrganismAlias.class);
        this.synchronizer.synchronize(organismAlias, false);

        Assert.assertNull(organismAlias.getAc());
        Assert.assertNull(organismAlias.getType());
        Assert.assertEquals("test synonym 2", organismAlias.getName());

        this.synchronizer.setIntactClass(InteractorAlias.class);
        this.synchronizer.synchronize(interactorAlias, false);

        Assert.assertNull(interactorAlias.getAc());
        Assert.assertNotNull(interactorAlias.getType());
        IntactCvTerm aliasType2 = (IntactCvTerm)interactorAlias.getType();
        Assert.assertNotNull(aliasType2.getAc());
        Assert.assertTrue(cvAliasWithType.getType() == aliasType2);
        Assert.assertEquals("test synonym 3", interactorAlias.getName());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_persist() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new AliasSynchronizerTemplate(this.context, AbstractIntactAlias.class);

        CvTermAlias cvAliasWithType = new CvTermAlias(IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI), "test synonym");

        OrganismAlias organismAlias = new OrganismAlias("test synonym 2");

        InteractorAlias interactorAlias = new InteractorAlias(IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI), "test synonym 3");

        this.synchronizer.setIntactClass(CvTermAlias.class);
        this.synchronizer.synchronize(cvAliasWithType, true);

        Assert.assertNotNull(cvAliasWithType.getAc());
        Assert.assertNotNull(cvAliasWithType.getType());
        IntactCvTerm aliasType = (IntactCvTerm)cvAliasWithType.getType();
        Assert.assertNotNull(aliasType.getAc());
        Assert.assertEquals(cvAliasWithType.getType(), IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI));
        Assert.assertEquals("test synonym", cvAliasWithType.getName());

        this.synchronizer.setIntactClass(OrganismAlias.class);
        this.synchronizer.synchronize(organismAlias, true);

        Assert.assertNotNull(organismAlias.getAc());
        Assert.assertNull(organismAlias.getType());
        Assert.assertEquals("test synonym 2", organismAlias.getName());

        this.synchronizer.setIntactClass(InteractorAlias.class);
        this.synchronizer.synchronize(interactorAlias, true);

        Assert.assertNotNull(interactorAlias.getAc());
        Assert.assertNotNull(interactorAlias.getType());
        IntactCvTerm aliasType2 = (IntactCvTerm)interactorAlias.getType();
        Assert.assertNotNull(aliasType2.getAc());
        Assert.assertTrue(cvAliasWithType.getType() == aliasType2);
        Assert.assertEquals("test synonym 3", interactorAlias.getName());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_jami() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new AliasSynchronizerTemplate(this.context, AbstractIntactAlias.class);

        Alias cvAliasWithType = new DefaultAlias(IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI), "test synonym");

        Alias organismAlias = new DefaultAlias("test synonym 2");

        Alias interactorAlias = new DefaultAlias(IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI), "test synonym 3");

        this.synchronizer.setIntactClass(CvTermAlias.class);
        CvTermAlias newAlias = (CvTermAlias)this.synchronizer.synchronize(cvAliasWithType, true);

        Assert.assertNotNull(newAlias.getAc());
        Assert.assertNotNull(newAlias.getType());
        IntactCvTerm aliasType = (IntactCvTerm)newAlias.getType();
        Assert.assertNotNull(aliasType.getAc());
        Assert.assertEquals(cvAliasWithType.getType(), IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI));
        Assert.assertEquals("test synonym", cvAliasWithType.getName());

        this.synchronizer.setIntactClass(OrganismAlias.class);
        OrganismAlias newAlias2 = (OrganismAlias)this.synchronizer.synchronize(organismAlias, true);

        Assert.assertNotNull(newAlias2.getAc());
        Assert.assertNull(newAlias2.getType());
        Assert.assertEquals("test synonym 2", newAlias2.getName());

        this.synchronizer.setIntactClass(InteractorAlias.class);
        InteractorAlias newAlias3 = (InteractorAlias)this.synchronizer.synchronize(interactorAlias, true);

        Assert.assertNotNull(newAlias3.getAc());
        Assert.assertNotNull(newAlias3.getType());
        IntactCvTerm aliasType2 = (IntactCvTerm)newAlias3.getType();
        Assert.assertNotNull(aliasType2.getAc());
        Assert.assertTrue(cvAliasWithType.getType() == aliasType2);
        Assert.assertEquals("test synonym 3", interactorAlias.getName());
    }

    private IntactCvTerm createExistingType() {
        // pre persist alias synonym
        IntactCvTerm aliasSynonym = new IntactCvTerm(Alias.SYNONYM);
        aliasSynonym.setObjClass(IntactUtils.ALIAS_TYPE_OBJCLASS);
        entityManager.persist(aliasSynonym);
        IntactCvTerm psimi = new IntactCvTerm(CvTerm.PSI_MI);
        psimi.setObjClass(IntactUtils.DATABASE_OBJCLASS);
        entityManager.persist(psimi);
        IntactCvTerm identity = new IntactCvTerm(Xref.IDENTITY);
        identity.setObjClass(IntactUtils.QUALIFIER_OBJCLASS);
        entityManager.persist(identity);

        CvTermXref ref1 = new CvTermXref(psimi, Alias.SYNONYM_MI, identity);
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
