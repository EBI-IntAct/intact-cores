package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.jami.IntactTestUtils;
import uk.ac.ebi.intact.jami.model.user.Preference;
import uk.ac.ebi.intact.jami.model.user.Role;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

/**
 * Unit test for UserSynchronizerTemplate
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/02/14</pre>
 */
public class UserSynchronizerTest extends AbstractDbSynchronizerTest<User,User>{

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

    @Override
    protected void testDeleteOtherProperties(User objectToTest) {
        Assert.assertNull(entityManager.find(Preference.class, objectToTest.getPreferences().iterator().next().getAc()));
        Assert.assertNotNull(entityManager.find(Role.class, objectToTest.getRoles().iterator().next().getAc()));
    }

    @Override
    protected User createDefaultJamiObject() {
        return null;
    }

    @Override
    protected void testUpdatedPropertiesAfterMerge(User objectToTest, User newObjToTest) {
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
    protected void updatePropertieDetachedInstance(User objectToTest) {
        objectToTest.setLastName("lastName2");
        objectToTest.getRoles().add(new Role("REVIEWER"));
        objectToTest.getPreferences().add(new Preference("key2", "value2"));
    }

    @Override
    protected User findObject(User objectToTest) {
        return entityManager.find(User.class, objectToTest.getAc());
    }

    @Override
    protected void initSynchronizer() {
        this.synchronizer = new UserSynchronizer(this.context);
    }

    @Override
    protected void testPersistedProperties(User persistedObject) {
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
    protected void testNonPersistedProperties(User persistedObject) {
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
    protected User createDefaultObject() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return IntactTestUtils.createCuratorUser();
    }
}
