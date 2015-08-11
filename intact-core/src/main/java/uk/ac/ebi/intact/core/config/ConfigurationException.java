/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>09-Aug-2006</pre>
 */
public class ConfigurationException extends RuntimeException {

    private static final Log log = LogFactory.getLog( ConfigurationException.class );

    public ConfigurationException() {
        super();
    }

    public ConfigurationException( String message ) {
        super( message );
    }

    public ConfigurationException( String message, Throwable cause ) {
        super( message, cause );
    }

    public ConfigurationException( Throwable cause ) {
        super( cause );
    }
}
