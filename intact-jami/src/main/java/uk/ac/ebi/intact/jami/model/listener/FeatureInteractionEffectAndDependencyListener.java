package uk.ac.ebi.intact.jami.model.listener;

import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.Feature;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactFeature;
import uk.ac.ebi.intact.jami.model.extension.FeatureAnnotation;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * This listener listen to feature object pre update/persist/load events
 * and set the interaction dependency/interaction effect property when it is necessary
 * This listener is for backward compatibility only with previous intact-core.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07/01/14</pre>
 */
public class FeatureInteractionEffectAndDependencyListener {

    @PrePersist
    @PreUpdate
    public void prePersist(AbstractIntactFeature intactFeature) {
        if (intactFeature.getInteractionDependency() != null){
            Annotation annot = AnnotationUtils.collectFirstAnnotationWithTopic(intactFeature.getAnnotations(), intactFeature.getInteractionDependency().getMIIdentifier(), intactFeature.getInteractionDependency().getShortName());
            if (annot == null || !intactFeature.getInteractionDependency().equals(annot.getTopic())){
                intactFeature.getAnnotations().add(new FeatureAnnotation(intactFeature.getInteractionDependency()));
            }
        }
        else{
            AnnotationUtils.removeAllAnnotationsWithTopic(intactFeature.getAnnotations(), intactFeature.getInteractionDependency().getMIIdentifier(), intactFeature.getInteractionDependency().getShortName());
        }

        if (intactFeature.getInteractionEffect() != null){
            Annotation annot = AnnotationUtils.collectFirstAnnotationWithTopic(intactFeature.getAnnotations(), intactFeature.getInteractionEffect().getMIIdentifier(), intactFeature.getInteractionEffect().getShortName());
            if (annot == null || !intactFeature.getInteractionEffect().equals(annot.getTopic())){
                intactFeature.getAnnotations().add(new FeatureAnnotation(intactFeature.getInteractionEffect()));
            }
        }
        else{
            AnnotationUtils.removeAllAnnotationsWithTopic(intactFeature.getAnnotations(), intactFeature.getInteractionEffect().getMIIdentifier(), intactFeature.getInteractionEffect().getShortName());
        }
    }

    @PostLoad
    public void postLoad(AbstractIntactFeature intactFeature) {
        for (Object obj : intactFeature.getAnnotations()){
            Annotation annotation = (Annotation)obj;
            if (AnnotationUtils.doesAnnotationHaveTopic(annotation, Feature.PREREQUISITE_PTM_MI, Feature.PREREQUISITE_PTM)
                    || AnnotationUtils.doesAnnotationHaveTopic(annotation, Feature.RESULTING_PTM_MI, Feature.RESULTING_PTM)
                    || AnnotationUtils.doesAnnotationHaveTopic(annotation, Feature.RESULTING_CLEAVAGE_MI, Feature.RESULTING_CLEAVAGE)){
                intactFeature.setInteractionDependency(annotation.getTopic());
            }
            // we have an interaction effect
            else if (AnnotationUtils.doesAnnotationHaveTopic(annotation, Feature.DECREASING_PTM_MI, Feature.DECREASING_PTM)
                    || AnnotationUtils.doesAnnotationHaveTopic(annotation, Feature.INCREASING_PTM_MI, Feature.INCREASING_PTM)
                    || AnnotationUtils.doesAnnotationHaveTopic(annotation, Feature.DISRUPTING_PTM_MI, Feature.DISRUPTING_PTM)){
                intactFeature.setInteractionEffect(annotation.getTopic());
            }
        }
    }
}
