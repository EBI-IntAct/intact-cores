package uk.ac.ebi.intact.jami.io.reader;

import psidev.psi.mi.jami.binary.BinaryInteractionEvidence;
import psidev.psi.mi.jami.datasource.BinaryInteractionEvidenceSource;
import psidev.psi.mi.jami.datasource.InteractionEvidenceSource;
import psidev.psi.mi.jami.exception.MIIOException;
import psidev.psi.mi.jami.model.InteractionEvidence;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Intact source for interaction evidences
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/02/14</pre>
 */

public class IntactBinaryEvidenceSource extends IntactBinaryEvidenceStream implements BinaryInteractionEvidenceSource{

    Collection<BinaryInteractionEvidence> loadedInteractions;

    public IntactBinaryEvidenceSource() {
        super(new IntactEvidenceSource());
    }

    public IntactBinaryEvidenceSource(String springFile, String intactServiceName) {
        super(new IntactEvidenceSource(springFile, intactServiceName));
    }

    public Collection<BinaryInteractionEvidence> getInteractions() throws MIIOException {
        if (loadedInteractions == null){
            Collection<InteractionEvidence> interactions = ((InteractionEvidenceSource)getDelegate()).getInteractions();
            loadedInteractions = new ArrayList<BinaryInteractionEvidence>(interactions.size());
            for (InteractionEvidence inter : interactions){
                if (getExpansionMethod().isInteractionExpandable(inter)){
                    loadedInteractions.addAll(getExpansionMethod().expand(inter));
                }
            }
        }

        return this.loadedInteractions;
    }

    public long getNumberOfInteractions() {
        return getInteractions().size();
    }
}
