package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.model.audit.Auditable;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import javax.persistence.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Abstract class for testing db synchronizers
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>02/04/14</pre>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/intact-jami-test.spring.xml"})
@Transactional(value = "jamiTransactionManager")
@TransactionConfiguration
@DirtiesContext
public abstract class AbstractDbSynchronizerTest<I, T extends Auditable> {
    @PersistenceContext(unitName = "intact-jami")
    protected EntityManager entityManager;
    @PersistenceUnit(unitName = "intact-jami", name = "intactEntityManagerFactory")
    protected EntityManagerFactory intactEntityManagerFactory;

    protected IntactDbSynchronizer synchronizer;
    protected SynchronizerContext context;

    @Before
    public void init(){
        this.context = new DefaultSynchronizerContext(this.entityManager);
        initSynchronizer();
    }

    public void persist() throws PersisterException, FinderException, SynchronizerException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        T objectToTest = createDefaultObject();

        persist(objectToTest);
    }

    public void persist_jami() throws PersisterException, FinderException, SynchronizerException {
        I objectToTest = createDefaultJamiObject();

        synchronizeJami(objectToTest);
    }

    public void delete() throws PersisterException, FinderException, SynchronizerException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        T objectToTest = createDefaultObject();
        this.synchronizer.persist(objectToTest);
        this.synchronizer.clearCache();
        this.entityManager.flush();

        this.synchronizer.delete(objectToTest);
        Assert.assertNull(findObject(objectToTest));
        testDeleteOtherProperties(objectToTest);
    }

    public void find_no_cache() throws PersisterException, FinderException, SynchronizerException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        T objectToTest = createDefaultObject();
        this.synchronizer.persist(objectToTest);
        // test cache if any
        Assert.assertNull(this.synchronizer.find(objectToTest));
        Assert.assertNull(this.synchronizer.find(createDefaultObject()));
        this.synchronizer.clearCache();
        this.entityManager.flush();

        // test without cache
        Assert.assertNull(this.synchronizer.find(objectToTest));
        Assert.assertNull(this.synchronizer.find(createDefaultObject()));
    }

    public void find_local_cache(boolean identityComparison) throws PersisterException, FinderException, SynchronizerException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        T objectToTest = createDefaultObject();
        this.synchronizer.persist(objectToTest);
        // test cache if any
        Assert.assertNotNull(this.synchronizer.find(objectToTest));
        T newObject = createDefaultObject();
        if (!identityComparison){
            Assert.assertNotNull(this.synchronizer.find(newObject));
        }
        else{
            Assert.assertNull(this.synchronizer.find(newObject));
        }
        this.synchronizer.clearCache();
        this.entityManager.flush();

        // test without cache
        if (!identityComparison){
            Assert.assertNotNull(this.synchronizer.find(objectToTest));
            Assert.assertNotNull(this.synchronizer.find(newObject));
        }
        else{
            Assert.assertNull(this.synchronizer.find(objectToTest));
            Assert.assertNull(this.synchronizer.find(newObject));
        }
    }

    public void synchronizeProperties() throws PersisterException, FinderException, SynchronizerException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        T objectToTest = createDefaultObject();
        this.synchronizer.synchronizeProperties(objectToTest);

        testNonPersistedProperties(objectToTest);
    }

    public void synchronize_not_persist() throws PersisterException, FinderException, SynchronizerException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        T objectToTest = createDefaultObject();
        objectToTest = (T)this.synchronizer.synchronize(objectToTest, false);

        testNonPersistedProperties(objectToTest);
    }

    public void synchronize_persist() throws PersisterException, FinderException, SynchronizerException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        T objectToTest = createDefaultObject();
        objectToTest = (T)this.synchronizer.synchronize(objectToTest, true);

        testPersistedProperties(objectToTest);
    }

    public void merge_test1() throws PersisterException, FinderException, SynchronizerException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        T objectToTest = createDefaultObject();
        objectToTest = (T)this.synchronizer.synchronize(objectToTest, true);
        entityManager.flush();
        entityManager.detach(objectToTest);
        this.synchronizer.clearCache();

        updatePropertieDetachedInstance(objectToTest);

        T newObjToTest = (T)this.synchronizer.synchronize(objectToTest, true);

        testUpdatedPropertiesAfterMerge(objectToTest, newObjToTest);
    }

    public void merge_test2() throws PersisterException, FinderException, SynchronizerException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        T objectToTest = createDefaultObject();
        objectToTest = (T) this.synchronizer.synchronize(objectToTest, true);
        entityManager.flush();
        entityManager.detach(objectToTest);
        this.synchronizer.clearCache();

        T reloadedObject = findObject(objectToTest);
        initPropertiesBeforeDetaching(reloadedObject);
        entityManager.detach(reloadedObject);
        updatePropertieDetachedInstance(reloadedObject);

        T newObjToTest = (T)this.synchronizer.synchronize(reloadedObject, true);

        testUpdatedPropertiesAfterMerge(objectToTest, newObjToTest);
    }

    protected abstract void testDeleteOtherProperties(T objectToTest);

    protected void initPropertiesBeforeDetaching(T reloadedObject){
       // nothing to do here
    }

    protected void persist(T objectToTest) throws FinderException, PersisterException, SynchronizerException {
        objectToTest = (T) this.synchronizer.persist(objectToTest);

        testPersistedProperties(objectToTest);
    }

    protected void synchronizeJami(I objectToTest) throws FinderException, PersisterException, SynchronizerException {
        T newObject = (T)this.synchronizer.synchronize(objectToTest, true);

        testPersistedProperties(newObject);
    }

    protected abstract I createDefaultJamiObject();

    protected abstract void testUpdatedPropertiesAfterMerge(T objectToTest, T newObjToTest);

    protected abstract void updatePropertieDetachedInstance(T objectToTest);

    protected abstract T findObject(T objectToTest);

    protected abstract void initSynchronizer();

    protected abstract void testPersistedProperties(T persistedObject);

    protected abstract void testNonPersistedProperties(T objectToTest);

    protected abstract T createDefaultObject() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;
}
