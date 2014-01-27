package uk.ac.ebi.intact.jami.synchronizer;

/**
 * Exception thrown when we cannot persist an object
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class PersisterException extends Exception {

    public PersisterException() {
        super();
    }

    public PersisterException(String message) {
        super(message);
    }

    public PersisterException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersisterException(Throwable cause) {
        super(cause);
    }

    protected PersisterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
