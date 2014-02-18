package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.VariableParameterValue;
import psidev.psi.mi.jami.model.VariableParameterValueSet;
import uk.ac.ebi.intact.jami.merger.IntactMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.IntactVariableParameterValue;
import uk.ac.ebi.intact.jami.model.extension.IntactVariableParameterValueSet;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Synchronizer for variable parameter value set
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31/01/14</pre>
 */

public class IntactVariableParameterValueSetSynchronizer extends AbstractIntactDbSynchronizer<VariableParameterValueSet, IntactVariableParameterValueSet> {

    private IntactDbSynchronizer<VariableParameterValue, IntactVariableParameterValue> parameterValueSynchronizer;

    public IntactVariableParameterValueSetSynchronizer(EntityManager entityManager) {
        super(entityManager, IntactVariableParameterValueSet.class);
    }

    @Override
    protected Object extractIdentifier(IntactVariableParameterValueSet object) {
        return object.getId();
    }

    @Override
    protected IntactVariableParameterValueSet instantiateNewPersistentInstance(VariableParameterValueSet object, Class<? extends IntactVariableParameterValueSet> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return new IntactVariableParameterValueSet(object);
    }

    public IntactVariableParameterValueSet find(VariableParameterValueSet object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(IntactVariableParameterValueSet object) throws FinderException, PersisterException, SynchronizerException {
        if (object.areVariableParameterValuesInitialized()){
            List<VariableParameterValue> valuesToPersist = new ArrayList<VariableParameterValue>(object);
            for (VariableParameterValue value : valuesToPersist){
                VariableParameterValue valueCheck = getParameterValueSynchronizer().synchronize(value, false);
                // we have a different instance because needed to be synchronized
                if (valueCheck != value){
                    object.remove(value);
                    object.add(valueCheck);
                }
            }
        }
    }

    public void clearCache() {
        getParameterValueSynchronizer().clearCache();
    }

    public IntactDbSynchronizer<VariableParameterValue, IntactVariableParameterValue> getParameterValueSynchronizer() {
        if (this.parameterValueSynchronizer == null){
            this.parameterValueSynchronizer = new IntactVariableParameterValueSynchronizer(getEntityManager());
        }
        return parameterValueSynchronizer;
    }

    public void setParameterValueSynchronizer(IntactDbSynchronizer<VariableParameterValue, IntactVariableParameterValue> parameterValueSynchronizer) {
        this.parameterValueSynchronizer = parameterValueSynchronizer;
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactMergerIgnoringPersistentObject<VariableParameterValueSet, IntactVariableParameterValueSet>(this));
    }
}
