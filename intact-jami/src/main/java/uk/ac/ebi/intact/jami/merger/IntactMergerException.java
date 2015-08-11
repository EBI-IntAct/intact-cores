package uk.ac.ebi.intact.jami.merger;

/**
 * Merger exception thrown when a merge fails
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public class IntactMergerException extends RuntimeException {

    public IntactMergerException() {
        super();
    }

    public IntactMergerException(String message) {
        super(message);
    }

    public IntactMergerException(String message, Throwable cause) {
        super(message, cause);
    }

    public IntactMergerException(Throwable cause) {
        super(cause);
    }

    protected IntactMergerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
