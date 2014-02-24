package uk.ac.ebi.intact.jami.utils;

import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Experiment;
import psidev.psi.mi.jami.utils.ExperimentUtils;
import psidev.psi.mi.jami.utils.comparator.CollectionComparator;
import psidev.psi.mi.jami.utils.comparator.annotation.AnnotationComparator;
import psidev.psi.mi.jami.utils.comparator.annotation.UnambiguousAnnotationComparator;
import psidev.psi.mi.jami.utils.comparator.experiment.UnambiguousExperimentComparator;

import java.util.Collection;

/**
 * Comparator for IntAct experiments that take into account annotations and
 * participant identification methods
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class IntactExperimentComparator extends UnambiguousExperimentComparator{
    private AnnotationComparator annotationComparator;
    private CollectionComparator<Annotation> annotationCollectionComparator;

    public IntactExperimentComparator() {
        super();
        this.annotationComparator = new UnambiguousAnnotationComparator();
        this.annotationCollectionComparator = new CollectionComparator<Annotation>(this.annotationComparator);
    }

    @Override
    public int compare(Experiment exp1, Experiment exp2) {
        if (exp1 == exp2){
            return 0;
        }
        int comp = super.compare(exp1, exp2);
        if (comp != 0){
            return comp;
        }

        CvTerm identificationMethod1 = ExperimentUtils.extractMostCommonParticipantDetectionMethodFrom(exp1);
        CvTerm identificationMethod2 = ExperimentUtils.extractMostCommonParticipantDetectionMethodFrom(exp2);
        int EQUAL = 0;
        int BEFORE = -1;
        int AFTER = 1;

        if (identificationMethod1 == null && identificationMethod2 == null){
            comp = EQUAL;
        }
        else if (identificationMethod1 == null){
            return AFTER;
        }
        else if (identificationMethod2 == null){
            return BEFORE;
        }
        else {
            comp = getCvTermComparator().compare(identificationMethod1, identificationMethod2);
        }
        if (comp != 0){
            return comp;
        }

        // check annotations
        Collection<Annotation> annots1 = exp1.getAnnotations();
        Collection<Annotation> annots2 = exp2.getAnnotations();
        return this.annotationCollectionComparator.compare(annots1, annots2);
    }

    public AnnotationComparator getAnnotationComparator() {
        return annotationComparator;
    }

    public CollectionComparator<Annotation> getAnnotationCollectionComparator() {
        return annotationCollectionComparator;
    }
}
