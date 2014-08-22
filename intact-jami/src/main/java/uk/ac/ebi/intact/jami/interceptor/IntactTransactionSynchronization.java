package uk.ac.ebi.intact.jami.interceptor;

import org.springframework.transaction.support.TransactionSynchronization;
import uk.ac.ebi.intact.jami.dao.IntactDao;

/**
 * Interface for classes that will react to a spring transaction event.
 *
 * This interface allows to register a specific intactdao and reacts to the spring transaction event (clearing cache of synchronizationContext of the intactDao for instance)
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/08/14</pre>
 */
public interface IntactTransactionSynchronization extends TransactionSynchronization {

    /**
     * Attach this intactDao to the transaction synchronization adapter so it will be taken into account during the spring transaction (clearing cache after commit for instance)
     * @param intactDao
     */
    public void registerDaoForSynchronization(IntactDao intactDao);
}
