package uk.ac.ebi.intact.jami.model.extension.factory;

import psidev.psi.mi.jami.binary.BinaryInteraction;
import psidev.psi.mi.jami.binary.BinaryInteractionEvidence;
import psidev.psi.mi.jami.binary.ModelledBinaryInteraction;
import psidev.psi.mi.jami.binary.impl.BinaryInteractionWrapper;
import psidev.psi.mi.jami.binary.impl.DefaultBinaryInteraction;
import psidev.psi.mi.jami.factory.BinaryInteractionFactory;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.InteractionCloner;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractionEvidence;
import uk.ac.ebi.intact.jami.model.extension.binary.IntactBinaryInteractionEvidence;
import uk.ac.ebi.intact.jami.model.extension.binary.IntactBinaryInteractionEvidenceWrapper;
import uk.ac.ebi.intact.jami.model.extension.binary.IntactModelledBinaryInteraction;
import uk.ac.ebi.intact.jami.model.extension.binary.IntactModelledBinaryInteractionWrapper;

/**
 * Intact extension of BinaryInteractionFactory
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>16/10/13</pre>
 */

public class IntactBinaryInteractionFactory implements BinaryInteractionFactory {

    public BinaryInteractionEvidence createSelfBinaryInteractionEvidenceFrom(InteractionEvidence interaction) {
        IntactBinaryInteractionEvidence binary = instantiateNewBinaryInteractionEvidence();
        InteractionCloner.copyAndOverrideParticipantsEvidencesToBinary(interaction, binary, false, true);

        copyIntactEvidenceProperties(interaction, binary);
        return binary;
    }

    public BinaryInteraction createBasicBinaryInteractionFrom(Interaction interaction, Participant p1, Participant p2, CvTerm expansionMethod) {
        BinaryInteraction binary = instantiateNewBinaryInteraction();
        binary.setComplexExpansion(expansionMethod);
        InteractionCloner.copyAndOverrideBasicInteractionProperties(interaction, binary, false, true);
        binary.setParticipantA(p1);
        binary.setParticipantB(p2);
        return binary;
    }

    public BinaryInteractionEvidence createBinaryInteractionEvidenceFrom(InteractionEvidence interaction, ParticipantEvidence p1, ParticipantEvidence p2, CvTerm expansionMethod) {
        IntactBinaryInteractionEvidence binary = instantiateNewBinaryInteractionEvidence();
        binary.setComplexExpansion(expansionMethod);
        copyIntactEvidenceProperties(interaction, binary);
        binary.setParticipantA(p1);
        binary.setParticipantB(p2);
        return binary;
    }

    public BinaryInteraction createSelfBinaryInteractionFrom(Interaction interaction) {
        BinaryInteraction<Participant> binary = instantiateNewBinaryInteraction();
        InteractionCloner.copyAndOverrideBasicInteractionProperties(interaction, binary, false, true);
        InteractionCloner.copyAndOverrideBasicParticipantsToBinary(interaction, binary, false, true);
        return binary;
    }

    public ModelledBinaryInteraction createModelledBinaryInteractionFrom(ModelledInteraction interaction, ModelledParticipant p1, ModelledParticipant p2, CvTerm expansionMethod) {
        IntactModelledBinaryInteraction binary = instantiateNewModelledBinaryInteraction();
        binary.setComplexExpansion(expansionMethod);
        copyIntactModelledInteractionProperties(interaction, binary);
        binary.setParticipantA(p1);
        binary.setParticipantB(p2);
        return binary;
    }

    public ModelledBinaryInteraction createSelfModelledBinaryInteractionFrom(ModelledInteraction interaction) {
        IntactModelledBinaryInteraction binary = instantiateNewModelledBinaryInteraction();
        InteractionCloner.copyAndOverrideModelledParticipantsToBinary(interaction, binary, false, true);

        copyIntactModelledInteractionProperties(interaction, binary);
        return binary;
    }

    public BinaryInteraction createBinaryInteractionWrapperFrom(Interaction interaction) {
        return new BinaryInteractionWrapper(interaction);
    }

    public BinaryInteractionEvidence createBinaryInteractionEvidenceWrapperFrom(InteractionEvidence interaction) {
        return new IntactBinaryInteractionEvidenceWrapper((IntactInteractionEvidence)interaction);
    }

    public ModelledBinaryInteraction createModelledBinaryInteractionWrapperFrom(ModelledInteraction interaction) {
        return new IntactModelledBinaryInteractionWrapper((IntactComplex)interaction);
    }

    public BinaryInteraction instantiateNewBinaryInteraction() {
        return new DefaultBinaryInteraction();
    }

    public IntactBinaryInteractionEvidence instantiateNewBinaryInteractionEvidence() {
        return new IntactBinaryInteractionEvidence();
    }

    public IntactModelledBinaryInteraction instantiateNewModelledBinaryInteraction() {
        return new IntactModelledBinaryInteraction();
    }

    private void copyIntactEvidenceProperties(InteractionEvidence interaction, IntactBinaryInteractionEvidence binary) {
        IntactInteractionEvidence intactSource = (IntactInteractionEvidence)interaction;
        InteractionCloner.copyAndOverrideInteractionEvidenceProperties(interaction, binary, false, true);
        binary.setAc(intactSource.getAc());
        binary.setCreator(intactSource.getCreator());
        binary.setUpdator(intactSource.getUpdator());

    }

    private void copyIntactModelledInteractionProperties(ModelledInteraction interaction, IntactModelledBinaryInteraction binary) {
        IntactModelledBinaryInteraction intactSource = (IntactModelledBinaryInteraction)interaction;
        InteractionCloner.copyAndOverrideModelledInteractionProperties(interaction, binary, false, true);
        binary.setAc(intactSource.getAc());
        binary.setCreator(intactSource.getCreator());
        binary.setUpdator(intactSource.getUpdator());
        binary.getLifecycleEvents().addAll(intactSource.getLifecycleEvents());
        binary.setStatus(intactSource.getStatus());
        binary.setCurrentOwner(intactSource.getCurrentOwner());
        binary.setCurrentReviewer(intactSource.getCurrentReviewer());
    }
}
