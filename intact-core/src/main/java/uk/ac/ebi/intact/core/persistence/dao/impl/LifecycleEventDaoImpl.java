package uk.ac.ebi.intact.core.persistence.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.context.IntactSession;
import uk.ac.ebi.intact.core.persistence.dao.LifecycleEventDao;
import uk.ac.ebi.intact.model.LifecycleEvent;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * LifecycleEvent DAO.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.5
 */
@Repository
@Transactional(readOnly = true)
public class LifecycleEventDaoImpl extends HibernateBaseDaoImpl<LifecycleEvent> implements LifecycleEventDao {

    public LifecycleEventDaoImpl() {
        super(LifecycleEvent.class);
    }

    public LifecycleEventDaoImpl( Class entityClass ) {
        super( entityClass );
    }

    public LifecycleEventDaoImpl( Class entityClass, EntityManager entityManager ) {
        super( entityClass, entityManager );
    }

    public LifecycleEventDaoImpl( Class entityClass, EntityManager entityManager, IntactSession intactSession ) {
        super( entityClass, entityManager, intactSession );
    }

    ///////////////////////
    // LifecycleEventDao

    @Override
    public List<LifecycleEvent> getByPublicationAc( String publicationAc ) {
        final Query query = getEntityManager().createQuery( "select le " +
                "from LifecycleEvent le inner join le.publication p " +
                "where p.ac = :ac" );
        query.setParameter( "ac", publicationAc );
        List<LifecycleEvent> events = query.getResultList();
        return events;
    }

    @Override
    public Object executeDetachedCriteria( DetachedCriteria crit, int firstResult, int maxResults ) {
        return crit.getExecutableCriteria( getSession() )
                .addOrder(Order.asc("pk"))
                .setFirstResult( firstResult )
                .setMaxResults( maxResults )
                .list();
    }

    @Override
    public List<LifecycleEvent> getAll( int firstResult, int maxResults ) {
        return getSession().createCriteria( getEntityClass() )
                .addOrder(Order.asc("pk"))
                .setFirstResult( firstResult )
                .setMaxResults( maxResults ).list();
    }
}
