package uk.ac.ebi.intact.core.persister.standard;

import org.junit.After;
import org.junit.Before;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.PersisterContext;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class AbstractPersisterTest extends IntactBasicTestCase {

    @Before
    public final void beforeTest() throws Exception {
        //new IntactUnit().createSchema();
        beginTransaction();
    }

    @After
    public final void afterTest() throws Exception {
        commitTransaction();
        PersisterContext.getInstance().clear();

        if (IntactContext.currentInstanceExists()) {
            IntactContext.getCurrentInstance().close();
        }
    }

}
