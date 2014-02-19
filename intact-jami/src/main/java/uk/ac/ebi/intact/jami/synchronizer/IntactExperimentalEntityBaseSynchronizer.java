package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.ParticipantCloner;
import uk.ac.ebi.intact.jami.merger.ExperimentalEntityMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.impl.CvTermSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.impl.ConfidenceSynchronizerTemplate;
import uk.ac.ebi.intact.jami.synchronizer.impl.OrganismSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.impl.ParameterSynchronizerTemplate;
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
        getConfidenceSynchronizer().clearCache();
        getParameterSynchronizer().clearCache();
        getExperimentalPreparationSynchronizer().clearCache();
        getExperimentalRoleSynchronizer().clearCache();
        getIdentificationMethodSynchronizer().clearCache();
        getOrganismSynchronizer().clearCache();
    }

    public IntactDbSynchronizer<Parameter, ExperimentalEntityParameter> getParameterSynchronizer() {
        if (this.parameterSynchronizer == null){
            this.parameterSynchronizer = new ParameterSynchronizerTemplate<Parameter, ExperimentalEntityParameter>(getEntityManager(), ExperimentalEntityParameter.class);
        }
        return parameterSynchronizer;
    }

    public void setParameterSynchronizer(IntactDbSynchronizer<Parameter, ExperimentalEntityParameter> parameterSynchronizer) {
        this.parameterSynchronizer = parameterSynchronizer;
    }

    public IntactDbSynchronizer<Confidence, ExperimentalEntityConfidence> getConfidenceSynchronizer() {
        if (this.confidenceSynchronizer == null){
            this.confidenceSynchronizer = new ConfidenceSynchronizerTemplate<Confidence, ExperimentalEntityConfidence>(getEntityManager(), ExperimentalEntityConfidence.class);
        }
        return confidenceSynchronizer;
    }

    public void setConfidenceSynchronizer(IntactDbSynchronizer<Confidence, ExperimentalEntityConfidence> confidenceSynchronizer) {
        this.confidenceSynchronizer = confidenceSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getIdentificationMethodSynchronizer() {
        if (this.identificationMethodSynchronizer == null){
            this.identificationMethodSynchronizer = new CvTermSynchronizer(getEntityManager(), IntactUtils.PARTICIPANT_DETECTION_METHOD_OBJCLASS);
        }
        return identificationMethodSynchronizer;
    }

    public void setIdentificationMethodSynchronizer(IntactDbSynchronizer<CvTerm, IntactCvTerm> identificationMethodSynchronizer) {
        this.identificationMethodSynchronizer = identificationMethodSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getExperimentalPreparationSynchronizer() {
        if (this.experimentalPreparationSynchronizer == null){
            this.experimentalPreparationSynchronizer = new CvTermSynchronizer(getEntityManager(), IntactUtils.PARTICIPANT_EXPERIMENTAL_PREPARATION_OBJCLASS);
        }
        return experimentalPreparationSynchronizer;
    }

    public void setExperimentalPreparationSynchronizer(IntactDbSynchronizer<CvTerm, IntactCvTerm> experimentalPreparationSynchronizer) {
        this.experimentalPreparationSynchronizer = experimentalPreparationSynchronizer;
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getExperimentalRoleSynchronizer() {
        if (this.experimentalRoleSynchronizer == null){
            this.experimentalRoleSynchronizer = new CvTermSynchronizer(getEntityManager(), IntactUtils.EXPERIMENTAL_ROLE_OBJCLASS);
        }
        return experimentalRoleSynchronizer;
    }

    public void setExperimentalRoleSynchronizer(IntactDbSynchronizer<CvTerm, IntactCvTerm> experimentalRoleSynchronizer) {
        this.experimentalRoleSynchronizer = experimentalRoleSynchronizer;
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
    protected I instantiateNewPersistentInstance(T object, Class<? extends I> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        I newParticipant = intactClass.getConstructor(Interactor.class).newInstance(object.getInteractor());
        ParticipantCloner.copyAndOverrideParticipantEvidenceProperties(object, newParticipant, false);
        return newParticipant;
    }

    protected void prepareOrganism(I intactEntity) throws PersisterException, FinderException, SynchronizerException {
        Organism organism = intactEntity.getExpressedInOrganism();
        if (organism != null){
            intactEntity.setExpressedInOrganism(getOrganismSynchronizer().synchronize(organism, true));
        }
    }

    protected void prepareConfidences(I intactEntity) throws PersisterException, FinderException, SynchronizerException {
        if (intactEntity.areConfidencesInitialized()){
            List<Confidence> confidencesToPersist = new ArrayList<Confidence>(intactEntity.getConfidences());
            for (Confidence confidence : confidencesToPersist){
                // do not persist or merge confidences because of cascades
                Confidence persistentConfidence = getConfidenceSynchronizer().synchronize(confidence, false);
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
                CvTerm persistentPreparation = getExperimentalPreparationSynchronizer().synchronize(preparation, true);
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
                Parameter persistentParameter = getParameterSynchronizer().synchronize(parameter, false);
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
                CvTerm persistentTerm = getIdentificationMethodSynchronizer().synchronize(term, true);
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
        intactParticipant.setExperimentalRole(getExperimentalRoleSynchronizer().synchronize(role, true));
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new ExperimentalEntityMergerEnrichOnly<T, I>());
    }
}
