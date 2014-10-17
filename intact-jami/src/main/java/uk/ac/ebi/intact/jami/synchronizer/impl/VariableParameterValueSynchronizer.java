package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.collections.map.IdentityMap;
import psidev.psi.mi.jami.model.VariableParameterValue;
import psidev.psi.mi.jami.model.VariableParameterValueSet;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.IntactVariableParameterValue;
import uk.ac.ebi.intact.jami.synchronizer.AbstractIntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Synchronizer for variable parameter value
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31/01/14</pre>
 */

public class VariableParameterValueSynchronizer extends AbstractIntactDbSynchronizer<VariableParameterValue, IntactVariableParameterValue> {
    private Map<VariableParameterValue, IntactVariableParameterValue> persistedObjects;
    private Map<VariableParameterValue, IntactVariableParameterValue> convertedObjects;

    public VariableParameterValueSynchronizer(SynchronizerContext context) {
        super(context, IntactVariableParameterValue.class);
        this.persistedObjects = new IdentityMap();
        this.convertedObjects = new IdentityMap();
    }

    @Override
    protected Object extractIdentifier(IntactVariableParameterValue object) {
        return object.getId();
    }

    @Override
    protected IntactVariableParameterValue instantiateNewPersistentInstance(VariableParameterValue object, Class<? extends IntactVariableParameterValue> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return new IntactVariableParameterValue(object.getValue(), object.getVariableParameter(), object.getOrder());
    }

    @Override
    protected void storeInCache(VariableParameterValue originalObject, IntactVariableParameterValue persistentObject, IntactVariableParameterValue existingInstance) {
        if (existingInstance != null){
            this.persistedObjects.put(originalObject, existingInstance);
        }
        else{
            this.persistedObjects.put(originalObject, persistentObject);
        }
    }

    @Override
    protected IntactVariableParameterValue fetchObjectFromCache(VariableParameterValue object) {
        return this.persistedObjects.get(object);
    }

    @Override
    protected boolean isObjectStoredInCache(VariableParameterValue object) {
        return this.persistedObjects.containsKey(object);
    }

    @Override
    protected boolean containsObjectInstance(VariableParameterValue object) {
        return this.convertedObjects.containsKey(object);
    }

    @Override
    protected void removeObjectInstanceFromIdentityCache(VariableParameterValue object) {
        this.convertedObjects.remove(object);
    }

    @Override
    protected IntactVariableParameterValue fetchMatchingObjectFromIdentityCache(VariableParameterValue object) {
        return this.convertedObjects.get(object);
    }

    @Override
    protected void convertPersistableProperties(IntactVariableParameterValue object) throws SynchronizerException, PersisterException, FinderException {
        // nothing to do
    }

    @Override
    protected void storeObjectInIdentityCache(VariableParameterValue originalObject, IntactVariableParameterValue persistableObject) {
         this.convertedObjects.put(originalObject, persistableObject);
    }

    @Override
    protected boolean isObjectDirty(VariableParameterValue originalObject) {
        return false;
    }

    public IntactVariableParameterValue find(VariableParameterValue object) throws FinderException {
        if (this.persistedObjects.containsKey(object)){
            return this.persistedObjects.get(object);
        }
        // only retrieve an object in the cache, otherwise return null
        else {
            return null;
        }
    }

    @Override
    public Collection<IntactVariableParameterValue> findAll(VariableParameterValue object) {
        if (this.persistedObjects.containsKey(object)){
            return Collections.singleton(this.persistedObjects.get(object));
        }
        // only retrieve an object in the cache, otherwise return null
        else {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public Collection<String> findAllMatchingAcs(VariableParameterValue object) {
        if (this.persistedObjects.containsKey(object)){
            IntactVariableParameterValue fetched = this.persistedObjects.get(object);
            if (fetched.getId() != null){
                return Collections.singleton(Long.toString(fetched.getId()));
            }
            return Collections.EMPTY_LIST;
        }
        // only retrieve an object in the cache, otherwise return null
        else {
            return Collections.EMPTY_LIST;
        }
    }

    public void synchronizeProperties(IntactVariableParameterValue object) throws FinderException, PersisterException, SynchronizerException {
        // only synchornize the value
        if (object.getValue() != null && IntactUtils.MAX_DESCRIPTION_LEN < object.getValue().length()){
            object.setValue(object.getValue().substring(0, IntactUtils.MAX_DESCRIPTION_LEN));
        }
    }

    public void clearCache() {
        this.persistedObjects.clear();
    }

    @Override
    public void deleteRelatedProperties(IntactVariableParameterValue value){
        if (!value.getInteractionParameterValues().isEmpty()){
            Collection<VariableParameterValueSet> existingSet = new ArrayList<VariableParameterValueSet>(value.getInteractionParameterValues());
            // delete interaction sets if it only contained the value that will be removed (avoid to keep empty sets)
            for (VariableParameterValueSet set : existingSet){
                if (set.size() == 1 && set.contains(value)){
                    value.getInteractionParameterValues().remove(set);
                    getEntityManager().remove(set);
                }
            }
        }
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<VariableParameterValue, IntactVariableParameterValue>(this));
    }
}
