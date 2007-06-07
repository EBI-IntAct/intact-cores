/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.business;

/**
 * <p>Exception class to provide more meaningful error messages.
 * There is an extra constructor to allow other exceptions to
 * pass on information.</p>
 */

public class DuplicateLabelException extends IntactException {

    /**
     * @param criteria the criteria with which we did the search
     * @param object   the Class of the object we were searching for
     */
    public DuplicateLabelException( String criteria, String object ) {
        super( "Search by label: " + criteria + ", returned more than one result of type: " + object );
    }

    public DuplicateLabelException() {
        super( "Search by label returned more than one result" );
    }

}
