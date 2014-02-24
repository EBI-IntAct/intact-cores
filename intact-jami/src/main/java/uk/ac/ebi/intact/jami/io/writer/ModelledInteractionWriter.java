package uk.ac.ebi.intact.jami.io.writer;

import psidev.psi.mi.jami.model.ModelledInteraction;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.service.ModelledInteractionService;

/**
 * Writer for modelled interactions
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/02/14</pre>
 */

public class ModelledInteractionWriter extends AbstractIntactWriter<ModelledInteraction>{
    @Override
    protected boolean isSpringContextInitialised() {
        return ApplicationContextProvider.getBean(ModelledInteractionService.class) != null;
    }

    @Override
    protected void initialiseDefaultIntactService() {
        setIntactService(ApplicationContextProvider.getBean(ModelledInteractionService.class));
    }
}
