package uk.ac.ebi.intact.jami.utils.comparator;

import org.hibernate.Hibernate;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.comparator.interactor.ComplexComparator;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledFeature;

/**
 * Basic IntactComplexComparator.
 *
 * It will first look at the default properties of an interactor using AbstractInteractorBaseComparator.
 * It will then compare the interaction types using AbstractCvtermComparator
 * If the basic interactor properties are the same, It will first compare the collection of components using ModelledParticipantComparator.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17/01/13</pre>
 */

public class IntactComplexComparator extends ComplexComparator implements IntactComparator<Complex>{

    /**
     * Creates a bew IntactComplexComparator. It needs a AbstractInteractorBaseComparator to compares interactor properties
     *
     */
    public IntactComplexComparator(){

        super(new IntactExactInteractorBaseComparator(), new IntactModelledParticipantComparator(), new IntactCvTermComparator());
    }

    public IntactComplexComparator(IntactModelledParticipantComparator participantComparator){

        super(new IntactExactInteractorBaseComparator(),
                participantComparator != null ? participantComparator : new IntactModelledParticipantComparator(),
                new IntactCvTermComparator());
    }

    public IntactInteractorBaseComparator getInteractorBaseComparator() {
        return (IntactInteractorBaseComparator)super.getInteractorBaseComparator();
    }

    public IntactCvTermComparator getCvTermComparator() {
        return (IntactCvTermComparator)super.getCvTermComparator();
    }

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
                    if (!IntactInteractorBaseComparator.canCompareAllProperties(part.getInteractor())){
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
