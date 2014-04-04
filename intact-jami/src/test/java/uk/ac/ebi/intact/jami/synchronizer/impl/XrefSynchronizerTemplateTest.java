package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.IntactTestUtils;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Unit test for XrefSynchronizerTemplate
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/02/14</pre>
 */
public class XrefSynchronizerTemplateTest extends AbstractDbSynchronizerTest<Xref, AbstractIntactXref>{

    private int testNumber = 1;

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_all() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(CvTermXref.class);
        this.testNumber = 1;
        persist();

        this.synchronizer.setIntactClass(InteractionXref.class);
        this.testNumber = 2;
        persist();

        this.synchronizer.setIntactClass(InteractorXref.class);
        this.testNumber = 3;
        persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_with_existing_db_qualifier() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        IntactCvTerm existingDb = createExistingDatabase();
        IntactCvTerm existingQualifier = createExistingQualifier((IntactCvTerm)existingDb.getIdentifiers().iterator().next().getDatabase(),
                (IntactCvTerm)existingDb.getIdentifiers().iterator().next().getQualifier());

        CvTermXref cvXref = IntactTestUtils.createPubmedXrefNoQualifier(CvTermXref.class);

        InteractionXref interactionXref = IntactTestUtils.createPubmedXrefNoQualifier(InteractionXref.class, "123456");

        InteractorXref interactorXref = IntactTestUtils.createXrefSeeAlso(InteractorXref.class);

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
    public void test_persist_with_detached_type() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // pre persist alias synonym
        IntactCvTerm existingDb = createExistingDatabase();
        IntactCvTerm existingQualifier = createExistingQualifier((IntactCvTerm) existingDb.getIdentifiers().iterator().next().getDatabase(),
                (IntactCvTerm) existingDb.getIdentifiers().iterator().next().getQualifier());

        entityManager.detach(existingDb);
        entityManager.detach(existingQualifier);

        CvTermXref cvXref = IntactTestUtils.createPubmedXrefNoQualifier(CvTermXref.class);
        cvXref.setDatabase(existingDb);

        InteractionXref interactionXref = IntactTestUtils.createPubmedXrefNoQualifier(InteractionXref.class, "123456");
        interactionXref.setDatabase(existingDb);

        InteractorXref interactorXref = IntactTestUtils.createXrefSeeAlso(InteractorXref.class);
        interactorXref.setQualifier(existingQualifier);

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
    public void test_find() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(CvTermXref.class);
        this.testNumber = 1;
        find_no_cache();

        this.synchronizer.setIntactClass(InteractionXref.class);
        this.testNumber = 2;
        find_no_cache();

        this.synchronizer.setIntactClass(InteractorXref.class);
        this.testNumber = 3;
        find_no_cache();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_delete() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(CvTermXref.class);
        this.testNumber = 1;
        delete();

        this.synchronizer.setIntactClass(InteractionXref.class);
        this.testNumber = 2;
        delete();

        this.synchronizer.setIntactClass(InteractorXref.class);
        this.testNumber = 3;
        delete();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_properties() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(CvTermXref.class);
        this.testNumber = 1;
        synchronizeProperties();

        this.synchronizer.setIntactClass(InteractionXref.class);
        this.testNumber = 2;
        synchronizeProperties();

        this.synchronizer.setIntactClass(InteractorXref.class);
        this.testNumber = 3;
        synchronizeProperties();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_not_persist() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(CvTermXref.class);
        this.testNumber = 1;
        synchronize_not_persist();

        this.synchronizer.setIntactClass(InteractionXref.class);
        this.testNumber = 2;
        synchronize_not_persist();

        this.synchronizer.setIntactClass(InteractorXref.class);
        this.testNumber = 3;
        synchronize_not_persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_persist() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(CvTermXref.class);
        this.testNumber = 1;
        synchronize_persist();

        this.synchronizer.setIntactClass(InteractionXref.class);
        this.testNumber = 2;
        synchronize_persist();

