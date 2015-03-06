package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import psidev.psi.mi.jami.utils.XrefUtils;
import psidev.psi.mi.jami.utils.clone.InteractorCloner;
import psidev.psi.mi.jami.utils.comparator.CollectionComparator;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.ComplexMergerEnrichOnly;
import uk.ac.ebi.intact.jami.merger.InteractorBaseMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.synchronizer.listener.impl.DbComplexEnricherListener;
import uk.ac.ebi.intact.jami.synchronizer.listener.impl.DbInteractorEnricherListener;
import uk.ac.ebi.intact.jami.utils.IntactUtils;
import uk.ac.ebi.intact.jami.utils.comparator.IntactComplexComparator;
import uk.ac.ebi.intact.jami.utils.comparator.IntactComplexGoXrefComparator;
import uk.ac.ebi.intact.jami.utils.comparator.IntactModelledParticipantComparator;

import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

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
    private ComplexExperimentBCSynchronizer experimentBCSynchronizer;

    public ComplexSynchronizer(SynchronizerContext context) {
        super(context, IntactComplex.class);
        this.participantsComparator = new CollectionComparator<ModelledParticipant>(new IntactModelledParticipantComparator());
        this.experimentBCSynchronizer = new ComplexExperimentBCSynchronizer(context);
    }

    @Override
    protected DbInteractorEnricherListener<Complex> initDefaultEnricherListener() {
        return new DbComplexEnricherListener(getContext(), this);
    }

    @Override
    protected IntactComplex postFilter(Complex term, Collection<IntactComplex> results) throws FinderException {
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
        else if (filteredResults.size() > 1){
            throw new FinderException("The complex "+term + " can match "+filteredResults.size()+" complexes in the database and we cannot determine which one is valid: "+filteredResults);
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
    protected Collection<IntactComplex> postFilterAll(Complex term, Collection<IntactComplex> results) {
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

        return filteredResults;
    }

    @Override
    protected Collection<String> postFilterAllAcs(Complex term, Collection<IntactComplex> results) {
        Collection<String> filteredResults = new ArrayList<String>(results.size());
        for (IntactComplex complex : results){
            // we accept empty participants when finding complexes
            if (term.getParticipants().isEmpty() && complex.getAc() != null){
                filteredResults.add(complex.getAc());
            }
            // same participants
            else if (this.participantsComparator.compare(term.getParticipants(), complex.getParticipants()) == 0 && complex.getAc() != null){
                filteredResults.add(complex.getAc());
            }
        }

        return filteredResults;
    }

    @Override
    protected Collection<IntactComplex> findByOtherProperties(Complex term, Collection<String> existingTypes, Collection<String> existingOrganisms) {
        Query query;
        if (existingOrganisms.isEmpty()){
            query = getEntityManager().createQuery("select i from IntactComplex i " +
                    "join i.interactorType as t " +
                    "where i.organism is null " +
                    "and size(i.participants) =:participantSize " +
                    "and t.ac in (:typeAc)");
            query.setParameter("typeAc", existingTypes);
            query.setParameter("participantSize", term.getParticipants().size());
        }
        else{
            query = getEntityManager().createQuery("select i from "+getIntactClass().getSimpleName()+" i " +
                    "join i.interactorType as t " +
                    "join i.organism as o " +
                    "where o.ac in (:orgAc) " +
                    "and size(i.participants) =:participantSize " +
                    "and t.ac in (:typeAc)");
            query.setParameter("orgAc", existingOrganisms);
            query.setParameter("participantSize", term.getParticipants().size());
            query.setParameter("typeAc", existingTypes);
        }
        return query.getResultList();
    }

    @Override
    protected void initialisePersistedObjectMap() {
        super.initialisePersistedObjectMap(new IntactComplexComparator());
    }

    @Override
    public void synchronizeProperties(IntactComplex intactComplex) throws FinderException, PersisterException, SynchronizerException {
        // prepare evidence type
        prepareEvidenceType(intactComplex, true);
        super.synchronizeProperties(intactComplex);
        // prepare interaction evidences
        //prepareInteractionEvidences(intactComplex);
        // then check confidences
        prepareConfidences(intactComplex, true);
        // then check parameters
        prepareParameters(intactComplex, true);
        // then check participants
        prepareParticipants(intactComplex, true);
        // then check cooperative effects
        //prepareCooperativeEffects(intactComplex);
        // prepare status
        prepareStatusAndCurators(intactComplex, true);
        // prepare lifecycle
        prepareLifeCycleEvents(intactComplex, true);
        // then prepare experiment for backward compatibility
        prepareExperiments(intactComplex, true);
        // then prepare source
        prepareSource(intactComplex, true);
    }

    @Override
    protected void prepareXrefs(IntactComplex intactInteractor, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactInteractor.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactInteractor.getDbXrefs());
            intactInteractor.getDbXrefs().clear();
            int index = 0;
            Set<Xref> goReferences = new TreeSet<Xref>(new IntactComplexGoXrefComparator());
            try{
                for (Xref xref : xrefsToPersist){
                    // do not persist or merge xrefs because of cascades
                    Xref cvXref = enableSynchronization ?
                            getContext().getComplexXrefSynchronizer().synchronize(xref, false) :
                            getContext().getComplexXrefSynchronizer().convertToPersistentObject(xref);
                    // we have a different instance because needed to be synchronized
                    if (cvXref != null && goReferences.add(cvXref)){
                        intactInteractor.getDbXrefs().add(cvXref);
                    }
                    index++;
                }
            }
            finally {
                // always add previous properties in case of exception
                if (index < xrefsToPersist.size() - 1){
                    for (int i = index; i < xrefsToPersist.size(); i++){
                        intactInteractor.getDbXrefs().add(xrefsToPersist.get(i));
                    }
                }
            }
        }
    }

    @Override
    public void convertPersistableProperties(IntactComplex intactComplex) throws FinderException, PersisterException, SynchronizerException {
        // prepare evidence type
        prepareEvidenceType(intactComplex, false);
        super.convertPersistableProperties(intactComplex);
        // prepare interaction evidences
        //prepareInteractionEvidences(intactComplex);
        // then check confidences
        prepareConfidences(intactComplex, false);
        // then check parameters
        prepareParameters(intactComplex, false);
        // then check participants
        prepareParticipants(intactComplex, false);
        // then check cooperative effects
        //prepareCooperativeEffects(intactComplex);
        // prepare status
        prepareStatusAndCurators(intactComplex, false);
        // prepare lifecycle
        prepareLifeCycleEvents(intactComplex, false);
        // then prepare experiment for backward compatibility
        prepareExperiments(intactComplex, false);
        // then check source
        prepareSource(intactComplex, false);
    }

    protected void prepareSource(IntactComplex intactSource, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        Source source = intactSource.getSource();
        if (source != null){
            intactSource.setSource(enableSynchronization ?
                    getContext().getSourceSynchronizer().synchronize(source, true) :
                    getContext().getSourceSynchronizer().convertToPersistentObject(source));
        }
    }

    @Override
    protected void synchronizePropertiesAfterMerge(IntactComplex mergedObject) throws SynchronizerException, PersisterException, FinderException {
        // prepare evidence type
        prepareEvidenceType(mergedObject, true);
    }

    @Override
    protected void prepareAnnotations(IntactComplex intactInteractor, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactInteractor.areAnnotationsInitialized()){
            if (AnnotationUtils.collectFirstAnnotationWithTopic(intactInteractor.getAnnotations(), null, "curated-complex") == null){
                intactInteractor.getAnnotations().add(new InteractorAnnotation(IntactUtils.createMITopic("curated-complex", null)));
            }
        }
        super.prepareAnnotations(intactInteractor, enableSynchronization);
    }

    protected void prepareEvidenceType(IntactComplex intactComplex, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {

       if (intactComplex.getEvidenceType() != null){
           CvTerm evidenceType = enableSynchronization ?
                   getContext().getDatabaseSynchronizer().synchronize(intactComplex.getEvidenceType(), true) :
                   getContext().getDatabaseSynchronizer().convertToPersistentObject(intactComplex.getEvidenceType());
           intactComplex.setEvidenceType(evidenceType);

           // for BC with intact-core only
           Xref ecoCode = XrefUtils.collectFirstIdentifierWithDatabase(evidenceType.getIdentifiers(), Complex.ECO_MI, Complex.ECO);
           // only add xref when we can reload the xrefs becaus ethe complex is in the session
           if (ecoCode != null && (intactComplex.getAc() == null || getEntityManager().contains(intactComplex))){
               Collection<Xref> ecoCodes = XrefUtils.collectAllXrefsHavingDatabase(intactComplex.getXrefs(), Complex.ECO_MI, Complex.ECO);
               // no eco codes
               if (ecoCodes.isEmpty()){
                   CvTerm db = IntactUtils.createMIDatabase(Complex.ECO,
                           Complex.ECO_MI);
                   intactComplex.getXrefs().add(new InteractorXref(getContext().getDatabaseSynchronizer().synchronize(db, true),
                           ecoCode.getId()));
               }
               // update eco codes
               else {
                   Collection<Xref> ecoCodesToRemove = new ArrayList<Xref>(ecoCodes.size());
                   boolean hasEco = false;
                   for (Xref eco : ecoCodes){
                       if (eco.getQualifier() == null && !eco.getId().equalsIgnoreCase(eco.getId())){
                           ecoCodesToRemove.add(eco);
                       }
                       else if (eco.getId().equalsIgnoreCase(eco.getId())){
                           hasEco = true;
                       }
                   }

                   if (!hasEco){
                       CvTerm db = IntactUtils.createMIDatabase(Complex.ECO,
                               Complex.ECO_MI);
                       intactComplex.getXrefs().add(new InteractorXref(getContext().getDatabaseSynchronizer().synchronize(db, true),
                               ecoCode.getId()));
                   }
                   intactComplex.getXrefs().removeAll(ecoCodesToRemove);
               }
           }
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

    protected void prepareStatusAndCurators(IntactComplex intactComplex, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {

        // first the status
        CvTerm status = intactComplex.getCvStatus();
        intactComplex.setCvStatus(enableSynchronization ?
                getContext().getLifecycleStatusSynchronizer().synchronize(status, true) :
                getContext().getLifecycleStatusSynchronizer().convertToPersistentObject(status));

        // then curator
        User curator = intactComplex.getCurrentOwner();
        // do not persist user if not there
        if (curator != null){
            intactComplex.setCurrentOwner(enableSynchronization ?
                    getContext().getUserReadOnlySynchronizer().synchronize(curator, false) :
                    getContext().getUserReadOnlySynchronizer().convertToPersistentObject(curator));
        }

        // then reviewer
        User reviewer = intactComplex.getCurrentReviewer();
        if (reviewer != null){
            intactComplex.setCurrentReviewer(enableSynchronization ?
                    getContext().getUserReadOnlySynchronizer().synchronize(reviewer, false) :
                    getContext().getUserReadOnlySynchronizer().convertToPersistentObject(reviewer));
        }
    }

    protected void prepareLifeCycleEvents(IntactComplex intactComplex, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {

        if (intactComplex.areLifeCycleEventsInitialized()){
            List<LifeCycleEvent> eventsToPersist = new ArrayList<LifeCycleEvent>(intactComplex.getLifecycleEvents());
            intactComplex.getLifecycleEvents().clear();
            int index = 0;
            try{
                for (LifeCycleEvent event : eventsToPersist){
                    // do not persist or merge events because of cascades
                    LifeCycleEvent evt = enableSynchronization ?
                            getContext().getComplexLifecycleSynchronizer().synchronize(event, false) :
                            getContext().getComplexLifecycleSynchronizer().convertToPersistentObject(event);
                    // we have a different instance because needed to be synchronized
                    if (evt != null && !intactComplex.getLifecycleEvents().contains(evt)){
                        intactComplex.getLifecycleEvents().add(evt);
                    }
                    index++;
                }
            }
            finally {
                // always add previous properties in case of exception
                if (index < eventsToPersist.size() - 1){
                    for (int i = index; i < eventsToPersist.size(); i++){
                        intactComplex.getLifecycleEvents().add(eventsToPersist.get(i));
                    }
                }
            }
        }
    }

    protected void prepareCooperativeEffects(IntactComplex intactInteraction, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {

        if (intactInteraction.areCooperativeEffectsInitialized()){
            List<CooperativeEffect> parametersToPersist = new ArrayList<CooperativeEffect>(intactInteraction.getCooperativeEffects());
            intactInteraction.getCooperativeEffects().clear();
            int index = 0;
            try{
                for (CooperativeEffect param : parametersToPersist){
                    // do not persist or merge parameters because of cascades
                    CooperativeEffect expParam = enableSynchronization ?
                            getContext().getCooperativeEffectSynchronizer().synchronize(param, false) :
                            getContext().getCooperativeEffectSynchronizer().convertToPersistentObject(param);
                    // we have a different instance because needed to be synchronized
                    if (expParam != null && !intactInteraction.getCooperativeEffects().contains(expParam)){
                        intactInteraction.getCooperativeEffects().add(expParam);
                    }
                    index++;
                }
            }
            finally {
                // always add previous properties in case of exception
                if (index < parametersToPersist.size() - 1){
                    for (int i = index; i < parametersToPersist.size(); i++){
                        intactInteraction.getCooperativeEffects().add(parametersToPersist.get(i));
                    }
                }
            }
        }
    }

    protected void prepareParticipants(IntactComplex intactInteraction, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        if (intactInteraction.areParticipantsInitialized()){
            List<ModelledParticipant> participantsToPersist = new ArrayList<ModelledParticipant>(intactInteraction.getParticipants());
            intactInteraction.getParticipants().clear();
            int index = 0;
            try{
                for (ModelledParticipant participant : participantsToPersist){
                    // reinit parent
                    participant.setInteraction(intactInteraction);
                    // do not persist or merge participants because of cascades
                    ModelledParticipant expPart = enableSynchronization ?
                            (ModelledParticipant) getContext().getModelledParticipantSynchronizer().synchronize(participant, false) :
                            (ModelledParticipant) getContext().getModelledParticipantSynchronizer().convertToPersistentObject(participant);
                    // we have a different instance because needed to be synchronized
                    if (expPart != null && !intactInteraction.getParticipants().contains(expPart)){
                        intactInteraction.addParticipant(expPart);
                    }
                    index++;
                }
            }
            finally {
                // always add previous properties in case of exception
                if (index < participantsToPersist.size() - 1){
                    for (int i = index; i < participantsToPersist.size(); i++){
                        intactInteraction.addParticipant(participantsToPersist.get(i));
                    }
                }
            }
        }
    }

    protected void prepareExperiments(IntactComplex intactComplex, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {

        if (intactComplex.areExperimentsInitialized()){

            if (!intactComplex.getExperiments().isEmpty()){
               Experiment exp = intactComplex.getExperiments().iterator().next();
                // check that experiment host is the same as existing organism
                if (intactComplex.getOrganism() != null && exp.getHostOrganism() != null){
                     if (intactComplex.getOrganism().getTaxId() != exp.getHostOrganism().getTaxId()){
                         intactComplex.getExperiments().clear();
                         IntactUtils.createAndAddDefaultExperimentForComplexes(intactComplex, exp.getPublication() != null ?
                                 (exp.getPublication().getPubmedId() != null ? exp.getPublication().getPubmedId() : "unassigned638")
                                 : "unassigned638");
                     }
                }
                else {
                    intactComplex.getExperiments().clear();
                    IntactUtils.createAndAddDefaultExperimentForComplexes(intactComplex, exp.getPublication() != null ?
                            (exp.getPublication().getPubmedId() != null ? exp.getPublication().getPubmedId() : "unassigned638")
                            : "unassigned638");
                }

            }
            List<Experiment> experimentsToPersist = new ArrayList<Experiment>(intactComplex.getExperiments());
            Set<Experiment> processedExperiments = new HashSet<Experiment>(intactComplex.getExperiments().size());
            intactComplex.getExperiments().clear();
            int index = 0;
            try{
                for (Experiment exp : experimentsToPersist){
                    // synchronize publication if not done yet
                    if (exp.getPublication() != null){
                        Publication syncPub = enableSynchronization ?
                                getContext().getPublicationSynchronizer().synchronize(exp.getPublication(), true) :
                                getContext().getPublicationSynchronizer().convertToPersistentObject(exp.getPublication());
                        // we have a different instance because needed to be synchronized
                        if (syncPub != exp.getPublication()){
                            exp.setPublication(syncPub);
                        }
                    }
                    // synchronize experiment
                    Experiment expPar = enableSynchronization ?
                            this.experimentBCSynchronizer.synchronize(exp, true) :
                            this.experimentBCSynchronizer.convertToPersistentObject(exp);
                    // we have a different instance because needed to be synchronized
                    if (expPar != null && processedExperiments.add(expPar)){
                        intactComplex.getExperiments().add(expPar);
                    }
                    index++;
                }
            }
            finally {
                // always add previous properties in case of exception
                if (index < experimentsToPersist.size() - 1){
                    for (int i = index; i < experimentsToPersist.size(); i++){
                        intactComplex.getExperiments().add(experimentsToPersist.get(i));
                    }
                }
            }
        }
    }

    protected void prepareParameters(IntactComplex intactInteraction, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        if (intactInteraction.areParametersInitialized()){
            List<ModelledParameter> parametersToPersist = new ArrayList<ModelledParameter>(intactInteraction.getModelledParameters());
            intactInteraction.getModelledParameters().clear();
            int index = 0;
            try{
                for (ModelledParameter param : parametersToPersist){
                    // do not persist or merge parameters because of cascades
                    ModelledParameter expPar = enableSynchronization ?
                            getContext().getComplexParameterSynchronizer().synchronize(param, false) :
                            getContext().getComplexParameterSynchronizer().convertToPersistentObject(param);
                    // we have a different instance because needed to be synchronized
                    if (expPar != null && !intactInteraction.getModelledParameters().contains(expPar)){
                        intactInteraction.getModelledParameters().add(expPar);
                    }
                    index++;
                }
            }
            finally {
                // always add previous properties in case of exception
                if (index < parametersToPersist.size() - 1){
                    for (int i = index; i < parametersToPersist.size(); i++){
                        intactInteraction.getModelledParameters().add(parametersToPersist.get(i));
                    }
                }
            }
        }
    }

    protected void prepareConfidences(IntactComplex intactInteraction, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactInteraction.areConfidencesInitialized()){
            List<ModelledConfidence> confsToPersist = new ArrayList<ModelledConfidence>(intactInteraction.getModelledConfidences());
            intactInteraction.getModelledConfidences().clear();
            int index = 0;
            try{
                for (ModelledConfidence confidence : confsToPersist){
                    // do not persist or merge confidences because of cascades
                    ModelledConfidence expConf = enableSynchronization ?
                            getContext().getComplexConfidenceSynchronizer().synchronize(confidence, false) :
                            getContext().getComplexConfidenceSynchronizer().convertToPersistentObject(confidence);
                    // we have a different instance because needed to be synchronized
                    if (expConf != null && !intactInteraction.getModelledConfidences().contains(expConf)){
                        intactInteraction.getModelledConfidences().add(expConf);
                    }
                    index++;
                }
            }
            finally {
                // always add previous properties in case of exception
                if (index < confsToPersist.size() - 1){
                    for (int i = index; i < confsToPersist.size(); i++){
                        intactInteraction.getModelledConfidences().add(confsToPersist.get(i));
                    }
                }
            }
        }
    }

    @Override
    public void prepareAndSynchronizeShortLabel(IntactComplex intactInteraction) {
        // first initialise shortlabel if not done
        if (intactInteraction.getShortName() == null){
            intactInteraction.setShortName(IntactUtils.generateAutomaticComplexShortlabelFor(intactInteraction, IntactUtils.MAX_SHORT_LABEL_LEN));
        }

        super.prepareAndSynchronizeShortLabel(intactInteraction);
    }

    @Override
    protected void initialiseDefaultMerger() {
        ComplexMergerEnrichOnly merger = new ComplexMergerEnrichOnly();
        merger.setListener(getEnricherListener());
        super.setIntactMerger(merger);
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

    @Override
    public void clearCache() {
        super.clearCache();
        this.experimentBCSynchronizer.clearCache();
    }
}
