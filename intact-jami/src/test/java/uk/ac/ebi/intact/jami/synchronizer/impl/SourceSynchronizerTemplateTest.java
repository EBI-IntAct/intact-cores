package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import org.hibernate.Hibernate;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.Source;
import psidev.psi.mi.jami.model.Xref;
import psidev.psi.mi.jami.model.impl.DefaultPublication;
import psidev.psi.mi.jami.utils.XrefUtils;
import uk.ac.ebi.intact.jami.IntactTestUtils;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.lang.reflect.InvocationTargetException;

/**
 * Unit test for SourceSynchronizerTemplate
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/02/14</pre>
 */
public class SourceSynchronizerTemplateTest extends AbstractDbSynchronizerTest<Source, IntactSource>{

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_all() throws PersisterException, FinderException, SynchronizerException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_find() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        find_local_cache(false);
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_delete() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        delete();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_properties() throws PersisterException, FinderException, SynchronizerException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        synchronizeProperties();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_not_persist() throws PersisterException, FinderException, SynchronizerException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        synchronize_not_persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_persist() throws PersisterException, FinderException, SynchronizerException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        synchronize_persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_jami() throws PersisterException, FinderException, SynchronizerException {
        persist_jami();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_merge1() throws PersisterException, FinderException, SynchronizerException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        merge_test1();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_merge2() throws PersisterException, FinderException, SynchronizerException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        merge_test2();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_shortlabel_synch() throws PersisterException, FinderException, SynchronizerException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        persist();
        this.entityManager.flush();
        Source sameLabel = IntactTestUtils.createIntactSource();
        sameLabel.setMIIdentifier("MI:xxxx");
       sameLabel = ((SourceSynchronizer) this.synchronizer).synchronize(sameLabel, true);
        this.entityManager.flush();
       Assert.assertEquals("IntAct-1", sameLabel.getShortName());
    }

    @Override
    protected Source createDefaultJamiObject() {
        return IntactTestUtils.createSource();
    }

    @Override
    protected void testUpdatedPropertiesAfterMerge(IntactSource objectToTest, IntactSource newObjToTest) {
        Assert.assertEquals(objectToTest.getAc(), newObjToTest.getAc());
        Assert.assertEquals("updated fullName", newObjToTest.getFullName());
        Assert.assertEquals("xxxx", newObjToTest.getPublication().getPubmedId());
        Assert.assertTrue(newObjToTest.getXrefs().contains(XrefUtils.createPrimaryXref(Xref.PUBMED, Xref.PUBMED_MI, "xxxx")));
    }

    @Override
    protected void updatePropertieDetachedInstance(IntactSource objectToTest) {
        objectToTest.setFullName("updated fullName");
        objectToTest.setPublication(new DefaultPublication("xxxx"));
    }

    @Override
    protected void testDeleteOtherProperties(IntactSource objectToTest) {
        Assert.assertNull(entityManager.find(SourceAlias.class, ((SourceAlias) objectToTest.getSynonyms().iterator().next()).getAc()));
        Assert.assertNull(entityManager.find(SourceXref.class, ((SourceXref) objectToTest.getDbXrefs().iterator().next()).getAc()));
        Assert.assertNull(entityManager.find(SourceAnnotation.class, ((SourceAnnotation) objectToTest.getDbAnnotations().iterator().next()).getAc()));
    }

    @Override
    protected void initPropertiesBeforeDetaching(IntactSource reloadedObject){
        Hibernate.initialize(reloadedObject.getDbAnnotations());
        Hibernate.initialize(reloadedObject.getDbXrefs());
        Hibernate.initialize(reloadedObject.getSynonyms());
    }

    @Override
    protected IntactSource findObject(IntactSource objectToTest) {
        return entityManager.find(IntactSource.class, objectToTest.getAc());
    }

    @Override
    protected void initSynchronizer() {
        this.synchronizer = new SourceSynchronizer(this.context);
    }

