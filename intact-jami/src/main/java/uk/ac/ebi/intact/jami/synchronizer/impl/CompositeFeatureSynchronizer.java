package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.Feature;
import psidev.psi.mi.jami.model.FeatureEvidence;
import psidev.psi.mi.jami.model.ModelledFeature;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactFeature;
import uk.ac.ebi.intact.jami.model.extension.IntactFeatureEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledFeature;
import uk.ac.ebi.intact.jami.synchronizer.AbstractIntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.lang.reflect.InvocationTargetException;

/**
 * Synchronizer for features
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/01/14</pre>
 */

public class CompositeFeatureSynchronizer extends AbstractIntactDbSynchronizer<Feature, AbstractIntactFeature> {

    public CompositeFeatureSynchronizer(SynchronizerContext context){
        super(context, AbstractIntactFeature.class);
    }

    public AbstractIntactFeature find(Feature term) throws FinderException {
        if (term instanceof FeatureEvidence){
            return getContext().getFeatureEvidenceSynchronizer().find((FeatureEvidence)term);
        }
        else if (term instanceof ModelledFeature){
            return getContext().getModelledFeatureSynchronizer().find((ModelledFeature)term);
        }
        else {
            return null;
        }
    }

    public AbstractIntactFeature persist(AbstractIntactFeature term) throws FinderException, PersisterException, SynchronizerException {
        if (term instanceof IntactFeatureEvidence){
            return getContext().getFeatureEvidenceSynchronizer().persist((IntactFeatureEvidence)term);
        }
        else {
            return getContext().getModelledFeatureSynchronizer().find((IntactModelledFeature)term);
        }
    }

    @Override
    public AbstractIntactFeature synchronize(Feature term, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        if (term instanceof FeatureEvidence){
            return getContext().getFeatureEvidenceSynchronizer().synchronize((FeatureEvidence)term, persist);
        }
        else {
            return getContext().getModelledFeatureSynchronizer().synchronize((ModelledFeature)term, persist);
        }
    }

    public void synchronizeProperties(AbstractIntactFeature term) throws FinderException, PersisterException, SynchronizerException {
        if (term instanceof IntactFeatureEvidence){
            getContext().getFeatureEvidenceSynchronizer().synchronizeProperties((IntactFeatureEvidence)term);
        }
        else {
            getContext().getModelledFeatureSynchronizer().synchronizeProperties((IntactModelledFeature)term);
        }
    }

    public void clearCache() {
        // nothing to do
    }

    @Override
    protected Object extractIdentifier(AbstractIntactFeature object) {
        return object.getAc();
    }

    @Override
    protected AbstractIntactFeature instantiateNewPersistentInstance(Feature object, Class<? extends AbstractIntactFeature> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        throw new UnsupportedOperationException("This synchronizer relies on delegate synchronizers and cannot be used this way");
    }

    @Override
    protected void storeInCache(Feature originalObject, AbstractIntactFeature persistentObject, AbstractIntactFeature existingInstance) {
        // nothing to do
    }
}
