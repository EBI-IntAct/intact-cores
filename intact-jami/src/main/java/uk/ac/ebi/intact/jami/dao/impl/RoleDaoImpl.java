package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.stereotype.Repository;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.RoleDao;
import uk.ac.ebi.intact.jami.model.extension.IntactRange;
import uk.ac.ebi.intact.jami.model.user.Preference;
import uk.ac.ebi.intact.jami.model.user.Role;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.impl.RoleSynchronizer;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of role dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */
public class RoleDaoImpl extends AbstractIntactBaseDao<Role, Role> implements RoleDao {

    public RoleDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super(Role.class, entityManager, context);
    }

    public Role getByName(String name) {
        Query query = getEntityManager().createQuery("select r from Role r " +
                "where r.name = :name ");
        query.setParameter("name",name);
        Collection<Role> roles = query.getResultList();
        if (roles.isEmpty()){
            return null;
        }
        else if (roles.size() == 1){
            return roles.iterator().next();
        }
        else{
            throw new NonUniqueResultException("We found "+roles.size()+" user roles matching name "+name);
        }
    }

    @Override
    public IntactDbSynchronizer<Role, Role> getDbSynchronizer() {
        return getSynchronizerContext().getRoleSynchronizer();
    }
}