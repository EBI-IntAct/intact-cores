/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.config.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactSession;

import java.io.File;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>09-Aug-2006</pre>
 */
public class CustomCoreDataConfig extends StandardCoreDataConfig {

    private static final Log log = LogFactory.getLog( CustomCoreDataConfig.class );

    private String name;
    private File configFile;

    public CustomCoreDataConfig( String name, File configFile, IntactSession session ) {
        super( session );
        this.name = name;
        this.configFile = configFile;
    }

    protected File getConfigFile() {
        return configFile;
    }

    public String getName() {
        return name;
    }
}
