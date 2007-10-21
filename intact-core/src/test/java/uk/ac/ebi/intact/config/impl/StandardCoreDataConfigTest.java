package uk.ac.ebi.intact.config.impl;

import static junit.framework.Assert.assertEquals;
import org.junit.Test;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.impl.StandaloneSession;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class StandardCoreDataConfigTest {

    @Test
    public void configuration() throws Exception {

        ClassLoaderWithHibernate classLoader = new ClassLoaderWithHibernate(Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(classLoader);

        IntactSession session = new StandaloneSession();

        DataConfig dataConfig = new StandardCoreDataConfig(session);
        IntactContext.initContext(dataConfig, session);

        assertEquals(StandardCoreDataConfig.NAME, IntactContext.getCurrentInstance().getConfig().getDefaultDataConfig().getName());
        assertEquals(1, IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getInstitutionDao().countAll());
        
        IntactContext.getCurrentInstance().close();
    }

    private class ClassLoaderWithHibernate extends URLClassLoader {

        public ClassLoaderWithHibernate(ClassLoader classLoader) {
            super(new URL[0], classLoader);
        }

        public URL getResource(String s) {
            if (s.endsWith("hibernate.cfg.xml")) {
                return StandardCoreDataConfigTest.class.getResource("/META-INF/mem-hibernate.cfg.xml");
            }
            return super.getResource(s);
        }

        public InputStream getResourceAsStream(String s) {
            if (s.endsWith("hibernate.cfg.xml")) {
                return StandardCoreDataConfigTest.class.getResourceAsStream("/META-INF/mem-hibernate.cfg.xml");
            }
            return super.getResourceAsStream(s);
        }
    }
}