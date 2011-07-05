package uk.ac.ebi.intact.core.persistence.dao.user.impl;

import org.hibernate.Session;
import org.hibernate.ejb.HibernateEntityManager;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.persistence.dao.user.UsersBaseDao;
import uk.ac.ebi.intact.model.user.Identifiable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Base operations for persistence of user management objects.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
@Transactional
public class UsersBaseDaoImpl<T extends Identifiable> implements UsersBaseDao<T> {

    @PersistenceContext( unitName = "intact-core-default" )
    private EntityManager entityManager;

    private Class<T> entityClass;

    public UsersBaseDaoImpl( Class<T> entityClass ) {
        this.entityClass = entityClass;
    }

    @Transactional( readOnly = true )
    public int countAll() {
        final Query query = entityManager.createQuery( "select count(*) from " + entityClass.getSimpleName() + " e" );
        return ((Long) query.getSingleResult()).intValue();
    }

    @Transactional( readOnly = true )
    public List<T> getAll() {
        return entityManager.createQuery( "select e from " + entityClass.getSimpleName() + " e" ).getResultList();
    }

    public boolean isManaged( T entity ) {
        return entityManager.contains( entity );
    }

    public boolean isDetached( T entity ) {
        return ( entity.getPk() != null && ! entityManager.contains( entity ) );
    }

    public void persist( T entity ) {
        entityManager.persist( entity );
    }

    public void delete( T entity ) {
        entityManager.remove( entity );
    }

    public void update( T entity ) {
        getSession().update( entity );
    }

    public void saveOrUpdate( T entity ) {
        getSession().saveOrUpdate( entity );
    }

    public void flush() {
        entityManager.flush();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public Session getSession() {
        return ( ( HibernateEntityManager ) entityManager ).getSession();
    }
}
