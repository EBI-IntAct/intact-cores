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
import uk.ac.ebi.intact.jami.model.user.Role;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

/**
 * Unit test for RoleSynchronizerTemplate
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
public class RoleSynchronizerTest {

    @PersistenceContext(unitName = "intact-core")
    private EntityManager entityManager;
    @PersistenceUnit(unitName = "intact-core", name = "intactEntityManagerFactory")
    private EntityManagerFactory intactEntityManagerFactory;

    private RoleSynchronizer synchronizer;
    private SynchronizerContext context;

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_all() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new RoleSynchronizer(this.context);

        Role role = new Role("CURATOR");
        this.synchronizer.persist(role);

        Assert.assertNotNull(role.getAc());
        Assert.assertEquals("CURATOR", role.getName());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_deleted() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new RoleSynchronizer(this.context);

        Role role = new Role("CURATOR");
        this.synchronizer.persist(role);

        Assert.assertNotNull(role.getAc());

        this.synchronizer.delete(role);
        Assert.assertNull(entityManager.find(Role.class, role.getAc()));
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_find() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new RoleSynchronizer(this.context);

        Role role = new Role("CURATOR");
        this.synchronizer.persist(role);

        Assert.assertNotNull(role.getAc());

        Assert.assertEquals(this.synchronizer.find(role), this.synchronizer.find(new Role("CURATOR")));
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_properties() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new RoleSynchronizer(this.context);

        Role role = new Role("CURATOR");
        this.synchronizer.synchronizeProperties(role);

        Assert.assertNull(role.getAc());
        Assert.assertEquals("CURATOR", role.getName());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_not_persist() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new RoleSynchronizer(this.context);

        Role role = new Role("CURATOR");
        this.synchronizer.synchronize(role, false);

        Assert.assertNull(role.getAc());
        Assert.assertEquals("CURATOR", role.getName());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_persist() throws PersisterException, FinderException, SynchronizerException {

        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new RoleSynchronizer(this.context);

        Role role = new Role("CURATOR");
        this.synchronizer.synchronize(role, true);

        Assert.assertNotNull(role.getAc());
        Assert.assertEquals("CURATOR", role.getName());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_merge() throws PersisterException, FinderException, SynchronizerException {

        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new RoleSynchronizer(this.context);

        Role role = new Role("CURATOR");
        this.synchronizer.synchronize(role, true);
        this.synchronizer.clearCache();

        Assert.assertNotNull(role.getAc());
        String ac = role.getAc();
        Assert.assertEquals("CURATOR", role.getName());

        this.entityManager.flush();
        this.entityManager.detach(role);

        role.setName("value2");

        Role newRole = this.synchronizer.synchronize(role, true);

        Assert.assertEquals(ac, newRole.getAc());
        Assert.assertEquals("VALUE2", newRole.getName());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_merge2() throws PersisterException, FinderException, SynchronizerException {

        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new RoleSynchronizer(this.context);

        Role role = new Role("CURATOR");
        this.synchronizer.synchronize(role, true);
        this.synchronizer.clearCache();

        Assert.assertNotNull(role.getAc());
        String ac = role.getAc();
        Assert.assertEquals("CURATOR", role.getName());

        this.entityManager.flush();
        this.entityManager.detach(role);

        Role ro = this.entityManager.find(Role.class, ac);
        ro.setName("VALUE2");
        this.entityManager.detach(ro);

        Role newRole = this.synchronizer.synchronize(ro, true);

        Assert.assertEquals(ac, newRole.getAc());
        Assert.assertEquals("VALUE2", newRole.getName());
    }
}
