package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.IntactTestUtils;
import uk.ac.ebi.intact.jami.model.extension.*;
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
public class AliasSynchronizerTemplateTest extends AbstractDbSynchronizerTest<Alias, AbstractIntactAlias>{

    private int testNumber=1;

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_all() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        this.synchronizer.setIntactClass(CvTermAlias.class);
        this.testNumber = 1;
        persist();

        this.synchronizer.setIntactClass(OrganismAlias.class);
        this.testNumber = 2;
        persist();

        this.synchronizer.setIntactClass(InteractorAlias.class);
        this.testNumber = 3;
        persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_with_existing_type() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        IntactCvTerm aliasSynonym = createExistingType();

        CvTermAlias cvAliasWithType = IntactTestUtils.createAliasSynonym(CvTermAlias.class);

        OrganismAlias organismAlias = IntactTestUtils.createAliasNoType(OrganismAlias.class);

        InteractorAlias interactorAlias = IntactTestUtils.createAliasSynonym(InteractorAlias.class, "test synonym 3");

        this.synchronizer.setIntactClass(CvTermAlias.class);
        this.synchronizer.persist(cvAliasWithType);

        Assert.assertNotNull(cvAliasWithType.getType());
        IntactCvTerm aliasType = (IntactCvTerm)cvAliasWithType.getType();
        Assert.assertEquals(aliasType.getAc(), aliasSynonym.getAc());
        Assert.assertEquals("test synonym", cvAliasWithType.getName());

        this.synchronizer.setIntactClass(OrganismAlias.class);
        this.synchronizer.persist(organismAlias);

        Assert.assertNotNull(organismAlias.getAc());
        Assert.assertNull(organismAlias.getType());
        Assert.assertEquals("test synonym 2", organismAlias.getName());

        this.synchronizer.setIntactClass(InteractorAlias.class);
        this.synchronizer.persist(interactorAlias);

        Assert.assertNotNull(interactorAlias.getAc());
        Assert.assertNotNull(interactorAlias.getType());
        IntactCvTerm aliasType2 = (IntactCvTerm)interactorAlias.getType();
        Assert.assertEquals(aliasType2.getAc(), aliasSynonym.getAc());
        Assert.assertEquals("test synonym 3", interactorAlias.getName());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_with_detached_type() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        // pre persist alias synonym
        IntactCvTerm aliasSynonym = createExistingType();

        entityManager.detach(aliasSynonym);

        CvTermAlias cvAliasWithType = IntactTestUtils.createAliasSynonym(CvTermAlias.class);
        cvAliasWithType.setType(aliasSynonym);

        OrganismAlias organismAlias = IntactTestUtils.createAliasNoType(OrganismAlias.class);

        InteractorAlias interactorAlias = IntactTestUtils.createAliasSynonym(InteractorAlias.class, "test synonym 3");
        interactorAlias.setType(aliasSynonym);

        this.synchronizer.setIntactClass(CvTermAlias.class);
        this.synchronizer.persist(cvAliasWithType);

        Assert.assertNotNull(cvAliasWithType.getType());
        IntactCvTerm aliasType = (IntactCvTerm)cvAliasWithType.getType();
        Assert.assertEquals(aliasType.getAc(), aliasSynonym.getAc());
        Assert.assertEquals("test synonym", cvAliasWithType.getName());

        this.synchronizer.setIntactClass(OrganismAlias.class);
        this.synchronizer.persist(organismAlias);

        Assert.assertNotNull(organismAlias.getAc());
        Assert.assertNull(organismAlias.getType());
        Assert.assertEquals("test synonym 2", organismAlias.getName());

        this.synchronizer.setIntactClass(InteractorAlias.class);
        this.synchronizer.persist(interactorAlias);

        Assert.assertNotNull(interactorAlias.getAc());
        Assert.assertNotNull(interactorAlias.getType());
        IntactCvTerm aliasType2 = (IntactCvTerm)interactorAlias.getType();
        Assert.assertEquals(aliasType2.getAc(), aliasSynonym.getAc());
        Assert.assertEquals("test synonym 3", interactorAlias.getName());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_alias_deleted() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(CvTermAlias.class);
        this.testNumber = 1;
        delete();

