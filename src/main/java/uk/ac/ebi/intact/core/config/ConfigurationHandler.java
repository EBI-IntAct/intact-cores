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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.annotations.PersistentConfiguration;
import uk.ac.ebi.intact.core.annotations.PersistentProperty;
import uk.ac.ebi.intact.core.config.property.PropertyConverter;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.meta.ApplicationDao;
import uk.ac.ebi.intact.model.meta.Application;
import uk.ac.ebi.intact.model.meta.ApplicationProperty;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

        Map<String,Object> beansWithConfig = IntactContext.getCurrentInstance().getSpringContext().getBeanFactory().getBeansWithAnnotation(PersistentConfiguration.class);

        for (Map.Entry<String,Object> entry : beansWithConfig.entrySet()) {

            String beanName = entry.getKey();
            Object config = entry.getValue();

            for (Field field : config.getClass().getDeclaredFields()) {
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

                        setConfigValue(config, field, value);
                    } else {
                        String value = getConfigValue(config, field, persistentProperty.defaultValue());

                        if (log.isDebugEnabled()) {
                            log.debug("Loading field (config object): "+key+"="+value);
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

        if (application.getAc() != null) {
            application = getApplicationDao().getByKey(application.getKey());
        }

        if (log.isInfoEnabled()) log.info("Persisting configuration for application: "+application.getKey());

        Map<String,Object> beansWithConfig = IntactContext.getCurrentInstance().getSpringContext().getBeanFactory().getBeansWithAnnotation(PersistentConfiguration.class);

        for (Map.Entry<String,Object> entry : beansWithConfig.entrySet()) {

            String beanName = entry.getKey();
            Object config = entry.getValue();

            for (Field field : config.getClass().getDeclaredFields()) {
                PersistentProperty persistentProperty = field.getAnnotation(PersistentProperty.class);

                if (persistentProperty != null) {
                    String key = calculateKey(beanName, field, persistentProperty);

                    ApplicationProperty applicationProperty = application.getProperty(key);

                    String value = getConfigValue(config, field, persistentProperty.defaultValue());

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

    private void setConfigValue(Object config, Field field, String value) {
        try {
            PropertyUtils.setProperty(config, field.getName(), value);
        } catch (Throwable e) {
            throw new RuntimeException("Problem writing field: "+field.getName(), e);
        }
    }

    private String getConfigValue(Object config, Field field, String defaultValue) {
        // get the value
        Object objValue = null;
        try {
            objValue = PropertyUtils.getProperty(config, field.getName());
        } catch (Throwable e) {
            throw new RuntimeException("Problem reading field: "+field.getName(), e);
        }

        //Class<? extends PropertyConverter> converterClass = persistentProperty.converter();
        Map<String, PropertyConverter> converters = IntactContext.getCurrentInstance().getSpringContext().getBeansOfType(PropertyConverter.class);

        PropertyConverter converter = null;

        for (PropertyConverter converterCandidate : converters.values()) {
            if (converterCandidate.getObjectType().isAssignableFrom(objValue.getClass())) {
                converter = converterCandidate;
                break;
            }
        }

        String value = converter.convertToString(objValue);

        if (value == null) {
            value = defaultValue;
        }
        return value;
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
