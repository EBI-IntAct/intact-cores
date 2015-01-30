package uk.ac.ebi.intact.jami.synchronizer.impl;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Publication;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.model.extension.IntactExperiment;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

/**
 * Unit test for UserSynchronizerTemplate
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/02/14</pre>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/intact-jami-test.spring.xml"})
@Transactional(value = "jamiTransactionManager")
@TransactionConfiguration
@DirtiesContext
public class ExperimentSynchronizerTest {
    @PersistenceContext(unitName = "intact-jami")
    protected EntityManager entityManager;

    protected IntactDbSynchronizer synchronizer;
    protected SynchronizerContext context;

    @Before
    public void init(){
        this.context = new DefaultSynchronizerContext(this.entityManager);
        this.synchronizer = new ExperimentSynchronizer(context);
    }

    @Transactional
    @Test
    @DirtiesContext
    public void test_persist_two_experiments() throws PersisterException, FinderException, SynchronizerException {
        Publication intactPublication = new IntactPublication("12345");
        intactPublication.setPublicationDate(new Date());
        intactPublication.getAuthors().add("bla_bla");

        IntactExperiment exp = new IntactExperiment(intactPublication);
        IntactExperiment exp2 = new IntactExperiment(intactPublication);

        this.synchronizer.synchronize(exp, true);
        this.synchronizer.synchronize(exp2, true);

        Assert.assertEquals(exp.getShortLabel(), "bla_bla-2015-1");
        Assert.assertEquals(exp2.getShortLabel(), "bla_bla-2015-1");
        Assert.assertEquals(exp.getAc(), exp2.getAc());
    }
}
