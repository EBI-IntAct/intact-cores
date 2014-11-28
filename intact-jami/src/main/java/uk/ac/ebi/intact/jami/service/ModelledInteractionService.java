package uk.ac.ebi.intact.jami.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Complex;
import psidev.psi.mi.jami.model.ModelledInteraction;
import psidev.psi.mi.jami.utils.clone.InteractorCloner;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.interceptor.IntactTransactionSynchronization;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * Modelled interaction service
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/02/14</pre>
 */
@Service(value = "modelledInteractionService")
@Lazy
public class ModelledInteractionService implements IntactService<ModelledInteraction>{

    @Resource(name = "intactDao")
    private IntactDao intactDAO;
    @Resource(name = "intactTransactionSynchronization")
    private IntactTransactionSynchronization afterCommitExecutor;

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll() {
        return this.intactDAO.getComplexDao().countAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<ModelledInteraction> iterateAll() {
        return new IntactQueryResultIterator<ModelledInteraction>(this);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ModelledInteraction> fetchIntactObjects(int first, int max) {
        return new ArrayList<ModelledInteraction>(this.intactDAO.getComplexDao().getAll("ac", first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll(String countQuery, Map<String, Object> parameters) {
        return this.intactDAO.getComplexDao().countByQuery(countQuery, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<ModelledInteraction> iterateAll(String queryCount, String query, Map<String, Object> parameters) {
        return new IntactQueryResultIterator<ModelledInteraction>(this, query, queryCount, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ModelledInteraction> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max) {
        return new ArrayList<ModelledInteraction>(this.intactDAO.getComplexDao().getByQuery(query, parameters, first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ModelledInteraction> fetchIntactObjects(String query, Map<String, Object> parameters) {
        return new ArrayList<ModelledInteraction>(this.intactDAO.getComplexDao().getByQuery(query, parameters, 0, Integer.MAX_VALUE));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<ModelledInteraction> iterateAll(boolean loadLazyCollections) {
        return new IntactQueryResultIterator<ModelledInteraction>(this, loadLazyCollections);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ModelledInteraction> fetchIntactObjects(int first, int max, boolean loadLazyCollections) {
        List<ModelledInteraction> results = new ArrayList<ModelledInteraction>(this.intactDAO.getComplexDao().getAll("ac", first, max));
        initialiseLazyInteraction(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<ModelledInteraction> iterateAll(String countQuery, String query, Map<String, Object> parameters, boolean loadLazyCollections) {
        return new IntactQueryResultIterator<ModelledInteraction>(this, query, countQuery, parameters, loadLazyCollections);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ModelledInteraction> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max, boolean loadLazyCollections) {
        List<ModelledInteraction> results = new ArrayList<ModelledInteraction>(this.intactDAO.getComplexDao().getByQuery(query, parameters, first, max));
        initialiseLazyInteraction(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ModelledInteraction> fetchIntactObjects(String query, Map<String, Object> parameters, boolean loadLazyCollections) {
        List<ModelledInteraction> results = new ArrayList<ModelledInteraction>(this.intactDAO.getComplexDao().getByQuery(query, parameters, 0, Integer.MAX_VALUE));
        initialiseLazyInteraction(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(ModelledInteraction object) throws PersisterException, FinderException, SynchronizerException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);

        saveModelledInteraction(object);
        this.intactDAO.getSynchronizerContext().getComplexSynchronizer().flush();
    }

    protected void saveModelledInteraction(ModelledInteraction object) throws FinderException, PersisterException, SynchronizerException {
        Complex complex;
        if (!(object instanceof Complex)){
            complex = new IntactComplex(object.getShortName() != null ? object.getShortName() : "unknown");
            InteractorCloner.copyAndOverrideBasicComplexPropertiesWithModelledInteractionProperties(object, complex);
        }
        else{
            complex = (Complex)object;
        }
        // we can synchronize the complex with the database now
        intactDAO.getSynchronizerContext().getComplexSynchronizer().synchronize(complex, true);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(Collection<? extends ModelledInteraction> objects) throws SynchronizerException, PersisterException, FinderException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);

        for (ModelledInteraction interaction : objects){
            saveModelledInteraction(interaction);
        }
        this.intactDAO.getSynchronizerContext().getComplexSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(ModelledInteraction object) throws PersisterException, FinderException, SynchronizerException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);

        if (object instanceof Complex){
            this.intactDAO.getSynchronizerContext().getComplexSynchronizer().delete((Complex)object);
        }
        this.intactDAO.getSynchronizerContext().getComplexSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(Collection<? extends ModelledInteraction> objects) throws SynchronizerException, PersisterException, FinderException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);

        for (ModelledInteraction interaction : objects){
            if (interaction instanceof Complex){
                this.intactDAO.getSynchronizerContext().getComplexSynchronizer().delete((Complex)interaction);
            }
        }
        this.intactDAO.getSynchronizerContext().getComplexSynchronizer().flush();
    }

    private void initialiseLazyInteraction(boolean loadLazyCollections, List<ModelledInteraction> results) {
        if (loadLazyCollections){
            for (ModelledInteraction interactor : results){
                IntactUtils.initialiseInteractor((IntactComplex) interactor);
            }
        }
    }
}
