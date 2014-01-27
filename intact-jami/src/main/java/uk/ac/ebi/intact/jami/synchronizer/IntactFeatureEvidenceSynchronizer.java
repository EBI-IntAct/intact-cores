package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Default finder/synchronizer for features
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class IntactFeatureEvidenceSynchronizer extends IntactFeatureSynchronizer<FeatureEvidence>{
    private IntactDbSynchronizer<CvTerm> methodSynchronizer;
    private static final Log log = LogFactory.getLog(IntactFeatureEvidenceSynchronizer.class);

    public IntactFeatureEvidenceSynchronizer(EntityManager entityManager){
        super(entityManager, IntactFeatureEvidence.class);
        this.methodSynchronizer = new IntactCvTermSynchronizer(getEntityManager(), IntactUtils.FEATURE_METHOD_OBJCLASS);
    }

    public IntactFeatureEvidenceSynchronizer(EntityManager entityManager, IntactDbSynchronizer<Alias> aliasSynchronizer,
                                             IntactDbSynchronizer<Annotation> annotationSynchronizer, IntactDbSynchronizer<Xref> xrefSynchronizer,
                                             IntactDbSynchronizer<CvTerm> typeSynchronizer, IntactDbSynchronizer<CvTerm> effectSynchronizer,
                                             IntactDbSynchronizer<Range> rangeSynchronizer, IntactDbSynchronizer<CvTerm> methodSynchronizer){
        super(entityManager, IntactFeatureEvidence.class, aliasSynchronizer, annotationSynchronizer,xrefSynchronizer, typeSynchronizer, effectSynchronizer, rangeSynchronizer);
        this.methodSynchronizer = methodSynchronizer != null ? methodSynchronizer : new IntactCvTermSynchronizer(getEntityManager(), IntactUtils.FEATURE_METHOD_OBJCLASS);
    }

    public FeatureEvidence find(FeatureEvidence feature) throws FinderException {
        return null;
    }

    public void clearCache() {
        super.clearCache();
        this.methodSynchronizer.clearCache();
    }

    @Override
    protected void synchronizeProperties(AbstractIntactFeature intactFeature) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizeProperties(intactFeature);

        IntactFeatureEvidence fEvidence = (IntactFeatureEvidence)intactFeature;
        if (((IntactFeatureEvidence) intactFeature).areDetectionMethodsInitialized()){
            List<CvTerm> methodsToPersist = new ArrayList<CvTerm>(((IntactFeatureEvidence) intactFeature).getDetectionMethods());
            for (CvTerm method : methodsToPersist){
                CvTerm featureTerm = this.methodSynchronizer.synchronize(method, true, true);
                // we have a different instance because needed to be synchronized
                if (featureTerm != method){
                    fEvidence.getDetectionMethods().remove(method);
                    fEvidence.getDetectionMethods().add(featureTerm);
                }
            }
        }
    }
}
