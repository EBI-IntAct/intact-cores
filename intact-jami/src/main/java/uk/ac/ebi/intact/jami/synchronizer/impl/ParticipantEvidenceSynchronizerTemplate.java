package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.ParticipantCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.ParticipantEvidenceMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.IntactParticipantEvidence;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

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

public class ParticipantEvidenceSynchronizerTemplate<T extends ParticipantEvidence, I extends IntactParticipantEvidence> extends ParticipantSynchronizerTemplate<T, I> {

    public ParticipantEvidenceSynchronizerTemplate(SynchronizerContext context, Class<I> intactClass){
        super(context, intactClass);
    }

    public void synchronizeProperties(I intactEntity) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizeProperties(intactEntity);
        // then check aliases
        prepareAliases(intactEntity);
        // then check annotations
        prepareAnnotations(intactEntity);
        // then check xrefs
        prepareXrefs(intactEntity);
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
    protected I instantiateNewPersistentInstance(T object, Class<? extends I> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        I newParticipant = intactClass.getConstructor(Interactor.class).newInstance(object.getInteractor());
        ParticipantCloner.copyAndOverrideParticipantEvidenceProperties(object, newParticipant, false);
        return newParticipant;
    }

    protected void prepareOrganism(I intactEntity) throws PersisterException, FinderException, SynchronizerException {
        Organism organism = intactEntity.getExpressedInOrganism();
        if (organism != null){
            intactEntity.setExpressedInOrganism(getContext().getOrganismSynchronizer().synchronize(organism, true));
        }
    }

    protected void prepareConfidences(I intactEntity) throws PersisterException, FinderException, SynchronizerException {
        if (intactEntity.areConfidencesInitialized()){
            List<Confidence> confidencesToPersist = new ArrayList<Confidence>(intactEntity.getConfidences());
            for (Confidence confidence : confidencesToPersist){
                // do not persist or merge confidences because of cascades
                Confidence persistentConfidence = getContext().getEntityConfidenceSynchronizer().synchronize(confidence, false);
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
                CvTerm persistentPreparation = getContext().getExperimentalPreparationSynchronizer().synchronize(preparation, true);
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
                Parameter persistentParameter = getContext().getEntityParameterSynchronizer().synchronize(parameter, false);
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
                CvTerm persistentTerm = getContext().getParticipantDetectionMethodSynchronizer().synchronize(term, true);
                // we have a different instance because needed to be synchronized
                if (persistentTerm != term){
                    intactEntity.getIdentificationMethods().remove(term);
                    intactEntity.getIdentificationMethods().add(persistentTerm);
                }
            }
        }
    }

    protected void prepareXrefs(I intactEntity) throws FinderException, PersisterException, SynchronizerException {
        if (intactEntity.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactEntity.getXrefs());
            for (Xref xref : xrefsToPersist){
                // do not persist or merge xrefs because of cascades
                Xref persistentXref = getContext().getExperimentalEntityXrefSynchronizer().synchronize(xref, false);
                // we have a different instance because needed to be synchronized
                if (persistentXref != xref){
                    intactEntity.getXrefs().remove(xref);
                    intactEntity.getXrefs().add(persistentXref);
                }
            }
        }
    }

    protected void prepareAnnotations(I intactEntity) throws FinderException, PersisterException, SynchronizerException {
        if (intactEntity.areAnnotationsInitialized()){
            List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactEntity.getAnnotations());
            for (Annotation annotation : annotationsToPersist){
                // do not persist or merge annotations because of cascades
                Annotation persistentAnnotation = getContext().getExperimentalEntityAnnotationSynchronizer().synchronize(annotation, false);
                // we have a different instance because needed to be synchronized
                if (persistentAnnotation != annotation){
                    intactEntity.getAnnotations().remove(annotation);
                    intactEntity.getAnnotations().add(persistentAnnotation);
                }
            }
        }
    }

    protected void prepareAliases(I intactEntity) throws FinderException, PersisterException, SynchronizerException {
        if (intactEntity.areAliasesInitialized()){
            List<Alias> aliasesToPersist = new ArrayList<Alias>(intactEntity.getAliases());
            for (Alias alias : aliasesToPersist){
                // do not persist or merge alias because of cascades
                Alias persistentAlias = getContext().getExperimentalEntityAliasSynchronizer().synchronize(alias, false);
                // we have a different instance because needed to be synchronized
                if (persistentAlias != alias){
                    intactEntity.getAliases().remove(alias);
                    intactEntity.getAliases().add(persistentAlias);
                }
            }
        }
    }

    protected void prepareExperimentalRole(I intactParticipant) throws PersisterException, FinderException, SynchronizerException {
        CvTerm role = intactParticipant.getExperimentalRole();
        intactParticipant.setExperimentalRole(getContext().getExperimentalRoleSynchronizer().synchronize(role, true));
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new ParticipantEvidenceMergerEnrichOnly<T, I>());
    }

    @Override
    protected IntactDbSynchronizer getFeatureSynchronizer() {
        return getContext().getFeatureEvidenceSynchronizer();
    }
}


