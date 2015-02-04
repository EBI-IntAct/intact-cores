package uk.ac.ebi.intact.jami.synchronizer.listener;

import psidev.psi.mi.jami.enricher.listener.ComplexEnricherListener;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.user.User;

/**
 * Intact extension of complex enricher listener
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public interface IntactComplexEnricherListener extends ComplexEnricherListener {

    public void onAddedLifeCycleEvent(IntactComplex complex, LifeCycleEvent added);

    public void onRemovedLifeCycleEvent(IntactComplex complex, LifeCycleEvent removed);

    public void onStatusUpdate(IntactComplex complex, CvTerm oldStatus);

    public void onCurrentOwnerUpdate(IntactComplex complex, User oldUser);

    public void onCurrentReviewerUpdate(IntactComplex complex, User oldUser);

}
