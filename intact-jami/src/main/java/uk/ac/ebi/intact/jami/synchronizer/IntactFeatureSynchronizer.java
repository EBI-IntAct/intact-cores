package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactFeature;
import uk.ac.ebi.intact.jami.model.extension.FeatureAlias;
import uk.ac.ebi.intact.jami.model.extension.FeatureAnnotation;
import uk.ac.ebi.intact.jami.model.extension.FeatureXref;
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

public class IntactFeatureSynchronizer<F extends Feature> implements IntactDbSynchronizer<F>{
    private EntityManager entityManager;

    private IntactDbSynchronizer<Alias> aliasSynchronizer;
    private IntactDbSynchronizer<Annotation> annotationSynchronizer;
    private IntactDbSynchronizer<Xref> xrefSynchronizer;

    private IntactDbSynchronizer<CvTerm> effectSynchronizer;
    private IntactDbSynchronizer<CvTerm> typeSynchronizer;

    private Class<? extends AbstractIntactFeature> featureClass;

    private static final Log log = LogFactory.getLog(IntactFeatureSynchronizer.class);

    public IntactFeatureSynchronizer(EntityManager entityManager, Class<? extends AbstractIntactFeature> featureClass){
        if (entityManager == null){
            throw new IllegalArgumentException("A feature synchronizer needs a non null entity manager");
        }
        this.entityManager = entityManager;
        if (featureClass == null){
            throw new IllegalArgumentException("A feature synchronizer needs a non null feature class");
        }
        this.featureClass = featureClass;

        this.aliasSynchronizer = new IntactAliasSynchronizer(this.entityManager, FeatureAlias.class);
        this.annotationSynchronizer = new IntactAnnotationsSynchronizer(this.entityManager, FeatureAnnotation.class);
        this.xrefSynchronizer = new IntactXrefSynchronizer(this.entityManager, FeatureXref.class);

        this.effectSynchronizer = new IntactCvTermSynchronizer(this.entityManager, IntactUtils.TOPIC_OBJCLASS);
        this.typeSynchronizer = new IntactCvTermSynchronizer(this.entityManager, IntactUtils.FEATURE_TYPE_OBJCLASS);
    }

    public IntactFeatureSynchronizer(EntityManager entityManager, Class<? extends AbstractIntactFeature> featureClass,
                                     IntactDbSynchronizer<Alias> aliasSynchronizer,
                                    IntactDbSynchronizer<Annotation> annotationSynchronizer, IntactDbSynchronizer<Xref> xrefSynchronizer,
                                    IntactDbSynchronizer<CvTerm> typeSynchronizer, IntactDbSynchronizer<CvTerm> effectSynchronizer){
        if (entityManager == null){
            throw new IllegalArgumentException("A feature synchronizer needs a non null entity manager");
        }
        this.entityManager = entityManager;
        if (featureClass == null){
            throw new IllegalArgumentException("A feature synchronizer needs a non null feature class");
        }
        this.featureClass = featureClass;
        this.aliasSynchronizer = aliasSynchronizer != null ? aliasSynchronizer : new IntactAliasSynchronizer(this.entityManager, FeatureAlias.class);
        this.annotationSynchronizer = annotationSynchronizer != null ? annotationSynchronizer : new IntactAnnotationsSynchronizer(this.entityManager, FeatureAnnotation.class);
        this.xrefSynchronizer = xrefSynchronizer != null ? xrefSynchronizer : new IntactXrefSynchronizer(this.entityManager, FeatureXref.class);

        this.effectSynchronizer = effectSynchronizer != null ? effectSynchronizer : new IntactCvTermSynchronizer(this.entityManager, IntactUtils.TOPIC_OBJCLASS);
        this.typeSynchronizer = typeSynchronizer != null ? typeSynchronizer : new IntactCvTermSynchronizer(this.entityManager, IntactUtils.FEATURE_TYPE_OBJCLASS);
    }

    public F find(F feature) throws FinderException {
        return null;
    }

    public F persist(F object) throws FinderException, PersisterException, SynchronizerException {
        synchronizeProperties((AbstractIntactFeature) object);
        this.entityManager.persist(object);
        return object;
    }

    public void synchronizeProperties(F object) throws FinderException, PersisterException, SynchronizerException {
        synchronizeProperties((AbstractIntactFeature)object);
    }

    public F synchronize(F object, boolean persist, boolean merge) throws FinderException, PersisterException, SynchronizerException {
        if (!object.getClass().isAssignableFrom(this.featureClass)){
            AbstractIntactFeature newFeature = null;
            try {
                newFeature = this.featureClass.newInstance();
            } catch (InstantiationException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.featureClass, e);
            } catch (IllegalAccessException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.featureClass, e);
            }

