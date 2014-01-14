package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.InteractionEvidence;
import psidev.psi.mi.jami.model.ParticipantEvidence;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Intact implementation of interaction evidence confidence
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@Entity
@Table(name = "ia_interaction_evidence_confidence")
public class InteractionEvidenceConfidence extends AbstractIntactConfidence{

    private InteractionEvidence interaction;

    public InteractionEvidenceConfidence() {
    }

    public InteractionEvidenceConfidence(CvTerm type, String value) {
        super(type, value);
    }

    @ManyToOne( targetEntity = IntactInteractionEvidence.class )
    @JoinColumn( name = "interaction_ac" )
    @Target(IntactInteractionEvidence.class)
    public InteractionEvidence getInteraction() {
        return interaction;
    }

    public void setInteraction(InteractionEvidence interaction) {
        this.interaction = interaction;
    }
}
