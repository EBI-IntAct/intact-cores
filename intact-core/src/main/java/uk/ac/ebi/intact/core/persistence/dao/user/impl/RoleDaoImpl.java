package uk.ac.ebi.intact.core.persistence.dao.user.impl;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.core.persistence.dao.impl.IntactObjectDaoImpl;
import uk.ac.ebi.intact.core.persistence.dao.user.RoleDao;
import uk.ac.ebi.intact.model.user.Role;

import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.util.Collection;
import java.util.List;

/**
 * Role DAO.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
@Repository
@SuppressWarnings( {"unchecked"} )
public class RoleDaoImpl extends IntactObjectDaoImpl<Role> implements RoleDao {

    public RoleDaoImpl() {
        super( Role.class );
    }

    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public Role getRoleByName( String name ) {
        if ( name == null ) {
            throw new IllegalArgumentException( "You must give a non null name" );
        }
        final Query query = getEntityManager().createQuery( "select r from Role r where upper(r.name) = :name" );
        query.setParameter( "name", name.toUpperCase() );
        List<Role> roles = query.getResultList();
        if ( roles.isEmpty() ) {
            return null;
        }
        return roles.get( 0 );
    }

    @Override
    @Retryable(
            include = PersistenceException.class,
            maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}", multiplierExpression = "${retry.multiplier}"))
    public Collection<Role> getByUserAc(String ac) {
        Query query = getEntityManager().createQuery("select r from Role r join r.users as user where user.ac = :ac");
        query.setParameter("ac", ac);

        return query.getResultList();
    }
}
