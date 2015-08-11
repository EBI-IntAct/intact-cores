package uk.ac.ebi.intact.jami.utils.comparator;

import org.hibernate.Hibernate;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.comparator.CollectionComparator;
import psidev.psi.mi.jami.utils.comparator.annotation.AnnotationComparator;
import psidev.psi.mi.jami.utils.comparator.annotation.UnambiguousAnnotationComparator;
import psidev.psi.mi.jami.utils.comparator.experiment.ExperimentComparator;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactExperiment;
import uk.ac.ebi.intact.jami.model.extension.IntactParticipantEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import java.util.Collection;

/**
 * Comparator for IntAct experiments that take into account annotations and
 * participant identification methods
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class IntactExperimentComparator extends ExperimentComparator implements IntactComparator<Experiment>{
    private AnnotationComparator annotationComparator;
    private CollectionComparator<Annotation> annotationCollectionComparator;

    public IntactExperimentComparator() {
        super(new IntactPublicationComparator(), new IntactOrganismComparator(),
                new CollectionComparator<VariableParameter>(new IntactVariableParameterComparator()));
        this.annotationComparator = new AnnotationComparator(new IntactCvTermComparator());
        this.annotationCollectionComparator = new CollectionComparator<Annotation>(this.annotationComparator);
    }

    @Override
    public int compare(Experiment exp1, Experiment exp2) {
        int comp = super.compare(exp1, exp2);
        if (comp != 0){
            return comp;
        }

        if (exp1 == exp2){
            return 0;
        }
        else if (exp1 == null){
            return 1;
        }
        else if (exp2 == null){
            return -1;
        }
        else {
            CvTerm identificationMethod1 = IntactUtils.extractMostCommonParticipantDetectionMethodFrom(exp1);
            CvTerm identificationMethod2 = IntactUtils.extractMostCommonParticipantDetectionMethodFrom(exp2);
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
    }

    @Override
    public IntactPublicationComparator getPublicationComparator() {
        return (IntactPublicationComparator) super.getPublicationComparator();
    }

    @Override
    public IntactCvTermComparator getCvTermComparator() {
        return (IntactCvTermComparator) super.getCvTermComparator();
    }

    @Override
    public IntactOrganismComparator getOrganismComparator() {
        return (IntactOrganismComparator) super.getOrganismComparator();
    }

    public UnambiguousAnnotationComparator getAnnotationComparator() {
        return (UnambiguousAnnotationComparator)annotationComparator;
    }

    public CollectionComparator<Annotation> getAnnotationCollectionComparator() {
        return annotationCollectionComparator;
    }

    @Override
    public boolean canCompare(Experiment objectToCompare) {
        // first check publication
        if (objectToCompare.getPublication() != null){
            Publication pub = objectToCompare.getPublication();
            if (pub instanceof IntactPublication){
                if (!((IntactPublication)pub).areAnnotationsInitialized() || !((IntactPublication)pub).areXrefsInitialized()){
                    return false;
                }
            }
        }

        // then check detection method
        if (objectToCompare.getInteractionDetectionMethod() != null){
            CvTerm detMethod = objectToCompare.getInteractionDetectionMethod();
            if (detMethod instanceof IntactCvTerm){
                if (!((IntactCvTerm)detMethod).areXrefsInitialized()){
                    return false;
                }
            }
        }

        // then check detection method
        if (objectToCompare instanceof IntactExperiment){
             IntactExperiment intactExperiment = (IntactExperiment)objectToCompare;
            if (intactExperiment.getParticipantIdentificationMethod() != null){
                CvTerm detMethod = intactExperiment.getParticipantIdentificationMethod();
                if (detMethod instanceof IntactCvTerm){
                    if (!((IntactCvTerm)detMethod).areXrefsInitialized()){
                        return false;
                    }
                }
            }
            else {
                if (!isParticipantIdentificationMethodInitialised(intactExperiment)){
                     return false;
                }
            }
        }
        else if (!isParticipantIdentificationMethodInitialised(objectToCompare)){
            return false;
        }

        // then check organism
        if (objectToCompare.getHostOrganism() != null){
            Organism host = objectToCompare.getHostOrganism();
            // check cellType
            if (host.getCellType() != null && host.getCellType() instanceof IntactCvTerm){
                if (!((IntactCvTerm)host.getCellType()).areXrefsInitialized()){
                    return false;
                }
            }
            // check tissue
            if (host.getTissue() != null && host.getTissue() instanceof IntactCvTerm){
                if (!((IntactCvTerm)host.getTissue()).areXrefsInitialized()){
                    return false;
                }
            }
        }

        // then check variable parameter values
        if (Hibernate.isInitialized(objectToCompare.getVariableParameters())){
            for (VariableParameter param : objectToCompare.getVariableParameters()){
                // check unit
                if (param.getUnit() != null && param.getUnit() instanceof IntactCvTerm){
                    if (!((IntactCvTerm)param.getUnit()).areXrefsInitialized()){
                        return false;
                    }
                }
                // check variable parameter values
                if (!Hibernate.isInitialized(param.getVariableValues())){
                    return false;
                }
            }
        }
        else {
            return false;
        }

        // then check annotations
        if (objectToCompare instanceof IntactExperiment){
             if (!((IntactExperiment) objectToCompare).areAnnotationsInitialized()){
                 return false;
             }
        }

        // then check
        return true;
    }

    private boolean isParticipantIdentificationMethodInitialised(Experiment exp){
         if (Hibernate.isInitialized(exp.getInteractionEvidences())){
              for (InteractionEvidence inter : exp.getInteractionEvidences()){
                  if (Hibernate.isInitialized(inter.getParticipants())){
                      for (ParticipantEvidence part : inter.getParticipants()){
                          if (part instanceof IntactParticipantEvidence){
                              if (!((IntactParticipantEvidence) part).areIdentificationMethodsInitialized()){
                                  return false;
                              }
                          }
                      }
                  }
                  else {
                      return false;
                  }
              }
         }
        else {
             return false;
         }
        return true;
    }
}
