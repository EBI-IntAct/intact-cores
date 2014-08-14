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
 * It does not cache persisted parameters. It only synchronize the parameter type and parameter unit (with persist = true) to make sure that the parameter type
 * and unit are persisted before so the parameter can be persisted
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
        prepareType(object, true);

        // check unit
        prepareUnit(object, true);
    }

    protected void prepareUnit(P object, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (object.getUnit() != null){
            CvTerm unit = object.getUnit();
            object.setUnit(enableSynchronization ?
                    getContext().getUnitSynchronizer().synchronize(unit, true) :
                    getContext().getUnitSynchronizer().convertToPersistentObject(unit));
        }
    }

    protected void prepareType(P object, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        CvTerm type = object.getType();
        object.setType(enableSynchronization ?
                getContext().getParameterTypeSynchronizer().synchronize(type, true) :
                getContext().getParameterTypeSynchronizer().convertToPersistentObject(type));
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
    protected void storeInCache(T originalObject, P persistentObject, P existingInstance) {
        // nothing to do
    }

    @Override
    protected P fetchObjectFromCache(T object) {
        return null;
    }

    @Override
    protected boolean isObjectStoredInCache(T object) {
        return false;
    }

    @Override
    protected boolean containsObjectInstance(T object) {
        return false;
    }

    @Override
    protected void removeObjectInstanceFromIdentityCache(T object) {
        // nothing to do
    }

    @Override
    protected P fetchMatchingObjectFromIdentityCache(T object) {
        return null;
    }

    @Override
    protected void convertPersistableProperties(P object) throws SynchronizerException, PersisterException, FinderException {
        // type first
        prepareType(object, false);

        // check unit
        prepareUnit(object, false);
    }

    @Override
    protected void storeObjectInIdentityCache(T originalObject, P persistableObject) {
        // nothing to do here
    }

    @Override
    protected boolean isObjectDirty(T originalObject) {
        return false;
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<T, P>(this));
    }
}
