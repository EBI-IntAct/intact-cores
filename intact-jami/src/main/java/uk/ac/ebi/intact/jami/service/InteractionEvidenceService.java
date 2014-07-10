package uk.ac.ebi.intact.jami.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Experiment;
import psidev.psi.mi.jami.model.InteractionEvidence;
import psidev.psi.mi.jami.model.Publication;
import psidev.psi.mi.jami.utils.clone.ExperimentCloner;
import psidev.psi.mi.jami.utils.clone.PublicationCloner;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.model.extension.IntactExperiment;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

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
@EnableTransactionManagement
@Configuration
public class InteractionEvidenceService implements IntactService<InteractionEvidence> {

    @Autowired
    @Qualifier("intactDAO")
    private IntactDao intactDAO;

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

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(InteractionEvidence object) throws PersisterException, FinderException, SynchronizerException {
        // if the interaction has an experiment, we may have to persist the experiment first
        if (object.getExperiment() != null){
           Experiment exp = object.getExperiment();
            // if the experiment has a publication, we may have to persist the publication first
           if (exp.getPublication() != null){
               Publication pub = exp.getPublication();
               // create publication first in the database if not done
               if (pub != null){
                   // transcient publication to persist first
                   if (!(pub instanceof IntactPublication)
                           || intactDAO.getPublicationDao().isTransient((IntactPublication)pub)){
                       IntactPublication intactCuratedPub = new IntactPublication();
                       PublicationCloner.copyAndOverridePublicationProperties(pub, intactCuratedPub);

                       intactDAO.getPublicationDao().persist(intactCuratedPub);
                       intactCuratedPub.addExperiment(exp);
                   }
               }
               // transcient experiment to persist first
               if (!(exp instanceof IntactExperiment)
                       || intactDAO.getExperimentDao().isTransient((IntactExperiment)exp)){
                   IntactExperiment intactExperiment = new IntactExperiment(null);
                   ExperimentCloner.copyAndOverrideExperimentProperties(exp, intactExperiment);

                   intactDAO.getExperimentDao().persist(intactExperiment);
                   intactExperiment.addInteractionEvidence(object);
               }
           }
        }

        // we can synchronize the interaction with the database now
        intactDAO.getSynchronizerContext().getInteractionSynchronizer().synchronize(object, true);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void saveOrUpdate(Collection<? extends InteractionEvidence> objects) throws SynchronizerException, PersisterException, FinderException {
        for (InteractionEvidence interaction : objects){
            saveOrUpdate(interaction);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(InteractionEvidence object) throws PersisterException, FinderException, SynchronizerException {

        this.intactDAO.getSynchronizerContext().getInteractionSynchronizer().delete(object);
    }

    @Transactional(propagation = Propagation.REQUIRED, value = "jamiTransactionManager")
    public void delete(Collection<? extends InteractionEvidence> objects) throws SynchronizerException, PersisterException, FinderException {
        for (InteractionEvidence interaction : objects){
            delete(interaction);
        }
    }
}
