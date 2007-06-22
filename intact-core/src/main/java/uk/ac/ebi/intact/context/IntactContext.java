package uk.ac.ebi.intact.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.config.impl.StandardCoreDataConfig;
import uk.ac.ebi.intact.config.impl.TemporaryH2DataConfig;
import uk.ac.ebi.intact.context.impl.StandaloneSession;
import uk.ac.ebi.intact.model.Institution;

import java.io.Serializable;

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

            log.warn( "Current instance of IntactContext is null. Initializing with StandaloneSession," +
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
        initContext( null, null );
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
        StandardCoreDataConfig stdDataConfig = new StandardCoreDataConfig(session);

        if (stdDataConfig.isConfigurable()) {
            return stdDataConfig;
        }

        return new TemporaryH2DataConfig(session);
    }
}
