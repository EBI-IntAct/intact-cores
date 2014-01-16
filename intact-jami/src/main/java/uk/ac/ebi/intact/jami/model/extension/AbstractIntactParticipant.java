package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.*;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * Abstract class for Intact participant
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>15/01/14</pre>
 */
@MappedSuperclass
public abstract class AbstractIntactParticipant<I extends Interaction, F extends Feature> extends AbstractIntactEntity<F> implements Participant<I,F> {

    private I interaction;

    protected AbstractIntactParticipant() {
        super();
    }

    public AbstractIntactParticipant(Interactor interactor){
        super(interactor);
    }

    public AbstractIntactParticipant(Interactor interactor, CvTerm bioRole){
        super(interactor, bioRole);
    }

    public AbstractIntactParticipant(Interactor interactor, Stoichiometry stoichiometry){
        super(interactor, stoichiometry);
    }

    public AbstractIntactParticipant(Interactor interactor, CvTerm bioRole, Stoichiometry stoichiometry){
        super(interactor, bioRole, stoichiometry);
    }

    public void setInteractionAndAddParticipant(I interaction) {

        if (this.interaction != null){
            this.interaction.removeParticipant(this);
        }

        if (interaction != null){
            interaction.addParticipant(this);
        }
    }

    @Transient
    public I getInteraction() {
        return this.interaction;
    }

    public void setInteraction(I interaction) {
        this.interaction = interaction;
    }

    @Override
    public String toString() {
        return getInteractor().toString() + " ( " + getInteractor().toString() + ")" + (getStoichiometry() != null ? ", stoichiometry: " + getStoichiometry().toString() : "");
    }
}
