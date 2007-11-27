package uk.ac.ebi.intact.core.persister;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.standard.*;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactUnit;
import uk.ac.ebi.intact.model.*;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PersisterHelperTest extends IntactBasicTestCase {

    @Before
    public void clearDb() throws Exception {
        new IntactUnit().createSchema();
    }

    @Test
    public void saveOrUpdate_default() throws Exception {
        Experiment experiment = getMockBuilder().createExperimentRandom(1);
        PersisterHelper.saveOrUpdate(experiment);

        beginTransaction();
        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getProteinDao().countAll());
        commitTransaction();
    }

    @Test
    public void saveOrUpdate_transactionAlreadyOpened() throws Exception {
        Experiment experiment = getMockBuilder().createExperimentRandom(1);
        beginTransaction();
        PersisterHelper.saveOrUpdate(experiment);

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getProteinDao().countAll());
        commitTransaction();
    }
}