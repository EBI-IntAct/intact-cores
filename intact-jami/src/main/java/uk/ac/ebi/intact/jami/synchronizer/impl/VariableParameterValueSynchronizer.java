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

    public VariableParameterValueSynchronizer(SynchronizerContext context) {
        super(context, IntactVariableParameterValue.class);
        this.persistedObjects = new IdentityMap();
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

    public IntactVariableParameterValue find(VariableParameterValue object) throws FinderException {
        if (this.persistedObjects.containsKey(object)){
            return this.persistedObjects.get(object);
        }
        // only retrieve an object in the cache, otherwise return null
        else {
            return null;
        }
    }

    public IntactVariableParameterValue persist(IntactVariableParameterValue object) throws FinderException, PersisterException, SynchronizerException {
        // only persist if not already done
        if (this.persistedObjects.containsKey(object)){
            return this.persistedObjects.get(object);
        }

        return super.persist(object);
    }

    @Override
    public IntactVariableParameterValue synchronize(VariableParameterValue object, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        // only synchronize if not already done
        if (!this.persistedObjects.containsKey(object)){
            return this.persistedObjects.get(object);
        }

        return super.synchronize(object, persist);
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
