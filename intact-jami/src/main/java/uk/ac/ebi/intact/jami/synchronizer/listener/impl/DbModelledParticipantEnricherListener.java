package uk.ac.ebi.intact.jami.synchronizer.listener.impl;

import psidev.psi.mi.jami.enricher.listener.EnrichmentStatus;
import psidev.psi.mi.jami.model.ModelledFeature;
import psidev.psi.mi.jami.model.ModelledParticipant;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.synchronizer.*;

/**
 * Listener that will synchronize updates done to an existing object in the database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public class DbModelledParticipantEnricherListener extends AbstractDbParticipantEnricherListener<ModelledParticipant, ModelledFeature> {

    public DbModelledParticipantEnricherListener(SynchronizerContext context, ParticipantSynchronizer dbSynchronizer) {
        super(context, dbSynchronizer);
    }

    @Override
    protected void processOtherUpdates(ModelledParticipant object, EnrichmentStatus status, String message) throws PersisterException, FinderException, SynchronizerException {
        // nothing to do
    }

    @Override
    protected void processOtherUpdates(ModelledParticipant object, String message, Exception e) throws PersisterException, FinderException, SynchronizerException {
        // nothing to do
    }

    @Override
    protected XrefSynchronizer getXrefSynchronizer() {
        return getContext().getModelledParticipantXrefSynchronizer();
    }

    @Override
    protected AnnotationSynchronizer getAnnotationSynchronizer() {
        return getContext().getModelledParticipantAnnotationSynchronizer();
    }

    @Override
    protected AliasSynchronizer getAliasSynchronizer() {
        return getContext().getModelledParticipantAliasSynchronizer();
    }

    @Override
    protected IntactDbSynchronizer getCausalRelationshipSynchronizer() {
        return getContext().getModelledCausalRelationshipSynchronizer();
    }

    @Override
    protected IntactDbSynchronizer getFeatureSynchronizer() {
        return getContext().getModelledFeatureSynchronizer();
    }
}
