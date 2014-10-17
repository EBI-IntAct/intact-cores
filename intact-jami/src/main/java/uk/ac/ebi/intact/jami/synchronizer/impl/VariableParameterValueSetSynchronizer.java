package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.VariableParameterValue;
import psidev.psi.mi.jami.model.VariableParameterValueSet;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.IntactVariableParameterValueSet;
import uk.ac.ebi.intact.jami.synchronizer.AbstractIntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Synchronizer for variable parameter value set
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31/01/14</pre>
 */

public class VariableParameterValueSetSynchronizer extends AbstractIntactDbSynchronizer<VariableParameterValueSet, IntactVariableParameterValueSet> {

    public VariableParameterValueSetSynchronizer(SynchronizerContext context) {
        super(context, IntactVariableParameterValueSet.class);
    }

    @Override
    protected Object extractIdentifier(IntactVariableParameterValueSet object) {
        return object.getId();
    }

    @Override
    protected IntactVariableParameterValueSet instantiateNewPersistentInstance(VariableParameterValueSet object, Class<? extends IntactVariableParameterValueSet> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return new IntactVariableParameterValueSet(object);
    }

    @Override
    protected void storeInCache(VariableParameterValueSet originalObject, IntactVariableParameterValueSet persistentObject, IntactVariableParameterValueSet existingInstance) {
        // nothing to do
    }

    @Override
    protected IntactVariableParameterValueSet fetchObjectFromCache(VariableParameterValueSet object) {
        return null;
    }

    @Override
    protected boolean isObjectStoredInCache(VariableParameterValueSet object) {
        return false;
    }

    @Override
    protected boolean containsObjectInstance(VariableParameterValueSet object) {
        return false;
    }

    @Override
    protected void removeObjectInstanceFromIdentityCache(VariableParameterValueSet object) {
        // nothing to do
    }

    @Override
    protected IntactVariableParameterValueSet fetchMatchingObjectFromIdentityCache(VariableParameterValueSet object) {
        return null;
    }

    @Override
    protected void convertPersistableProperties(IntactVariableParameterValueSet object) throws SynchronizerException, PersisterException, FinderException {
        prepareVariableParameterValues(object, false);
    }

    @Override
    protected void storeObjectInIdentityCache(VariableParameterValueSet originalObject, IntactVariableParameterValueSet persistableObject) {
         // nothing to do
    }

    @Override
    protected boolean isObjectDirty(VariableParameterValueSet originalObject) {
        return false;
    }

    public IntactVariableParameterValueSet find(VariableParameterValueSet object) throws FinderException {
        return null;
    }

    @Override
    public Collection<IntactVariableParameterValueSet> findAll(VariableParameterValueSet object) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<String> findAllMatchingAcs(VariableParameterValueSet object) {
        return Collections.EMPTY_LIST;
    }

    public void synchronizeProperties(IntactVariableParameterValueSet object) throws FinderException, PersisterException, SynchronizerException {
        prepareVariableParameterValues(object, true);
    }

    protected void prepareVariableParameterValues(IntactVariableParameterValueSet object, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (object.areVariableParameterValuesInitialized()){
            List<VariableParameterValue> valuesToPersist = new ArrayList<VariableParameterValue>(object);
            for (VariableParameterValue value : valuesToPersist){
                VariableParameterValue valueCheck = enableSynchronization ?
                        getContext().getVariableParameterValueSynchronizer().synchronize(value, false):
                        getContext().getVariableParameterValueSynchronizer().convertToPersistentObject(value);
                // we have a different instance because needed to be synchronized
                if (valueCheck != value){
                    object.remove(value);
                    object.add(valueCheck);
                }
            }
        }
    }

    public void clearCache() {
        // nothing to do
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<VariableParameterValueSet, IntactVariableParameterValueSet>(this));
    }
}
