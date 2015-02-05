package uk.ac.ebi.intact.jami.synchronizer.listener;

import psidev.psi.mi.jami.enricher.listener.PublicationEnricherListener;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Experiment;
import psidev.psi.mi.jami.model.Publication;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.user.User;

/**
 * Intact extension of complex enricher listener
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public interface IntactPublicationEnricherListener extends PublicationEnricherListener{

    public void onAddedLifeCycleEvent(IntactPublication complex, LifeCycleEvent added);

    public void onRemovedLifeCycleEvent(IntactPublication complex, LifeCycleEvent removed);

    public void onStatusUpdate(IntactPublication complex, CvTerm oldStatus);

    public void onCurrentOwnerUpdate(IntactPublication complex, User oldUser);

    public void onCurrentReviewerUpdate(IntactPublication complex, User oldUser);

    public void onAddedExperiment(Publication complex, Experiment added);

    public void onRemovedExperiment(Publication complex, Experiment removed);

}
