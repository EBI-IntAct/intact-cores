package uk.ac.ebi.intact.core.persister;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;

import javax.persistence.Query;

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
        getCorePersister().saveOrUpdate(experiment);

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getProteinDao().countAll());
    }

    @Test
    public void saveOrUpdate_transactionAlreadyOpened() throws Exception {
        Experiment experiment = getMockBuilder().createExperimentRandom(1);
        getCorePersister().saveOrUpdate(experiment);

        Assert.assertEquals(1, getDaoFactory().getExperimentDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getInteractionDao().countAll());
        Assert.assertEquals(2, getDaoFactory().getProteinDao().countAll());
    }

    @Test
    public void paginatedFetch() throws Exception {

        for (int i=0; i<10; i++) {
            Interaction interaction = getMockBuilder().createInteraction("brca2", "lala"+i);
            getCorePersister().saveOrUpdate(interaction);
        }

        Query query = getEntityManager().createQuery("select distinct i " +
                                                                 "from InteractionImpl i left join i.xrefs as x " +
                                                                 "                       left join fetch i.experiments as e " +
                                                                 "where    i.ac = :ac " +
                                                                 "      or lower(i.shortLabel) like :query " +
                                                                 "      or lower(x.primaryId) like :query " +
                                                                 "order by i.updated desc");
        query.setParameter("ac", "brca2");
        query.setParameter("query", "%brca2%");


        query.setFirstResult(0);
        query.setMaxResults(2);

        Assert.assertEquals(2, query.getResultList().size());
    }

}