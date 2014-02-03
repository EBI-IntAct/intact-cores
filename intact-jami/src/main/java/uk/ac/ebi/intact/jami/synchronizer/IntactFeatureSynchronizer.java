package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.collections.map.IdentityMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.merger.IntactFeatureMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Default finder/synchronizer for features
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class IntactFeatureSynchronizer<F extends Feature, I extends AbstractIntactFeature> extends AbstractIntactDbSynchronizer<F,I>{

    private IntactDbSynchronizer<Alias, FeatureAlias> aliasSynchronizer;
    private IntactDbSynchronizer<Annotation, FeatureAnnotation> annotationSynchronizer;
    private IntactDbSynchronizer<Xref, FeatureXref> xrefSynchronizer;

    private IntactDbSynchronizer<CvTerm, IntactCvTerm> effectSynchronizer;
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> typeSynchronizer;
    private IntactDbSynchronizer<Range, IntactRange> rangeSynchronizer;
    private Map<F, I> persistedObjects;

    private static final Log log = LogFactory.getLog(IntactFeatureSynchronizer.class);

    public IntactFeatureSynchronizer(EntityManager entityManager, Class<? extends I> featureClass){
        super(entityManager, featureClass);

        this.aliasSynchronizer = new IntactAliasSynchronizer(entityManager, FeatureAlias.class);
        this.annotationSynchronizer = new IntactAnnotationsSynchronizer(entityManager, FeatureAnnotation.class);
        this.xrefSynchronizer = new IntactXrefSynchronizer(entityManager, FeatureXref.class);

        this.effectSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.TOPIC_OBJCLASS);
        this.typeSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.FEATURE_TYPE_OBJCLASS);
        this.rangeSynchronizer = new IntactRangeSynchronizer(entityManager);

        this.persistedObjects = new IdentityMap();
    }

    public IntactFeatureSynchronizer(EntityManager entityManager, Class<? extends I> featureClass,
                                     IntactDbSynchronizer<Alias, FeatureAlias> aliasSynchronizer,
                                    IntactDbSynchronizer<Annotation, FeatureAnnotation> annotationSynchronizer, IntactDbSynchronizer<Xref, FeatureXref> xrefSynchronizer,
                                    IntactDbSynchronizer<CvTerm, IntactCvTerm> typeSynchronizer, IntactDbSynchronizer<CvTerm, IntactCvTerm> effectSynchronizer,
                                    IntactDbSynchronizer<Range, IntactRange> rangeSynchronizer){
        super(entityManager, featureClass);
        this.aliasSynchronizer = aliasSynchronizer != null ? aliasSynchronizer : new IntactAliasSynchronizer(entityManager, FeatureAlias.class);
        this.annotationSynchronizer = annotationSynchronizer != null ? annotationSynchronizer : new IntactAnnotationsSynchronizer(entityManager, FeatureAnnotation.class);
        this.xrefSynchronizer = xrefSynchronizer != null ? xrefSynchronizer : new IntactXrefSynchronizer(entityManager, FeatureXref.class);

        this.effectSynchronizer = effectSynchronizer != null ? effectSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.TOPIC_OBJCLASS);
        this.typeSynchronizer = typeSynchronizer != null ? typeSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.FEATURE_TYPE_OBJCLASS);
        this.rangeSynchronizer = rangeSynchronizer != null ? rangeSynchronizer : new IntactRangeSynchronizer(entityManager);
        this.persistedObjects = new IdentityMap();
    }

    public I find(F feature) throws FinderException {
        if (this.persistedObjects.containsKey(feature)){
            return this.persistedObjects.get(feature);
        }
        // only retrieve an object in the cache, otherwise return null
        else {
            return null;
        }
    }

    @Override
    public I persist(I object) throws FinderException, PersisterException, SynchronizerException {
        // only persist if not already done
        if (!this.persistedObjects.containsKey(object)){
            return this.persistedObjects.get(object);
        }

        I persisted = super.persist(object);
        this.persistedObjects.put((F)object, persisted);

        return persisted;
    }

    @Override
    public I synchronize(F object, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        // only synchronize if not already done
        if (!this.persistedObjects.containsKey(object)){
            return this.persistedObjects.get(object);
        }

        I org = super.synchronize(object, persist);
        this.persistedObjects.put(object, org);
        return org;
    }

    public void synchronizeProperties(I intactFeature) throws FinderException, PersisterException, SynchronizerException {
        // then check shortlabel/synchronize
        prepareAndSynchronizeShortLabel(intactFeature);
        // then check full name
        prepareFullName(intactFeature);
        // then check def
        prepareInteractionEffectAndDependencies(intactFeature);
        // then check aliases
        prepareAliases(intactFeature);
        // then check annotations
        prepareAnnotations(intactFeature);
        // then check xrefs
        prepareXrefs(intactFeature);
        // then check ranges
        prepareRanges(intactFeature);
        // then check linkedFeatures
        prepareLinkedFeatures(intactFeature);
    }

    public void clearCache() {
        this.aliasSynchronizer.clearCache();
        this.xrefSynchronizer.clearCache();
        this.annotationSynchronizer.clearCache();

        this.typeSynchronizer.clearCache();
        this.effectSynchronizer.clearCache();
        this.persistedObjects.clear();
    }

    protected void prepareLinkedFeatures(I intactFeature) throws PersisterException, FinderException, SynchronizerException {
        if (intactFeature.areLinkedFeaturesInitialized()){
            List<I> featureToSynchronize = new ArrayList<I>(intactFeature.getLinkedFeatures());
            for (I feature : featureToSynchronize){
                // do not persist or merge features because of cascades
                I linkedFeature = synchronize((F) feature, false);
                // we have a different instance because needed to be synchronized
                if (linkedFeature != feature){
                    intactFeature.getLinkedFeatures().remove(feature);
                    intactFeature.getLinkedFeatures().add(linkedFeature);
                }
            }
        }
    }

    protected void prepareRanges(I intactFeature) throws PersisterException, FinderException, SynchronizerException {
        if (intactFeature.areRangesInitialized()){
            List<Range> rangesToPersist = new ArrayList<Range>(intactFeature.getRanges());
            for (Range range : rangesToPersist){
                // do not persist or merge ranges because of cascades
                Range featureRange = this.rangeSynchronizer.synchronize(range, false);
                // we have a different instance because needed to be synchronized
                if (featureRange != range){
                    intactFeature.getRanges().remove(range);
                    intactFeature.getRanges().add(featureRange);
                }
            }
        }
    }

    protected void prepareInteractionEffectAndDependencies(I intactFeature) throws PersisterException, FinderException, SynchronizerException {
        if (intactFeature.getInteractionDependency() != null){
            intactFeature.setInteractionDependency(this.effectSynchronizer.synchronize(intactFeature.getInteractionDependency(), true));
        }

        if (intactFeature.getInteractionEffect() != null){
            intactFeature.setInteractionEffect(this.effectSynchronizer.synchronize(intactFeature.getInteractionEffect(), true));
        }
    }

    protected void prepareXrefs(I intactFeature) throws FinderException, PersisterException, SynchronizerException {
        if (intactFeature.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactFeature.getPersistentXrefs());
            for (Xref xref : xrefsToPersist){
                // do not persist or merge xrefs because of cascades
                Xref featureXref = this.xrefSynchronizer.synchronize(xref, false);
                // we have a different instance because needed to be synchronized
                if (featureXref != xref){
                    intactFeature.getPersistentXrefs().remove(xref);
                    intactFeature.getPersistentXrefs().add(featureXref);
                }
            }
        }
    }

    protected void prepareAnnotations(I intactFeature) throws FinderException, PersisterException, SynchronizerException {
        if (intactFeature.areAnnotationsInitialized()){
            List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactFeature.getAnnotations());
            for (Annotation annotation : annotationsToPersist){
                // do not persist or merge annotations because of cascades
                Annotation featureAnnotation = this.annotationSynchronizer.synchronize(annotation, false);
                // we have a different instance because needed to be synchronized
                if (featureAnnotation != annotation){
                    intactFeature.getAnnotations().remove(annotation);
                    intactFeature.getAnnotations().add(featureAnnotation);
                }
            }
        }
    }

    protected void prepareAliases(I intactFeature) throws FinderException, PersisterException, SynchronizerException {
        if (intactFeature.areAliasesInitialized()){
            List<Alias> aliasesToPersist = new ArrayList<Alias>(intactFeature.getAliases());
            for (Alias alias : aliasesToPersist){
                // do not persist or merge alias because of cascades
                Alias featureAlias = this.aliasSynchronizer.synchronize(alias, false);
                // we have a different instance because needed to be synchronized
                if (featureAlias != alias){
                    intactFeature.getAliases().remove(alias);
                    intactFeature.getAliases().add(featureAlias);
                }
            }
        }
    }

    protected void prepareFullName(I intactFeature) {
        // truncate if necessary
        if (intactFeature.getFullName() != null && IntactUtils.MAX_FULL_NAME_LEN < intactFeature.getFullName().length()){
            log.warn("Feature fullName too long: "+intactFeature.getFullName()+", will be truncated to "+ IntactUtils.MAX_FULL_NAME_LEN+" characters.");
            intactFeature.setFullName(intactFeature.getFullName().substring(0, IntactUtils.MAX_FULL_NAME_LEN));
        }
    }

    protected void prepareAndSynchronizeShortLabel(I intactFeature) {
        // truncate if necessary
        if (intactFeature.getShortName() == null){
            intactFeature.setShortName("N/A");
        }
        else if (IntactUtils.MAX_SHORT_LABEL_LEN < intactFeature.getShortName().length()){
            log.warn("Feature shortLabel too long: "+intactFeature.getShortName()+", will be truncated to "+ IntactUtils.MAX_SHORT_LABEL_LEN+" characters.");
            intactFeature.setShortName(intactFeature.getShortName().substring(0, IntactUtils.MAX_SHORT_LABEL_LEN));
        }
    }

    @Override
    protected I instantiateNewPersistentInstance(F object, Class<? extends I> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return intactClass.newInstance();
    }

    @Override
    protected Object extractIdentifier(I object) {
        return object.getAc();
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactFeatureMergerEnrichOnly<F, I>());
    }
}
