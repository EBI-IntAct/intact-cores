package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.ExperimentUtils;
import psidev.psi.mi.jami.utils.clone.ExperimentCloner;
import psidev.psi.mi.jami.utils.comparator.CollectionComparator;
import uk.ac.ebi.intact.jami.merger.ExperimentMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.impl.AnnotationSynchronizerTemplate;
import uk.ac.ebi.intact.jami.synchronizer.impl.CvTermSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.impl.OrganismSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.impl.XrefSynchronizerTemplate;
import uk.ac.ebi.intact.jami.utils.IntactExperimentComparator;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
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

public class IntactExperimentSynchronizer extends AbstractIntactDbSynchronizer<Experiment, IntactExperiment> {

    private Map<Experiment, IntactExperiment> persistedObjects;

    private IntactDbSynchronizer<Annotation, ExperimentAnnotation> annotationSynchronizer;
    private IntactDbSynchronizer<Xref, ExperimentXref> xrefSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> detectionMethodSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> participantDetectionMethodSynchronizer;
    private IntactDbSynchronizer<Publication, IntactPublication> publicationSynchronizer;
    private IntactDbSynchronizer<InteractionEvidence, IntactInteractionEvidence> interactionSynchronizer;
    private IntactDbSynchronizer<VariableParameter, IntactVariableParameter> variableParameterSynchronizer;
    private IntactDbSynchronizer<Organism, IntactOrganism> organismSynchronizer;

    private CollectionComparator<Annotation> annotationCollectionComparator;
    private CollectionComparator<VariableParameter> variableParameterComparator;

    private static final Log log = LogFactory.getLog(IntactExperimentSynchronizer.class);

