/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.config.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.EmptyInterceptor;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.config.ConfigurationException;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.persistence.util.ImportFromClasspathEntityResolver;
import uk.ac.ebi.intact.persistence.util.IntactAnnotator;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Aug-2006</pre>
 */
public abstract class AbstractHibernateDataConfig extends DataConfig<SessionFactory, Configuration> {

    private static final Log log = LogFactory.getLog( AbstractHibernateDataConfig.class );

    private static final String INTERCEPTOR_CLASS = "hibernate.util.interceptor_class";
    private static final String NOT_DEFINED_JDBC_DRIVER = "NOT_DEFINED";

    private Configuration configuration;

    private SessionFactory sessionFactory;

    private List<String> packagesWithEntities;

    public AbstractHibernateDataConfig( IntactSession session ) {
        super( session );
        this.packagesWithEntities = getPackagesWithEntities();

        configuration = getConfiguration();
    }


    @Override
    public void initialize() {
        log.debug( "Initializing Hibernate" );

        if ( isInitialized() ) {
            log.debug( "Hibernate already initialized" );
            return;
        }

        File cfgFile = getConfigFile();

        // Create the initial SessionFactory from the default configuration files
        try {
            for ( String packageName : getPackagesWithEntities() ) {
                log.debug( "Processing package: " + packageName );

                List<Class> annotatedClasses = IntactAnnotator.getAnnotatedClasses( packageName );

                if ( annotatedClasses.size() == 0 ) {
                    log.error( "No annotated classes found in: " + packageName +
                               ". Be aware that if the package also exists in the src/test/java folder your " +
                               "entities won't be loaded." );
                }

                for ( Class clazz : annotatedClasses ) {
                    log.debug( "Adding annotated class to hibernate: " + clazz.getName() );
                    ( ( AnnotationConfiguration ) configuration ).addAnnotatedClass( clazz );
                }
            }

            // This custom entity resolver supports entity placeholders in XML mapping files
            // and tries to resolve them on the classpath as a resource
            configuration.setEntityResolver( new ImportFromClasspathEntityResolver() );

            // Read not only hibernate.properties, but also hibernate.cfg.xml
            if ( cfgFile != null ) {
                log.info( "Reading from config file: " + cfgFile );

                try {
                    configuration.configure( cfgFile );
                }
                catch ( Throwable t ) {
                    throw new ConfigurationException( "Couldn't configure hibernate using file: " + cfgFile );
                }
            } else {
                log.info( "Reading from default config file" );
                try {
                    configuration.configure();
                }
                catch ( Throwable t ) {
                    throw new ConfigurationException( "Couldn't configure hibernate using default file", t );
                }
            }

            // Set global interceptor from configuration
            setInterceptor( configuration, null );

            log.debug( "Session is webapp: " + getSession().isWebapp() + " / SessionFactory name: " + configuration.getProperty( Environment.SESSION_FACTORY_NAME ) );

            checkConfiguration( configuration );

            if ( getSession().isWebapp() && configuration.getProperty( Environment.SESSION_FACTORY_NAME ) != null ) {
                // Let Hibernate bind the factory to JNDI
                log.debug( "Building webapp sessionFactory: " + configuration.getProperty( Environment.SESSION_FACTORY_NAME ) );
                configuration.buildSessionFactory();
            } else {
                // or use static variable handling
                configuration.getProperties().remove( Environment.SESSION_FACTORY_NAME );

                log.debug( "Building standalone sessionFactory" );
                sessionFactory = configuration.buildSessionFactory();
            }

        }
        catch ( HibernateException ex ) {
            log.error( "Building SessionFactory failed.", ex );
            throw new ExceptionInInitializerError( ex );
        }

        setInitialized( true );
    }

