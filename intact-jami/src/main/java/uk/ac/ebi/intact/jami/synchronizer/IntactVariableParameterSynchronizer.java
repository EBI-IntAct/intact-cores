package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.VariableParameter;
import psidev.psi.mi.jami.model.VariableParameterValue;
import psidev.psi.mi.jami.utils.clone.VariableParameterCloner;
import uk.ac.ebi.intact.jami.merger.IntactMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactVariableParameter;
import uk.ac.ebi.intact.jami.model.extension.IntactVariableParameterValue;
import uk.ac.ebi.intact.jami.synchronizer.impl.IntactCvTermSynchronizer;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
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

public class IntactVariableParameterSynchronizer extends AbstractIntactDbSynchronizer<VariableParameter, IntactVariableParameter>{
    private IntactDbSynchronizer<CvTerm, IntactCvTerm> unitSynchronizer;
    private IntactDbSynchronizer<VariableParameterValue, IntactVariableParameterValue> parameterValueSynchronizer;

    public IntactVariableParameterSynchronizer(EntityManager entityManager) {
        super(entityManager, IntactVariableParameter.class);
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

    public IntactDbSynchronizer<CvTerm, IntactCvTerm> getUnitSynchronizer() {
        if (this.unitSynchronizer == null){
            this.unitSynchronizer = new IntactCvTermSynchronizer(getEntityManager(), IntactUtils.UNIT_OBJCLASS);
        }
        return unitSynchronizer;
    }

    public void setUnitSynchronizer(IntactDbSynchronizer<CvTerm, IntactCvTerm> unitSynchronizer) {
        this.unitSynchronizer = unitSynchronizer;
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

    protected void synchronizeParameterValues(IntactVariableParameter object) throws FinderException, PersisterException, SynchronizerException {
        if (object.areVariableParameterValuesInitialized()){
            List<VariableParameterValue> valuesToPersist = new ArrayList<VariableParameterValue>(object.getVariableValues());
            for (VariableParameterValue value : valuesToPersist){
                VariableParameterValue valueCheck = getParameterValueSynchronizer().synchronize(value, false);
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
            object.setUnit(getUnitSynchronizer().synchronize(object.getUnit(), true));
        }
    }

    protected void synchronizeDescription(IntactVariableParameter object) {
        if (object.getDescription() != null && IntactUtils.MAX_DESCRIPTION_LEN < object.getDescription().length()){
            object.setDescription(object.getDescription().substring(0, IntactUtils.MAX_DESCRIPTION_LEN));
        }
    }

    public void clearCache() {
        getUnitSynchronizer().clearCache();
        getParameterValueSynchronizer().clearCache();
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactMergerIgnoringPersistentObject<VariableParameter, IntactVariableParameter>(this));
    }
}
