package uk.ac.ebi.intact.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.config.impl.*;
import uk.ac.ebi.intact.context.impl.StandaloneSession;
import uk.ac.ebi.intact.model.Institution;

import javax.persistence.EntityManagerFactory;
import java.io.Closeable;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Constructor;

/**
 * The {@code IntactContext} class is the central point of access to the IntAct Core API. It allows
 * to access the {@code DataContext} instance, to be able to use the database access objects and other data utilities.
 * It also contains the {@code RuntimeConfig} application instance, with the configuration for the platform.
 * <p>{@code IntactContext} follows a ThreadLocal pattern - one instance per thread - and the instance is accessed
 * using the {@code IntactContext.getCurrentInstance()} method. When this method is invoked for the first time,
 * the IntactContext is initialized automatically and the {@code IntactConfigurator} is invoked. IntAct Core
 * will start using accessible JPA configurations, or using a temporary H2 database instance if no JPA configurations
 * are explicitly used.</p>
 * <p>It is possible to start it manually. By invoking {@code IntactContext.initStandaloneInstance()} before any other
 * invokation to {@code IntactContext} it is possible to start IntAct Core using user defined {@cope DataContext}s or
 * directly by passing a hibernate configuration file.</p>
 *
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactContext implements Serializable, Closeable {

    private static final Log log = LogFactory.getLog( IntactContext.class );

    /**
     * Contains scoped data and variables
     */
    private transient IntactSession session;

    /**
     * Data access
     */
    private transient DataContext dataContext;


    protected IntactContext( DataContext dataContext, IntactSession session ) {
        //this.userContext = userContext;
        this.session = session;
        this.dataContext = dataContext;
    }

    /**
     * Gets the current (ThreadLocal) instance of {@code IntactContext}. If no such instance exist,
     * IntAct Core will be automatically initialized using JPA configurations in the classpath, configured
     * DataConfigs and, if these are not found, using a temporary database.
     * @return the IntactContext instance
     */
    public static synchronized IntactContext getCurrentInstance() {
        if ( !currentInstanceExists() ) {
            // stack trace element to know from where this method was called
            StackTraceElement ste = Thread.currentThread().getStackTrace()[3];

            log.debug( "Current instance of IntactContext is null. Initializing with StandaloneSession," +
                      "because probably this application is not a web application.\nCalled at:\n\t" +
                      ste.toString() );

            initStandaloneContext();
        }

        return currentInstance.get();
    }

    /**
     * Checks if an instance already exists.
     * @return True if an instance of IntactContext exist.
     */
    public static boolean currentInstanceExists() {
        return currentInstance.get() != null;
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
    public static void initStandaloneContext(File hibernateFile) {
        IntactSession session = new StandaloneSession();
        DataConfig dataConfig = new CustomCoreDataConfig("customDataConfig", hibernateFile, session);
        initContext( dataConfig, session );
    }

    /**
     * Initializes a standalone {@code IntactContext} using a temporary database. This is probably only useful
     * for testing.
     */
    public static void initStandaloneContextInMemory() {
        IntactSession session = new StandaloneSession();
        DataConfig dataConfig = new InMemoryDataConfig(session);
        initContext( dataConfig, session );
    }

    /**
     * Initializes a standalone {@code IntactContext} using a persistence unit name and an {@code IntactSession} instance.
     * @param persistenceUnitName The name of the persistence unit. This is used to create a {@code DataConfig} of type {@code JpaCoreDataConfig}
     * @param session The IntactSession object. By default, this will be an instance of {@code StandaloneSession} for
     * standalone applications or a {@code WebappSession} for web applications. This value cannot be null.
     */
    public static void initContext( String persistenceUnitName, IntactSession session ) {
        if (session == null) throw new NullPointerException("IntactSession cannot be null.");

        DataConfig dataConfig;

        if (persistenceUnitName != null) {
            dataConfig = new JpaCoreDataConfig(session, persistenceUnitName);
        } else {
            dataConfig = calculateDefaultDataConfig(session);
        }

        initContext(dataConfig, session);
    }

    /**
     * Initializes a standalone {@code IntactContext} using an {@code EntityManagerFactory} and an {@code IntactSession} instance.
     * @param emf An EntityManagerFactory configured to access the IntAct database. This is used to create a {@code DataConfig} of type {@code JpaEntityManagerFactoryDataConfig}
     * @param session The IntactSession object. By default, this will be an instance of {@code StandaloneSession} for
     * standalone applications or a {@code WebappSession} for web applications. This value cannot be null.
     */
    public static void initContext( EntityManagerFactory emf, IntactSession session ) {
        if (emf == null) {
            throw new IllegalArgumentException("Trying to initialize IntactContext with null EntityManagerFactory");
        }
        DataConfig dataConfig = new JpaEntityManagerFactoryDataConfig(session, emf);

        initContext(dataConfig, session);
    }

    /**
     * Initializes a standalone {@code IntactContext} using a {@code DataConfig} instance and an {@code IntactSession} instance.
     * @param defaultDataConfig This will be the main {@code DataConfig} used by IntAct.
     * @param session The IntactSession object. By default, this will be an instance of {@code StandaloneSession} for
     * standalone applications or a {@code WebappSession} for web applications. This value cannot be null.
     */
    public static void initContext( DataConfig defaultDataConfig, IntactSession session ) {
        if ( session == null ) {
            session = new StandaloneSession();
        }

        if ( defaultDataConfig == null ) {
            defaultDataConfig = calculateDefaultDataConfig( session );
        }

        if ( !defaultDataConfig.isInitialized() ) {
            defaultDataConfig.initialize();
        }

        RuntimeConfig.initRuntime( session, defaultDataConfig );
        IntactConfigurator.initIntact( session );
        IntactConfigurator.createIntactContext( session );
    }

    private static ThreadLocal<IntactContext> currentInstance = new ThreadLocal<IntactContext>() {
        protected IntactContext initialValue() {
            return null;
        }
    };

    /**
     * This allows to set the current instance. This method is used during initialization by the {@code IntactConfiguration}
     * and using it for other purposes is not advisable
     * @param context The instance to set
     */
    public static void setCurrentInstance( IntactContext context ) {
        currentInstance.set( context );
    }


    /**
     * The {@UserContext contains user-specific information, such as the current user name}
     * @return The UserContext instance
     */
    public UserContext getUserContext() {
        return UserContext.getCurrentInstance( session );
    }

    /**
     * @deprecated Use the CvObjectDaos for safe access to CVs
     */
    @Deprecated
    public CvContext getCvContext() {
        return CvContext.getCurrentInstance( session );
    }

    /**
     * Gets the institution from the RuntimeConfig object. In addition, tries to refresh
     * the instance from the database if it is detached.
     * @return
     * @throws IntactException
     */
    public Institution getInstitution() throws IntactException {
        Institution institution = getConfig().getInstitution();

        if (institution.getAc() != null && getDataContext().getDaoFactory().getInstitutionDao().isTransient(institution)) {
            institution = getDataContext().getDaoFactory().getInstitutionDao().getByAc(institution.getAc());
        }

        return institution;
    }

    /**
     * Access to the configuration of the platform.
     * @return The {@RuntimeConfig} instance containing the configuration
     */
    public RuntimeConfig getConfig() {
        return RuntimeConfig.getCurrentInstance( session );
    }


    public IntactSession getSession() {
        return session;
    }

    public DataContext getDataContext() {
        return dataContext;
    }

    /**
     * Closes this instance of {@code IntactContext} and finalizes the data access, by closing the EntityManagerFactories
     * for all the registered DataConfigs. Other fields are set to null, as well as the current instance.     *
     */
    public void close() {
        for (DataConfig dataConfig : getConfig().getDataConfigs()) {
            dataConfig.getEntityManagerFactory().close();
        }
        session = null;
        dataContext = null;
        currentInstance.set(null);
    }

    /**
     * Closes the current IntactContext.
     */
    public static void closeCurrentInstance() {
        if (currentInstanceExists()) {
            currentInstance.get().close();
        } else {
            if (log.isDebugEnabled()) log.debug("No IntactContext found, so it didn't need to be closed");
        }
    }

    /**
     * Calculate the default {@code DataConfig} to be used according to the configuration and environment. First, a generic
     * {@code StandardCoreDataConfig} is created. This DataConfig checks if there is a hibernate.cfg.xml configuration available
     * (to be available, the hibernate.cfg.xml should be found in the classpath), and this is done by invoking the DataConfig.isConfigurable()
     * method. If this does not exist, the
     * configuration is check to see if another DataConfig should be used. If the property "{@code uk.ac.ebi.intact.DATA_CONFIG}" is found,
     * an instance of DataConfig is created with the value of this property. Finally, if no such configuration variable exists,
     * a {@code TemporaryH2DataConfig} is created, which will allow access to a temporary instance of the H2 database by default.
     * @param session The session to use
     * @return The default {@code DataConfig} resulting from the above algorithm.
     * @throws IntactInitializationError if the property {@literal uk.ac.ebi.intact.DATA_CONFIG} is found in the configuration,
     * but the class refered by it cannot be instantiated.
     *
     */
    public static DataConfig calculateDefaultDataConfig(IntactSession session) {
        if (log.isDebugEnabled()) log.debug("Calculating default DataConfig");

        DataConfig dataConfig = new StandardCoreDataConfig( session );

        if (!dataConfig.isConfigurable()) {
            if (log.isDebugEnabled()) log.debug("\tDataConfig not configurable (hibernate.cfg.xml not found)");

            if ( session.containsInitParam( IntactEnvironment.DATA_CONFIG_PARAM_NAME.getFqn() ) ) {
                String dataConfigClass = session.getInitParam( IntactEnvironment.DATA_CONFIG_PARAM_NAME.getFqn() );
                 try {
                        Constructor constructor = Class.forName( dataConfigClass ).getConstructor(IntactSession.class);
                        dataConfig = ( DataConfig ) constructor.newInstance(session);
                    }
                    catch ( Exception e ) {
                        throw new IntactInitializationError( "Error initializing data configs. A data config must have a constructor" +
                                "that accepts an IntactSession object", e );
                    }
                if (log.isDebugEnabled()) log.debug("\tInitialized from session init parameter");
            } else {
               dataConfig = new TemporaryH2DataConfig(session); 
            }
        }

        if (log.isDebugEnabled()) log.debug("\tUsing DataConfig: "+dataConfig.getName());

        return dataConfig;
    }


}
