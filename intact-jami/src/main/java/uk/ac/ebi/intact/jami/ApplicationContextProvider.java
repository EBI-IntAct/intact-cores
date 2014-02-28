package uk.ac.ebi.intact.jami;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * This class will be used to provide SPRING application context
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>18/12/13</pre>
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @SuppressWarnings("static-access")
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getBean(Class<T> requiredType) {
        if (applicationContext == null){
            return null;
        }
        return applicationContext.getBean(requiredType);
    }

    public static <T> T getBean(String name, Class<T> requiredType) {
        if (applicationContext == null){
            return null;
        }
        return applicationContext.getBean(name, requiredType);
    }

    public static <T> T getBean(String name) {
        if (applicationContext == null){
            return null;
        }
        return (T)applicationContext.getBean(name);
    }
}