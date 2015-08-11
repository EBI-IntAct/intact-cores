package uk.ac.ebi.intact.core.persistence.svc;

/**
 * UserService Exception.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.5
 */
public class UserServiceException extends Exception {

    public UserServiceException() {
        super();
    }

    public UserServiceException( String message ) {
        super( message );
    }

    public UserServiceException( String message, Throwable cause ) {
        super( message, cause );
    }

    public UserServiceException( Throwable cause ) {
        super( cause );
    }
}
