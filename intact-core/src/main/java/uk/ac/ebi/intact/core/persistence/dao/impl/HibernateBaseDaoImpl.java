/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.jpa.HibernateEntityManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.IntactException;
import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.core.persistence.dao.BaseDao;
import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.model.NotAnEntityException;

import javax.persistence.*;
import java.sql.Connection;
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
@Transactional(readOnly = true)
public abstract class HibernateBaseDaoImpl<T> implements BaseDao<T> {

    public static final Log log = LogFactory.getLog(HibernateBaseDaoImpl.class);

    private Class<T> entityClass;
    private IntactSession intactSession;

    @PersistenceContext(unitName = "intact-core-default")
    private EntityManager entityManager;

    @PersistenceUnit(unitName = "intact-core-default")
    private EntityManagerFactory entityManagerFactory;

    public HibernateBaseDaoImpl() {
    }

    public HibernateBaseDaoImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public HibernateBaseDaoImpl(Class<T> entityClass, EntityManager entityManager) {
        this.entityClass = entityClass;
        //this.entityManager = entityManager;
    }

    public HibernateBaseDaoImpl(Class<T> entityClass, EntityManager entityManager, IntactSession intactSession) {
        this(entityClass, entityManager);
        this.intactSession = intactSession;
    }

    public Session getSession() {
        return ((HibernateEntityManager) getEntityManager()).getSession();
    }

