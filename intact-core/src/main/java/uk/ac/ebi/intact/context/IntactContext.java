package uk.ac.ebi.intact.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.config.impl.*;
import uk.ac.ebi.intact.context.impl.StandaloneSession;
import uk.ac.ebi.intact.model.Institution;

import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Constructor;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactContext implements Serializable {

    private static final Log log = LogFactory.getLog( IntactContext.class );

    private IntactSession session;

    //private UserContext userContext;
    private DataContext dataContext;


    protected IntactContext( DataContext dataContext, IntactSession session ) {
        //this.userContext = userContext;
        this.session = session;
        this.dataContext = dataContext;
    }

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

    public static boolean currentInstanceExists() {
        return currentInstance.get() != null;
    }

    public static void initStandaloneContext() {
        initContext( (String)null, new StandaloneSession() );
    }

    public static void initStandaloneContext(File hibernateFile) {
        IntactSession session = new StandaloneSession();
        DataConfig dataConfig = new CustomCoreDataConfig("customDataConfig", hibernateFile, session);
        initContext( dataConfig, session );
    }

    public static void initStandaloneContextInMemory() {
        IntactSession session = new StandaloneSession();
        DataConfig dataConfig = new InMemoryDataConfig(session);
        initContext( dataConfig, session );
    }

    public static void initContext( String persistenceUnitName, IntactSession session ) {
        DataConfig dataConfig;

        if (persistenceUnitName != null) {
            dataConfig = new JpaCoreDataConfig(session, persistenceUnitName);
        } else {
            dataConfig = calculateDefaultDataConfig(session);
        }

        initContext(dataConfig, session);
    }

    public static void initContext( EntityManagerFactory emf, IntactSession session ) {
        if (emf == null) {
            throw new IllegalArgumentException("Trying to initialize IntactContext with null EntityManagerFactory");
        }
        DataConfig dataConfig = new JpaEntityManagerFactoryDataConfig(session, emf);

        initContext(dataConfig, session);
    }

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

    public static void setCurrentInstance( IntactContext context ) {
        currentInstance.set( context );
    }


    public UserContext getUserContext() {
        return UserContext.getCurrentInstance( session );
    }

    public CvContext getCvContext() {
        return CvContext.getCurrentInstance( session );
    }

    public Institution getInstitution() throws IntactException {
        return getConfig().getInstitution();
    }

    public RuntimeConfig getConfig() {
        return RuntimeConfig.getCurrentInstance( session );
    }


    public IntactSession getSession() {
        return session;
    }

    public DataContext getDataContext() {
        return dataContext;
    }

    public void close() {
        for (DataConfig dataConfig : getConfig().getDataConfigs()) {
            dataConfig.getEntityManagerFactory().close();
        }
        session = null;
        dataContext = null;
        currentInstance.set(null);
    }

    /**
     * Calculate the default data config. If the standard data-config exists (there is a hibernate.cfg.xml file in the classpath)
     * use it, otherwise use the memory data-config;
     * @param session
     * @return
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
