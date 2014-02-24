package uk.ac.ebi.intact.jami.io.reader;

import psidev.psi.mi.jami.datasource.ModelledInteractionStream;
import psidev.psi.mi.jami.model.ModelledInteraction;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.service.ModelledInteractionService;

/**
 * Intact stream for interaction evidences
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/02/14</pre>
 */

public class IntactModelledStream extends AbstractIntactStream<ModelledInteraction> implements ModelledInteractionStream<ModelledInteraction>{

    public IntactModelledStream() {
        super();
    }

    public IntactModelledStream(String springFile, String intactServiceName) {
        super(springFile, intactServiceName);
    }

    @Override
    protected boolean isSpringContextInitialised() {
        return ApplicationContextProvider.getBean(ModelledInteractionService.class) != null;
    }

    @Override
    protected void initialiseDefaultIntactService() {
        setIntactService(ApplicationContextProvider.getBean(ModelledInteractionService.class));
    }
}
