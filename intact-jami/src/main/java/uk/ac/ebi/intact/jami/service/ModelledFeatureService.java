package uk.ac.ebi.intact.jami.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.ModelledFeature;
import psidev.psi.mi.jami.model.ModelledParticipant;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.interceptor.IntactTransactionSynchronization;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledFeature;
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
@Service(value = "featureEvidenceService")
@Lazy
public class ModelledFeatureService implements IntactService<ModelledFeature> {

    @Resource(name = "intactDao")
    private IntactDao intactDAO;
    @Resource(name = "intactTransactionSynchronization")
    private IntactTransactionSynchronization afterCommitExecutor;

    @Resource(name = "modelledParticipantService")
    private ModelledParticipantService modelledParticipantService;

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll() {
        return this.intactDAO.getModelledFeatureDao().countAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<ModelledFeature> iterateAll() {
        return new IntactQueryResultIterator<ModelledFeature>(this);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ModelledFeature> fetchIntactObjects(int first, int max) {
        return new ArrayList<ModelledFeature>(this.intactDAO.getModelledFeatureDao().getAll("ac", first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll(String countQuery, Map<String, Object> parameters) {
        return this.intactDAO.getFeatureEvidenceDao().countByQuery(countQuery, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<ModelledFeature> iterateAll(String queryCount, String query, Map<String, Object> parameters) {
        return new IntactQueryResultIterator<ModelledFeature>(this, query, queryCount, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ModelledFeature> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max) {
        return new ArrayList<ModelledFeature>(this.intactDAO.getModelledFeatureDao().getByQuery(query, parameters, first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ModelledFeature> fetchIntactObjects(String query, Map<String, Object> parameters) {
        return new ArrayList<ModelledFeature>(this.intactDAO.getModelledFeatureDao().getByQuery(query, parameters, 0, Integer.MAX_VALUE));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<ModelledFeature> iterateAll(boolean loadLazyCollections) {
        return new IntactQueryResultIterator<ModelledFeature>(this, loadLazyCollections);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ModelledFeature> fetchIntactObjects(int first, int max, boolean loadLazyCollections) {
        List<ModelledFeature> results = new ArrayList<ModelledFeature>(this.intactDAO.getModelledFeatureDao().getAll("ac", first, max));
        initialiseLazyFeature(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<ModelledFeature> iterateAll(String countQuery, String query, Map<String, Object> parameters, boolean loadLazyCollections) {
        return new IntactQueryResultIterator<ModelledFeature>(this, query, countQuery, parameters, loadLazyCollections);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ModelledFeature> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max, boolean loadLazyCollections) {
        List<ModelledFeature> results = new ArrayList<ModelledFeature>(this.intactDAO.getModelledFeatureDao().getByQuery(query, parameters, first, max));
        initialiseLazyFeature(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ModelledFeature> fetchIntactObjects(String query, Map<String, Object> parameters, boolean loadLazyCollections) {
        List<ModelledFeature> results = new ArrayList<ModelledFeature>(this.intactDAO.getModelledFeatureDao().getByQuery(query, parameters, 0, Integer.MAX_VALUE));
        initialiseLazyFeature(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(ModelledFeature object) throws PersisterException, FinderException, SynchronizerException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        saveFeature(object);
        this.intactDAO.getSynchronizerContext().getModelledFeatureSynchronizer().flush();
    }

    protected void saveFeature(ModelledFeature object) throws FinderException, PersisterException, SynchronizerException {
        // if the feature has a participant, we may have to persist the participant first
        if (object.getParticipant() != null && (!(object.getParticipant() instanceof IntactModelledParticipant && object.getParticipant() instanceof ModelledParticipant)
                || intactDAO.getModelledParticipantDao().isTransient((IntactModelledParticipant)object.getParticipant()))){
            ModelledParticipant participant = (ModelledParticipant)object.getParticipant();
            this.modelledParticipantService.saveParticipant(participant);
        }

        // we can synchronize the feature with the database now
        intactDAO.getSynchronizerContext().getModelledFeatureSynchronizer().synchronize(object, true);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(Collection<? extends ModelledFeature> objects) throws SynchronizerException, PersisterException, FinderException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        for (ModelledFeature feature : objects){
            saveFeature(feature);
        }
        this.intactDAO.getSynchronizerContext().getModelledFeatureSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(ModelledFeature object) throws PersisterException, FinderException, SynchronizerException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        this.intactDAO.getSynchronizerContext().getModelledFeatureSynchronizer().delete(object);
        this.intactDAO.getSynchronizerContext().getModelledFeatureSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(Collection<? extends ModelledFeature> objects) throws SynchronizerException, PersisterException, FinderException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        for (ModelledFeature feature : objects){
            this.intactDAO.getSynchronizerContext().getModelledFeatureSynchronizer().delete(feature);
        }
        this.intactDAO.getSynchronizerContext().getModelledFeatureSynchronizer().flush();
    }

    private void initialiseLazyFeature(boolean loadLazyCollections, List<ModelledFeature> results) {
        if (loadLazyCollections){
            for (ModelledFeature feature : results){
                IntactUtils.initialiseModelledFeature((IntactModelledFeature) feature);
            }
        }
    }
}
