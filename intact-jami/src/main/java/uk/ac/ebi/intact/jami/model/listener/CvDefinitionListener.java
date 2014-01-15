package uk.ac.ebi.intact.jami.model.listener;

import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * This listener listen to Cv object pre update/persist/load events
 * and generates a definition xref when it is necessary
 * This listener is for backward compatibility only with previous intact-core.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07/01/14</pre>
 */
public class CvDefinitionListener {

    @PrePersist
    @PreUpdate
    public void prePersist(IntactCvTerm intactCv) {
        if (intactCv.getDefinition() == null){
            AnnotationUtils.removeAllAnnotationsWithTopic(intactCv.getAnnotations(), null, "definition");
        }
        else{
            Annotation def = AnnotationUtils.collectFirstAnnotationWithTopic(intactCv.getAnnotations(), null, "definition");
            if (!intactCv.getDefinition().equalsIgnoreCase(def.getValue())){
                def.setValue(intactCv.getDefinition());
            }
        }
    }

    @PostLoad
    public void postLoad(IntactCvTerm intactCv) {
        if (intactCv.getDefinition() == null){
            AnnotationUtils.removeAllAnnotationsWithTopic(intactCv.getAnnotations(), null, "definition");
        }
        else{
            Annotation def = AnnotationUtils.collectFirstAnnotationWithTopic(intactCv.getAnnotations(), null, "definition");
            if (!intactCv.getDefinition().equalsIgnoreCase(def.getValue())){
                def.setValue(intactCv.getDefinition());
            }
        }
    }
}
