package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.collections.map.IdentityMap;
import psidev.psi.mi.jami.model.VariableParameterValue;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.IntactVariableParameterValue;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Synchronizer for variable parameter value
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31/01/14</pre>
 */

public class IntactVariableParameterValueSynchronizer extends AbstractIntactDbSynchronizer<VariableParameterValue, IntactVariableParameterValue>{
    private Map<VariableParameterValue, IntactVariableParameterValue> persistedObjects;

    public IntactVariableParameterValueSynchronizer(EntityManager entityManager) {
        super(entityManager, IntactVariableParameterValue.class);
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
        if (!this.persistedObjects.containsKey(object)){
            return this.persistedObjects.get(object);
        }

        IntactVariableParameterValue persisted = super.persist(object);
        this.persistedObjects.put(object, persisted);

        return persisted;
    }

    @Override
    public IntactVariableParameterValue synchronize(VariableParameterValue object, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        // only synchronize if not already done
        if (!this.persistedObjects.containsKey(object)){
            return this.persistedObjects.get(object);
        }

        IntactVariableParameterValue org = super.synchronize(object, persist);
        this.persistedObjects.put(object, org);
        return org;
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
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<VariableParameterValue, IntactVariableParameterValue>(this));
    }
}
