/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.persistence.dao.AnnotatedObjectDao;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
@SuppressWarnings( {"unchecked"} )
public class AnnotatedObjectDaoImpl<T extends AnnotatedObject> extends IntactObjectDaoImpl<T> implements AnnotatedObjectDao<T> {

    private static final Log log = LogFactory.getLog( AnnotatedObjectDaoImpl.class );

    public AnnotatedObjectDaoImpl( Class<T> entityClass, Session session, IntactSession intactSession ) {
        super( entityClass, session, intactSession );
    }

    public T getByShortLabel( String value ) {
        return getByShortLabel( value, true );
    }

    public T getByShortLabel( String value, boolean ignoreCase ) {
        return getByPropertyName( "shortLabel", value, ignoreCase );
    }

    public Collection<T> getByShortLabelLike( String value ) {
        return getByPropertyNameLike( "shortLabel", value );
    }

    public Collection<T> getByShortLabelLike( String value, int firstResult, int maxResults ) {
        return getByPropertyNameLike( "shortLabel", value, true, firstResult, maxResults );
    }

    public Collection<T> getByShortLabelLike( String value, boolean ignoreCase ) {
        return getByPropertyNameLike( "shortLabel", value, ignoreCase, -1, -1 );
    }

    public Collection<T> getByShortLabelLike( String value, boolean ignoreCase, int firstResult, int maxResults ) {
        return getByPropertyNameLike( "shortLabel", value, ignoreCase, firstResult, maxResults );
    }

    public Collection<T> getByShortLabelLike( String value, boolean ignoreCase, int firstResult, int maxResults, boolean orderAsc ) {
        return getByPropertyNameLike( "shortLabel", value, ignoreCase, firstResult, maxResults, orderAsc );
    }

    public Iterator<T> getByShortLabelLikeIterator( String value, boolean ignoreCase ) {
        DetachedCriteria crit = DetachedCriteria.forClass( getEntityClass() )
                .add( Restrictions.like( "shortLabel", value ) );
        return new IntactObjectIterator<T>( getEntityClass(), crit );
    }

    public T getByXref( String primaryId ) {
        return ( T ) getSession().createCriteria( getEntityClass() )
                .createCriteria( "xrefs", "xref" )
                .add( Restrictions.eq( "xref.primaryId", primaryId ) ).uniqueResult();
    }

    public List<T> getByXrefLike( String primaryId ) {
        return getSession().createCriteria( getEntityClass() )
                .createCriteria( "xrefs", "xref" )
                .add( Restrictions.like( "xref.primaryId", primaryId ) ).list();
    }

    public List<T> getByXrefLike( CvDatabase database, String primaryId ) {
        return getSession().createCriteria( getEntityClass() )
                .createCriteria( "xrefs", "xref" )
                .add( Restrictions.like( "xref.primaryId", primaryId ) )
                .add( Restrictions.eq( "xref.cvDatabase", database ) ).list();
    }

    public List<T> getByXrefLike( CvDatabase database, CvXrefQualifier qualifier, String primaryId ) {
        return getSession().createCriteria( getEntityClass() )
                .createCriteria( "xrefs", "xref" )
                .add( Restrictions.like( "xref.primaryId", primaryId ) )
                .add( Restrictions.eq( "xref.cvDatabase", database ) )
                .add( Restrictions.eq( "xref.cvXrefQualifier", qualifier ) ).list();

    }

    public String getPrimaryIdByAc( String ac, String cvDatabaseShortLabel ) {
        return ( String ) getSession().createCriteria( getEntityClass() )
                .add( Restrictions.idEq( ac ) )
                .createAlias( "xrefs", "xref" )
                .createAlias( "xref.cvDatabase", "cvDatabase" )
                .add( Restrictions.like( "cvDatabase.shortLabel", cvDatabaseShortLabel ) )
                .setProjection( Property.forName( "xref.primaryId" ) ).uniqueResult();

    }

    public List<T> getByAnnotationAc( String ac ) {
        return getSession().createCriteria( getEntityClass() )
                .createAlias( "annotations", "annot" )
                .add( Restrictions.eq( "annot.ac", ac ) ).list();
    }

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
    public List<T> getByAnnotationTopicAndDescription( CvTopic topic, String description ) {
        return getSession().createCriteria( getEntityClass() ).createAlias( "annotations", "annot" )
                .add( Restrictions.eq( "annot.cvTopic", topic ) )
                .add( Restrictions.eq( "annot.annotationText", description ) ).list();
    }

    /**
     * Gets all the CVs for the current entity
     * @param excludeObsolete if true exclude the obsolete CVs
     * @param excludeHidden if true exclude the hidden CVs
     * @return the list of CVs
     */
    /**
     * Gets all the CVs for the current entity
     *
     * @param excludeObsolete if true exclude the obsolete CVs
     * @param excludeHidden   if true exclude the hidden CVs
     *
     * @return the list of CVs
     */
    public List<T> getAll( boolean excludeObsolete, boolean excludeHidden ) {

        Criteria crit = getSession().createCriteria( getEntityClass() ).addOrder( Order.asc( "shortLabel" ) );
        List<T> listTotal = crit.list();
        Collection<T> subList = Collections.EMPTY_LIST;
        if ( excludeObsolete || excludeHidden ) {
            crit.createAlias( "annotations", "annot" )
                    .createAlias( "annot.cvTopic", "annotTopic" )
                    .createAlias("annotTopic.xrefs", "topicXref")
                    .createAlias("topicXref.cvXrefQualifier", "topicXrefQual");
        }

        if ( excludeObsolete && excludeHidden ) {
            crit.add( Restrictions.or(
                                      Restrictions.and(Restrictions.eq("topicXrefQual.shortLabel", CvXrefQualifier.IDENTITY),
                                                       Restrictions.eq("topicXref.primaryId",CvTopic.OBSOLETE_MI_REF )),
                                      Restrictions.eq( "annotTopic.shortLabel", CvTopic.HIDDEN ) )
            );
            subList = crit.list();
        } else if ( excludeObsolete && !excludeHidden ) {
            crit.add( Restrictions.and(Restrictions.eq("topicXrefQual.shortLabel", CvXrefQualifier.IDENTITY),
                                       Restrictions.eq("topicXref.primaryId",CvTopic.OBSOLETE_MI_REF )) );
            subList = crit.list();
            System.out.println("subList.size() = " + subList.size());
        } else if ( !excludeObsolete && excludeHidden ) {
            crit.add( Restrictions.eq( "annotTopic.shortLabel", CvTopic.HIDDEN ) );
            subList = crit.list();
        }

        listTotal.removeAll( subList );
        return listTotal;
    }

    /**
     * This method will search in the database an AnnotatedObject of type T having it's shortlabel or it's
     * ac like the searchString given in argument.
     *
     * @param searchString (ex : "butkevitch-2006-%", "butkevitch-%-%", "EBI-12345%"
     *
     * @return a List of AnnotatedObject having their ac or shortlabel like the searchString
     */
    public List<T> getByShortlabelOrAcLike( String searchString ) {
        return getSession().createCriteria( getEntityClass() ).addOrder( Order.asc( "shortLabel" ) )
                .add( Restrictions.or(
                        Restrictions.like( "ac", searchString ).ignoreCase(),
                        Restrictions.like( "shortLabel", searchString ).ignoreCase() ) ).list();
    }
}
