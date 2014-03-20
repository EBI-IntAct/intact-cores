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
import psidev.psi.mi.jami.model.Xref;
import psidev.psi.mi.jami.model.impl.DefaultXref;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.synchronizer.XrefSynchronizer;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

/**
 * Unit test for XrefSynchronizerTemplate
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
public class XrefSynchronizerTemplateTest {

    @PersistenceContext(unitName = "intact-core")
    private EntityManager entityManager;
    @PersistenceUnit(unitName = "intact-core", name = "intactEntityManagerFactory")
    private EntityManagerFactory intactEntityManagerFactory;

    private XrefSynchronizer synchronizer;
    private SynchronizerContext context;

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_all() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new XrefSynchronizerTemplate(this.context, AbstractIntactXref.class);

        CvTermXref cvXref = new CvTermXref(IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI), "12345");

        InteractionXref interactionXref = new InteractionXref(IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI), "123456");

        InteractorXref interactorXref = new InteractorXref(IntactUtils.createMIDatabase(Xref.IMEX, Xref.IMEX_MI), "IM-1-1", IntactUtils.createMIQualifier(Xref.SEE_ALSO, Xref.SEE_ALSO_MI));

        this.synchronizer.setIntactClass(CvTermXref.class);
        this.synchronizer.persist(cvXref);

        Assert.assertNotNull(cvXref.getAc());
        Assert.assertNotNull(cvXref.getDatabase());
        Assert.assertNull(cvXref.getQualifier());
        IntactCvTerm db = (IntactCvTerm)cvXref.getDatabase();
        Assert.assertNotNull(db.getAc());
        Assert.assertEquals(cvXref.getDatabase(), IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI));
        Assert.assertEquals("12345", cvXref.getId());

        this.synchronizer.setIntactClass(InteractionXref.class);
        this.synchronizer.persist(interactionXref);

        Assert.assertNotNull(interactionXref.getAc());
        Assert.assertNotNull(interactionXref.getDatabase());
        Assert.assertNull(interactionXref.getQualifier());
        IntactCvTerm db2 = (IntactCvTerm)interactionXref.getDatabase();
        Assert.assertNotNull(db2.getAc());
        Assert.assertTrue(interactionXref.getDatabase() == cvXref.getDatabase());
        Assert.assertEquals("123456", interactionXref.getId());

        this.synchronizer.setIntactClass(InteractorXref.class);
        this.synchronizer.persist(interactorXref);

        Assert.assertNotNull(interactorXref.getAc());
        Assert.assertNotNull(interactorXref.getDatabase());
        Assert.assertNotNull(interactorXref.getQualifier());
        IntactCvTerm db3 = (IntactCvTerm)interactorXref.getDatabase();
        Assert.assertNotNull(db3.getAc());
        Assert.assertTrue(interactorXref.getDatabase() != cvXref.getDatabase());
        Assert.assertEquals("IM-1-1", interactorXref.getId());
        IntactCvTerm qualifier = (IntactCvTerm)interactorXref.getQualifier();
        Assert.assertNotNull(qualifier.getAc());
        Assert.assertEquals(interactorXref.getQualifier(), IntactUtils.createMIQualifier(Xref.SEE_ALSO, Xref.SEE_ALSO_MI));
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_with_existing_db_qualifier() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new XrefSynchronizerTemplate(this.context, AbstractIntactXref.class);

        IntactCvTerm existingDb = createExistingDatabase();
        IntactCvTerm existingQualifier = createExistingQualifier((IntactCvTerm)existingDb.getIdentifiers().iterator().next().getDatabase(),
                (IntactCvTerm)existingDb.getIdentifiers().iterator().next().getQualifier());

        CvTermXref cvXref = new CvTermXref(IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI), "12345");

        InteractionXref interactionXref = new InteractionXref(IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI), "123456");

        InteractorXref interactorXref = new InteractorXref(IntactUtils.createMIDatabase(Xref.IMEX, Xref.IMEX_MI), "IM-1-1", IntactUtils.createMIQualifier(Xref.SEE_ALSO, Xref.SEE_ALSO_MI));

        this.synchronizer.setIntactClass(CvTermXref.class);
        this.synchronizer.persist(cvXref);

        Assert.assertNotNull(cvXref.getAc());
        Assert.assertNotNull(cvXref.getDatabase());
        Assert.assertNull(cvXref.getQualifier());
        IntactCvTerm db = (IntactCvTerm)cvXref.getDatabase();
        Assert.assertNotNull(db.getAc());
        Assert.assertEquals(((IntactCvTerm) cvXref.getDatabase()).getAc(), existingDb.getAc());
        Assert.assertEquals("12345", cvXref.getId());

        this.synchronizer.setIntactClass(InteractionXref.class);
        this.synchronizer.persist(interactionXref);

        Assert.assertNotNull(interactionXref.getAc());
        Assert.assertNotNull(interactionXref.getDatabase());
        Assert.assertNull(interactionXref.getQualifier());
        IntactCvTerm db2 = (IntactCvTerm)interactionXref.getDatabase();
        Assert.assertNotNull(db2.getAc());
        Assert.assertTrue(interactionXref.getDatabase() == cvXref.getDatabase());
        Assert.assertEquals("123456", interactionXref.getId());

        this.synchronizer.setIntactClass(InteractorXref.class);
        this.synchronizer.persist(interactorXref);

        Assert.assertNotNull(interactorXref.getAc());
        Assert.assertNotNull(interactorXref.getDatabase());
        Assert.assertNotNull(interactorXref.getQualifier());
        IntactCvTerm db3 = (IntactCvTerm)interactorXref.getDatabase();
        Assert.assertNotNull(db3.getAc());
        Assert.assertTrue(interactorXref.getDatabase() != cvXref.getDatabase());
        Assert.assertEquals("IM-1-1", interactorXref.getId());
        IntactCvTerm qualifier = (IntactCvTerm)interactorXref.getQualifier();
        Assert.assertNotNull(qualifier.getAc());
        Assert.assertEquals(((IntactCvTerm) interactorXref.getQualifier()).getAc(), existingQualifier.getAc());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_with_detached_type() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new XrefSynchronizerTemplate(this.context, AbstractIntactXref.class);

        // pre persist alias synonym
        IntactCvTerm existingDb = createExistingDatabase();
        IntactCvTerm existingQualifier = createExistingQualifier((IntactCvTerm) existingDb.getIdentifiers().iterator().next().getDatabase(),
                (IntactCvTerm) existingDb.getIdentifiers().iterator().next().getQualifier());

        entityManager.detach(existingDb);
        entityManager.detach(existingQualifier);

        CvTermXref cvXref = new CvTermXref(IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI), "12345");

        InteractionXref interactionXref = new InteractionXref(IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI), "123456");

        InteractorXref interactorXref = new InteractorXref(IntactUtils.createMIDatabase(Xref.IMEX, Xref.IMEX_MI), "IM-1-1", IntactUtils.createMIQualifier(Xref.SEE_ALSO, Xref.SEE_ALSO_MI));

        this.synchronizer.setIntactClass(CvTermXref.class);
        this.synchronizer.persist(cvXref);

        Assert.assertNotNull(cvXref.getAc());
        Assert.assertNotNull(cvXref.getDatabase());
        Assert.assertNull(cvXref.getQualifier());
        IntactCvTerm db = (IntactCvTerm)cvXref.getDatabase();
        Assert.assertNotNull(db.getAc());
        Assert.assertEquals(((IntactCvTerm) cvXref.getDatabase()).getAc(), existingDb.getAc());
        Assert.assertEquals("12345", cvXref.getId());

        this.synchronizer.setIntactClass(InteractionXref.class);
        this.synchronizer.persist(interactionXref);

        Assert.assertNotNull(interactionXref.getAc());
        Assert.assertNotNull(interactionXref.getDatabase());
        Assert.assertNull(interactionXref.getQualifier());
        IntactCvTerm db2 = (IntactCvTerm)interactionXref.getDatabase();
        Assert.assertNotNull(db2.getAc());
        Assert.assertTrue(interactionXref.getDatabase() == cvXref.getDatabase());
        Assert.assertEquals("123456", interactionXref.getId());

        this.synchronizer.setIntactClass(InteractorXref.class);
        this.synchronizer.persist(interactorXref);

        Assert.assertNotNull(interactorXref.getAc());
        Assert.assertNotNull(interactorXref.getDatabase());
        Assert.assertNotNull(interactorXref.getQualifier());
        IntactCvTerm db3 = (IntactCvTerm)interactorXref.getDatabase();
        Assert.assertNotNull(db3.getAc());
        Assert.assertTrue(interactorXref.getDatabase() != cvXref.getDatabase());
        Assert.assertEquals("IM-1-1", interactorXref.getId());
        IntactCvTerm qualifier = (IntactCvTerm)interactorXref.getQualifier();
        Assert.assertNotNull(qualifier.getAc());
        Assert.assertEquals(((IntactCvTerm) interactorXref.getQualifier()).getAc(), existingQualifier.getAc());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_find() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new XrefSynchronizerTemplate(this.context, AbstractIntactXref.class);

        CvTermXref cvXrefNotPersisted = new CvTermXref(IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI), "test synonym");
        CvTermXref cvXrefPersisted = new CvTermXref(IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI), "test synonym 2");
        this.synchronizer.persist(cvXrefNotPersisted);
        entityManager.flush();
        this.context.clearCache();

        System.out.println("flush");

        Assert.assertNull(this.synchronizer.find(cvXrefNotPersisted));
        Assert.assertNull(this.synchronizer.find(cvXrefPersisted));
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_delete() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new XrefSynchronizerTemplate(this.context, AbstractIntactXref.class);

        CvTermXref cvXrefNotPersisted = new CvTermXref(IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI), "test synonym");
        this.synchronizer.setIntactClass(CvTermXref.class);
        this.synchronizer.persist(cvXrefNotPersisted);
        entityManager.flush();
        this.context.clearCache();

        System.out.println("flush");

        this.synchronizer.delete(cvXrefNotPersisted);
        Assert.assertNull(entityManager.find(CvTermXref.class, cvXrefNotPersisted.getAc()));
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_properties() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new XrefSynchronizerTemplate(this.context, AbstractIntactXref.class);

        CvTermXref cvXref = new CvTermXref(IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI), "12345");

        InteractionXref interactionXref = new InteractionXref(IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI), "123456");

        InteractorXref interactorXref = new InteractorXref(IntactUtils.createMIDatabase(Xref.IMEX, Xref.IMEX_MI), "IM-1-1", IntactUtils.createMIQualifier(Xref.SEE_ALSO, Xref.SEE_ALSO_MI));

        this.synchronizer.setIntactClass(CvTermXref.class);
        this.synchronizer.synchronizeProperties(cvXref);

        Assert.assertNull(cvXref.getAc());
        Assert.assertNotNull(cvXref.getDatabase());
        Assert.assertNull(cvXref.getQualifier());
        IntactCvTerm db = (IntactCvTerm)cvXref.getDatabase();
        Assert.assertNotNull(db.getAc());
        Assert.assertEquals(cvXref.getDatabase(), IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI));
        Assert.assertEquals("12345", cvXref.getId());

        this.synchronizer.setIntactClass(InteractionXref.class);
        this.synchronizer.synchronizeProperties(interactionXref);

        Assert.assertNull(interactionXref.getAc());
        Assert.assertNotNull(interactionXref.getDatabase());
        Assert.assertNull(interactionXref.getQualifier());
        IntactCvTerm db2 = (IntactCvTerm)interactionXref.getDatabase();
        Assert.assertNotNull(db2.getAc());
        Assert.assertTrue(interactionXref.getDatabase() == cvXref.getDatabase());
        Assert.assertEquals("123456", interactionXref.getId());

        this.synchronizer.setIntactClass(InteractorXref.class);
        this.synchronizer.synchronizeProperties(interactorXref);

        Assert.assertNull(interactorXref.getAc());
        Assert.assertNotNull(interactorXref.getDatabase());
        Assert.assertNotNull(interactorXref.getQualifier());
        IntactCvTerm db3 = (IntactCvTerm)interactorXref.getDatabase();
        Assert.assertNotNull(db3.getAc());
        Assert.assertTrue(interactorXref.getDatabase() != cvXref.getDatabase());
        Assert.assertEquals("IM-1-1", interactorXref.getId());
        IntactCvTerm qualifier = (IntactCvTerm)interactorXref.getQualifier();
        Assert.assertNotNull(qualifier.getAc());
        Assert.assertEquals(interactorXref.getQualifier(), IntactUtils.createMIQualifier(Xref.SEE_ALSO, Xref.SEE_ALSO_MI));
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_not_persist() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new XrefSynchronizerTemplate(this.context, AbstractIntactXref.class);

        CvTermXref cvXref = new CvTermXref(IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI), "12345");

        InteractionXref interactionXref = new InteractionXref(IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI), "123456");

        InteractorXref interactorXref = new InteractorXref(IntactUtils.createMIDatabase(Xref.IMEX, Xref.IMEX_MI), "IM-1-1", IntactUtils.createMIQualifier(Xref.SEE_ALSO, Xref.SEE_ALSO_MI));

        this.synchronizer.setIntactClass(CvTermXref.class);
        CvTermXref newRef = (CvTermXref)this.synchronizer.synchronize(cvXref, false);

        Assert.assertNull(newRef.getAc());
        Assert.assertNotNull(newRef.getDatabase());
        Assert.assertNull(newRef.getQualifier());
        IntactCvTerm db = (IntactCvTerm)newRef.getDatabase();
        Assert.assertNotNull(db.getAc());
        Assert.assertEquals(newRef.getDatabase(), IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI));
        Assert.assertEquals("12345", cvXref.getId());

        this.synchronizer.setIntactClass(InteractionXref.class);
        InteractionXref newRef2 = (InteractionXref) this.synchronizer.synchronize(interactionXref, false);

        Assert.assertNull(newRef2.getAc());
        Assert.assertNotNull(newRef2.getDatabase());
        Assert.assertNull(newRef2.getQualifier());
        IntactCvTerm db2 = (IntactCvTerm)newRef2.getDatabase();
        Assert.assertNotNull(db2.getAc());
        Assert.assertTrue(newRef2.getDatabase() == cvXref.getDatabase());
        Assert.assertEquals("123456", newRef2.getId());

        this.synchronizer.setIntactClass(InteractorXref.class);
        InteractorXref newRef3 = (InteractorXref) this.synchronizer.synchronize(interactorXref, false);

        Assert.assertNull(newRef3.getAc());
        Assert.assertNotNull(newRef3.getDatabase());
        Assert.assertNotNull(newRef3.getQualifier());
        IntactCvTerm db3 = (IntactCvTerm)newRef3.getDatabase();
        Assert.assertNotNull(db3.getAc());
        Assert.assertTrue(newRef3.getDatabase() != cvXref.getDatabase());
        Assert.assertEquals("IM-1-1", interactorXref.getId());
        IntactCvTerm qualifier = (IntactCvTerm)newRef3.getQualifier();
        Assert.assertNotNull(qualifier.getAc());
        Assert.assertEquals(newRef3.getQualifier(), IntactUtils.createMIQualifier(Xref.SEE_ALSO, Xref.SEE_ALSO_MI));
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_persist() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new XrefSynchronizerTemplate(this.context, AbstractIntactXref.class);

        CvTermXref cvXref = new CvTermXref(IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI), "12345");

        InteractionXref interactionXref = new InteractionXref(IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI), "123456");

        InteractorXref interactorXref = new InteractorXref(IntactUtils.createMIDatabase(Xref.IMEX, Xref.IMEX_MI), "IM-1-1", IntactUtils.createMIQualifier(Xref.SEE_ALSO, Xref.SEE_ALSO_MI));

        this.synchronizer.setIntactClass(CvTermXref.class);
        CvTermXref newRef = (CvTermXref)this.synchronizer.synchronize(cvXref, true);

        Assert.assertNotNull(newRef.getAc());
        Assert.assertNotNull(newRef.getDatabase());
        Assert.assertNull(newRef.getQualifier());
        IntactCvTerm db = (IntactCvTerm)newRef.getDatabase();
        Assert.assertNotNull(db.getAc());
        Assert.assertEquals(newRef.getDatabase(), IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI));
        Assert.assertEquals("12345", cvXref.getId());

        this.synchronizer.setIntactClass(InteractionXref.class);
        InteractionXref newRef2 = (InteractionXref) this.synchronizer.synchronize(interactionXref, true);

        Assert.assertNotNull(newRef2.getAc());
        Assert.assertNotNull(newRef2.getDatabase());
        Assert.assertNull(newRef2.getQualifier());
        IntactCvTerm db2 = (IntactCvTerm)newRef2.getDatabase();
        Assert.assertNotNull(db2.getAc());
        Assert.assertTrue(newRef2.getDatabase() == cvXref.getDatabase());
        Assert.assertEquals("123456", newRef2.getId());

        this.synchronizer.setIntactClass(InteractorXref.class);
        InteractorXref newRef3 = (InteractorXref) this.synchronizer.synchronize(interactorXref, true);

        Assert.assertNotNull(newRef3.getAc());
        Assert.assertNotNull(newRef3.getDatabase());
        Assert.assertNotNull(newRef3.getQualifier());
        IntactCvTerm db3 = (IntactCvTerm)newRef3.getDatabase();
        Assert.assertNotNull(db3.getAc());
        Assert.assertTrue(newRef3.getDatabase() != cvXref.getDatabase());
        Assert.assertEquals("IM-1-1", interactorXref.getId());
        IntactCvTerm qualifier = (IntactCvTerm)newRef3.getQualifier();
        Assert.assertNotNull(qualifier.getAc());
        Assert.assertEquals(newRef3.getQualifier(), IntactUtils.createMIQualifier(Xref.SEE_ALSO, Xref.SEE_ALSO_MI));
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_jami() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new XrefSynchronizerTemplate(this.context, AbstractIntactXref.class);

        Xref cvXref = new DefaultXref(IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI), "12345");

        Xref interactionXref = new DefaultXref(IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI), "123456");

        Xref interactorXref = new DefaultXref(IntactUtils.createMIDatabase(Xref.IMEX, Xref.IMEX_MI), "IM-1-1", IntactUtils.createMIQualifier(Xref.SEE_ALSO, Xref.SEE_ALSO_MI));

        this.synchronizer.setIntactClass(CvTermXref.class);
        CvTermXref newRef = (CvTermXref)this.synchronizer.synchronize(cvXref, true);

        Assert.assertNotNull(newRef.getAc());
        Assert.assertNotNull(newRef.getDatabase());
        Assert.assertNull(newRef.getQualifier());
        IntactCvTerm db = (IntactCvTerm)newRef.getDatabase();
        Assert.assertNotNull(db.getAc());
        Assert.assertEquals(newRef.getDatabase(), IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI));
        Assert.assertEquals("12345", cvXref.getId());

        this.synchronizer.setIntactClass(InteractionXref.class);
        InteractionXref newRef2 = (InteractionXref) this.synchronizer.synchronize(interactionXref, true);

        Assert.assertNotNull(newRef2.getAc());
        Assert.assertNotNull(newRef2.getDatabase());
        Assert.assertNull(newRef2.getQualifier());
        IntactCvTerm db2 = (IntactCvTerm)newRef2.getDatabase();
        Assert.assertNotNull(db2.getAc());
        Assert.assertTrue(newRef2.getDatabase() == cvXref.getDatabase());
        Assert.assertEquals("123456", newRef2.getId());

        this.synchronizer.setIntactClass(InteractorXref.class);
        InteractorXref newRef3 = (InteractorXref) this.synchronizer.synchronize(interactorXref, true);

        Assert.assertNotNull(newRef3.getAc());
        Assert.assertNotNull(newRef3.getDatabase());
        Assert.assertNotNull(newRef3.getQualifier());
        IntactCvTerm db3 = (IntactCvTerm)newRef3.getDatabase();
        Assert.assertNotNull(db3.getAc());
        Assert.assertTrue(newRef3.getDatabase() != cvXref.getDatabase());
        Assert.assertEquals("IM-1-1", interactorXref.getId());
        IntactCvTerm qualifier = (IntactCvTerm)newRef3.getQualifier();
        Assert.assertNotNull(qualifier.getAc());
        Assert.assertEquals(newRef3.getQualifier(), IntactUtils.createMIQualifier(Xref.SEE_ALSO, Xref.SEE_ALSO_MI));
    }

    private IntactCvTerm createExistingDatabase() {
        // pre persist db
        IntactCvTerm db = new IntactCvTerm(Xref.PUBMED);
        db.setObjClass(IntactUtils.DATABASE_OBJCLASS);
        entityManager.persist(db);
        IntactCvTerm psimi = new IntactCvTerm(CvTerm.PSI_MI);
        psimi.setObjClass(IntactUtils.DATABASE_OBJCLASS);
        entityManager.persist(psimi);
        IntactCvTerm identity = new IntactCvTerm(Xref.IDENTITY);
        identity.setObjClass(IntactUtils.QUALIFIER_OBJCLASS);
        entityManager.persist(identity);

        CvTermXref ref1 = new CvTermXref(psimi, Xref.PUBMED_MI, identity);
        db.getDbXrefs().add(ref1);
        CvTermXref ref2 = new CvTermXref(psimi, CvTerm.PSI_MI, identity);
        psimi.getDbXrefs().add(ref2);
        CvTermXref ref3 = new CvTermXref(psimi, Xref.IDENTITY_MI, identity);
        identity.getDbXrefs().add(ref3);
        entityManager.flush();
        this.context.clearCache();
        return db;
    }

    private IntactCvTerm createExistingQualifier(IntactCvTerm psimi, IntactCvTerm identity) {
        // pre persist qualifier
        IntactCvTerm qualifier = new IntactCvTerm(Xref.SEE_ALSO);
        qualifier.setObjClass(IntactUtils.QUALIFIER_OBJCLASS);
        entityManager.persist(qualifier);

        CvTermXref ref1 = new CvTermXref(psimi, Xref.SEE_ALSO_MI, identity);
        qualifier.getDbXrefs().add(ref1);
        entityManager.flush();
        this.context.clearCache();
        return qualifier;
    }
}
