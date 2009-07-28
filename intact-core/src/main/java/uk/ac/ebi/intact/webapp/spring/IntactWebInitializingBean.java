package uk.ac.ebi.intact.webapp.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.impl.WebappSession;

import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContext;

/**
 * When using Spring, use this bean to initialize the IntactContext
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactWebInitializingBean implements InitializingBean, ServletContextAware {

    private Log log = LogFactory.getLog(IntactWebInitializingBean.class);

    private EntityManagerFactory entityManagerFactory;
    private ServletContext servletContext;

    public void afterPropertiesSet() throws Exception {
        IntactSession intactSession = new WebappSession( servletContext, null, null );

        log.info( "Starting Webapp IntAct Core..." );

        // start the intact application (e.g. load Institution, etc)
        IntactContext.initContext( entityManagerFactory, intactSession );

    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void setServletContext(ServletContext servletContext) {
       this.servletContext = servletContext;
    }
}
