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
        Iterator<Annotation> annotIterator = annotationsToBeAdded.iterator();
        while (annotIterator.hasNext()){
            Annotation annotation = annotIterator.next();
            // do not persist or merge annotations because of cascades
            Annotation expAnnotation = (Annotation)annotationSynchronizer.synchronize(annotation, false);
            // we have a different instance because needed to be synchronized
            if (expAnnotation != annotation){
                if (expAnnotation != null && !synchronizedAnnots.contains(expAnnotation)){
                    synchronizedAnnots.add(expAnnotation);
                }
            }
            else{
                annotIterator.remove();
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
        Iterator<Role> roleIterator = rolesToBeAdded.iterator();
        while (roleIterator.hasNext()){
            Role role = roleIterator.next();
            // do not persist or merge annotations because of cascades
            Role expAnnotation = (Role)roleSynchronizer.synchronize(role, true);
            // we have a different instance because needed to be synchronized
            if (expAnnotation != role){
                if (expAnnotation != null && !synchronizedRoles.contains(expAnnotation)){
                    synchronizedRoles.add(expAnnotation);
                }
            }
            else{
                roleIterator.remove();
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
        Iterator<Alias> aliasIterator = aliasesToBeAdded.iterator();
        while (aliasIterator.hasNext()){
            Alias annotation = aliasIterator.next();
            // do not persist or merge aliases because of cascades
            Alias expAlias = (Alias)aliasSynchronizer.synchronize(annotation, false);
            // we have a different instance because needed to be synchronized
            if (expAlias != annotation){
                if (expAlias != null && !synchronizedAliases.contains(expAlias)){
                    synchronizedAliases.add(expAlias);
                }
            }
            else{
                aliasIterator.remove();
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
        Iterator<VariableParameter> paramsToBeAddedIterator = paramsToBweAdded.iterator();
        while (paramsToBeAddedIterator.hasNext()){
            VariableParameter annotation = paramsToBeAddedIterator.next();
            // do not persist or merge variable parameters because of cascades
            VariableParameter cpExp = (VariableParameter)vPSynchronizer.synchronize(annotation, false);
            // we have a different instance because needed to be synchronized
            if (annotation != cpExp){
                if (cpExp != null && !synchronizedParameters.contains(cpExp)){
                    synchronizedParameters.add(cpExp);
                }
            }
            else{
                paramsToBeAddedIterator.remove();
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
        Iterator<VariableParameterValueSet> paramsToBeAddedIterator = paramsToBweAdded.iterator();
        while (paramsToBeAddedIterator.hasNext()){
            VariableParameterValueSet annotation = paramsToBeAddedIterator.next();
            // do not persist or merge variable parameters because of cascades
            VariableParameterValueSet cpExp = (VariableParameterValueSet)vPSynchronizer.synchronize(annotation, false);
            // we have a different instance because needed to be synchronized
            if (cpExp != annotation){
                if (cpExp != null && !synchronizedParameters.contains(cpExp)){
                    synchronizedParameters.add(cpExp);
                }
            }
            else{
               paramsToBeAddedIterator.remove();
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
        Iterator<C> cvsToBeAddedIterator = cvsToBeAdded.iterator();
        while (cvsToBeAddedIterator.hasNext()){
            C cv = cvsToBeAddedIterator.next();
            // do not persist or merge cv parents because of cascades
            C expCv = (C)cvSynchronizer.synchronize(cv, true);
            // we have a different instance because needed to be synchronized
            if (expCv != cv){
                if (expCv != null && !synchronizedCvs.contains(expCv)){
                    synchronizedCvs.add(expCv);
                }
            }
            else{
                cvsToBeAddedIterator.remove();
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
        Iterator<Xref> xrefsToBeAddedIterator = xrefsToBeAdded.iterator();
        Set<Xref> goReferences = new TreeSet<Xref>(new IntactComplexGoXrefComparator());
        while (xrefsToBeAddedIterator.hasNext()){
            Xref ref = xrefsToBeAddedIterator.next();
            // do not persist or merge xrefs because of cascades
            Xref expRef = (Xref)xrefSynchronizer.synchronize(ref, false);
            if (expRef != ref){
                if (expRef != null){
                    if (goReferences.add(expRef)){
                        synchronizedXrefs.add(expRef);
                    }
                }
            }
            else{
               xrefsToBeAddedIterator.remove();
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
        Iterator<Interactor> interactorsToBeAddedIterator = interactorsToBeAdded.iterator();
        while (interactorsToBeAddedIterator.hasNext()){
            Interactor ref = interactorsToBeAddedIterator.next();
            // do not persist or merge interactors because of cascades
            Interactor expRef = (Interactor)interactorSynchronizer.synchronize(ref, true);
            // we have a different instance because needed to be synchronized
            if (expRef != ref){
                if (expRef != null && !synchronizedInteractors.contains(expRef)){
                    synchronizedInteractors.add(expRef);
                }
            }
            else{
                interactorsToBeAddedIterator.remove();
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
        Iterator<C> confidencesToBeAddedIterator = confidencesToBeAdded.iterator();
        while (confidencesToBeAddedIterator.hasNext()){
            C conf = confidencesToBeAddedIterator.next();
            // do not persist or merge confidences because of cascades
            C expConf = (C)confidenceSynchronizer.synchronize(conf, false);
            // we have a different instance because needed to be synchronized
            if (expConf != conf){
                if (expConf != null && !synchronizedConfidences.contains(expConf)){
                    synchronizedConfidences.add(expConf);
                }
            }
            else{
                confidencesToBeAddedIterator.remove();
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
        Iterator<C> parametersToBeAddedIterator = parametersToBeAdded.iterator();
        while (parametersToBeAddedIterator.hasNext()){
            C para = parametersToBeAddedIterator.next();
            // do not persist or merge parameters because of cascades
            C expConf = (C)parameterSynchronizer.synchronize(para, false);
            // we have a different instance because needed to be synchronized
            if (expConf != para){
                if (expConf != null && !synchronizedParameters.contains(expConf)){
                    synchronizedParameters.add(expConf);
                }
            }
            else{
                parametersToBeAddedIterator.remove();
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
        Iterator<C> participantsToBeAddedIterator = participantsToBeAdded.iterator();
        while (participantsToBeAddedIterator.hasNext()){
            C para = participantsToBeAddedIterator.next();
            // do not persist or merge participants because of cascades
            C expParticipant = (C)participantSynchronizer.synchronize(para, false);
            // we have a different instance because needed to be synchronized
            if (expParticipant != para){
                if (expParticipant != null && !synchronizedParticipants.contains(expParticipant)){
                    synchronizedParticipants.add(expParticipant);
                }
            }
            else{
                participantsToBeAddedIterator.remove();
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
        Iterator<InteractionEvidence> addedInteractionsIterator = addedInteractions.iterator();
        while (addedInteractionsIterator.hasNext()){
            InteractionEvidence para = addedInteractionsIterator.next();
            // do not persist or merge interactions because of cascades
            InteractionEvidence expInteraction = (InteractionEvidence)interactionSynchronizer.synchronize(para, false);
            // we have a different instance because needed to be synchronized
            if (expInteraction != para){
                if (expInteraction != null && !synchronizedInteractions.contains(expInteraction)){
                    synchronizedInteractions.add(expInteraction);

                }
            }
            else{
                addedInteractionsIterator.remove();
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
        Iterator<Experiment> experimentsToBeAddedIterator = experimentsToBeAdded.iterator();
        while (experimentsToBeAddedIterator.hasNext()){
            Experiment para = experimentsToBeAddedIterator.next();
            // do not persist or merge experiments because of cascades
            Experiment expInteraction = (Experiment)experimentSynchronizer.synchronize(para, false);
            // we have a different instance because needed to be synchronized
            if (expInteraction != para){
                if (expInteraction != null && !synchronizedInteractions.contains(expInteraction)){
                    synchronizedInteractions.add(expInteraction);
                }
            }
            else{
               experimentsToBeAddedIterator.remove();
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
        Iterator<Range> rangesToBeAddedIterator = rangesToBeAdded.iterator();
        while (rangesToBeAddedIterator.hasNext()){
            Range para = rangesToBeAddedIterator.next();
            // do not persist or merge ranges because of cascades
            Range expInteraction = (Range)rangeSynchronizer.synchronize(para, false);
            // we have a different instance because needed to be synchronized
            if (expInteraction != para){
                if (expInteraction != null && !synchronizedRanges.contains(expInteraction)){
                    synchronizedRanges.add(expInteraction);
                }
            }
            else{
                rangesToBeAddedIterator.remove();
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
        Iterator<F> featuresToBeAddedIterator = featuresToBeAdded.iterator();
        while (featuresToBeAddedIterator.hasNext()){
            F para = featuresToBeAddedIterator.next();
            // do not persist or merge ranges because of cascades
            F expInteraction = (F)featureSynchronizer.synchronize(para, false);
            // we have a different instance because needed to be synchronized
            if (expInteraction != para){
                if (expInteraction != null && !synchronizedfeatures.contains(expInteraction)){
                    synchronizedfeatures.add(expInteraction);
                }
            }
            else{
                featuresToBeAddedIterator.remove();
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
        Iterator<F> crToBeAddedIterator = crToBeAdded.iterator();
        while (crToBeAddedIterator.hasNext()){
            F para = crToBeAddedIterator.next();
            // do not persist or merge causal relationships because of cascades
            F expInteraction = (F)crSynchronizer.synchronize(para, false);
            // we have a different instance because needed to be synchronized
            if (expInteraction != para){
                if (expInteraction != null && !synchronizedCr.contains(para)){
                    synchronizedCr.add(expInteraction);
                }
            }
            else{
                crToBeAddedIterator.remove();
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
        Iterator<C> lsToBeAddedIterator = lcToBeAdded.iterator();
        while (lsToBeAddedIterator.hasNext()){
            C para = lsToBeAddedIterator.next();
            // do not persist or merge lifecycle because of cascades
            C expLc = (C)lcSynchronizer.synchronize(para, false);
            // we have a different instance because needed to be synchronized
            if (expLc != para){
                if (expLc != null && !synchronizedLc.contains(expLc)){
                    synchronizedLc.add(expLc);
                }
            }
            // we don't need more processing of this element so we can remove it from the list of added
            else {
                lsToBeAddedIterator.remove();
            }
        }

        return synchronizedLc;
    }
}