    public IntactExperimentSynchronizer(EntityManager entityManager){
        super(entityManager, IntactExperiment.class);
        // to keep track of persisted cvs
        IntactExperimentComparator comp = new IntactExperimentComparator();
        this.annotationCollectionComparator = comp.getAnnotationCollectionComparator();
        this.variableParameterComparator = comp.getVariableParameterCollectionComparator();

        this.persistedObjects = new TreeMap<Experiment, IntactExperiment>(comp);
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
                fetchedPublication = getPublicationSynchronizer().find(experiment.getPublication());
                // the publication does not exist so the experiment does not exist
                if (fetchedPublication == null){
                    return null;
                }
            }
            IntactOrganism fetchedOrganism = null;
            if (experiment.getHostOrganism() != null){
                fetchedOrganism = getOrganismSynchronizer().find(experiment.getHostOrganism());
                // the organism does not exist so the experiment does not exist
                if (fetchedOrganism == null){
                    return null;
                }
            }
            IntactCvTerm fetchedDetectionMethod = null;
            if (experiment.getInteractionDetectionMethod() != null){
                fetchedDetectionMethod = getDetectionMethodSynchronizer().find(experiment.getInteractionDetectionMethod());
                // the detection method does not exist so the experiment does not exist
                if (fetchedDetectionMethod == null){
                    return null;
                }
            }
            IntactCvTerm fetchedParticipantDetectionMethod = null;
            CvTerm commonMethod = ExperimentUtils.extractMostCommonParticipantDetectionMethodFrom(experiment);
            if (commonMethod != null){
                fetchedParticipantDetectionMethod = getParticipantDetectionMethodSynchronizer().find(commonMethod);
                // the participant detection method does not exist so the experiment does not exist
                if (fetchedParticipantDetectionMethod == null){
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
                    (fetchedDetectionMethod != null ? "det.ac = :detAc " : "e.interactionDetectionMethod is null " ) +
                    (fetchedPublication != null ? "p.ac = :pubAc " : "e.publication is null " ) +
                    (fetchedParticipantDetectionMethod != null ? "ident.ac = :identAc" : "e.participantIdentificationMethod is null" ));
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

    public IntactExperiment persist(IntactExperiment object) throws FinderException, PersisterException, SynchronizerException {
        // only persist if not already done
        if (!this.persistedObjects.containsKey(object)){
            return this.persistedObjects.get(object);
        }

        IntactExperiment persisted = super.persist(object);
        this.persistedObjects.put(object, persisted);

        return persisted;
    }

    @Override
    public IntactExperiment synchronize(Experiment object, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        // only synchronize if not already done
        if (!this.persistedObjects.containsKey(object)){
            return this.persistedObjects.get(object);
        }

        IntactExperiment persisted = super.synchronize(object, persist);
        this.persistedObjects.put(object, persisted);

        return persisted;
    }

    public void synchronizeProperties(IntactExperiment intactExperiment) throws FinderException, PersisterException, SynchronizerException {
        // then check shortlabel/synchronize
        prepareAndSynchronizeShortLabel(intactExperiment);
        // then check interaction detection method
        prepareInteractionDetectionMethod(intactExperiment);
        // then check participant identification method
        prepareParticipantIdentificationMethod(intactExperiment);
        // then check organism
        prepareHostOrganism(intactExperiment);
        // then check annotations
        prepareAnnotations(intactExperiment);
        // then check xrefs
        prepareXrefs(intactExperiment);
        // then check interactions
        prepareInteractions(intactExperiment);
        // then check variable parameters
        prepareVariableParameters(intactExperiment);
    }

    public void clearCache() {
        this.persistedObjects.clear();
        getInteractionSynchronizer().clearCache();
        getXrefSynchronizer().clearCache();
        getAnnotationSynchronizer().clearCache();
        getVariableParameterSynchronizer().clearCache();
        getOrganismSynchronizer().clearCache();
        getDetectionMethodSynchronizer().clearCache();
        getParticipantDetectionMethodSynchronizer().clearCache();
        getPublicationSynchronizer().clearCache();
    }

    public IntactDbSynchronizer<Annotation, ExperimentAnnotation> getAnnotationSynchronizer() {
        if (this.annotationSynchronizer == null){
            this.annotationSynchronizer = new AnnotationSynchronizerTemplate<ExperimentAnnotation>(getEntityManager(), ExperimentAnnotation.class);
        }
        return annotationSynchronizer;
    }

    public void setAnnotationSynchronizer(IntactDbSynchronizer<Annotation, ExperimentAnnotation> annotationSynchronizer) {
        this.annotationSynchronizer = annotationSynchronizer;
    }

    public IntactDbSynchronizer<Xref, ExperimentXref> getXrefSynchronizer() {
        if (this.xrefSynchronizer == null){
            this.xrefSynchronizer = new XrefSynchronizerTemplate<ExperimentXref>(getEntityManager(), ExperimentXref.class);
        }
        return xrefSynchronizer;
    }

    public void setXrefSynchronizer(IntactDbSynchronizer<Xref, ExperimentXref> xrefSynchronizer) {
        this.xrefSynchronizer = xrefSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getDetectionMethodSynchronizer() {
        if (this.detectionMethodSynchronizer == null){
            this.detectionMethodSynchronizer = new CvTermSynchronizer(getEntityManager(), IntactUtils.INTERACTION_DETECTION_METHOD_OBJCLASS);
        }
        return detectionMethodSynchronizer;
    }

    public void setDetectionMethodSynchronizer(IntactDbSynchronizer<CvTerm, IntactCvTerm> detectionMethodSynchronizer) {
        this.detectionMethodSynchronizer = detectionMethodSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getParticipantDetectionMethodSynchronizer() {
        if (this.participantDetectionMethodSynchronizer == null){
            this.participantDetectionMethodSynchronizer = new CvTermSynchronizer(getEntityManager(), IntactUtils.PARTICIPANT_DETECTION_METHOD_OBJCLASS);
        }
        return participantDetectionMethodSynchronizer;
    }

    public void setParticipantDetectionMethodSynchronizer(IntactDbSynchronizer<CvTerm, IntactCvTerm> participantDetectionMethodSynchronizer) {
        this.participantDetectionMethodSynchronizer = participantDetectionMethodSynchronizer;
    }

    public IntactDbSynchronizer<Publication, IntactPublication> getPublicationSynchronizer() {
        if (this.publicationSynchronizer == null){
            this.publicationSynchronizer = new IntactPublicationSynchronizer(getEntityManager());

        }
        return publicationSynchronizer;
    }

    public void setPublicationSynchronizer(IntactDbSynchronizer<Publication, IntactPublication> publicationSynchronizer) {
        this.publicationSynchronizer = publicationSynchronizer;
    }

    public IntactDbSynchronizer<InteractionEvidence, IntactInteractionEvidence> getInteractionSynchronizer() {
        if (this.interactionSynchronizer == null){
            this.interactionSynchronizer = new IntactInteractionEvidenceSynchronizer(getEntityManager());
        }
        return interactionSynchronizer;
    }

    public void setInteractionSynchronizer(IntactDbSynchronizer<InteractionEvidence, IntactInteractionEvidence> interactionSynchronizer) {
        this.interactionSynchronizer = interactionSynchronizer;
    }

    public IntactDbSynchronizer<VariableParameter, IntactVariableParameter> getVariableParameterSynchronizer() {
        if (this.variableParameterSynchronizer == null){
            this.variableParameterSynchronizer = new IntactVariableParameterSynchronizer(getEntityManager());
        }
        return variableParameterSynchronizer;
    }

    public void setVariableParameterSynchronizer(IntactDbSynchronizer<VariableParameter, IntactVariableParameter> variableParameterSynchronizer) {
        this.variableParameterSynchronizer = variableParameterSynchronizer;
    }

    public IntactDbSynchronizer<Organism, IntactOrganism> getOrganismSynchronizer() {
        if (this.organismSynchronizer == null){
            this.organismSynchronizer = new OrganismSynchronizer(getEntityManager());
        }
        return organismSynchronizer;
    }

    public void setOrganismSynchronizer(IntactDbSynchronizer<Organism, IntactOrganism> organismSynchronizer) {
        this.organismSynchronizer = organismSynchronizer;
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

    protected void prepareVariableParameters(IntactExperiment intactExperiment) throws PersisterException, FinderException, SynchronizerException {

        if (intactExperiment.areVariableParametersInitialized()){
            Collection<VariableParameter> parametersToPersist = new ArrayList<VariableParameter>(intactExperiment.getVariableParameters());
            for (VariableParameter param : parametersToPersist){
                // do not persist or merge parameters because of cascades
                VariableParameter expParam = getVariableParameterSynchronizer().synchronize(param, false);
                // we have a different instance because needed to be synchronized
                if (expParam != param){
                    intactExperiment.getVariableParameters().remove(param);
                    intactExperiment.addVariableParameter(expParam);
                }
            }
        }
    }

    protected void prepareInteractions(IntactExperiment intactExperiment) throws PersisterException, FinderException, SynchronizerException {
        if (intactExperiment.areInteractionEvidencesInitialized()){
            Collection<InteractionEvidence> interactionsToPersist = new ArrayList<InteractionEvidence>(intactExperiment.getInteractionEvidences());
            for (InteractionEvidence interaction : interactionsToPersist){
                // do not persist or merge interactions because of cascades
                InteractionEvidence expInter = getInteractionSynchronizer().synchronize(interaction, false);
                // we have a different instance because needed to be synchronized
                if (expInter != interaction){
                    intactExperiment.getInteractionEvidences().remove(interaction);
                    intactExperiment.addInteractionEvidence(expInter);
                }
            }
        }
    }

    protected void prepareHostOrganism(IntactExperiment intactExperiment) throws PersisterException, FinderException, SynchronizerException {
        Organism host = intactExperiment.getHostOrganism();
        if (host != null){
            intactExperiment.setHostOrganism(getOrganismSynchronizer().synchronize(host, true));
        }
    }

    protected void prepareInteractionDetectionMethod(IntactExperiment intactExperiment) throws PersisterException, FinderException, SynchronizerException {
        CvTerm detectionMethod = intactExperiment.getInteractionDetectionMethod();
        if (detectionMethod != null){
            intactExperiment.setInteractionDetectionMethod(getDetectionMethodSynchronizer().synchronize(detectionMethod, true));
        }
    }

    protected void prepareXrefs(IntactExperiment intactExperiment) throws FinderException, PersisterException, SynchronizerException {
        if (intactExperiment.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactExperiment.getXrefs());
            for (Xref xref : xrefsToPersist){
                // do not persist or merge xrefs because of cascades
                Xref expRef = getXrefSynchronizer().synchronize(xref, false);
                // we have a different instance because needed to be synchronized
                if (expRef != xref){
                    intactExperiment.getXrefs().remove(xref);
                    intactExperiment.getXrefs().add(expRef);
                }
            }
        }
    }

    protected void prepareAnnotations(IntactExperiment intactExperiment) throws FinderException, PersisterException, SynchronizerException {
        if (intactExperiment.areAnnotationsInitialized()){
            List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactExperiment.getAnnotations());
            for (Annotation annotation : annotationsToPersist){
                // do not persist or merge annotations because of cascades
                Annotation expAnnotation = getAnnotationSynchronizer().synchronize(annotation, false);
                // we have a different instance because needed to be synchronized
                if (expAnnotation != annotation){
                    intactExperiment.getAnnotations().remove(annotation);
                    intactExperiment.getAnnotations().add(expAnnotation);
                }
            }
        }
    }

    protected void prepareParticipantIdentificationMethod(IntactExperiment intactExperiment) throws FinderException, PersisterException, SynchronizerException {
        CvTerm detectionMethod = intactExperiment.getParticipantIdentificationMethod();
        if (detectionMethod != null){
            intactExperiment.setParticipantIdentificationMethod(getParticipantDetectionMethodSynchronizer().synchronize(detectionMethod, true));
        }
        else{
            intactExperiment.setParticipantIdentificationMethod(ExperimentUtils.extractMostCommonParticipantDetectionMethodFrom(intactExperiment));
        }
    }

    protected void prepareAndSynchronizeShortLabel(IntactExperiment intactExperiment) throws SynchronizerException {
        // first initialise shortlabel if not done
        if (intactExperiment.getShortLabel() == null){
            intactExperiment.setShortLabel(IntactUtils.generateAutomaticExperimentShortlabelFor(intactExperiment, IntactUtils.MAX_SHORT_LABEL_LEN));
        }

        // then synchronize with database
        boolean first = true;
        String name;
        List<String> existingExperiments;
        do{
            name = intactExperiment.getShortLabel().trim().toLowerCase();
            existingExperiments = Collections.EMPTY_LIST;
            String originalName = first ? name : IntactUtils.excludeLastNumberInShortLabel(name);

            if (first){
                first = false;
            }
            // don't truncate year so we remove year (4 characters + 1 for the '-') in addition to the last character of the author (total remove 6 characters)
            else if (originalName.length() > 6){
                name = originalName.substring(0, name.length() - 6);
            }
            else {
                break;
            }

            // check if short name already exist, if yes, synchronize with existing label
            Query query = getEntityManager().createQuery("select e.shortLabel from IntactExperiment e " +
                    "where e.shortLabel = :name or e.shortLabel like :nameWithSuffix"
                    + (intactExperiment.getAc() != null ? "and e.ac <> :expAc" : ""));
            query.setParameter("name", name);
            query.setParameter("nameWithSuffix", name+"-%");
            if (intactExperiment.getAc() != null){
                query.setParameter("expAc", intactExperiment.getAc());
            }
            existingExperiments = query.getResultList();
            String nameInSync = IntactUtils.synchronizeShortlabel(name, existingExperiments, IntactUtils.MAX_SHORT_LABEL_LEN, true);
            intactExperiment.setShortLabel(nameInSync);
        }
        while(name.length() > 6 && !existingExperiments.isEmpty());
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new ExperimentMergerEnrichOnly());
    }
}
