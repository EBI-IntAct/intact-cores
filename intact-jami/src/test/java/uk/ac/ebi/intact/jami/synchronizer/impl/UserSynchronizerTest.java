package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.model.user.Preference;
import uk.ac.ebi.intact.jami.model.user.Role;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.Iterator;

/**
 * Unit test for UserSynchronizerTemplate
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
public class UserSynchronizerTest {

    @PersistenceContext(unitName = "intact-core")
    private EntityManager entityManager;
    @PersistenceUnit(unitName = "intact-core", name = "intactEntityManagerFactory")
    private EntityManagerFactory intactEntityManagerFactory;

    private UserSynchronizer synchronizer;
    private SynchronizerContext context;

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_all() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new UserSynchronizer(this.context);

        User user = new User("default", "firstName", "lastName", "name@ebi.ac.uk");
        user.getPreferences().add(new Preference("key", "value"));
        user.getRoles().add(new Role("CURATOR"));
        this.synchronizer.persist(user);

        Assert.assertNotNull(user.getAc());
        Assert.assertEquals("default", user.getLogin());
        Assert.assertEquals("firstName", user.getFirstName());
        Assert.assertEquals("lastName", user.getLastName());
        Assert.assertEquals("name@ebi.ac.uk", user.getEmail());
        Assert.assertEquals(1, user.getRoles().size());
        Role role = user.getRoles().iterator().next();
        Assert.assertNotNull(role.getAc());
        Assert.assertEquals(1, user.getPreferences().size());
        Preference pref = user.getPreferences().iterator().next();
        Assert.assertNotNull(pref.getAc());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_deleted() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new UserSynchronizer(this.context);

        User user = new User("default", "firstName", "lastName", "name@ebi.ac.uk");
        user.getPreferences().add(new Preference("key", "value"));
        user.getRoles().add(new Role("CURATOR"));
        this.synchronizer.persist(user);
        this.entityManager.flush();

        this.synchronizer.delete(user);
        Assert.assertNull(entityManager.find(User.class, user.getAc()));
        Assert.assertNull(entityManager.find(Preference.class, user.getPreferences().iterator().next().getAc()));
        Assert.assertNotNull(entityManager.find(Role.class, user.getRoles().iterator().next().getAc()));
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_find() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new UserSynchronizer(this.context);

        User user = new User("default", "firstName", "lastName", "name@ebi.ac.uk");
        user.getPreferences().add(new Preference("key", "value"));
        user.getRoles().add(new Role("CURATOR"));
        this.synchronizer.persist(user);
        this.entityManager.flush();
        this.synchronizer.clearCache();

        Assert.assertNotNull(this.synchronizer.find(user));
        Assert.assertEquals(this.synchronizer.find(user), this.synchronizer.find(new User("default", "firstName2", "lastName2", "name2@ebi.ac.uk")));
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_properties() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new UserSynchronizer(this.context);

        User user = new User("default", "firstName", "lastName", "name@ebi.ac.uk");
        user.getPreferences().add(new Preference("key", "value"));
        user.getRoles().add(new Role("CURATOR"));
        this.synchronizer.synchronizeProperties(user);

        Assert.assertNull(user.getAc());
        Assert.assertEquals("default", user.getLogin());
        Assert.assertEquals("firstName", user.getFirstName());
        Assert.assertEquals("lastName", user.getLastName());
        Assert.assertEquals("name@ebi.ac.uk", user.getEmail());
        Assert.assertEquals(1, user.getRoles().size());
        Role role = user.getRoles().iterator().next();
        Assert.assertNotNull(role.getAc());
        Assert.assertEquals(1, user.getPreferences().size());
        Preference pref = user.getPreferences().iterator().next();
        Assert.assertNull(pref.getAc());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_not_persist() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new UserSynchronizer(this.context);

        User user = new User("default", "firstName", "lastName", "name@ebi.ac.uk");
        user.getPreferences().add(new Preference("key", "value"));
        user.getRoles().add(new Role("CURATOR"));
        this.synchronizer.synchronize(user, false);

        Assert.assertNull(user.getAc());
        Assert.assertEquals("default", user.getLogin());
        Assert.assertEquals("firstName", user.getFirstName());
        Assert.assertEquals("lastName", user.getLastName());
        Assert.assertEquals("name@ebi.ac.uk", user.getEmail());
        Assert.assertEquals(1, user.getRoles().size());
        Role role = user.getRoles().iterator().next();
        Assert.assertNotNull(role.getAc());
        Assert.assertEquals(1, user.getPreferences().size());
        Preference pref = user.getPreferences().iterator().next();
        Assert.assertNull(pref.getAc());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_persist() throws PersisterException, FinderException, SynchronizerException {

        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new UserSynchronizer(this.context);

        User user = new User("default", "firstName", "lastName", "name@ebi.ac.uk");
        user.getPreferences().add(new Preference("key", "value"));
        user.getRoles().add(new Role("CURATOR"));
        this.synchronizer.synchronize(user, true);

        Assert.assertNotNull(user.getAc());
        Assert.assertEquals("default", user.getLogin());
        Assert.assertEquals("firstName", user.getFirstName());
        Assert.assertEquals("lastName", user.getLastName());
        Assert.assertEquals("name@ebi.ac.uk", user.getEmail());
        Assert.assertEquals(1, user.getRoles().size());
        Role role = user.getRoles().iterator().next();
        Assert.assertNotNull(role.getAc());
        Assert.assertEquals(1, user.getPreferences().size());
        Preference pref = user.getPreferences().iterator().next();
        Assert.assertNotNull(pref.getAc());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_merge() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new UserSynchronizer(this.context);

        User user = new User("default", "firstName", "lastName", "name@ebi.ac.uk");
        user.getPreferences().add(new Preference("key", "value"));
        user.getRoles().add(new Role("CURATOR"));
        this.synchronizer.persist(user);
        this.entityManager.flush();
        this.entityManager.detach(user);
        this.synchronizer.clearCache();

        Assert.assertNotNull(user.getAc());
        String ac = user.getAc();
        Assert.assertEquals(1, user.getRoles().size());
        Role role = user.getRoles().iterator().next();
        Assert.assertNotNull(role.getAc());
        Assert.assertEquals(1, user.getPreferences().size());
        Preference pref = user.getPreferences().iterator().next();
        Assert.assertNotNull(pref.getAc());
        user.setLastName("lastName2");
        user.getRoles().add(new Role("REVIEWER"));
        user.getPreferences().add(new Preference("key2", "value2"));

        User newUser = this.synchronizer.synchronize(user, true);
        Assert.assertEquals(ac, newUser.getAc());
        Assert.assertEquals(2, newUser.getRoles().size());
        Iterator<Role> roleIterator = newUser.getRoles().iterator();
        Role role2 = roleIterator.next();
        Assert.assertNotNull(role2.getAc());
        Assert.assertNotNull(roleIterator.next().getAc());
        Assert.assertEquals(2, newUser.getPreferences().size());
        Iterator<Preference> preferenceIterator = newUser.getPreferences().iterator();
        Preference pref2 = preferenceIterator.next();
        Assert.assertNotNull(pref2.getAc());
        Assert.assertNotNull(preferenceIterator.next().getAc());
        Assert.assertEquals("lastName2", newUser.getLastName());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_merge2() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new UserSynchronizer(this.context);

        User user = new User("default", "firstName", "lastName", "name@ebi.ac.uk");
        user.getPreferences().add(new Preference("key", "value"));
        user.getRoles().add(new Role("CURATOR"));
        this.synchronizer.persist(user);
        this.entityManager.flush();
        this.entityManager.detach(user);
        this.synchronizer.clearCache();

        Assert.assertNotNull(user.getAc());
        String ac = user.getAc();

        User us = this.entityManager.find(User.class, ac);
        this.entityManager.detach(us);
        us.setLastName("lastName2");
        us.getRoles().add(new Role("REVIEWER"));
        us.getPreferences().add(new Preference("key2", "value2"));

        User newUser = this.synchronizer.synchronize(us, true);
        Assert.assertEquals(ac, newUser.getAc());
        Assert.assertEquals(2, newUser.getRoles().size());
        Iterator<Role> roleIterator = newUser.getRoles().iterator();
        Role role2 = roleIterator.next();
        Assert.assertNotNull(role2.getAc());
        Assert.assertNotNull(roleIterator.next().getAc());
        Assert.assertEquals(2, newUser.getPreferences().size());
        Iterator<Preference> preferenceIterator = newUser.getPreferences().iterator();
        Preference pref2 = preferenceIterator.next();
        Assert.assertNotNull(pref2.getAc());
        Assert.assertNotNull(preferenceIterator.next().getAc());
        Assert.assertEquals("lastName2", newUser.getLastName());
    }
}
