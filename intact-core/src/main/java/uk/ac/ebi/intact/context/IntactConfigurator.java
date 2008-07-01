/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.config.CvPrimer;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.config.SchemaVersion;
import uk.ac.ebi.intact.config.impl.EssentialCvPrimer;
import uk.ac.ebi.intact.context.impl.IntactContextWrapper;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.InstitutionAlias;
import uk.ac.ebi.intact.model.InstitutionXref;
import uk.ac.ebi.intact.model.meta.DbInfo;
import uk.ac.ebi.intact.model.util.AliasUtils;
import uk.ac.ebi.intact.model.util.XrefUtils;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import javax.persistence.EntityTransaction;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Class to initialize IntAct, and to initialize IntactContexts
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/08/2006</pre>
 */
public class IntactConfigurator {

    private static final Log log = LogFactory.getLog( IntactConfigurator.class );

    private static final String DEFAULT_INSTITUTION_LABEL = "Unknown";

    private static final String EBI_INSTITUTION_LABEL = Institution.INTACT;
    private static final String EBI_INSTITUTION_ALIAS = "ebi";

    private static final String EBI_INSTITUTION_FULL_NAME = "European Bioinformatics Institute";

    private static final String EBI_INSTITUTION_POSTAL_ADDRESS = "European Bioinformatics Institute; " +
                                                                 "Wellcome Trust Genome Campus; " +
                                                                 "Hinxton, Cambridge; " +
                                                                 "CB10 1SD; " +
                                                                 "United Kingdom";

    private static final String EBI_INSTITUTION_URL = "http://www.ebi.ac.uk/";

    private static final String DEFAULT_AC_PREFIX = "UNK";

    private static final String DEFAULT_READ_ONLY_APP = Boolean.FALSE.toString();

    private static final String DEFAULT_SYNCHRONIZED_SEARCH_ITEMS = Boolean.FALSE.toString();

    private static final String DEFAULT_TRANSACTION_AUTO_BEGIN = Boolean.FALSE.toString();

    private static final String DEFAULT_DEBUG_MODE = Boolean.FALSE.toString();

    private static final String DEFAULT_LOCAL_CV_PREFIX = "IA";

    private static final String INSTITUTION_TO_BE_PERSISTED_FLAG = "uk.ac.ebi.intact.internal.INSTITUTION_TO_BE_PERSISTED";

    private static final String SCHEMA_VERSION_TO_BE_PERSISTED_FLAG = "uk.ac.ebi.intact.internal.SCHEMA_VERSION_TO_BE_PERSISTED";

    private static final String INITIALIZED_APP_ATT_NAME = IntactConfigurator.class + "_INITIALIZED";

