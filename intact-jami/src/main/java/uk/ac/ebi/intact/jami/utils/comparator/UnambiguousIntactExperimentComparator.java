package uk.ac.ebi.intact.jami.utils.comparator;

import org.hibernate.Hibernate;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.comparator.experiment.UnambiguousExperimentComparator;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;

/**
 * Comparator for IntAct experiments that take into account annotations and
 * participant identification methods
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class UnambiguousIntactExperimentComparator extends UnambiguousExperimentComparator
implements IntactComparator<Experiment>{

    @Override
    /**
     * @return true if the object has some properties necessary for the comparator that are not lazy loaded
     */
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

        // then check
        return true;
    }
}
