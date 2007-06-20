/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.CvXrefQualifier;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-May-2006</pre>
 */
@Mockable
public interface AnnotatedObjectDao<T extends AnnotatedObject> extends IntactObjectDao<T> {

    T getByShortLabel( String value );

    T getByShortLabel( String value, boolean ignoreCase );

    Collection<T> getByShortLabelLike( String value );

    Collection<T> getByShortLabelLike( String value, int firstResult, int maxResults );

    Collection<T> getByShortLabelLike( String value, boolean ignoreCase );

    Collection<T> getByShortLabelLike( String value, boolean ignoreCase, int firstResult, int maxResults );

    Collection<T> getByShortLabelLike( String value, boolean ignoreCase, int firstResult, int maxResults, boolean orderAsc );

    Iterator<T> getByShortLabelLikeIterator( String value, boolean ignoreCase );

    T getByXref( String primaryId );

    List<T> getByXrefLike( String primaryId );

    List<T> getByXrefLike( CvDatabase database, String primaryId );

    List<T> getByXrefLike( CvDatabase database, CvXrefQualifier qualifier, String primaryId );

    String getPrimaryIdByAc( String ac, String cvDatabaseShortLabel );

    List<T> getByAnnotationAc( String ac );

    /**
     * Return a collection of annotated object of type <T> being annotated with an annotation having
     * a topic equal to the topic given in parameter and the description equal to the description given
     * in parameter.
     *
     * @param topic
     * @param description
     *
     * @return a list of annotated objects.
     */
    List<T> getByAnnotationTopicAndDescription( CvTopic topic, String description );

    /**
     * Gets all the cases for the current entity
     *
     * @param excludeObsolete if true exclude the obsolete CVs
     * @param excludeHidden   if true exclude the hidden CVs
     *
     * @return the list of all entities
     */
    List<T> getAll( boolean excludeObsolete, boolean excludeHidden );

    /**
     * This method will search in the database an AnnotatedObject of type T having it's shortlabel or it's
     * ac like the searchString given in argument.
     *
     * @param searchString (ex : "butkevitch-2006-%", "butkevitch-%-%", "EBI-12345%"
     *
     * @return a List of AnnotatedObject having their ac or shortlabel like the searchString
     */
    List<T> getByShortlabelOrAcLike( String searchString );

    /**
     * Gets the shortLabels like the value provided.
     *
     * @param labelLike will get labels similar to this one. Remember to provided the '%' in the open end of the value
     * to search to one side of the other (or both).
     *
     * @since 1.6
     */
    List<String> getShortLabelsLike( String labelLike );
}
