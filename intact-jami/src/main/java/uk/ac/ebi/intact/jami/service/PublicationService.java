package uk.ac.ebi.intact.jami.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Publication;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Publication service
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/02/14</pre>
 */
@Service(value = "publicationService")
@Lazy
public class PublicationService extends AbstractReleasableLifeCycleService<IntactPublication> implements IntactService<Publication>{

    private static final Logger LOGGER = Logger.getLogger("ComplexService");

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll() {
        return getIntactDao().getPublicationDao().countAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Publication> iterateAll() {
        return new IntactQueryResultIterator<Publication>(this);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Publication> fetchIntactObjects(int first, int max) {
        return new ArrayList<Publication>(getIntactDao().getPublicationDao().getAll("ac", first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll(String countQuery, Map<String, Object> parameters) {
        return getIntactDao().getPublicationDao().countByQuery(countQuery, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Publication> iterateAll(String queryCount, String query, Map<String, Object> parameters) {
        return new IntactQueryResultIterator<Publication>(this, query, queryCount, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Publication> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max) {
        return new ArrayList<Publication>(getIntactDao().getPublicationDao().getByQuery(query, parameters, first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(Publication object) throws PersisterException, FinderException, SynchronizerException {
        getAfterCommitExecutor().registerDaoForSynchronization(getIntactDao());

        // we can synchronize the complex with the database now
        getIntactDao().getSynchronizerContext().getPublicationSynchronizer().synchronize(object, true);
        getIntactDao().getEntityManager().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(Collection<? extends Publication> objects) throws SynchronizerException, PersisterException, FinderException {
        getAfterCommitExecutor().registerDaoForSynchronization(getIntactDao());

        for (Publication pub : objects){
            // we can synchronize the complex with the database now
            getIntactDao().getSynchronizerContext().getPublicationSynchronizer().synchronize(pub, true);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(Publication object) throws PersisterException, FinderException, SynchronizerException {
        getAfterCommitExecutor().registerDaoForSynchronization(getIntactDao());

        getIntactDao().getSynchronizerContext().getPublicationSynchronizer().delete(object);

        getIntactDao().getEntityManager().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(Collection<? extends Publication> objects) throws SynchronizerException, PersisterException, FinderException {
        getAfterCommitExecutor().registerDaoForSynchronization(getIntactDao());

        for (Publication pub : objects){
            getIntactDao().getSynchronizerContext().getPublicationSynchronizer().delete(pub);
        }

    }

    @Override
    protected IntactPublication loadReleasableByAc(String ac) {
        return getIntactDao().getPublicationDao().getByAc(ac);
    }

    @Override
    protected void updateReleasable(IntactPublication releasable) {
        try {
            getIntactDao().getPublicationDao().update(releasable);
        } catch (FinderException e) {
            LOGGER.log(Level.SEVERE, "Cannot update publication "+releasable.getAc(), e);
        } catch (SynchronizerException e) {
            LOGGER.log(Level.SEVERE, "Cannot update publication "+releasable.getAc(), e);
        } catch (PersisterException e) {
            LOGGER.log(Level.SEVERE, "Cannot update publication "+releasable.getAc(), e);
        }
    }

    @Override
    protected void registerListeners() {
        // nothing to do
    }
}
