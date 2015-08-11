package uk.ac.ebi.intact.jami.synchronizer.listener;

import psidev.psi.mi.jami.enricher.listener.ExperimentEnricherListener;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Experiment;
import psidev.psi.mi.jami.model.InteractionEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactExperiment;

/**
 * Intact extension of experiment enricher listener
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public interface IntactExperimentEnricherListener extends ExperimentEnricherListener {

    void onShortLabelUpdate(IntactExperiment experiment, String oldLabel);

    void onParticipantDetectionMethodUpdate(IntactExperiment experiment, CvTerm oldTerm);

    void onAddedInteractionEvidence(Experiment experiment, InteractionEvidence added);

    void onRemovedInteractionEvidence(Experiment experiment, InteractionEvidence removed);
}
