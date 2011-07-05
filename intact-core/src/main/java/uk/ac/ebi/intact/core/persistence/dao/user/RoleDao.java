package uk.ac.ebi.intact.core.persistence.dao.user;

import uk.ac.ebi.intact.model.user.Role;

import java.io.Serializable;

/**
 * Role DAO.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public interface RoleDao extends UsersBaseDao<Role>, Serializable {

    Role getRoleByName( String name );

}
