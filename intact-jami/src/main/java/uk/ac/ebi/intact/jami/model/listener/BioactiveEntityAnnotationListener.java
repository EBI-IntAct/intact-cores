package uk.ac.ebi.intact.jami.model.listener;

import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.Checksum;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import psidev.psi.mi.jami.utils.ChecksumUtils;
import uk.ac.ebi.intact.jami.model.extension.IntactBioactiveEntity;
import uk.ac.ebi.intact.jami.model.extension.InteractorAnnotation;
import uk.ac.ebi.intact.jami.model.extension.InteractorChecksum;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * This listener listen to bioactive entities object pre update/persist/load events
 * and set checksums/annotations accordingly to existing annotations
 * This listener is for backward compatibility only with previous intact-core.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07/01/14</pre>
 */
public class BioactiveEntityAnnotationListener {

    @PrePersist
    @PreUpdate
    public void prePersist(IntactBioactiveEntity intactEntity) {
        if (!intactEntity.getChecksums().isEmpty()){
            for (Checksum check : intactEntity.getChecksums()){
                Annotation annot = AnnotationUtils.collectFirstAnnotationWithTopic(intactEntity.getAnnotations(), check.getMethod().getMIIdentifier(), check.getMethod().getShortName());
                if (annot == null || (annot != null && !annot.getValue().equals(check.getValue()))){
                    intactEntity.getAnnotations().add(new InteractorAnnotation(check.getMethod(), check.getValue()));
                }
            }
        }
    }

    @PostLoad
    public void postLoad(IntactBioactiveEntity intactEntity) {
        if (!intactEntity.getAnnotations().isEmpty()){
            for (Annotation annot : intactEntity.getAnnotations()){
                if (AnnotationUtils.doesAnnotationHaveTopic(annot, Checksum.CHECKSUM_MI, Checksum.CHECKUM)
                        || AnnotationUtils.doesAnnotationHaveTopic(annot, Checksum.SMILE_MI, Checksum.SMILE)
                        || AnnotationUtils.doesAnnotationHaveTopic(annot, Checksum.STANDARD_INCHI_KEY_MI, Checksum.STANDARD_INCHI_KEY)
                        || AnnotationUtils.doesAnnotationHaveTopic(annot, Checksum.INCHI_KEY_MI, Checksum.INCHI_KEY)
                        || AnnotationUtils.doesAnnotationHaveTopic(annot, Checksum.INCHI_MI, Checksum.INCHI)
                        || AnnotationUtils.doesAnnotationHaveTopic(annot, Checksum.INCHI_MI, Checksum.INCHI_SHORT)){
                    Checksum check = ChecksumUtils.collectFirstChecksumWithMethod(intactEntity.getChecksums(), annot.getTopic().getMIIdentifier(), annot.getTopic().getShortName());
                    if (check == null || (check != null && !check.getValue().equals(annot.getValue()))){
                        intactEntity.getChecksums().add(new InteractorChecksum(annot.getTopic(), annot.getValue()));
                    }
                }
            }
        }
    }
}
