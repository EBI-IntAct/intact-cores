package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Experiment;

/**
 * ExperimentDaoImpl Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public class ExperimentDaoImplTest extends IntactBasicTestCase {
        
    @Test
    public void countInteractionsForExperimentWithAc() {
        Experiment exp = getMockBuilder().createExperimentRandom(5);
        getCorePersister().saveOrUpdate(exp);

        final int count = getDaoFactory().getExperimentDao().countInteractionsForExperimentWithAc(exp.getAc());
        Assert.assertEquals(5, count);
    }
}
