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
import psidev.psi.mi.jami.model.impl.DefaultResultingSequence;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

/**
 * Unit test for ModelledRangeSynchronizer
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
public class ModelledRangeSynchronizerTest {

    @PersistenceContext(unitName = "intact-core")
    private EntityManager entityManager;
    @PersistenceUnit(unitName = "intact-core", name = "intactEntityManagerFactory")
    private EntityManagerFactory intactEntityManagerFactory;

    private ModelledRangeSynchronizer synchronizer;
    private SynchronizerContext context;

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_all() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ModelledRangeSynchronizer(this.context);

        ModelledRange range = new ModelledRange(new IntactPosition(1), new IntactPosition(2));
        range.setResultingSequence(new ModelledResultingSequence("AAGA","ACGA"));
        this.synchronizer.persist(range);

        Assert.assertNotNull(range.getAc());
        Assert.assertEquals(new DefaultPosition(1), range.getStart());
        Assert.assertEquals(new DefaultPosition(2), range.getEnd());
        Assert.assertFalse(range.isLink());
        Assert.assertNotNull(range.getResultingSequence());
        Assert.assertEquals("AAGA", range.getResultingSequence().getOriginalSequence());
        Assert.assertEquals("ACGA", range.getResultingSequence().getNewSequence());
        Assert.assertNotNull(((IntactCvTerm) range.getStart().getStatus()).getAc());
        Assert.assertNotNull(((IntactCvTerm) range.getEnd().getStatus()).getAc());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_deleted() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ModelledRangeSynchronizer(this.context);

        ModelledRange range = new ModelledRange(new IntactPosition(1), new IntactPosition(2));
        this.synchronizer.persist(range);
        Assert.assertNotNull(range.getAc());

        this.synchronizer.delete(range);
        Assert.assertNull(entityManager.find(ModelledRange.class, range.getAc()));
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_find() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ModelledRangeSynchronizer(this.context);

        ModelledRange range = new ModelledRange(new IntactPosition(1), new IntactPosition(2));
        this.synchronizer.persist(range);
        Assert.assertNotNull(range.getAc());

        Assert.assertNull(this.synchronizer.find(range));
        Assert.assertNull(this.synchronizer.find(new ModelledRange(new IntactPosition(1), new IntactPosition(2))));
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_properties() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ModelledRangeSynchronizer(this.context);

        ModelledRange range = new ModelledRange(new IntactPosition(1), new IntactPosition(2));
        range.setResultingSequence(new ModelledResultingSequence("AAGA","ACGA"));
        this.synchronizer.synchronizeProperties(range);

        Assert.assertNull(range.getAc());
        Assert.assertEquals(new DefaultPosition(1), range.getStart());
        Assert.assertEquals(new DefaultPosition(2), range.getEnd());
        Assert.assertFalse(range.isLink());
        Assert.assertNotNull(range.getResultingSequence());
        Assert.assertEquals("AAGA", range.getResultingSequence().getOriginalSequence());
        Assert.assertEquals("ACGA", range.getResultingSequence().getNewSequence());
        Assert.assertNotNull(((IntactCvTerm) range.getStart().getStatus()).getAc());
        Assert.assertNotNull(((IntactCvTerm) range.getEnd().getStatus()).getAc());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_not_persist() throws PersisterException, FinderException, SynchronizerException {
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ModelledRangeSynchronizer(this.context);

        ModelledRange range = new ModelledRange(new IntactPosition(1), new IntactPosition(2));
        range.setResultingSequence(new ModelledResultingSequence("AAGA","ACGA"));
        this.synchronizer.synchronize(range, false);

        Assert.assertNull(range.getAc());
        Assert.assertEquals(new DefaultPosition(1), range.getStart());
        Assert.assertEquals(new DefaultPosition(2), range.getEnd());
        Assert.assertFalse(range.isLink());
        Assert.assertNotNull(range.getResultingSequence());
        Assert.assertEquals("AAGA", range.getResultingSequence().getOriginalSequence());
        Assert.assertEquals("ACGA", range.getResultingSequence().getNewSequence());
        Assert.assertNotNull(((IntactCvTerm) range.getStart().getStatus()).getAc());
        Assert.assertNotNull(((IntactCvTerm) range.getEnd().getStatus()).getAc());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_persist() throws PersisterException, FinderException, SynchronizerException {

        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ModelledRangeSynchronizer(this.context);

        ModelledRange range = new ModelledRange(new IntactPosition(1), new IntactPosition(2));
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
        this.synchronizer = new ModelledRangeSynchronizer(this.context);

        ModelledRange range = new ModelledRange(new IntactPosition(1), new IntactPosition(2));
        this.synchronizer.synchronize(range, true);
        entityManager.flush();
        entityManager.detach(range);
        this.synchronizer.clearCache();

        Assert.assertNotNull(range.getAc());
        ModelledRange newRange = this.synchronizer.synchronize(range, true);
        Assert.assertNotNull(newRange.getAc());
        Assert.assertEquals(range.getAc(), newRange.getAc());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_merge2() throws PersisterException, FinderException, SynchronizerException {

        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ModelledRangeSynchronizer(this.context);

        ModelledRange range = new ModelledRange(new IntactPosition(1), new IntactPosition(2));
        this.synchronizer.synchronize(range, true);
        entityManager.flush();
        entityManager.detach(range);
        this.synchronizer.clearCache();

        ModelledRange ra = entityManager.find(ModelledRange.class, range.getAc());
        ra.setPositions(new IntactPosition(2), new IntactPosition(3));
        ra.setResultingSequence(new ModelledResultingSequence("AAGA","ACGA"));
        entityManager.detach(ra);

        Assert.assertNotNull(range.getAc());
        ModelledRange newRange = this.synchronizer.synchronize(ra, true);
        Assert.assertNotNull(newRange.getAc());
        Assert.assertEquals(range.getAc(), newRange.getAc());
        Assert.assertEquals(new DefaultPosition(2), newRange.getStart());
        Assert.assertEquals(new DefaultPosition(3), newRange.getEnd());
        Assert.assertNotNull(ra.getResultingSequence());
        Assert.assertEquals("AAGA", ra.getResultingSequence().getOriginalSequence());
        Assert.assertEquals("ACGA", ra.getResultingSequence().getNewSequence());
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_synchronize_jami() throws PersisterException, FinderException, SynchronizerException {

        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ModelledRangeSynchronizer(this.context);

        Range range = new DefaultRange(new DefaultPosition(1), new DefaultPosition(2));
        range.setResultingSequence(new DefaultResultingSequence("AAGA","ACGA"));
        ModelledRange newRange = this.synchronizer.synchronize(range, true);

        Assert.assertNotNull(newRange.getAc());
        Assert.assertEquals(new DefaultPosition(1), newRange.getStart());
        Assert.assertEquals(new DefaultPosition(2), newRange.getEnd());
        Assert.assertFalse(newRange.isLink());
        Assert.assertNotNull(range.getResultingSequence());
        Assert.assertEquals("AAGA", range.getResultingSequence().getOriginalSequence());
        Assert.assertEquals("ACGA", range.getResultingSequence().getNewSequence());
        Assert.assertNotNull(((IntactCvTerm) newRange.getStart().getStatus()).getAc());
        Assert.assertNotNull(((IntactCvTerm) newRange.getEnd().getStatus()).getAc());
    }
}
