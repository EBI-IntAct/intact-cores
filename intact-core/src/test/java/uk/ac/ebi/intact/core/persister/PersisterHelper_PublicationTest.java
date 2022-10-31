package uk.ac.ebi.intact.core.persister;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.IntactBasicTestCase;
import uk.ac.ebi.intact.model.*;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PersisterHelper_PublicationTest extends IntactBasicTestCase {

    private int startCvCount;

    @Before
    public void before() {
        startCvCount = getDaoFactory().getCvObjectDao().countAll();
    }


    @Test
    public void persist_publication() throws Exception {
        Publication publication = getMockBuilder().createPublicationRandom();
        getCorePersister().saveOrUpdate( publication );

        Assert.assertEquals(1, getDaoFactory().getPublicationDao().countAll());
        Assert.assertEquals(startCvCount, getDaoFactory().getCvObjectDao().countAll());
        Assert.assertEquals(1, getDaoFactory().getLifecycleEventDao().countAll());
    }


}