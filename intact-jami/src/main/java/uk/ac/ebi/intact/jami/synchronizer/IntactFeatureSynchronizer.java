package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.bridges.exception.BridgeFailedException;
import psidev.psi.mi.jami.bridges.fetcher.InteractorFetcher;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.CooperativeEffectCloner;
import psidev.psi.mi.jami.utils.clone.FeatureCloner;
import psidev.psi.mi.jami.utils.clone.InteractorCloner;
import uk.ac.ebi.intact.jami.model.extension.*;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * Synchronizer for features
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/01/14</pre>
 */

public class IntactFeatureSynchronizer extends AbstractIntactDbSynchronizer<Feature, AbstractIntactFeature> {

    private IntactDbSynchronizer<ModelledFeature, IntactModelledFeature> modelledFeatureSynchronizer;
    private IntactDbSynchronizer<FeatureEvidence, IntactFeatureEvidence> featureEvidenceSynchronizer;

    public IntactFeatureSynchronizer(EntityManager entityManager){
        super(entityManager, AbstractIntactFeature.class);
        this.modelledFeatureSynchronizer = new IntactFeatureBaseSynchronizer<ModelledFeature, IntactModelledFeature>(entityManager, IntactModelledFeature.class);
        this.featureEvidenceSynchronizer = new IntactFeatureEvidenceSynchronizer(entityManager);

    }

    public IntactFeatureSynchronizer(EntityManager entityManager, IntactDbSynchronizer<ModelledFeature, IntactModelledFeature> modelledFeatureSynchronizer,
                                     IntactDbSynchronizer<FeatureEvidence, IntactFeatureEvidence> featureEvidenceSynchronizer){
        super(entityManager, AbstractIntactFeature.class);
        this.modelledFeatureSynchronizer = modelledFeatureSynchronizer != null ? modelledFeatureSynchronizer : new IntactFeatureBaseSynchronizer<ModelledFeature, IntactModelledFeature>(entityManager, IntactModelledFeature.class);
        this.featureEvidenceSynchronizer = featureEvidenceSynchronizer != null ? featureEvidenceSynchronizer : new IntactFeatureEvidenceSynchronizer(entityManager);
    }

    public AbstractIntactFeature find(Feature term) throws FinderException{
        if (term instanceof FeatureEvidence){
            return this.featureEvidenceSynchronizer.find((FeatureEvidence)term);
        }
        else if (term instanceof ModelledFeature){
            return this.modelledFeatureSynchronizer.find((ModelledFeature)term);
        }
        else {
            return null;
        }
    }

    public AbstractIntactFeature persist(AbstractIntactFeature term) throws FinderException, PersisterException, SynchronizerException{
        if (term instanceof IntactFeatureEvidence){
            return this.featureEvidenceSynchronizer.persist((IntactFeatureEvidence)term);
        }
        else {
            return this.modelledFeatureSynchronizer.find((IntactModelledFeature)term);
        }
    }

    @Override
    public AbstractIntactFeature synchronize(Feature term, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        if (term instanceof FeatureEvidence){
            return this.featureEvidenceSynchronizer.synchronize((FeatureEvidence)term, persist);
        }
        else if (term instanceof ModelledFeature){
            return this.modelledFeatureSynchronizer.synchronize((ModelledFeature)term, persist);
        }
        else {
            try {
                IntactFeatureEvidence featureEvidence = (IntactFeatureEvidence)instantiateNewPersistentInstance(term, getIntactClass());
                return this.featureEvidenceSynchronizer.synchronize(featureEvidence, persist);
            } catch (InstantiationException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+IntactFeatureEvidence.class, e);
            } catch (IllegalAccessException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+IntactFeatureEvidence.class, e);
            } catch (InvocationTargetException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+IntactFeatureEvidence.class, e);
            } catch (NoSuchMethodException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+IntactFeatureEvidence.class, e);
            }
        }
    }

    public void synchronizeProperties(AbstractIntactFeature term) throws FinderException, PersisterException, SynchronizerException {
        if (term instanceof IntactFeatureEvidence){
            this.featureEvidenceSynchronizer.synchronizeProperties((IntactFeatureEvidence)term);
        }
        else {
            this.modelledFeatureSynchronizer.synchronizeProperties((IntactModelledFeature)term);
        }
    }

    public void clearCache() {
        this.featureEvidenceSynchronizer.clearCache();
        this.modelledFeatureSynchronizer.clearCache();
    }

    public IntactDbSynchronizer<ModelledFeature, IntactModelledFeature> getModelledFeatureSynchronizer() {
        return modelledFeatureSynchronizer;
    }

    public IntactDbSynchronizer<FeatureEvidence, IntactFeatureEvidence> getFeatureEvidenceSynchronizer() {
        return featureEvidenceSynchronizer;
    }

    @Override
    protected Object extractIdentifier(AbstractIntactFeature object) {
        return object.getAc();
    }

    @Override
    protected AbstractIntactFeature instantiateNewPersistentInstance(Feature object, Class<? extends AbstractIntactFeature> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactFeatureEvidence newFeature = new IntactFeatureEvidence();
        FeatureCloner.copyAndOverrideBasicFeaturesProperties(object, newFeature);
        return newFeature;
    }
}
