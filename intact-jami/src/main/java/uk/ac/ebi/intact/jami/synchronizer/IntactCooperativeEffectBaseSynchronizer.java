package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.CooperativityEvidenceCloner;
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

public class IntactCooperativeEffectBaseSynchronizer<C extends AbstractIntactCooperativeEffect> extends AbstractIntactDbSynchronizer<CooperativeEffect, C>{
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> cvSynchronizer;
    private IntactDbSynchronizer<Annotation, CooperativeEffectAnnotation> annotationSynchronizer;
    private IntactDbSynchronizer<CooperativityEvidence, IntactCooperativityEvidence> evidenceSynchronizer;
    private IntactDbSynchronizer<ModelledInteraction, IntactComplex> complexSynchronizer;

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

    public IntactCooperativityEvidence find(CooperativityEvidence object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(IntactCooperativityEvidence object) throws FinderException, PersisterException, SynchronizerException {
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
                Annotation persistentAnnot = this.annotationSynchronizer.synchronize(annot, true);
                // we have a different instance because needed to be synchronized
                if (persistentAnnot != annot){
                    object.getAnnotations().remove(annot);
                    object.getAnnotations().add(persistentAnnot);
                }
            }
        }
    }

    protected void prepareAffectedInteractions(C object) {
        if (object.areAffectedInteractionsInitialized()){
            Collection<ModelledInteraction> interactionsToPersist = new ArrayList<ModelledInteraction>(object.getAffectedInteractions());
            for (ModelledInteraction interaction : interactionsToPersist){
                Complex persistentInteraction = this.annotationSynchronizer.synchronize(interaction, true);
                // we have a different instance because needed to be synchronized
                if (persistentInteraction != interaction){
                    object.getAnnotations().remove(interaction);
                    object.getAnnotations().add(persistentInteraction);
                }
            }
        }
    }

    protected void prepareCooperativityEvidences(IntactCooperativityEvidence object) {
        if (object.areEvidenceMethodsInitialized()){
            Collection<CvTerm> parametersToPersist = new ArrayList<CvTerm>(object.getEvidenceMethods());
            for (CvTerm param : parametersToPersist){
                CvTerm expParam = this.cvSynchronizer.synchronize(param, true);
                // we have a different instance because needed to be synchronized
                if (expParam != param){
                    object.getEvidenceMethods().remove(param);
                    object.getEvidenceMethods().add(param);
                }
            }
        }
    }

    protected void prepareResponse(IntactCooperativityEvidence object) {

    }

    protected void prepareOutcome(IntactCooperativityEvidence object) {


    }

    @Override
    protected Object extractIdentifier(C object) {
        return object.getId();
    }

    @Override
    protected C instantiateNewPersistentInstance(CooperativeEffect object, Class<? extends C> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactCooperativityEvidence ev = new IntactCooperativityEvidence(object.getPublication());
        CooperativityEvidenceCloner.copyAndOverrideCooperativityEvidenceProperties(object, ev);
        return ev;
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactMergerIgnoringPersistentObject<CooperativeEffect, C>(this));
    }
}
