package uk.ac.ebi.intact.core.users.persistence.dao;

import uk.ac.ebi.intact.core.users.model.Role;

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
