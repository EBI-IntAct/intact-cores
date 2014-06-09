package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Organism;
import uk.ac.ebi.intact.jami.model.extension.IntactOrganism;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.lang.reflect.InvocationTargetException;

/**
 * Unit test for OrganismSynchronizerTemplate
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/02/14</pre>
 */
public class OrganismSynchronizerTest extends AbstractDbSynchronizerTest<Organism,IntactOrganism>{

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

    @Override
    protected void testDeleteOtherProperties(IntactOrganism objectToTest) {

    }

    @Override
    protected Organism createDefaultJamiObject() {
        return null;
    }

    @Override
    protected void testUpdatedPropertiesAfterMerge(IntactOrganism objectToTest, IntactOrganism newObjToTest) {

    }

    @Override
    protected void updatePropertieDetachedInstance(IntactOrganism objectToTest) {

    }

    @Override
    protected IntactOrganism findObject(IntactOrganism objectToTest) {
        return null;
    }

    @Override
    protected void initSynchronizer() {

    }

    @Override
    protected void testPersistedProperties(IntactOrganism persistedObject) {

    }

    @Override
    protected void testNonPersistedProperties(IntactOrganism objectToTest) {

    }

    @Override
    protected IntactOrganism createDefaultObject() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return null;
    }

    /*@Override
    protected void testDeleteOtherProperties(IntactOrganism objectToTest) {
        Assert.assertNull(entityManager.find(Preference.class, objectToTest.getPreferences().iterator().next().getAc()));
        Assert.assertNotNull(entityManager.find(Role.class, objectToTest.getRoles().iterator().next().getAc()));
    }

    @Override
    protected Organism createDefaultJamiObject() {
        return null;
    }

    @Override
    protected void testUpdatedPropertiesAfterMerge(IntactOrganism objectToTest, IntactOrganism newObjToTest) {
        Assert.assertEquals(objectToTest.getAc(), newObjToTest.getAc());
        Assert.assertEquals(2, newObjToTest.getRoles().size());
        Iterator<Role> roleIterator = newObjToTest.getRoles().iterator();
        Role role2 = roleIterator.next();
        Assert.assertNotNull(role2.getAc());
        Assert.assertNotNull(roleIterator.next().getAc());
        Assert.assertEquals(2, newObjToTest.getPreferences().size());
        Iterator<Preference> preferenceIterator = newObjToTest.getPreferences().iterator();
        Preference pref2 = preferenceIterator.next();
        Assert.assertNotNull(pref2.getAc());
        Assert.assertNotNull(preferenceIterator.next().getAc());
        Assert.assertEquals("lastName2", newObjToTest.getLastName());
    }

    @Override
    protected void updatePropertieDetachedInstance(IntactOrganism objectToTest) {
        objectToTest.setLastName("lastName2");
        objectToTest.getRoles().add(new Role("REVIEWER"));
        objectToTest.getPreferences().add(new Preference("key2", "value2"));
    }

    @Override
    protected IntactOrganism findObject(IntactOrganism objectToTest) {
        return entityManager.find(User.class, objectToTest.getAc());
    }

    @Override
    protected void initSynchronizer() {
        this.synchronizer = new UserSynchronizer(this.context);
    }

    @Override
    protected void testPersistedProperties(IntactOrganism persistedObject) {
        Assert.assertNotNull(persistedObject.getAc());
        Assert.assertEquals("default", persistedObject.getLogin());
        Assert.assertEquals("firstName", persistedObject.getFirstName());
        Assert.assertEquals("lastName", persistedObject.getLastName());
        Assert.assertEquals("name@ebi.ac.uk", persistedObject.getEmail());
        Assert.assertEquals(1, persistedObject.getRoles().size());
        Role role = persistedObject.getRoles().iterator().next();
        Assert.assertNotNull(role.getAc());
        Assert.assertEquals(1, persistedObject.getPreferences().size());
        Preference pref = persistedObject.getPreferences().iterator().next();
        Assert.assertNotNull(pref.getAc());
    }

    @Override
    protected void testNonPersistedProperties(IntactOrganism persistedObject) {
        Assert.assertNull(persistedObject.getAc());
        Assert.assertEquals("default", persistedObject.getLogin());
        Assert.assertEquals("firstName", persistedObject.getFirstName());
        Assert.assertEquals("lastName", persistedObject.getLastName());
        Assert.assertEquals("name@ebi.ac.uk", persistedObject.getEmail());
        Assert.assertEquals(1, persistedObject.getRoles().size());
        Role role = persistedObject.getRoles().iterator().next();
        Assert.assertNotNull(role.getAc());
        Assert.assertEquals(1, persistedObject.getPreferences().size());
        Preference pref = persistedObject.getPreferences().iterator().next();
        Assert.assertNull(pref.getAc());
    }

    @Override
    protected IntactOrganism createDefaultObject() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return IntactTestUtils.createCuratorUser();
    }*/
}
