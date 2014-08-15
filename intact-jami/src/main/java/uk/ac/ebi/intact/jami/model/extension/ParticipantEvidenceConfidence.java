package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Intact implementation of parent confidence
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@Entity
@Table(name = "ia_component_confidence")
public class ParticipantEvidenceConfidence extends AbstractIntactConfidence{

    protected ParticipantEvidenceConfidence() {
    }

    public ParticipantEvidenceConfidence(CvTerm type, String value) {
        super(type, value);
    }
}