    @Override
    protected void testPersistedProperties(IntactSource objectToTest) {
        Assert.assertNotNull(objectToTest.getAc());
        Assert.assertEquals("IntAct", objectToTest.getShortName());
        Assert.assertEquals("Molecular Interaction Database", objectToTest.getFullName());
        Assert.assertEquals(2, objectToTest.getAnnotations().size());
        Assert.assertEquals("http://www.ebi.ac.uk/intact/", objectToTest.getUrl());
        Assert.assertEquals("postalAddress", objectToTest.getPostalAddress());
        Assert.assertEquals(1, objectToTest.getSynonyms().size());
        Assert.assertEquals(2, objectToTest.getXrefs().size());
        Assert.assertEquals(2, objectToTest.getIdentifiers().size());
        Assert.assertEquals(3, objectToTest.getDbXrefs().size());
        Assert.assertNotNull(objectToTest.getPublication());
        Assert.assertNull(((IntactPublication)objectToTest.getPublication()).getAc());
        Assert.assertTrue(objectToTest.getXrefs().contains(XrefUtils.createPrimaryXref(Xref.PUBMED, Xref.PUBMED_MI, "12345")));
        Assert.assertTrue(objectToTest.getXrefs().contains(XrefUtils.createXrefWithQualifier(Xref.IMEX, Xref.IMEX_MI, "IM-1-1",
                Xref.SEE_ALSO, Xref.SEE_ALSO_MI)));
        Assert.assertNotNull(psidev.psi.mi.jami.utils.AnnotationUtils.collectFirstAnnotationWithTopic(objectToTest.getAnnotations(),
                Annotation.URL_MI, Annotation.URL));
        Assert.assertNotNull(psidev.psi.mi.jami.utils.AnnotationUtils.collectFirstAnnotationWithTopic(objectToTest.getAnnotations(),
                null, Annotation.POSTAL_ADDRESS));

        Assert.assertEquals(2, objectToTest.getIdentifiers().size());
        Assert.assertEquals(objectToTest.getAc(), XrefUtils.collectFirstIdentifierWithDatabase(objectToTest.getIdentifiers(), null, "intact").getId());
    }

    @Override
    protected void testNonPersistedProperties(IntactSource objectToTest) {
        Assert.assertNull(objectToTest.getAc());
        Assert.assertEquals("IntAct", objectToTest.getShortName());
        Assert.assertEquals("Molecular Interaction Database", objectToTest.getFullName());
        Assert.assertEquals(2, objectToTest.getAnnotations().size());
        Assert.assertEquals("http://www.ebi.ac.uk/intact/", objectToTest.getUrl());
        Assert.assertEquals("postalAddress", objectToTest.getPostalAddress());
        Assert.assertEquals(1, objectToTest.getSynonyms().size());
        Assert.assertEquals(2, objectToTest.getXrefs().size());
        Assert.assertEquals(1, objectToTest.getIdentifiers().size());
        Assert.assertEquals(3, objectToTest.getDbXrefs().size());
        Assert.assertNotNull(objectToTest.getPublication());
        Assert.assertNull(((IntactPublication)objectToTest.getPublication()).getAc());
        Assert.assertTrue(objectToTest.getXrefs().contains(XrefUtils.createPrimaryXref(Xref.PUBMED, Xref.PUBMED_MI, "12345")));
        Assert.assertTrue(objectToTest.getXrefs().contains(XrefUtils.createXrefWithQualifier(Xref.IMEX, Xref.IMEX_MI, "IM-1-1",
                Xref.SEE_ALSO, Xref.SEE_ALSO_MI)));
        SourceAlias alias = (SourceAlias) objectToTest.getSynonyms().iterator().next();
        Assert.assertNull(alias.getAc());
        Assert.assertEquals("test synonym", alias.getName());
        Assert.assertNotNull(psidev.psi.mi.jami.utils.AnnotationUtils.collectFirstAnnotationWithTopic(objectToTest.getAnnotations(),
                Annotation.URL_MI, Annotation.URL));
        Assert.assertNotNull(psidev.psi.mi.jami.utils.AnnotationUtils.collectFirstAnnotationWithTopic(objectToTest.getAnnotations(),
                null, Annotation.POSTAL_ADDRESS));

        Assert.assertEquals(1, objectToTest.getIdentifiers().size());
        Assert.assertNull(XrefUtils.collectFirstIdentifierWithDatabase(objectToTest.getIdentifiers(), null, "intact"));
    }

    @Override
    protected IntactSource createDefaultObject() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return IntactTestUtils.createIntactSource();
    }
}