            // synchronize properties
            synchronizeProperties(newFeature);
            if (persist){
                this.entityManager.persist(newFeature);
            }
            return (F)newFeature;
        }
        else{
            AbstractIntactFeature intactType = (AbstractIntactFeature)object;
            // detached existing instance
            if (intactType.getAc() != null && !this.entityManager.contains(intactType)){
                // synchronize properties
                synchronizeProperties(intactType);
                // merge
                if (merge){
                    return (F)this.entityManager.merge(intactType);
                }
                else{
                    return (F)intactType;
                }
            }
            // retrieve and or persist transient instance
            else if (intactType.getAc() == null){
                // synchronize properties
                synchronizeProperties(intactType);
                // persist alias
                if (persist){
                    this.entityManager.persist(intactType);
                }
                return (F)intactType;
            }
            else{
                // synchronize properties
                synchronizeProperties(intactType);
                return (F)intactType;
            }
        }
    }

    public void clearCache() {
        this.aliasSynchronizer.clearCache();
        this.xrefSynchronizer.clearCache();
        this.annotationSynchronizer.clearCache();

        this.typeSynchronizer.clearCache();
        this.effectSynchronizer.clearCache();
    }

    protected void synchronizeProperties(AbstractIntactFeature intactFeature) throws FinderException, PersisterException, SynchronizerException {
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
    }

    protected void prepareInteractionEffectAndDependencies(AbstractIntactFeature intactFeature) throws PersisterException, FinderException, SynchronizerException {
        if (intactFeature.getInteractionDependency() != null){
            intactFeature.setInteractionDependency(this.effectSynchronizer.synchronize(intactFeature.getInteractionDependency(), true, true));
        }

        if (intactFeature.getInteractionEffect() != null){
            intactFeature.setInteractionEffect(this.effectSynchronizer.synchronize(intactFeature.getInteractionEffect(), true, true));
        }
    }

    protected void prepareXrefs(AbstractIntactFeature intactFeature) throws FinderException, PersisterException, SynchronizerException {
        if (intactFeature.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactFeature.getPersistentXrefs());
            for (Xref xref : xrefsToPersist){
                // do not persist or merge xrefs because of cascades
                Xref featureXref = this.xrefSynchronizer.synchronize(xref, false, false);
                // we have a different instance because needed to be synchronized
                if (featureXref != xref){
                    intactFeature.getPersistentXrefs().remove(xref);
                    intactFeature.getPersistentXrefs().add(featureXref);
                }
            }
        }
    }

    protected void prepareAnnotations(AbstractIntactFeature intactFeature) throws FinderException, PersisterException, SynchronizerException {
        if (intactFeature.areAnnotationsInitialized()){
            List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactFeature.getAnnotations());
            for (Annotation annotation : annotationsToPersist){
                // do not persist or merge annotations because of cascades
                Annotation featureAnnotation = this.annotationSynchronizer.synchronize(annotation, false, false);
                // we have a different instance because needed to be synchronized
                if (featureAnnotation != annotation){
                    intactFeature.getAnnotations().remove(annotation);
                    intactFeature.getAnnotations().add(featureAnnotation);
                }
            }
        }
    }

    protected void prepareAliases(AbstractIntactFeature intactFeature) throws FinderException, PersisterException, SynchronizerException {
        if (intactFeature.areAliasesInitialized()){
            List<Alias> aliasesToPersist = new ArrayList<Alias>(intactFeature.getAliases());
            for (Alias alias : aliasesToPersist){
                // do not persist or merge alias because of cascades
                Alias featureAlias = this.aliasSynchronizer.synchronize(alias, false, false);
                // we have a different instance because needed to be synchronized
                if (featureAlias != alias){
                    intactFeature.getAliases().remove(alias);
                    intactFeature.getAliases().add(featureAlias);
                }
            }
        }
    }

    protected void prepareFullName(AbstractIntactFeature intactFeature) {
        // truncate if necessary
        if (intactFeature.getFullName() != null && IntactUtils.MAX_FULL_NAME_LEN < intactFeature.getFullName().length()){
            log.warn("Feature fullName too long: "+intactFeature.getFullName()+", will be truncated to "+ IntactUtils.MAX_FULL_NAME_LEN+" characters.");
            intactFeature.setFullName(intactFeature.getFullName().substring(0, IntactUtils.MAX_FULL_NAME_LEN));
        }
    }

    protected void prepareAndSynchronizeShortLabel(AbstractIntactFeature intactFeature) {
        // truncate if necessary
        if (intactFeature.getShortName() == null){
            intactFeature.setShortName("N/A");
        }
        else if (IntactUtils.MAX_SHORT_LABEL_LEN < intactFeature.getShortName().length()){
            log.warn("Feature shortLabel too long: "+intactFeature.getShortName()+", will be truncated to "+ IntactUtils.MAX_SHORT_LABEL_LEN+" characters.");
            intactFeature.setShortName(intactFeature.getShortName().substring(0, IntactUtils.MAX_SHORT_LABEL_LEN));
        }
    }
}
