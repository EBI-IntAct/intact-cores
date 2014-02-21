package uk.ac.ebi.intact.jami.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Experiment;
import psidev.psi.mi.jami.model.InteractionEvidence;
import psidev.psi.mi.jami.model.Publication;
import psidev.psi.mi.jami.utils.clone.ExperimentCloner;
import psidev.psi.mi.jami.utils.clone.PublicationCloner;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.model.extension.IntactCuratedPublication;
import uk.ac.ebi.intact.jami.model.extension.IntactExperiment;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Interaction evidence service
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/02/14</pre>
 */
@Service
public class InteractionEvidenceService implements IntactService<InteractionEvidence> {

    @Autowired
    private IntactDao intactDAO;

    @Transactional(propagation = Propagation.REQUIRED)
    public List<InteractionEvidence> fetchIntactObjects(String query, Map<String, Object> queryParameters, int first, int max) {
        if (query == null){
            return new ArrayList<InteractionEvidence>(this.intactDAO.getInteractionDao().getAll("ac", first, max));
        }
        return new ArrayList<InteractionEvidence>(this.intactDAO.getInteractionDao().getByQuery(query, queryParameters, first, max));
    }

    @Transactional(propagation = Propagation.REQUIRED)
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
                   if (!(pub instanceof IntactCuratedPublication)
                           || intactDAO.getCuratedPublicationDao().isTransient((IntactCuratedPublication)pub)){
                       IntactCuratedPublication intactCuratedPub = new IntactCuratedPublication();
                       PublicationCloner.copyAndOverridePublicationProperties(pub, intactCuratedPub);

                       intactDAO.getCuratedPublicationDao().persist(intactCuratedPub);
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

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdate(Collection<? extends InteractionEvidence> objects) throws SynchronizerException, PersisterException, FinderException {
        for (InteractionEvidence interaction : objects){
            saveOrUpdate(interaction);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(InteractionEvidence object) throws PersisterException, FinderException, SynchronizerException {

        this.intactDAO.getSynchronizerContext().getInteractionSynchronizer().delete(object);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Collection<? extends InteractionEvidence> objects) throws SynchronizerException, PersisterException, FinderException {
        for (InteractionEvidence interaction : objects){
            delete(interaction);
        }
    }
}
