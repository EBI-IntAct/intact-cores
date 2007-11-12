/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.ejb.HibernateEntityManager;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactEnvironment;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.RuntimeConfig;
import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.model.NotAnEntityException;
import uk.ac.ebi.intact.persistence.dao.BaseDao;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
public abstract class HibernateBaseDaoImpl<T> implements BaseDao<T> {

    public static final Log log = LogFactory.getLog( HibernateBaseDaoImpl.class );

    private Class<T> entityClass;
    private EntityManager entityManager;
    private IntactSession intactSession;

    public HibernateBaseDaoImpl( Class<T> entityClass, EntityManager entityManager, IntactSession intactSession ) {
        this.entityClass = entityClass;
        this.entityManager = entityManager;
        this.intactSession = intactSession;
    }

    public Session getSession() {
        Session session = ((HibernateEntityManager)getEntityManager()).getSession();
        return session;

        /*
        RuntimeConfig config = RuntimeConfig.getCurrentInstance( intactSession );
        DaoFactory daoFactory = DaoFactory.getCurrentInstance( intactSession, config.getDefaultDataConfig() );

        if ( !daoFactory.isTransactionActive() ) {
            if ( config.isAutoBeginTransaction() ) {
                log.debug( "Auto starting transaction" );
                daoFactory.beginTransaction(); // starts or uses an existing transaction
            } else {
                throw new AutoBeginTransactionException( "You must begin a transaction manually." );
            }
        }

        // invoking the method from the session factory because if the session is closed it will automatically
        // open one
        return session.getSessionFactory().getCurrentSession();
        */
    }

    public EntityManager getEntityManager() {
        if (entityManager != null && !entityManager.isOpen()) {
            entityManager = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getEntityManager();
        }
        return entityManager;
    }

    @Deprecated
    public void flushCurrentSession() {
        flushEntityManager();
    }

    public void flushEntityManager() {
        entityManager.flush();
    }

    protected IntactSession getIntactSession() {
        return intactSession;
    }

    /**
     * Provides the database name that is being connected to.
     *
     * @return String the database name, or an empty String if the query fails
     */
    public String getDbName() throws SQLException {
        String url = getSession().connection().getMetaData().getURL();

        if ( url.contains( ":" ) ) {
            url = url.substring( url.lastIndexOf( ":" ) + 1, url.length() );
        }
        getSession().connection().close();
        return url;
    }

    public List<T> getAll() {
        return getSession().createCriteria( getEntityClass() ).list();
    }

    public Iterator<T> getAllIterator() {
        return getSession().createQuery("from "+getEntityClass().getSimpleName()).iterate();
    }

    public List<T> getAll( int firstResult, int maxResults ) {
        return getSession().createCriteria( getEntityClass() )
                .setFirstResult( firstResult )
                .setMaxResults( maxResults ).list();
    }

    public int countAll() {
        return ( Integer ) getSession()
                .createCriteria( getEntityClass() )
                .setProjection( Projections.rowCount() )
                .uniqueResult();
    }

    /**
     * Provides the user name that is connecting to the DB.
     *
     * @return String the user name
     *
     * @throws SQLException thrown if the metatdata can't be obtained
     */
    public String getDbUserName() throws SQLException {
        final String name = getSession().connection().getMetaData().getUserName();
        getSession().connection().close();
        return name;
    }

    public void update( T objToUpdate ) {
        checkReadOnly();

        getSession().update( objToUpdate );
    }

    public void persist( T objToPersist ) {
        checkReadOnly();

        getEntityManager().persist( objToPersist );
    }

    public void persistAll( Collection<T> objsToPersist ) {
        checkReadOnly();

        for ( T objToPersist : objsToPersist ) {
            persist( objToPersist );
        }
    }

    public void delete( T objToDelete ) {
        checkReadOnly();

        getSession().delete( objToDelete );
    }

    public void deleteAll( Collection<T> objsToDelete ) {
        checkReadOnly();

        for ( T objToDelete : objsToDelete ) {
            delete( objToDelete );
        }
    }

    public void saveOrUpdate( T objToPersist ) {
        checkReadOnly();

        getSession().saveOrUpdate( objToPersist );
    }

    public void refresh( T objToRefresh ) {
        getSession().refresh( objToRefresh );
    }

    public void evict(T objToEvict) {
        getSession().evict(objToEvict);
    }

    public void replicate(T objToReplicate) {
        replicate(objToReplicate, true);
    }

    public void replicate(T objToReplicate, boolean ignoreIfExisting) {
        ReplicationMode replicationMode;

        if (ignoreIfExisting) {
            replicationMode = ReplicationMode.IGNORE;
        } else {
            replicationMode = ReplicationMode.LATEST_VERSION;
        }
        getSession().replicate(objToReplicate, replicationMode);
    }

    public void merge(T objToMerge) {
        getSession().merge(objToMerge);
    }

