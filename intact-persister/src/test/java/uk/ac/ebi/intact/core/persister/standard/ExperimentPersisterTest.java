package uk.ac.ebi.intact.core.persister.standard;

import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.PersisterUnexpectedException;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.ExperimentXref;
import uk.ac.ebi.intact.model.Publication;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentPersisterTest extends AbstractPersisterTest
{
    private ExperimentPersister persister;

    @Before
    public void before() {
        persister = ExperimentPersister.getInstance();
    }

    @After
    public void after() {
        persister = null;
    }

    @Test
    public void persistExperiment_publication() throws Exception {
        Experiment exp = getMockBuilder().createExperimentRandom(1);
        exp.setShortLabel("lala-2005");
        exp.setPublication(getMockBuilder().createPublication("1234567"));

        beginTransaction();

        persister.saveOrUpdate(exp);
        persister.commit();

        commitTransaction();

        beginTransaction();

        Experiment refreshedExperiment = getDaoFactory().getExperimentDao().getByShortLabel("lala-2005-1");
        assertNotNull(refreshedExperiment);

        assertNotNull(refreshedExperiment.getPublication());
        assertEquals("1234567", refreshedExperiment.getPublication().getShortLabel());
        assertEquals(1, refreshedExperiment.getInteractions().size());

        commitTransaction();
    }

    @Test
    public void persistExperiment_assignedLabel() throws Exception {
        Experiment exp = getMockBuilder().createExperimentRandom(1);
        exp.setShortLabel("lala-2005-2");

        beginTransaction();

        persister.saveOrUpdate(exp);
        persister.commit();

        commitTransaction();

        beginTransaction();

        Experiment refreshedExperiment = getDaoFactory().getExperimentDao().getByShortLabel("lala-2005-2");
        assertNotNull(refreshedExperiment);
        assertEquals(1, refreshedExperiment.getInteractions().size());

        commitTransaction();
    }

    @Test
    public void persistExperiment_similarLabels() throws Exception {

        Experiment exp = getMockBuilder().createExperimentRandom(1);
        exp.setShortLabel("tata-2005-2");

        Assert.assertEquals(1, exp.getXrefs().size());

        ExperimentXref xref = exp.getXrefs().iterator().next();

        beginTransaction();
        persister.saveOrUpdate(exp);
        persister.commit();
        commitTransaction();

        Experiment exp2 = getMockBuilder().createExperimentRandom(1);
        exp2.setShortLabel("tata-2005");
        xref.setAc(null);
        exp2.getXrefs().clear();
        exp2.addXref(xref);

        beginTransaction();
        persister.saveOrUpdate(exp2);
        persister.commit();
        commitTransaction();

        beginTransaction();

        Experiment reloadedExperiment = getDaoFactory().getExperimentDao().getByShortLabel("tata-2005-3");

        Assert.assertEquals(2, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(1, reloadedExperiment.getXrefs().size());
        Assert.assertEquals(1, reloadedExperiment.getInteractions().size());

        commitTransaction();
    }

    @Test
    public void updateExperiment_avoidDuplications() throws Exception {

        Experiment exp = getMockBuilder().createExperimentRandom(1);
        exp.setShortLabel("lala-2005-2");

        Assert.assertEquals(1, exp.getXrefs().size());
        Assert.assertNotNull(exp.getPublication());

        ExperimentXref xref = exp.getXrefs().iterator().next();
        Publication pub = exp.getPublication();

        beginTransaction();
        persister.saveOrUpdate(exp);
        persister.commit();
        commitTransaction();

        Experiment exp2 = getMockBuilder().createExperimentRandom(1);
        exp2.setShortLabel("lala-2005-2");
        xref.setAc(null);
        exp2.getXrefs().clear();
        exp2.addXref(xref);
        pub.setAc(null);
        exp2.setPublication(pub);

        beginTransaction();
        persister.saveOrUpdate(exp2);
        persister.commit();
        commitTransaction();

        beginTransaction();

        Experiment reloadedExperiment = getDaoFactory().getExperimentDao().getByShortLabel("lala-2005-2");

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(1, reloadedExperiment.getXrefs().size());
        Assert.assertEquals(2, reloadedExperiment.getInteractions().size());

        commitTransaction();
    }

    @Test (expected = PersisterUnexpectedException.class)
    public void updateExperiment_sameLabelDifferentPubmeds() throws Exception {

        Experiment exp = getMockBuilder().createExperimentRandom(1);
        exp.setShortLabel("lala-2005-4");

        beginTransaction();
        persister.saveOrUpdate(exp);
        persister.commit();
        commitTransaction();

        Experiment exp2 = getMockBuilder().createExperimentRandom(1);
        exp2.setShortLabel("lala-2005-4");

        beginTransaction();
        persister.saveOrUpdate(exp2);
        persister.commit();
        commitTransaction();
    }
}