        this.synchronizer.setIntactClass(OrganismAlias.class);
        this.testNumber = 2;
        delete();

        this.synchronizer.setIntactClass(InteractorAlias.class);
        this.testNumber = 3;
        delete();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_find() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(CvTermAlias.class);
        this.testNumber = 1;
        find_no_cache();

        this.synchronizer.setIntactClass(OrganismAlias.class);
        this.testNumber = 2;
        find_no_cache();

        this.synchronizer.setIntactClass(InteractorAlias.class);
        this.testNumber = 3;
        find_no_cache();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_properties() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(CvTermAlias.class);
        this.testNumber = 1;
        synchronizeProperties();

        this.synchronizer.setIntactClass(OrganismAlias.class);
        this.testNumber = 2;
        synchronizeProperties();

        this.synchronizer.setIntactClass(InteractorAlias.class);
        this.testNumber = 3;
        synchronizeProperties();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_not_persist() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(CvTermAlias.class);
        this.testNumber = 1;
        synchronize_not_persist();

        this.synchronizer.setIntactClass(OrganismAlias.class);
        this.testNumber = 2;
        synchronize_not_persist();

        this.synchronizer.setIntactClass(InteractorAlias.class);
        this.testNumber = 3;
        synchronize_not_persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_persist() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.synchronizer.setIntactClass(CvTermAlias.class);
        this.testNumber = 1;
        synchronize_persist();

        this.synchronizer.setIntactClass(OrganismAlias.class);
        this.testNumber = 2;
        synchronize_persist();

        this.synchronizer.setIntactClass(InteractorAlias.class);
        this.testNumber = 3;
        synchronize_persist();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_jami() throws PersisterException, FinderException, SynchronizerException {
        this.synchronizer.setIntactClass(CvTermAlias.class);
        this.testNumber = 1;
        persist_jami();

        this.synchronizer.setIntactClass(OrganismAlias.class);
        this.testNumber = 2;
        persist_jami();

        this.synchronizer.setIntactClass(InteractorAlias.class);
        this.testNumber = 3;
        persist_jami();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_merge() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        this.synchronizer.setIntactClass(CvTermAlias.class);
        this.testNumber = 1;
        merge_test1();

        this.synchronizer.setIntactClass(OrganismAlias.class);
        this.testNumber = 2;
        merge_test1();

        this.synchronizer.setIntactClass(InteractorAlias.class);
        this.testNumber = 3;
        merge_test1();
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_merge2() throws PersisterException, FinderException, SynchronizerException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        this.synchronizer.setIntactClass(CvTermAlias.class);
        this.testNumber = 1;
        merge_test2();

        this.synchronizer.setIntactClass(OrganismAlias.class);
        this.testNumber = 2;
        merge_test2();

        this.synchronizer.setIntactClass(InteractorAlias.class);
        this.testNumber = 3;
        merge_test2();
    }

