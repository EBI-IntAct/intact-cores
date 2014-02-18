package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.CooperativeEffect;
import psidev.psi.mi.jami.model.ModelledFeature;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactCooperativeEffect;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledFeature;

/**
 * Db synchronizers for allostery
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */

public interface CooperativeEffectDbSynchronizer<C extends CooperativeEffect, I extends AbstractIntactCooperativeEffect> extends IntactDbSynchronizer<C, I> {

    public IntActEntitySynchronizer getParticipantSynchronizer();

    public CooperativeEffectDbSynchronizer setParticipantSynchronizer(IntActEntitySynchronizer participantSynchronizer);

    public FeatureDbSynchronizer<ModelledFeature, IntactModelledFeature> getFeatureSynchronizer();

    public CooperativeEffectDbSynchronizer setFeatureSynchronizer(FeatureDbSynchronizer<ModelledFeature, IntactModelledFeature> featureSynchronizer);
}
