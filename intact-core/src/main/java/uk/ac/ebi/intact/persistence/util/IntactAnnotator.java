/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.annotation.util.AnnotationUtil;

import javax.persistence.Entity;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the hibernate annotated files from the classpath
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17-Mar-2006</pre>
 */
public class IntactAnnotator {

    public static final Log log = LogFactory.getLog( IntactAnnotator.class );

    private IntactAnnotator() {
    }

    /**
     * Retrieves a list of the annotated classes to use. This methods look for classes annotated with
     * <code>@javax.persistence.Entity</code> in the uk.ac.ebi.intact.model package
     *
     * @return The list of hibernate annotated classes
     */
    public static List<Class> getAnnotatedClasses( String packageName ) {
        String pkg = packageName;

        if ( !packageName.startsWith( "/" ) && packageName.contains( "." ) ) {
            packageName = "/" + packageName.replaceAll( "\\.", "/" );
        }

        List<Class> annotatedClasses = new ArrayList<Class>();

        // Get a File object for the package
        URL url = IntactAnnotator.class.getResource( packageName );

        // convert funny chars (%20 into spaces...)
        String strDir = null;
        try {
            strDir = URLDecoder.decode( url.getPath(), "UTF-8" );
        } catch ( UnsupportedEncodingException e ) {
            // this error should never occur, let's not add a throws in the method signature.
            throw new RuntimeException( "An error occured while decoding the URL.", e );
        }

        File directory = new File( strDir );

        if ( directory.exists() ) {
            log.debug( "Reading annotated classes from classpath dirs" );
            annotatedClasses.addAll(AnnotationUtil.getClassesWithAnnotationFromClasspathDirs(Entity.class));

        } else {
            log.info( "Directory not found: " + directory + ". Reading classes from jar" );

            // probably directory points inside a jar file, we get the jar name
            // and will look for annotated classes inside
            String jarPath = directory.toString().substring( 5, directory.toString().indexOf( ".jar" ) + 4 );

            log.info( "Searching classes in jar: " + jarPath );
            try {
                annotatedClasses.addAll( AnnotationUtil.getClassesWithAnnotationFromJar( Entity.class, jarPath ) );
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }

        return annotatedClasses;
    }


}
