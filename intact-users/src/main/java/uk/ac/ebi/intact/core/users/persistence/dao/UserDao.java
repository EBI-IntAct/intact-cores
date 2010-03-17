package uk.ac.ebi.intact.core.users.persistence.dao;

import uk.ac.ebi.intact.core.users.model.User;

import java.io.Serializable;

/**
 * User DAO.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public interface UserDao extends UsersBaseDao<User>, Serializable {

    User getByLogin( String login );

    User getByEmail( String email );
}
