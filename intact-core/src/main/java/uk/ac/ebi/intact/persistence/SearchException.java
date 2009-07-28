/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.persistence;

/**
 * <p>Exception class to provide more meaningful error messages.
 * There is an extra constructor to allow other exceptions to
 * pass on information.</p>
 */

public class SearchException extends Exception {


    private String nestedMessage;
    private Exception rootCause;

    public SearchException() {
    }

    public SearchException( String msg ) {

        super( msg );
    }

    public SearchException( String msg, Exception e ) {

        super( msg );
        e.fillInStackTrace();
        nestedMessage = e.getMessage();

        //this is the bottom level of intact exceptions - so wrap the origin
        rootCause = e;

    }


    public SearchException( String message, Throwable cause ) {
        super( message, cause );
    }

    public SearchException( Throwable cause ) {
        super( cause );
    }

    public String getNestedMessage() {

        if ( nestedMessage != null ) {

            return nestedMessage;
        } else {

            return "No nested messages have been passed on.";
        }
    }

    public Exception getRootCause() {
        return rootCause;
    }


}
