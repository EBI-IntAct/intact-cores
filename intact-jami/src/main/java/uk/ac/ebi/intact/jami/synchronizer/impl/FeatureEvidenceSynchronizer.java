package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.FeatureEvidence;
import psidev.psi.mi.jami.utils.clone.FeatureCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.FeatureEvidenceMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.IntactFeatureEvidence;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

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

public class FeatureEvidenceSynchronizer extends FeatureSynchronizerTemplate<FeatureEvidence, IntactFeatureEvidence> {

    public FeatureEvidenceSynchronizer(SynchronizerContext context){
        super(context, IntactFeatureEvidence.class);
    }

    public void clearCache() {
        super.clearCache();
    }

    @Override
    public void synchronizeProperties(IntactFeatureEvidence intactFeature) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizeProperties(intactFeature);
        if (intactFeature.areDetectionMethodsInitialized()){
            List<CvTerm> methodsToPersist = new ArrayList<CvTerm>(intactFeature.getDetectionMethods());
            for (CvTerm method : methodsToPersist){
                CvTerm featureTerm = getContext().getFeatureDetectionMethodSynchronizer().synchronize(method, true);
                // we have a different instance because needed to be synchronized
                if (featureTerm != method){
                    intactFeature.getDetectionMethods().remove(method);
                    intactFeature.getDetectionMethods().add(featureTerm);
                }
            }
        }
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
