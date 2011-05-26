package uk.ac.ebi.intact.core.persister;

/**
 * This exception is thrown when we try to delete an IntAct object which is still attached to existing Intact objects in the database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>26/05/11</pre>
 */

public class IntactObjectDeleteException extends RuntimeException{

    public IntactObjectDeleteException() {
        super();
    }

    public IntactObjectDeleteException(String message) {
        super(message);
    }

    public IntactObjectDeleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public IntactObjectDeleteException(Throwable cause) {
        super(cause);
    }
}
