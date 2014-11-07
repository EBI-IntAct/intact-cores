package uk.ac.ebi.intact.jami.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Complex;
import uk.ac.ebi.intact.jami.lifecycle.ComplexBCLifecycleEventListener;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Complex service
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/02/14</pre>
 */
@Service(value = "complexService")
@Lazy
public class ComplexService extends AbstractReleasableLifeCycleService<IntactComplex> implements IntactService<Complex>{
    private static final Logger LOGGER = Logger.getLogger("ComplexService");

    private ComplexBCLifecycleEventListener complexBCListener = new ComplexBCLifecycleEventListener();

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll() {
        return getIntactDao().getComplexDao().countAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Complex> iterateAll() {
        return new IntactQueryResultIterator<Complex>(this);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Complex> fetchIntactObjects(int first, int max) {
        return new ArrayList<Complex>(getIntactDao().getComplexDao().getAll("ac", first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll(String countQuery, Map<String, Object> parameters) {
        return getIntactDao().getComplexDao().countByQuery(countQuery, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Complex> iterateAll(String queryCount, String query, Map<String, Object> parameters) {
        return new IntactQueryResultIterator<Complex>(this, query, queryCount, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Complex> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max) {
        return new ArrayList<Complex>(getIntactDao().getComplexDao().getByQuery(query, parameters, first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(Complex object) throws PersisterException, FinderException, SynchronizerException {
        getAfterCommitExecutor().registerDaoForSynchronization(getIntactDao());

        // we can synchronize the complex with the database now
        getIntactDao().getSynchronizerContext().getComplexSynchronizer().synchronize(object, true);
        getIntactDao().getEntityManager().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(Collection<? extends Complex> objects) throws SynchronizerException, PersisterException, FinderException {
        getAfterCommitExecutor().registerDaoForSynchronization(getIntactDao());
        for (Complex interaction : objects){
            // we can synchronize the complex with the database now
            getIntactDao().getSynchronizerContext().getComplexSynchronizer().synchronize(interaction, true);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(Complex object) throws PersisterException, FinderException, SynchronizerException {
        getAfterCommitExecutor().registerDaoForSynchronization(getIntactDao());
        getIntactDao().getSynchronizerContext().getComplexSynchronizer().delete(object);
        getIntactDao().getEntityManager().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(Collection<? extends Complex> objects) throws SynchronizerException, PersisterException, FinderException {
        getAfterCommitExecutor().registerDaoForSynchronization(getIntactDao());
        for (Complex interaction : objects){
            getIntactDao().getSynchronizerContext().getComplexSynchronizer().delete(interaction);
        }
    }

    @Override
    protected IntactComplex loadReleasableByAc(String ac) {
        return getIntactDao().getComplexDao().getByAc(ac);
    }

    @Override
    protected void updateReleasable(IntactComplex releasable) {
        try {
            getIntactDao().getComplexDao().update(releasable);
        } catch (FinderException e) {
            LOGGER.log(Level.SEVERE, "Cannot update complex "+releasable.getAc(), e);
        } catch (SynchronizerException e) {
            LOGGER.log(Level.SEVERE, "Cannot update complex " + releasable.getAc(), e);
        } catch (PersisterException e) {
            LOGGER.log(Level.SEVERE, "Cannot update complex " + releasable.getAc(), e);
        }
    }

    @Override
    protected void registerListeners() {
        getLifecycleManager().registerListener(this.complexBCListener);
    }
}
