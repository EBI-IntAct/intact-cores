package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import psidev.psi.mi.jami.utils.ChecksumUtils;
import uk.ac.ebi.intact.jami.model.extension.*;

import javax.persistence.EntityManager;

/**
 * Synchronizer for bioactive entities
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/01/14</pre>
 */

public class IntactBioactiveEntitySynchronizer extends IntactInteractorBaseSynchronizer<BioactiveEntity, IntactBioactiveEntity>{

    public IntactBioactiveEntitySynchronizer(EntityManager entityManager) {
        super(entityManager, IntactBioactiveEntity.class);
    }

    public IntactBioactiveEntitySynchronizer(EntityManager entityManager, IntactDbSynchronizer<Alias, InteractorAlias> aliasSynchronizer, IntactDbSynchronizer<Annotation, InteractorAnnotation> annotationSynchronizer, IntactDbSynchronizer<Xref, InteractorXref> xrefSynchronizer, IntactDbSynchronizer<Organism, IntactOrganism> organismSynchronizer, IntactDbSynchronizer<CvTerm, IntactCvTerm> typeSynchronizer, IntactDbSynchronizer<Checksum, InteractorChecksum> checksumSynchronizer) {
        super(entityManager, IntactBioactiveEntity.class, aliasSynchronizer, annotationSynchronizer, xrefSynchronizer, organismSynchronizer, typeSynchronizer, checksumSynchronizer);
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


    protected void prepareAnnotations(IntactBioactiveEntity intactInteractor) throws FinderException, PersisterException, SynchronizerException {
        super.prepareAnnotations(intactInteractor);
        if (intactInteractor.areAnnotationsInitialized()){
            for (Annotation annot : intactInteractor.getAnnotations()){
                // we have a checksum
                if (AnnotationUtils.doesAnnotationHaveTopic(annot, Checksum.SMILE_MI, Checksum.SMILE)
                        && annot.getValue() != null && intactInteractor.getSmile() == null){
                     intactInteractor.getChecksums().add(new InteractorChecksum(annot.getTopic(), annot.getValue()));
                }
                else if (AnnotationUtils.doesAnnotationHaveTopic(annot, Checksum.STANDARD_INCHI_KEY_MI, Checksum.STANDARD_INCHI_KEY)
                        && annot.getValue() != null && intactInteractor.getStandardInchiKey() == null){
                    intactInteractor.getChecksums().add(new InteractorChecksum(annot.getTopic(), annot.getValue()));
                }
                else if (AnnotationUtils.doesAnnotationHaveTopic(annot, Checksum.INCHI_MI, Checksum.INCHI)
                        && annot.getValue() != null && intactInteractor.getStandardInchi() == null){
                    intactInteractor.getChecksums().add(new InteractorChecksum(annot.getTopic(), annot.getValue()));
                }
            }
        }
    }
}
