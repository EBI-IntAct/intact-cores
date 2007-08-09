/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.context.*;
import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.model.NotAnEntityException;
import uk.ac.ebi.intact.persistence.dao.BaseDao;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import javax.persistence.Entity;
import java.sql.SQLException;
import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
public abstract class HibernateBaseDaoImpl<T> implements BaseDao<Session> {

    public static final Log log = LogFactory.getLog( HibernateBaseDaoImpl.class );

    private Class<T> entityClass;
    private Session session;
    private IntactSession intactSession;

    public HibernateBaseDaoImpl( Class<T> entityClass, Session session, IntactSession intactSession ) {
        this.entityClass = entityClass;
        this.session = session;
        this.intactSession = intactSession;
    }

    public Session getSession() {

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
    }

    public void flushCurrentSession() {
        session.flush();
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
        String url = session.connection().getMetaData().getURL();

        if ( url.contains( ":" ) ) {
            url = url.substring( url.lastIndexOf( ":" ) + 1, url.length() );
        }
        return url;
    }

    /**
     * Provides the user name that is connecting to the DB.
     *
     * @return String the user name
     *
     * @throws SQLException thrown if the metatdata can't be obtained
     */
    public String getDbUserName() throws SQLException {
        return session.connection().getMetaData().getUserName();
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
        return ( T ) getCriteriaByPropertyName( propertyName, value, ignoreCase ).uniqueResult();
    }

    public Collection<T> getColByPropertyName( String propertyName, String value ) {
        return getColByPropertyName( propertyName, value, true );
    }

    protected Collection<T> getColByPropertyName( String propertyName, String value, boolean ignoreCase ) {
        if ( value.startsWith( "%" ) || value.endsWith( "%" ) ) {
            return getByPropertyNameLike( propertyName, value, ignoreCase, -1, -1 );
        }

        return getCriteriaByPropertyName( propertyName, value, ignoreCase ).list();
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

    private Criteria getCriteriaByPropertyName( String propertyName, String value, boolean ignoreCase ) {
        Criteria criteria = getSession().createCriteria( entityClass );
        SimpleExpression restriction = Restrictions.eq( propertyName, value );

        if ( ignoreCase ) {
            restriction.ignoreCase();
        }

        criteria.add( restriction );

        return criteria;
    }
}
