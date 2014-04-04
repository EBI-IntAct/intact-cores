package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import org.hibernate.Hibernate;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import psidev.psi.mi.jami.utils.CvTermUtils;
import psidev.psi.mi.jami.utils.XrefUtils;
import uk.ac.ebi.intact.jami.IntactTestUtils;
import uk.ac.ebi.intact.jami.model.extension.CvTermAlias;
import uk.ac.ebi.intact.jami.model.extension.CvTermAnnotation;
import uk.ac.ebi.intact.jami.model.extension.CvTermXref;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Unit test for AliasSynchronizerTemplate
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/02/14</pre>
 */
public class CvTermSynchronizerTemplateTest extends AbstractDbSynchronizerTest<CvTerm, IntactCvTerm>{

    private int testNumber=1;

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_all() throws PersisterException, FinderException, SynchronizerException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.ALIAS_TYPE_OBJCLASS);
        this.testNumber = 1;
        persist();

        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.TOPIC_OBJCLASS);
        this.testNumber = 2;
        persist();

        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.DATABASE_OBJCLASS);
        this.testNumber = 3;
        persist();

        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.CONFIDENCE_TYPE_OBJCLASS);
        this.testNumber = 4;
        persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_with_existing_cv() throws PersisterException, FinderException, SynchronizerException {
        IntactCvTerm existingType = createExistingType();

        // simple cv with xrefs
        IntactCvTerm aliasType = IntactTestUtils.createCvTermWithXrefs();

        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.ALIAS_TYPE_OBJCLASS);
        IntactCvTerm newAliasType = (IntactCvTerm)this.synchronizer.synchronize(aliasType, true);

        Assert.assertEquals(newAliasType.getAc(), existingType.getAc());

    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_find() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.ALIAS_TYPE_OBJCLASS);
        this.testNumber = 1;
        find_local_cache(false);

        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.TOPIC_OBJCLASS);
        this.testNumber = 2;
        find_local_cache(false);

        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.DATABASE_OBJCLASS);
        this.testNumber = 3;
        find_local_cache(false);

        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.CONFIDENCE_TYPE_OBJCLASS);
        this.testNumber = 4;
        find_local_cache(false);
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_delete() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.ALIAS_TYPE_OBJCLASS);
        this.testNumber = 1;
        delete();

        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.TOPIC_OBJCLASS);
        this.testNumber = 2;
        delete();

        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.DATABASE_OBJCLASS);
        this.testNumber = 3;
        delete();

        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.CONFIDENCE_TYPE_OBJCLASS);
        this.testNumber = 4;
        delete();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_properties() throws PersisterException, FinderException, SynchronizerException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.ALIAS_TYPE_OBJCLASS);
        this.testNumber = 1;
        synchronizeProperties();

        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.TOPIC_OBJCLASS);
        this.testNumber = 2;
        synchronizeProperties();

        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.DATABASE_OBJCLASS);
        this.testNumber = 3;
        synchronizeProperties();

        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.CONFIDENCE_TYPE_OBJCLASS);
        this.testNumber = 4;
        synchronizeProperties();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_not_persist() throws PersisterException, FinderException, SynchronizerException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.ALIAS_TYPE_OBJCLASS);
        this.testNumber = 1;
        synchronize_not_persist();

        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.TOPIC_OBJCLASS);
        this.testNumber = 2;
        synchronize_not_persist();

        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.DATABASE_OBJCLASS);
        this.testNumber = 3;
        synchronize_not_persist();

        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.CONFIDENCE_TYPE_OBJCLASS);
        this.testNumber = 4;
        synchronize_not_persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_persist() throws PersisterException, FinderException, SynchronizerException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.ALIAS_TYPE_OBJCLASS);
        this.testNumber = 1;
        synchronize_persist();

        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.TOPIC_OBJCLASS);
        this.testNumber = 2;
        synchronize_persist();

        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.DATABASE_OBJCLASS);
        this.testNumber = 3;
        synchronize_persist();

        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.CONFIDENCE_TYPE_OBJCLASS);
        this.testNumber = 4;
        synchronize_persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_jami() throws PersisterException, FinderException, SynchronizerException {
        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.ALIAS_TYPE_OBJCLASS);
        this.testNumber = 1;
        persist_jami();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_merge1() throws PersisterException, FinderException, SynchronizerException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.ALIAS_TYPE_OBJCLASS);
        this.testNumber = 1;
        merge_test1();

        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.TOPIC_OBJCLASS);
        this.testNumber = 2;
        merge_test1();

        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.DATABASE_OBJCLASS);
        this.testNumber = 3;
        merge_test1();

        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.CONFIDENCE_TYPE_OBJCLASS);
        this.testNumber = 4;
        merge_test1();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_merge2() throws PersisterException, FinderException, SynchronizerException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.ALIAS_TYPE_OBJCLASS);
        this.testNumber = 1;
        merge_test2();

        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.TOPIC_OBJCLASS);
        this.testNumber = 2;
        merge_test2();

        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.DATABASE_OBJCLASS);
        this.testNumber = 3;
        merge_test2();

        ((CvTermSynchronizer)this.synchronizer).setObjClass(IntactUtils.CONFIDENCE_TYPE_OBJCLASS);
        this.testNumber = 4;
        merge_test2();
    }

    private IntactCvTerm createExistingType() {
        // pre persist alias synonym
        IntactCvTerm aliasSynonym = new IntactCvTerm(Alias.GENE_NAME);
        aliasSynonym.setObjClass(IntactUtils.ALIAS_TYPE_OBJCLASS);
        entityManager.persist(aliasSynonym);
        IntactCvTerm psimi = new IntactCvTerm(CvTerm.PSI_MI);
        psimi.setObjClass(IntactUtils.DATABASE_OBJCLASS);
        entityManager.persist(psimi);
        IntactCvTerm identity = new IntactCvTerm(Xref.IDENTITY);
        identity.setObjClass(IntactUtils.QUALIFIER_OBJCLASS);
        entityManager.persist(identity);

        CvTermXref ref1 = new CvTermXref(psimi, Alias.GENE_NAME_MI, identity);
        aliasSynonym.getDbXrefs().add(ref1);
        CvTermXref ref2 = new CvTermXref(psimi, CvTerm.PSI_MI, identity);
        psimi.getDbXrefs().add(ref2);
        CvTermXref ref3 = new CvTermXref(psimi, Xref.IDENTITY_MI, identity);
        identity.getDbXrefs().add(ref3);
        entityManager.flush();
        this.context.clearCache();
        return aliasSynonym;
    }

    @Override
    protected CvTerm createDefaultJamiObject() {
        return CvTermUtils.createGeneNameAliasType();
    }

    @Override
    protected void testUpdatedPropertiesAfterMerge(IntactCvTerm objectToTest, IntactCvTerm newObjToTest) {
        Assert.assertEquals(objectToTest.getAc(), newObjToTest.getAc());
        Assert.assertEquals("updated definition", newObjToTest.getDefinition());
    }

    @Override
    protected void updatePropertieDetachedInstance(IntactCvTerm objectToTest) {
        objectToTest.setDefinition("updated definition");
    }

    @Override
    protected void initPropertiesBeforeDetaching(IntactCvTerm reloadedObject){
        Hibernate.initialize(reloadedObject.getDbAnnotations());
        Hibernate.initialize(reloadedObject.getDbXrefs());
    }

    @Override
    protected IntactCvTerm findObject(IntactCvTerm objectToTest) {
        return entityManager.find(IntactCvTerm.class, objectToTest.getAc());
    }

    @Override
    protected void initSynchronizer() {
        this.synchronizer = new CvTermSynchronizer(this.context);
    }

    @Override
    protected void testPersistedProperties(IntactCvTerm objectToTest) {
        // simple cv with xrefs
        if (testNumber == 1){
            Assert.assertNotNull(objectToTest.getAc());
            Assert.assertEquals(Alias.GENE_NAME, objectToTest.getShortName());
            Assert.assertNull(objectToTest.getFullName());
            Assert.assertNull(objectToTest.getDefinition());
            Assert.assertTrue(objectToTest.getAnnotations().isEmpty());
            Assert.assertTrue(objectToTest.getSynonyms().isEmpty());
            Assert.assertTrue(objectToTest.getParents().isEmpty());
            Assert.assertTrue(objectToTest.getChildren().isEmpty());
            Assert.assertEquals(1, objectToTest.getDbXrefs().size());
            Assert.assertEquals(IntactUtils.ALIAS_TYPE_OBJCLASS, objectToTest.getObjClass());
            CvTermXref ref = (CvTermXref) objectToTest.getDbXrefs().iterator().next();
            Assert.assertNull(ref.getAc());
            Assert.assertEquals(Alias.GENE_NAME_MI, ref.getId());

            Assert.assertEquals(2, objectToTest.getIdentifiers().size());
            Assert.assertEquals(objectToTest.getAc(), XrefUtils.collectFirstIdentifierWithDatabase(objectToTest.getIdentifiers(), null, "intact").getId());

        }
        // cvs with parent/children
        else if (testNumber == 2){
            Assert.assertNotNull(objectToTest.getAc());
            Assert.assertEquals("test", objectToTest.getShortName());
            Assert.assertNull(objectToTest.getFullName());
            Assert.assertNull(objectToTest.getDefinition());
            Assert.assertTrue(objectToTest.getAnnotations().isEmpty());
            Assert.assertTrue(objectToTest.getSynonyms().isEmpty());
            Assert.assertEquals(1, objectToTest.getDbXrefs().size());
            Assert.assertTrue(objectToTest.getChildren().isEmpty());
            Assert.assertEquals(1, objectToTest.getParents().size());
            Assert.assertEquals(IntactUtils.TOPIC_OBJCLASS, objectToTest.getObjClass());
            CvTermXref ref = (CvTermXref) objectToTest.getDbXrefs().iterator().next();
            Assert.assertNull(ref.getAc());
            Assert.assertEquals("intact", ref.getDatabase().getShortName());
            Assert.assertTrue(ref.getId().startsWith("IA:"));

            IntactCvTerm parent = (IntactCvTerm)objectToTest.getParents().iterator().next();
            Assert.assertNotNull(parent.getAc());
            Assert.assertEquals(Annotation.CAUTION, parent.getShortName());
            Assert.assertNull(parent.getFullName());
            Assert.assertNull(parent.getDefinition());
            Assert.assertTrue(parent.getAnnotations().isEmpty());
            Assert.assertTrue(parent.getSynonyms().isEmpty());
            Assert.assertEquals(1, parent.getDbXrefs().size());
            Assert.assertTrue(parent.getParents().isEmpty());
            Assert.assertEquals(1, parent.getChildren().size());
            Assert.assertEquals(IntactUtils.TOPIC_OBJCLASS, parent.getObjClass());
            ref = (CvTermXref) parent.getDbXrefs().iterator().next();
            Assert.assertNull(ref.getAc());
            Assert.assertEquals(Annotation.CAUTION_MI, ref.getId());
            Assert.assertTrue(objectToTest == parent.getChildren().iterator().next());
        }
        // cvs with annotations and aliases
        else if (testNumber == 3){
            Assert.assertNotNull(objectToTest.getAc());
            Assert.assertEquals("test", objectToTest.getShortName());
            Assert.assertNull(objectToTest.getFullName());
            Assert.assertNull(objectToTest.getDefinition());
            Assert.assertEquals(1, objectToTest.getAnnotations().size());
            Assert.assertTrue(objectToTest.getParents().isEmpty());
            Assert.assertEquals(1, objectToTest.getDbXrefs().size());
            Assert.assertTrue(objectToTest.getChildren().isEmpty());
            Assert.assertEquals(1, objectToTest.getSynonyms().size());
            Assert.assertEquals(IntactUtils.DATABASE_OBJCLASS, objectToTest.getObjClass());
            CvTermAnnotation annot = (CvTermAnnotation) objectToTest.getAnnotations().iterator().next();
            Assert.assertNull(annot.getAc());
            CvTermAlias alias = (CvTermAlias) objectToTest.getSynonyms().iterator().next();
            Assert.assertNull(alias.getAc());
            Assert.assertEquals("test synonym", alias.getName());
            CvTermXref ref = (CvTermXref) objectToTest.getDbXrefs().iterator().next();
            Assert.assertNull(ref.getAc());
            Assert.assertEquals("intact", ref.getDatabase().getShortName());
            Assert.assertTrue(ref.getId().startsWith("IA:"));
        }
        // cvs with fullname and definition
        else {
            Assert.assertNotNull(objectToTest.getAc());
            Assert.assertEquals("test3", objectToTest.getShortName());
            Assert.assertEquals("Test Confidence", objectToTest.getFullName());
            Assert.assertEquals("Test Definition", objectToTest.getDefinition());
            Assert.assertEquals(0, objectToTest.getAnnotations().size());
            Assert.assertEquals(1, objectToTest.getDbAnnotations().size());
            Assert.assertTrue(objectToTest.getSynonyms().isEmpty());
            Assert.assertEquals(1, objectToTest.getDbXrefs().size());
            Assert.assertTrue(objectToTest.getChildren().isEmpty());
            Assert.assertTrue(objectToTest.getParents().isEmpty());
            Assert.assertEquals(IntactUtils.CONFIDENCE_TYPE_OBJCLASS, objectToTest.getObjClass());
            CvTermXref ref = (CvTermXref) objectToTest.getDbXrefs().iterator().next();
            Assert.assertNull(ref.getAc());
            Assert.assertEquals("intact", ref.getDatabase().getShortName());
            Assert.assertTrue(ref.getId().startsWith("IA:"));
            CvTermAnnotation annot = (CvTermAnnotation) objectToTest.getDbAnnotations().iterator().next();
            Assert.assertNull(annot.getAc());
            Assert.assertEquals(annot.getValue(), objectToTest.getDefinition());
            Assert.assertEquals("definition", annot.getTopic().getShortName());
        }
    }

    @Override
    protected void testNonPersistedProperties(IntactCvTerm objectToTest) {
        // simple cv with xrefs
        if (testNumber == 1){
            Assert.assertNull(objectToTest.getAc());
            Assert.assertEquals(Alias.GENE_NAME, objectToTest.getShortName());
            Assert.assertNull(objectToTest.getFullName());
            Assert.assertNull(objectToTest.getDefinition());
            Assert.assertTrue(objectToTest.getAnnotations().isEmpty());
            Assert.assertTrue(objectToTest.getSynonyms().isEmpty());
            Assert.assertTrue(objectToTest.getParents().isEmpty());
            Assert.assertTrue(objectToTest.getChildren().isEmpty());
            Assert.assertEquals(1, objectToTest.getDbXrefs().size());
            Assert.assertEquals(IntactUtils.ALIAS_TYPE_OBJCLASS, objectToTest.getObjClass());
            CvTermXref ref = (CvTermXref) objectToTest.getDbXrefs().iterator().next();
            Assert.assertNull(ref.getAc());
            Assert.assertEquals(Alias.GENE_NAME_MI, ref.getId());

            Assert.assertEquals(1, objectToTest.getIdentifiers().size());
            Assert.assertNull(XrefUtils.collectFirstIdentifierWithDatabase(objectToTest.getIdentifiers(), null, "intact"));

        }
        // cvs with parent/children
        else if (testNumber == 2){
            Assert.assertNull(objectToTest.getAc());
            Assert.assertEquals("test", objectToTest.getShortName());
            Assert.assertNull(objectToTest.getFullName());
            Assert.assertNull(objectToTest.getDefinition());
            Assert.assertTrue(objectToTest.getAnnotations().isEmpty());
            Assert.assertTrue(objectToTest.getSynonyms().isEmpty());
            Assert.assertEquals(1, objectToTest.getDbXrefs().size());
            Assert.assertTrue(objectToTest.getChildren().isEmpty());
            Assert.assertEquals(1, objectToTest.getParents().size());
            Assert.assertEquals(IntactUtils.TOPIC_OBJCLASS, objectToTest.getObjClass());
            CvTermXref ref = (CvTermXref) objectToTest.getDbXrefs().iterator().next();
            Assert.assertNull(ref.getAc());
            Assert.assertEquals("intact", ref.getDatabase().getShortName());
            Assert.assertTrue(ref.getId().startsWith("IA:"));

            IntactCvTerm parent = (IntactCvTerm)objectToTest.getParents().iterator().next();
            Assert.assertNotNull(parent.getAc());
            Assert.assertEquals(Annotation.CAUTION, parent.getShortName());
            Assert.assertNull(parent.getFullName());
            Assert.assertNull(parent.getDefinition());
            Assert.assertTrue(parent.getAnnotations().isEmpty());
            Assert.assertTrue(parent.getSynonyms().isEmpty());
            Assert.assertEquals(1, parent.getDbXrefs().size());
            Assert.assertTrue(parent.getParents().isEmpty());
            Assert.assertEquals(1, parent.getChildren().size());
            Assert.assertEquals(IntactUtils.TOPIC_OBJCLASS, parent.getObjClass());
            ref = (CvTermXref) parent.getDbXrefs().iterator().next();
            Assert.assertNull(ref.getAc());
            Assert.assertEquals(Annotation.CAUTION_MI, ref.getId());
            Assert.assertTrue(objectToTest == parent.getChildren().iterator().next());
        }
        // cvs with annotations and aliases
        else if (testNumber == 3){
            Assert.assertNull(objectToTest.getAc());
            Assert.assertEquals("test", objectToTest.getShortName());
            Assert.assertNull(objectToTest.getFullName());
            Assert.assertNull(objectToTest.getDefinition());
            Assert.assertEquals(1, objectToTest.getAnnotations().size());
            Assert.assertTrue(objectToTest.getParents().isEmpty());
            Assert.assertEquals(1, objectToTest.getDbXrefs().size());
            Assert.assertTrue(objectToTest.getChildren().isEmpty());
            Assert.assertEquals(1, objectToTest.getSynonyms().size());
            Assert.assertEquals(IntactUtils.DATABASE_OBJCLASS, objectToTest.getObjClass());
            CvTermAnnotation annot = (CvTermAnnotation) objectToTest.getAnnotations().iterator().next();
            Assert.assertNull(annot.getAc());
            CvTermAlias alias = (CvTermAlias) objectToTest.getSynonyms().iterator().next();
            Assert.assertNull(alias.getAc());
            Assert.assertEquals("test synonym", alias.getName());
            CvTermXref ref = (CvTermXref) objectToTest.getDbXrefs().iterator().next();
            Assert.assertNull(ref.getAc());
            Assert.assertEquals("intact", ref.getDatabase().getShortName());
            Assert.assertTrue(ref.getId().startsWith("IA:"));
        }
        // cvs with fullname and definition
        else {
            Assert.assertNull(objectToTest.getAc());
            Assert.assertEquals("test3", objectToTest.getShortName());
            Assert.assertEquals("Test Confidence", objectToTest.getFullName());
            Assert.assertEquals("Test Definition", objectToTest.getDefinition());
            Assert.assertEquals(0, objectToTest.getAnnotations().size());
            Assert.assertEquals(1, objectToTest.getDbAnnotations().size());
            Assert.assertTrue(objectToTest.getSynonyms().isEmpty());
            Assert.assertEquals(1, objectToTest.getDbXrefs().size());
            Assert.assertTrue(objectToTest.getChildren().isEmpty());
            Assert.assertTrue(objectToTest.getParents().isEmpty());
            Assert.assertEquals(IntactUtils.CONFIDENCE_TYPE_OBJCLASS, objectToTest.getObjClass());
            CvTermXref ref = (CvTermXref) objectToTest.getDbXrefs().iterator().next();
            Assert.assertNull(ref.getAc());
            Assert.assertEquals("intact", ref.getDatabase().getShortName());
            Assert.assertTrue(ref.getId().startsWith("IA:"));
            CvTermAnnotation annot = (CvTermAnnotation) objectToTest.getDbAnnotations().iterator().next();
            Assert.assertNull(annot.getAc());
            Assert.assertEquals(annot.getValue(), objectToTest.getDefinition());
            Assert.assertEquals("definition", annot.getTopic().getShortName());
        }
    }

    @Override
    protected IntactCvTerm createDefaultObject() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // simple cv with xrefs
        if (testNumber == 1){
            return IntactTestUtils.createCvTermWithXrefs();
        }
        // cvs with parent/children
        else if (testNumber == 2){
            return IntactTestUtils.createCvTermWithParent();
        }
        // cvs with annotations and aliases
        else if (testNumber == 3){
            return IntactTestUtils.createCvTermWithAnnotationsAndAliases();
        }
        // cvs with fullname and definition
        else {
            return IntactTestUtils.createCvWithDefinition();
        }
    }
}