    /**
     * Initializes the fundamental intact parameters, such as data configs, the default prefix,  and default data
     * from the database. Note that it only performs only database read-only, as to write to the database
     * is not allowed at this point if using the IntactIdGenerator.
     *
     * @param session the IntactSession to use
     *
     * @throws IntactInitializationError if something unexpected happends during loading or a check fails
     */
    public static void initIntact( IntactSession session ) throws IntactInitializationError {
        if (log.isInfoEnabled()) {
            Properties buildInfoProps = new Properties();
            try {
                buildInfoProps.load(IntactConfigurator.class.getResourceAsStream("/META-INF/BuildInfo.properties"));
            }
            catch (IOException e) {
                throw new IntactInitializationError("Problem loading build info properties", e);
            }
            String version = buildInfoProps.getProperty("version");

            if (log.isInfoEnabled())
                log.info("Initializing intact-core (" + version + ") with session of class: " + session.getClass());
        }

        if ( isInitialized( session ) ) {
            throw new IntactInitializationError( "IntAct already initialized" );
        }

        // when logging info, print init params
        if ( log.isInfoEnabled() ) {
            List<String> initParams = new ArrayList<String>();

            for ( String initParam : session.getInitParamNames() ) {
                initParams.add( initParam + "=" + session.getInitParam( initParam ) );
            }

            log.info( "Init Params: " + initParams );
        }

        RuntimeConfig config = RuntimeConfig.getCurrentInstance( session );

        if ( config.getDefaultDataConfig() == null ) {
            if (log.isDebugEnabled()) log.debug("Registering default data-config");
            // add the core model data config
            DataConfig dataConfig = IntactContext.calculateDefaultDataConfig(session);
            registerDataConfig(dataConfig, config, true);
        }

        // load the default prefix for generated ACs
        String prefix = getInitParamValue( session, IntactEnvironment.AC_PREFIX_PARAM_NAME.getFqn(), DEFAULT_AC_PREFIX );
        config.setAcPrefix( prefix );

        if ( log.isWarnEnabled() && prefix.equals( DEFAULT_AC_PREFIX ) ) {
            log.warn( "The default prefix '" + DEFAULT_AC_PREFIX + "' will be used when persisting data. Usually." +
                      "this is not the desired functionality if the application is persisting data." );
        }

        // check the schema Version
        String strForceNoSchemaCheck = getInitParamValue( session, IntactEnvironment.FORCE_NO_SCHEMA_VERSION_CHECK.getFqn(), Boolean.FALSE.toString() );
        boolean forceNoSchemaCheck = Boolean.parseBoolean( strForceNoSchemaCheck );

        if ( !forceNoSchemaCheck ) {
            checkSchemaCompatibility( config.getDefaultDataConfig(), session );
        }

        // read only
        String strReadOnly = getInitParamValue( session, IntactEnvironment.READ_ONLY_APP.getFqn(), DEFAULT_READ_ONLY_APP );
        boolean readOnly = Boolean.parseBoolean( strReadOnly );
        config.setReadOnlyApp( readOnly );
        log.debug( "Application is read-only: " + readOnly );

        // synchronize search items
        String strSynchronizeSearchItems = getInitParamValue( session,
                                                              IntactEnvironment.SYNCHRONIZED_SEARCH_ITEMS.getFqn(),
                                                              DEFAULT_SYNCHRONIZED_SEARCH_ITEMS );
        boolean syncSearchItems = Boolean.parseBoolean( strSynchronizeSearchItems );
        config.setSynchronizedSearchItems( syncSearchItems );
        log.debug( "Application synchronizes SearchItems: " + syncSearchItems );

        // auto begin transaction
        String strAutoBeginTransaction = getInitParamValue( session, IntactEnvironment.AUTO_BEGIN_TRANSACTION.getFqn(),
                                                            DEFAULT_TRANSACTION_AUTO_BEGIN );
        boolean autoBeginTransaction = Boolean.parseBoolean( strAutoBeginTransaction );
        config.setAutoBeginTransaction( autoBeginTransaction );
        log.debug( "Application will auto begin transaction: " + autoBeginTransaction );

        // debug mode
        String strDebugMode = getInitParamValue( session, IntactEnvironment.DEBUG_MODE.getFqn(),
                                                 DEFAULT_DEBUG_MODE );
        boolean debugMode = Boolean.parseBoolean( strDebugMode );
        config.setDebugMode( debugMode );
        log.debug( "Application is in debug mode: " + debugMode );

        // load the institution
        loadInstitution( session );

        // preload the most common CvObjects
        boolean preloadCommonCvs = Boolean.valueOf( getInitParamValue( session, IntactEnvironment.PRELOAD_COMMON_CVS_PARAM_NAME.getFqn(), String.valueOf( Boolean.FALSE ) ) );
        if ( preloadCommonCvs ) {
            log.info( "Preloading common CvObjects" );
            CvContext.getCurrentInstance( session ).loadCommonCvObjects();
        }

        // local cv object prefix: used for prefixes that are not part of MI or similar
        String localCvPrefix = getInitParamValue(session, IntactEnvironment.LOCAL_CV_PREFIX.getFqn(), DEFAULT_LOCAL_CV_PREFIX);
        config.setLocalCvPrefix(localCvPrefix);

        setInitialized( session, true );
    }

    /**
     * Registers a data-config, returning true if it has been correctly registered, false otherwise
     * @return true if it has been correctly registered, false otherwise
     */
    private static boolean registerDataConfig(DataConfig dataConfig, RuntimeConfig config, boolean isDefault) {
        log.info("Registering data-config: " + dataConfig.getName());

        if (!dataConfig.isInitialized()) {
            dataConfig.initialize();
        }

        config.addDataConfig(dataConfig, isDefault);

        return true;
    }

