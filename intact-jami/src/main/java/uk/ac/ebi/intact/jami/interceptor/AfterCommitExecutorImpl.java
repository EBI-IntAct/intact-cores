package uk.ac.ebi.intact.jami.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import uk.ac.ebi.intact.jami.dao.IntactDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class allows to execute some runnables after each transaction (used for clearing cache for instance)
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/08/14</pre>
 */
@Component(value = "afterCommitExecutor")
public class AfterCommitExecutorImpl extends TransactionSynchronizationAdapter implements IntactTransactionSynchronization, Executor {
    private static final Logger LOGGER = Logger.getLogger("AfterCommitExecutorImpl");
    private static final ThreadLocal<List<Runnable>> RUNNABLES = new ThreadLocal<List<Runnable>>();

    @Override
    public void execute(Runnable runnable) {
        LOGGER.log(Level.INFO, "Submitting new runnable {} to run after commit", runnable);
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            LOGGER.log(Level.FINE, "Transaction synchronization is NOT ACTIVE. Executing right now runnable {}", runnable);
            runnable.run();
            return;
        }
        List<Runnable> threadRunnables = RUNNABLES.get();
        if (threadRunnables == null) {
            threadRunnables = new ArrayList<Runnable>();
            RUNNABLES.set(threadRunnables);
            TransactionSynchronizationManager.registerSynchronization(this);
        }
        threadRunnables.add(runnable);
    }

    @Override
    public void afterCommit() {
        List<Runnable> threadRunnables = getThreadRunnables();
        LOGGER.log(Level.FINE, "Transaction successfully committed, executing {} runnables", threadRunnables.size());
        for (int i = 0; i < threadRunnables.size(); i++) {
            Runnable runnable = threadRunnables.get(i);
            LOGGER.log(Level.FINE, "Executing runnable {}", runnable);
            try {
                runnable.run();
            } catch (RuntimeException e) {
                LOGGER.log(Level.SEVERE, "Failed to execute runnable " + runnable, e);
            }
        }
    }

    @Override
    public void afterCompletion(int status) {
        LOGGER.log(Level.FINE, "Transaction completed with status {}", status == STATUS_COMMITTED ? "COMMITTED" : "ROLLED_BACK");
        RUNNABLES.remove();
    }

    @Override
    public void registerDaoForSynchronization(final IntactDao intactDao) {
        execute(new Runnable() {
            @Override
            public void run() {
                intactDao.getSynchronizerContext().clearCache();
            }
        });
    }

    private List<Runnable> getThreadRunnables(){
        List<Runnable> threadRunnables = RUNNABLES.get();
        if (threadRunnables == null) {
            return Collections.EMPTY_LIST;
        }
        return threadRunnables;
    }
}