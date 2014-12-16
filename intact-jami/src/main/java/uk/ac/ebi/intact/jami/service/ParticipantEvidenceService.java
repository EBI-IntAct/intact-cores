package uk.ac.ebi.intact.jami.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.InteractionEvidence;
import psidev.psi.mi.jami.model.ParticipantEvidence;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.interceptor.IntactTransactionSynchronization;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractionEvidence;
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
@Service(value = "participantEvidenceService")
@Lazy
public class ParticipantEvidenceService implements IntactService<ParticipantEvidence> {

    @Resource(name = "intactDao")
    private IntactDao intactDAO;
    @Resource(name = "intactTransactionSynchronization")
    private IntactTransactionSynchronization afterCommitExecutor;

    @Resource(name = "interactionEvidenceService")
    private InteractionEvidenceService interactionEvidenceService;

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll() {
        return this.intactDAO.getParticipantEvidenceDao().countAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<ParticipantEvidence> iterateAll() {
        return new IntactQueryResultIterator<ParticipantEvidence>(this);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ParticipantEvidence> fetchIntactObjects(int first, int max) {
        return new ArrayList<ParticipantEvidence>(this.intactDAO.getParticipantEvidenceDao().getAll("ac", first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll(String countQuery, Map<String, Object> parameters) {
        return this.intactDAO.getParticipantEvidenceDao().countByQuery(countQuery, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<ParticipantEvidence> iterateAll(String queryCount, String query, Map<String, Object> parameters) {
        return new IntactQueryResultIterator<ParticipantEvidence>(this, query, queryCount, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ParticipantEvidence> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max) {
        return new ArrayList<ParticipantEvidence>(this.intactDAO.getParticipantEvidenceDao().getByQuery(query, parameters, first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ParticipantEvidence> fetchIntactObjects(String query, Map<String, Object> parameters) {
        return new ArrayList<ParticipantEvidence>(this.intactDAO.getParticipantEvidenceDao().getByQuery(query, parameters, 0, Integer.MAX_VALUE));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<ParticipantEvidence> iterateAll(boolean loadLazyCollections) {
        return new IntactQueryResultIterator<ParticipantEvidence>(this, loadLazyCollections);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ParticipantEvidence> fetchIntactObjects(int first, int max, boolean loadLazyCollections) {
        List<ParticipantEvidence> results = new ArrayList<ParticipantEvidence>(this.intactDAO.getParticipantEvidenceDao().getAll("ac", first, max));
        initialiseLazyParticipant(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<ParticipantEvidence> iterateAll(String countQuery, String query, Map<String, Object> parameters, boolean loadLazyCollections) {
        return new IntactQueryResultIterator<ParticipantEvidence>(this, query, countQuery, parameters, loadLazyCollections);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ParticipantEvidence> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max, boolean loadLazyCollections) {
        List<ParticipantEvidence> results = new ArrayList<ParticipantEvidence>(this.intactDAO.getParticipantEvidenceDao().getByQuery(query, parameters, first, max));
        initialiseLazyParticipant(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<ParticipantEvidence> fetchIntactObjects(String query, Map<String, Object> parameters, boolean loadLazyCollections) {
        List<ParticipantEvidence> results = new ArrayList<ParticipantEvidence>(this.intactDAO.getParticipantEvidenceDao().getByQuery(query, parameters, 0, Integer.MAX_VALUE));
        initialiseLazyParticipant(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(ParticipantEvidence object) throws PersisterException, FinderException, SynchronizerException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        saveParticipant(object);
        this.intactDAO.getSynchronizerContext().getParticipantEvidenceSynchronizer().flush();
    }

    protected void saveParticipant(ParticipantEvidence object) throws FinderException, PersisterException, SynchronizerException {
        // if the participant has an interaction, we may have to persist the interaction first
        if (object.getInteraction() != null && (!(object.getInteraction() instanceof IntactInteractionEvidence)
                || intactDAO.getInteractionDao().isTransient((IntactInteractionEvidence)object.getInteraction()))){
           InteractionEvidence interaction = object.getInteraction();
           this.interactionEvidenceService.saveInteraction(interaction);
        }

        // we can synchronize the participant with the database now
        intactDAO.getSynchronizerContext().getParticipantEvidenceSynchronizer().synchronize(object, true);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(Collection<? extends ParticipantEvidence> objects) throws SynchronizerException, PersisterException, FinderException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        for (ParticipantEvidence interaction : objects){
            saveParticipant(interaction);
        }
        this.intactDAO.getSynchronizerContext().getParticipantEvidenceSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(ParticipantEvidence object) throws PersisterException, FinderException, SynchronizerException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        this.intactDAO.getSynchronizerContext().getParticipantEvidenceSynchronizer().delete(object);
        this.intactDAO.getSynchronizerContext().getParticipantEvidenceSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(Collection<? extends ParticipantEvidence> objects) throws SynchronizerException, PersisterException, FinderException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        for (ParticipantEvidence participant : objects){
            this.intactDAO.getSynchronizerContext().getParticipantEvidenceSynchronizer().delete(participant);
        }
        this.intactDAO.getSynchronizerContext().getParticipantEvidenceSynchronizer().flush();
    }

    private void initialiseLazyParticipant(boolean loadLazyCollections, List<ParticipantEvidence> results) {
        if (loadLazyCollections){
            for (ParticipantEvidence participant : results){
                IntactUtils.initialiseParticipantEvidence((IntactParticipantEvidence) participant);
            }
        }
    }
}
