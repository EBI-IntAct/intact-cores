package uk.ac.ebi.intact.core.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Controller;
import uk.ac.ebi.intact.core.IntactException;
import uk.ac.ebi.intact.core.config.ConfigurationException;
import uk.ac.ebi.intact.core.config.IntactConfiguration;
import uk.ac.ebi.intact.core.context.impl.StandaloneSession;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.model.Institution;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@code IntactContext} class is the central point of access to the IntAct Core API.  *
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Controller
public class IntactContext implements DisposableBean, Serializable {

    private static final Log log = LogFactory.getLog( IntactContext.class );

    private static IntactContext instance;

    /**
     * Contains scoped data and variables
     */
    private transient IntactSession session;

    @Autowired
    private DataContext dataContext;

    @Autowired
    private DaoFactory daoFactory;

    @Autowired
    private PersisterHelper persisterHelper;

    @Autowired
    private IntactConfiguration config;

    @Autowired
    private UserContext userContext;

    @Autowired
    private ApplicationContext springContext;

    public IntactContext() {
                                                                                          
    }

    @PostConstruct
    public void init() {

        //configurator.initIntact( new StandaloneSession() );
        instance = this;
    }

    /**
     * Gets the current (ThreadLocal) instance of {@code IntactContext}. If no such instance exist,
     * IntAct Core will be automatically initialized using JPA configurations in the classpath, configured
     * DataConfigs and, if these are not found, using a temporary database.
     * @return the IntactContext instance
     */
    public static IntactContext getCurrentInstance() {
        if ( !currentInstanceExists() ) {

            log.warn( "Current instance of IntactContext is null. Initializing a context in memory." );

            initStandaloneContextInMemory();
        }

        return instance;
    }

    /**
     * Checks if an instance already exists.
     * @return True if an instance of IntactContext exist.
     */
    public static boolean currentInstanceExists() {
        return instance != null;
    }

    /**
     * Initializes a standalone {@code IntactContext} (not to use in web applications, where the initialization
     * might be controlled by other means). It will try to find a working configuration or start a temporary database otherwise.
     */
    public static void initStandaloneContext() {
        initContext( (String)null, new StandaloneSession() );
    }

    /**
     * Initializes a standalone {@code IntactContext} using the Hibernate configuration file provided
     * (not to use in web applications, where the initialization might be controlled by other means).
     * It will try to find a working configuration or start a temporary database otherwise.
     * @param hibernateFile The hibernate configuration file
     */
    @Deprecated
    public static void initStandaloneContext(File hibernateFile) {
        throw new UnsupportedOperationException();
    }

    /**
     * Initializes a standalone {@code IntactContext} using a memory database.
     */
    public static void initStandaloneContextInMemory() {
        initStandaloneContextInMemory((ApplicationContext)null);
    }

    /**
     * Initializes a standalone {@code IntactContext} using a memory database.
     */
    public static void initStandaloneContextInMemory(IntactConfiguration config) {
        ConfigurableApplicationContext parent = new GenericApplicationContext();
        parent.getBeanFactory().registerSingleton("intactConfig", config);
        parent.refresh();

        initContext(new String[] { "classpath*:/META-INF/standalone/jpa-standalone.spring.xml",
                                   "classpath*:/META-INF/standalone/intact-standalone.spring.xml"}, parent);
    }

    public static void initStandaloneContextInMemory(ApplicationContext parent) {
        initContext(new String[] { "classpath*:/META-INF/standalone/*-standalone.spring.xml" }, parent);
    }

    /**
     * Initializes a standalone {@code IntactContext} using a persistence unit name and an {@code IntactSession} instance.
     * @param persistenceUnitName The name of the persistence unit. This is used to create a {@code DataConfig} of type {@code JpaCoreDataConfig}
     * @param session The IntactSession object. By default, this will be an instance of {@code StandaloneSession} for
     * standalone applications or a {@code WebappSession} for web applications. This value cannot be null.
     */
    @Deprecated
    public static void initContext( String persistenceUnitName, IntactSession session ) {
        throw new UnsupportedOperationException();
    }

