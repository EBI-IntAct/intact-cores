package uk.ac.ebi.intact.jami.model.extension.binary;

import psidev.psi.mi.jami.binary.BinaryInteractionEvidence;
import psidev.psi.mi.jami.binary.impl.BinaryInteractionEvidenceWrapper;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.context.UserContext;
import uk.ac.ebi.intact.jami.model.IntactPrimaryObject;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractionEvidence;

import java.util.Collection;
import java.util.Date;

/**
 * A wrapper for InteractionEvidence which contains two participants
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>05/06/13</pre>
 */

public class IntactBinaryInteractionEvidenceWrapper implements BinaryInteractionEvidence,IntactPrimaryObject{
    private IntactInteractionEvidence wrappedInteraction;
    private BinaryInteractionEvidenceWrapper binaryWrapper;

    public IntactBinaryInteractionEvidenceWrapper(IntactInteractionEvidence interaction){
        this.wrappedInteraction = interaction;
        this.binaryWrapper = new BinaryInteractionEvidenceWrapper(interaction);
    }

    public IntactBinaryInteractionEvidenceWrapper(IntactInteractionEvidence interaction, CvTerm complexExpansion){
        this(interaction);
        this.binaryWrapper = new BinaryInteractionEvidenceWrapper(interaction, complexExpansion);
    }

    public ParticipantEvidence getParticipantA() {
        return this.binaryWrapper.getParticipantA();
    }

    public ParticipantEvidence getParticipantB() {
        return this.binaryWrapper.getParticipantB();
    }

    public void setParticipantA(ParticipantEvidence participantA) {
        this.binaryWrapper.setParticipantA(participantA);
    }

    public void setParticipantB(ParticipantEvidence participantB) {
        this.binaryWrapper.setParticipantB(participantB);
    }

    public CvTerm getComplexExpansion() {
        return this.binaryWrapper.getComplexExpansion();
    }

    public void setComplexExpansion(CvTerm expansion) {
        this.binaryWrapper.setComplexExpansion(expansion);
    }

    /**
     * The collection of participants for this binary interaction.
     * It cannot be changed.
     * @return
     */
    public Collection<ParticipantEvidence> getParticipants() {
        return this.binaryWrapper.getParticipants();
    }

    /**
     * Adds a new Participant and set the Interaction of this participant if added.
     * If the participant B and A are null, it will first set the participantA. If the participantA is set, it will set the ParticipantB
     * @param part
     * @return
     * @throws IllegalArgumentException if this Binary interaction already contains two participants
     */
    public boolean addParticipant(ParticipantEvidence part) {
        return this.binaryWrapper.addParticipant(part);
    }

    /**
     * Removes the Participant from this binary interaction
     * @param part
     * @return
     */
    public boolean removeParticipant(ParticipantEvidence part) {
        return this.binaryWrapper.removeParticipant(part);
    }

    /**
     * Adds the participants and set the Interaction of this participant if added.
     * If the participant B and A are null, it will first set the participantA. If the participantA is set, it will set the ParticipantB
     * @param participants
     * @return
     * @throws IllegalArgumentException if this Binary interaction already contains two participants or the given participants contains more than two participants
     */
    public boolean addAllParticipants(Collection<? extends ParticipantEvidence> participants) {
        return this.binaryWrapper.addAllParticipants(participants);
    }

    public boolean removeAllParticipants(Collection<? extends ParticipantEvidence> participants) {
        return this.binaryWrapper.removeAllParticipants(participants);
    }

    public String getShortName() {
        return this.wrappedInteraction.getShortName();
    }

    public void setShortName(String name) {
        this.wrappedInteraction.setShortName(name);
    }

    public String getRigid() {
        return this.wrappedInteraction.getRigid();
    }

    public void setRigid(String rigid) {
        this.wrappedInteraction.setRigid(rigid);
    }

    public Collection<Xref> getIdentifiers() {
        return this.wrappedInteraction.getIdentifiers();
    }

    public Collection<Xref> getXrefs() {
        return this.wrappedInteraction.getXrefs();
    }

    public Collection<Checksum> getChecksums() {
        return this.wrappedInteraction.getChecksums();
    }

    public Collection<Annotation> getAnnotations() {
        return this.wrappedInteraction.getAnnotations();
    }

    public Date getUpdatedDate() {
        return this.wrappedInteraction.getUpdatedDate();
    }

    public void setUpdatedDate(Date updated) {
        this.wrappedInteraction.setUpdatedDate(updated);
    }

    public Date getCreatedDate() {
        return this.wrappedInteraction.getCreatedDate();
    }

    public void setCreatedDate(Date created) {
        this.wrappedInteraction.setCreatedDate(created);
    }

    public CvTerm getInteractionType() {
        return this.wrappedInteraction.getInteractionType();
    }

    public void setInteractionType(CvTerm term) {
        this.wrappedInteraction.setInteractionType(term);
    }

    @Override
    public String toString() {
        return this.wrappedInteraction.toString();
    }

    public String getImexId() {
        return this.wrappedInteraction.getImexId();
    }

    public void assignImexId(String identifier) {
        this.wrappedInteraction.assignImexId(identifier);
    }

    public Experiment getExperiment() {
        return this.wrappedInteraction.getExperiment();
    }

    public void setExperiment(Experiment experiment) {
        this.wrappedInteraction.setExperiment(experiment);
    }

    public void setExperimentAndAddInteractionEvidence(Experiment experiment) {
        this.wrappedInteraction.setExperimentAndAddInteractionEvidence(experiment);
    }

    public Collection<VariableParameterValueSet> getVariableParameterValues() {
        return this.wrappedInteraction.getVariableParameterValues();
    }

    public String getAvailability() {
        return this.wrappedInteraction.getAvailability();
    }

    public void setAvailability(String availability) {
        this.wrappedInteraction.setAvailability(availability);
    }

    public Collection<Parameter> getParameters() {
        return this.wrappedInteraction.getParameters();
    }

    public boolean isInferred() {
        return this.wrappedInteraction.isInferred();
    }

    public void setInferred(boolean inferred) {
        this.wrappedInteraction.setInferred(isInferred());
    }

    public Collection<Confidence> getConfidences() {
        return this.wrappedInteraction.getConfidences();
    }

    public boolean isNegative() {
        return this.wrappedInteraction.isNegative();
    }

    public void setNegative(boolean negative) {
        this.wrappedInteraction.setNegative(negative);
    }

    public String getAc() {
        return this.wrappedInteraction.getAc();
    }

    public Date getCreated() {
        return this.wrappedInteraction.getCreated();
    }

    public void setCreated(Date created) {
        this.wrappedInteraction.setCreated(created);
    }

    public Date getUpdated() {
        return this.wrappedInteraction.getUpdated();
    }

    public void setUpdated(Date updated) {
        this.wrappedInteraction.setUpdated(updated);
    }

    public String getCreator() {
        return this.wrappedInteraction.getCreator();
    }

    public void setCreator(String createdUser) {
        this.wrappedInteraction.setCreator(createdUser);
    }

    public String getUpdator() {
        return this.wrappedInteraction.getUpdator();
    }

    public void setUpdator(String userStamp) {
        this.wrappedInteraction.setUpdator(userStamp);
    }

    @Override
    public UserContext getLocalUserContext() {
        return this.wrappedInteraction.getLocalUserContext();
    }

    @Override
    public void setLocalUserContext(UserContext context) {
        this.wrappedInteraction.setLocalUserContext(context);
    }
}