package uk.ac.ebi.intact.jami.interceptor;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import uk.ac.ebi.intact.jami.dao.IntactDao;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class allows to execute some runnables after each transaction (used for clearing cache for instance)
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/08/14</pre>
 */
@Component(value = "intactTransactionSynchronization")
@Lazy
@Scope( BeanDefinition.SCOPE_PROTOTYPE )
public class IntactTransactionSynchronizationImpl extends TransactionSynchronizationAdapter implements IntactTransactionSynchronization {
    private static final Logger LOGGER = Logger.getLogger("IntactTransactionSynchronizationImpl");
    private Set<IntactDao> registeredDao = new HashSet<IntactDao>();

    @Override
    public void afterCommit() {
        LOGGER.log(Level.FINE, "Transaction successfully committed, clearing cache {} intact dao", registeredDao.size());
        Set<IntactDao> daos = this.registeredDao;
        for (IntactDao dao : daos) {
            LOGGER.log(Level.FINE, "Clearing cache {}", dao);
            try {
                clearIntactDaoCache(dao);
            } catch (RuntimeException e) {
                LOGGER.log(Level.SEVERE, "Failed to clear intact dao cache " + dao, e);
            }
        }
    }

    @Override
    public void afterCompletion(int status) {
        LOGGER.log(Level.FINE, "Transaction completed with status {}", status == STATUS_COMMITTED ? "COMMITTED" : "ROLLED_BACK");
        Set<IntactDao> daos = this.registeredDao;
        for (IntactDao dao : daos) {
            LOGGER.log(Level.FINE, "Clearing cache {}", dao);
            try {
                clearIntactDaoCache(dao);
            } catch (RuntimeException e) {
                LOGGER.log(Level.SEVERE, "Failed to clear intact dao cache " + dao, e);
            }
        }
        clearRegisteredDao();
    }

    @Override
    public void registerDaoForSynchronization(final IntactDao intactDao) {
        LOGGER.log(Level.INFO, "Submitting new intactdao to clear after commit", intactDao);
        TransactionSynchronizationManager.registerSynchronization(this);
        addIntactDao(intactDao);
    }

    private synchronized void addIntactDao(IntactDao dao){
        this.registeredDao.add(dao);
    }

    private synchronized void clearRegisteredDao(){
        this.registeredDao.clear();
    }

    private synchronized void clearIntactDaoCache(IntactDao dao) {
        dao.getSynchronizerContext().clearCache();
        clearRegisteredDao();
    }
}