/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.core.persistence;

/**
 * <p>Exception class to provide more meaningful error messages.
 * There is an extra constructor to allow other exceptions to
 * pass on information. </p>
 *
 * @author Chris Lewington
 * @version $Id$
 */

public class UpdateException extends Exception {

    private String nestedMessage;
    private Exception rootCause;

    /**
     * The default construstor with a default error message.
     */
    public UpdateException() {
        this( "Unable to update: the object not yet persisted; create first!" );
    }

    public UpdateException( String msg ) {
        super( msg );
    }

    /**
     * this constructor is used typically to pass on extra failure
     * information from excpetions thrown elsewhere, eg from within other APIs
     *
     * @param msg - the currently raised message
     * @param e   - an Exception being passed on
     */
    public UpdateException( String msg, Exception e ) {
        super( msg );
        e.fillInStackTrace();
        nestedMessage = e.getMessage();
        //this is the bottom level of intact exceptions - so wrap the origin
        rootCause = e;
    }

    /**
     * this method obtains any information from within nested exceptions
     * that have been passed on.
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

    public Exception getRootCause() {
        return rootCause;
    }
}
