package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.CooperativeEffectCloner;
import psidev.psi.mi.jami.utils.clone.InteractorCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.*;
import uk.ac.ebi.intact.jami.synchronizer.CooperativeEffectSynchronizer;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Default finder/synchronizer for basic cooperative effect
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class CooperativeEffectSynchronizerTemplate<T extends CooperativeEffect, C extends AbstractIntactCooperativeEffect> extends AbstractIntactDbSynchronizer<T, C>
implements CooperativeEffectSynchronizer<T, C> {

    private static final Log log = LogFactory.getLog(CooperativeEffectSynchronizerTemplate.class);

    public CooperativeEffectSynchronizerTemplate(SynchronizerContext context, Class<? extends C> intactClass) {
        super(context, intactClass);
    }

    public C find(T object) throws FinderException {
        return null;
    }

    @Override
    public Collection<C> findAll(T object) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<String> findAllMatchingAcs(T object) {
        return Collections.EMPTY_LIST;
    }

    public void synchronizeProperties(C object) throws FinderException, PersisterException, SynchronizerException {
        // outcome first
        prepareOutcome(object, true);
        // response
        prepareResponse(object, true);
        // cooperativity evidences
        prepareCooperativityEvidences(object, true);
        // affected interactions
        prepareAffectedInteractions(object, true);
        // annotations
        prepareAnnotations(object, true);
    }

    public void clearCache() {
        // nothing to do
    }

    protected void prepareAnnotations(C object, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        if (object.areAnnotationsInitialized()){
            Collection<Annotation> annotsToPersist = new ArrayList<Annotation>(object.getAnnotations());
            for (Annotation annot : annotsToPersist){
                Annotation persistentAnnot = enableSynchronization ?
                        getContext().getCooperativeEffectAnnotationSynchronizer().synchronize(annot, false) :
                        getContext().getCooperativeEffectAnnotationSynchronizer().convertToPersistentObject(annot);
                // we have a different instance because needed to be synchronized
                if (persistentAnnot != annot){
                    object.getAnnotations().remove(annot);
                    object.getAnnotations().add(persistentAnnot);
                }
            }
        }
    }

    protected void prepareAffectedInteractions(C object, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        if (object.areAffectedInteractionsInitialized()){
            Collection<ModelledInteraction> interactionsToPersist = new ArrayList<ModelledInteraction>(object.getAffectedInteractions());
            for (ModelledInteraction interaction : interactionsToPersist){
                // first convert to complex as we import modelled interactions as complexes in intact
                Complex convertedComplex = null;
                if (!(interaction instanceof Complex)){
                    convertedComplex = new IntactComplex(interaction.getShortName() != null ? interaction.getShortName() : IntactUtils.generateAutomaticShortlabelForModelledInteraction(interaction, IntactUtils.MAX_SHORT_LABEL_LEN));
                    InteractorCloner.copyAndOverrideBasicComplexPropertiesWithModelledInteractionProperties(interaction, convertedComplex);
                }
                else{
                    convertedComplex = (Complex)interaction;
                }

                Complex persistentInteraction = enableSynchronization ?
                        getContext().getComplexSynchronizer().synchronize(convertedComplex, true) :
                        getContext().getComplexSynchronizer().convertToPersistentObject(convertedComplex);
                // we have a different instance because needed to be synchronized
                if (persistentInteraction != interaction){
                    object.getAffectedInteractions().remove(interaction);
                    object.getAffectedInteractions().add(persistentInteraction);
                }
            }
        }
    }

    protected void prepareCooperativityEvidences(C object, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        if (object.areCooperativityEvidencesInitialized()){
            Collection<CooperativityEvidence> parametersToPersist = new ArrayList<CooperativityEvidence>(object.getCooperativityEvidences());
            for (CooperativityEvidence param : parametersToPersist){
                CooperativityEvidence expParam = enableSynchronization ?
                        getContext().getCooperativityEvidenceSynchronizer().synchronize(param, false) :
                        getContext().getCooperativityEvidenceSynchronizer().convertToPersistentObject(param);
                // we have a different instance because needed to be synchronized
                if (expParam != param){
                    object.getCooperativityEvidences().remove(param);
                    object.getCooperativityEvidences().add(expParam);
                }
            }
        }
    }

    protected void prepareResponse(C object, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
       CvTerm outcome = object.getOutCome();
       object.setOutCome(enableSynchronization ?
               getContext().getTopicSynchronizer().synchronize(outcome, true) :
               getContext().getTopicSynchronizer().convertToPersistentObject(outcome));
    }

    protected void prepareOutcome(C object, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {

        CvTerm response = object.getResponse();
        if (response != null){
            object.setResponse(enableSynchronization ?
                    getContext().getTopicSynchronizer().synchronize(response, true) :
                    getContext().getTopicSynchronizer().convertToPersistentObject(response));
        }
    }

    @Override
    protected Object extractIdentifier(C object) {
        return object.getId();
    }

    @Override
    protected C instantiateNewPersistentInstance(T object, Class<? extends C> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        C newEffect = intactClass.getConstructor(CvTerm.class).newInstance(object.getOutCome());
        CooperativeEffectCloner.copyAndOverrideBasicCooperativeEffectProperties(object, newEffect);
        return newEffect;
    }

    @Override
    protected void storeInCache(T originalObject, C persistentObject, C existingInstance) {
        // nothing to do
    }

    @Override
    protected C fetchObjectFromCache(T object) {
        return null;
    }

    @Override
    protected boolean isObjectStoredInCache(T object) {
        return false;
    }

    @Override
    protected boolean containsObjectInstance(T object) {
        return false;
    }

    @Override
    protected void removeObjectInstanceFromIdentityCache(T object) {
        // nothing to do
    }

    @Override
    protected C fetchMatchingObjectFromIdentityCache(T object) {
        return null;
    }

    @Override
    protected void convertPersistableProperties(C object) throws SynchronizerException, PersisterException, FinderException {
        // outcome first
        prepareOutcome(object, false);
        // response
        prepareResponse(object, false);
        // cooperativity evidences
        prepareCooperativityEvidences(object, false);
        // affected interactions
        prepareAffectedInteractions(object, false);
        // annotations
        prepareAnnotations(object, false);
    }

    @Override
    protected void storeObjectInIdentityCache(T originalObject, C persistableObject) {
        // nothing to do here
    }

    @Override
    protected boolean isObjectDirty(T originalObject) {
        return false;
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<T, C>(this));
    }
}
