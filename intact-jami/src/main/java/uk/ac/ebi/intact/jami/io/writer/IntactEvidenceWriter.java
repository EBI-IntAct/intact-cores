package uk.ac.ebi.intact.jami.io.writer;

import psidev.psi.mi.jami.model.InteractionEvidence;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.service.InteractionEvidenceService;

/**
 * Writer for interaction evidences
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/02/14</pre>
 */

public class IntactEvidenceWriter extends AbstractIntactWriter<InteractionEvidence>{
    @Override
    protected boolean isSpringContextInitialised() {
        return ApplicationContextProvider.getBean(InteractionEvidenceService.class) != null;
    }

    @Override
    protected void initialiseDefaultIntactService() {
        setIntactService((InteractionEvidenceService)ApplicationContextProvider.getBean("interactionEvidenceService"));
    }
}