    public static IntactContext createIntactContext( IntactSession session ) {
        if ( RuntimeConfig.getCurrentInstance( session ).getDataConfigs().isEmpty() ) {
            log.warn( "No data configs found. Re-initializing IntAct" );
            initIntact( session );
        }

        IntactContext context;

        log.debug( "Creating data context..." );
        DataContext dataContext = new DataContext( session );

        // start a context
        log.debug( "Creating IntactContext..." );
        context = new IntactContextWrapper( dataContext, session );

        initializeDatabaseIfNecessary(context);

        return context;
    }

    /**
     * Initialize the database, adding the institution and schema version, if necessary
     * @param context IntactContext
     */
    public static void initializeDatabaseIfNecessary(IntactContext context) {
        try {
           persistInstitutionAndCvsIfNecessary( context );
        } catch ( IntactTransactionException e ) {
            throw new IntactException( "Error while persisting institution.", e );
        }

        persistSchemaVersionIfNecessary( context );
    }

    /**
     * Initialize the database, adding the institution and schema version
     * @param context IntactContext
     */
    public static void initializeDatabase(IntactContext context) {
        try {
            persistInstitution(context);
        } catch (IntactTransactionException e) {
            throw new IntactException("Error while persisting institution.", e);
        }

        persistSchemaVersion(context);
        persistBasicCvObjects(context);
    }

    private static void checkSchemaCompatibility( DataConfig dataConfig, IntactSession session ) {
        SchemaVersion requiredVersion = dataConfig.getMinimumRequiredVersion();

        DaoFactory daoFactory = DaoFactory.getCurrentInstance( session, RuntimeConfig.getCurrentInstance( session ).getDefaultDataConfig() );

        EntityTransaction tx = daoFactory.beginTransaction();
        DbInfo dbInfoSchemaVersion = daoFactory.getDbInfoDao().get( DbInfo.SCHEMA_VERSION );
        try {
            tx.commit();
        } catch ( Exception e ) {
            log.error( "Exception committing " + e );
        }
        SchemaVersion schemaVersion;

        if ( dbInfoSchemaVersion == null ) {
            log.warn( "Schema version does not exist. Will be created" );
            setSchemaVersionToBePersisted( session );
            return;
        }

        try {
            schemaVersion = SchemaVersion.parse( dbInfoSchemaVersion.getValue() );
        }
        catch ( Exception e ) {
            throw new IntactInitializationError( "Error getting schema version", e );
        }

        log.info( "Schema Version: " + schemaVersion );

        if ( !schemaVersion.isCompatibleWith( requiredVersion ) ) {
            throw new IntactInitializationError( "Database schema version " + requiredVersion + " is required" +
                                                 " to use this version of intact-core. Schema version found: " + schemaVersion );
        }
    }

    private static String getInitParamValue( IntactSession session, String initParamName, String defaultValue ) {
        return getInitParamValue( session, initParamName, defaultValue, initParamName );
    }

    private static String getInitParamValue( IntactSession session, String initParamName, String defaultValue, String systemPropertyDefault ) {
        String initParamValue = null;

        if ( session.containsInitParam( initParamName ) ) {
            initParamValue = session.getInitParam( initParamName );
            log.debug( initParamName + ": " + initParamValue );
        } else {
            if ( session.isWebapp() ) {
                log.warn( "Init-Param missing in web.xml: " + initParamName );
            }
        }

        if ( systemPropertyDefault != null ) {
            String propValue = System.getProperty( systemPropertyDefault );

            if ( propValue != null ) {
                log.debug( "Found environment property for param: " + initParamName + "=" + propValue );
                initParamValue = propValue;
            }
        }

        if ( initParamValue == null ) {
            log.debug( "Using default value for param " + initParamName + ": " + defaultValue );
            initParamValue = defaultValue;
        }

        return initParamValue;
    }

