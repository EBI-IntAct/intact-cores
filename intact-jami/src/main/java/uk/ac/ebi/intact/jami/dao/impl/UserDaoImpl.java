package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.stereotype.Repository;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.UserDao;
import uk.ac.ebi.intact.jami.model.extension.IntactSource;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.impl.UserSynchronizer;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of user dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */
public class UserDaoImpl extends AbstractIntactBaseDao<User, User> implements UserDao {

    public UserDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super(User.class, entityManager, context);
    }

    @Override
    public User getByAc(String ac) {
        return getEntityManager().find(User.class, ac);
    }

    public User getByLogin(String login) {
        Query query = getEntityManager().createQuery("select u from User u " +
                "where u.login = :login ");
        query.setParameter("login",login);
        Collection<User> users = query.getResultList();
        if (users.isEmpty()){
            return null;
        }
        else if (users.size() == 1){
            return users.iterator().next();
        }
        else{
            throw new NonUniqueResultException("We found "+users.size()+" users matching login "+login);
        }
    }

    public Collection<User> getByFirstName(String name) {
        Query query = getEntityManager().createQuery("select u from User u " +
                "where u.firstName = :name ");
        query.setParameter("name",name);
        return query.getResultList();
    }

    public Collection<User> getByLastName(String name) {
        Query query = getEntityManager().createQuery("select u from User u " +
                "where u.lastName = :name ");
        query.setParameter("name",name);
        return query.getResultList();
    }

    public User getByEMail(String mail) {
        Query query = getEntityManager().createQuery("select u from User u " +
                "where u.email = :mail ");
        query.setParameter("mail",mail);
        Collection<User> users = query.getResultList();
        if (users.isEmpty()){
            return null;
        }
        else if (users.size() == 1){
            return users.iterator().next();
        }
        else{
            throw new NonUniqueResultException("We found "+users.size()+" users matching e-mail "+mail);
        }
    }

    public Collection<User> getByDisabled(boolean disabled) {
        Query query = getEntityManager().createQuery("select u from User u " +
                "where u.disabled = :dis ");
        query.setParameter("dis",disabled);
        return query.getResultList();
    }

    public Collection<User> getByRole(String role) {
        Query query = getEntityManager().createQuery("select distinct u from User u " +
                "join u.roles as r " +
                "where r.name = :name ");
        query.setParameter("name",role);
        return query.getResultList();
    }

    public Collection<User> getByPreference(String key, String value) {
        Query query = getEntityManager().createQuery("select distinct u from User u " +
                "join u.preferences as p " +
                "where p.key = :k and p.value "+(value != null ? "= :val " : "is null"));
        query.setParameter("k",key);
        if (value != null){
            query.setParameter("val",value);
        }
        return query.getResultList();
    }

    @Override
    public IntactDbSynchronizer<User, User> getDbSynchronizer() {
        return getSynchronizerContext().getUserSynchronizer();
    }
}