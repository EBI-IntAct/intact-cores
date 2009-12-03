/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.ejb.HibernateQuery;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.core.persistence.dao.AnnotatedObjectDao;
import uk.ac.ebi.intact.model.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
@Transactional(readOnly = true)
@SuppressWarnings( {"unchecked"} )
public abstract class AnnotatedObjectDaoImpl<T extends AnnotatedObject> extends IntactObjectDaoImpl<T> implements AnnotatedObjectDao<T> {

    private static final Log log = LogFactory.getLog( AnnotatedObjectDaoImpl.class );

    public AnnotatedObjectDaoImpl( Class<T> entityClass ) {
        super( entityClass );
    }

    public AnnotatedObjectDaoImpl( Class<T> entityClass, EntityManager entityManager ) {
        super( entityClass, entityManager );
    }

    public AnnotatedObjectDaoImpl( Class<T> entityClass, EntityManager entityManager, IntactSession intactSession ) {
        super( entityClass, entityManager, intactSession );
    }

    public T getByAc(String ac, boolean prefetchXrefs) {
        Criteria criteria = getSession().createCriteria(getEntityClass())
                .add(Restrictions.eq("ac", ac));

        if (prefetchXrefs) {
            criteria.setFetchMode("xrefs", FetchMode.JOIN);
        }

        return (T) criteria.uniqueResult();
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

    @Transactional(propagation = Propagation.MANDATORY)
    public Iterator<T> getByShortLabelLikeIterator( String value, boolean ignoreCase ) {
        Query query;

        if (ignoreCase) {
           query = getEntityManager().createQuery("from "+getEntityClass().getSimpleName()+" where lower(shortlabel) = lower(:label)");
        } else {
           query = getEntityManager().createQuery("from "+getEntityClass().getSimpleName()+" where shortlabel = :label");
        }

        query.setParameter("label", value);

        return  ((HibernateQuery)query).getHibernateQuery().iterate();
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

    public List<T> getByXrefLike( String databaseMi, String qualifierMi, String primaryId ) {
        return getSession().createCriteria( getEntityClass() )
                .createAlias( "xrefs", "xref" )
                .createAlias( "xref.cvXrefQualifier", "qual" )
                .createAlias( "xref.cvDatabase", "database" )
                .createCriteria( "qual.xrefs", "qualXref" )
                .createCriteria( "database.xrefs", "dbXref" )
                .add( Restrictions.eq( "qualXref.primaryId", qualifierMi ) )
                .add( Restrictions.eq( "dbXref.primaryId", databaseMi ) )
                .add( Restrictions.eq( "xref.primaryId", primaryId ) ).list();
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
     * @inheritDoc
     */
    public List<T> getByAnnotationTopicAndDescription( CvTopic topic, String description ) {
        return getSession().createCriteria( getEntityClass() ).createAlias( "annotations", "annot" )
                .add( Restrictions.eq( "annot.cvTopic", topic ) )
                .add( Restrictions.eq( "annot.annotationText", description ) ).list();
    }

    /**
     * @inheritDoc
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
            
        } else if ( !excludeObsolete && excludeHidden ) {
            crit.add( Restrictions.eq( "annotTopic.shortLabel", CvTopic.HIDDEN ) );
            subList = crit.list();
        }

        listTotal.removeAll( subList );
        return listTotal;
    }

    /**
     * @inheritDoc
     */
    public List<T> getByShortlabelOrAcLike( String searchString ) {
        return getSession().createCriteria( getEntityClass() ).addOrder( Order.asc( "shortLabel" ) )
                .add( Restrictions.or(
                        Restrictions.like( "ac", searchString ).ignoreCase(),
                        Restrictions.like( "shortLabel", searchString ).ignoreCase() ) ).list();
    }

    /**
     * @inheritDoc
     */
    public List<String> getShortLabelsLike(String labelLike) {
        return getSession().createCriteria(getEntityClass())
                .add(Restrictions.like("shortLabel", labelLike))
                .setProjection(Projections.property("shortLabel")).list();
    }

    /**
     * @inheritDoc
     */
    public List<Publication> getByLastImexUpdate(Date fromDate, Date toDate) {
        Query query = getEntityManager().createQuery("select p from Publication p join p.annotations as annotation " +
                "where annotation.cvTopic.shortLabel = :lastImexUpdateLabel and annotation.updated >= :fromDate and annotation.updated <= :toDate");
        query.setParameter("lastImexUpdateLabel", CvTopic.LAST_IMEX_UPDATE);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);

        return query.getResultList();
    }
}
