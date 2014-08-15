package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.CvTerm;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Intact implementation of parent evidence confidence
 *
 * Future improvements: this table will be ia_interaction_evidence_confidence once we deprecates intact-core and split complexes
 * and interaction evidences in two separate tables
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14/01/14</pre>
 */
@Entity
@Table(name = "ia_confidence")
public class InteractionEvidenceConfidence extends AbstractIntactConfidence{

    protected InteractionEvidenceConfidence() {
    }

    public InteractionEvidenceConfidence(CvTerm type, String value) {
        super(type, value);
    }
}
