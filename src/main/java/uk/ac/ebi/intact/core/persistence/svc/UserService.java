package uk.ac.ebi.intact.core.persistence.svc;

import uk.ac.ebi.intact.model.user.User;

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
}
