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
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

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
public class PreferenceSynchronizerTest {

    @PersistenceContext(unitName = "intact-core")
    private EntityManager entityManager;
    @PersistenceUnit(unitName = "intact-core", name = "intactEntityManagerFactory")
    private EntityManagerFactory intactEntityManagerFactory;

    private PreferenceSynchronizer synchronizer;
    private SynchronizerContext context;

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_all() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new PreferenceSynchronizer(this.context);

        Preference preference = new Preference("key", "value");
        this.synchronizer.persist(preference);

        Assert.assertNotNull(preference.getAc());
        Assert.assertEquals("key", preference.getKey());
        Assert.assertEquals("value", preference.getValue());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_deleted() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new PreferenceSynchronizer(this.context);

        Preference preference = new Preference("key", "value");
        this.synchronizer.persist(preference);

        Assert.assertNotNull(preference.getAc());

        this.synchronizer.delete(preference);
        Assert.assertNull(entityManager.find(Preference.class, preference.getAc()));
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_find() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new PreferenceSynchronizer(this.context);

        Preference preference = new Preference("key", "value");
        this.synchronizer.persist(preference);

        Assert.assertNotNull(preference.getAc());

        Assert.assertNull(this.synchronizer.find(preference));
        Assert.assertNull(this.synchronizer.find(new Preference("key", "value")));
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_properties() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new PreferenceSynchronizer(this.context);

        Preference preference = new Preference("key", "value");
        this.synchronizer.synchronizeProperties(preference);

        Assert.assertNull(preference.getAc());
        Assert.assertEquals("key", preference.getKey());
        Assert.assertEquals("value", preference.getValue());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_not_persist() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new PreferenceSynchronizer(this.context);

        Preference preference = new Preference("key", "value");
        this.synchronizer.synchronize(preference, false);

        Assert.assertNull(preference.getAc());
        Assert.assertEquals("key", preference.getKey());
        Assert.assertEquals("value", preference.getValue());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_persist() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new PreferenceSynchronizer(this.context);

        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new PreferenceSynchronizer(this.context);

        Preference preference = new Preference("key", "value");
        this.synchronizer.synchronize(preference, true);

        Assert.assertNotNull(preference.getAc());
        Assert.assertEquals("key", preference.getKey());
        Assert.assertEquals("value", preference.getValue());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_merge() throws PersisterException, FinderException, SynchronizerException {

        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new PreferenceSynchronizer(this.context);

        Preference preference = new Preference("key", "value");
        this.synchronizer.synchronize(preference, true);

        Assert.assertNotNull(preference.getAc());
        String ac = preference.getAc();
        Assert.assertEquals("key", preference.getKey());
        Assert.assertEquals("value", preference.getValue());

        this.entityManager.flush();
        this.entityManager.detach(preference);

        preference.setValue("value2");

        Preference newPref = this.synchronizer.synchronize(preference, true);

        Assert.assertEquals(ac, newPref.getAc());
        Assert.assertEquals("key", newPref.getKey());
        Assert.assertEquals("value2", newPref.getValue());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_merge2() throws PersisterException, FinderException, SynchronizerException {

        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new PreferenceSynchronizer(this.context);

        Preference preference = new Preference("key", "value");
        this.synchronizer.synchronize(preference, true);

        Assert.assertNotNull(preference.getAc());
        String ac = preference.getAc();
        Assert.assertEquals("key", preference.getKey());
        Assert.assertEquals("value", preference.getValue());

        this.entityManager.flush();
        this.entityManager.detach(preference);

        Preference pref = this.entityManager.find(Preference.class, ac);
        pref.setValue("value2");
        this.entityManager.detach(pref);

        Preference newPref = this.synchronizer.synchronize(pref, true);

        Assert.assertEquals(ac, newPref.getAc());
        Assert.assertEquals("key", newPref.getKey());
        Assert.assertEquals("value2", newPref.getValue());
    }
}
