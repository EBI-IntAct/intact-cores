package uk.ac.ebi.intact.jami.io.reader;

import psidev.psi.mi.jami.binary.BinaryInteractionEvidence;
import psidev.psi.mi.jami.binary.expansion.InteractionEvidenceSpokeExpansion;
import psidev.psi.mi.jami.datasource.BinaryInteractionEvidenceStream;
import psidev.psi.mi.jami.model.InteractionEvidence;

/**
 * Intact stream for binary interaction evidences
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/02/14</pre>
 */

public class IntactBinaryEvidenceStream extends AbstractIntactBinaryStream<InteractionEvidence, BinaryInteractionEvidence>
implements BinaryInteractionEvidenceStream{

    public IntactBinaryEvidenceStream() {
        super(new IntactEvidenceStream());
    }

    public IntactBinaryEvidenceStream(String springFile, String intactServiceName) {
        super(new IntactEvidenceStream(springFile, intactServiceName));
    }

    protected IntactBinaryEvidenceStream(AbstractIntactStream<InteractionEvidence> delegate){
        super(delegate);
    }

    @Override
    protected void initialiseDefaultExpansionMethod() {
        super.setExpansionMethod(new InteractionEvidenceSpokeExpansion());
    }
}
