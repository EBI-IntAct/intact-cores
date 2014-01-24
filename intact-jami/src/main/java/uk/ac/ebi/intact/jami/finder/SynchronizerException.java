package uk.ac.ebi.intact.jami.finder;

/**
 * Exception thrown when we cannot synchronize an object
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class SynchronizerException extends Exception{

    public SynchronizerException() {
        super();
    }

    public SynchronizerException(String message) {
        super(message);
    }

    public SynchronizerException(String message, Throwable cause) {
        super(message, cause);
    }

    public SynchronizerException(Throwable cause) {
        super(cause);
    }

    protected SynchronizerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
