package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import org.hibernate.Hibernate;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Interactor;
import psidev.psi.mi.jami.model.impl.DefaultCvTerm;
import psidev.psi.mi.jami.utils.XrefUtils;
import uk.ac.ebi.intact.jami.IntactTestUtils;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.lang.reflect.InvocationTargetException;

/**
 * Unit test for InteractorSynchronizerTemplate
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/02/14</pre>
 */
public class InteractorSynchronizerTemplateTest extends AbstractDbSynchronizerTest<Interactor,IntactInteractor>{

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_all() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_deleted() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        delete();
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
    public void test_synchronize_properties() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        synchronizeProperties();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_not_persist() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        synchronize_not_persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_persist() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        synchronize_persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_merge() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        merge_test1();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_merge2() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        merge_test2();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_jami() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        persist_jami();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_commonName_synch() throws PersisterException, FinderException, SynchronizerException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        persist();
        this.entityManager.flush();

        Interactor sameLabel = IntactTestUtils.createDefaultInteractor();
        sameLabel.getOrganism().setTaxId(9055);
        sameLabel = ((InteractorSynchronizerTemplate<Interactor, IntactInteractor>) this.synchronizer).synchronize(sameLabel, true);
        this.entityManager.flush();
        Assert.assertEquals("test interactor-1", sameLabel.getShortName());
    }

    @Override
    protected void initPropertiesBeforeDetaching(IntactInteractor reloadedObject){
        Hibernate.initialize(reloadedObject.getDbAnnotations());
        Hibernate.initialize(reloadedObject.getDbXrefs());
        Hibernate.initialize(reloadedObject.getDbAliases());
    }

    @Override
    protected void testDeleteOtherProperties(IntactInteractor objectToTest) {
        Assert.assertNull(entityManager.find(InteractorAlias.class, ((InteractorAlias)objectToTest.getAliases().iterator().next()).getAc()));
        Assert.assertNull(entityManager.find(InteractorAlias.class, ((InteractorXref) objectToTest.getDbXrefs().iterator().next()).getAc()));
        Assert.assertNull(entityManager.find(InteractorAlias.class, ((InteractorAnnotation)objectToTest.getDbAnnotations().iterator().next()).getAc()));
        Assert.assertNotNull(entityManager.find(IntactCvTerm.class, ((IntactCvTerm)objectToTest.getInteractorType()).getAc()));
    }

    @Override
    protected Interactor createDefaultJamiObject() {
        return IntactTestUtils.createDefaultInteractor();
    }

    @Override
    protected void testUpdatedPropertiesAfterMerge(IntactInteractor objectToTest, IntactInteractor persistedObject) {
        Assert.assertEquals(objectToTest.getAc(), persistedObject.getAc());
        Assert.assertEquals("new name", persistedObject.getShortName());
        Assert.assertEquals("new type", persistedObject.getInteractorType().getShortName());
    }

    @Override
    protected void updatePropertieDetachedInstance(IntactInteractor objectToTest) {
        objectToTest.setShortName("new name");
        objectToTest.setInteractorType(new DefaultCvTerm("new type"));
    }

    @Override
    protected IntactInteractor findObject(IntactInteractor objectToTest) {
        return entityManager.find(IntactInteractor.class, objectToTest.getAc());
    }

    @Override
    protected void initSynchronizer() {
        this.synchronizer = new InteractorSynchronizerTemplate(this.context, IntactInteractor.class);
    }

    @Override
    protected void testPersistedProperties(IntactInteractor persistedObject) {
        Assert.assertNotNull(persistedObject.getAc());
        Assert.assertEquals("test interactor", persistedObject.getShortName());
        Assert.assertEquals("Full interactor name", persistedObject.getFullName());
        Assert.assertNotNull(persistedObject.getOrganism());
        Assert.assertNotNull(((IntactOrganism) persistedObject.getOrganism()).getAc());
        Assert.assertEquals(9606, persistedObject.getOrganism().getTaxId());
        Assert.assertEquals(1, persistedObject.getAliases().size());
        Assert.assertNotNull(((InteractorAlias) persistedObject.getAliases().iterator().next()).getAc());
        Assert.assertEquals(1, persistedObject.getAnnotations().size());
        Assert.assertNotNull(((InteractorAnnotation) persistedObject.getAnnotations().iterator().next()).getAc());
        Assert.assertEquals(1, persistedObject.getDbXrefs().size());
        Assert.assertNotNull(((InteractorXref) persistedObject.getDbXrefs().iterator().next()).getAc());
        Assert.assertNotNull(persistedObject.getInteractorType());
        Assert.assertNotNull(((IntactCvTerm) persistedObject.getInteractorType()).getAc());

        Assert.assertEquals(1, persistedObject.getIdentifiers().size());
        Assert.assertEquals(persistedObject.getAc(), XrefUtils.collectFirstIdentifierWithDatabase(persistedObject.getIdentifiers(), null, "intact").getId());
    }

    @Override
    protected void testNonPersistedProperties(IntactInteractor persistedObject) {
        Assert.assertNull(persistedObject.getAc());
        Assert.assertEquals("test interactor", persistedObject.getShortName());
        Assert.assertEquals("Full interactor name", persistedObject.getFullName());
        Assert.assertNotNull(persistedObject.getOrganism());
        Assert.assertNotNull(((IntactOrganism) persistedObject.getOrganism()).getAc());
        Assert.assertEquals(9606, persistedObject.getOrganism().getTaxId());
        Assert.assertEquals(1, persistedObject.getAliases().size());
        Assert.assertNull(((InteractorAlias) persistedObject.getAliases().iterator().next()).getAc());
        Assert.assertEquals(1, persistedObject.getAnnotations().size());
        Assert.assertNull(((InteractorAnnotation) persistedObject.getAnnotations().iterator().next()).getAc());
        Assert.assertEquals(1, persistedObject.getDbXrefs().size());
        Assert.assertNull(((InteractorXref) persistedObject.getDbXrefs().iterator().next()).getAc());
        Assert.assertNotNull(persistedObject.getInteractorType());
        Assert.assertNotNull(((IntactCvTerm) persistedObject.getInteractorType()).getAc());

        Assert.assertEquals(0, persistedObject.getIdentifiers().size());
    }

    @Override
    protected IntactInteractor createDefaultObject() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return IntactTestUtils.createIntactInteractor();
    }
}
