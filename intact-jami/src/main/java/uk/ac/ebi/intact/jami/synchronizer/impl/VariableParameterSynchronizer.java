package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.VariableParameter;
import psidev.psi.mi.jami.model.VariableParameterValue;
import psidev.psi.mi.jami.utils.clone.VariableParameterCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.IntactVariableParameter;
import uk.ac.ebi.intact.jami.synchronizer.AbstractIntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Synchronizer of variable parameter
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31/01/14</pre>
 */

public class VariableParameterSynchronizer extends AbstractIntactDbSynchronizer<VariableParameter, IntactVariableParameter> {

    public VariableParameterSynchronizer(SynchronizerContext context) {
        super(context, IntactVariableParameter.class);
    }

    @Override
    protected Object extractIdentifier(IntactVariableParameter object) {
        return object.getId();
    }

    @Override
    protected IntactVariableParameter instantiateNewPersistentInstance(VariableParameter object, Class<? extends IntactVariableParameter> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactVariableParameter param = new IntactVariableParameter(object.getDescription(), object.getExperiment(), object.getUnit());
        VariableParameterCloner.copyAndOverrideVariableParameterProperties(object, param);
        return param;
    }

    @Override
    protected void storeInCache(VariableParameter originalObject, IntactVariableParameter persistentObject, IntactVariableParameter existingInstance) {
        // nothing to do
    }

    @Override
    protected IntactVariableParameter fetchObjectFromCache(VariableParameter object) {
        return null;
    }

    @Override
    protected boolean isObjectStoredInCache(VariableParameter object) {
        return false;
    }

    public IntactVariableParameter find(VariableParameter object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(IntactVariableParameter object) throws FinderException, PersisterException, SynchronizerException {
        // synchronize
        synchronizeDescription(object);
        // synchronize unit
        synchronizeUnit(object);
        // synchronize values
        synchronizeParameterValues(object);
    }

    protected void synchronizeParameterValues(IntactVariableParameter object) throws FinderException, PersisterException, SynchronizerException {
        if (object.areVariableParameterValuesInitialized()){
            List<VariableParameterValue> valuesToPersist = new ArrayList<VariableParameterValue>(object.getVariableValues());
            for (VariableParameterValue value : valuesToPersist){
                VariableParameterValue valueCheck = getContext().getVariableParameterValueSynchronizer().synchronize(value, false);
                // we have a different instance because needed to be synchronized
                if (valueCheck != value){
                    object.getVariableValues().remove(value);
                    object.getVariableValues().add(valueCheck);
                }
            }
        }
    }

    protected void synchronizeUnit(IntactVariableParameter object) throws FinderException, PersisterException, SynchronizerException {
        if (object.getUnit() != null){
            object.setUnit(getContext().getUnitSynchronizer().synchronize(object.getUnit(), true));
        }
    }

    protected void synchronizeDescription(IntactVariableParameter object) {
        if (object.getDescription() != null && IntactUtils.MAX_DESCRIPTION_LEN < object.getDescription().length()){
            object.setDescription(object.getDescription().substring(0, IntactUtils.MAX_DESCRIPTION_LEN));
        }
    }

    public void clearCache() {
        // nothing to do
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<VariableParameter, IntactVariableParameter>(this));
    }
}