    /**
     * Checks if the class passed as an argument has the annotation <code>@javax.persistence.Entity</code>.
     * If not, this methods throws a <code>NotAnEntityException</code>
     *
     * @param entity The entity to validate
     */
    public static void validateEntity( Class<? extends IntactObject> entity ) {
        if ( entity.getAnnotation( Entity.class ) == null ) {
            throw new NotAnEntityException( entity );
        }
    }

    protected T getByPropertyName( String propertyName, String value ) {
        return getByPropertyName( propertyName, value, true );
    }

    protected T getByPropertyName( String propertyName, String value, boolean ignoreCase ) {
        Query query = getQueryByPropertyName(propertyName, value, ignoreCase);

        List<T> results = query.getResultList();

        if (results.isEmpty()) return null;

        return results.iterator().next();
    }

    public Collection<T> getColByPropertyName( String propertyName, String value ) {
        return getColByPropertyName( propertyName, value, true );
    }

    protected Collection<T> getColByPropertyName( String propertyName, String value, boolean ignoreCase ) {
        if ( value.startsWith( "%" ) || value.endsWith( "%" ) ) {
            return getByPropertyNameLike( propertyName, value, ignoreCase, -1, -1 );
        }

        return getQueryByPropertyName( propertyName, value, ignoreCase ).getResultList();
    }

    private Query getQueryByPropertyName(String propertyName, String value, boolean ignoreCase) {
        Query query;

        if (ignoreCase) {
            query = getEntityManager().createQuery("from "+getEntityClass().getSimpleName()+" where lower("+propertyName+") = lower(:propValue)");
        } else {
            query = getEntityManager().createQuery("from "+getEntityClass().getSimpleName()+" where "+propertyName+" = :propValue");
        }

        query.setParameter("propValue", value);
        return query;
    }

    protected Collection<T> getByPropertyNameLike( String propertyName, String value ) {
        return getByPropertyNameLike( propertyName, value, true, -1, -1 );
    }

    protected Collection<T> getByPropertyNameLike( String propertyName, String value, boolean ignoreCase ) {
        return getByPropertyNameLike( propertyName, value, ignoreCase, -1, -1 );
    }

    protected Collection<T> getByPropertyNameLike( String propertyName, String value, boolean ignoreCase, int firstResult, int maxResults ) {
        return getByPropertyNameLike( propertyName, value, ignoreCase, firstResult, maxResults, false );
    }

    protected Collection<T> getByPropertyNameLike( String propertyName, String value, boolean ignoreCase, int firstResult, int maxResults, boolean orderAsc ) {
        Criteria criteria = getSession().createCriteria( entityClass );

        SimpleExpression rest = Restrictions.like( propertyName, value );

        if ( ignoreCase ) {
            rest.ignoreCase();
        }

        criteria.add( rest );

        if ( firstResult >= 0 ) {
            criteria.setFirstResult( firstResult );
        }

        if ( maxResults > 0 ) {
            criteria.setMaxResults( maxResults );
        }

        if ( orderAsc ) {
            criteria.addOrder( Order.asc( propertyName ) );
        }

        return criteria.list();
    }

    protected Disjunction disjunctionForArray( String propertyName, String[] values ) {
        return disjunctionForArray( propertyName, values, false );
    }

    protected Disjunction disjunctionForArray( String propertyName, String[] values, boolean ignoreCase ) {
        Disjunction disj = Restrictions.disjunction();

        for ( String value : values ) {
            SimpleExpression res;

            if ( value.contains( "%" ) ) {
                res = Restrictions.like( propertyName, value );
            } else {
                res = Restrictions.eq( propertyName, value );
            }

            if ( ignoreCase ) {
                res.ignoreCase();
            }

            disj.add( res );
        }

        return disj;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public Object executeDetachedCriteria( DetachedCriteria crit ) {
        return crit.getExecutableCriteria( getSession() ).list();
    }

    public Object executeDetachedCriteria( DetachedCriteria crit, int firstResult, int maxResults ) {
        return crit.getExecutableCriteria( getSession() )
                .setFirstResult( firstResult )
                .setMaxResults( maxResults )
                .list();
    }

    protected void checkReadOnly() {
        boolean readOnly = RuntimeConfig.getCurrentInstance( intactSession ).isReadOnlyApp();

        if ( readOnly ) {
            throw new IntactException( "This application is running on mode READ-ONLY, so it cannot persist or update " +
                                       "objects in the database. Set the init-param " + IntactEnvironment.READ_ONLY_APP + " to false if you want to " +
                                       "do that." );
        }
    }

    protected T uniqueResult(Query query) {
        List results = query.getResultList();

        if (results.isEmpty()) {
            return null;
        } else if (results.size() > 1) {
            throw new IntactException("Query returned more than one result");
        }

        return (T) results.iterator().next();
    }

}
