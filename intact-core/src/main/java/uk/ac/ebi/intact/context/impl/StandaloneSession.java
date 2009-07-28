/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.context.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactSession;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Standalone session, to be used outside web applications
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/08/2006</pre>
 */
public class StandaloneSession extends IntactSession {

    private static final Log log = LogFactory.getLog( StandaloneSession.class );

    private Map<String, Serializable> sessionMap;
    private Map<String, Object> applicationMap;
    private Map<String, Object> requestMap;
    private Map<String, String> initParamMap;

    public StandaloneSession() {
        this.sessionMap = new HashMap<String, Serializable>();
        this.applicationMap = new HashMap<String, Object>();
        this.requestMap = new HashMap<String, Object>();
        this.initParamMap = new HashMap<String, String>();

        try {
            readDefaultProperties();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public StandaloneSession( Properties properties ) {
        this();

        initParametersWithProperties( properties );
    }

    public Object getApplicationAttribute( String name ) {
        return applicationMap.get( name );
    }

    public void setApplicationAttribute( String name, Object attribute ) {
        applicationMap.put( name, attribute );
    }

    public Serializable getAttribute( String name ) {
        return sessionMap.get( name );
    }

    public void setAttribute( String name, Serializable attribute ) {
        sessionMap.put( name, attribute );
    }

    public Object getRequestAttribute( String name ) {
        return requestMap.get( name );
    }

    public void setRequestAttribute( String name, Object value ) {
        requestMap.put( name, value );
    }

    public boolean containsInitParam( String name ) {
        return initParamMap.containsKey( name );
    }

    public String getInitParam( String name ) {
        return initParamMap.get( name );
    }

    public void setInitParam( String name, String value ) {
        initParamMap.put( name, value );
    }

    public Collection<String> getInitParamNames() {
        return initParamMap.keySet();
    }

    public Collection<String> getAttributeNames() {
        return sessionMap.keySet();
    }

    public Collection<String> getApplicationAttributeNames() {
        return applicationMap.keySet();
    }

    public Collection<String> getRequestAttributeNames() {
        return requestMap.keySet();
    }

    public boolean isWebapp() {
        return false;
    }

    public boolean isRequestAvailable() {
        return true;
    }
}
