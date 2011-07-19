/**
 * Copyright 2011 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.core.config;

import com.google.common.collect.Maps;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.annotations.PersistentConfiguration;
import uk.ac.ebi.intact.core.annotations.PersistentProperty;
import uk.ac.ebi.intact.core.config.property.*;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.meta.ApplicationDao;
import uk.ac.ebi.intact.model.meta.Application;
import uk.ac.ebi.intact.model.meta.ApplicationProperty;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Controller
public class ConfigurationHandler {

    private static final Log log = LogFactory.getLog(ConfigurationHandler.class);

    @Transactional
    public void loadConfiguration(Application application) {
        Application appFromDb = getApplicationDao().getByKey(application.getKey());

        if (appFromDb != null) {
            application = appFromDb;
        }

        if (log.isInfoEnabled()) {
            log.info("Loading configuration for application: "+application.getKey());
        }

        final ConfigurableListableBeanFactory beanFactory = IntactContext.getCurrentInstance().getSpringContext().getBeanFactory();
        final Map<String,Object> beansWithConfig = beanFactory.getBeansWithAnnotation( PersistentConfiguration.class );

        for (Map.Entry<String,Object> entry : beansWithConfig.entrySet()) {

            final String beanName = entry.getKey();
            final Object beanWithConfig = entry.getValue();

            for (Field field : beanWithConfig.getClass().getDeclaredFields()) {
                PersistentProperty persistentProperty = field.getAnnotation(PersistentProperty.class);

                if (persistentProperty != null) {
                    String key = calculateKey(beanName, field, persistentProperty);

                    ApplicationProperty applicationProperty = application.getProperty(key);

                    if (applicationProperty != null) {
                        // set the property
                        String value = applicationProperty.getValue();

                        if (log.isDebugEnabled()) {
                            log.debug("Loading field (db): "+key+"="+value);
                        }

                        setConfigValue(beanWithConfig, field, value);
                    } else {
                        String value = getConfigValue(beanWithConfig, field, persistentProperty.defaultValue());

                        if (log.isDebugEnabled()) {
                            log.debug("Loading field (beanWithConfig object): "+key+"="+value);
                        }

                        // create the property
                        applicationProperty = new ApplicationProperty(application, key, value);
                        application.addProperty(applicationProperty);
                    }

                }
            }
        }
    }

    @Transactional
    public void persistConfiguration() {
        // update the Application object with the latest changes, since the last time the config was loaded
        Application application = IntactContext.getCurrentInstance().getApplication();
        if (log.isInfoEnabled()) log.info("Persisting configuration for application: "+application.getKey());

        Application dbApp = getApplicationDao().getByKey(application.getKey());
        if (dbApp != null) {
            synchronizeApplication( application, dbApp );
            application = dbApp;
        }

        Map<String,Object> beansWithConfig = IntactContext.getCurrentInstance().getSpringContext().getBeanFactory().getBeansWithAnnotation(PersistentConfiguration.class);

        for (Map.Entry<String,Object> entry : beansWithConfig.entrySet()) {

            final String beanName = entry.getKey();
            final Object beanWithConfig = entry.getValue();

            for (Field field : beanWithConfig.getClass().getDeclaredFields()) {
                PersistentProperty persistentProperty = field.getAnnotation(PersistentProperty.class);

                if (persistentProperty != null) {
                    String key = calculateKey(beanName, field, persistentProperty);

                    ApplicationProperty applicationProperty = application.getProperty(key);

                    String value = getConfigValue(beanWithConfig, field, persistentProperty.defaultValue());

                    if (applicationProperty == null) {
                        if (log.isDebugEnabled()) {
                            log.debug("Creating field: " + key + "=" + value);
                        }

                        applicationProperty = new ApplicationProperty(application, key, value);
                        application.addProperty(applicationProperty);
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("Updating field: " + key + "=" + value);
                        }

                        applicationProperty.setValue(value);
                    }
                }
            }
        }

        // persist the application object
        getApplicationDao().saveOrUpdate(application);
    }

    /**
     * Synchronize all attributes of source onto target.
     * @param source the source application
     * @param target the target application
     */
    private void synchronizeApplication( Application source, Application target ) {
        target.setDescription( source.getDescription() );

        // create in target missing properties and update existing ones
        for ( ApplicationProperty sourceProperty : source.getProperties() ) {
            final ApplicationProperty targetProperty = target.getProperty( sourceProperty.getKey() );
            if( targetProperty != null ) {
                 targetProperty.setValue( sourceProperty.getValue() );
             } else {
                 target.addProperty( sourceProperty );
             }
        }

        // remove from target those properties that are not in the source
//        final Iterator<ApplicationProperty> targetPropertiesIterator = target.getProperties().iterator();
//        while ( targetPropertiesIterator.hasNext() ) {
//            ApplicationProperty targetProperty = targetPropertiesIterator.next();
//            if( source.getProperty( targetProperty.getKey() ) == null ) {
//                targetPropertiesIterator.remove();
//            }
//        }
    }

    private void setConfigValue(Object beanWithConfig, Field field, String value) {
        try {
            PropertyConverter converter = getPropertyConverter( field.getType() );
            Object objectValue = converter.convertFromString( value );
            PropertyUtils.setProperty(beanWithConfig, field.getName(), objectValue);
        } catch (Throwable e) {
            throw new RuntimeException("Problem writing field: "+field.getName(), e);
        }
    }

    private String getConfigValue(Object beanWithConfig, Field field, String defaultValue) {
        // get the value
        Object objValue = null;
        try {
            objValue = PropertyUtils.getProperty(beanWithConfig, field.getName());
        } catch (Throwable e) {
            throw new RuntimeException("Problem reading field: "+field.getName(), e);
        }

        PropertyConverter converter = getPropertyConverter( objValue.getClass() );
        String value = converter.convertToString(objValue);

        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    private static Map<Class, PropertyConverter> supportedPrimitiveConverter = Maps.newHashMap();
    static {
        final ConfigurableApplicationContext springContext = IntactContext.getCurrentInstance().getSpringContext();

        supportedPrimitiveConverter.put( java.lang.Boolean.TYPE, springContext.getBean( BooleanPropertyConverter.class ) );
        supportedPrimitiveConverter.put( java.lang.Short.TYPE, springContext.getBean( ShortPropertyConverter.class ) );
        supportedPrimitiveConverter.put( java.lang.Integer.TYPE, springContext.getBean( IntegerPropertyConverter.class ) );
        supportedPrimitiveConverter.put( java.lang.Long.TYPE, springContext.getBean( LongPropertyConverter.class ) );
        supportedPrimitiveConverter.put( java.lang.Float.TYPE, springContext.getBean( FloatPropertyConverter.class ) );
        supportedPrimitiveConverter.put( java.lang.Double.TYPE, springContext.getBean( DoublePropertyConverter.class ) );
        supportedPrimitiveConverter.put( java.lang.Character.TYPE, springContext.getBean( CharPropertyConverter.class ) );
    }

    private PropertyConverter getPropertyConverter( Class clazz ) {

        PropertyConverter converter = null;

        final ConfigurableApplicationContext springContext = IntactContext.getCurrentInstance().getSpringContext();
        if( supportedPrimitiveConverter.containsKey( clazz ) ) {
            // we have a converter for this primitive type
            converter = supportedPrimitiveConverter.get( clazz );
        } else {
            final Map<String, PropertyConverter> converters = springContext.getBeansOfType( PropertyConverter.class );
            for (PropertyConverter converterCandidate : converters.values()) {
                if (converterCandidate.getObjectType().isAssignableFrom( clazz )) {
                    converter = converterCandidate;
                    break;
                }
            }
        }

        if( converter == null ) {
            throw new RuntimeException( "Could not find a PropertyConverter for attribute of type: " + clazz.getName() );
        }

        return converter;
    }

    private String calculateKey(String beanName, Field field, PersistentProperty persistentProperty) {
        String key = persistentProperty.key();

        if (key.isEmpty()) {
            key = beanName+"."+field.getName();
        }
        return key;
    }

    private ApplicationDao getApplicationDao() {
        return IntactContext.getCurrentInstance().getDaoFactory().getApplicationDao();
    }
}
