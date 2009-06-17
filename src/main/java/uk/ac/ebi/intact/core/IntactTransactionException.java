package uk.ac.ebi.intact.core;

/**
 * Thrown if a transaction fails.
 */
public class IntactTransactionException extends RuntimeException {

    private String nestedMessage;
    private Exception rootCause;

    public IntactTransactionException() {
    }

    public IntactTransactionException( String msg ) {

        super( msg );
    }

    public IntactTransactionException( String msg, Exception e ) {

        super( msg, e );
        /*
        if (e != null) {
            e.fillInStackTrace();
            nestedMessage = e.getMessage();
            if(e instanceof SearchException) {

                //filter to initital cause up...
                rootCause = ((SearchException)e).getRootCause();
            }
            else {
                rootCause = e;
            }
        }  */
    }


    public IntactTransactionException( String message, Throwable cause ) {
        super( message, cause );
    }

    public IntactTransactionException( Throwable cause ) {
        super( cause );
    }

    public String getNestedMessage() {

        if ( nestedMessage != null ) {

            return nestedMessage;
        } else {

            return "No nested messages have been passed on.";
        }
    }

    public boolean rootCauseExists() {
        return ( rootCause != null );
    }

    public Exception getRootCause() {
        return rootCause;
    }
}
