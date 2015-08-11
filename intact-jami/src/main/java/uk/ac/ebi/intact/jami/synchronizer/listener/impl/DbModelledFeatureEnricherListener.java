package uk.ac.ebi.intact.jami.synchronizer.listener.impl;

import psidev.psi.mi.jami.enricher.listener.EnrichmentStatus;
import psidev.psi.mi.jami.model.ModelledFeature;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.synchronizer.*;

/**
 * Listener that will synchronize updates done to an existing object in the database
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/02/15</pre>
 */

public class DbModelledFeatureEnricherListener extends AbstractDbFeatureEnricherListener<ModelledFeature> {

    public DbModelledFeatureEnricherListener(SynchronizerContext context, IntactDbSynchronizer dbSynchronizer) {
        super(context, dbSynchronizer);
    }

    @Override
    protected void processOtherUpdates(ModelledFeature object, EnrichmentStatus status, String message) throws PersisterException, FinderException, SynchronizerException {
        // nothing to do
    }

    @Override
    protected void processOtherUpdates(ModelledFeature object, String message, Exception e) throws PersisterException, FinderException, SynchronizerException {
        // nothing to do
    }

    @Override
    protected XrefSynchronizer getXrefSynchronizer() {
        return getContext().getModelledFeatureXrefSynchronizer();
    }

    @Override
    protected AnnotationSynchronizer getAnnotationSynchronizer() {
        return getContext().getModelledFeatureAnnotationSynchronizer();
    }

    @Override
    protected AliasSynchronizer getAliasSynchronizer() {
        return getContext().getModelledFeatureAliasSynchronizer();
    }

    @Override
    protected IntactDbSynchronizer getRangeSynchronizer() {
        return getContext().getModelledRangeSynchronizer();
    }
}
