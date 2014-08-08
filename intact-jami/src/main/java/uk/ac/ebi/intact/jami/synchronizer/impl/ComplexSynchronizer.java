package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import psidev.psi.mi.jami.utils.clone.InteractorCloner;
import psidev.psi.mi.jami.utils.comparator.CollectionComparator;
import psidev.psi.mi.jami.utils.comparator.interactor.UnambiguousExactComplexComparator;
import psidev.psi.mi.jami.utils.comparator.participant.UnambiguousModelledParticipantComparator;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.ComplexMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleStatus;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

/**
 * Default synchronizer for complexes
 *
 * NOTE: when we want to persist cooperative effects, we would remove the transcient property in the IntActConmplex
 * and uncomment the prepareCooperativeEffects
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public class ComplexSynchronizer extends InteractorSynchronizerTemplate<Complex, IntactComplex>{

    private CollectionComparator<ModelledParticipant> participantsComparator;

    public ComplexSynchronizer(SynchronizerContext context) {
        super(context, IntactComplex.class);
        this.participantsComparator = new CollectionComparator<ModelledParticipant>(new UnambiguousModelledParticipantComparator());
    }

    @Override
    protected IntactComplex postFilter(Complex term, Collection<IntactComplex> results) {
        Collection<IntactComplex> filteredResults = new ArrayList<IntactComplex>(results.size());
        for (IntactComplex complex : results){
            // we accept empty participants when finding complexes
            if (term.getParticipants().isEmpty()){
                filteredResults.add(complex);
            }
            // same participants
            else if (this.participantsComparator.compare(term.getParticipants(), complex.getParticipants()) == 0){
                filteredResults.add(complex);
            }
        }

        if (filteredResults.size() == 1){
            return filteredResults.iterator().next();
        }
        else{
            return null;
        }
    }

    @Override
    protected Collection<IntactComplex> findByOtherProperties(Complex term, IntactCvTerm existingType, IntactOrganism existingOrganism) {
        Query query;
        if (existingOrganism == null){
            query = getEntityManager().createQuery("select i from IntactComplex i " +
                    "join i.interactorType as t " +
                    "where i.organism is null " +
                    "and size(i.participants) =:participantSize " +
                    "and t.ac = :typeAc");
            query.setParameter("typeAc", existingType.getAc());
            query.setParameter("participantSize", term.getParticipants().size());
        }
        else{
            query = getEntityManager().createQuery("select i from "+getIntactClass().getSimpleName()+" i " +
                    "join i.interactorType as t " +
                    "join i.organism as o " +
                    "where o.ac = :orgAc " +
                    "and size(i.participants) =:participantSize " +
                    "and t.ac = :typeAc");
            query.setParameter("orgAc", existingOrganism.getAc());
            query.setParameter("participantSize", term.getParticipants().size());
            query.setParameter("typeAc", existingType.getAc());
        }
        return query.getResultList();
    }

    @Override
    protected void initialisePersistedObjectMap() {
        super.setPersistedObjects(new TreeMap<Complex, IntactComplex>(new UnambiguousExactComplexComparator()));
    }

    @Override
    public void synchronizeProperties(IntactComplex intactComplex) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizeProperties(intactComplex);
        // prepare evidence type
        prepareEvidenceType(intactComplex);
        // prepare interaction evidences
        //prepareInteractionEvidences(intactComplex);
        // then check confidences
        prepareConfidences(intactComplex);
        // then check parameters
        prepareParameters(intactComplex);
        // then check participants
        prepareParticipants(intactComplex);
        // then check cooperative effects
        //prepareCooperativeEffects(intactComplex);
        // prepare status
        prepareStatusAndCurators(intactComplex);
        // prepare lifecycle
        prepareLifeCycleEvents(intactComplex);
        // then prepare experiment for backward compatibility
        prepareExperiments(intactComplex);
    }

    @Override
    protected void prepareAnnotations(IntactComplex intactInteractor) throws FinderException, PersisterException, SynchronizerException {
        if (intactInteractor.areAnnotationsInitialized()){
            if (AnnotationUtils.collectFirstAnnotationWithTopic(intactInteractor.getAnnotations(), null, "curated-complex") == null){
                intactInteractor.getAnnotations().add(new InteractorAnnotation(IntactUtils.createMITopic("curated-complex", null)));
            }
        }
        super.prepareAnnotations(intactInteractor);
    }

    protected void prepareEvidenceType(IntactComplex intactComplex) throws PersisterException, FinderException, SynchronizerException {

       if (intactComplex.getEvidenceType() != null){
           intactComplex.setEvidenceType(getContext().getDatabaseSynchronizer().synchronize(intactComplex.getEvidenceType(), true));
       }
    }

    /*protected void prepareInteractionEvidences(IntactComplex intactComplex) throws PersisterException, FinderException, SynchronizerException {
        if (intactComplex.areInteractionEvidencesInitialized()){
            Collection<InteractionEvidence> evidencesToPersist = new ArrayList<InteractionEvidence>(intactComplex.getInteractionEvidences());
            for (InteractionEvidence interaction : evidencesToPersist){
                // do not persist or merge interaction evidences
                InteractionEvidence persistetnInter = getContext().getInteractionSynchronizer().synchronize(interaction, false);
                // we have a different instance because needed to be synchronized
                if (persistetnInter != interaction){
                    intactComplex.getInteractionEvidences().remove(interaction);
                    intactComplex.getInteractionEvidences().add(persistetnInter);
                }
            }
        }
    }*/

    protected void prepareStatusAndCurators(IntactComplex intactComplex) throws PersisterException, FinderException, SynchronizerException {

        // first the status
        CvTerm status = intactComplex.getStatus().toCvTerm();
        intactComplex.setCvStatus(getContext().getLifecycleStatusSynchronizer().synchronize(status, true));

        // then curator
        User curator = intactComplex.getCurrentOwner();
        // do not persist user if not there
        if (curator != null){
            intactComplex.setCurrentOwner(getContext().getUserReadOnlySynchronizer().synchronize(curator, false));
        }

        // then reviewer
        User reviewer = intactComplex.getCurrentReviewer();
        if (reviewer != null){
            intactComplex.setCurrentReviewer(getContext().getUserReadOnlySynchronizer().synchronize(reviewer, false));
        }
    }

    protected void prepareLifeCycleEvents(IntactComplex intactComplex) throws PersisterException, FinderException, SynchronizerException {

        if (intactComplex.areLifeCycleEventsInitialized()){
            List<LifeCycleEvent> eventsToPersist = new ArrayList<LifeCycleEvent>(intactComplex.getLifecycleEvents());
            for (LifeCycleEvent event : eventsToPersist){
                // do not persist or merge events because of cascades
                LifeCycleEvent evt = getContext().getComplexLifecycleSynchronizer().synchronize(event, false);
                // we have a different instance because needed to be synchronized
                if (evt != event){
                    intactComplex.getLifecycleEvents().add(intactComplex.getLifecycleEvents().indexOf(event), evt);
                    intactComplex.getLifecycleEvents().remove(event);
                }
            }
        }
    }

    protected void prepareCooperativeEffects(IntactComplex intactInteraction) throws PersisterException, FinderException, SynchronizerException {

        if (intactInteraction.areCooperativeEffectsInitialized()){
            Collection<CooperativeEffect> parametersToPersist = new ArrayList<CooperativeEffect>(intactInteraction.getCooperativeEffects());
            for (CooperativeEffect param : parametersToPersist){
                // do not persist or merge parameters because of cascades
                CooperativeEffect expParam = getContext().getCooperativeEffectSynchronizer().synchronize(param, false);
                // we have a different instance because needed to be synchronized
                if (expParam != param){
                    intactInteraction.getCooperativeEffects().remove(param);
                    intactInteraction.getCooperativeEffects().add(expParam);
                }
            }
        }
    }

    protected void prepareParticipants(IntactComplex intactInteraction) throws PersisterException, FinderException, SynchronizerException {
        if (intactInteraction.areParticipantsInitialized()){
            Collection<ModelledParticipant> participantsToPersist = new ArrayList<ModelledParticipant>(intactInteraction.getParticipants());
            for (ModelledParticipant participant : participantsToPersist){
                // reinit parent
                participant.setInteraction(intactInteraction);
                // do not persist or merge participants because of cascades
                ModelledParticipant expPart = (ModelledParticipant) getContext().getParticipantSynchronizer().synchronize(participant, false);
                // we have a different instance because needed to be synchronized
                if (expPart != participant){
                    intactInteraction.getParticipants().remove(participant);
                    intactInteraction.addParticipant(expPart);
                }
            }
        }
    }

    protected void prepareExperiments(IntactComplex intactComplex) throws PersisterException, FinderException, SynchronizerException {

        if (intactComplex.areExperimentsInitialized()){

            Collection<Experiment> experimentsToPersist = new ArrayList<Experiment>(intactComplex.getExperiments());
            for (Experiment exp : experimentsToPersist){
                // synchronize experiment
                Experiment expPar = getContext().getExperimentSynchronizer().synchronize(exp, true);
                // we have a different instance because needed to be synchronized
                if (expPar != exp){
                    intactComplex.getExperiments().remove(exp);
                    intactComplex.getExperiments().add(expPar);
                }
            }
        }
    }

    protected void prepareParameters(IntactComplex intactInteraction) throws PersisterException, FinderException, SynchronizerException {
        if (intactInteraction.areParametersInitialized()){
            Collection<ModelledParameter> parametersToPersist = new ArrayList<ModelledParameter>(intactInteraction.getModelledParameters());
            for (ModelledParameter param : parametersToPersist){
                // do not persist or merge parameters because of cascades
                ModelledParameter expPar = getContext().getComplexParameterSynchronizer().synchronize(param, false);
                // we have a different instance because needed to be synchronized
                if (expPar != param){
                    intactInteraction.getModelledParameters().remove(param);
                    intactInteraction.getModelledParameters().add(expPar);
                }
            }
        }
    }

    protected void prepareConfidences(IntactComplex intactInteraction) throws FinderException, PersisterException, SynchronizerException {
        if (intactInteraction.areConfidencesInitialized()){
            List<ModelledConfidence> confsToPersist = new ArrayList<ModelledConfidence>(intactInteraction.getModelledConfidences());
            for (ModelledConfidence confidence : confsToPersist){
                // do not persist or merge confidences because of cascades
                ModelledConfidence expConf = getContext().getComplexConfidenceSynchronizer().synchronize(confidence, false);
                // we have a different instance because needed to be synchronized
                if (expConf != confidence){
                    intactInteraction.getModelledConfidences().remove(confidence);
                    intactInteraction.getModelledConfidences().add(expConf);
                }
            }
        }
    }

    @Override
    protected void prepareAndSynchronizeShortLabel(IntactComplex intactInteraction) {
        // first initialise shortlabel if not done
        if (intactInteraction.getShortName() == null){
            intactInteraction.setShortName(IntactUtils.generateAutomaticComplexShortlabelFor(intactInteraction, IntactUtils.MAX_SHORT_LABEL_LEN));
        }

        super.prepareAndSynchronizeShortLabel(intactInteraction);
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new ComplexMergerEnrichOnly());
    }

    @Override
    protected IntactComplex instantiateNewPersistentInstance(Complex object, Class<? extends IntactComplex> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactComplex newInteractor = new IntactComplex(object.getShortName());
        InteractorCloner.copyAndOverrideComplexProperties(object, newInteractor, false, false);
        return newInteractor;
    }

    @Override
    public void deleteRelatedProperties(IntactComplex intactParticipant){
        for (Object f : intactParticipant.getParticipants()){
            getContext().getModelledParticipantSynchronizer().delete((ModelledParticipant)f);
        }
        intactParticipant.getParticipants().clear();
    }
}
