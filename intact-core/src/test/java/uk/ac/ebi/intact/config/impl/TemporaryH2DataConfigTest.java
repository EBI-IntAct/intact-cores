package uk.ac.ebi.intact.config.impl;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.impl.StandaloneSession;
import uk.ac.ebi.intact.core.util.SchemaUtils;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class TemporaryH2DataConfigTest {

    @Test
    public void configuration() throws Exception {
        IntactSession session = new StandaloneSession();
        IntactContext.initContext(new TemporaryH2DataConfig(session), session);

        SchemaUtils.createSchema(false);
 
        Assert.assertTrue(IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getInstitutionDao().getAll().isEmpty());

        IntactContext.getCurrentInstance().close();
    }
}
