package uk.ac.ebi.intact.jami.synchronizer.listener.impl;

import psidev.psi.mi.jami.enricher.listener.EnrichmentStatus;
import psidev.psi.mi.jami.model.Feature;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.synchronizer.*;

/**
 * Listener that will synchronize updates done to an existing object in the database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public class DbFeatureEnricherListener<F extends Feature> extends AbstractDbFeatureEnricherListener<F> {

    public DbFeatureEnricherListener(SynchronizerContext context, IntactDbSynchronizer dbSynchronizer) {
        super(context, dbSynchronizer);
    }

    @Override
    protected void processOtherUpdates(F object, EnrichmentStatus status, String message) throws PersisterException, FinderException, SynchronizerException {
        // nothing to do
    }

    @Override
    protected void processOtherUpdates(F object, String message, Exception e) throws PersisterException, FinderException, SynchronizerException {
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
    protected IntactDbSynchronizer getRangeSynchronizer() {
        return null;
    }
}
