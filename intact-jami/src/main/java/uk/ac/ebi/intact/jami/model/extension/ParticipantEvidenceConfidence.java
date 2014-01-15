package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.ParticipantEvidence;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Intact implementation of participant confidence
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@Entity
@Table(name = "ia_component_confidence")
public class ParticipantEvidenceConfidence extends AbstractIntactConfidence{

    private ParticipantEvidence participant;

    public ParticipantEvidenceConfidence() {
    }

    public ParticipantEvidenceConfidence(CvTerm type, String value) {
        super(type, value);
    }

    @ManyToOne( targetEntity = IntactParticipantEvidence.class )
    @JoinColumn( name = "component_ac", referencedColumnName = "ac" )
    @Target(IntactParticipantEvidence.class)
    public ParticipantEvidence getParticipant() {
        return participant;
    }

    public void setParticipant(ParticipantEvidence participant) {
        this.participant = participant;
    }
}
