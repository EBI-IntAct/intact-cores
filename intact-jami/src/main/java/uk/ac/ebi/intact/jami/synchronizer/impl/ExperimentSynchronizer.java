package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.collections.map.IdentityMap;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.ExperimentUtils;
import psidev.psi.mi.jami.utils.clone.ExperimentCloner;
import psidev.psi.mi.jami.utils.comparator.CollectionComparator;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.ExperimentMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.*;
import uk.ac.ebi.intact.jami.utils.comparator.IntactComparator;
import uk.ac.ebi.intact.jami.utils.comparator.IntactExperimentComparator;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Default synchronizer for publications
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public class ExperimentSynchronizer extends AbstractIntactDbSynchronizer<Experiment, IntactExperiment> {

    private Map<Experiment, IntactExperiment> persistedObjects;
    private Map<Experiment, IntactExperiment> convertedObjects;

    private CollectionComparator<Annotation> annotationCollectionComparator;
    private CollectionComparator<VariableParameter> variableParameterComparator;

    private IntactExperimentComparator experimentComparator;

    public ExperimentSynchronizer(SynchronizerContext context){
        super(context, IntactExperiment.class);
        // to keep track of persisted cvs
        this.experimentComparator = new IntactExperimentComparator();
        this.annotationCollectionComparator = experimentComparator.getAnnotationCollectionComparator();
        this.variableParameterComparator = experimentComparator.getVariableParameterCollectionComparator();

        this.persistedObjects = new TreeMap<Experiment, IntactExperiment>(experimentComparator);
        this.convertedObjects = new IdentityMap();
    }

    public IntactExperiment find(Experiment experiment) throws FinderException {
        if (experiment == null){
            return null;
        }
        else if (this.persistedObjects.containsKey(experiment)){
            return this.persistedObjects.get(experiment);
        }
        else{
            IntactPublication fetchedPublication = null;
            if (experiment.getPublication() != null){
                fetchedPublication = getContext().getPublicationSynchronizer().find(experiment.getPublication());
                // the publication does not exist so the experiment does not exist
                if (fetchedPublication == null || fetchedPublication.getAc() == null){
                    return null;
                }
            }
            IntactOrganism fetchedOrganism = null;
            if (experiment.getHostOrganism() != null){
                fetchedOrganism = getContext().getOrganismSynchronizer().find(experiment.getHostOrganism());
                // the organism does not exist so the experiment does not exist
                if (fetchedOrganism == null || fetchedOrganism.getAc() == null){
                    return null;
                }
            }
            IntactCvTerm fetchedDetectionMethod = null;
            if (experiment.getInteractionDetectionMethod() != null){
                fetchedDetectionMethod = getContext().getInteractionDetectionMethodSynchronizer().find(experiment.getInteractionDetectionMethod());
                // the detection method does not exist so the experiment does not exist
                if (fetchedDetectionMethod == null || fetchedDetectionMethod.getAc() == null){
                    return null;
                }
            }
            IntactCvTerm fetchedParticipantDetectionMethod = null;
            CvTerm commonMethod = ExperimentUtils.extractMostCommonParticipantDetectionMethodFrom(experiment);
            if (commonMethod != null){
                fetchedParticipantDetectionMethod = getContext().getParticipantDetectionMethodSynchronizer().find(commonMethod);
                // the participant detection method does not exist so the experiment does not exist
                if (fetchedParticipantDetectionMethod == null || fetchedParticipantDetectionMethod.getAc() == null){
                    return null;
                }
            }

            Query query = getEntityManager().createQuery("select e from IntactExperiment e " +
                    (fetchedOrganism != null ? "join e.hostOrganism as h " : "" ) +
                    (fetchedDetectionMethod != null ? "join e.interactionDetectionMethod as det " : "" ) +
                    (fetchedPublication != null ? "join e.publication as p " : "" ) +
                    (fetchedParticipantDetectionMethod != null ? "join e.participantIdentificationMethod as ident " : "" ) +
                    "where "+
                    (fetchedOrganism != null ? "h.ac = :orgAc " : "e.hostOrganism is null " ) +
                    (fetchedDetectionMethod != null ? "and det.ac = :detAc " : "and e.interactionDetectionMethod is null " ) +
                    (fetchedPublication != null ? "and p.ac = :pubAc " : "and e.publication is null " ) +
                    (fetchedParticipantDetectionMethod != null ? "and ident.ac = :identAc" : "and e.participantIdentificationMethod is null" ));
            if (fetchedOrganism != null){
                query.setParameter("orgAc", fetchedOrganism.getAc());
            }
            if (fetchedDetectionMethod != null){
                query.setParameter("detAc", fetchedDetectionMethod.getAc());
            }
            if (fetchedPublication != null){
                query.setParameter("pubAc", fetchedPublication.getAc());
            }
            if (fetchedParticipantDetectionMethod != null){
                query.setParameter("identAc", fetchedParticipantDetectionMethod.getAc());
            }

            Collection<IntactExperiment> results = query.getResultList();
            if (!results.isEmpty()){
                Collection<IntactExperiment> filteredResults = new ArrayList<IntactExperiment>(results.size());
                for (IntactExperiment exp : filteredResults){
                    if (this.annotationCollectionComparator.compare(experiment.getAnnotations(), exp.getAnnotations()) == 0
                            && this.variableParameterComparator.compare(experiment.getVariableParameters(), exp.getVariableParameters()) == 0){
                        filteredResults.add(exp);
                    }
                }

                if (filteredResults.size() == 1){
                    return filteredResults.iterator().next();
                }
                else if (filteredResults.size() > 1){
                    throw new FinderException("The experiment "+experiment.toString() + " can match "+filteredResults.size()+" experiments in the database and we cannot determine which one is valid.");
                }
            }
            return null;
        }
    }

    public void synchronizeProperties(IntactExperiment intactExperiment) throws FinderException, PersisterException, SynchronizerException {
        // then check shortlabel/synchronize
        prepareAndSynchronizeShortLabel(intactExperiment);
        // then check interaction detection method
        prepareInteractionDetectionMethod(intactExperiment, true);
        // then check participant identification method
        prepareParticipantIdentificationMethod(intactExperiment, true);
        // then check organism
        prepareHostOrganism(intactExperiment, true);
        // then check annotations
        prepareAnnotations(intactExperiment, true);
        // then check xrefs
        prepareXrefs(intactExperiment, true);
        // then check interactions
        prepareInteractions(intactExperiment, true);
        // then check variable parameters
        prepareVariableParameters(intactExperiment, true);
    }

    public void clearCache() {
        this.persistedObjects.clear();
        this.convertedObjects.clear();
    }
    @Override
    protected Object extractIdentifier(IntactExperiment object) {
        return object.getAc();
    }

    @Override
    protected IntactExperiment instantiateNewPersistentInstance(Experiment object, Class<? extends IntactExperiment> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactExperiment exp = new IntactExperiment(object.getPublication());
        ExperimentCloner.copyAndOverrideExperimentPropertiesAndInteractionEvidences(object, exp);
        return exp;
    }

    @Override
    protected void storeInCache(Experiment originalObject, IntactExperiment persistentObject, IntactExperiment existingInstance) {
        if (existingInstance != null){
            this.persistedObjects.put(originalObject, existingInstance);
        }
        else{
            this.persistedObjects.put(originalObject, persistentObject);
        }
    }

    @Override
    protected IntactExperiment fetchObjectFromCache(Experiment object) {
        return this.persistedObjects.get(object);
    }

    @Override
    protected boolean isObjectStoredInCache(Experiment object) {
        return this.persistedObjects.containsKey(object);
    }

    @Override
    protected boolean containsObjectInstance(Experiment object) {
        return this.convertedObjects.containsKey(object);
    }

    @Override
    protected void removeObjectInstanceFromIdentityCache(Experiment object) {
        this.convertedObjects.remove(object);
    }

    @Override
    protected IntactExperiment fetchMatchingObjectFromIdentityCache(Experiment object) {
        return this.convertedObjects.get(object);
    }

    @Override
    protected void convertPersistableProperties(IntactExperiment intactExperiment) throws SynchronizerException, PersisterException, FinderException {
        // then check interaction detection method
        prepareInteractionDetectionMethod(intactExperiment, false);
        // then check participant identification method
        prepareParticipantIdentificationMethod(intactExperiment, false);
        // then check organism
        prepareHostOrganism(intactExperiment, false);
        // then check annotations
        prepareAnnotations(intactExperiment, false);
        // then check xrefs
        prepareXrefs(intactExperiment, false);
        // then check interactions
        prepareInteractions(intactExperiment, false);
        // then check variable parameters
        prepareVariableParameters(intactExperiment, false);
    }

    @Override
    protected void storeObjectInIdentityCache(Experiment originalObject, IntactExperiment persistableObject) {
        this.convertedObjects.put(originalObject, persistableObject);
    }

    @Override
    protected boolean isObjectDirty(Experiment originalObject) {
        return !this.experimentComparator.canCompare(originalObject);
    }

    protected void prepareVariableParameters(IntactExperiment intactExperiment, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {

        if (intactExperiment.areVariableParametersInitialized()){
            Collection<VariableParameter> parametersToPersist = new ArrayList<VariableParameter>(intactExperiment.getVariableParameters());
            for (VariableParameter param : parametersToPersist){
                // do not persist or merge parameters because of cascades
                VariableParameter expParam = enableSynchronization ?
                        getContext().getVariableParameterSynchronizer().synchronize(param, false) :
                        getContext().getVariableParameterSynchronizer().convertToPersistentObject(param);
                // we have a different instance because needed to be synchronized
                if (expParam != param){
                    intactExperiment.getVariableParameters().remove(param);
                    intactExperiment.addVariableParameter(expParam);
                }
            }
        }
    }

    protected void prepareInteractions(IntactExperiment intactExperiment, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        if (intactExperiment.areInteractionEvidencesInitialized()){
            Collection<InteractionEvidence> interactionsToPersist = new ArrayList<InteractionEvidence>(intactExperiment.getInteractionEvidences());
            for (InteractionEvidence interaction : interactionsToPersist){
                // do not persist or merge interactions because of cascades
                InteractionEvidence expInter = enableSynchronization ?
                        getContext().getInteractionSynchronizer().synchronize(interaction, false) :
                        getContext().getInteractionSynchronizer().convertToPersistentObject(interaction);
                // we have a different instance because needed to be synchronized
                if (expInter != interaction){
                    intactExperiment.getInteractionEvidences().remove(interaction);
                    intactExperiment.addInteractionEvidence(expInter);
                }
            }
        }
    }

    protected void prepareHostOrganism(IntactExperiment intactExperiment, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        Organism host = intactExperiment.getHostOrganism();
        if (host != null){
            intactExperiment.setHostOrganism(enableSynchronization ?
                    getContext().getOrganismSynchronizer().synchronize(host, true) :
                    getContext().getOrganismSynchronizer().convertToPersistentObject(host));
        }
    }

    protected void prepareInteractionDetectionMethod(IntactExperiment intactExperiment, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        CvTerm detectionMethod = intactExperiment.getInteractionDetectionMethod();
        if (detectionMethod != null){
            intactExperiment.setInteractionDetectionMethod(enableSynchronization ?
                    getContext().getInteractionDetectionMethodSynchronizer().synchronize(detectionMethod, true) :
                    getContext().getInteractionDetectionMethodSynchronizer().convertToPersistentObject(detectionMethod));
        }
    }

    protected void prepareXrefs(IntactExperiment intactExperiment, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactExperiment.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactExperiment.getXrefs());
            for (Xref xref : xrefsToPersist){
                // do not persist or merge xrefs because of cascades
                Xref expRef = enableSynchronization ?
                        getContext().getExperimentXrefSynchronizer().synchronize(xref, false) :
                        getContext().getExperimentXrefSynchronizer().convertToPersistentObject(xref);
                // we have a different instance because needed to be synchronized
                if (expRef != xref){
                    intactExperiment.getXrefs().remove(xref);
                    intactExperiment.getXrefs().add(expRef);
                }
            }
        }
    }

    protected void prepareAnnotations(IntactExperiment intactExperiment, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactExperiment.areAnnotationsInitialized()){
            List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactExperiment.getAnnotations());
            for (Annotation annotation : annotationsToPersist){
                // do not persist or merge annotations because of cascades
                Annotation expAnnotation = enableSynchronization ?
                        getContext().getExperimentAnnotationSynchronizer().synchronize(annotation, false) :
                        getContext().getExperimentAnnotationSynchronizer().convertToPersistentObject(annotation);
                // we have a different instance because needed to be synchronized
                if (expAnnotation != annotation){
                    intactExperiment.getAnnotations().remove(annotation);
                    intactExperiment.getAnnotations().add(expAnnotation);
                }
            }
        }
    }

    protected void prepareParticipantIdentificationMethod(IntactExperiment intactExperiment, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        CvTerm detectionMethod = intactExperiment.getParticipantIdentificationMethod();
        if (detectionMethod != null){
            intactExperiment.setParticipantIdentificationMethod(enableSynchronization ?
                    getContext().getParticipantDetectionMethodSynchronizer().synchronize(detectionMethod, true) :
                    getContext().getParticipantDetectionMethodSynchronizer().convertToPersistentObject(detectionMethod));
        }
        else{
            detectionMethod = ExperimentUtils.extractMostCommonParticipantDetectionMethodFrom(intactExperiment);
            intactExperiment.setParticipantIdentificationMethod(enableSynchronization ?
                    getContext().getParticipantDetectionMethodSynchronizer().synchronize(detectionMethod, true) :
                    getContext().getParticipantDetectionMethodSynchronizer().convertToPersistentObject(detectionMethod));
        }
    }

    protected void prepareAndSynchronizeShortLabel(IntactExperiment intactExperiment) throws SynchronizerException {
        // first initialise shortlabel if not done
        if (intactExperiment.getShortLabel() == null){
            intactExperiment.setShortLabel(IntactUtils.generateAutomaticExperimentShortlabelFor(intactExperiment, IntactUtils.MAX_SHORT_LABEL_LEN));
        }

        // then synchronize with database
        String name;
        List<String> existingExperiments;
        do{
            name = intactExperiment.getShortLabel().trim().toLowerCase();
            existingExperiments = Collections.EMPTY_LIST;

            // check if short name already exist, if yes, synchronize with existing label
            Query query = getEntityManager().createQuery("select e.shortLabel from IntactExperiment e " +
                    "where (e.shortLabel = :name or e.shortLabel like :nameWithSuffix) "
                    + (intactExperiment.getAc() != null ? "and e.ac <> :expAc" : ""));
            query.setParameter("name", name);
            query.setParameter("nameWithSuffix", name+"-%");
            if (intactExperiment.getAc() != null){
                query.setParameter("expAc", intactExperiment.getAc());
            }
            existingExperiments = query.getResultList();
            if (!existingExperiments.isEmpty()){
                String nameInSync = IntactUtils.synchronizeShortlabel(name, existingExperiments, IntactUtils.MAX_SHORT_LABEL_LEN, true);
                if (!nameInSync.equals(name)){
                    intactExperiment.setShortLabel(nameInSync);
                }
                else{
                    break;
                }
            }
            else{
                intactExperiment.setShortLabel(name);
            }
        }
        while(!existingExperiments.isEmpty());
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new ExperimentMergerEnrichOnly());
    }

    @Override
    public void deleteRelatedProperties(IntactExperiment intactParticipant){
        for (InteractionEvidence f : intactParticipant.getInteractionEvidences()){
            getContext().getInteractionSynchronizer().delete(f);
        }
        intactParticipant.getInteractionEvidences().clear();
    }
}
