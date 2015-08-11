/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.model;

/**
 * Thrown if trying to do an hibernate query with a class not annotated as @javax.persistence.Entity
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
public class NotAnEntityException extends RuntimeException {

    public NotAnEntityException( Class offendingClass ) {
        super( "Class is not annotated with @javax.persistence.Entity: " + offendingClass );
    }

}
