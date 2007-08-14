package uk.ac.ebi.intact.core.persister.standard;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.PersisterContext;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Experiment;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentPersisterTest extends IntactBasicTestCase
{
    private ExperimentPersister persister;

    @Before
    public void beforeTest() {
        persister = ExperimentPersister.getInstance();
    }

    @After
    public void afterTest() {
        persister = null;
        PersisterContext.getInstance().clear();
    }

    @Test
    public void testPersistExperimentPublication() throws Exception {
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
}