    public EntityManager getEntityManager() {
        if (entityManager != null && !entityManager.isOpen()) {
            entityManager = entityManagerFactory.createEntityManager();
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

    /**
     * Provides the database name that is being connected to.
     *
     * @return String the database name, or an empty String if the query fails
     */
    @Transactional(readOnly = true)
    public String getDbName() throws SQLException {
        String url = null;
        HibernateEntityManager hFactory = (HibernateEntityManager) getEntityManager();
        if (hFactory.getSession() != null) {
            SessionImplementor settings = (SessionImplementor) hFactory.getSession();
            Connection connection = settings.connection();
            url = connection.getMetaData().getURL();
        }

        if (url.contains(":")) {
            url = url.substring(url.lastIndexOf(":") + 1, url.length());
        }
        return url;
    }

    @Transactional(readOnly = true)
    public List<T> getAll() {
        return getEntityManager().createQuery("select o from " + getEntityClass().getName() + " o").getResultList();
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public Iterator<T> getAllIterator() {
        return getSession().createQuery("from " + getEntityClass().getSimpleName()).iterate();
    }

    @Transactional(readOnly = true)
    public List<T> getAll(int firstResult, int maxResults) {
        return getEntityManager().createQuery("select o from " + getEntityClass().getName() + " o")
                .setFirstResult(firstResult)
                .setMaxResults(maxResults).getResultList();
    }

    @Transactional(readOnly = true)
    public List<T> getAllSorted(int firstResult, int maxResults, String sortProperty, boolean ascendant) {
        String strQuery = "from " + getEntityClass().getSimpleName() + " order by " + sortProperty + " " + ((ascendant) ? "asc" : "desc");
        Query query = getEntityManager().createQuery(strQuery);

        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);

        return query.getResultList();
    }

    @Transactional(readOnly = true)
    public int countAll() {
        final Long count = (Long) getSession()
                .createCriteria(getEntityClass())
                .setProjection(Projections.rowCount())
                .uniqueResult();
        return count.intValue();
    }

    /**
     * Provides the user name that is connecting to the DB.
     *
     * @return String the user name
     * @throws SQLException thrown if the metatdata can't be obtained
     */
    @Transactional(readOnly = true)
    public String getDbUserName() throws SQLException {
        String name = null;
        HibernateEntityManager hFactory = (HibernateEntityManager) getEntityManager();
        if (hFactory.getSession() != null) {
            SessionImplementor settings = (SessionImplementor) hFactory.getSession();
            Connection connection = settings.connection();
            String dbURL = connection.getMetaData().getURL();
            name = connection.getMetaData().getUserName();
        }
        return name;
    }

    @Transactional
    public void update(T objToUpdate) {
        checkReadOnly();

        getSession().update(objToUpdate);
    }

    @Transactional
    public void persist(T objToPersist) {
        checkReadOnly();

        getEntityManager().persist(objToPersist);
    }

    @Transactional
    public void persistAll(Collection<T> objsToPersist) {
        checkReadOnly();

        for (T objToPersist : objsToPersist) {
            persist(objToPersist);
        }
    }

    @Transactional
    public void delete(T objToDelete) {
        checkReadOnly();

        getSession().delete(objToDelete);
    }

    @Transactional
    public void deleteAll(Collection<T> objsToDelete) {
        checkReadOnly();

        for (T objToDelete : objsToDelete) {
            delete(objToDelete);
        }
    }

    @Transactional
    public int deleteAll() {
        // TODO fix this
        Query query = getEntityManager().createQuery("delete from " + getEntityClass());
        return query.executeUpdate();
    }

    @Transactional
    public void saveOrUpdate(T objToPersist) {
        checkReadOnly();

        getSession().saveOrUpdate(objToPersist);
    }

    public void refresh(T objToRefresh) {
        getSession().refresh(objToRefresh);
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

    @Transactional
    public void merge(T objToMerge) {
        getSession().merge(objToMerge);
    }

    /**
     * Checks if an object is transient (not contained in the current session)
     *
     * @param object the object to check
     * @return True if the object is transient - may contain uninitialized lazy collections
     * @since 1.8.0
     */
    public boolean isTransient(T object) {
        return !getSession().contains(object);
    }

    /**
     * Checks if the class passed as an argument has the annotation <code>@javax.persistence.Entity</code>.
     * If not, this methods throws a <code>NotAnEntityException</code>
     *
     * @param entity The entity to validate
     */
    public static void validateEntity(Class<? extends IntactObject> entity) {
        if (entity.getAnnotation(Entity.class) == null) {
            throw new NotAnEntityException(entity);
        }
    }

    protected T getByPropertyName(String propertyName, String value) {
        return getByPropertyName(propertyName, value, false);
    }

    protected T getByPropertyName(String propertyName, String value, boolean ignoreCase) {
        Query query = getQueryByPropertyName(propertyName, value, ignoreCase);

        List<T> results = query.getResultList();

        if (results.isEmpty()) return null;

        return results.iterator().next();
    }

    public Collection<T> getColByPropertyName(String propertyName, String value) {
        return getColByPropertyName(propertyName, value, false);
    }

    protected Collection<T> getColByPropertyName(String propertyName, String value, boolean ignoreCase) {
        if (value.startsWith("%") || value.endsWith("%")) {
            return getByPropertyNameLike(propertyName, value, ignoreCase, -1, -1);
        }

        return getQueryByPropertyName(propertyName, value, ignoreCase).getResultList();
    }

    protected Query getQueryByPropertyName(String propertyName, String value, boolean ignoreCase) {
        Query query;

        if (ignoreCase) {
            query = getEntityManager().createQuery("from " + getEntityClass().getSimpleName() + " where lower(" + propertyName + ") = lower(:propValue)");
        } else {
            query = getEntityManager().createQuery("from " + getEntityClass().getSimpleName() + " where " + propertyName + " = :propValue");
        }

        query.setParameter("propValue", value);
        return query;
    }

    protected Collection<T> getByPropertyNameLike(String propertyName, String value) {
        return getByPropertyNameLike(propertyName, value, true, -1, -1);
    }

    protected Collection<T> getByPropertyNameLike(String propertyName, String value, boolean ignoreCase) {
        return getByPropertyNameLike(propertyName, value, ignoreCase, -1, -1);
    }

    protected Collection<T> getByPropertyNameLike(String propertyName, String value, boolean ignoreCase, int firstResult, int maxResults) {
        return getByPropertyNameLike(propertyName, value, ignoreCase, firstResult, maxResults, false);
    }

    protected Collection<T> getByPropertyNameLike(String propertyName, String value, boolean ignoreCase, int firstResult, int maxResults, boolean orderAsc) {
        Criteria criteria = getSession().createCriteria(entityClass);

        SimpleExpression rest = Restrictions.like(propertyName, value);

        if (ignoreCase) {
            rest.ignoreCase();
        }

        criteria.add(rest);

        if (firstResult >= 0) {
            criteria.setFirstResult(firstResult);
        }

        if (maxResults > 0) {
            criteria.setMaxResults(maxResults);
        }

        if (orderAsc) {
            criteria.addOrder(Order.asc(propertyName));
        } else {
            criteria.addOrder(Order.desc(propertyName));
        }

        return criteria.list();
    }

    protected Disjunction disjunctionForArray(String propertyName, String[] values) {
        return disjunctionForArray(propertyName, values, false);
    }

    protected Disjunction disjunctionForArray(String propertyName, String[] values, boolean ignoreCase) {
        Disjunction disj = Restrictions.disjunction();

        for (String value : values) {
            SimpleExpression res;

            if (value.contains("%")) {
                res = Restrictions.like(propertyName, value);
            } else {
                res = Restrictions.eq(propertyName, value);
            }

            if (ignoreCase) {
                res.ignoreCase();
            }

            disj.add(res);
        }

        return disj;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public Object executeDetachedCriteria(DetachedCriteria crit) {
        return crit.getExecutableCriteria(getSession()).list();
    }

    public Object executeDetachedCriteria(DetachedCriteria crit, int firstResult, int maxResults) {
        return crit.getExecutableCriteria(getSession())
                .setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .list();
    }

    protected void checkReadOnly() {
//        if (intactSession != null) {
//            boolean readOnly = config.isReadOnlyApp();
//
//            if ( readOnly ) {
//                throw new IntactException( "This application is running on mode READ-ONLY, so it cannot persist or update " +
//                                           "objects in the database. Set the init-param " + IntactEnvironment.READ_ONLY_APP + " to false if you want to " +
//                                           "do that." );
//            }
//        }
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

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
}
