package uk.ac.ebi.intact.jami.interceptor;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.hibernate.engine.transaction.spi.LocalStatus;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;

import java.util.Iterator;

/**
 * Basic interceptor for the synchronizers in the dao that will
 * clear the cache on commit//rollback
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>19/02/14</pre>
 */

public class IntactSynchronizerInterceptor extends EmptyInterceptor{
    /** Completion status in case of proper commit */
    int STATUS_COMMITTED = 0;

    /** Completion status in case of proper rollback */
    int STATUS_ROLLED_BACK = 1;

    /** Completion status in case of heuristic mixed completion or system errors */
    int STATUS_UNKNOWN = 2;

    @Override
    public void postFlush(Iterator entities) {

        IntactTransactionSynchronization afterCommitExecutor = ApplicationContextProvider.getBean("afterCommitExecutor");
        if (afterCommitExecutor != null){
            afterCommitExecutor.afterCommit();
        }

        super.postFlush(entities);
    }

    @Override
    public void afterTransactionCompletion(Transaction tx) {
        IntactTransactionSynchronization afterCommitExecutor = ApplicationContextProvider.getBean("afterCommitExecutor");
        if (afterCommitExecutor != null){
            if (tx.getLocalStatus() == LocalStatus.COMMITTED){
                afterCommitExecutor.afterCompletion(STATUS_COMMITTED);
            }
            else if (tx.getLocalStatus() == LocalStatus.ROLLED_BACK){
                afterCommitExecutor.afterCompletion(STATUS_ROLLED_BACK);
            }
            else{
                afterCommitExecutor.afterCompletion(STATUS_UNKNOWN);
            }
        }

        super.afterTransactionCompletion(tx);
    }
}
