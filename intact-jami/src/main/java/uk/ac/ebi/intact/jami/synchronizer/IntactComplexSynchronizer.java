package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.InteractorCloner;
import psidev.psi.mi.jami.utils.comparator.CollectionComparator;
import psidev.psi.mi.jami.utils.comparator.interactor.UnambiguousExactComplexComparator;
import psidev.psi.mi.jami.utils.comparator.participant.UnambiguousModelledParticipantComparator;
import uk.ac.ebi.intact.jami.merger.IntactComplexMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.ComplexLifecycleEvent;
import uk.ac.ebi.intact.jami.model.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.impl.IntactCvTermSynchronizer;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Default synchronizer for complexes
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public class IntactComplexSynchronizer extends IntactInteractorBaseSynchronizer<Complex, IntactComplex> {

    private IntactDbSynchronizer<Entity, AbstractIntactEntity> participantSynchronizer;
    private IntactDbSynchronizer<ModelledParameter, ComplexParameter> parameterSynchronizer;
    private IntactDbSynchronizer<ModelledConfidence, ComplexConfidence> confidenceSynchronizer;
    private IntactDbSynchronizer<CooperativeEffect, AbstractIntactCooperativeEffect> cooperativeEffectSynchronizer;
    private IntactDbSynchronizer<Experiment, IntactExperiment> experimentSynchronizer;
    private IntactDbSynchronizer<LifeCycleEvent, ComplexLifecycleEvent> lifeCycleEventSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> statusSynchronizer;

    private CollectionComparator<ModelledEntity> participantsComparator;

    public IntactComplexSynchronizer(EntityManager entityManager) {
        super(entityManager, IntactComplex.class);
        this.participantsComparator = new CollectionComparator<ModelledEntity>(new UnambiguousModelledParticipantComparator());
    }

    public IntactDbSynchronizer<Entity, AbstractIntactEntity> getParticipantSynchronizer() {
        if (this.participantSynchronizer == null){
            this.participantSynchronizer = new IntActEntitySynchronizer(getEntityManager());
        }
        return participantSynchronizer;
    }

    public void setParticipantSynchronizer(IntactDbSynchronizer<Entity, AbstractIntactEntity> participantSynchronizer) {
        this.participantSynchronizer = participantSynchronizer;
    }

    public IntactDbSynchronizer<ModelledParameter, ComplexParameter> getParameterSynchronizer() {
        if (this.parameterSynchronizer == null){
            this.parameterSynchronizer = new IntactParameterSynchronizer<ModelledParameter, ComplexParameter>(getEntityManager(), ComplexParameter.class);
        }
        return parameterSynchronizer;
    }

    public void setParameterSynchronizer(IntactDbSynchronizer<ModelledParameter, ComplexParameter> parameterSynchronizer) {
        this.parameterSynchronizer = parameterSynchronizer;
    }

    public IntactDbSynchronizer<ModelledConfidence, ComplexConfidence> getConfidenceSynchronizer() {
        if (this.confidenceSynchronizer == null){
            this.confidenceSynchronizer = new IntactConfidenceSynchronizer<ModelledConfidence, ComplexConfidence>(getEntityManager(), ComplexConfidence.class);
        }
        return confidenceSynchronizer;
    }

    public void setConfidenceSynchronizer(IntactDbSynchronizer<ModelledConfidence, ComplexConfidence> confidenceSynchronizer) {
        this.confidenceSynchronizer = confidenceSynchronizer;
    }

    public IntactDbSynchronizer<CooperativeEffect, AbstractIntactCooperativeEffect> getCooperativeEffectSynchronizer() {
        if (this.cooperativeEffectSynchronizer == null){
            this.cooperativeEffectSynchronizer = new IntactCooperativeEffectSynchronizer(getEntityManager());
        }
        return cooperativeEffectSynchronizer;
    }

    public void setCooperativeEffectSynchronizer(IntactDbSynchronizer<CooperativeEffect, AbstractIntactCooperativeEffect> cooperativeEffectSynchronizer) {
        this.cooperativeEffectSynchronizer = cooperativeEffectSynchronizer;
    }

    public IntactDbSynchronizer<Experiment, IntactExperiment> getExperimentSynchronizer() {
        if (this.experimentSynchronizer == null){
            this.experimentSynchronizer = new IntactExperimentSynchronizer(getEntityManager());
        }
        return experimentSynchronizer;
    }

    public void setExperimentSynchronizer(IntactDbSynchronizer<Experiment, IntactExperiment> experimentSynchronizer) {
        this.experimentSynchronizer = experimentSynchronizer;
    }

    public IntactDbSynchronizer<LifeCycleEvent, ComplexLifecycleEvent> getLifeCycleEventSynchronizer() {
        if (this.lifeCycleEventSynchronizer == null){
            this.lifeCycleEventSynchronizer = new IntactLifeCycleSynchronizer<ComplexLifecycleEvent>(getEntityManager(), ComplexLifecycleEvent.class);
        }
        return lifeCycleEventSynchronizer;
    }

    public void setLifeCycleEventSynchronizer(IntactDbSynchronizer<LifeCycleEvent, ComplexLifecycleEvent> lifeCycleEventSynchronizer) {
        this.lifeCycleEventSynchronizer = lifeCycleEventSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getStatusSynchronizer() {
        if (this.statusSynchronizer == null){
            this.statusSynchronizer = new IntactCvTermSynchronizer(getEntityManager(), IntactUtils.PUBLICATION_STATUS_OBJCLASS);

        }
        return statusSynchronizer;
    }

    public void setStatusSynchronizer(IntactDbSynchronizer<CvTerm, IntactCvTerm> statusSynchronizer) {
        this.statusSynchronizer = statusSynchronizer;
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
            query = getEntityManager().createQuery("select i from "+getIntactClass()+" i " +
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
        // then check confidences
        prepareConfidences(intactComplex);
        // then check parameters
        prepareParameters(intactComplex);
        // then check participants
        prepareParticipants(intactComplex);
        // then check cooperative effects
        prepareCooperativeEffects(intactComplex);
        // prepare status
        prepareStatus(intactComplex);
        // prepare lifecycle
        prepareLifeCycleEvents(intactComplex);

        // then prepare experiment for backward compatibility
        prepareExperiments(intactComplex);
    }

    public void clearCache() {
        super.clearCache();
        getParameterSynchronizer().clearCache();
        getConfidenceSynchronizer().clearCache();
        getParticipantSynchronizer().clearCache();
        getCooperativeEffectSynchronizer().clearCache();
        getLifeCycleEventSynchronizer().clearCache();
        getStatusSynchronizer().clearCache();
    }

    protected void prepareStatus(IntactComplex intactComplex) throws PersisterException, FinderException, SynchronizerException {

        // first the status
        CvTerm status = intactComplex.getStatus() != null ? intactComplex.getStatus() : IntactUtils.createLifecycleStatus(LifeCycleEvent.NEW_STATUS);
        intactComplex.setStatus(getStatusSynchronizer().synchronize(status, true));

        // if the publication is released or ready for release, we move the experiment to another publication which should be released
        if (LifeCycleEvent.RELEASED.equals(status.getShortName()) ||
                LifeCycleEvent.READY_FOR_RELEASE.equals(status.getShortName())){
            Experiment exp = intactComplex.getExperiments().isEmpty() ? intactComplex.getExperiments().iterator().next() : null;
            if (exp == null){
                createExperiment(intactComplex, "14681455");
                exp = intactComplex.getExperiments().iterator().next();
                ((IntactPublication)exp.getPublication()).setStatus(intactComplex.getStatus());
            }
            else if (exp.getPublication() == null){
                exp.setPublication(new IntactPublication("14681455"));
                ((IntactPublication)exp.getPublication()).setStatus(intactComplex.getStatus());
            }
            else if (exp.getPublication().getPubmedId() == null || !exp.getPublication().getPubmedId().equals("14681455")){
                exp.setPublication(new IntactPublication("14681455"));
                ((IntactPublication)exp.getPublication()).setStatus(intactComplex.getStatus());
            }
        }
    }

    protected void prepareLifeCycleEvents(IntactComplex intactComplex) throws PersisterException, FinderException, SynchronizerException {

        if (intactComplex.areLifeCycleEventsInitialized()){
            List<LifeCycleEvent> eventsToPersist = new ArrayList<LifeCycleEvent>(intactComplex.getLifecycleEvents());
            for (LifeCycleEvent event : eventsToPersist){
                // do not persist or merge events because of cascades
                LifeCycleEvent evt = getLifeCycleEventSynchronizer().synchronize(event, false);
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
                CooperativeEffect expParam = getCooperativeEffectSynchronizer().synchronize(param, false);
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
                ModelledParticipant expPart = (ModelledParticipant) getParticipantSynchronizer().synchronize(participant, false);
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
            if (intactComplex.getExperiments().isEmpty()){
                createExperiment(intactComplex, "unassigned638");
            }
        }
    }

    private void createExperiment(IntactComplex intactComplex, String pubmed) throws FinderException, PersisterException, SynchronizerException {
        // create default experiment with publication unassigned for complexes
        IntactExperiment defaultExperiment = new IntactExperiment(new IntactPublication(pubmed));
        // inferred by curator
        defaultExperiment.setInteractionDetectionMethod(IntactUtils.createMIInteractionDetectionMethod(Experiment.INFERRED_BY_CURATOR, Experiment.INFERRED_BY_CURATOR_MI));
        // use host organism of interaction
        defaultExperiment.setHostOrganism(intactComplex.getOrganism());
        // persist this experiment
        getExperimentSynchronizer().persist(defaultExperiment);
        // then add this complex
        intactComplex.getExperiments().add(defaultExperiment);
    }

    protected void prepareParameters(IntactComplex intactInteraction) throws PersisterException, FinderException, SynchronizerException {
        if (intactInteraction.areParametersInitialized()){
            Collection<ModelledParameter> parametersToPersist = new ArrayList<ModelledParameter>(intactInteraction.getModelledParameters());
            for (ModelledParameter param : parametersToPersist){
                // do not persist or merge parameters because of cascades
                ModelledParameter expPar = getParameterSynchronizer().synchronize(param, false);
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
                ModelledConfidence expConf = getConfidenceSynchronizer().synchronize(confidence, false);
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
        super.setIntactMerger(new IntactComplexMergerEnrichOnly());
    }

    @Override
    protected IntactComplex instantiateNewPersistentInstance(Complex object, Class<? extends IntactComplex> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactComplex newInteractor = new IntactComplex(object.getShortName());
        InteractorCloner.copyAndOverrideComplexProperties(object, newInteractor, false, false);
        return newInteractor;
    }
}
