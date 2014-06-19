package uk.ac.ebi.intact.jami.model.extension.binary;

import psidev.psi.mi.jami.binary.ModelledBinaryInteraction;
import psidev.psi.mi.jami.binary.impl.ModelledBinaryInteractionWrapper;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.model.IntactPrimaryObject;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleStatus;
import uk.ac.ebi.intact.jami.model.user.User;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * A wrapper for ModelledInteraction which contains two participants
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>05/06/13</pre>
 */

public class IntactModelledBinaryInteractionWrapper implements ModelledBinaryInteraction, IntactPrimaryObject{
    private IntactComplex wrappedInteraction;
    private ModelledBinaryInteractionWrapper binaryWrapper;

    public IntactModelledBinaryInteractionWrapper(IntactComplex interaction){
        this.wrappedInteraction = interaction;
        this.binaryWrapper = new ModelledBinaryInteractionWrapper(interaction);
    }

    public IntactModelledBinaryInteractionWrapper(IntactComplex interaction, CvTerm complexExpansion){
        this(interaction);
        this.binaryWrapper = new ModelledBinaryInteractionWrapper(interaction, complexExpansion);
    }

    public ModelledParticipant getParticipantA() {
        return this.binaryWrapper.getParticipantA();
    }

    public ModelledParticipant getParticipantB() {
        return this.binaryWrapper.getParticipantB();
    }

    public void setParticipantA(ModelledParticipant participantA) {
        this.binaryWrapper.setParticipantA(participantA);
    }

    public void setParticipantB(ModelledParticipant participantB) {
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
    public Collection<ModelledParticipant> getParticipants() {
        return this.binaryWrapper.getParticipants();
    }

    /**
     * Adds a new Participant and set the Interaction of this participant if added.
     * If the participant B and A are null, it will first set the participantA. If the participantA is set, it will set the ParticipantB
     * @param part
     * @return
     * @throws IllegalArgumentException if this Binary interaction already contains two participants
     */
    public boolean addParticipant(ModelledParticipant part) {
        return this.binaryWrapper.addParticipant(part);
    }

    /**
     * Removes the Participant from this binary interaction
     * @param part
     * @return
     */
    public boolean removeParticipant(ModelledParticipant part) {
        return this.binaryWrapper.removeParticipant(part);
    }

    /**
     * Adds the participants and set the Interaction of this participant if added.
     * If the participant B and A are null, it will first set the participantA. If the participantA is set, it will set the ParticipantB
     * @param participants
     * @return
     * @throws IllegalArgumentException if this Binary interaction already contains two participants or the given participants contains more than two participants
     */
    public boolean addAllParticipants(Collection<? extends ModelledParticipant> participants) {
        return this.binaryWrapper.addAllParticipants(participants);
    }

    public boolean removeAllParticipants(Collection<? extends ModelledParticipant> participants) {
        return this.binaryWrapper.removeAllParticipants(participants);
    }

    public String getFullName() {
        return this.wrappedInteraction.getFullName();
    }

    public void setFullName(String name) {
        this.wrappedInteraction.setFullName(name);
    }

    public Collection<Alias> getAliases() {
        return this.wrappedInteraction.getAliases();
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

    public Collection<InteractionEvidence> getInteractionEvidences() {
        return this.wrappedInteraction.getInteractionEvidences();
    }

    public Source getSource() {
        return this.wrappedInteraction.getSource();
    }

    public void setSource(Source source) {
        this.wrappedInteraction.setSource(source);
    }

    public CvTerm getEvidenceType() {
        return this.wrappedInteraction.getEvidenceType();
    }

    public void setEvidenceType(CvTerm eco) {
        this.wrappedInteraction.setEvidenceType(eco);
    }

    public Collection<ModelledConfidence> getModelledConfidences() {
        return this.wrappedInteraction.getModelledConfidences();
    }

    public Collection<ModelledParameter> getModelledParameters() {
        return this.wrappedInteraction.getModelledParameters();
    }

    public Collection<CooperativeEffect> getCooperativeEffects() {
        return this.wrappedInteraction.getCooperativeEffects();
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

    public LifeCycleStatus getStatus() {
        return this.wrappedInteraction.getStatus();
    }

    public void setStatus( LifeCycleStatus status ) {
        this.wrappedInteraction.setStatus(status);
    }

    public List<LifeCycleEvent> getLifecycleEvents() {
        return this.wrappedInteraction.getLifecycleEvents();
    }

    public User getCurrentOwner() {
        return this.wrappedInteraction.getCurrentOwner();
    }

    public void setCurrentOwner( User currentOwner ) {
        this.wrappedInteraction.setCurrentOwner(currentOwner);
    }

    public User getCurrentReviewer() {
        return this.wrappedInteraction.getCurrentReviewer();
    }

    public void setCurrentReviewer( User currentReviewer ) {
        this.wrappedInteraction.setCurrentReviewer(currentReviewer);
    }
}