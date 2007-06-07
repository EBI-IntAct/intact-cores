/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.config.impl.StandardCoreDataConfig;
import uk.ac.ebi.intact.model.Institution;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO: comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/08/2006</pre>
 */
public final class RuntimeConfig implements Serializable {

    private static final Log log = LogFactory.getLog( RuntimeConfig.class );

    private static final String APPLICATION_PARAM_NAME = RuntimeConfig.class.getName() + ".CONFIG";
    private static final String DEFAULT_DATA_CONFIG_PARAM_NAME = RuntimeConfig.class.getName() + ".DEFAULT_DATA_CONFIG";

    private IntactSession session;

    private Institution institution;
    private String acPrefix;
    private final Map<String, DataConfig> dataConfigs;

    private boolean readOnlyApp;
    private boolean isAutoBeginTransaction;
    private boolean synchronizedSearchItems;
    private boolean debugMode;

    private RuntimeConfig( IntactSession session ) {
        this.dataConfigs = new HashMap<String, DataConfig>();
        this.session = session;
    }

    public static RuntimeConfig getCurrentInstance( IntactSession session ) {
        RuntimeConfig runtimeConfig
                = ( RuntimeConfig ) session.getApplicationAttribute( APPLICATION_PARAM_NAME );
        if ( runtimeConfig == null ) {
            runtimeConfig = initRuntime( session, null );
        }
        return runtimeConfig;
    }

    public static RuntimeConfig initRuntime( IntactSession session, DataConfig dataConfig ) {
        log.debug( "Creating new RuntimeConfig" );
        RuntimeConfig runtimeConfig = new RuntimeConfig( session );

        if ( dataConfig != null ) {
            runtimeConfig.addDataConfig( dataConfig, true );
        }

        session.setApplicationAttribute( APPLICATION_PARAM_NAME, runtimeConfig );

        return runtimeConfig;
    }

    public Institution getInstitution() throws IntactException {
        return institution;
    }

    public void setInstitution( Institution institution ) {
        this.institution = institution;
    }

    public String getAcPrefix() {
        return acPrefix;
    }

    public void setAcPrefix( String acPrefix ) {
        this.acPrefix = acPrefix;
    }

    public Collection<DataConfig> getDataConfigs() {
        return dataConfigs.values();
    }

    public DataConfig getDataConfig( String name ) {
        return dataConfigs.get( name );
    }

    public void addDataConfig( DataConfig dataConfig ) {
        addDataConfig( dataConfig, false );
    }

    public void addDataConfig( DataConfig dataConfig, boolean isTheDefaultOne ) {
        if ( !dataConfig.isInitialized() ) {
            throw new IllegalArgumentException( "DataConfig added to RuntimeConfig must be already initialized: " + dataConfig.getName() );
        }

        this.dataConfigs.put( dataConfig.getName(), dataConfig );

        if ( isTheDefaultOne ) {
            setDefaultDataConfig( dataConfig );
        }
    }

    public void setDefaultDataConfig( DataConfig dataConfig ) {
        if ( !dataConfigs.containsKey( dataConfig.getName() ) ) {
            addDataConfig( dataConfig );
        }

        session.setApplicationAttribute( DEFAULT_DATA_CONFIG_PARAM_NAME, dataConfig );
    }

    public DataConfig getDefaultDataConfig() {
        DataConfig defaultDataConfig = ( DataConfig ) session.getApplicationAttribute( DEFAULT_DATA_CONFIG_PARAM_NAME );

        if ( defaultDataConfig == null ) {
            log.warn( "No default data config configured. Using: " + StandardCoreDataConfig.NAME );
            defaultDataConfig = getDataConfig( StandardCoreDataConfig.NAME );
            session.setApplicationAttribute( DEFAULT_DATA_CONFIG_PARAM_NAME, defaultDataConfig );
        }

        return defaultDataConfig;
    }

    public boolean isAutoBeginTransaction() {
         return isAutoBeginTransaction;
    }

    public void setAutoBeginTransaction( boolean autoBeginTransaction ) {
        isAutoBeginTransaction = autoBeginTransaction;
    }

    public boolean isReadOnlyApp() {
        return readOnlyApp;
    }

    public void setReadOnlyApp( boolean readOnlyApp ) {
        this.readOnlyApp = readOnlyApp;
    }

    public boolean isSynchronizedSearchItems() {
        return synchronizedSearchItems;
    }

    public void setSynchronizedSearchItems( boolean synchronizedSearchItems ) {
        this.synchronizedSearchItems = synchronizedSearchItems;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode( boolean debugMode ) {
        this.debugMode = debugMode;
    }
}
