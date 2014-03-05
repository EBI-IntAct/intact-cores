package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
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

    public InteractionEvidenceConfidence() {
    }

    public InteractionEvidenceConfidence(CvTerm type, String value) {
        super(type, value);
    }
}