    private static void loadInstitution( IntactSession session ) {
        String institutionLabel = getInitParamValue( session, IntactEnvironment.INSTITUTION_LABEL.getFqn(), null, "institution" );

        if ( institutionLabel == null ) {
            String message;
            if ( session.isWebapp() ) {
                message = "An institution label is recommended. " +
                                           "Provide it by setting the init parameter " + IntactEnvironment.INSTITUTION_LABEL + " " +
                                           "in the web.xml file";
            } else {
                message = "An institution label is recommended. " +
                                           "Provide it by setting the environment variable 'institution'" +
                                           " when executing the java command. (e.g. java ... -Dinstitution=yourInstitution)." +
                                           " You can also set the init parameter " + IntactEnvironment.INSTITUTION_LABEL + " in the IntactSession Object. " ;
            }
            if (log.isWarnEnabled()) log.warn(message);

            institutionLabel = DEFAULT_INSTITUTION_LABEL;
        }

        DaoFactory daoFactory = DaoFactory.getCurrentInstance( session, RuntimeConfig.getCurrentInstance( session ).getDefaultDataConfig() );

        Institution institution = daoFactory
                .getInstitutionDao().getByShortLabel( institutionLabel );

        if ( institution == null ) {
            // doesn't exist, create it
            institution = new Institution( institutionLabel );

            String fullName;
            String postalAddress;
            String url;

            if ( institutionLabel.equalsIgnoreCase( EBI_INSTITUTION_LABEL ) || institutionLabel.equals( EBI_INSTITUTION_ALIAS )) {
                fullName = getInitParamValue( session, IntactEnvironment.INSTITUTION_FULL_NAME.getFqn(), EBI_INSTITUTION_FULL_NAME);
                postalAddress = getInitParamValue( session, IntactEnvironment.INSTITUTION_POSTAL_ADDRESS.getFqn(), EBI_INSTITUTION_POSTAL_ADDRESS);
                url = getInitParamValue( session, IntactEnvironment.INSTITUTION_URL.getFqn(), EBI_INSTITUTION_URL);
            } else {
                fullName = getInitParamValue( session, IntactEnvironment.INSTITUTION_FULL_NAME.getFqn(), "" );
                postalAddress = getInitParamValue( session, IntactEnvironment.INSTITUTION_POSTAL_ADDRESS.getFqn(), "" );
                url = getInitParamValue( session, IntactEnvironment.INSTITUTION_URL.getFqn(), "" );
            }

            // try to assign an xref if intact or mint
            if (institutionLabel.equals(Institution.INTACT)) {
                InstitutionXref xref = XrefUtils.createIdentityXrefPsiMi(institution, Institution.INTACT_REF);
                institution.addXref(xref);

                InstitutionAlias alias = AliasUtils.createAlias(institution, EBI_INSTITUTION_ALIAS, null);
                institution.addAlias(alias);

            } else if (institutionLabel.equals(Institution.MINT)) {
                InstitutionXref xref = XrefUtils.createIdentityXrefPsiMi(institution, Institution.MINT_REF);
                institution.addXref(xref);
            }

            if (!institutionLabel.equals(DEFAULT_INSTITUTION_LABEL)) {
                institution.setFullName( fullName );
                institution.setPostalAddress( postalAddress );
                institution.setUrl( url );
            }

            log.warn( "Institution does not exist. Will be created." );
            setInstitutionToBePersisted( session );
        } else {
            // prefetch xrefs
            institution = daoFactory
                .getInstitutionDao().getByAc(institution.getAc(), true);
        }

        RuntimeConfig.getCurrentInstance( session ).setInstitution( institution );
        log.debug( "Institution: " + institution.getShortLabel() );
    }

    private static void persistInstitutionAndCvsIfNecessary( IntactContext context ) throws IntactTransactionException {
        IntactSession session = context.getSession();

        Object obj = session.getApplicationAttribute( INSTITUTION_TO_BE_PERSISTED_FLAG );

        if ( obj == null ) {
            return;
        }

        boolean needsToBePersisted = ( Boolean ) obj;

        if ( needsToBePersisted ) {
            persistInstitution(context);
            persistBasicCvObjects(context);

            session.setApplicationAttribute( INSTITUTION_TO_BE_PERSISTED_FLAG, Boolean.FALSE );
        }
    }

