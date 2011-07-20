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
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.IntactException;
import uk.ac.ebi.intact.core.config.ConfigurationException;
import uk.ac.ebi.intact.core.config.ConfigurationHandler;
import uk.ac.ebi.intact.core.config.IntactConfiguration;
import uk.ac.ebi.intact.core.context.impl.StandaloneSession;
import uk.ac.ebi.intact.core.lifecycle.LifecycleManager;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.core.persister.CoreDeleter;
import uk.ac.ebi.intact.core.persister.CorePersister;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.meta.Application;
import uk.ac.ebi.intact.model.util.ApplicationFactoryBean;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@code IntactContext} class is the central point of access to the IntAct Core API.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Controller
public class IntactContext implements DisposableBean, Serializable {

    private static final Log log = LogFactory.getLog(IntactContext.class);

    private static IntactContext instance;

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
    private LifecycleManager lifecycleManager;

    @Resource(name = "defaultApp")
    private Application application;

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
     *
     * @return the IntactContext instance
     */
    public static IntactContext getCurrentInstance() {
        if (!currentInstanceExists()) {

            log.warn("Current instance of IntactContext is null. Initializing a context in memory.");

            initStandaloneContextInMemory();
        }

        return instance;
    }

    /**
     * Checks if an instance already exists.
     *
     * @return True if an instance of IntactContext exist.
     */
    public static boolean currentInstanceExists() {
        return instance != null;
    }

    /**
     * Initializes a standalone {@code IntactContext} using a memory database.
     */
    public static void initStandaloneContextInMemory() {
        initStandaloneContextInMemory((ApplicationContext) null);
    }

    /**
     * Initializes a standalone {@code IntactContext} using a memory database.
     */
    public static void initStandaloneContextInMemory(IntactConfiguration config) {
        ConfigurableApplicationContext parent = new GenericApplicationContext();
        parent.getBeanFactory().registerSingleton("intactConfig", config);
        parent.refresh();

        initContext(new String[]{"classpath*:/META-INF/standalone/jpa-standalone.spring.xml",
                "classpath*:/META-INF/standalone/intact-standalone.spring.xml"}, parent);
    }

    public static void initStandaloneContextInMemory(ApplicationContext parent) {
        initContext(new String[]{"classpath*:/META-INF/standalone/*-standalone.spring.xml"}, parent);
    }

    /**
     * Initializes a standalone context.
     */
    public static void initContext(String[] configurationResourcePaths) {
        initContext(configurationResourcePaths, null);
    }

    /**
     * Initializes a standalone context.
     */
    public static void initContext(String[] configurationResourcePaths, ApplicationContext parent) {
        // check for overflow initialization
        for (int i = 5; i < Thread.currentThread().getStackTrace().length; i++) {
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

        if (log.isDebugEnabled()) {
            log.debug("Loading Spring XML config:");
            for (String configurationResourcePath : configurationResourcePaths) {
                log.debug(" - " + configurationResourcePath);
            }
        }

        // init Spring
        ClassPathXmlApplicationContext springContext = new ClassPathXmlApplicationContext(configurationResourcePaths, parent);
        springContext.registerShutdownHook();

        instance = (IntactContext) springContext.getBean("intactContext");
    }

    /**
     * The {@UserContext contains user-specific information, such as the current user name}
     *
     * @return The UserContext instance
     */
    public UserContext getUserContext() {
        if (userContext == null) {
            throw new ConfigurationException("No bean of type " + UserContext.class.getName() + " found. One is expected");
        }

        return userContext;
    }

    /**
     * Gets the institution from the RuntimeConfig object. In addition, tries to refresh
     * the instance from the database if it is detached.
     *
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

    public DataContext getDataContext() {
        return dataContext;
    }

    public DaoFactory getDaoFactory() {
        return daoFactory;
    }

    /**
     * Gets the lifecycle manager for publications.
     *
     * @return the lifecycle manager.
     * @since 2.5.0
     */
    public LifecycleManager getLifecycleManager() {
        return lifecycleManager;
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

    /**
     * @return
     * @deprecated Use getCorePersister() instead.
     */
    @Deprecated
    public PersisterHelper getPersisterHelper() {
        return persisterHelper;
    }

    public CorePersister getCorePersister() {
        return (CorePersister) IntactContext.getCurrentInstance().getSpringContext().getBean("corePersister");
    }

    /**
     * @since 2.4.0
     */
    public CoreDeleter getCoreDeleter() {
        return (CoreDeleter) IntactContext.getCurrentInstance().getSpringContext().getBean("coreDeleter");
    }

    public void bindToApplication(Application application) {
        setApplication(application);
        getConfigurationHandler().loadConfiguration(application);
    }

    private ConfigurationHandler getConfigurationHandler() {
        return (ConfigurationHandler) getSpringContext().getBean("configurationHandler");
    }

    public Application getApplication() {
        return application;
    }

    private void setApplication(Application application) {
        this.application = application;
    }

    public ConfigurableApplicationContext getSpringContext() {
        return (ConfigurableApplicationContext) springContext;
    }

    public void destroy() throws Exception {
        if (log.isDebugEnabled()) log.debug("Persisting configuration");
        getConfigurationHandler().persistConfiguration();

        getSpringContext().close();

        if (log.isDebugEnabled()) log.debug("Releasing LogFactory");
        LogFactory.release(Thread.currentThread().getContextClassLoader());

        if (log.isInfoEnabled()) log.debug("Destroying IntactContext");
        instance = null;
    }
}
