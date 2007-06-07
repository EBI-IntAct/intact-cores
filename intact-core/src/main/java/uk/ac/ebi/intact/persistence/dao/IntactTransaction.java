/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.RuntimeConfig;

/**
 * It is a wrapper for Transactions
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>11-Jul-2006</pre>
 */
public class IntactTransaction {

    private static final Log log = LogFactory.getLog( IntactTransaction.class );

    private Transaction transaction;

    /**
     * If DEBUG_MODE is true, a stack trace has been stored in the transaction so we can trace who created it.
     */
    private StackTraceElement[] stackTrace;

    public IntactTransaction( IntactSession session, Transaction transaction ) {
        this.transaction = transaction;

        log.debug( "Transaction started" );

        if ( !RuntimeConfig.getCurrentInstance( session ).isDebugMode() ) {
            if ( log.isDebugEnabled() ) {
                log.debug( "Storing StackTrace on opening transaction, now the caller can be traced !!" );
            }

            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            stackTrace = new StackTraceElement[elements.length - 2];
            for ( int i = 2; i < elements.length; i++ ) {
                stackTrace[i - 2] = elements[i];
            }
        }
    }

    /**
     * If DEBUG_MODE is true, a stack trace has been stored in the transaction so we can trace who created it.
     *
     * @return This method returns the StackTraceElement array representing the stack trace. May be null.
     */
    public StackTraceElement[] getStackTrace() {
        return stackTrace;
    }

    public void commit() throws IntactTransactionException {
        log.debug( "Committing transaction" );

        try {
            transaction.commit();
        }
        catch ( HibernateException e ) {
            throw new IntactTransactionException( "Commit exception", e );
        }

        assert ( wasCommitted() );

        log.debug( "Transaction committed" );
    }

    public void rollback() throws IntactTransactionException {
        log.debug( "Rolling-back transaction" );

        try {
            transaction.rollback();
        }
        catch ( HibernateException e ) {
            throw new IntactTransactionException( "Rollback exception", e );
        }
    }

    public boolean wasCommitted() {
        return transaction.wasCommitted();
    }

    public boolean wasRolledBack() {
        return transaction.wasRolledBack();
    }

    public Object getWrappedTransaction() {
        return transaction;
    }
}