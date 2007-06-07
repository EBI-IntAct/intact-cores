/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.persistence;

/**
 * <p>Exception class to provide more meaningful error messages. There is an extra constructor to allow other exceptions
 * to pass on information. </p>
 *
 * @author Chris Lewington
 */
public class CreateException extends Exception {

    private String nestedMessage;

    public CreateException() {
    }

    public CreateException( String msg ) {

        super( msg );
    }

    /**
     * this constructor is used typically to pass on extra failure information from exceptions thrown elsewhere, eg from
     * within other APIs
     *
     * @param msg   the currently raised message
     * @param cause an Exception being passed on
     */
    public CreateException( String msg, Exception cause ) {

        super( msg, cause ); // pass on the cause exception
        cause.fillInStackTrace();
        nestedMessage = cause.getMessage();
    }

    /**
     * this method obtains any information from within nested exceptions that have been passed on.
     *
     * @return String - a nested message, or a string indiciating none available
     */
    public String getNestedMessage() {

        if ( nestedMessage != null ) {

            return nestedMessage;
        } else {

            return "No nested messages have been passed on.";
        }
    }
}