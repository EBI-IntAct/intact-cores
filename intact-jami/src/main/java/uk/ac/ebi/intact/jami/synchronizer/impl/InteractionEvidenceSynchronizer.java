package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.collections.map.IdentityMap;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.InteractionCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.InteractionEvidenceMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractionEvidence;
import uk.ac.ebi.intact.jami.synchronizer.AbstractIntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
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

public class InteractionEvidenceSynchronizer extends AbstractIntactDbSynchronizer<InteractionEvidence, IntactInteractionEvidence> {

    private Map<InteractionEvidence, IntactInteractionEvidence> persistedObjects;
    private Map<InteractionEvidence, IntactInteractionEvidence> convertedObjects;

    private Set<String> persistedNames;

    public InteractionEvidenceSynchronizer(SynchronizerContext context){
        super(context, IntactInteractionEvidence.class);
        // to keep track of persisted cvs
        this.persistedObjects = new IdentityMap();
        this.convertedObjects = new IdentityMap();
        this.persistedNames = new HashSet<String>();
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
            Collection<VariableParameterValueSet> parametersToPersist = new ArrayList<VariableParameterValueSet>(intactInteraction.getVariableParameterValues());
            intactInteraction.getVariableParameterValues().clear();
            for (VariableParameterValueSet param : parametersToPersist){
                // do not persist or merge parameters because of cascades
                VariableParameterValueSet expParam = enableSynchronization ?
                        getContext().getVariableParameterValueSetSynchronizer().synchronize(param, false) :
                        getContext().getVariableParameterValueSetSynchronizer().convertToPersistentObject(param);
                // we have a different instance because needed to be synchronized
                intactInteraction.getVariableParameterValues().add(expParam);
            }
        }
    }

    protected void prepareParticipants(IntactInteractionEvidence intactInteraction, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        if (intactInteraction.areParticipantsInitialized()){
            Collection<ParticipantEvidence> participantsToPersist = new ArrayList<ParticipantEvidence>(intactInteraction.getParticipants());
            intactInteraction.getParticipants().clear();
            for (ParticipantEvidence participant : participantsToPersist){
                // reinit parent
                participant.setInteraction(intactInteraction);
                // do not persist or merge participants because of cascades
                ParticipantEvidence expPart = enableSynchronization ?
                        getContext().getParticipantEvidenceSynchronizer().synchronize(participant, false) :
                        getContext().getParticipantEvidenceSynchronizer().convertToPersistentObject(participant);
                // we have a different instance because needed to be synchronized
                intactInteraction.addParticipant(expPart);
            }
        }
    }

    protected void prepareParameters(IntactInteractionEvidence intactInteraction, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        if (intactInteraction.areParametersInitialized()){
            Collection<Parameter> parametersToPersist = new ArrayList<Parameter>(intactInteraction.getParameters());
            intactInteraction.getParameters().clear();
            for (Parameter param : parametersToPersist){
                // do not persist or merge parameters because of cascades
                Parameter expPar = enableSynchronization ?
                        getContext().getInteractionParameterSynchronizer().synchronize(param, false) :
                        getContext().getInteractionParameterSynchronizer().convertToPersistentObject(param);
                // we have a different instance because needed to be synchronized
                intactInteraction.getParameters().add(expPar);
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
            for (Xref xref : xrefsToPersist){
                // do not persist or merge xrefs because of cascades
                Xref expRef = enableSynchronization ?
                        getContext().getInteractionXrefSynchronizer().synchronize(xref, false) :
                        getContext().getInteractionXrefSynchronizer().convertToPersistentObject(xref);
                // we have a different instance because needed to be synchronized
                intactInteraction.getDbXrefs().add(expRef);
            }
        }
    }

    protected void prepareAnnotations(IntactInteractionEvidence intactInteraction, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactInteraction.areAnnotationsInitialized()){
            List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactInteraction.getDbAnnotations());
            intactInteraction.getDbAnnotations().clear();
            for (Annotation annotation : annotationsToPersist){
                // do not persist or merge annotations because of cascades
                Annotation expAnnotation = enableSynchronization ?
                        getContext().getInteractionAnnotationSynchronizer().synchronize(annotation, false) :
                        getContext().getInteractionAnnotationSynchronizer().convertToPersistentObject(annotation);
                // we have a different instance because needed to be synchronized
                intactInteraction.getDbAnnotations().add(expAnnotation);
            }
        }
    }

    protected void prepareConfidences(IntactInteractionEvidence intactInteraction, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactInteraction.areConfidencesInitialized()){
            List<Confidence> confsToPersist = new ArrayList<Confidence>(intactInteraction.getConfidences());
            intactInteraction.getConfidences().clear();
            for (Confidence confidence : confsToPersist){
                // do not persist or merge confidences because of cascades
                Confidence expConf = enableSynchronization ?
                        getContext().getInteractionConfidenceSynchronizer().synchronize(confidence, false) :
                        getContext().getInteractionConfidenceSynchronizer().convertToPersistentObject(confidence);
                // we have a different instance because needed to be synchronized
                intactInteraction.getConfidences().add(expConf);
            }
        }
    }

    protected void prepareAndSynchronizeShortLabel(IntactInteractionEvidence intactInteraction) throws SynchronizerException {
        // first initialise shortlabel if not done
        if (intactInteraction.getShortName() == null){
            intactInteraction.setShortName(IntactUtils.generateAutomaticInteractionEvidenceShortlabelFor(intactInteraction, IntactUtils.MAX_SHORT_LABEL_LEN));
        }

        IntactUtils.synchronizeInteractionEvidenceShortName(intactInteraction, getEntityManager(), this.persistedNames);

        this.persistedNames.add(intactInteraction.getShortName());
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new InteractionEvidenceMergerEnrichOnly());
    }

    @Override
    public void deleteRelatedProperties(IntactInteractionEvidence intactParticipant){
        for (Object f : intactParticipant.getParticipants()){
            getContext().getParticipantEvidenceSynchronizer().delete((ParticipantEvidence)f);
        }
        intactParticipant.getParticipants().clear();
    }

    @Override
    protected void mergeWithCache(InteractionEvidence object, IntactInteractionEvidence existingInstance) throws PersisterException, FinderException, SynchronizerException {
        // store object in a identity cache so no lazy properties can be called before synchronization
        storeObjectInIdentityCache(object, existingInstance);
        // then check new confidences if any
        prepareConfidences(existingInstance, true);
        // then check new parameters if any
        prepareParameters(existingInstance, true);
        // then check new annotations if any
        prepareAnnotations(existingInstance, true);
        // then check new xrefs if any
        prepareXrefs(existingInstance, true);
        // then check new interactions if any
        prepareParticipants(existingInstance, true);
        // then check new variable parameters if any
        prepareVariableParametersValues(existingInstance, true);
        // remove object from identity cache as not dirty anymore
        removeObjectInstanceFromIdentityCache(object);
    }
}
