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

    private IntactDbSynchronizer<ModelledParticipant, IntactModelledParticipant> participantSynchronizer;
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
        this.parameterSynchronizer = new IntactParameterSynchronizer<ModelledParameter, ComplexParameter>(entityManager, ComplexParameter.class);
        this.confidenceSynchronizer = new IntactConfidenceSynchronizer<ModelledConfidence, ComplexConfidence>(entityManager, ComplexConfidence.class);
        this.experimentSynchronizer = new IntactExperimentSynchronizer(entityManager);
        this.lifeCycleEventSynchronizer = new IntactLifeCycleSynchronizer<ComplexLifecycleEvent>(entityManager, ComplexLifecycleEvent.class);
        this.statusSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.PUBLICATION_STATUS_OBJCLASS);
        this.cooperativeEffectSynchronizer = new IntactCooperativeEffectSynchronizer(entityManager);
        this.participantSynchronizer = new IntActEntitySynchronizer<ModelledParticipant, IntactModelledParticipant, ModelledEntityPool, IntactModelledEntityPool>(entityManager,
                IntactModelledParticipant.class, IntactModelledEntityPool.class,
                new IntactEntityBaseSynchronizer<ModelledParticipant, IntactModelledParticipant>(entityManager, IntactModelledParticipant.class),
                new IntactModelledEntityPoolSynchronizer(entityManager));
    }

    public IntactComplexSynchronizer(EntityManager entityManager, IntactDbSynchronizer<Alias, InteractorAlias> aliasSynchronizer,
                                     IntactDbSynchronizer<Annotation, InteractorAnnotation> annotationSynchronizer,
                                     IntactDbSynchronizer<Xref, InteractorXref> xrefSynchronizer,
                                     IntactDbSynchronizer<Organism, IntactOrganism> organismSynchronizer,
                                     IntactDbSynchronizer<CvTerm, IntactCvTerm> typeSynchronizer,
                                     IntactDbSynchronizer<Checksum,InteractorChecksum> checksumSynchronizer,
                                     IntactDbSynchronizer<ModelledParticipant, IntactModelledParticipant> participantSynchronizer,
                                     IntactDbSynchronizer<ModelledParameter, ComplexParameter> parameterSynchronizer,
                                     IntactDbSynchronizer<ModelledConfidence, ComplexConfidence> confidenceSynchronizer,
                                     IntactDbSynchronizer<CooperativeEffect, AbstractIntactCooperativeEffect> cooperativeEffectSynchronizer,
                                     IntactDbSynchronizer<Experiment, IntactExperiment> experimentSynchronizer,
                                     IntactDbSynchronizer<LifeCycleEvent, ComplexLifecycleEvent> lifeCycleEventSynchronizer,
                                     IntactDbSynchronizer<CvTerm, IntactCvTerm> statusSynchronizer) {
        super(entityManager, IntactComplex.class, aliasSynchronizer, annotationSynchronizer,
                xrefSynchronizer, organismSynchronizer, typeSynchronizer, checksumSynchronizer);
        this.participantsComparator = new CollectionComparator<ModelledEntity>(new UnambiguousModelledParticipantComparator());
        this.parameterSynchronizer = parameterSynchronizer != null ? parameterSynchronizer : new IntactParameterSynchronizer<ModelledParameter, ComplexParameter>(entityManager, ComplexParameter.class);
        this.confidenceSynchronizer = confidenceSynchronizer != null ? confidenceSynchronizer : new IntactConfidenceSynchronizer<ModelledConfidence, ComplexConfidence>(entityManager, ComplexConfidence.class);
        this.experimentSynchronizer = experimentSynchronizer != null ? experimentSynchronizer : new IntactExperimentSynchronizer(entityManager);
        this.lifeCycleEventSynchronizer = lifeCycleEventSynchronizer != null ? lifeCycleEventSynchronizer : new IntactLifeCycleSynchronizer<ComplexLifecycleEvent>(entityManager, ComplexLifecycleEvent.class);
        this.statusSynchronizer = statusSynchronizer != null ? statusSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.PUBLICATION_STATUS_OBJCLASS);
        this.cooperativeEffectSynchronizer = cooperativeEffectSynchronizer != null ? cooperativeEffectSynchronizer : new IntactCooperativeEffectSynchronizer(entityManager);
        this.participantSynchronizer = participantSynchronizer != null ? participantSynchronizer : new IntActEntitySynchronizer<ModelledParticipant, IntactModelledParticipant, ModelledEntityPool, IntactModelledEntityPool>(entityManager,
                IntactModelledParticipant.class, IntactModelledEntityPool.class,
                new IntactEntityBaseSynchronizer<ModelledParticipant, IntactModelledParticipant>(entityManager, IntactModelledParticipant.class),
                new IntactModelledEntityPoolSynchronizer(entityManager));
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
        this.parameterSynchronizer.clearCache();
        this.confidenceSynchronizer.clearCache();
        this.participantSynchronizer.clearCache();
        this.cooperativeEffectSynchronizer.clearCache();
        this.lifeCycleEventSynchronizer.clearCache();
        this.statusSynchronizer.clearCache();
    }

    protected void prepareStatus(IntactComplex intactComplex) throws PersisterException, FinderException, SynchronizerException {

        // first the status
        CvTerm status = intactComplex.getStatus() != null ? intactComplex.getStatus() : IntactUtils.createLifecycleStatus(LifeCycleEvent.NEW_STATUS);
        intactComplex.setStatus(this.statusSynchronizer.synchronize(status, true));
    }

    protected void prepareLifeCycleEvents(IntactComplex intactComplex) throws PersisterException, FinderException, SynchronizerException {

        if (intactComplex.areLifeCycleEventsInitialized()){
            List<LifeCycleEvent> eventsToPersist = new ArrayList<LifeCycleEvent>(intactComplex.getLifecycleEvents());
            for (LifeCycleEvent event : eventsToPersist){
                // do not persist or merge events because of cascades
                LifeCycleEvent evt = this.lifeCycleEventSynchronizer.synchronize(event, false);
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
                CooperativeEffect expParam = this.cooperativeEffectSynchronizer.synchronize(param, false);
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
                ModelledParticipant expPart = this.participantSynchronizer.synchronize(participant, false);
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
                // create default experiment with publication unassigned for complexes
                IntactExperiment defaultExperiment = new IntactExperiment(new IntactPublication("unassigned638"));
                // inferred by curator
                defaultExperiment.setInteractionDetectionMethod(IntactUtils.createMIInteractionDetectionMethod(Experiment.INFERRED_BY_CURATOR, Experiment.INFERRED_BY_CURATOR_MI));
                // use host organism of interaction
                defaultExperiment.setHostOrganism(intactComplex.getOrganism());
                // persist this experiment
                this.experimentSynchronizer.persist(defaultExperiment);
                // then add this complex
                intactComplex.getExperiments().add(defaultExperiment);
            }
        }
    }

    protected void prepareParameters(IntactComplex intactInteraction) throws PersisterException, FinderException, SynchronizerException {
        if (intactInteraction.areParametersInitialized()){
            Collection<ModelledParameter> parametersToPersist = new ArrayList<ModelledParameter>(intactInteraction.getModelledParameters());
            for (ModelledParameter param : parametersToPersist){
                // do not persist or merge parameters because of cascades
                ModelledParameter expPar = this.parameterSynchronizer.synchronize(param, false);
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
                ModelledConfidence expConf = this.confidenceSynchronizer.synchronize(confidence, false);
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