    private void checkConfiguration( Configuration config ) throws ConfigurationException {
        String hibernateFile = ( getConfigFile() != null ) ? getConfigFile().toString() :
                               AbstractHibernateDataConfig.class.getResource( "/hibernate.cfg.xml" ).getFile();

        String driver = config.getProperty( Environment.DRIVER );

        if ( driver.equals( NOT_DEFINED_JDBC_DRIVER ) ) {
            try {
                throw new ConfigurationException( "Not defined JDBC driver in the hibernate.cfg.xml (" + hibernateFile + ") file.\n"
                                                  + configPropertiesDump( config ) );
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    private String configPropertiesDump( Configuration config ) throws IOException {
        StringWriter writer = new StringWriter();
        PrintWriter pWriter = new PrintWriter( writer );

        Properties props = config.getProperties();

        Enumeration e = props.propertyNames();

        while ( e.hasMoreElements() ) {
            String name = ( String ) e.nextElement();

            if ( name.startsWith( "hibernate." ) ) {
                pWriter.println( name + "=" + props.getProperty( name ) );
            }
        }

        pWriter.close();
        writer.close();
        return writer.toString();
    }

    @Override
    public SessionFactory getSessionFactory() {
        if ( sessionFactory != null ) {
            return sessionFactory;
        }

        if ( getSession().isWebapp() ) {
            try {
                configuration.configure();
                String sessionFactoryName = configuration.getProperty( Environment.SESSION_FACTORY_NAME );
                log.debug( "Looking up sessionFactory from JNDI: " + sessionFactoryName );

                if ( sessionFactoryName != null ) {
                    sessionFactory = ( SessionFactory ) new InitialContext().lookup( configuration.getProperty( Environment.SESSION_FACTORY_NAME ) );

                    setInitialized( true );
                }
            }
            catch ( ClassCastException cce ) {
                log.debug( "Classcast exception thrown when getting the sessionFactory from JNDI. " +
                           "Probably initializing application and trying to get an instance created " +
                           "with a different classloader" );
            }
            catch ( NamingException ne ) {
                log.debug( "SessionFactory not found in JNDI: " + configuration.getProperty( Environment.SESSION_FACTORY_NAME ) );
            }
        }

        if ( sessionFactory == null ) {
            checkInitialization();

            if ( sessionFactory != null ) {
                return sessionFactory;
            }

            try {
                sessionFactory = ( SessionFactory ) new InitialContext().lookup( configuration.getProperty( Environment.SESSION_FACTORY_NAME ) );
            }
            catch ( NamingException e ) {
                throw new IntactException( "SessionFactory could not be retrieved from JNDI: " + Environment.SESSION_FACTORY_NAME );
            }
        }

        return sessionFactory;
    }


    public void closeSessionFactory() {
        if ( sessionFactory != null ) {
            sessionFactory.close();
        }
    }

    @Override
    public Configuration getConfiguration() {
        if ( configuration == null ) {
            configuration = new AnnotationConfiguration();
        }

        return configuration;
    }

    public void addPackageWithEntities( String packageName ) {
        if ( isInitialized() ) {
            throw new IntactException( "Cannot add package after the sessionFactory has been initialized" );
        }

        packagesWithEntities.add( packageName );
    }

    protected abstract List<String> getPackagesWithEntities();

    protected abstract File getConfigFile();

    /**
     * Either sets the given interceptor on the configuration or looks
     * it up from configuration if null.
     */
    private void setInterceptor( Configuration configuration, Interceptor interceptor ) {
        String interceptorName = configuration.getProperty( INTERCEPTOR_CLASS );
        if ( interceptor == null && interceptorName != null ) {
            try {
                Class interceptorClass =
                        AbstractHibernateDataConfig.class.getClassLoader().loadClass( interceptorName );
                interceptor = ( Interceptor ) interceptorClass.newInstance();
            } catch ( Exception ex ) {
                throw new RuntimeException( "Could not initialize interceptor: " + interceptorName, ex );
            }
        }
        if ( interceptor != null ) {
            configuration.setInterceptor( interceptor );
        } else {
            configuration.setInterceptor( EmptyInterceptor.INSTANCE );
        }
    }
}
