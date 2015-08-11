/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.webapp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSession;

/**
 * TODO: comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/08/2006</pre>
 */
public class BasicStartupIntactListener {

    private static final Log log = LogFactory.getLog( BasicStartupIntactListener.class );

    public void contextInitialized( ServletContextEvent servletContextEvent ) {
    }

    public void sessionCreated( HttpSessionEvent httpSessionEvent ) {
        HttpSession session = httpSessionEvent.getSession();

        if (log.isDebugEnabled()) {
            log.debug( "Session started: " + session.getId() );
        }

    }

    public void contextDestroyed( ServletContextEvent servletContextEvent ) {
        if (log.isDebugEnabled()) log.debug( "LogFactory.release and destroying application" );
        LogFactory.release( Thread.currentThread().getContextClassLoader() );
    }

    public void sessionDestroyed( HttpSessionEvent httpSessionEvent ) {
        if (log.isDebugEnabled()) log.debug( "Session destroyed: " + httpSessionEvent.getSession().getId() );
    }
}