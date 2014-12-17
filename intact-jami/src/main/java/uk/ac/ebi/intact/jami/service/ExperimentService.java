package uk.ac.ebi.intact.jami.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Experiment;
import psidev.psi.mi.jami.model.Publication;
import psidev.psi.mi.jami.utils.clone.PublicationCloner;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.interceptor.IntactTransactionSynchronization;
import uk.ac.ebi.intact.jami.model.extension.IntactExperiment;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * Experiment service
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/02/14</pre>
 */
@Service(value = "experimentService")
@Lazy
public class ExperimentService implements IntactService<Experiment>{

    @Resource(name = "intactDao")
    private IntactDao intactDAO;
    @Autowired
    @Qualifier("intactTransactionSynchronization")
    private IntactTransactionSynchronization afterCommitExecutor;

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll() {
        return this.intactDAO.getExperimentDao().countAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Experiment> iterateAll() {
        return new IntactQueryResultIterator<Experiment>(this);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Experiment> fetchIntactObjects(int first, int max) {
        return new ArrayList<Experiment>(this.intactDAO.getExperimentDao().getAll("ac", first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll(String countQuery, Map<String, Object> parameters) {
        return this.intactDAO.getExperimentDao().countByQuery(countQuery, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Experiment> iterateAll(String queryCount, String query, Map<String, Object> parameters) {
        return new IntactQueryResultIterator<Experiment>(this, query, queryCount, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Experiment> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max) {
        return new ArrayList<Experiment>(this.intactDAO.getExperimentDao().getByQuery(query, parameters, first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Experiment> fetchIntactObjects(String query, Map<String, Object> parameters) {
        return new ArrayList<Experiment>(this.intactDAO.getExperimentDao().getByQuery(query, parameters, 0, Integer.MAX_VALUE));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Experiment> iterateAll(boolean loadLazyCollections) {
        return new IntactQueryResultIterator<Experiment>(this, loadLazyCollections);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Experiment> fetchIntactObjects(int first, int max, boolean loadLazyCollections) {
        List<Experiment> results = new ArrayList<Experiment>(this.intactDAO.getExperimentDao().getAll("ac", first, max));
        initialiseLazyExperiment(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Experiment> iterateAll(String countQuery, String query, Map<String, Object> parameters, boolean loadLazyCollections) {
        return new IntactQueryResultIterator<Experiment>(this, query, countQuery, parameters, loadLazyCollections);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Experiment> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max, boolean loadLazyCollections) {
        List<Experiment> results = new ArrayList<Experiment>(this.intactDAO.getExperimentDao().getByQuery(query, parameters, first, max));
        initialiseLazyExperiment(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Experiment> fetchIntactObjects(String query, Map<String, Object> parameters, boolean loadLazyCollections) {
        List<Experiment> results = new ArrayList<Experiment>(this.intactDAO.getExperimentDao().getByQuery(query, parameters, 0, Integer.MAX_VALUE));
        initialiseLazyExperiment(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(Experiment object) throws PersisterException, FinderException, SynchronizerException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        saveExperiment(object);
        this.intactDAO.getSynchronizerContext().getExperimentSynchronizer().flush();
    }

    protected IntactExperiment saveExperiment(Experiment object) throws FinderException, SynchronizerException, PersisterException {
        Publication pub = object.getPublication();
        // create publication first in the database if not done
        if (pub != null){
            // transient publication to persist first
            if (!(pub instanceof IntactPublication)
                    || intactDAO.getPublicationDao().isTransient((IntactPublication)pub)){
                IntactPublication intactCuratedPub = new IntactPublication();
                PublicationCloner.copyAndOverridePublicationProperties(pub, intactCuratedPub);

                object.setPublication(intactDAO.getSynchronizerContext().getPublicationSynchronizer().synchronize(intactCuratedPub, true));
            }
        }
        // we can synchronize the complex with the database now
        return intactDAO.getSynchronizerContext().getExperimentSynchronizer().synchronize(object, true);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(Collection<? extends Experiment> objects) throws SynchronizerException, PersisterException, FinderException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        for (Experiment exp : objects){
            saveExperiment(exp);
        }
        this.intactDAO.getSynchronizerContext().getExperimentSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Experiment object) throws PersisterException, FinderException, SynchronizerException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        this.intactDAO.getSynchronizerContext().getExperimentSynchronizer().delete(object);
        this.intactDAO.getSynchronizerContext().getExperimentSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Collection<? extends Experiment> objects) throws SynchronizerException, PersisterException, FinderException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        for (Experiment exp : objects){
            this.intactDAO.getSynchronizerContext().getExperimentSynchronizer().delete(exp);
        }
        this.intactDAO.getSynchronizerContext().getExperimentSynchronizer().flush();
    }

    private void initialiseLazyExperiment(boolean loadLazyCollections, List<Experiment> results) {
        if (loadLazyCollections){
            for (Experiment exp : results){
                IntactUtils.initialiseExperiment((IntactExperiment) exp, true);
            }
        }
    }
}
