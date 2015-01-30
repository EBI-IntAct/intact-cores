package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.collections.map.IdentityMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.Feature;
import psidev.psi.mi.jami.utils.clone.FeatureCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.FeatureMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactFeature;
import uk.ac.ebi.intact.jami.synchronizer.AbstractIntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Default finder/synchronizer for features
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class FeatureSynchronizerTemplate<F extends Feature, I extends AbstractIntactFeature> extends AbstractIntactDbSynchronizer<F,I>{
    private Map<F, I> persistedObjects;
    private Map<F, I> convertedObjects;

    private static final Log log = LogFactory.getLog(FeatureSynchronizerTemplate.class);

    public FeatureSynchronizerTemplate(SynchronizerContext context, Class<? extends I> featureClass){
        super(context, featureClass);

        this.persistedObjects = new IdentityMap();
        this.convertedObjects = new IdentityMap();
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
    public Collection<I> findAll(F feature) {
        if (this.persistedObjects.containsKey(feature)){
            return Collections.singleton(this.persistedObjects.get(feature));
        }
        // only retrieve an object in the cache, otherwise return null
        else {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public Collection<String> findAllMatchingAcs(F feature) {
        if (this.persistedObjects.containsKey(feature)){
            I retrievedFeature = this.persistedObjects.get(feature);
            if (retrievedFeature != null && retrievedFeature.getAc() != null){
               return Collections.singleton(retrievedFeature.getAc());
            }
            return Collections.EMPTY_LIST;
        }
        // only retrieve an object in the cache, otherwise return null
        else {
            return Collections.EMPTY_LIST;
        }
    }

    public void synchronizeProperties(I intactFeature) throws FinderException, PersisterException, SynchronizerException {
        // synchronize feature type
        prepareType(intactFeature, true);
        // then check def
        prepareInteractionEffectAndDependencies(intactFeature, true);
        // then check linkedFeatures
        prepareLinkedFeatures(intactFeature, true);
    }

    @Override
    public void deleteRelatedProperties(I intactFeature){
        for (Object f : intactFeature.getRelatedBindings()){
            I feature = (I)f;
            feature.getLinkedFeatures().remove(intactFeature);
        }
        intactFeature.getRelatedBindings().clear();
        for (Object f : intactFeature.getRelatedLinkedFeatures()){
            I feature = (I)f;
            feature.getLinkedFeatures().remove(intactFeature);
        }
        intactFeature.getLinkedFeatures().clear();
    }

    public void clearCache() {
        this.persistedObjects.clear();
        this.convertedObjects.clear();
    }

    protected void prepareType(I intactFeature, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        if (intactFeature.getType() != null){
            intactFeature.setType(enableSynchronization ?
                    getContext().getFeatureTypeSynchronizer().synchronize(intactFeature.getType(), true) :
                    getContext().getFeatureTypeSynchronizer().convertToPersistentObject(intactFeature.getType()));
        }
    }

    protected void prepareLinkedFeatures(I intactFeature, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {
        if (intactFeature.getBinds() != null){
            intactFeature.setBinds(enableSynchronization ?
                    synchronize((F)intactFeature.getBinds(), false) :
                    convertToPersistentObject((F)intactFeature.getBinds()));
        }
        if (intactFeature.areLinkedFeaturesInitialized()){
            List<F> featureToSynchronize = new ArrayList<F>(intactFeature.getDbLinkedFeatures());
            intactFeature.getDbLinkedFeatures().clear();
            for (F feature : featureToSynchronize){
                if (intactFeature != feature){
                    // do not persist or merge features because of cascades
                    I linkedFeature = enableSynchronization ?
                            synchronize((F) feature, false) :
                            convertToPersistentObject((F)feature);
                    // we have a different instance because needed to be synchronized
                    intactFeature.getDbLinkedFeatures().add(linkedFeature);
                }
            }
        }
    }

    protected void prepareInteractionEffectAndDependencies(I intactFeature, boolean enableSynchronization) throws PersisterException, FinderException, SynchronizerException {

        if (intactFeature.getRole() != null){
            intactFeature.setRole(enableSynchronization ?
                    getContext().getTopicSynchronizer().synchronize(intactFeature.getRole(), true) :
                    getContext().getTopicSynchronizer().convertToPersistentObject(intactFeature.getRole()));
        }
    }

    @Override
    protected I instantiateNewPersistentInstance(F object, Class<? extends I> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        I newFeature = intactClass.newInstance();
        FeatureCloner.copyAndOverrideBasicFeaturesProperties(object, newFeature);
        newFeature.setParticipant(object.getParticipant());
        return newFeature;
    }

    @Override
    protected void storeInCache(F originalObject, I persistentObject, I existingInstance) {
        if (existingInstance != null){
            this.persistedObjects.put(originalObject, existingInstance);
        }
        else{
            this.persistedObjects.put(originalObject, persistentObject);
        }
    }

    @Override
    protected I fetchObjectFromCache(F object) {

        return this.persistedObjects.get(object);
    }

    @Override
    protected boolean isObjectStoredInCache(F object) {
        return this.persistedObjects.containsKey(object);
    }

    @Override
    protected boolean containsObjectInstance(F object) {
        return this.convertedObjects.containsKey(object);
    }

    @Override
    protected void removeObjectInstanceFromIdentityCache(F object) {
        this.convertedObjects.remove(object);
    }

    @Override
    protected I fetchMatchingObjectFromIdentityCache(F object) {
        return this.convertedObjects.get(object);
    }

    @Override
    protected void convertPersistableProperties(I intactFeature) throws SynchronizerException, PersisterException, FinderException {
        // synchronize feature type
        prepareType(intactFeature, false);
        // then check def
        prepareInteractionEffectAndDependencies(intactFeature, false);
        // then check linkedFeatures
        prepareLinkedFeatures(intactFeature, false);
    }

    @Override
    protected void storeObjectInIdentityCache(F originalObject, I persistableObject) {
        this.convertedObjects.put(originalObject, persistableObject);
    }

    @Override
    protected boolean isObjectPartiallyInitialised(F originalObject) {
        // always return false as we only keep identity map of features
        return false;
    }

    @Override
    protected Object extractIdentifier(I object) {
        return object.getAc();
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new FeatureMergerEnrichOnly<F, I>());
    }

    @Override
    protected void persistObject(I existingInstance) {
        // first remove all dependencies to other features to avoid cycle dependencies when persisting the objects
        Collection<F> linkedFeatures = new ArrayList<F>(existingInstance.getDbLinkedFeatures());
        F bind = (F)existingInstance.getBinds();
        existingInstance.getDbLinkedFeatures().clear();
        existingInstance.setBinds(null);

        super.persistObject(existingInstance);

        // after persistence, re-attach dependent objects to avoid internal loops when cvs are called by each other
        existingInstance.getDbLinkedFeatures().addAll(linkedFeatures);
        existingInstance.setBinds(bind);
    }

    @Override
    protected void synchronizePropertiesBeforeCacheMerge(I existingInstance) throws FinderException, PersisterException, SynchronizerException {
        // then check linkedFeatures if any
        prepareLinkedFeatures(existingInstance, true);
    }
}
