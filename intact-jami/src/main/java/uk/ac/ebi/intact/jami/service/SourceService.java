package uk.ac.ebi.intact.jami.service;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Source;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.interceptor.IntactTransactionSynchronization;
import uk.ac.ebi.intact.jami.model.extension.IntactSource;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * Source service
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/02/14</pre>
 */
@Service(value = "sourceService")
@Lazy
@Scope( BeanDefinition.SCOPE_PROTOTYPE )
public class SourceService implements IntactService<Source>{

    @Resource(name = "intactDao")
    private IntactDao intactDAO;
    @Resource(name = "intactTransactionSynchronization")
    private IntactTransactionSynchronization afterCommitExecutor;

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll() {
        return this.intactDAO.getSourceDao().countAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Source> iterateAll() {
        // use proxy and not this for transactional annotations to work
        return new IntactQueryResultIterator<Source>((SourceService) ApplicationContextProvider.getBean("sourceService"));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Source> fetchIntactObjects(int first, int max) {
        return new ArrayList<Source>(this.intactDAO.getSourceDao().getAll("ac", first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll(String countQuery, Map<String, Object> parameters) {
        return this.intactDAO.getSourceDao().countByQuery(countQuery, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Source> iterateAll(String queryCount, String query, Map<String, Object> parameters) {
        // use proxy and not this for transactional annotations to work
        return new IntactQueryResultIterator<Source>((SourceService) ApplicationContextProvider.getBean("sourceService"), query, queryCount, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Source> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max) {
        return new ArrayList<Source>(this.intactDAO.getSourceDao().getByQuery(query, parameters, first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Source> fetchIntactObjects(String query, Map<String, Object> parameters) {
        return new ArrayList<Source>(this.intactDAO.getSourceDao().getByQuery(query, parameters, 0, Integer.MAX_VALUE));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Source> iterateAll(boolean loadLazyCollections) {
        // use proxy and not this for transactional annotations to work
        return new IntactQueryResultIterator<Source>((SourceService) ApplicationContextProvider.getBean("sourceService"), loadLazyCollections);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Source> fetchIntactObjects(int first, int max, boolean loadLazyCollections) {
        List<Source> results = new ArrayList<Source>(this.intactDAO.getSourceDao().getAll("ac", first, max));
        initialiseLazySource(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Source> iterateAll(String countQuery, String query, Map<String, Object> parameters, boolean loadLazyCollections) {
        // use proxy and not this for transactional annotations to work
        return new IntactQueryResultIterator<Source>((SourceService) ApplicationContextProvider.getBean("sourceService"), query, countQuery, parameters, loadLazyCollections);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Source> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max, boolean loadLazyCollections) {
        List<Source> results = new ArrayList<Source>(this.intactDAO.getSourceDao().getByQuery(query, parameters, first, max));
        initialiseLazySource(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Source> fetchIntactObjects(String query, Map<String, Object> parameters, boolean loadLazyCollections) {
        List<Source> results =  new ArrayList<Source>(this.intactDAO.getSourceDao().getByQuery(query, parameters, 0, Integer.MAX_VALUE));
        initialiseLazySource(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(Source object) throws PersisterException, FinderException, SynchronizerException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        // we can synchronize the complex with the database now
        intactDAO.getSynchronizerContext().getSourceSynchronizer().synchronize(object, true);

        this.intactDAO.getSynchronizerContext().getSourceSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(Collection<? extends Source> objects) throws SynchronizerException, PersisterException, FinderException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        for (Source source : objects){
            // we can synchronize the complex with the database now
            intactDAO.getSynchronizerContext().getSourceSynchronizer().synchronize(source, true);
        }
        this.intactDAO.getSynchronizerContext().getSourceSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(Source object) throws PersisterException, FinderException, SynchronizerException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        this.intactDAO.getSynchronizerContext().getSourceSynchronizer().delete(object);
        this.intactDAO.getSynchronizerContext().getSourceSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(Collection<? extends Source> objects) throws SynchronizerException, PersisterException, FinderException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        for (Source source : objects){
            this.intactDAO.getSynchronizerContext().getSourceSynchronizer().delete(source);
        }
        this.intactDAO.getSynchronizerContext().getSourceSynchronizer().flush();
    }

    public IntactDao getIntactDao() {
        return intactDAO;
    }

    private void initialiseLazySource(boolean loadLazyCollections, List<Source> results) {
        if (loadLazyCollections){
            for (Source source : results){
                IntactUtils.initialiseSource((IntactSource) source);
            }
        }
    }
}
