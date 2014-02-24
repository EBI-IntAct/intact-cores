package uk.ac.ebi.intact.jami.io.reader;

import psidev.psi.mi.jami.datasource.InteractionEvidenceStream;
import psidev.psi.mi.jami.model.InteractionEvidence;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.service.InteractionEvidenceService;

/**
 * Intact stream for interaction evidences
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/02/14</pre>
 */

public class IntactEvidenceStream extends AbstractIntactStream<InteractionEvidence> implements InteractionEvidenceStream<InteractionEvidence>{

    public IntactEvidenceStream() {
        super();
    }

    public IntactEvidenceStream(String springFile, String intactServiceName) {
        super(springFile, intactServiceName);
    }

    @Override
    protected boolean isSpringContextInitialised() {
        return ApplicationContextProvider.getBean(InteractionEvidenceService.class) != null;
    }

    @Override
    protected void initialiseDefaultIntactService() {
        setIntactService(ApplicationContextProvider.getBean(InteractionEvidenceService.class));
    }
}
