/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Contains the attributes stored by the IntAct applications. It has to be used to get and set
 * all the attributes used by the application.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/08/2006</pre>
 */
public abstract class IntactSession {

    private static final Log log = LogFactory.getLog( IntactSession.class );

    private static final String DEFAULT_PROP_FILE = "intact.properties";
    private static final String CONFIG_FILE_SYSTEM_VAR = "intact.config.file";

    protected void readDefaultProperties() throws IOException {
        // first read the properties for the classpath
        Properties props = readFromClasspathProperties();

        // if an intact.properties file is in the filesystem (from the dir where the app is called)
        // or a system variable intact.config.file has been defined, overwrite the properties with
        // those from that file
        Properties propsSystem = readFromFilesystem();
        props.putAll( propsSystem );

        // read properties supplied in the system environment (or provided with the -D parameter)
        Properties propsEnvironment = readFromEnvironment();
        props.putAll( propsEnvironment );

        // init
        initParametersWithProperties( props );
    }


    private Properties readFromClasspathProperties() throws IOException {
        Properties properties = new Properties();

        URL intactPropertiesFilename = IntactSession.class.getResource( "/" + DEFAULT_PROP_FILE );

        if ( intactPropertiesFilename != null ) {
            File intactPropsFile = new File( intactPropertiesFilename.getFile() );

            if ( intactPropsFile.exists() && !intactPropsFile.isDirectory() ) {
                log.info( "Loading properties from classpath: " + intactPropertiesFilename );
                properties.load( new FileInputStream( intactPropsFile ) );
            }
        }

        return properties;
    }

    /**
     * Check if there is a default file in the filesystem or if the intact.config.file system property
     * has been configured and points to a file. If any of those cases is true, read the file and
     * return the properties
     */
    private Properties readFromFilesystem() throws IOException {
        Properties properties = new Properties();

        File propFile = new File( DEFAULT_PROP_FILE );

        if ( propFile.exists() && !propFile.isDirectory() ) {
            log.info( "Loading properties from filesystem: " + propFile );
            properties.load( new FileInputStream( propFile ) );
        } else {
            String filePath = System.getProperty( CONFIG_FILE_SYSTEM_VAR );

            if ( filePath != null ) {
                propFile = new File( filePath );

                if ( propFile.exists() && !propFile.isDirectory() ) {
                    log.info( "Loading properties from filesystem: " + propFile );
                    properties.load( new FileInputStream( propFile ) );
                }
            }
        }

        return properties;
    }

    /**
     * Check the system properties for IntactEnvironment properties
     */
    private Properties readFromEnvironment() {
        Properties properties = new Properties();

        Enumeration<String> systemPropNames = ( Enumeration<String> ) System.getProperties().propertyNames();

        while ( systemPropNames.hasMoreElements() ) {
            String propName = systemPropNames.nextElement();

            if ( propName.startsWith( "uk.ac.ebi.intact" ) ) {
                for ( IntactEnvironment env : IntactEnvironment.values() ) {
                    if ( env.getFqn().equals( propName ) ) {
                        if ( log.isDebugEnabled() )
                            log.debug( "Property found in environment: " + propName + "=" + System.getProperty( propName ) );

                        properties.put( propName, System.getProperty( propName ) );
                    }
                }
            }
        }

        return properties;
    }

    protected void initParametersWithProperties( Properties properties ) {
        Enumeration<String> propNames = ( Enumeration<String> ) properties.propertyNames();

        while ( propNames.hasMoreElements() ) {
            String propName = propNames.nextElement();
            setInitParam( propName, properties.getProperty( propName ) );
        }
    }

    public abstract Object getApplicationAttribute( String name );

    public abstract void setApplicationAttribute( String name, Object attribute );

    public abstract Serializable getAttribute( String name );

    public abstract void setAttribute( String name, Serializable attribute );

    public abstract Object getRequestAttribute( String name );

    public abstract void setRequestAttribute( String name, Object value );

    public abstract boolean containsInitParam( String name );

    public abstract String getInitParam( String name );

    public abstract void setInitParam( String name, String value );

    public abstract Collection<String> getInitParamNames();

    public abstract Collection<String> getAttributeNames();

    public abstract Collection<String> getApplicationAttributeNames();

    public abstract Collection<String> getRequestAttributeNames();

    public abstract boolean isWebapp();

    public abstract boolean isRequestAvailable();
}
