package uk.ac.ebi.intact.jami.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.interceptor.IntactTransactionSynchronization;
import uk.ac.ebi.intact.jami.lifecycle.LifeCycleManager;
import uk.ac.ebi.intact.jami.model.lifecycle.Releasable;

import javax.persistence.FlushModeType;
import java.util.logging.Logger;

/**
 * Abstract class for releasable services
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07/10/14</pre>
 */

public abstract class AbstractReleasableLifeCycleService<T extends Releasable> implements ReleasableLifecycleService{

    @Autowired
    @Qualifier("intactDao")
    private IntactDao intactDao;

    @Autowired
    @Qualifier("jamiLifeCycleManager")
    private LifeCycleManager lifecycleManager;

    @Autowired
    @Qualifier("intactTransactionSynchronization")
    private IntactTransactionSynchronization afterCommitExecutor;

    private static final Logger LOGGER = Logger.getLogger("AbstractReleasableLifeCycleService");

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void createNewReleasable(String ac, String comment, String userLogin){
        // register intactdao in the transaction manager so it can clean cache after transaction commit
        afterCommitExecutor.registerDaoForSynchronization(this.intactDao);

        // register listeners if necessary
        registerListeners();

        // load releasable
        T releasable = loadReleasableByAc(ac);
        if (releasable == null){
            LOGGER.severe("Releasable " + ac + " does not exist.");
        }
        else{
            // do lifecycle action
            lifecycleManager.getStartStatus().create(releasable,comment, intactDao.getUserDao().getByLogin(userLogin));

            // update releasable
            updateReleasable(releasable);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void assignReleasableToCurator(String ac, String curatorLogin, String loginUser){
        // register intactdao in the transaction manager so it can clean cache after transaction commit
        afterCommitExecutor.registerDaoForSynchronization(this.intactDao);

        // register listeners if necessary
        registerListeners();

        // load releasable
        T releasable = loadReleasableByAc(ac);

        if (releasable == null){
            LOGGER.severe("Releasable " + ac + " does not exist.");
        }
        else{
            // do lifecycle action
            lifecycleManager.getNewStatus().assignToCurator(releasable,
                    intactDao.getUserDao().getByLogin(curatorLogin),
                    intactDao.getUserDao().getByLogin(loginUser));

            // update releasable
            updateReleasable(releasable);
        }

    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void claimReleasableOwnership(String ac, String loginUser){
        // register intactdao in the transaction manager so it can clean cache after transaction commit
        afterCommitExecutor.registerDaoForSynchronization(this.intactDao);

        // register listeners if necessary
        registerListeners();

        // load releasable
        T releasable = loadReleasableByAc(ac);
        if (releasable == null){
            LOGGER.severe("Releasable " + ac + " does not exist.");
        }
        else{
            lifecycleManager.getNewStatus().claimOwnership(releasable,
                    intactDao.getUserDao().getByLogin(loginUser));

            // update releasable
            updateReleasable(releasable);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void reserveReleasable(String ac, String message, String loginUser){
        // register intactdao in the transaction manager so it can clean cache after transaction commit
        afterCommitExecutor.registerDaoForSynchronization(this.intactDao);

        // register listeners if necessary
        registerListeners();

        // load releasable
        T releasable = loadReleasableByAc(ac);
        if (releasable == null){
            LOGGER.severe("Releasable " + ac + " does not exist.");
        }
        else{
            lifecycleManager.getNewStatus().reserve(releasable,
                    message,
                    intactDao.getUserDao().getByLogin(loginUser));

            // update releasable
            updateReleasable(releasable);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void startReleasableCuration(String ac, String loginUser){
        // register intactdao in the transaction manager so it can clean cache after transaction commit
        afterCommitExecutor.registerDaoForSynchronization(this.intactDao);

        // register listeners if necessary
        registerListeners();

        // load releasable
        T releasable = loadReleasableByAc(ac);
        if (releasable == null){
            LOGGER.severe("Releasable " + ac + " does not exist.");
        }
        else{
            lifecycleManager.getAssignedStatus().startCuration(releasable,
                    intactDao.getUserDao().getByLogin(loginUser));

            // update releasable
            updateReleasable(releasable);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void unassignReleasable(String ac, String message, String loginUser){
        // register intactdao in the transaction manager so it can clean cache after transaction commit
        afterCommitExecutor.registerDaoForSynchronization(this.intactDao);

        // register listeners if necessary
        registerListeners();

        // load releasable
        T releasable = loadReleasableByAc(ac);
        if (releasable == null){
            LOGGER.severe("Releasable " + ac + " does not exist.");
        }
        else{
            lifecycleManager.getAssignedStatus().unassign(releasable,
                    message,
                    intactDao.getUserDao().getByLogin(loginUser));

            // update releasable
            updateReleasable(releasable);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void readyForCheckingReleasable(String ac, String message, String userLogin){
        // register intactdao in the transaction manager so it can clean cache after transaction commit
        afterCommitExecutor.registerDaoForSynchronization(this.intactDao);

        // we don't want to flush the queries
        getIntactDao().getEntityManager().setFlushMode(FlushModeType.COMMIT);

        // load releasable
        T releasable = loadReleasableByAc(ac);
        if (releasable == null){
            LOGGER.severe("Releasable " + ac + " does not exist.");
        }
        else{
            lifecycleManager.getCurationInProgressStatus().readyForChecking(releasable,
                    message,
                    true,
                    intactDao.getUserDao().getByLogin(userLogin));

            // update releasable
            updateReleasable(releasable);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void acceptReleasable(String ac, String message, String loginUser){
        // register intactdao in the transaction manager so it can clean cache after transaction commit
        afterCommitExecutor.registerDaoForSynchronization(this.intactDao);

        // register listeners if necessary
        registerListeners();

        // load releasable
        T releasable = loadReleasableByAc(ac);
        if (releasable == null){
            LOGGER.severe("Releasable " + ac + " does not exist.");
        }
        else{
            lifecycleManager.getReadyForCheckingStatus().accept(releasable,
                    message,
                    intactDao.getUserDao().getByLogin(loginUser));

            // update releasable
            updateReleasable(releasable);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void rejectReleasable(String ac, String message, String loginUser){
        // register intactdao in the transaction manager so it can clean cache after transaction commit
        afterCommitExecutor.registerDaoForSynchronization(this.intactDao);

        // register listeners if necessary
        registerListeners();

        // load releasable
        T releasable = loadReleasableByAc(ac);
        if (releasable == null){
            LOGGER.severe("Releasable " + ac + " does not exist.");
        }
        else{
            lifecycleManager.getReadyForCheckingStatus().reject(releasable,
                    message,
                    intactDao.getUserDao().getByLogin(loginUser));

            // update releasable
            updateReleasable(releasable);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void revertReleasableFromReadForChecking(String ac, String userLogin){
        // register intactdao in the transaction manager so it can clean cache after transaction commit
        afterCommitExecutor.registerDaoForSynchronization(this.intactDao);

        // register listeners if necessary
        registerListeners();

        // load releasable
        T releasable = loadReleasableByAc(ac);
        if (releasable == null){
            LOGGER.severe("Releasable " + ac + " does not exist.");
        }
        else{
            lifecycleManager.getReadyForCheckingStatus().revert(releasable,
                    intactDao.getUserDao().getByLogin(userLogin));

            // update releasable
            updateReleasable(releasable);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void readyForRelease(String ac, String message, String loginUser){
        // register intactdao in the transaction manager so it can clean cache after transaction commit
        afterCommitExecutor.registerDaoForSynchronization(this.intactDao);

        // register listeners if necessary
        registerListeners();

        // load releasable
        T releasable = loadReleasableByAc(ac);
        if (releasable == null){
            LOGGER.severe("Releasable " + ac + " does not exist.");
        }
        else{
            lifecycleManager.getAcceptedStatus().readyForRelease(releasable,
                    message,
                    intactDao.getUserDao().getByLogin(loginUser));

            // update releasable
            updateReleasable(releasable);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void removeOnHoldFromReleasable(String ac, String message, String loginUser){
        // register intactdao in the transaction manager so it can clean cache after transaction commit
        afterCommitExecutor.registerDaoForSynchronization(this.intactDao);

        // register listeners if necessary
        registerListeners();

        // load releasable
        T releasable = loadReleasableByAc(ac);
        if (releasable == null){
            LOGGER.severe("Releasable "+ac+" does not exist.");
        }
        else{
            lifecycleManager.getAcceptedOnHoldStatus().onHoldRemoved(releasable,
                    message,
                    intactDao.getUserDao().getByLogin(loginUser));

            // update releasable
            updateReleasable(releasable);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void release(String ac, String message, String user){
        // register intactdao in the transaction manager so it can clean cache after transaction commit
        afterCommitExecutor.registerDaoForSynchronization(this.intactDao);

        // register listeners if necessary
        registerListeners();

        // load releasable
        T releasable = loadReleasableByAc(ac);
        if (releasable == null){
            LOGGER.severe("Releasable " + ac + " does not exist.");
        }
        else{
            lifecycleManager.getReadyForReleaseStatus().release(releasable,
                    message,
                    intactDao.getUserDao().getByLogin(user));

            // update releasable
            updateReleasable(releasable);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void putReleasableOnHoldFromReadyForRelease(String ac, String message, String loginUser){
        // register intactdao in the transaction manager so it can clean cache after transaction commit
        afterCommitExecutor.registerDaoForSynchronization(this.intactDao);

        // register listeners if necessary
        registerListeners();

        // load releasable
        T releasable = loadReleasableByAc(ac);
        if (releasable == null){
            LOGGER.severe("Releasable " + ac + " does not exist.");
        }
        else{
            lifecycleManager.getReadyForReleaseStatus().putOnHold(releasable,
                    message,
                    intactDao.getUserDao().getByLogin(loginUser));

            // update releasable
            updateReleasable(releasable);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void revertReleasableToReadyForChecking(String ac, String loginUser){
        // register intactdao in the transaction manager so it can clean cache after transaction commit
        afterCommitExecutor.registerDaoForSynchronization(this.intactDao);

        // register listeners if necessary
        registerListeners();

        // load releasable
        T releasable = loadReleasableByAc(ac);
        if (releasable == null){
            LOGGER.severe("Releasable " + ac + " does not exist.");
        }
        else{
            lifecycleManager.getReadyForReleaseStatus().revert(releasable,
                    intactDao.getUserDao().getByLogin(loginUser));

            // update releasable
            updateReleasable(releasable);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void moveReleasableFromReleasedToOnHold(String ac, String message, String loginUser){
        // register intactdao in the transaction manager so it can clean cache after transaction commit
        afterCommitExecutor.registerDaoForSynchronization(this.intactDao);

        // register listeners if necessary
        registerListeners();

        // load releasable
        T releasable = loadReleasableByAc(ac);
        if (releasable == null){
            LOGGER.severe("Releasable " + ac + " does not exist.");
        }
        else{
            lifecycleManager.getReleasedStatus().putOnHold(releasable,
                    message,
                    intactDao.getUserDao().getByLogin(loginUser));

            // update releasable
            updateReleasable(releasable);
        }
    }

    protected abstract T loadReleasableByAc(String ac);
    protected abstract void updateReleasable(T releasable);

    protected IntactDao getIntactDao() {
        return intactDao;
    }

    protected LifeCycleManager getLifecycleManager() {
        return lifecycleManager;
    }

    protected IntactTransactionSynchronization getAfterCommitExecutor() {
        return afterCommitExecutor;
    }

    protected abstract void registerListeners();

}
