package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.ParticipantCloner;
import uk.ac.ebi.intact.jami.merger.IntactExperimentalEntityMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Synchronizer for experimental entities and participants
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/01/14</pre>
 */

public class IntactExperimentalEntityBaseSynchronizer<T extends ExperimentalEntity, I extends IntactParticipantEvidence> extends IntactEntityBaseSynchronizer<T, I>{

    private static final Log log = LogFactory.getLog(IntactExperimentalEntityBaseSynchronizer.class);
    private IntactDbSynchronizer<Parameter, ExperimentalEntityParameter> parameterSynchronizer;
    private IntactDbSynchronizer<Confidence, ExperimentalEntityConfidence> confidenceSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> identificationMethodSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> experimentalPreparationSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> experimentalRoleSynchronizer;
    private IntactDbSynchronizer<Organism, IntactOrganism> organismSynchronizer;

    public IntactExperimentalEntityBaseSynchronizer(EntityManager entityManager, Class<I> intactClass){
        super(entityManager, intactClass);
        this.parameterSynchronizer = new IntactParameterSynchronizer<Parameter, ExperimentalEntityParameter>(entityManager, ExperimentalEntityParameter.class);
        this.confidenceSynchronizer = new IntactConfidenceSynchronizer<Confidence, ExperimentalEntityConfidence>(entityManager, ExperimentalEntityConfidence.class);
        this.identificationMethodSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.PARTICIPANT_DETECTION_METHOD_OBJCLASS);
        this.experimentalPreparationSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.PARTICIPANT_EXPERIMENTAL_PREPARATION_OBJCLASS);
        this.experimentalRoleSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.EXPERIMENTAL_ROLE_OBJCLASS);
        this.organismSynchronizer = new IntactOrganismSynchronizer(entityManager);
    }

    public IntactExperimentalEntityBaseSynchronizer(EntityManager entityManager, Class<I> intactClass,
                                                    IntactDbSynchronizer<Alias, EntityAlias> aliasSynchronizer,
                                                    IntactDbSynchronizer<Annotation, EntityAnnotation> annotationSynchronizer, IntactDbSynchronizer<Xref, EntityXref> xrefSynchronizer,
                                                    IntactDbSynchronizer<CvTerm, IntactCvTerm> biologicalRoleSynchronizer,
                                                    IntactDbSynchronizer<Feature, AbstractIntactFeature> featureSynchronizer,
                                                    IntactDbSynchronizer<CausalRelationship, IntactCausalRelationship> causalRelationshipSynchronizer,
                                                    IntactDbSynchronizer<Interactor, IntactInteractor> interactorSynchronizer,
                                                    IntactDbSynchronizer<Parameter, ExperimentalEntityParameter> parameterSynchronizer,
                                                    IntactDbSynchronizer<Confidence, ExperimentalEntityConfidence> confidenceSynchronizer,
                                                    IntactDbSynchronizer<CvTerm, IntactCvTerm> identificationMethodSynchronizer,
                                                    IntactDbSynchronizer<CvTerm, IntactCvTerm> experimentalPreparationSynchronizer,
                                                    IntactDbSynchronizer<CvTerm, IntactCvTerm> experimentalRoleSynchronizer,
                                                    IntactDbSynchronizer<Organism, IntactOrganism> organismSynchronizer){
        super(entityManager, intactClass, aliasSynchronizer, annotationSynchronizer, xrefSynchronizer, biologicalRoleSynchronizer, featureSynchronizer,
                causalRelationshipSynchronizer, interactorSynchronizer);
        this.parameterSynchronizer = parameterSynchronizer != null ? parameterSynchronizer : new IntactParameterSynchronizer<Parameter, ExperimentalEntityParameter>(entityManager, ExperimentalEntityParameter.class);
        this.confidenceSynchronizer = confidenceSynchronizer != null ? confidenceSynchronizer : new IntactConfidenceSynchronizer<Confidence, ExperimentalEntityConfidence>(entityManager, ExperimentalEntityConfidence.class);
        this.identificationMethodSynchronizer = identificationMethodSynchronizer != null ? identificationMethodSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.PARTICIPANT_DETECTION_METHOD_OBJCLASS);
        this.experimentalPreparationSynchronizer = experimentalPreparationSynchronizer != null ? experimentalPreparationSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.PARTICIPANT_EXPERIMENTAL_PREPARATION_OBJCLASS);
        this.experimentalRoleSynchronizer = experimentalRoleSynchronizer != null ? experimentalRoleSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.EXPERIMENTAL_ROLE_OBJCLASS);
        this.organismSynchronizer = organismSynchronizer != null ? organismSynchronizer : new IntactOrganismSynchronizer(entityManager);
    }

    public void synchronizeProperties(I intactEntity) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizeProperties(intactEntity);
        // check expressed in organism
        prepareOrganism(intactEntity);
        // then check experimentalRole
        prepareExperimentalRole(intactEntity);
        // then check participant identification methods
        prepareIdentificationMethods(intactEntity);
        // then check experimental preparations
        prepareExperimentalPreparations(intactEntity);
        // then check confidences
        prepareConfidences(intactEntity);
        // then check parameters
        prepareParameters(intactEntity);
    }

    @Override
    public void clearCache() {
        super.clearCache();
        this.confidenceSynchronizer.clearCache();
        this.parameterSynchronizer.clearCache();
        this.experimentalPreparationSynchronizer.clearCache();
        this.experimentalRoleSynchronizer.clearCache();
        this.identificationMethodSynchronizer.clearCache();
        this.organismSynchronizer.clearCache();
    }

    @Override
    protected I instantiateNewPersistentInstance(T object, Class<? extends I> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        I newParticipant = intactClass.getConstructor(Interactor.class).newInstance(object.getInteractor());
        ParticipantCloner.copyAndOverrideParticipantEvidenceProperties(object, newParticipant, false);
        return newParticipant;
    }

    protected void prepareOrganism(I intactEntity) throws PersisterException, FinderException, SynchronizerException {
        Organism organism = intactEntity.getExpressedInOrganism();
        if (organism != null){
            intactEntity.setExpressedInOrganism(organismSynchronizer.synchronize(organism, true));
        }
    }

    protected void prepareConfidences(I intactEntity) throws PersisterException, FinderException, SynchronizerException {
        if (intactEntity.areConfidencesInitialized()){
            List<Confidence> confidencesToPersist = new ArrayList<Confidence>(intactEntity.getConfidences());
            for (Confidence confidence : confidencesToPersist){
                // do not persist or merge confidences because of cascades
                Confidence persistentConfidence = this.confidenceSynchronizer.synchronize(confidence, false);
                // we have a different instance because needed to be synchronized
                if (persistentConfidence != confidence){
                    intactEntity.getConfidences().remove(confidence);
                    intactEntity.getConfidences().add(persistentConfidence);
                }
            }
        }
    }

    protected void prepareExperimentalPreparations(I intactEntity) throws PersisterException, FinderException, SynchronizerException {
        if (intactEntity.areExperimentalPreparationsInitialized()){
            List<CvTerm> preparationsToPersist = new ArrayList<CvTerm>(intactEntity.getExperimentalPreparations());
            for (CvTerm preparation : preparationsToPersist){
                CvTerm persistentPreparation = this.experimentalPreparationSynchronizer.synchronize(preparation, true);
                // we have a different instance because needed to be synchronized
                if (persistentPreparation != preparation){
                    intactEntity.getExperimentalPreparations().remove(preparation);
                    intactEntity.getExperimentalPreparations().add(persistentPreparation);
                }
            }
        }
    }

    protected void prepareParameters(I intactEntity) throws FinderException, PersisterException, SynchronizerException {
        if (intactEntity.areParametersInitialized()){
            List<Parameter> parametersToPersist = new ArrayList<Parameter>(intactEntity.getParameters());
            for (Parameter parameter : parametersToPersist){
                Parameter persistentParameter = this.parameterSynchronizer.synchronize(parameter, false);
                // we have a different instance because needed to be synchronized
                if (persistentParameter != parameter){
                    intactEntity.getParameters().remove(parameter);
                    intactEntity.getParameters().add(persistentParameter);
                }
            }
        }
    }

    protected void prepareIdentificationMethods(I intactEntity) throws PersisterException, FinderException, SynchronizerException {
        if (intactEntity.areIdentificationMethodsInitialized()){
            List<CvTerm> methodsToPersist = new ArrayList<CvTerm>(intactEntity.getIdentificationMethods());
            for (CvTerm term : methodsToPersist){
                CvTerm persistentTerm = this.identificationMethodSynchronizer.synchronize(term, true);
                // we have a different instance because needed to be synchronized
                if (persistentTerm != term){
                    intactEntity.getIdentificationMethods().remove(term);
                    intactEntity.getIdentificationMethods().add(persistentTerm);
                }
            }
        }
    }

    protected void prepareExperimentalRole(I intactParticipant) throws PersisterException, FinderException, SynchronizerException {
        CvTerm role = intactParticipant.getExperimentalRole();
        intactParticipant.setExperimentalRole(this.experimentalRoleSynchronizer.synchronize(role, true));
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactExperimentalEntityMergerEnrichOnly<T, I>());
    }
}
