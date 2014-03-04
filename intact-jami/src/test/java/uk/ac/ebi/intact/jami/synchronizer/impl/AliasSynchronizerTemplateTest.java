package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Alias;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.AliasSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

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
public class AliasSynchronizerTemplateTest {

    @Autowired
    private ApplicationContext applicationContext;
    @PersistenceContext(unitName = "intact-core")
    private EntityManager entityManager;
    @PersistenceUnit(unitName = "intact-core", name = "intactEntityManagerFactory")
    private EntityManagerFactory intactEntityManagerFactory;

    private AliasSynchronizer synchronizer;


    @Transactional
    @Test
    public void test_persist_all() throws PersisterException, FinderException, SynchronizerException {
        this.synchronizer = new AliasSynchronizerTemplate(new DefaultSynchronizerContext(this.entityManager), AbstractIntactAlias.class);

        CvTermAlias cvAliasWithType = new CvTermAlias(IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI), "test synonym");

        OrganismAlias organismAlias = new OrganismAlias("test synonym 2");

        InteractorAlias interactorAlias = new InteractorAlias(IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI), "test synonym 3");

        this.synchronizer.setIntactClass(CvTermAlias.class);
        this.synchronizer.persist(cvAliasWithType);

        Assert.assertNotNull(cvAliasWithType.getAc());
        Assert.assertNotNull(cvAliasWithType.getType());
        IntactCvTerm aliasType = (IntactCvTerm)cvAliasWithType.getType();
        Assert.assertNotNull(aliasType.getAc());
        Assert.assertEquals(cvAliasWithType.getType(), IntactUtils.createMIAliasType(Alias.SYNONYM, Alias.SYNONYM_MI));
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
        Assert.assertNotNull(aliasType2.getAc());
        Assert.assertTrue(cvAliasWithType.getType() == aliasType2);
        Assert.assertEquals("test synonym 3", interactorAlias.getName());

        entityManager.flush();

        System.out.println("flush");
    }
}
