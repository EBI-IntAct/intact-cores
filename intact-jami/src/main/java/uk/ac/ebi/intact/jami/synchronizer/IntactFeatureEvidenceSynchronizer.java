package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.FeatureCloner;
import uk.ac.ebi.intact.jami.merger.IntactFeatureEvidenceMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.*;
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
                CvTerm featureTerm = this.methodSynchronizer.synchronize(method, true);
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
        super.setIntactMerger(new IntactFeatureEvidenceMergerEnrichOnly());
    }

    @Override
    protected IntactFeatureEvidence instantiateNewPersistentInstance(FeatureEvidence object, Class<? extends IntactFeatureEvidence> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactFeatureEvidence newFeature = new IntactFeatureEvidence();
        FeatureCloner.copyAndOverrideFeatureEvidenceProperties(object, newFeature);
        newFeature.setParticipant(object.getParticipant());
        return newFeature;
    }
}
