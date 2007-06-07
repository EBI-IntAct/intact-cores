/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

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

    List<T> getByAnnotationTopicAndDescription( CvTopic topic, String description );

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
}
