package uk.ac.ebi.intact.jami;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.context.UserContext;

import java.util.Iterator;

/**
 * Basic interceptor for the synchronizers that will
 * clear the cache on commit//rollback
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>19/02/14</pre>
 */

public class IntactSynchronizerInterceptor extends EmptyInterceptor{

    @Override
    public void postFlush(Iterator entities) {

        UserContext userContext = ApplicationContextProvider.getBean("", SynchronizerContext.class);

        if (userContext != null && userContext.getUserId() != null) {
            currentUser = userContext.getUserId().toUpperCase();
        }

        super.postFlush(entities);
    }

    @Override
    public void afterTransactionCompletion(Transaction tx) {
        super.afterTransactionCompletion(tx);
    }
}
