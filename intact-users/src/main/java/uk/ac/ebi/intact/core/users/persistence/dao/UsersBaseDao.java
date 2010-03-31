package uk.ac.ebi.intact.core.users.persistence.dao;

import org.hibernate.Session;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Basic method a DAO should be able to perform.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public interface UsersBaseDao<T> {

    int countAll();

    List<T> getAll();

    boolean isManaged( T entity );

    boolean isDetached( T entity );

    void persist( T entity );

    void delete( T entity );

    void update( T entity );

    void saveOrUpdate( T entity );

    void flush();

    EntityManager getEntityManager();

    Session getSession();
}
