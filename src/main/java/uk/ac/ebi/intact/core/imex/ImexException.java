package uk.ac.ebi.intact.core.imex;

/**
 * ImexException.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public class ImexException extends Exception {
    public ImexException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public ImexException( String message ) {
        super( message );    //To change body of overridden methods use File | Settings | File Templates.
    }

    public ImexException( String message, Throwable cause ) {
        super( message, cause );    //To change body of overridden methods use File | Settings | File Templates.
    }

    public ImexException( Throwable cause ) {
        super( cause );    //To change body of overridden methods use File | Settings | File Templates.
    }
}
