package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Range;
import psidev.psi.mi.jami.model.impl.DefaultPosition;
import psidev.psi.mi.jami.model.impl.DefaultRange;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactRange;
import uk.ac.ebi.intact.jami.model.extension.ExperimentalRange;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactPosition;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

/**
 * Unit test for RangeSynchronizerTemplate
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
public class RangeSynchronizerTemplateTest {

    @PersistenceContext(unitName = "intact-core")
    private EntityManager entityManager;
    @PersistenceUnit(unitName = "intact-core", name = "intactEntityManagerFactory")
    private EntityManagerFactory intactEntityManagerFactory;

    private RangeSynchronizerTemplate synchronizer;
    private SynchronizerContext context;

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_all() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new RangeSynchronizerTemplate(this.context, AbstractIntactRange.class);

        ExperimentalRange range = new ExperimentalRange(new IntactPosition(1), new IntactPosition(2));
        this.synchronizer.persist(range);

        Assert.assertNotNull(range.getAc());
        Assert.assertEquals(new DefaultPosition(1), range.getStart());
        Assert.assertEquals(new DefaultPosition(2), range.getEnd());
        Assert.assertFalse(range.isLink());
        Assert.assertNull(range.getResultingSequence());
        Assert.assertNotNull(((IntactCvTerm) range.getStart().getStatus()).getAc());
        Assert.assertNotNull(((IntactCvTerm) range.getEnd().getStatus()).getAc());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_deleted() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new RangeSynchronizerTemplate(this.context, ExperimentalRange.class);

        ExperimentalRange range = new ExperimentalRange(new IntactPosition(1), new IntactPosition(2));
        this.synchronizer.persist(range);
        Assert.assertNotNull(range.getAc());

        this.synchronizer.delete(range);
        Assert.assertNull(entityManager.find(ExperimentalRange.class, range.getAc()));
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_find() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new RangeSynchronizerTemplate(this.context, AbstractIntactRange.class);

        ExperimentalRange range = new ExperimentalRange(new IntactPosition(1), new IntactPosition(2));
        this.synchronizer.persist(range);
        Assert.assertNotNull(range.getAc());

        Assert.assertNull(this.synchronizer.find(range));
        Assert.assertNull(this.synchronizer.find(new ExperimentalRange(new IntactPosition(1), new IntactPosition(2))));
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_properties() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new RangeSynchronizerTemplate(this.context, AbstractIntactRange.class);

        ExperimentalRange range = new ExperimentalRange(new IntactPosition(1), new IntactPosition(2));
        this.synchronizer.synchronizeProperties(range);

        Assert.assertNull(range.getAc());
        Assert.assertEquals(new DefaultPosition(1), range.getStart());
        Assert.assertEquals(new DefaultPosition(2), range.getEnd());
        Assert.assertFalse(range.isLink());
        Assert.assertNull(range.getResultingSequence());
        Assert.assertNotNull(((IntactCvTerm) range.getStart().getStatus()).getAc());
        Assert.assertNotNull(((IntactCvTerm) range.getEnd().getStatus()).getAc());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_not_persist() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new RangeSynchronizerTemplate(this.context, AbstractIntactRange.class);

        ExperimentalRange range = new ExperimentalRange(new IntactPosition(1), new IntactPosition(2));
        this.synchronizer.synchronize(range, false);

        Assert.assertNull(range.getAc());
        Assert.assertEquals(new DefaultPosition(1), range.getStart());
        Assert.assertEquals(new DefaultPosition(2), range.getEnd());
        Assert.assertFalse(range.isLink());
        Assert.assertNull(range.getResultingSequence());
        Assert.assertNotNull(((IntactCvTerm) range.getStart().getStatus()).getAc());
        Assert.assertNotNull(((IntactCvTerm) range.getEnd().getStatus()).getAc());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_persist() throws PersisterException, FinderException, SynchronizerException {

        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new RangeSynchronizerTemplate(this.context, AbstractIntactRange.class);

        ExperimentalRange range = new ExperimentalRange(new IntactPosition(1), new IntactPosition(2));
        this.synchronizer.synchronize(range, true);

        Assert.assertNotNull(range.getAc());
        Assert.assertEquals(new DefaultPosition(1), range.getStart());
        Assert.assertEquals(new DefaultPosition(2), range.getEnd());
        Assert.assertFalse(range.isLink());
        Assert.assertNull(range.getResultingSequence());
        Assert.assertNotNull(((IntactCvTerm) range.getStart().getStatus()).getAc());
        Assert.assertNotNull(((IntactCvTerm) range.getEnd().getStatus()).getAc());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_merge() throws PersisterException, FinderException, SynchronizerException {

        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new RangeSynchronizerTemplate(this.context, ExperimentalRange.class);

        ExperimentalRange range = new ExperimentalRange(new IntactPosition(1), new IntactPosition(2));
        this.synchronizer.synchronize(range, true);
        entityManager.flush();
        entityManager.detach(range);
        this.synchronizer.clearCache();

        Assert.assertNotNull(range.getAc());
        ExperimentalRange newRange = (ExperimentalRange)this.synchronizer.synchronize(range, true);
        Assert.assertNotNull(newRange.getAc());
        Assert.assertEquals(range.getAc(), newRange.getAc());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_merge2() throws PersisterException, FinderException, SynchronizerException {

        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new RangeSynchronizerTemplate(this.context, AbstractIntactRange.class);

        ExperimentalRange range = new ExperimentalRange(new IntactPosition(1), new IntactPosition(2));
        this.synchronizer.synchronize(range, true);
        entityManager.flush();
        entityManager.detach(range);
        this.synchronizer.clearCache();

        ExperimentalRange ra = entityManager.find(ExperimentalRange.class, range.getAc());
        ra.setPositions(new IntactPosition(2), new IntactPosition(3));
        entityManager.detach(ra);

        Assert.assertNotNull(range.getAc());
        ExperimentalRange newRange = (ExperimentalRange)this.synchronizer.synchronize(ra, true);
        Assert.assertNotNull(newRange.getAc());
        Assert.assertEquals(range.getAc(), newRange.getAc());
        Assert.assertEquals(new DefaultPosition(2), newRange.getStart());
        Assert.assertEquals(new DefaultPosition(3), newRange.getEnd());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_jami() throws PersisterException, FinderException, SynchronizerException {

        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new RangeSynchronizerTemplate(this.context, ExperimentalRange.class);

        Range range = new DefaultRange(new DefaultPosition(1), new DefaultPosition(2));
        ExperimentalRange newRange = (ExperimentalRange)this.synchronizer.synchronize(range, true);

        Assert.assertNotNull(newRange.getAc());
        Assert.assertEquals(new DefaultPosition(1), newRange.getStart());
        Assert.assertEquals(new DefaultPosition(2), newRange.getEnd());
        Assert.assertFalse(newRange.isLink());
        Assert.assertNull(newRange.getResultingSequence());
        Assert.assertNotNull(((IntactCvTerm) newRange.getStart().getStatus()).getAc());
        Assert.assertNotNull(((IntactCvTerm) newRange.getEnd().getStatus()).getAc());
    }
}
