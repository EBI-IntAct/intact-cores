package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.Allostery;
import psidev.psi.mi.jami.model.ModelledFeature;
import uk.ac.ebi.intact.jami.model.extension.*;

/**
 * Db synchronizers for allostery
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface AllosteryDbSynchronizer extends CooperativeEffectDbSynchronizer<Allostery, IntactAllostery> {

    public IntActEntitySynchronizer getParticipantSynchronizer();

    public AllosteryDbSynchronizer setParticipantSynchronizer(IntActEntitySynchronizer participantSynchronizer);

    public FeatureDbSynchronizer<ModelledFeature, IntactModelledFeature> getFeatureSynchronizer();

    public AllosteryDbSynchronizer setFeatureSynchronizer(FeatureDbSynchronizer<ModelledFeature, IntactModelledFeature> featureSynchronizer);
}
