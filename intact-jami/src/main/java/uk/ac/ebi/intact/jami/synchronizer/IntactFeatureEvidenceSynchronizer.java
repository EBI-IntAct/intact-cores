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

public class IntactFeatureEvidenceSynchronizer extends IntactFeatureSynchronizer<FeatureEvidence, IntactFeatureEvidence>{
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> methodSynchronizer;
    private static final Log log = LogFactory.getLog(IntactFeatureEvidenceSynchronizer.class);

    public IntactFeatureEvidenceSynchronizer(EntityManager entityManager){
        super(entityManager, IntactFeatureEvidence.class);
        this.methodSynchronizer = new IntactCvTermSynchronizer(getEntityManager(), IntactUtils.FEATURE_METHOD_OBJCLASS);
    }

    public IntactFeatureEvidenceSynchronizer(EntityManager entityManager, IntactDbSynchronizer<Alias, FeatureAlias> aliasSynchronizer,
                                             IntactDbSynchronizer<Annotation, FeatureAnnotation> annotationSynchronizer, IntactDbSynchronizer<Xref, FeatureXref> xrefSynchronizer,
                                             IntactDbSynchronizer<CvTerm, IntactCvTerm> typeSynchronizer, IntactDbSynchronizer<CvTerm, IntactCvTerm> effectSynchronizer,
                                             IntactDbSynchronizer<Range, IntactRange> rangeSynchronizer, IntactDbSynchronizer<CvTerm, IntactCvTerm> methodSynchronizer){
        super(entityManager, IntactFeatureEvidence.class, aliasSynchronizer, annotationSynchronizer,xrefSynchronizer, typeSynchronizer, effectSynchronizer, rangeSynchronizer);
        this.methodSynchronizer = methodSynchronizer != null ? methodSynchronizer : new IntactCvTermSynchronizer(getEntityManager(), IntactUtils.FEATURE_METHOD_OBJCLASS);
    }

    public IntactFeatureEvidence find(FeatureEvidence feature) throws FinderException {
        return null;
    }

    public void clearCache() {
        super.clearCache();
        this.methodSynchronizer.clearCache();
    }

    @Override
    public void synchronizeProperties(IntactFeatureEvidence intactFeature) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizeProperties(intactFeature);
        if (intactFeature.areDetectionMethodsInitialized()){
            List<CvTerm> methodsToPersist = new ArrayList<CvTerm>(intactFeature.getDetectionMethods());
            for (CvTerm method : methodsToPersist){
                CvTerm featureTerm = this.methodSynchronizer.synchronize(method, true, true);
                // we have a different instance because needed to be synchronized
                if (featureTerm != method){
                    intactFeature.getDetectionMethods().remove(method);
                    intactFeature.getDetectionMethods().add(featureTerm);
                }
            }
        }
    }
}