    private IntactCvTerm createExistingType() {
        // pre persist alias synonym
        IntactCvTerm aliasSynonym = new IntactCvTerm(Alias.SYNONYM);
        aliasSynonym.setObjClass(IntactUtils.ALIAS_TYPE_OBJCLASS);
        entityManager.persist(aliasSynonym);
        IntactCvTerm psimi = new IntactCvTerm(CvTerm.PSI_MI);
        psimi.setObjClass(IntactUtils.DATABASE_OBJCLASS);
        entityManager.persist(psimi);
        IntactCvTerm identity = new IntactCvTerm(Xref.IDENTITY);
        identity.setObjClass(IntactUtils.QUALIFIER_OBJCLASS);
        entityManager.persist(identity);

        CvTermXref ref1 = new CvTermXref(psimi, Alias.SYNONYM_MI, identity);
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
    protected Alias createDefaultJamiObject() {
        if (testNumber == 1){
            return IntactTestUtils.createAliasSynonym();
        }
        else if (testNumber == 2){
            return IntactTestUtils.createAliasNoType();
        }
        else{
            return IntactTestUtils.createAliasSynonym( "test synonym 3");
        }
    }

    @Override
    protected void testUpdatedPropertiesAfterMerge(AbstractIntactAlias objectToTest, AbstractIntactAlias newObjToTest) {
        Assert.assertEquals(objectToTest.getAc(), newObjToTest.getAc());
        Assert.assertEquals("value2", newObjToTest.getName());
    }

    @Override
    protected void updatePropertieDetachedInstance(AbstractIntactAlias objectToTest) {
        objectToTest.setName("value2");
    }

    @Override
    protected AbstractIntactAlias findObject(AbstractIntactAlias objectToTest) {
        if (testNumber == 1){
            return entityManager.find(CvTermAlias.class, objectToTest.getAc());
        }
        else if (testNumber == 2){
            return entityManager.find(OrganismAlias.class, objectToTest.getAc());
        }
        else{
            return entityManager.find(InteractorAlias.class, objectToTest.getAc());
        }
    }

    @Override
    protected void initSynchronizer() {
        this.synchronizer = new AliasSynchronizerTemplate(this.context, AbstractIntactAlias.class);
    }

    @Override
    protected void testPersistedProperties(AbstractIntactAlias persistedObject) {
        if (testNumber == 1){
            Assert.assertNotNull(persistedObject.getAc());
            Assert.assertNotNull(persistedObject.getType());
            IntactCvTerm aliasType = (IntactCvTerm)persistedObject.getType();
            Assert.assertNotNull(aliasType.getAc());
            Assert.assertEquals(persistedObject.getType(), IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI));
            Assert.assertEquals("test synonym", persistedObject.getName());
        }
        else if (testNumber == 2){
            Assert.assertNotNull(persistedObject.getAc());
            Assert.assertNull(persistedObject.getType());
            Assert.assertEquals("test synonym 2", persistedObject.getName());
        }
        else{
            Assert.assertNotNull(persistedObject.getAc());
            Assert.assertNotNull(persistedObject.getType());
            IntactCvTerm aliasType2 = (IntactCvTerm)persistedObject.getType();
            Assert.assertNotNull(aliasType2.getAc());
            Assert.assertEquals(persistedObject.getType(), IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI));
            Assert.assertEquals("test synonym 3", persistedObject.getName());
        }
    }

    @Override
    protected void testNonPersistedProperties(AbstractIntactAlias objectToTest) {
        if (testNumber == 1){
            Assert.assertNull(objectToTest.getAc());
            Assert.assertNotNull(objectToTest.getType());
            IntactCvTerm aliasType = (IntactCvTerm)objectToTest.getType();
            Assert.assertNotNull(aliasType.getAc());
            Assert.assertEquals(objectToTest.getType(), IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI));
            Assert.assertEquals("test synonym", objectToTest.getName());
        }
        else if (testNumber == 2){
            Assert.assertNull(objectToTest.getAc());
            Assert.assertNull(objectToTest.getType());
            Assert.assertEquals("test synonym 2", objectToTest.getName());
        }
        else{
            Assert.assertNull(objectToTest.getAc());
            Assert.assertNotNull(objectToTest.getType());
            IntactCvTerm aliasType2 = (IntactCvTerm)objectToTest.getType();
            Assert.assertNotNull(aliasType2.getAc());
            Assert.assertEquals(objectToTest.getType(), IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI));
            Assert.assertEquals("test synonym 3", objectToTest.getName());
        }
    }

    @Override
    protected AbstractIntactAlias createDefaultObject() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (testNumber == 1){
            return IntactTestUtils.createAliasSynonym(CvTermAlias.class);
        }
        else if (testNumber == 2){
           return IntactTestUtils.createAliasNoType(OrganismAlias.class);
        }
        else{
            return IntactTestUtils.createAliasSynonym(InteractorAlias.class, "test synonym 3");
        }
    }
}
