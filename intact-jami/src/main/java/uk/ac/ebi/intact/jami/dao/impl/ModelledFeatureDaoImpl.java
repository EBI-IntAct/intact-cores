package uk.ac.ebi.intact.jami.dao.impl;

import psidev.psi.mi.jami.model.ModelledFeature;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledFeature;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;

import javax.persistence.EntityManager;

/**
 * Implementation for feature dao of modelled features
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class ModelledFeatureDaoImpl extends FeatureDaoImpl<ModelledFeature, IntactModelledFeature> {

    public ModelledFeatureDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super(IntactModelledFeature.class, entityManager, context);
    }

    @Override
    public IntactDbSynchronizer<ModelledFeature, IntactModelledFeature> getDbSynchronizer() {
        return getSynchronizerContext().getModelledFeatureSynchronizer();
    }
}
