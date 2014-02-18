package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.Feature;
import psidev.psi.mi.jami.model.FeatureEvidence;
import psidev.psi.mi.jami.model.ModelledFeature;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactFeature;
import uk.ac.ebi.intact.jami.model.extension.IntactFeatureEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledFeature;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;

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
    }

    public AbstractIntactFeature find(Feature term) throws FinderException{
        if (term instanceof FeatureEvidence){
            return getFeatureEvidenceSynchronizer().find((FeatureEvidence)term);
        }
        else if (term instanceof ModelledFeature){
            return getModelledFeatureSynchronizer().find((ModelledFeature)term);
        }
        else {
            return null;
        }
    }

    public AbstractIntactFeature persist(AbstractIntactFeature term) throws FinderException, PersisterException, SynchronizerException{
        if (term instanceof IntactFeatureEvidence){
            return getFeatureEvidenceSynchronizer().persist((IntactFeatureEvidence)term);
        }
        else {
            return getModelledFeatureSynchronizer().find((IntactModelledFeature)term);
        }
    }

    @Override
    public AbstractIntactFeature synchronize(Feature term, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        if (term instanceof FeatureEvidence){
            return getFeatureEvidenceSynchronizer().synchronize((FeatureEvidence)term, persist);
        }
        else {
            return getModelledFeatureSynchronizer().synchronize((ModelledFeature)term, persist);
        }
    }

    public void synchronizeProperties(AbstractIntactFeature term) throws FinderException, PersisterException, SynchronizerException {
        if (term instanceof IntactFeatureEvidence){
            getFeatureEvidenceSynchronizer().synchronizeProperties((IntactFeatureEvidence)term);
        }
        else {
            getModelledFeatureSynchronizer().synchronizeProperties((IntactModelledFeature)term);
        }
    }

    public void clearCache() {
        getFeatureEvidenceSynchronizer().clearCache();
        getModelledFeatureSynchronizer().clearCache();
    }

    public IntactDbSynchronizer<ModelledFeature, IntactModelledFeature> getModelledFeatureSynchronizer() {
        if (this.modelledFeatureSynchronizer == null){
            this.modelledFeatureSynchronizer = new IntactFeatureBaseSynchronizer<ModelledFeature, IntactModelledFeature>(getEntityManager(), IntactModelledFeature.class);
        }
        return modelledFeatureSynchronizer;
    }

    public IntactDbSynchronizer<FeatureEvidence, IntactFeatureEvidence> getFeatureEvidenceSynchronizer() {
        if (this.featureEvidenceSynchronizer == null){
            this.featureEvidenceSynchronizer = new IntactFeatureEvidenceSynchronizer(getEntityManager());
        }
        return featureEvidenceSynchronizer;
    }

    public void setModelledFeatureSynchronizer(IntactDbSynchronizer<ModelledFeature, IntactModelledFeature> modelledFeatureSynchronizer) {
        this.modelledFeatureSynchronizer = modelledFeatureSynchronizer;
    }

    public void setFeatureEvidenceSynchronizer(IntactDbSynchronizer<FeatureEvidence, IntactFeatureEvidence> featureEvidenceSynchronizer) {
        this.featureEvidenceSynchronizer = featureEvidenceSynchronizer;
    }

    @Override
    protected Object extractIdentifier(AbstractIntactFeature object) {
        return object.getAc();
    }

    @Override
    protected AbstractIntactFeature instantiateNewPersistentInstance(Feature object, Class<? extends AbstractIntactFeature> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        throw new UnsupportedOperationException("This synchronizer relies on delegate synchronizers and cannot be used this way");
    }
}
