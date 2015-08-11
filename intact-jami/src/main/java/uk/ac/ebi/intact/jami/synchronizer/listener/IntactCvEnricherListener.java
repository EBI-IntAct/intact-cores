package uk.ac.ebi.intact.jami.synchronizer.listener;

import psidev.psi.mi.jami.enricher.listener.CvTermEnricherListener;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.OntologyTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;

/**
 * Intact extension of cv enricher listener
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public interface IntactCvEnricherListener extends CvTermEnricherListener<CvTerm> {

    public void onAddedParent(IntactCvTerm complex, OntologyTerm added);

    public void onRemovedParent(IntactCvTerm complex, OntologyTerm removed);

    public void onUpdatedDefinition(IntactCvTerm cv, String def);

}
