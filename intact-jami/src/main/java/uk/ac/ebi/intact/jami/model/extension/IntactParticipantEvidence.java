package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.model.IntactPrimaryObject;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Intact implementation of participant evidence
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15/01/14</pre>
 */
@javax.persistence.Entity
@DiscriminatorValue("participant_evidence")
public class IntactParticipantEvidence extends AbstractIntactExperimentalEntity implements ParticipantEvidence{

    private InteractionEvidence interaction;
    private String interactionAc;

    protected IntactParticipantEvidence() {
    }

    public IntactParticipantEvidence(Interactor interactor, CvTerm participantIdentificationMethod) {
        super(interactor, participantIdentificationMethod);
    }

    public IntactParticipantEvidence(Interactor interactor, CvTerm bioRole, CvTerm participantIdentificationMethod) {
        super(interactor, bioRole, participantIdentificationMethod);
    }

    public IntactParticipantEvidence(Interactor interactor, Stoichiometry stoichiometry, CvTerm participantIdentificationMethod) {
        super(interactor, stoichiometry, participantIdentificationMethod);
    }

    public IntactParticipantEvidence(Interactor interactor, CvTerm bioRole, CvTerm expRole, CvTerm participantIdentificationMethod) {
        super(interactor, bioRole, expRole, participantIdentificationMethod);
    }

    public IntactParticipantEvidence(Interactor interactor, CvTerm bioRole, CvTerm expRole, Stoichiometry stoichiometry, CvTerm participantIdentificationMethod) {
        super(interactor, bioRole, expRole, stoichiometry, participantIdentificationMethod);
    }

    public IntactParticipantEvidence(Interactor interactor, CvTerm bioRole, CvTerm expRole, Organism expressedIn, CvTerm participantIdentificationMethod) {
        super(interactor, bioRole, expRole, expressedIn, participantIdentificationMethod);
    }

    public IntactParticipantEvidence(Interactor interactor, CvTerm bioRole, CvTerm expRole, Stoichiometry stoichiometry, Organism expressedIn, CvTerm participantIdentificationMethod) {
        super(interactor, bioRole, expRole, stoichiometry, expressedIn, participantIdentificationMethod);
    }

    public IntactParticipantEvidence(Interactor interactor) {
        super(interactor);
    }

    public IntactParticipantEvidence(Interactor interactor, Stoichiometry stoichiometry) {
        super(interactor, stoichiometry);
    }

    public void setInteractionAndAddParticipant(InteractionEvidence interaction) {

        if (this.interaction != null){
            this.interaction.removeParticipant(this);
        }

        if (interaction != null){
            interaction.addParticipant(this);
        }

        if (interaction instanceof IntactPrimaryObject){
            this.interactionAc = ((IntactPrimaryObject)interaction).getAc();
        }
    }

    @ManyToOne( targetEntity = IntactInteractionEvidence.class )
    @JoinColumn( name = "interaction_evidence_ac" )
    @Target(IntactInteractionEvidence.class)
    public InteractionEvidence getInteraction() {
        return this.interaction;
    }

    public void setInteraction(InteractionEvidence interaction) {
        this.interaction = interaction;
        if (interaction instanceof IntactPrimaryObject){
            this.interactionAc = ((IntactPrimaryObject)interaction).getAc();
        }
    }

    /**
     *
     * @return
     * @deprecated for intact backward compatibility only
     */
    @Column(name = "interaction_ac")
    private String getInteractionAc(){
        return this.interactionAc;
    }

    /**
     *
     * @param ac
     * @deprecated for intact backward compatibility only
     */
    private void setInteractionAc(String ac){
        this.interactionAc = ac;
    }
}
