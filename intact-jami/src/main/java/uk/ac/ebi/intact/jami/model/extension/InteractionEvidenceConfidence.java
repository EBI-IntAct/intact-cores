package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.InteractionEvidence;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Intact implementation of parent evidence confidence
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@Entity
@Table(name = "ia_interaction_evidence_confidence")
public class InteractionEvidenceConfidence extends AbstractIntactConfidence{

    private InteractionEvidence parent;

    public InteractionEvidenceConfidence() {
    }

    public InteractionEvidenceConfidence(CvTerm type, String value) {
        super(type, value);
    }

    @ManyToOne( targetEntity = IntactInteractionEvidence.class )
    @JoinColumn( name = "interaction_ac", referencedColumnName = "ac" )
    @Target(IntactInteractionEvidence.class)
    public InteractionEvidence getParent() {
        return parent;
    }

    public void setParent(InteractionEvidence interaction) {
        this.parent = interaction;
    }
}
