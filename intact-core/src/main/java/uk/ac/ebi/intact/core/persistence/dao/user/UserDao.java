package uk.ac.ebi.intact.core.persistence.dao.user;

import uk.ac.ebi.intact.core.persistence.dao.IntactObjectDao;
import uk.ac.ebi.intact.model.user.User;

import java.io.Serializable;
import java.util.List;

/**
 * User DAO.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public interface UserDao extends IntactObjectDao<User>, Serializable {

    User getByLogin( String login );

    User getByEmail( String email );

    List<User> getByRole( String roleName );

    List<User> getCurators();

    List<User> getReviewers();

    List<User> getAdmins( );
}