    /**
     * Initializes a standalone {@code IntactContext} using an {@code EntityManagerFactory} and an {@code IntactSession} instance.
     * @param emf An EntityManagerFactory configured to access the IntAct database. This is used to create a {@code DataConfig} of type {@code JpaEntityManagerFactoryDataConfig}
     * @param session The IntactSession object. By default, this will be an instance of {@code StandaloneSession} for
     * standalone applications or a {@code WebappSession} for web applications. This value cannot be null.
     */
    @Deprecated
    public static void initContext( EntityManagerFactory emf, IntactSession session ) {
        throw new UnsupportedOperationException();
    }

    /**
     * Initializes a standalone context.
     */
    public static void initContext( String[] configurationResourcePaths ) {
        initContext(configurationResourcePaths, null);
    }

    /**
     * Initializes a standalone context.
     */
    public static void initContext( String[] configurationResourcePaths, ApplicationContext parent ) {
        // check for overflow initialization
        for (int i=5; i< Thread.currentThread().getStackTrace().length; i++) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[i];

            if (stackTraceElement.getClassName().equals(IntactContext.class.getName())
                    && stackTraceElement.getMethodName().equals("initContext")) {
                throw new IntactInitializationError("Infinite recursive invocation to IntactContext.initContext(). This" +
                        " may be due to an illegal invocation of IntactContext.getCurrentInstance() during bean instantiation.");
            }
        }

        // the order of the resources matters when overriding beans, so we add the intact first,
        // so the user can override the default beans.
        List<String> resourcesList = new LinkedList<String>();
        resourcesList.add("classpath*:/META-INF/intact.spring.xml");
        resourcesList.addAll(Arrays.asList(configurationResourcePaths));

        configurationResourcePaths = resourcesList.toArray(new String[resourcesList.size()]);

        if ( log.isDebugEnabled() ) {
            log.debug( "Loading Spring XML config:" );
            for ( String configurationResourcePath : configurationResourcePaths ) {
                log.debug( " - " + configurationResourcePath );
            }
        }

        // init Spring
        ClassPathXmlApplicationContext springContext = new ClassPathXmlApplicationContext(configurationResourcePaths, parent);
        springContext.registerShutdownHook();

        instance = (IntactContext) springContext.getBean("intactContext");
    }


    /**
     * The {@UserContext contains user-specific information, such as the current user name}
     * @return The UserContext instance
     */
    public UserContext getUserContext() {
        if (userContext == null) {
            throw new ConfigurationException("No bean of type "+UserContext.class.getName()+" found. One is expected");
        }
        
        return userContext;
    }

    /**
     * Gets the institution from the RuntimeConfig object. In addition, tries to refresh
     * the instance from the database if it is detached.
     * @return
     * @throws IntactException
     */
    public Institution getInstitution() throws IntactException {
        Institution institution = config.getDefaultInstitution();

        if (institution.getAc() != null && getDataContext().getDaoFactory().getInstitutionDao().isTransient(institution)) {
            institution = getDataContext().getDaoFactory().getInstitutionDao().getByAc(institution.getAc());
        }

        return institution;
    }

    public IntactConfiguration getConfig() {
        return config;
    }


    public IntactSession getSession() {
        return session;
    }

    public DataContext getDataContext() {
        return dataContext;
    }

    public DaoFactory getDaoFactory() {
        return daoFactory;
    }

    /**
     * Closes this instance of {@code IntactContext} and finalizes the data access, by closing the EntityManagerFactories
     * for all the registered DataConfigs. Other fields are set to null, as well as the current instance.     *
     */
//    public void close() {
//        getSpringContext().close();
//        session = null;
//        instance = null;
//    }

    /**
     * Closes the current IntactContext.
     */
//    public static void closeCurrentInstance() {
//        if (currentInstanceExists()) {
//            instance.close();
//        } else {
//            if (log.isDebugEnabled()) log.debug("No IntactContext found, so it didn't need to be closed");
//        }
//    }

    public PersisterHelper getPersisterHelper() {
        return persisterHelper;
    }

    public ConfigurableApplicationContext getSpringContext() {
        return (ConfigurableApplicationContext) springContext;
    }

    public void destroy() throws Exception {
        if (log.isDebugEnabled()) log.debug( "Releasing LogFactory" );
        LogFactory.release( Thread.currentThread().getContextClassLoader() );

        if (log.isInfoEnabled()) log.debug( "Destroying IntacContext" );
        instance = null;
    }
}
