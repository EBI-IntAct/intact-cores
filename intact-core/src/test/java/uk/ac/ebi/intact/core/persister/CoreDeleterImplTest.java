package uk.ac.ebi.intact.core.persister;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class CoreDeleterImplTest extends IntactBasicTestCase {

    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void delete_interaction() throws Exception {
        TransactionStatus transactionStatus = getDataContext().beginTransaction();

        Experiment exp = getMockBuilder().createExperimentRandom(2);
        getCorePersister().saveOrUpdate(exp);

        Interaction inter = exp.getInteractions().iterator().next();

        getDataContext().commitTransaction(transactionStatus);

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(4, getDaoFactory().getComponentDao().countAll());

        TransactionStatus transactionStatus2 = getDataContext().beginTransaction();
        getCoreDeleter().delete(inter);

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getComponentDao().countAll());

        Experiment refreshedExperiment = getDaoFactory().getExperimentDao().getByAc(exp.getAc());
        Assert.assertEquals(1, refreshedExperiment.getInteractions().size());

        getDataContext().commitTransaction(transactionStatus2);
    }
}
