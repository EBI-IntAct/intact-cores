package uk.ac.ebi.intact.core.persister;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Experiment;

/**
 * PersisterHelper Tester.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PersisterHelperTest extends IntactBasicTestCase {

    @Test
    public void saveOrUpdate_default() throws Exception {
        Experiment experiment = getMockBuilder().createExperimentRandom(1);
        getPersisterHelper().save(experiment);

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getProteinDao().countAll());
    }

    @Test
    public void saveOrUpdate_transactionAlreadyOpened() throws Exception {
        Experiment experiment = getMockBuilder().createExperimentRandom(1);
        getPersisterHelper().save(experiment);

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getProteinDao().countAll());
    }
}