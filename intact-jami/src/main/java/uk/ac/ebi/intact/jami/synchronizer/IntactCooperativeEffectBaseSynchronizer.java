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
        getCvSynchronizer().clearCache();
        getAnnotationSynchronizer().clearCache();
        getEvidenceSynchronizer().clearCache();
        getComplexSynchronizer().clearCache();
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getCvSynchronizer() {
        if (this.cvSynchronizer == null){
            this.cvSynchronizer = new IntactCvTermSynchronizer(getEntityManager(), IntactUtils.TOPIC_OBJCLASS);
        }
        return cvSynchronizer;
    }

    public void setCvSynchronizer(IntactDbSynchronizer<CvTerm, IntactCvTerm> cvSynchronizer) {
        this.cvSynchronizer = cvSynchronizer;
    }

    public IntactDbSynchronizer<Annotation, CooperativeEffectAnnotation> getAnnotationSynchronizer() {
        if (this.annotationSynchronizer == null){
            this.annotationSynchronizer = new IntactAnnotationsSynchronizer<CooperativeEffectAnnotation>(getEntityManager(), CooperativeEffectAnnotation.class);
        }
        return annotationSynchronizer;
    }

    public void setAnnotationSynchronizer(IntactDbSynchronizer<Annotation, CooperativeEffectAnnotation> annotationSynchronizer) {
        this.annotationSynchronizer = annotationSynchronizer;
    }

    public IntactDbSynchronizer<CooperativityEvidence, IntactCooperativityEvidence> getEvidenceSynchronizer() {
        if (this.evidenceSynchronizer == null){
            this.evidenceSynchronizer = new IntactCooperativityEvidenceSynchronizer(getEntityManager());
        }
        return evidenceSynchronizer;
    }

    public void setEvidenceSynchronizer(IntactDbSynchronizer<CooperativityEvidence, IntactCooperativityEvidence> evidenceSynchronizer) {
        this.evidenceSynchronizer = evidenceSynchronizer;
    }

    public IntactDbSynchronizer<Complex, IntactComplex> getComplexSynchronizer() {
        if (this.complexSynchronizer == null){
            this.complexSynchronizer = new IntactComplexSynchronizer(getEntityManager());
        }
        return complexSynchronizer;
    }

    public void setComplexSynchronizer(IntactDbSynchronizer<Complex, IntactComplex> complexSynchronizer) {
        this.complexSynchronizer = complexSynchronizer;
    }

    protected void prepareAnnotations(C object) throws PersisterException, FinderException, SynchronizerException {
        if (object.areAnnotationsInitialized()){
            Collection<Annotation> annotsToPersist = new ArrayList<Annotation>(object.getAnnotations());
            for (Annotation annot : annotsToPersist){
                Annotation persistentAnnot = getAnnotationSynchronizer().synchronize(annot, false);
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

                Complex persistentInteraction = getComplexSynchronizer().synchronize(convertedComplex, true);
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
                CooperativityEvidence expParam = getEvidenceSynchronizer().synchronize(param, false);
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
       object.setOutCome(getCvSynchronizer().synchronize(outcome, true));
    }

    protected void prepareOutcome(C object) throws PersisterException, FinderException, SynchronizerException {

        CvTerm response = object.getResponse();
        object.setResponse(getCvSynchronizer().synchronize(response, true));
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
}
