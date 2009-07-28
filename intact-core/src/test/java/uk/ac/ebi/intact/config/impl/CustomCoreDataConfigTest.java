package uk.ac.ebi.intact.config.impl;

import static junit.framework.Assert.assertEquals;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.impl.StandaloneSession;

import java.io.File;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class CustomCoreDataConfigTest {

    @Test
    public void configuration() throws Exception {
        IntactSession session = new StandaloneSession();
        File hibernateConfigFile = new File(CustomCoreDataConfigTest.class.getResource("/META-INF/mem-hibernate.cfg.xml").getFile());

        CustomCoreDataConfig coreDataConfig = new CustomCoreDataConfig("testDataConfig", hibernateConfigFile, session);
        IntactContext.initContext(coreDataConfig, session);

        assertEquals("testDataConfig", IntactContext.getCurrentInstance().getConfig().getDefaultDataConfig().getName());
        assertEquals(1, IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getInstitutionDao().countAll());

        IntactContext.getCurrentInstance().close();
    }
}