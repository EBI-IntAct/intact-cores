package uk.ac.ebi.intact.jami.synchronizer.listener.impl;

import psidev.psi.mi.jami.enricher.listener.EnrichmentStatus;
import psidev.psi.mi.jami.model.Feature;
import psidev.psi.mi.jami.model.Participant;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.synchronizer.*;

/**
 * Listener that will synchronize updates done to an existing object in the database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public class DbParticipantEnricherListener<P extends Participant, F extends Feature> extends AbstractDbParticipantEnricherListener<P, F> {

    public DbParticipantEnricherListener(SynchronizerContext context, IntactDbSynchronizer dbSynchronizer) {
        super(context, dbSynchronizer);
    }

    @Override
    protected void processOtherUpdates(P object, EnrichmentStatus status, String message) throws PersisterException, FinderException, SynchronizerException {
        // nothing to do
    }

    @Override
    protected void processOtherUpdates(P object, String message, Exception e) throws PersisterException, FinderException, SynchronizerException {
        // nothing to do
    }

    @Override
    protected XrefSynchronizer getXrefSynchronizer() {
        return null;
    }

    @Override
    protected AnnotationSynchronizer getAnnotationSynchronizer() {
        return null;
    }

    @Override
    protected AliasSynchronizer getAliasSynchronizer() {
        return null;
    }

    @Override
    protected IntactDbSynchronizer getCausalRelationshipSynchronizer() {
        return null;
    }

    @Override
    protected IntactDbSynchronizer getFeatureSynchronizer() {
        return getContext().getFeatureSynchronizer();
    }
}
