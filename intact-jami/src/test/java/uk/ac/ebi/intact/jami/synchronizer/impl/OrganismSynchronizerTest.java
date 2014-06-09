package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Organism;
import psidev.psi.mi.jami.model.impl.DefaultCvTerm;
import uk.ac.ebi.intact.jami.IntactTestUtils;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactOrganism;
import uk.ac.ebi.intact.jami.model.extension.OrganismAlias;
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
        Assert.assertNull(entityManager.find(IntactOrganism.class, ((OrganismAlias)objectToTest.getAliases().iterator().next()).getAc()));
        Assert.assertNotNull(entityManager.find(IntactCvTerm.class, ((IntactCvTerm)objectToTest.getCellType()).getAc()));
        Assert.assertNotNull(entityManager.find(IntactCvTerm.class, ((IntactCvTerm)objectToTest.getTissue()).getAc()));
    }

    @Override
    protected Organism createDefaultJamiObject() {
        return IntactTestUtils.createOrganism();
    }

    @Override
    protected void testUpdatedPropertiesAfterMerge(IntactOrganism objectToTest, IntactOrganism persistedObject) {
        Assert.assertEquals(objectToTest.getAc(), persistedObject.getAc());
        Assert.assertEquals("new name", persistedObject.getCommonName());
        Assert.assertEquals("Homo Sapiens", persistedObject.getScientificName());
        Assert.assertEquals(9606, persistedObject.getTaxId());
        Assert.assertEquals(1, persistedObject.getAliases().size());
        Assert.assertNotNull(((OrganismAlias) persistedObject.getAliases().iterator().next()).getAc());
        Assert.assertNotNull(persistedObject.getCellType());
        Assert.assertNotNull(((IntactCvTerm) persistedObject.getCellType()).getAc());
        Assert.assertEquals("new cell type", persistedObject.getCellType().getShortName());
        Assert.assertNotNull(persistedObject.getTissue());
        Assert.assertNotNull(((IntactCvTerm) persistedObject.getTissue()).getAc());
        Assert.assertEquals("test tissue", persistedObject.getTissue().getShortName());
        Assert.assertNull(persistedObject.getCompartment());
    }

    @Override
    protected void updatePropertieDetachedInstance(IntactOrganism objectToTest) {
        objectToTest.setCommonName("new name");
        objectToTest.setCellType(new DefaultCvTerm("new cell type"));
    }

    @Override
    protected IntactOrganism findObject(IntactOrganism objectToTest) {
        return entityManager.find(IntactOrganism.class, objectToTest.getAc());
    }

    @Override
    protected void initSynchronizer() {
        this.synchronizer = new OrganismSynchronizer(this.context);
    }

    @Override
    protected void testPersistedProperties(IntactOrganism persistedObject) {
        Assert.assertNotNull(persistedObject.getAc());
        Assert.assertEquals("human", persistedObject.getCommonName());
        Assert.assertEquals("Homo Sapiens", persistedObject.getScientificName());
        Assert.assertEquals(9606, persistedObject.getTaxId());
        Assert.assertEquals(1, persistedObject.getAliases().size());
        Assert.assertNotNull(((OrganismAlias) persistedObject.getAliases().iterator().next()).getAc());
        Assert.assertNotNull(persistedObject.getCellType());
        Assert.assertNotNull(((IntactCvTerm) persistedObject.getCellType()).getAc());
        Assert.assertEquals("293t", persistedObject.getCellType().getShortName());
        Assert.assertNotNull(persistedObject.getTissue());
        Assert.assertNotNull(((IntactCvTerm) persistedObject.getTissue()).getAc());
        Assert.assertEquals("test tissue", persistedObject.getTissue().getShortName());
        Assert.assertNull(persistedObject.getCompartment());
    }

    @Override
    protected void testNonPersistedProperties(IntactOrganism persistedObject) {
        Assert.assertNull(persistedObject.getAc());
        Assert.assertEquals("human", persistedObject.getCommonName());
        Assert.assertEquals("Homo Sapiens", persistedObject.getScientificName());
        Assert.assertEquals(9606, persistedObject.getTaxId());
        Assert.assertEquals(1, persistedObject.getAliases().size());
        Assert.assertNull(((OrganismAlias) persistedObject.getAliases().iterator().next()).getAc());
        Assert.assertNotNull(persistedObject.getCellType());
        Assert.assertNotNull(((IntactCvTerm) persistedObject.getCellType()).getAc());
        Assert.assertEquals("293t", persistedObject.getCellType().getShortName());
        Assert.assertNotNull(persistedObject.getTissue());
        Assert.assertNotNull(((IntactCvTerm)persistedObject.getTissue()).getAc());
        Assert.assertEquals("test tissue", persistedObject.getTissue().getShortName());
        Assert.assertNull(persistedObject.getCompartment());
    }

    @Override
    protected IntactOrganism createDefaultObject() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return IntactTestUtils.createIntactOrganismWithCellAndTissue();
    }
}
