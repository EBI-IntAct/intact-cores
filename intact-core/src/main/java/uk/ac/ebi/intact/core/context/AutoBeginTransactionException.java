/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core.context;

/**
 * Used when auto begin transaction is not active and the user forgot to start a transaction before to read/write data.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.5
 */
public class AutoBeginTransactionException extends RuntimeException {

    public AutoBeginTransactionException() {
    }

    public AutoBeginTransactionException( Throwable cause ) {
        super( cause );
    }

    public AutoBeginTransactionException( String message ) {
        super( message );
    }

    public AutoBeginTransactionException( String message, Throwable cause ) {
        super( message, cause );
    }
}