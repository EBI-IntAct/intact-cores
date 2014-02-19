package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Parameter;
import psidev.psi.mi.jami.model.ParameterValue;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactParameter;
import uk.ac.ebi.intact.jami.synchronizer.*;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

/**
 * default finder/Synchronizer for parameter
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class ParameterSynchronizerTemplate<T extends Parameter, P extends AbstractIntactParameter> extends AbstractIntactDbSynchronizer<T, P>
implements ParameterSynchronizer<T,P>{

    public ParameterSynchronizerTemplate(SynchronizerContext context, Class<? extends P> paramClass){
        super(context, paramClass);
    }

    public P find(T object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(P object) throws FinderException, PersisterException, SynchronizerException {
        // type first
        CvTerm type = object.getType();
        object.setType(getContext().getParameterTypeSynchronizer().synchronize(type, true));

        // check unit
        if (object.getUnit() != null){
            CvTerm unit = object.getUnit();
            object.setUnit(getContext().getUnitSynchronizer().synchronize(unit, true));
        }
    }

    public void clearCache() {
        // nothing to do
    }

    @Override
    protected Object extractIdentifier(P object) {
        return object.getAc();
    }

    @Override
    protected P instantiateNewPersistentInstance(T object, Class<? extends P> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return intactClass.getConstructor(CvTerm.class, ParameterValue.class, CvTerm.class, BigDecimal.class).newInstance(object.getType(), object.getValue(), object.getUnit(), object.getUncertainty());
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<T, P>(this));
    }
}