    private static void persistInstitution(IntactContext context) throws IntactTransactionException {

        Institution institution = context.getConfig().getInstitution();

        if (institution == null) {
            throw new NullPointerException("Institution is null. Set an institution to the RuntimeConfig first");
        }

        DaoFactory daoFactory = getDefaultDaoFactory(context);

        if (institution.getAc() != null)  {
            log.warn("Institution already has an AC: '"+institution.getShortLabel()+"' ("+institution.getAc()+").");
            if (daoFactory.getInstitutionDao().getByAc(institution.getAc()) == null) {
                log.debug("\tAnd does not exist in the DB. Replicating.");
                daoFactory.beginTransaction();
                daoFactory.getInstitutionDao().replicate(institution);
                context.getDataContext().commitTransaction();
            } else {
                log.debug("\tAnd already exists in the DB. Ignored.");
            }
        } else {
            log.debug("Persisting institution: " + institution.getShortLabel());
            PersisterHelper.saveOrUpdate(institution);
        }
    }

    private static void persistSchemaVersionIfNecessary( IntactContext context ) {
        IntactSession session = context.getSession();

        Object obj = session.getApplicationAttribute( SCHEMA_VERSION_TO_BE_PERSISTED_FLAG );

        if ( obj == null ) {
            return;
        }

        boolean needsToBePersisted = ( Boolean ) obj;

        if ( needsToBePersisted ) {
            persistSchemaVersion(context);

            session.setApplicationAttribute( SCHEMA_VERSION_TO_BE_PERSISTED_FLAG, Boolean.FALSE );
        }
    }

    private static void persistSchemaVersion(IntactContext context) {
        IntactSession session = context.getSession();
        SchemaVersion requiredVersion = context.getConfig().getDefaultDataConfig().getMinimumRequiredVersion();

        DbInfo dbInfo = new DbInfo(DbInfo.SCHEMA_VERSION, requiredVersion.toString());

        if (log.isDebugEnabled()) log.debug("Persisting schema version: " + requiredVersion.toString());

        DaoFactory daoFactory = DaoFactory.getCurrentInstance(session, RuntimeConfig.getCurrentInstance(session).getDefaultDataConfig());
        context.getDataContext().beginTransaction();
        daoFactory.getDbInfoDao().persist(dbInfo);
        try {
            context.getDataContext().commitTransaction();
        } catch (IntactTransactionException e) {
            log.error(e);
        }
    }

    private static void persistBasicCvObjects(IntactContext context) {
        log.debug("Persisting necessary CvObjects (EssentialCvPrimer)");

        DaoFactory daoFactory = getDefaultDaoFactory(context);

        daoFactory.beginTransaction();

        CvPrimer cvPrimer = new EssentialCvPrimer(daoFactory);
        cvPrimer.createCVs();

        try {
            context.getDataContext().commitTransaction();
        } catch (IntactTransactionException e) {
            log.error(e);
        }
    }

    private static DaoFactory getDefaultDaoFactory(IntactContext context) {
        IntactSession session = context.getSession();
        return DaoFactory.getCurrentInstance(session, RuntimeConfig.getCurrentInstance(session).getDefaultDataConfig());
    }


    private static void setInstitutionToBePersisted( IntactSession session ) {
        session.setApplicationAttribute( INSTITUTION_TO_BE_PERSISTED_FLAG, Boolean.TRUE );
    }

    private static void setSchemaVersionToBePersisted( IntactSession session ) {
        session.setApplicationAttribute( SCHEMA_VERSION_TO_BE_PERSISTED_FLAG, Boolean.TRUE );
    }

    private static void setInitialized( IntactSession session, boolean initialized ) {
        session.setApplicationAttribute( INITIALIZED_APP_ATT_NAME, initialized );
    }

    public static boolean isInitialized( IntactSession session ) {
        Object obj = session.getApplicationAttribute( INITIALIZED_APP_ATT_NAME );

        if ( obj == null ) {
            return false;
        }

        return ( Boolean ) obj;
    }
}
