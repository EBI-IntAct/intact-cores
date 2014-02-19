package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.FeatureEvidence;
import psidev.psi.mi.jami.utils.clone.FeatureCloner;
import uk.ac.ebi.intact.jami.merger.FeatureEvidenceMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactFeatureEvidence;
import uk.ac.ebi.intact.jami.synchronizer.impl.CvTermSynchronizer;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Default finder/synchronizer for features
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class IntactFeatureEvidenceSynchronizer extends IntactFeatureBaseSynchronizer<FeatureEvidence, IntactFeatureEvidence> {
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> methodSynchronizer;

    public IntactFeatureEvidenceSynchronizer(EntityManager entityManager){
        super(entityManager, IntactFeatureEvidence.class);
    }

    public void clearCache() {
        super.clearCache();
        getMethodSynchronizer().clearCache();
    }

    @Override
    public void synchronizeProperties(IntactFeatureEvidence intactFeature) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizeProperties(intactFeature);
        if (intactFeature.areDetectionMethodsInitialized()){
            List<CvTerm> methodsToPersist = new ArrayList<CvTerm>(intactFeature.getDetectionMethods());
            for (CvTerm method : methodsToPersist){
                CvTerm featureTerm = getMethodSynchronizer().synchronize(method, true);
                // we have a different instance because needed to be synchronized
                if (featureTerm != method){
                    intactFeature.getDetectionMethods().remove(method);
                    intactFeature.getDetectionMethods().add(featureTerm);
                }
            }
        }
    }

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getMethodSynchronizer() {
        if (this.methodSynchronizer == null){
            this.methodSynchronizer = new CvTermSynchronizer(getEntityManager(), IntactUtils.FEATURE_METHOD_OBJCLASS);
        }
        return methodSynchronizer;
    }

    public void setMethodSynchronizer(IntactDbSynchronizer<CvTerm, IntactCvTerm> methodSynchronizer) {
        this.methodSynchronizer = methodSynchronizer;
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new FeatureEvidenceMergerEnrichOnly());
    }

    @Override
    protected IntactFeatureEvidence instantiateNewPersistentInstance(FeatureEvidence object, Class<? extends IntactFeatureEvidence> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactFeatureEvidence newFeature = new IntactFeatureEvidence();
        FeatureCloner.copyAndOverrideFeatureEvidenceProperties(object, newFeature);
        newFeature.setParticipant(object.getParticipant());
        return newFeature;
    }
}
