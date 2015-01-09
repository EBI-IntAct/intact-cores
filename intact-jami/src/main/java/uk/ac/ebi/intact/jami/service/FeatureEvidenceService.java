package uk.ac.ebi.intact.jami.service;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.FeatureEvidence;
import psidev.psi.mi.jami.model.ParticipantEvidence;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.interceptor.IntactTransactionSynchronization;
import uk.ac.ebi.intact.jami.model.extension.IntactFeatureEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactParticipantEvidence;
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
@Scope( BeanDefinition.SCOPE_PROTOTYPE )
public class FeatureEvidenceService implements IntactService<FeatureEvidence> {

    @Resource(name = "intactDao")
    private IntactDao intactDAO;
    @Resource(name = "intactTransactionSynchronization")
    private IntactTransactionSynchronization afterCommitExecutor;

    @Resource(name = "participantEvidenceService")
    private ParticipantEvidenceService participantEvidenceService;

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll() {
        return this.intactDAO.getFeatureEvidenceDao().countAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<FeatureEvidence> iterateAll() {
        // use proxy and not this for transactional annotations to work
        return new IntactQueryResultIterator<FeatureEvidence>((FeatureEvidenceService) ApplicationContextProvider.getBean("featureEvidenceService"));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<FeatureEvidence> fetchIntactObjects(int first, int max) {
        return new ArrayList<FeatureEvidence>(this.intactDAO.getFeatureEvidenceDao().getAll("ac", first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll(String countQuery, Map<String, Object> parameters) {
        return this.intactDAO.getFeatureEvidenceDao().countByQuery(countQuery, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<FeatureEvidence> iterateAll(String queryCount, String query, Map<String, Object> parameters) {
        // use proxy and not this for transactional annotations to work
        return new IntactQueryResultIterator<FeatureEvidence>((FeatureEvidenceService) ApplicationContextProvider.getBean("featureEvidenceService"), query, queryCount, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<FeatureEvidence> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max) {
        return new ArrayList<FeatureEvidence>(this.intactDAO.getFeatureEvidenceDao().getByQuery(query, parameters, first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<FeatureEvidence> fetchIntactObjects(String query, Map<String, Object> parameters) {
        return new ArrayList<FeatureEvidence>(this.intactDAO.getFeatureEvidenceDao().getByQuery(query, parameters, 0, Integer.MAX_VALUE));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<FeatureEvidence> iterateAll(boolean loadLazyCollections) {
        // use proxy and not this for transactional annotations to work
        return new IntactQueryResultIterator<FeatureEvidence>((FeatureEvidenceService) ApplicationContextProvider.getBean("featureEvidenceService"), loadLazyCollections);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<FeatureEvidence> fetchIntactObjects(int first, int max, boolean loadLazyCollections) {
        List<FeatureEvidence> results = new ArrayList<FeatureEvidence>(this.intactDAO.getFeatureEvidenceDao().getAll("ac", first, max));
        initialiseLazyFeature(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<FeatureEvidence> iterateAll(String countQuery, String query, Map<String, Object> parameters, boolean loadLazyCollections) {
        // use proxy and not this for transactional annotations to work
        return new IntactQueryResultIterator<FeatureEvidence>((FeatureEvidenceService) ApplicationContextProvider.getBean("featureEvidenceService"), query, countQuery, parameters, loadLazyCollections);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<FeatureEvidence> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max, boolean loadLazyCollections) {
        List<FeatureEvidence> results = new ArrayList<FeatureEvidence>(this.intactDAO.getFeatureEvidenceDao().getByQuery(query, parameters, first, max));
        initialiseLazyFeature(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<FeatureEvidence> fetchIntactObjects(String query, Map<String, Object> parameters, boolean loadLazyCollections) {
        List<FeatureEvidence> results = new ArrayList<FeatureEvidence>(this.intactDAO.getFeatureEvidenceDao().getByQuery(query, parameters, 0, Integer.MAX_VALUE));
        initialiseLazyFeature(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(FeatureEvidence object) throws PersisterException, FinderException, SynchronizerException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        saveFeature(object);
        this.intactDAO.getSynchronizerContext().getFeatureEvidenceSynchronizer().flush();
    }

    protected IntactFeatureEvidence saveFeature(FeatureEvidence object) throws FinderException, PersisterException, SynchronizerException {
        // if the feature has a participant, we may have to persist the participant first
        if (object.getParticipant() != null && (!(object.getParticipant() instanceof IntactParticipantEvidence && object.getParticipant() instanceof ParticipantEvidence)
                || intactDAO.getParticipantEvidenceDao().isTransient((IntactParticipantEvidence)object.getParticipant()))){
            ParticipantEvidence participant = (ParticipantEvidence)object.getParticipant();
            object.setParticipant(this.participantEvidenceService.saveParticipant(participant));
        }

        // we can synchronize the feature with the database now
        return intactDAO.getSynchronizerContext().getFeatureEvidenceSynchronizer().synchronize(object, true);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(Collection<? extends FeatureEvidence> objects) throws SynchronizerException, PersisterException, FinderException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        for (FeatureEvidence feature : objects){
            saveFeature(feature);
        }
        this.intactDAO.getSynchronizerContext().getFeatureEvidenceSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(FeatureEvidence object) throws PersisterException, FinderException, SynchronizerException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        this.intactDAO.getSynchronizerContext().getFeatureEvidenceSynchronizer().delete(object);
        this.intactDAO.getSynchronizerContext().getFeatureEvidenceSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(Collection<? extends FeatureEvidence> objects) throws SynchronizerException, PersisterException, FinderException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        for (FeatureEvidence feature : objects){
            this.intactDAO.getSynchronizerContext().getFeatureEvidenceSynchronizer().delete(feature);
        }
        this.intactDAO.getSynchronizerContext().getFeatureEvidenceSynchronizer().flush();
    }

    public IntactDao getIntactDao() {
        return intactDAO;
    }

    private void initialiseLazyFeature(boolean loadLazyCollections, List<FeatureEvidence> results) {
        if (loadLazyCollections){
            for (FeatureEvidence feature : results){
                IntactUtils.initialiseFeatureEvidence((IntactFeatureEvidence) feature);
            }
        }
    }
}
