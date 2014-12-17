package uk.ac.ebi.intact.jami.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Experiment;
import psidev.psi.mi.jami.model.InteractionEvidence;
import psidev.psi.mi.jami.model.Publication;
import psidev.psi.mi.jami.utils.clone.ExperimentCloner;
import psidev.psi.mi.jami.utils.clone.PublicationCloner;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.interceptor.IntactTransactionSynchronization;
import uk.ac.ebi.intact.jami.model.extension.IntactExperiment;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractionEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;
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
@Service(value = "interactionEvidenceService")
@Lazy
public class InteractionEvidenceService implements IntactService<InteractionEvidence> {

    @Resource(name = "intactDao")
    private IntactDao intactDAO;
    @Resource(name = "intactTransactionSynchronization")
    private IntactTransactionSynchronization afterCommitExecutor;

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll() {
        return this.intactDAO.getInteractionDao().countAll();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<InteractionEvidence> iterateAll() {
        return new IntactQueryResultIterator<InteractionEvidence>(this);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<InteractionEvidence> fetchIntactObjects(int first, int max) {
        return new ArrayList<InteractionEvidence>(this.intactDAO.getInteractionDao().getAll("ac", first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public long countAll(String countQuery, Map<String, Object> parameters) {
        return this.intactDAO.getInteractionDao().countByQuery(countQuery, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<InteractionEvidence> iterateAll(String queryCount, String query, Map<String, Object> parameters) {
        return new IntactQueryResultIterator<InteractionEvidence>(this, query, queryCount, parameters);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<InteractionEvidence> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max) {
        return new ArrayList<InteractionEvidence>(this.intactDAO.getInteractionDao().getByQuery(query, parameters, first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<InteractionEvidence> fetchIntactObjects(String query, Map<String, Object> parameters) {
        return new ArrayList<InteractionEvidence>(this.intactDAO.getInteractionDao().getByQuery(query, parameters, 0, Integer.MAX_VALUE));
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<InteractionEvidence> iterateAll(boolean loadLazyCollections) {
        return new IntactQueryResultIterator<InteractionEvidence>(this, loadLazyCollections);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<InteractionEvidence> fetchIntactObjects(int first, int max, boolean loadLazyCollections) {
        List<InteractionEvidence> results = new ArrayList<InteractionEvidence>(this.intactDAO.getInteractionDao().getAll("ac", first, max));
        initialiseLazyInteraction(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public Iterator<InteractionEvidence> iterateAll(String countQuery, String query, Map<String, Object> parameters, boolean loadLazyCollections) {
        return new IntactQueryResultIterator<InteractionEvidence>(this, query, countQuery, parameters, loadLazyCollections);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<InteractionEvidence> fetchIntactObjects(String query, Map<String, Object> parameters, int first, int max, boolean loadLazyCollections) {
        List<InteractionEvidence> results = new ArrayList<InteractionEvidence>(this.intactDAO.getInteractionDao().getByQuery(query, parameters, first, max));
        initialiseLazyInteraction(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager", readOnly = true)
    public List<InteractionEvidence> fetchIntactObjects(String query, Map<String, Object> parameters, boolean loadLazyCollections) {
        List<InteractionEvidence> results = new ArrayList<InteractionEvidence>(this.intactDAO.getInteractionDao().getByQuery(query, parameters, 0, Integer.MAX_VALUE));
        initialiseLazyInteraction(loadLazyCollections, results);
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(InteractionEvidence object) throws PersisterException, FinderException, SynchronizerException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        saveInteraction(object);
        this.intactDAO.getSynchronizerContext().getInteractionSynchronizer().flush();
    }

    protected void saveInteraction(InteractionEvidence object) throws FinderException, PersisterException, SynchronizerException {
        IntactExperiment curatedExperiment = null;
        // if the interaction has an experiment, we may have to persist the experiment first
        if (object.getExperiment() != null && (!(object.getExperiment() instanceof IntactExperiment)
                || intactDAO.getExperimentDao().isTransient((IntactExperiment)object.getExperiment()))){
           Experiment exp = object.getExperiment();
           IntactPublication intactCuratedPub = null;
            // if the experiment has a publication, we may have to persist the publication first
           if (exp.getPublication() != null && (!(exp.getPublication() instanceof IntactPublication)
                   || intactDAO.getPublicationDao().isTransient((IntactPublication)exp.getPublication()))){
               Publication pub = exp.getPublication();
               // create publication first in the database if not done
               if (pub != null){
                   intactCuratedPub = new IntactPublication();
                   PublicationCloner.copyAndOverridePublicationProperties(pub, intactCuratedPub);

                   intactDAO.getPublicationDao().persist(intactCuratedPub);
               }
           }

            curatedExperiment = new IntactExperiment(null);
            ExperimentCloner.copyAndOverrideExperimentProperties(exp, curatedExperiment);
            if (intactCuratedPub != null){
               curatedExperiment.setPublication(intactCuratedPub);
            }

            // create experiment in database if not done
            intactDAO.getExperimentDao().persist(curatedExperiment);
            object.setExperimentAndAddInteractionEvidence(curatedExperiment);
        }

        // we can synchronize the interaction with the database now
        intactDAO.getSynchronizerContext().getInteractionSynchronizer().synchronize(object, true);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(Collection<? extends InteractionEvidence> objects) throws SynchronizerException, PersisterException, FinderException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        for (InteractionEvidence interaction : objects){
            saveInteraction(interaction);
        }
        this.intactDAO.getSynchronizerContext().getInteractionSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(InteractionEvidence object) throws PersisterException, FinderException, SynchronizerException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        this.intactDAO.getSynchronizerContext().getInteractionSynchronizer().delete(object);
        this.intactDAO.getSynchronizerContext().getInteractionSynchronizer().flush();
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(Collection<? extends InteractionEvidence> objects) throws SynchronizerException, PersisterException, FinderException {
        afterCommitExecutor.registerDaoForSynchronization(intactDAO);
        for (InteractionEvidence interaction : objects){
            this.intactDAO.getSynchronizerContext().getInteractionSynchronizer().delete(interaction);
        }
        this.intactDAO.getSynchronizerContext().getInteractionSynchronizer().flush();
    }

    private void initialiseLazyInteraction(boolean loadLazyCollections, List<InteractionEvidence> results) {
        if (loadLazyCollections){
            for (InteractionEvidence interaction : results){
                IntactUtils.initialiseInteractionEvidence((IntactInteractionEvidence) interaction, true);
            }
        }
    }
}
