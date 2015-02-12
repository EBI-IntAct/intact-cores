package uk.ac.ebi.intact.jami.utils;

import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.user.Role;
import uk.ac.ebi.intact.jami.synchronizer.*;
import uk.ac.ebi.intact.jami.utils.comparator.IntactComplexGoXrefComparator;

import java.util.*;

/**
 * Enricher utils for intact
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>02/02/15</pre>
 */

public class IntactEnricherUtils {

    /**
     * Method which will synchronize annotations that are not present in the annotations to enrich, only in the enriched annotations.
     * It will refresh the enriched annotations to have fully initialised annotations before enrichment
     * @param annotationsToBeAdded : the collection of annotations to be enriched
     * @param annotationSynchronizer : the annotation sycnrhonizer to be used
     * @return the synchronized annotations which will be added to the annotations to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static List<Annotation> synchronizeAnnotationsToEnrich(Collection<Annotation> annotationsToBeAdded,
                                                      AnnotationSynchronizer annotationSynchronizer) throws PersisterException, FinderException, SynchronizerException {

        List<Annotation> synchronizedAnnots = new ArrayList<Annotation>(annotationsToBeAdded.size());
        for (Annotation annotation : annotationsToBeAdded){
            // do not persist or merge annotations because of cascades
            Annotation expAnnotation = (Annotation)annotationSynchronizer.synchronize(annotation, false);
            // we have a different instance because needed to be synchronized
            if (!synchronizedAnnots.contains(expAnnotation)){
                synchronizedAnnots.add(expAnnotation);
            }
        }

        return synchronizedAnnots;
    }

    /**
     * Method which will synchronize roles that are not present in the roles to enrich, only in the enriched roles.
     * It will refresh the enriched roles to have fully initialised roles before enrichment
     * @param rolesToBeAdded : the collection of roles to be enriched
     * @param roleSynchronizer : the role sycnrhonizer to be used
     * @return the synchronized roles which will be added to the roles to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static List<Role> synchronizeUserRolesToEnrich(Collection<Role> rolesToBeAdded,
                                                                  IntactDbSynchronizer roleSynchronizer) throws PersisterException, FinderException, SynchronizerException {

        List<Role> synchronizedRoles = new ArrayList<Role>(rolesToBeAdded.size());
        for (Role role : rolesToBeAdded){
            // do not persist or merge annotations because of cascades
            Role expAnnotation = (Role)roleSynchronizer.synchronize(role, true);
            // we have a different instance because needed to be synchronized
            if (!synchronizedRoles.contains(expAnnotation)){
                synchronizedRoles.add(expAnnotation);
            }
        }

        return synchronizedRoles;
    }

    /**
     * Method which will synchronize aliases that are not present in the aliases to enrich, only in the enriched aliases.
     * It will refresh the enriched aliases to have fully initialised aliases before enrichment
     * @param aliasesToBeAdded : the collection of aliases to be enriched
     * @param aliasSynchronizer : the aliases synchronizer to be used
     * @return the synchronized aliases which will be added to the aliases to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static List<Alias> synchronizeAliasesToEnrich(Collection<Alias> aliasesToBeAdded,
                                                  AliasSynchronizer aliasSynchronizer) throws PersisterException, FinderException, SynchronizerException {

        List<Alias> synchronizedAliases = new ArrayList<Alias>(aliasesToBeAdded.size());
        for (Alias annotation : aliasesToBeAdded){
            // do not persist or merge aliases because of cascades
            Alias expAlias = (Alias)aliasSynchronizer.synchronize(annotation, false);
            // we have a different instance because needed to be synchronized
            if (!synchronizedAliases.contains(expAlias)){
                synchronizedAliases.add(expAlias);
            }
        }

        return synchronizedAliases;
    }

    /**
     * Method which will synchronize variable parameters that are not present in the variable parameters to enrich, only in the enriched variable parameters.
     * It will refresh the enriched variable parameters to have fully initialised variable parameters before enrichment
     * @param paramsToBweAdded : the collection of variable parameters to be enriched
     * @param vPSynchronizer : the variable parameters synchronizer to be used
     * @return the synchronized variable parameters which will be added to the variable parameters to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static List<VariableParameter> synchronizeVariableParametersToEnrich(Collection<VariableParameter> paramsToBweAdded,
                                                             IntactDbSynchronizer vPSynchronizer) throws PersisterException, FinderException, SynchronizerException {

        List<VariableParameter> synchronizedParameters = new ArrayList<VariableParameter>(paramsToBweAdded.size());
        for (VariableParameter annotation : paramsToBweAdded){
            // do not persist or merge variable parameters because of cascades
            VariableParameter cpExp = (VariableParameter)vPSynchronizer.synchronize(annotation, false);
            // we have a different instance because needed to be synchronized
            if (!synchronizedParameters.contains(cpExp)){
                synchronizedParameters.add(cpExp);
            }
        }

        return synchronizedParameters;
    }

    /**
     * Method which will synchronize variable parameters that are not present in the variable parameters to enrich, only in the enriched variable parameters.
     * It will refresh the enriched variable parameters to have fully initialised variable parameters before enrichment
     * @param paramsToBweAdded : the collection of variable parameters to be enriched
     * @param vPSynchronizer : the variable parameters synchronizer to be used
     * @return the synchronized variable parameters which will be added to the variable parameters to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static List<VariableParameterValueSet> synchronizeVariableParameterValuesToEnrich(Collection<VariableParameterValueSet> paramsToBweAdded,
                                                                                IntactDbSynchronizer vPSynchronizer) throws PersisterException, FinderException, SynchronizerException {

        List<VariableParameterValueSet> synchronizedParameters = new ArrayList<VariableParameterValueSet>(paramsToBweAdded.size());
        for (VariableParameterValueSet annotation : paramsToBweAdded){
            // do not persist or merge variable parameters because of cascades
            VariableParameterValueSet cpExp = (VariableParameterValueSet)vPSynchronizer.synchronize(annotation, false);
            // we have a different instance because needed to be synchronized
            if (!synchronizedParameters.contains(cpExp)){
                synchronizedParameters.add(cpExp);
            }
        }

        return synchronizedParameters;
    }

    /**
     * Method which will synchronize cv parents that are not present in the aliases to enrich, only in the enriched cv parents.
     * It will refresh the enriched cv parents to have fully initialised cv parents before enrichment
     * @param cvsToBeAdded : the collection of cv parents to be enriched
     * @param cvSynchronizer : the cv parents synchronizer to be used
     * @return the synchronized cv parents which will be added to the cv parents to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static <C extends CvTerm> List<C> synchronizeCvsToEnrich(Collection<C> cvsToBeAdded,
                                                                 IntactDbSynchronizer cvSynchronizer) throws PersisterException, FinderException, SynchronizerException {

        List<C> synchronizedCvs = new ArrayList<C>(cvsToBeAdded.size());
        for (C cv : cvsToBeAdded){
            // do not persist or merge cv parents because of cascades
            C expCv = (C)cvSynchronizer.synchronize(cv, true);
            // we have a different instance because needed to be synchronized
            if (!synchronizedCvs.contains(expCv)){
                synchronizedCvs.add(expCv);
            }
        }

        return synchronizedCvs;
    }

    /**
     * Method which will synchronize xrefs that are not present in the xrefs to enrich
     * @param xrefsToBeAdded : the collection of xrefs to be enriched
     * @param xrefSynchronizer : the xref sycnrhonizer to be used
     * @return the synchronized xrefs which will be added to the xrefs to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static List<Xref> synchronizeXrefsToEnrich(Collection<Xref> xrefsToBeAdded,
                                                XrefSynchronizer xrefSynchronizer) throws PersisterException, FinderException, SynchronizerException {

        List<Xref> synchronizedXrefs = new ArrayList<Xref>(xrefsToBeAdded.size());
        for (Xref ref : xrefsToBeAdded){
            // do not persist or merge xrefs because of cascades
            Xref expRef = (Xref)xrefSynchronizer.synchronize(ref, false);
            Set<Xref> goReferences = new TreeSet<Xref>(new IntactComplexGoXrefComparator());
            for (Xref obj : synchronizedXrefs){
                if (goReferences.add(obj)){
                    synchronizedXrefs.add(expRef);
                }
            }
        }

        return synchronizedXrefs;
    }

    /**
     * Method which will synchronize interactors that are not present in the interactors to enrich
     * @param interactorsToBeAdded : the collection of interactors to be enriched
     * @param interactorSynchronizer : the interactors sycnrhonizer to be used
     * @return the synchronized interactors which will be added to the xrefs to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static List<Interactor> synchronizeInteractorsToEnrich(Collection<Interactor> interactorsToBeAdded,
                                                      IntactDbSynchronizer interactorSynchronizer) throws PersisterException, FinderException, SynchronizerException {

        List<Interactor> synchronizedInteractors = new ArrayList<Interactor>(interactorsToBeAdded.size());
        for (Interactor ref : interactorsToBeAdded){
            // do not persist or merge interactors because of cascades
            Interactor expRef = (Interactor)interactorSynchronizer.synchronize(ref, true);
            // we have a different instance because needed to be synchronized
            if (!synchronizedInteractors.contains(expRef)){
                synchronizedInteractors.add(expRef);
            }
        }

        return synchronizedInteractors;
    }

    /**
     * Method which will synchronize confidences that are not present in the confidences to enrich
     * @param confidencesToBeAdded : the collection of confidences to be enriched
     * @param confidenceSynchronizer : the confidences synchronizer to be used
     * @return the synchronized confidences which will be added to the confidences to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static <C extends Confidence> List<C> synchronizeConfidencesToEnrich(Collection<C> confidencesToBeAdded,
                                                            ConfidenceSynchronizer confidenceSynchronizer) throws PersisterException, FinderException, SynchronizerException {

        List<C> synchronizedConfidences = new ArrayList<C>(confidencesToBeAdded.size());
        for (C conf : confidencesToBeAdded){
            // do not persist or merge confidences because of cascades
            C expConf = (C)confidenceSynchronizer.synchronize(conf, false);
            // we have a different instance because needed to be synchronized
            if (!synchronizedConfidences.contains(expConf)){
                synchronizedConfidences.add(expConf);
            }
        }

        return synchronizedConfidences;
    }

    /**
     * Method which will synchronize parameters that are not present in the parameters to enrich
     * @param parametersToBeAdded : the collection of parameters to be enriched
     * @param parameterSynchronizer : the parameters synchronizer to be used
     * @return the synchronized parameters which will be added to the parameters to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static <C extends Parameter> List<C> synchronizeParametersToEnrich(Collection<C> parametersToBeAdded,
                                                                           ParameterSynchronizer parameterSynchronizer) throws PersisterException, FinderException, SynchronizerException {

        List<C> synchronizedParameters = new ArrayList<C>(parametersToBeAdded.size());
        for (C para : parametersToBeAdded){
            // do not persist or merge parameters because of cascades
            C expConf = (C)parameterSynchronizer.synchronize(para, false);
            // we have a different instance because needed to be synchronized
            if (!synchronizedParameters.contains(expConf)){
                synchronizedParameters.add(expConf);
            }
        }

        return synchronizedParameters;
    }

    /**
     * Method which will synchronize participants that are not present in the participants to enrich
     * @param participantsToBeAdded : the collection of participants to be enriched
     * @param participantSynchronizer : the participants synchronizer to be used
     * @return the synchronized participants which will be added to the participants to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static <C extends Participant> List<C> synchronizeParticipantsToEnrich(Collection<C> participantsToBeAdded,
                                                                               ParticipantSynchronizer participantSynchronizer) throws PersisterException, FinderException, SynchronizerException {

        List<C> synchronizedParticipants = new ArrayList<C>(participantsToBeAdded.size());
        for (C para : participantsToBeAdded){
            // do not persist or merge participants because of cascades
            C expParticipant = (C)participantSynchronizer.synchronize(para, false);
            // we have a different instance because needed to be synchronized
            if (!synchronizedParticipants.contains(expParticipant)){
                synchronizedParticipants.add(expParticipant);
            }
        }

        return synchronizedParticipants;
    }

    /**
     * Method which will synchronize interactions that are not present in the interactions to enrich
     * @param addedInteractions : the collection of interactions to be synchronized
     * @param interactionSynchronizer : the interactions synchronizer to be used
     * @return the synchronized interactions which will be added to the interactions to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static List<InteractionEvidence> synchronizeInteractionsToEnrich(Collection<InteractionEvidence> addedInteractions,
                                                                                        IntactDbSynchronizer interactionSynchronizer) throws PersisterException, FinderException, SynchronizerException {

        List<InteractionEvidence> synchronizedInteractions = new ArrayList<InteractionEvidence>(addedInteractions.size());
        for (InteractionEvidence para : addedInteractions){
            // do not persist or merge interactions because of cascades
            InteractionEvidence expInteraction = (InteractionEvidence)interactionSynchronizer.synchronize(para, false);
            // we have a different instance because needed to be synchronized
            if (!synchronizedInteractions.contains(expInteraction)){
                synchronizedInteractions.add(expInteraction);

            }
        }

        return synchronizedInteractions;
    }

    /**
     * Method which will synchronize experiments that are not present in the experiments to enrich
     * @param experimentsToBeAdded : the collection of experiments to be enriched
     * @param experimentSynchronizer : the experiments synchronizer to be used
     * @return the synchronized experiments which will be added to the experiments to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static List<Experiment> synchronizeExperimentsToEnrich(Collection<Experiment> experimentsToBeAdded,
                                                      IntactDbSynchronizer experimentSynchronizer) throws PersisterException, FinderException, SynchronizerException {

        List<Experiment> synchronizedInteractions = new ArrayList<Experiment>(experimentsToBeAdded.size());
        for (Experiment para : experimentsToBeAdded){
            // do not persist or merge experiments because of cascades
            Experiment expInteraction = (Experiment)experimentSynchronizer.synchronize(para, false);
            // we have a different instance because needed to be synchronized
            if (!synchronizedInteractions.contains(expInteraction)){
                synchronizedInteractions.add(expInteraction);
            }
        }

        return synchronizedInteractions;
    }

    /**
     * Method which will synchronize ranges that are not present in the ranges to enrich
     * @param rangesToBeAdded : the collection of ranges to be enriched
     * @param rangeSynchronizer : the ranges synchronizer to be used
     * @return the synchronized ranges which will be added to the ranges to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static List<Range> synchronizeRangesToEnrich(Collection<Range> rangesToBeAdded,
                                                 IntactDbSynchronizer rangeSynchronizer) throws PersisterException, FinderException, SynchronizerException {

        List<Range> synchronizedRanges = new ArrayList<Range>(rangesToBeAdded.size());
        for (Range para : rangesToBeAdded){
            // do not persist or merge ranges because of cascades
            Range expInteraction = (Range)rangeSynchronizer.synchronize(para, false);
            // we have a different instance because needed to be synchronized
            if (!synchronizedRanges.contains(expInteraction)){
                synchronizedRanges.add(expInteraction);
            }
        }

        return synchronizedRanges;
    }

    /**
     * Method which will synchronize features that are not present in the features to enrich
     * @param featuresToBeAdded : the collection of features to be enriched
     * @param featureSynchronizer : the feature synchronizer to be used
     * @return the synchronized features which will be added to the features to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static <F extends Feature> List<F> synchronizeFeaturesToEnrich(Collection<F> featuresToBeAdded,
                                                                       IntactDbSynchronizer featureSynchronizer) throws PersisterException, FinderException, SynchronizerException {

        List<F> synchronizedfeatures = new ArrayList<F>(featuresToBeAdded.size());
        for (F para : featuresToBeAdded){
            // do not persist or merge ranges because of cascades
            F expInteraction = (F)featureSynchronizer.synchronize(para, false);
            // we have a different instance because needed to be synchronized
            if (!synchronizedfeatures.contains(expInteraction)){
                synchronizedfeatures.add(expInteraction);
            }
        }

        return synchronizedfeatures;
    }

    /**
     * Method which will synchronize causal relationships that are not present in the causal relationships to enrich
     * @param crToBeAdded : the collection of causal relationships to be enriched
     * @param crSynchronizer : the causal relationships synchronizer to be used
     * @return the synchronized causal relationships which will be added to the causal relationships to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static <F extends CausalRelationship> List<F> synchronizeCausalRelationshipsToEnrich(Collection<F> crToBeAdded,
                                                                                             IntactDbSynchronizer crSynchronizer) throws PersisterException, FinderException, SynchronizerException {

        List<F> synchronizedCr = new ArrayList<F>(crToBeAdded.size());
        for (F para : crToBeAdded){
            // do not persist or merge causal relationships because of cascades
            F expInteraction = (F)crSynchronizer.synchronize(para, false);
            // we have a different instance because needed to be synchronized
            if (!synchronizedCr.contains(para)){
                synchronizedCr.add(expInteraction);
            }
        }

        return synchronizedCr;
    }

    /**
     * Method which will synchronize lifecycle that are not present in the lifecycle to enrich
     * @param lcToBeAdded : the collection of lifecycle to be enriched
     * @param lcSynchronizer : the lifecycle synchronizer to be used
     * @return the synchronized lifecycle which will be added to the lifecycle to enrich
     * @throws PersisterException
     * @throws FinderException
     * @throws SynchronizerException
     */
    public static <C extends LifeCycleEvent> List<C> synchronizeLifeCycleEventsToEnrich(Collection<C> lcToBeAdded,
                                                                                     IntactDbSynchronizer lcSynchronizer) throws PersisterException, FinderException, SynchronizerException {

        List<C> synchronizedLc = new ArrayList<C>(lcToBeAdded.size());
        for (C para : lcToBeAdded){
            // do not persist or merge lifecycle because of cascades
            C expLc = (C)lcSynchronizer.synchronize(para, false);
            // we have a different instance because needed to be synchronized
            if (!synchronizedLc.contains(expLc)){
                synchronizedLc.add(expLc);
            }
        }

        return synchronizedLc;
    }
}
