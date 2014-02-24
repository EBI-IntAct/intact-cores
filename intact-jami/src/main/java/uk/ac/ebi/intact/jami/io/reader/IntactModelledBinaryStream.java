package uk.ac.ebi.intact.jami.io.reader;

import psidev.psi.mi.jami.binary.ModelledBinaryInteraction;
import psidev.psi.mi.jami.binary.expansion.ModelledInteractionSpokeExpansion;
import psidev.psi.mi.jami.datasource.ModelledBinaryInteractionStream;
import psidev.psi.mi.jami.model.ModelledInteraction;

/**
 * Intact stream for modelledBinary interactions
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/02/14</pre>
 */

public class IntactModelledBinaryStream extends AbstractIntactBinaryStream<ModelledInteraction, ModelledBinaryInteraction>
        implements ModelledBinaryInteractionStream {

    public IntactModelledBinaryStream() {
        super(new IntactModelledStream());
    }

    public IntactModelledBinaryStream(String springFile, String intactServiceName) {
        super(new IntactModelledStream(springFile, intactServiceName));
    }

    protected IntactModelledBinaryStream(AbstractIntactStream<ModelledInteraction> delegate){
        super(delegate);
    }

    @Override
    protected void initialiseDefaultExpansionMethod() {
        super.setExpansionMethod(new ModelledInteractionSpokeExpansion());
    }
}
