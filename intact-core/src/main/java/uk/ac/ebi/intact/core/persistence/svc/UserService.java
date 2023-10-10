package uk.ac.ebi.intact.core.persistence.svc;

import org.w3c.dom.Document;
import uk.ac.ebi.intact.model.user.User;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * User Service.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.5
 */
public interface UserService {

    void importUsers( Collection<User> users, boolean updateExistingUsers );

    Document buildUsersDocument( Collection<User> users ) throws UserServiceException;

    Collection<User> readUsersDocument( Document document );

    void marshallUsers( Collection<User> users, OutputStream os ) throws UserServiceException;

    Collection<User> parseUsers( InputStream userInputStream ) throws UserServiceException;
}
