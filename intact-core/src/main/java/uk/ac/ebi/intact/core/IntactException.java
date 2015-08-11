/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.core;

/**
 * <p>Exception class to provide more meaningful error messages.
 * There is an extra constructor to allow other exceptions to
 * pass on information.</p>
 */

public class IntactException extends RuntimeException {


    private String nestedMessage;
    private Exception rootCause;

    public IntactException() {
    }

    public IntactException( String msg ) {

        super( msg );
    }

    public IntactException( String msg, Exception e ) {

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


    public IntactException( String message, Throwable cause ) {
        super( message, cause );
    }

    public IntactException( Throwable cause ) {
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
