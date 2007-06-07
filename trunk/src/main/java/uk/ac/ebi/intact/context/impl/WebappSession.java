/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.context.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactSession;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Webapp session, that uses the session and the request to store attributes
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/08/2006</pre>
 */
public class WebappSession extends IntactSession {

    private static final Log log = LogFactory.getLog( WebappSession.class );

    private HttpSession session;
    private ServletContext servletContext;
    private HttpServletRequest request;
    private Map<String, String> overrideInitParamMap;

    public WebappSession( ServletContext servletContext, HttpSession session, HttpServletRequest request ) {
        this.session = session;
        this.servletContext = servletContext;
        this.request = request;
        overrideInitParamMap = new HashMap<String, String>();

        try {
            readDefaultProperties();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public WebappSession( ServletContext servletContext, HttpSession session, HttpServletRequest request, Properties properties ) {
        this( servletContext, session, request );

        Enumeration<String> propNames = ( Enumeration<String> ) properties.propertyNames();

        while ( propNames.hasMoreElements() ) {
            String propName = propNames.nextElement();
            setInitParam( propName, properties.getProperty( propName ) );
        }
    }

    public Object getApplicationAttribute( String name ) {
        return servletContext.getAttribute( name );
    }

    public void setApplicationAttribute( String name, Object attribute ) {
        if ( log.isDebugEnabled() ) {
            log.debug( "APP: " + name + "=" + attribute);
        }
        servletContext.setAttribute( name, attribute );
    }

    public Serializable getAttribute( String name ) {
        return ( Serializable ) session.getAttribute( name );
    }

    public void setAttribute( String name, Serializable attribute ) {
        if ( log.isDebugEnabled() ) {
            log.debug( "SES: " + name + "=" + attribute +" (sessionid="+session.getId()+")");
        }
        session.setAttribute( name, attribute );
    }

    public Object getRequestAttribute( String name ) {
        if (log.isDebugEnabled())
        {
            log.debug("REQ - Get '"+name+"'");
        }
        return request.getAttribute( name );
    }

    public void setRequestAttribute( String name, Object value ) {
        if ( log.isDebugEnabled() ) {
            log.debug( "REQ - Set " + name + "=" + value);
        }
        request.setAttribute( name, value );
    }

    public boolean containsInitParam( String name ) {
        boolean containsParam = overrideInitParamMap.containsKey( name );

        return containsParam || ( servletContext.getInitParameter( name ) != null );

    }

    public String getInitParam( String name ) {
        if ( overrideInitParamMap.containsKey( name ) ) {
            return overrideInitParamMap.get( name );
        }

        return servletContext.getInitParameter( name );
    }

    public void setInitParam( String name, String value ) {
        if ( log.isDebugEnabled() ) {
            String webParam = servletContext.getInitParameter( name );
            if ( webParam != null ) {
                log.debug( "Param in web.xml is being overriden by the same param in intact.properties: " + webParam );
            }
        }
        overrideInitParamMap.put( name, value );
    }

    public Collection<String> getInitParamNames() {
        List<String> initParams = new ArrayList<String>( overrideInitParamMap.keySet() );
        initParams.addAll( enumerationToList( servletContext.getInitParameterNames() ) );

        return initParams;
    }

    public Collection<String> getAttributeNames() {
        return enumerationToList( session.getAttributeNames() );
    }

    public Collection<String> getApplicationAttributeNames() {
        return enumerationToList( servletContext.getAttributeNames() );
    }

    public Collection<String> getRequestAttributeNames() {
        if ( !isRequestAvailable() ) {
            return Collections.EMPTY_SET;
        }

        return enumerationToList( request.getAttributeNames() );
    }

    public boolean isWebapp() {
        return true;
    }

    public boolean isRequestAvailable() {
        return request != null;
    }

    private static List<String> enumerationToList( Enumeration<String> enumeration ) {
        List<String> list = new ArrayList<String>();

        while ( enumeration.hasMoreElements() ) {
            list.add( enumeration.nextElement() );

        }

        return list;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpSession getSession() {
        return session;
    }
}
