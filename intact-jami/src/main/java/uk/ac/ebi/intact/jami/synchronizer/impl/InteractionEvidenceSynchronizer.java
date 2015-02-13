package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.collections.map.IdentityMap;
import psidev.psi.mi.jami.enricher.InteractionEnricher;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.InteractionCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMerger;
import uk.ac.ebi.intact.jami.merger.InteractionEvidenceMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractionEvidence;
import uk.ac.ebi.intact.jami.synchronizer.*;
import uk.ac.ebi.intact.jami.synchronizer.listener.impl.DbInteractionEnricherListener;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Default synchronizer for interaction evidences
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public class InteractionEvidenceSynchronizer extends AbstractIntactDbSynchronizer<InteractionEvidence, IntactInteractionEvidence>
implements IntactInteractionSynchronizer{

    private Map<InteractionEvidence, IntactInteractionEvidence> persistedObjects;
    private Map<InteractionEvidence, IntactInteractionEvidence> convertedObjects;

    private Set<String> persistedNames;

    private DbInteractionEnricherListener enricherListener;

    public InteractionEvidenceSynchronizer(SynchronizerContext context){
        super(context, IntactInteractionEvidence.class);
        // to keep track of persisted cvs
        this.persistedObjects = new IdentityMap();
        this.convertedObjects = new IdentityMap();
        this.persistedNames = new HashSet<String>();

        enricherListener = new DbInteractionEnricherListener(getContext(), this);
    }

    public IntactInteractionEvidence find(InteractionEvidence interaction) throws FinderException {
        if (this.persistedObjects.containsKey(interaction)){
            return this.persistedObjects.get(interaction);
        }
        else{
            return null;
        }
    }

    @Override
    public Collection<IntactInteractionEvidence> findAll(InteractionEvidence interaction) {
        if (this.persistedObjects.containsKey(interaction)){
            return Collections.singleton(this.persistedObjects.get(interaction));
        }
        else{
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public Collection<String> findAllMatchingAcs(InteractionEvidence interaction) {
        if (this.persistedObjects.containsKey(interaction)){
            IntactInteractionEvidence evCached = this.persistedObjects.get(interaction);
            if (evCached != null && evCached.getAc() != null){
               return Collections.singleton(evCached.getAc());
            }
            return Collections.EMPTY_LIST;
        }
        else{
            return Collections.EMPTY_LIST;
        }
    }

    public void synchronizeProperties(IntactInteractionEvidence intactInteraction) throws FinderException, PersisterException, SynchronizerException {
        // then check interaction detection method
        prepareInteractionType(intactInteraction, true);
        // then check participant identification method
        prepareConfidences(intactInteraction, true);
        // then check organism
        prepareParameters(intactInteraction, true);
        // then check annotations
        prepareAnnotations(intactInteraction, true);
        // then check xrefs
        prepareXrefs(intactInteraction, true);
        // then check interactions
        prepareParticipants(intactInteraction, true);
        // then check variable parameters
        prepareVariableParametersValues(intactInteraction, true);
        // then check shortlabel/synchronize
        prepareAndSynchronizeShortLabel(intactInteraction);
    }

    public void clearCache() {
        this.persistedObjects.clear();
        this.convertedObjects.clear();
        this.persistedNames.clear();
        this.enricherListener.getInteractionUpdates().clear();
    }

    @Override
    protected Object extractIdentifier(IntactInteractionEvidence object) {
        return object.getAc();
    }

    @Override
    protected IntactInteractionEvidence instantiateNewPersistentInstance(InteractionEvidence object, Class<? extends IntactInteractionEvidence> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactInteractionEvidence inter = new IntactInteractionEvidence();
        InteractionCloner.copyAndOverrideInteractionEvidenceProperties(object, inter, false, false);
        return inter;
    }

    @Override
    protected void storeInCache(InteractionEvidence originalObject, IntactInteractionEvidence persistentObject, IntactInteractionEvidence existingInstance) {
        if (existingInstance != null){
            this.persistedObjects.put(originalObject, existingInstance);
        }
        else{
            this.persistedObjects.put(originalObject, persistentObject);
        }
    }

    @Override
    protected IntactInteractionEvidence fetchObjectFromCache(InteractionEvidence object) {
        return this.persistedObjects.get(object);
    }

    @Override
    protected boolean isObjectStoredInCache(InteractionEvidence object) {
        return this.persistedObjects.containsKey(object);
    }

    @Override
    protected boolean containsObjectInstance(InteractionEvidence object) {
        return this.convertedObjects.containsKey(object);
    }

    @Override
    protected void removeObjectInstanceFromIdentityCache(InteractionEvidence object) {
        this.convertedObjects.remove(object);
    }

    @Override
    protected IntactInteractionEvidence fetchMatchingObjectFromIdentityCache(InteractionEvidence object) {
        return this.convertedObjects.get(object);
    }

    @Override
    protected void convertPersistableProperties(IntactInteractionEvidence intactInteraction) throws SynchronizerException, PersisterException, FinderException {
        // then check interaction detection method
        prepareInteractionType(intactInteraction, false);
        // then check participant identification method
        prepareConfidences(intactInteraction, false);
        // then check organism
        prepareParameters(intactInteraction, false);
        // then check annotations
        prepareAnnotations(intactInteraction, false);
        // then check xrefs
        prepareXrefs(intactInteraction, false);
        // then check interactions
        prepareParticipants(intactInteraction, false);
        // then check variable parameters
        prepareVariableParametersValues(intactInteraction, false);
    }

    @Override
    protected void storeObjectInIdentityCache(InteractionEvidence originalObject, IntactInteractionEvidence persistableObject) {
         this.convertedObjects.put(originalObject, persistableObject);
    }

    @Override
    protected boolean isObjectPartiallyInitialised(InteractionEvidence originalObject) {
        return false;
    }

    protected void prepareVariableParametersValues(IntactInteractionEvidence intactInteraction, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {

        if (intactInteraction.areVariableParameterValuesInitialized()){
            List<VariableParameterValueSet> parametersToPersist = new ArrayList<VariableParameterValueSet>(intactInteraction.getVariableParameterValues());
            intactInteraction.getVariableParameterValues().clear();
            int index = 0;
            try{
                for (VariableParameterValueSet param : parametersToPersist){
                    // do not persist or merge parameters because of cascades
                    VariableParameterValueSet expParam = enableSynchronization ?
                            getContext().getVariableParameterValueSetSynchronizer().synchronize(param, false) :
                            getContext().getVariableParameterValueSetSynchronizer().convertToPersistentObject(param);
                    // we have a different instance because needed to be synchronized
                    if (!intactInteraction.getVariableParameterValues().contains(expParam)){
                        intactInteraction.getVariableParameterValues().add(expParam);
                    }
                    index++;
                }
            }
            finally {
                // always add previous properties in case of exception
                if (index < parametersToPersist.size() - 1) {
                    for (int i = index; i < parametersToPersist.size(); i++) {
                        intactInteraction.getVariableParameterValues().add(parametersToPersist.get(i));
                    }
                }
            }
        }
    }

    protected void prepareParticipants(IntactInteractionEvidence intactInteraction, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        if (intactInteraction.areParticipantsInitialized()){
            List<ParticipantEvidence> participantsToPersist = new ArrayList<ParticipantEvidence>(intactInteraction.getParticipants());
            intactInteraction.getParticipants().clear();
            int index = 0;
            try{
                for (ParticipantEvidence participant : participantsToPersist){
                    // reinit parent
                    participant.setInteraction(intactInteraction);
                    // do not persist or merge participants because of cascades
                    ParticipantEvidence expPart = enableSynchronization ?
                            getContext().getParticipantEvidenceSynchronizer().synchronize(participant, false) :
                            getContext().getParticipantEvidenceSynchronizer().convertToPersistentObject(participant);
                    // we have a different instance because needed to be synchronized
                    if (!intactInteraction.getParticipants().contains(expPart)){
                        intactInteraction.addParticipant(expPart);
                    }
                    index++;
                }
            }
            finally {
                // always add previous properties in case of exception
                if (index < participantsToPersist.size() - 1) {
                    for (int i = index; i < participantsToPersist.size(); i++) {
                        intactInteraction.addParticipant(participantsToPersist.get(i));
                    }
                }
            }
        }
    }

    protected void prepareParameters(IntactInteractionEvidence intactInteraction, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        if (intactInteraction.areParametersInitialized()){
            List<Parameter> parametersToPersist = new ArrayList<Parameter>(intactInteraction.getParameters());
            intactInteraction.getParameters().clear();
            int index = 0;
            try{
                for (Parameter param : parametersToPersist){
                    // do not persist or merge parameters because of cascades
                    Parameter expPar = enableSynchronization ?
                            getContext().getInteractionParameterSynchronizer().synchronize(param, false) :
                            getContext().getInteractionParameterSynchronizer().convertToPersistentObject(param);
                    // we have a different instance because needed to be synchronized
                    if (!intactInteraction.getParameters().contains(expPar)){
                        intactInteraction.getParameters().add(expPar);
                    }
                    index++;
                }
            }
            finally {
                // always add previous properties in case of exception
                if (index < parametersToPersist.size() - 1){
                    for (int i = index; i < parametersToPersist.size(); i++){
                        intactInteraction.getParameters().add(parametersToPersist.get(i));
                    }
                }
            }
        }
    }

    protected void prepareInteractionType(IntactInteractionEvidence intactInteraction, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        CvTerm type = intactInteraction.getInteractionType();
        if (type != null){
            intactInteraction.setInteractionType(enableSynchronization ?
                    getContext().getInteractionTypeSynchronizer().synchronize(type, true) :
                    getContext().getInteractionTypeSynchronizer().convertToPersistentObject(type));
        }
    }

    protected void prepareXrefs(IntactInteractionEvidence intactInteraction, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactInteraction.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactInteraction.getDbXrefs());
            intactInteraction.getDbXrefs().clear();
            int index = 0;
            try{
                for (Xref xref : xrefsToPersist){
                    // do not persist or merge xrefs because of cascades
                    Xref expRef = enableSynchronization ?
                            getContext().getInteractionXrefSynchronizer().synchronize(xref, false) :
                            getContext().getInteractionXrefSynchronizer().convertToPersistentObject(xref);
                    // we have a different instance because needed to be synchronized
                    if (!intactInteraction.getDbXrefs().contains(expRef)){
                        intactInteraction.getDbXrefs().add(expRef);
                    }
                    index++;
                }
            }
            finally {
                // always add previous properties in case of exception
                if (index < xrefsToPersist.size() - 1){
                    for (int i = index; i < xrefsToPersist.size(); i++){
                        intactInteraction.getDbXrefs().add(xrefsToPersist.get(i));
                    }
                }
            }
        }
    }

    protected void prepareAnnotations(IntactInteractionEvidence intactInteraction, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactInteraction.areAnnotationsInitialized()){
            List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactInteraction.getDbAnnotations());
            intactInteraction.getDbAnnotations().clear();
            int index = 0;
            try{
                for (Annotation annotation : annotationsToPersist){
                    // do not persist or merge annotations because of cascades
                    Annotation expAnnotation = enableSynchronization ?
                            getContext().getInteractionAnnotationSynchronizer().synchronize(annotation, false) :
                            getContext().getInteractionAnnotationSynchronizer().convertToPersistentObject(annotation);
                    // we have a different instance because needed to be synchronized
                    if (!intactInteraction.getDbAnnotations().contains(expAnnotation)){
                        intactInteraction.getDbAnnotations().add(expAnnotation);
                    }
                    index++;
                }
            }
            finally {
                // always add previous properties in case of exception
                if (index < annotationsToPersist.size() - 1){
                    for (int i = index; i < annotationsToPersist.size(); i++){
                        intactInteraction.getDbAnnotations().add(annotationsToPersist.get(i));
                    }
                }
            }
        }
    }

    protected void prepareConfidences(IntactInteractionEvidence intactInteraction, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactInteraction.areConfidencesInitialized()){
            List<Confidence> confsToPersist = new ArrayList<Confidence>(intactInteraction.getConfidences());
            intactInteraction.getConfidences().clear();
            int index = 0;
            try{
                for (Confidence confidence : confsToPersist){
                    // do not persist or merge confidences because of cascades
                    Confidence expConf = enableSynchronization ?
                            getContext().getInteractionConfidenceSynchronizer().synchronize(confidence, false) :
                            getContext().getInteractionConfidenceSynchronizer().convertToPersistentObject(confidence);
                    // we have a different instance because needed to be synchronized
                    if (!intactInteraction.getConfidences().contains(expConf)){
                        intactInteraction.getConfidences().add(expConf);
                    }
                    index++;
                }
            }
            finally {
                // always add previous properties in case of exception
                if (index < confsToPersist.size() - 1){
                    for (int i = index; i < confsToPersist.size(); i++){
                        intactInteraction.getConfidences().add(confsToPersist.get(i));
                    }
                }
            }
        }
    }

    public void prepareAndSynchronizeShortLabel(IntactInteractionEvidence intactInteraction) throws SynchronizerException {
        // first initialise shortlabel if not done
        if (intactInteraction.getShortName() == null){
            intactInteraction.setShortName(IntactUtils.generateAutomaticInteractionEvidenceShortlabelFor(intactInteraction, IntactUtils.MAX_SHORT_LABEL_LEN));
        }

        String oldLabel = intactInteraction.getShortName();

        IntactUtils.synchronizeInteractionEvidenceShortName(intactInteraction, getEntityManager(), this.persistedNames);

        // only add name as persisted name if new object persisted or update in shortlabel
        if (intactInteraction.getAc() == null){
            this.persistedNames.add(intactInteraction.getShortName());
        }
        else if (!oldLabel.equals(intactInteraction.getShortName())){
            this.persistedNames.add(intactInteraction.getShortName());
        }
    }

    @Override
    protected void initialiseDefaultMerger() {
        InteractionEvidenceMergerEnrichOnly enricher = new InteractionEvidenceMergerEnrichOnly();
        enricher.setInteractionEnricherListener(this.enricherListener);
        super.setIntactMerger(enricher);
    }

    @Override
    public void setIntactMerger(IntactDbMerger<InteractionEvidence, IntactInteractionEvidence> intactMerger) {
        if (intactMerger instanceof InteractionEnricher){
            ((InteractionEnricher)intactMerger).setInteractionEnricherListener(enricherListener);
        }
        super.setIntactMerger(intactMerger);
    }

    @Override
    public void deleteRelatedProperties(IntactInteractionEvidence intactParticipant){
        for (Object f : intactParticipant.getParticipants()){
            getContext().getParticipantEvidenceSynchronizer().delete((ParticipantEvidence)f);
        }
        intactParticipant.getParticipants().clear();
    }

    @Override
    protected void resetObjectIdentifier(IntactInteractionEvidence intactObject) {
        intactObject.setAc(null);
    }
}
