package uk.ac.ebi.intact.core.persistence.dao.user;

import uk.ac.ebi.intact.core.persistence.dao.IntactObjectDao;
import uk.ac.ebi.intact.model.user.Role;

import java.io.Serializable;
import java.util.Collection;

/**
 * Role DAO.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public interface RoleDao extends IntactObjectDao<Role> {

    Role getRoleByName( String name );

    Collection<Role> getByUserAc(String ac);
}
