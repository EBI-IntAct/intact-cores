package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.BioactiveEntity;
import psidev.psi.mi.jami.model.Checksum;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import psidev.psi.mi.jami.utils.ChecksumUtils;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.model.extension.IntactBioactiveEntity;
import uk.ac.ebi.intact.jami.model.extension.InteractorAnnotation;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

/**
 * Synchronizer for bioactive entities
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/01/14</pre>
 */

public class BioactiveEntitySynchronizer extends InteractorSynchronizerTemplate<BioactiveEntity, IntactBioactiveEntity> {

    public BioactiveEntitySynchronizer(SynchronizerContext context) {
        super(context, IntactBioactiveEntity.class);
    }

    @Override
    protected void prepareChecksums(IntactBioactiveEntity intactInteractor) throws FinderException, PersisterException, SynchronizerException {
        super.prepareChecksums(intactInteractor);
        if (intactInteractor.areChecksumsInitialized()){
            String smile = intactInteractor.getSmile();
            if (smile != null){
                // for backward compatibility, check if this checksum is in annotations and add it if it is not there
                Annotation annot = AnnotationUtils.collectFirstAnnotationWithTopic(intactInteractor.getAnnotations(), Checksum.SMILE_MI, Checksum.SMILE);
                if (annot == null || (annot != null && !annot.getValue().equals(smile))){
                    intactInteractor.getAnnotations().remove(annot);
                    intactInteractor.getAnnotations().add(new InteractorAnnotation(ChecksumUtils.collectFirstChecksumWithMethodAndValue(intactInteractor.getChecksums(), Checksum.SMILE_MI, Checksum.SMILE, smile).getMethod(), smile));
                }
            }
            String standardInchi = intactInteractor.getStandardInchi();
            if (standardInchi != null){
                // for backward compatibility, check if this checksum is in annotations and add it if it is not there
                Annotation annot = AnnotationUtils.collectFirstAnnotationWithTopic(intactInteractor.getAnnotations(), Checksum.INCHI_MI, Checksum.INCHI);
                if (annot == null || (annot != null && !annot.getValue().equals(smile))){
                    intactInteractor.getAnnotations().remove(annot);
                    intactInteractor.getAnnotations().add(new InteractorAnnotation(ChecksumUtils.collectFirstChecksumWithMethodAndValue(intactInteractor.getChecksums(), Checksum.INCHI_MI, Checksum.INCHI, standardInchi).getMethod(), standardInchi));
                }
            }
            String inchiKey = intactInteractor.getStandardInchiKey();
            if (inchiKey != null){
                // for backward compatibility, check if this checksum is in annotations and add it if it is not there
                Annotation annot = AnnotationUtils.collectFirstAnnotationWithTopic(intactInteractor.getAnnotations(), Checksum.INCHI_KEY_MI, Checksum.INCHI_KEY);
                if (annot == null || (annot != null && !annot.getValue().equals(inchiKey))){
                    intactInteractor.getAnnotations().remove(annot);
                    intactInteractor.getAnnotations().add(new InteractorAnnotation(ChecksumUtils.collectFirstChecksumWithMethodAndValue(intactInteractor.getChecksums(), Checksum.INCHI_KEY_MI, Checksum.INCHI_KEY, inchiKey).getMethod(), inchiKey));
                }
            }
        }
    }
}
