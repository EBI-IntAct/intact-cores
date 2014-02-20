package uk.ac.ebi.intact.jami.interceptor;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.dao.impl.IntactDaoImpl;

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

    @Override
    public void postFlush(Iterator entities) {

        IntactDao intactDao = ApplicationContextProvider.getBean(IntactDaoImpl.class);

        if (intactDao != null) {
            intactDao.getSynchronizerContext().clearCache();
        }

        super.postFlush(entities);
    }

    @Override
    public void afterTransactionCompletion(Transaction tx) {
        IntactDao intactDao = ApplicationContextProvider.getBean(IntactDaoImpl.class);

        if (intactDao != null) {
            intactDao.getSynchronizerContext().clearCache();
        }

        super.afterTransactionCompletion(tx);
    }
}
