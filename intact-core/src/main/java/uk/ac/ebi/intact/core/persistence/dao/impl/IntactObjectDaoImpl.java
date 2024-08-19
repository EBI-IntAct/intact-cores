/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.hibernate.HibernateException;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.core.persistence.util.CgLibUtil;
import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.core.persistence.dao.IntactObjectDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Basic queries for IntactObjects
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
@Transactional(readOnly = true)
@SuppressWarnings( {"unchecked"} )
public class IntactObjectDaoImpl<T extends IntactObject> extends HibernateBaseDaoImpl<T> implements IntactObjectDao<T> {

    public IntactObjectDaoImpl( Class<T> entityClass ) {
        super( entityClass );
    }

    public IntactObjectDaoImpl( Class<T> entityClass, EntityManager entityManager ) {
        super( entityClass, entityManager );
    }

    public IntactObjectDaoImpl( Class<T> entityClass, EntityManager entityManager, IntactSession intactSession ) {
        super( entityClass, entityManager, intactSession );
    }

    /**
     * Get an item using its AC
     *
     * @param ac the identifier
     *
     * @return the object
     */
    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public T getByAc( String ac ) {
        return ( T ) getSession().get( CgLibUtil.removeCglibEnhanced( getEntityClass() ), ac );
    }

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public Collection<T> getByAcLike( String ac ) {
        return getByPropertyNameLike( "ac", ac );
    }

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public Collection<T> getByAcLike( String ac, boolean ignoreCase ) {
        return getByPropertyNameLike( "ac", ac, ignoreCase );
    }

    /**
     * Performs a unique query for an array of ACs. Beware that depending on the database used this query has limitation
     * (for instance, in Oracle it is limited to 1000 items)
     *
     * @param acs The acs to look for
     *
     * @return the collection of entities with those ACs
     */
    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public List<T> getByAc( String[] acs ) {
        if ( acs.length == 0 ) {
            throw new HibernateException( "At least one AC is needed to query by AC." );
        }

        return getSession().createCriteria( getEntityClass() )
                .add( Restrictions.in( "ac", acs ) )
                .addOrder( Order.asc( "ac" ) ).list();
    }

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public List<T> getByAc( Collection<String> acs ) {
        return getByAc( acs.toArray( new String[acs.size()] ) );
    }

    /**
     * @deprecated use getAllIterator() instead. Method might be removed in version 1.6
     */
    @Deprecated
    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public Iterator<T> iterator() {
        return getAllIterator();
    }

    /**
     * @deprecated use getAllIterator() instead. Method might be removed in version 1.6
     */
    @Deprecated
    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public Iterator<T> iterator( int batchSize ) {
        return getAllIterator();
    }

    @Transactional(readOnly = true)
    @Override
    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public List<T> getAll( int firstResult, int maxResults ) {
        return getEntityManager().createQuery("select o from "+getEntityClass().getName()+" o order by o.ac")
                .setFirstResult( firstResult )
                .setMaxResults( maxResults ).getResultList();
    }

    @Override
    public Object executeDetachedCriteria( DetachedCriteria crit, int firstResult, int maxResults ) {
        return crit.getExecutableCriteria( getSession() ).addOrder(Order.asc("ac"))
                .setFirstResult( firstResult )
                .setMaxResults( maxResults )
                .list();
    }

    public int deleteByAc( String ac ) {

        T o = getByAc( ac );
        if ( o == null ) {
            return 0;
        }
        delete( o );
        return 1;

        // this doesn't work for annoated object or dependencies.
        // to get thit to work for annotatedObject, we need to overload the method in AnnotatedObjectDaoImpl and
        // delete manually xrefs and aliases before to call super.deleteByAc().
//        Query deleteQuery = getSession().createQuery( "delete " + getEntityClass().getName() + " item where item.ac = :ac" );
//        deleteQuery.setParameter( "ac", ac );
//        return deleteQuery.executeUpdate();
    }

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public boolean exists( T obj ) {
        return ( getSession().get( getEntityClass(), obj.getAc() ) != null );
    }
}
