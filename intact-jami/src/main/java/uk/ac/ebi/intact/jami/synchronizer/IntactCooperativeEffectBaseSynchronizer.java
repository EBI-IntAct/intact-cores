package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.CooperativeEffectCloner;
import psidev.psi.mi.jami.utils.clone.InteractorCloner;
import uk.ac.ebi.intact.jami.merger.IntactMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
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

public class IntactCooperativeEffectBaseSynchronizer<T extends CooperativeEffect, C extends AbstractIntactCooperativeEffect> extends AbstractIntactDbSynchronizer<T, C>{
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> cvSynchronizer;
    private IntactDbSynchronizer<Annotation, CooperativeEffectAnnotation> annotationSynchronizer;
    private IntactDbSynchronizer<CooperativityEvidence, IntactCooperativityEvidence> evidenceSynchronizer;
    private IntactDbSynchronizer<Complex, IntactComplex> complexSynchronizer;

    private static final Log log = LogFactory.getLog(IntactCooperativeEffectBaseSynchronizer.class);

    public IntactCooperativeEffectBaseSynchronizer(EntityManager entityManager, Class<? extends C> intactClass) {
        super(entityManager, intactClass);
        this.cvSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.TOPIC_OBJCLASS);
        this.annotationSynchronizer = new IntactAnnotationsSynchronizer<CooperativeEffectAnnotation>(entityManager, CooperativeEffectAnnotation.class);
        this.evidenceSynchronizer = new IntactCooperativityEvidenceSynchronizer(entityManager);
        this.complexSynchronizer = new IntactComplexSynchronizer(entityManager);
    }

    public IntactCooperativeEffectBaseSynchronizer(EntityManager entityManager, Class<? extends C> intactClass,
                                                   IntactDbSynchronizer<CvTerm, IntactCvTerm> cvSynchronizer,
                                                   IntactDbSynchronizer<Annotation, CooperativeEffectAnnotation> annotationSynchronizer,
                                                   IntactDbSynchronizer<CooperativityEvidence, IntactCooperativityEvidence> evidenceSynchronizer,
                                                   IntactDbSynchronizer<Complex, IntactComplex> complexSynchronizer) {
        super(entityManager, intactClass);
        this.cvSynchronizer = cvSynchronizer != null ? cvSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.TOPIC_OBJCLASS);
        this.annotationSynchronizer = annotationSynchronizer != null ? annotationSynchronizer : new IntactAnnotationsSynchronizer<CooperativeEffectAnnotation>(entityManager, CooperativeEffectAnnotation.class);
        this.evidenceSynchronizer = evidenceSynchronizer != null ? evidenceSynchronizer : new IntactCooperativityEvidenceSynchronizer(entityManager);
        this.complexSynchronizer = complexSynchronizer != null ? complexSynchronizer : new IntactComplexSynchronizer(entityManager);
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
        this.cvSynchronizer.clearCache();
        this.annotationSynchronizer.clearCache();
        this.evidenceSynchronizer.clearCache();
        this.complexSynchronizer.clearCache();
    }

    protected void prepareAnnotations(C object) throws PersisterException, FinderException, SynchronizerException {
        if (object.areAnnotationsInitialized()){
            Collection<Annotation> annotsToPersist = new ArrayList<Annotation>(object.getAnnotations());
            for (Annotation annot : annotsToPersist){
                Annotation persistentAnnot = this.annotationSynchronizer.synchronize(annot, false);
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

                Complex persistentInteraction = this.complexSynchronizer.synchronize(convertedComplex, true);
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
                CooperativityEvidence expParam = this.evidenceSynchronizer.synchronize(param, false);
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
       object.setOutCome(this.cvSynchronizer.synchronize(outcome, true));
    }

    protected void prepareOutcome(C object) throws PersisterException, FinderException, SynchronizerException {

        CvTerm response = object.getResponse();
        object.setResponse(this.cvSynchronizer.synchronize(response, true));
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
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactMergerIgnoringPersistentObject<T, C>(this));
    }

    protected IntactDbSynchronizer<CvTerm, IntactCvTerm> getCvSynchronizer() {
        return cvSynchronizer;
    }
}
