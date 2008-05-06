package uk.ac.ebi.intact.persistence.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.context.IntactSession;

import javax.persistence.EntityTransaction;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class JpaIntactTransaction extends IntactTransaction {

    private Log log = LogFactory.getLog(JpaIntactTransaction.class);

    private EntityTransaction transaction;
    private boolean wasCommitted = false;
    private boolean wasRolledBack = false;

    public JpaIntactTransaction(IntactSession session, EntityTransaction transaction) {
        super(session, null);
        this.transaction = transaction;
    }

    public boolean wasCommitted() {
        return wasCommitted;
    }

    public void rollback() throws IntactTransactionException {
       transaction.rollback();
        wasRolledBack = true;
    }

    public void commit() throws IntactTransactionException {
        if (log.isDebugEnabled()) log.debug("Commit transaction");
        transaction.commit();
        wasCommitted = true;
    }

    public Object getWrappedTransaction() {
        return transaction;
    }

    public boolean wasRolledBack() {
        return wasRolledBack;
    }
}
