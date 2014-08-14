package uk.ac.ebi.intact.jami.utils.comparator;

import org.hibernate.Hibernate;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.comparator.interactor.UnambiguousExactComplexComparator;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledFeature;

/**
 * Comparator for IntAct experiments that take into account annotations and
 * participant identification methods
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */

public class UnambiguousIntactComplexComparator extends UnambiguousExactComplexComparator
implements IntactComparator<Complex>{

    @Override
    /**
     * @return true if the object has some properties necessary for the comparator that are not lazy loaded
     */
    public boolean canCompare(Complex objectToCompare) {
        // first check interactor type
        if (objectToCompare.getInteractorType() != null){
            if (objectToCompare.getInteractorType() instanceof IntactCvTerm){
                if (!((IntactCvTerm)objectToCompare.getInteractorType()).areXrefsInitialized()){
                    return false;
                }
            }
        }

        // then check interaction type
        if (objectToCompare.getInteractionType() != null){
            if (objectToCompare.getInteractionType() instanceof IntactCvTerm){
                if (!((IntactCvTerm)objectToCompare.getInteractionType()).areXrefsInitialized()){
                    return false;
                }
            }
        }

        // then check organism
        if (objectToCompare.getOrganism() != null){
            Organism host = objectToCompare.getOrganism();
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

        // then check identifiers
        if (objectToCompare instanceof IntactComplex){
            IntactComplex intactInteractor = (IntactComplex)objectToCompare;
            if (!intactInteractor.areXrefsInitialized()){
                return false;
            }

            if (!intactInteractor.areAliasesInitialized()){
                return false;
            }

            // then check participants
            if (intactInteractor.areParticipantsInitialized()){
                for (ModelledParticipant part : intactInteractor.getParticipants()){
                    // check role
                    if (part.getBiologicalRole() != null){
                        if (part.getBiologicalRole() instanceof IntactCvTerm){
                            if (!((IntactCvTerm)part.getBiologicalRole()).areXrefsInitialized()){
                                return false;
                            }
                        }
                    }

                    // check interactor
                    if (!UnambiguousIntactInteractorBaseComparator.canCompareAllProperties(part.getInteractor())){
                         return false;
                    }

                    // check features
                    if (Hibernate.isInitialized(part.getFeatures())){
                        for (ModelledFeature feature : part.getFeatures()){
                            // check type
                            if (feature.getType() != null){
                                if (feature.getType() instanceof IntactCvTerm){
                                    if (!((IntactCvTerm)feature.getType()).areXrefsInitialized()){
                                        return false;
                                    }
                                }
                            }

                            // check role
                            if (feature.getRole() != null){
                                if (feature.getRole() instanceof IntactCvTerm){
                                    if (!((IntactCvTerm)feature.getRole()).areXrefsInitialized()){
                                        return false;
                                    }
                                }
                            }

                            // check ranges
                            if (Hibernate.isInitialized(feature.getRanges())){
                                 for (Range r : feature.getRanges()){
                                     // check start status
                                     if (r.getStart().getStatus() instanceof IntactCvTerm){
                                         if (!((IntactCvTerm)r.getStart().getStatus()).areXrefsInitialized()){
                                             return false;
                                         }
                                     }
                                     // check end status
                                     if (r.getEnd().getStatus() instanceof IntactCvTerm){
                                         if (!((IntactCvTerm)r.getEnd().getStatus()).areXrefsInitialized()){
                                             return false;
                                         }
                                     }
                                 }
                            }
                            else{
                                return false;
                            }

                            // check xrefs
                            if (feature instanceof IntactModelledFeature){
                                 if (!((IntactModelledFeature)feature).areXrefsInitialized()){
                                     return false;
                                 }
                            }
                        }
                    }
                    else{
                        return false;
                    }
                }
            }
            else{
                return false;
            }
        }

        // then check
        return true;
    }
}
