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

    public void synchronizeProperties(C object) throws FinderException, PersisterException, SynchronizerException {
        // outcome first
        prepareOutcome(object);
        // response
        prepareResponse(object);
        // cooperativity evidences
        prepareCooperativityEvidences(object);
        // affected interactions
        prepareAffectedInteractions(object);
        // annotations
        prepareAnnotations(object);
    }

    public void clearCache() {
        // nothing to do
    }

    protected void prepareAnnotations(C object) throws PersisterException, FinderException, SynchronizerException {
        if (object.areAnnotationsInitialized()){
            Collection<Annotation> annotsToPersist = new ArrayList<Annotation>(object.getAnnotations());
            for (Annotation annot : annotsToPersist){
                Annotation persistentAnnot = getContext().getCooperativeEffectAnnotationSynchronizer().synchronize(annot, false);
                // we have a different instance because needed to be synchronized
                if (persistentAnnot != annot){
                    object.getAnnotations().remove(annot);
                    object.getAnnotations().add(persistentAnnot);
                }
            }
        }
    }

    protected void prepareAffectedInteractions(C object) throws PersisterException, FinderException, SynchronizerException {
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

                Complex persistentInteraction = getContext().getComplexSynchronizer().synchronize(convertedComplex, true);
                // we have a different instance because needed to be synchronized
                if (persistentInteraction != interaction){
                    object.getAffectedInteractions().remove(interaction);
                    object.getAffectedInteractions().add(persistentInteraction);
                }
            }
        }
    }

    protected void prepareCooperativityEvidences(C object) throws PersisterException, FinderException, SynchronizerException {
        if (object.areCooperativityEvidencesInitialized()){
            Collection<CooperativityEvidence> parametersToPersist = new ArrayList<CooperativityEvidence>(object.getCooperativityEvidences());
            for (CooperativityEvidence param : parametersToPersist){
                CooperativityEvidence expParam = getContext().getCooperativityEvidenceSynchronizer().synchronize(param, false);
                // we have a different instance because needed to be synchronized
                if (expParam != param){
                    object.getCooperativityEvidences().remove(param);
                    object.getCooperativityEvidences().add(expParam);
                }
            }
        }
    }

    protected void prepareResponse(C object) throws PersisterException, FinderException, SynchronizerException {
       CvTerm outcome = object.getOutCome();
       object.setOutCome(getContext().getTopicSynchronizer().synchronize(outcome, true));
    }

    protected void prepareOutcome(C object) throws PersisterException, FinderException, SynchronizerException {

        CvTerm response = object.getResponse();
        object.setResponse(getContext().getTopicSynchronizer().synchronize(response, true));
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
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<T, C>(this));
    }
}
