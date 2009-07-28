/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.config.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.meta.DbInfo;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Aug-2006</pre>
 */
public class StandardCoreDataConfig extends AbstractHibernateDataConfig {

    private static final Log log = LogFactory.getLog( StandardCoreDataConfig.class );

    public static final String NAME = "uk.ac.ebi.intact.config.STANDARD_CORE";

    private boolean listenersRegistered;

    public StandardCoreDataConfig( IntactSession session ) {
        super( session );
    }

    public String getName() {
        return NAME;
    }

    protected List<String> getPackagesWithEntities() {
        List<String> packages = new ArrayList<String>( 1 );

        // /uk/ac/ebi/intact/model
        packages.add( Interactor.class.getPackage().getName() );
        packages.add( DbInfo.class.getPackage().getName() );

        return packages;
    }

    public List<String> getExcludedEntities()
    {
        return Arrays.asList("uk.ac.ebi.intact.model.SearchItem");
    }

    protected File getConfigFile() {
        URL resource = StandardCoreDataConfig.class.getResource("/hibernate.cfg.xml");

        if (resource == null) return null;

        String configFilePath = null;

        // in windows, spaces are url encoded, decode the path
        try {
            configFilePath = URLDecoder.decode(resource.getFile(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            // will never get here. I am sure that encoding exists ;-)
            e.printStackTrace();
        }

        return new File(configFilePath);
    }

}
