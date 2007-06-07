/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvTopic;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Uses the current Database connexion to create a list of Annotations' Topic not to be displayed in the public
 * interface. Those CvTopics are annotated with the term 'no-export'.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30-Jun-2005</pre>
 */
public class AnnotationFilter {

    /**
     * Sets up a logger for that class.
     */
    public static final Log logger = LogFactory.getLog( AnnotationFilter.class );

    /**
     * Keeps all CvTopics to be filtered out.
     */
    private Set<CvTopic> filteredTopics = null;

    private static AnnotationFilter ourInstance;

    public static AnnotationFilter getInstance() {
        if ( ourInstance == null ) {
            ourInstance = new AnnotationFilter();
        }
        return ourInstance;
    }

    /**
     * Loads all CvTopics that should not be shown on the public interface.
     */
    private AnnotationFilter() {

        logger.debug( "Initializing which CvTopic should be filtered out." );

        // search for the CvTopic no-export
        CvTopic noExport = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getAnnotatedObjectDao( CvTopic.class ).getByShortLabel( CvTopic.NO_EXPORT );

        if ( noExport != null ) {

            // load all CvTopics
            Collection<CvTopic> cvTopics = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getCvObjectDao( CvTopic.class ).getAll();

            // select those that have an Annotation( no-export )
            for ( CvTopic cvTopic : cvTopics ) {
                for ( Annotation annotation : cvTopic.getAnnotations() ) {
                    if ( noExport.equals( annotation.getCvTopic() ) ) {
                        if ( filteredTopics == null ) {
                            filteredTopics = new HashSet<CvTopic>( 8 );
                        }

                        logger.debug( "CvTopic( " + cvTopic.getShortLabel() + " )" );
                        filteredTopics.add( cvTopic );
                    }
                }
            }
        }

        if ( filteredTopics == null ) {
            filteredTopics = Collections.EMPTY_SET;
        }

        logger.debug( filteredTopics.size() + " CvTopic" + ( filteredTopics.size() > 1 ? "s" : "" ) + " filtered." );

    }

    /**
     * Checks if the given CvTopic is not to be exported.
     *
     * @param topic the CvTopic we want to know if we have to display.
     *
     * @return true if the given CvTopic isn't supposed to be shown on a public view, otherwise false.
     */
    public boolean isFilteredOut( CvTopic topic ) {

        return filteredTopics.contains( topic );
    }

    /**
     * Checks if the CvTopic of the given annotation is not to be exported.
     *
     * @param annotation the annotation we want to know if we have to display.
     *
     * @return true if an Annotation isn't supposed to be shown on a public view, otherwise false.
     */
    public boolean isFilteredOut( Annotation annotation ) {

        if ( annotation != null ) {

            // an annotation must have a CvTopic (non null)
            if ( filteredTopics.contains( annotation.getCvTopic() ) ) {
                return true;
            }
        }

        // no annotation available
        return false;
    }

    public Set getFilters() {
        Set result = new HashSet( filteredTopics.size() );

        for ( CvTopic cvTopic : filteredTopics ) {
            result.add( cvTopic );
        }

        return result;
    }
}
