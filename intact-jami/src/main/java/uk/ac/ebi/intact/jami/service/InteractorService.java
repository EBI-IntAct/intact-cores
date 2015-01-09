package uk.ac.ebi.intact.jami.service;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Interactor;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.interceptor.IntactTransactionSynchronization;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractor;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * Interactor service
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/02/14</pre>
 */
@Service(value = "interactorService")
@Lazy
@Scope( BeanDefinition.SCOPE_PROTOTYPE )
public class InteractorService implements IntactService<Interactor>{

    @Resource(name = "intactDao")
    private IntactDao intactDAO;
    @Resource(name = "intactTransactionSynchronization")
    private IntactTransactionSynchronization afterCommitExecutor;

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll() {
        return this.intactDAO.getInteractorBaseDao().countAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Interactor> iterateAll() {
        // use proxy and not this for transactional annotations to work
        return new IntactQueryResultIterator<Interactor>((InteractorService) ApplicationContextProvider.getBean("interactorService"));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Interactor> fetchIntactObjects(int first, int max) {
        return new ArrayList<Interactor>(this.intactDAO.getInteractorBaseDao().getAll("ac", first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public void saveOrUpdate(Interactor object) throws PersisterException, FinderException, SynchronizerException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        // we can synchronize the complex with the database now
        intactDAO.getSynchronizerContext().getInteractorSynchronizer().synchronize(object, true);
        this.intactDAO.getSynchronizerContext().getInteractorSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll(String countQuery, Map<String, Object> parameters) {
        return this.intactDAO.getInteractorBaseDao().countByQuery(countQuery, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Interactor> iterateAll(String queryCount, String query, Map<String, Object> parameters) {
        // use proxy and not this for transactional annotations to work
        return new IntactQueryResultIterator<Interactor>((InteractorService) ApplicationContextProvider.getBean("interactorService"), query, queryCount, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Interactor> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max) {
        return new ArrayList<Interactor>(this.intactDAO.getInteractorBaseDao().getByQuery(query, parameters, first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Interactor> fetchIntactObjects(String query, Map<String, Object> parameters) {
        return new ArrayList<Interactor>(this.intactDAO.getInteractorBaseDao().getByQuery(query, parameters, 0, Integer.MAX_VALUE));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Interactor> iterateAll(boolean loadLazyCollections) {
        // use proxy and not this for transactional annotations to work
        return new IntactQueryResultIterator<Interactor>((InteractorService) ApplicationContextProvider.getBean("interactorService"), loadLazyCollections);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Interactor> fetchIntactObjects(int first, int max, boolean loadLazyCollections) {
        List<Interactor> results = new ArrayList<Interactor>(this.intactDAO.getInteractorBaseDao().getAll("ac", first, max));
        initialiseLazyInteractor(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Interactor> iterateAll(String countQuery, String query, Map<String, Object> parameters, boolean loadLazyCollections) {
        // use proxy and not this for transactional annotations to work
        return new IntactQueryResultIterator<Interactor>((InteractorService) ApplicationContextProvider.getBean("interactorService"), query, countQuery, parameters, loadLazyCollections);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Interactor> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max, boolean loadLazyCollections) {
        List<Interactor> results = new ArrayList<Interactor>(this.intactDAO.getInteractorBaseDao().getByQuery(query, parameters, first, max));
        initialiseLazyInteractor(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Interactor> fetchIntactObjects(String query, Map<String, Object> parameters, boolean loadLazyCollections) {
        List<Interactor> results = new ArrayList<Interactor>(this.intactDAO.getInteractorBaseDao().getByQuery(query, parameters, 0, Integer.MAX_VALUE));
        initialiseLazyInteractor(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(Collection<? extends Interactor> objects) throws SynchronizerException, PersisterException, FinderException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        for (Interactor interactor : objects){
            // we can synchronize the complex with the database now
            intactDAO.getSynchronizerContext().getInteractorSynchronizer().synchronize(interactor, true);
        }
        this.intactDAO.getSynchronizerContext().getInteractorSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(Interactor object) throws PersisterException, FinderException, SynchronizerException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        this.intactDAO.getSynchronizerContext().getInteractorSynchronizer().delete(object);
        this.intactDAO.getSynchronizerContext().getInteractorSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(Collection<? extends Interactor> objects) throws SynchronizerException, PersisterException, FinderException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        for (Interactor interactor : objects){
            this.intactDAO.getSynchronizerContext().getInteractorSynchronizer().delete(interactor);
        }
        this.intactDAO.getSynchronizerContext().getInteractorSynchronizer().flush();
    }

    public IntactDao getIntactDao() {
        return intactDAO;
    }

    private void initialiseLazyInteractor(boolean loadLazyCollections, List<Interactor> results) {
        if (loadLazyCollections){
            for (Interactor interactor : results){
                IntactUtils.initialiseInteractor((IntactInteractor) interactor);
            }
        }
    }
}
