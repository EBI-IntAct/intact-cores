package uk.ac.ebi.intact.jami.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Organism;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.interceptor.IntactTransactionSynchronization;
import uk.ac.ebi.intact.jami.model.extension.IntactOrganism;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import java.util.*;

/**
 * Organism service
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/02/14</pre>
 */
@Service(value = "organismService")
@Lazy
public class OrganismService implements IntactService<Organism>{

    @Autowired
    @Qualifier("intactDao")
    private IntactDao intactDAO;
    @Autowired
    @Qualifier("intactTransactionSynchronization")
    private IntactTransactionSynchronization afterCommitExecutor;

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll() {
        return this.intactDAO.getOrganismDao().countAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Organism> iterateAll() {
        return new IntactQueryResultIterator<Organism>(this);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Organism> fetchIntactObjects(int first, int max) {
        return new ArrayList<Organism>(this.intactDAO.getOrganismDao().getAll("ac", first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll(String countQuery, Map<String, Object> parameters) {
        return this.intactDAO.getOrganismDao().countByQuery(countQuery, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Organism> iterateAll(String queryCount, String query, Map<String, Object> parameters) {
        return new IntactQueryResultIterator<Organism>(this, query, queryCount, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Organism> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max) {
        return new ArrayList<Organism>(this.intactDAO.getOrganismDao().getByQuery(query, parameters, first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Organism> fetchIntactObjects(String query, Map<String, Object> parameters) {
        return new ArrayList<Organism>(this.intactDAO.getOrganismDao().getByQuery(query, parameters, 0, Integer.MAX_VALUE));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Organism> iterateAll(boolean loadLazyCollections) {
        return new IntactQueryResultIterator<Organism>(this, loadLazyCollections);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Organism> fetchIntactObjects(int first, int max, boolean loadLazyCollections) {
        List<Organism> results = new ArrayList<Organism>(this.intactDAO.getOrganismDao().getAll("ac", first, max));
        initialiseLazyOrganism(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<Organism> iterateAll(String countQuery, String query, Map<String, Object> parameters, boolean loadLazyCollections) {
        return new IntactQueryResultIterator<Organism>(this, query, countQuery, parameters, loadLazyCollections);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Organism> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max, boolean loadLazyCollections) {
        List<Organism> results =  new ArrayList<Organism>(this.intactDAO.getOrganismDao().getByQuery(query, parameters, first, max));
        initialiseLazyOrganism(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<Organism> fetchIntactObjects(String query, Map<String, Object> parameters, boolean loadLazyCollections) {
        List<Organism> results =  new ArrayList<Organism>(this.intactDAO.getOrganismDao().getByQuery(query, parameters, 0, Integer.MAX_VALUE));
        initialiseLazyOrganism(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(Organism object) throws PersisterException, FinderException, SynchronizerException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);

        // we can synchronize the complex with the database now
        intactDAO.getSynchronizerContext().getOrganismSynchronizer().synchronize(object, true);
        this.intactDAO.getSynchronizerContext().getOrganismSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(Collection<? extends Organism> objects) throws SynchronizerException, PersisterException, FinderException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);

        for (Organism org : objects){
            // we can synchronize the complex with the database now
            intactDAO.getSynchronizerContext().getOrganismSynchronizer().synchronize(org, true);
        }
        this.intactDAO.getSynchronizerContext().getOrganismSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(Organism object) throws PersisterException, FinderException, SynchronizerException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);

        this.intactDAO.getSynchronizerContext().getOrganismSynchronizer().delete(object);
        this.intactDAO.getSynchronizerContext().getOrganismSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(Collection<? extends Organism> objects) throws SynchronizerException, PersisterException, FinderException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);

        for (Organism org : objects){
            this.intactDAO.getSynchronizerContext().getOrganismSynchronizer().delete(org);
        }
        this.intactDAO.getSynchronizerContext().getOrganismSynchronizer().flush();
    }

    private void initialiseLazyOrganism(boolean loadLazyCollections, List<Organism> results) {
        if (loadLazyCollections){
            for (Organism organism : results){
                IntactUtils.initialiseOrganism((IntactOrganism) organism);
            }
        }
    }
}