        this.synchronizer.setIntactClass(InteractorXref.class);
        this.testNumber = 3;
        synchronize_persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_jami() throws PersisterException, FinderException, SynchronizerException {
        this.synchronizer.setIntactClass(CvTermXref.class);
        this.testNumber = 1;
        persist_jami();

        this.synchronizer.setIntactClass(InteractionXref.class);
        this.testNumber = 2;
        persist_jami();

        this.synchronizer.setIntactClass(InteractorXref.class);
        this.testNumber = 3;
        persist_jami();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_merge1() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(CvTermXref.class);
        this.testNumber = 1;
        merge_test1();

        this.synchronizer.setIntactClass(InteractionXref.class);
        this.testNumber = 2;
        merge_test1();

        this.synchronizer.setIntactClass(InteractorXref.class);
        this.testNumber = 3;
        merge_test1();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_merge2() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(CvTermXref.class);
        this.testNumber = 1;
        merge_test2();

        this.synchronizer.setIntactClass(InteractionXref.class);
        this.testNumber = 2;
        merge_test2();

        this.synchronizer.setIntactClass(InteractorXref.class);
        this.testNumber = 3;
        merge_test2();
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

    @Override
    protected void testDeleteOtherProperties(AbstractIntactXref objectToTest) {
        // nothing to do
    }

    @Override
    protected Xref createDefaultJamiObject() {
        if (testNumber == 1){
            return IntactTestUtils.createPubmedXrefNoQualifier();
        }
        else if (testNumber == 2) {
            return IntactTestUtils.createPubmedXrefNoQualifier("123456");
        }
        else {
            return IntactTestUtils.createXrefSeeAlso();
        }
    }

    @Override
    protected void testUpdatedPropertiesAfterMerge(AbstractIntactXref objectToTest, AbstractIntactXref newObjToTest) {
         Assert.assertEquals(objectToTest.getAc(), newObjToTest.getAc());
         Assert.assertEquals("newId", newObjToTest.getId());
        Assert.assertEquals(IntactUtils.createMIQualifier(Xref.PRIMARY, Xref.PRIMARY_MI), newObjToTest.getQualifier());
    }

    @Override
    protected void updatePropertieDetachedInstance(AbstractIntactXref objectToTest) {
         objectToTest.setId("newId");
         objectToTest.setQualifier(IntactUtils.createMIQualifier(Xref.PRIMARY, Xref.PRIMARY_MI));
    }

    @Override
    protected AbstractIntactXref findObject(AbstractIntactXref objectToTest) {
        if (testNumber == 1){
           return entityManager.find(CvTermXref.class, objectToTest.getAc());
        }
        else if (testNumber == 2){
            return entityManager.find(InteractionXref.class, objectToTest.getAc());
        }
        else{
            return entityManager.find(InteractorXref.class, objectToTest.getAc());
        }
    }

    @Override
    protected void initSynchronizer() {
        this.synchronizer = new XrefSynchronizerTemplate(this.context, AbstractIntactXref.class);
    }

    @Override
    protected void testPersistedProperties(AbstractIntactXref persistedObject) {
         if (testNumber == 1){
             Assert.assertNotNull(persistedObject.getAc());
             Assert.assertNotNull(persistedObject.getDatabase());
             Assert.assertNull(persistedObject.getQualifier());
             IntactCvTerm db = (IntactCvTerm)persistedObject.getDatabase();
             Assert.assertNotNull(db.getAc());
             Assert.assertEquals(persistedObject.getDatabase(), IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI));
             Assert.assertEquals("12345", persistedObject.getId());
         }
        else if (testNumber == 2){

             Assert.assertNotNull(persistedObject.getAc());
             Assert.assertNotNull(persistedObject.getDatabase());
             Assert.assertNull(persistedObject.getQualifier());
             IntactCvTerm db2 = (IntactCvTerm)persistedObject.getDatabase();
             Assert.assertNotNull(db2.getAc());
             Assert.assertEquals(persistedObject.getDatabase(), IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI));
             Assert.assertEquals("123456", persistedObject.getId());
         }
        else{
             Assert.assertNotNull(persistedObject.getAc());
             Assert.assertNotNull(persistedObject.getDatabase());
             Assert.assertNotNull(persistedObject.getQualifier());
             IntactCvTerm db3 = (IntactCvTerm)persistedObject.getDatabase();
             Assert.assertNotNull(db3.getAc());
             Assert.assertEquals(persistedObject.getDatabase(), IntactUtils.createMIDatabase(Xref.IMEX, Xref.IMEX_MI));
             Assert.assertEquals("IM-1-1", persistedObject.getId());
             IntactCvTerm qualifier = (IntactCvTerm)persistedObject.getQualifier();
             Assert.assertNotNull(qualifier.getAc());
             Assert.assertEquals(persistedObject.getQualifier(), IntactUtils.createMIQualifier(Xref.SEE_ALSO, Xref.SEE_ALSO_MI));
        }
    }

    @Override
    protected void testNonPersistedProperties(AbstractIntactXref persistedObject) {
        if (testNumber == 1){
            Assert.assertNull(persistedObject.getAc());
            Assert.assertNotNull(persistedObject.getDatabase());
            Assert.assertNull(persistedObject.getQualifier());
            IntactCvTerm db = (IntactCvTerm)persistedObject.getDatabase();
            Assert.assertNotNull(db.getAc());
            Assert.assertEquals(persistedObject.getDatabase(), IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI));
            Assert.assertEquals("12345", persistedObject.getId());
        }
        else if (testNumber == 2){

            Assert.assertNull(persistedObject.getAc());
            Assert.assertNotNull(persistedObject.getDatabase());
            Assert.assertNull(persistedObject.getQualifier());
            IntactCvTerm db2 = (IntactCvTerm)persistedObject.getDatabase();
            Assert.assertNotNull(db2.getAc());
            Assert.assertEquals(persistedObject.getDatabase(), IntactUtils.createMIDatabase(Xref.PUBMED, Xref.PUBMED_MI));
            Assert.assertEquals("123456", persistedObject.getId());
        }
        else{
            Assert.assertNull(persistedObject.getAc());
            Assert.assertNotNull(persistedObject.getDatabase());
            Assert.assertNotNull(persistedObject.getQualifier());
            IntactCvTerm db3 = (IntactCvTerm)persistedObject.getDatabase();
            Assert.assertNotNull(db3.getAc());
            Assert.assertEquals(persistedObject.getDatabase(), IntactUtils.createMIDatabase(Xref.IMEX, Xref.IMEX_MI));
            Assert.assertEquals("IM-1-1", persistedObject.getId());
            IntactCvTerm qualifier = (IntactCvTerm)persistedObject.getQualifier();
            Assert.assertNotNull(qualifier.getAc());
            Assert.assertEquals(persistedObject.getQualifier(), IntactUtils.createMIQualifier(Xref.SEE_ALSO, Xref.SEE_ALSO_MI));
        }
    }

    @Override
    protected AbstractIntactXref createDefaultObject() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (testNumber == 1){
            return IntactTestUtils.createPubmedXrefNoQualifier(CvTermXref.class);
        }
        else if (testNumber == 2) {
            return IntactTestUtils.createPubmedXrefNoQualifier(InteractionXref.class, "123456");
        }
        else {
            return IntactTestUtils.createXrefSeeAlso(InteractorXref.class);
        }
    }
}
