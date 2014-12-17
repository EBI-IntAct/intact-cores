package uk.ac.ebi.intact.jami.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.interceptor.IntactTransactionSynchronization;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledParticipant;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * Interaction evidence service
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/02/14</pre>
 */
@Service(value = "modelledParticipantService")
@Lazy
public class ModelledParticipantService implements IntactService<ModelledParticipant> {

    @Resource(name = "intactDao")
    private IntactDao intactDAO;
    @Resource(name = "intactTransactionSynchronization")
    private IntactTransactionSynchronization afterCommitExecutor;

    @Resource(name = "modelledInteractionService")
    private ModelledInteractionService modelledInteractionService;

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll() {
        return this.intactDAO.getModelledParticipantDao().countAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<ModelledParticipant> iterateAll() {
        return new IntactQueryResultIterator<ModelledParticipant>(this);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ModelledParticipant> fetchIntactObjects(int first, int max) {
        return new ArrayList<ModelledParticipant>(this.intactDAO.getModelledParticipantDao().getAll("ac", first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll(String countQuery, Map<String, Object> parameters) {
        return this.intactDAO.getParticipantEvidenceDao().countByQuery(countQuery, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<ModelledParticipant> iterateAll(String queryCount, String query, Map<String, Object> parameters) {
        return new IntactQueryResultIterator<ModelledParticipant>(this, query, queryCount, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ModelledParticipant> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max) {
        return new ArrayList<ModelledParticipant>(this.intactDAO.getModelledParticipantDao().getByQuery(query, parameters, first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ModelledParticipant> fetchIntactObjects(String query, Map<String, Object> parameters) {
        return new ArrayList<ModelledParticipant>(this.intactDAO.getModelledParticipantDao().getByQuery(query, parameters, 0, Integer.MAX_VALUE));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<ModelledParticipant> iterateAll(boolean loadLazyCollections) {
        return new IntactQueryResultIterator<ModelledParticipant>(this, loadLazyCollections);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ModelledParticipant> fetchIntactObjects(int first, int max, boolean loadLazyCollections) {
        List<ModelledParticipant> results = new ArrayList<ModelledParticipant>(this.intactDAO.getModelledParticipantDao().getAll("ac", first, max));
        initialiseLazyParticipant(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<ModelledParticipant> iterateAll(String countQuery, String query, Map<String, Object> parameters, boolean loadLazyCollections) {
        return new IntactQueryResultIterator<ModelledParticipant>(this, query, countQuery, parameters, loadLazyCollections);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ModelledParticipant> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max, boolean loadLazyCollections) {
        List<ModelledParticipant> results = new ArrayList<ModelledParticipant>(this.intactDAO.getModelledParticipantDao().getByQuery(query, parameters, first, max));
        initialiseLazyParticipant(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ModelledParticipant> fetchIntactObjects(String query, Map<String, Object> parameters, boolean loadLazyCollections) {
        List<ModelledParticipant> results = new ArrayList<ModelledParticipant>(this.intactDAO.getModelledParticipantDao().getByQuery(query, parameters, 0, Integer.MAX_VALUE));
        initialiseLazyParticipant(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(ModelledParticipant object) throws PersisterException, FinderException, SynchronizerException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        saveParticipant(object);
        this.intactDAO.getSynchronizerContext().getModelledParticipantSynchronizer().flush();
    }

    protected IntactModelledParticipant saveParticipant(ModelledParticipant object) throws FinderException, PersisterException, SynchronizerException {
        // if the participant has an interaction, we may have to persist the interaction first
        if (object.getInteraction() != null && (!(object.getInteraction() instanceof IntactComplex)
                || intactDAO.getComplexDao().isTransient((IntactComplex)object.getInteraction()))){
           ModelledInteraction interaction = object.getInteraction();
           object.setInteraction(this.modelledInteractionService.saveModelledInteraction(interaction));
        }

        // we can synchronize the participant with the database now
        return intactDAO.getSynchronizerContext().getModelledParticipantSynchronizer().synchronize(object, true);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(Collection<? extends ModelledParticipant> objects) throws SynchronizerException, PersisterException, FinderException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        for (ModelledParticipant participant : objects){
            saveParticipant(participant);
        }
        this.intactDAO.getSynchronizerContext().getModelledParticipantSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(ModelledParticipant object) throws PersisterException, FinderException, SynchronizerException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        this.intactDAO.getSynchronizerContext().getModelledParticipantSynchronizer().delete(object);
        this.intactDAO.getSynchronizerContext().getModelledParticipantSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(Collection<? extends ModelledParticipant> objects) throws SynchronizerException, PersisterException, FinderException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        for (ModelledParticipant participant : objects){
            this.intactDAO.getSynchronizerContext().getModelledParticipantSynchronizer().delete(participant);
        }
        this.intactDAO.getSynchronizerContext().getModelledParticipantSynchronizer().flush();
    }

    private void initialiseLazyParticipant(boolean loadLazyCollections, List<ModelledParticipant> results) {
        if (loadLazyCollections){
            for (ModelledParticipant participant : results){
                IntactUtils.initialiseModelledParticipant((IntactModelledParticipant) participant);
            }
        }
    }
}
