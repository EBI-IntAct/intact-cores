package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.Allostery;
import psidev.psi.mi.jami.model.CooperativeEffect;
import psidev.psi.mi.jami.model.Preassembly;
import psidev.psi.mi.jami.utils.clone.CooperativeEffectCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactCooperativeEffect;
import uk.ac.ebi.intact.jami.model.extension.IntactAllostery;
import uk.ac.ebi.intact.jami.model.extension.IntactPreassembly;
import uk.ac.ebi.intact.jami.synchronizer.*;

import java.lang.reflect.InvocationTargetException;

/**
 * Default finder/synchronizer for cooperative effect
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class CompositeCooperativeEffectSynchronizer extends AbstractIntactDbSynchronizer<CooperativeEffect, AbstractIntactCooperativeEffect>
implements CooperativeEffectSynchronizer<CooperativeEffect, AbstractIntactCooperativeEffect>{

    public CompositeCooperativeEffectSynchronizer(SynchronizerContext context) {
        super(context, AbstractIntactCooperativeEffect.class);
    }

    public AbstractIntactCooperativeEffect find(CooperativeEffect object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(AbstractIntactCooperativeEffect object) throws FinderException, PersisterException, SynchronizerException {
        // preassembly
        if (object instanceof IntactPreassembly){
            getContext().getPreAssemblySynchronizer().synchronizeProperties((IntactPreassembly)object);
        }
        // allostery
        else {
            getContext().getAllosterySynchronizer().synchronizeProperties((IntactAllostery)object);
        }
    }

    @Override
    public AbstractIntactCooperativeEffect persist(AbstractIntactCooperativeEffect object) throws FinderException, PersisterException, SynchronizerException {
        // preassembly
        if (object instanceof IntactPreassembly){
            return getContext().getPreAssemblySynchronizer().persist((IntactPreassembly)object);
        }
        // allostery
        else {
            return getContext().getAllosterySynchronizer().persist((IntactAllostery)object);
        }
    }

    @Override
    public AbstractIntactCooperativeEffect synchronize(CooperativeEffect object, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        // preassembly
        if (object instanceof Preassembly){
            return getContext().getPreAssemblySynchronizer().synchronize((IntactPreassembly) object, persist);
        }
        // allostery
        else if (object instanceof Allostery){
            return getContext().getAllosterySynchronizer().synchronize((IntactAllostery) object, persist);
        }
        // consider that as preassembly in intact
        else{
            try {
                IntactPreassembly preassembly = (IntactPreassembly)instantiateNewPersistentInstance(object, getIntactClass());
                return getContext().getPreAssemblySynchronizer().synchronize(preassembly, persist);
            } catch (InstantiationException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+IntactPreassembly.class, e);
            } catch (IllegalAccessException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+IntactPreassembly.class, e);
            } catch (InvocationTargetException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+IntactPreassembly.class, e);
            } catch (NoSuchMethodException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+IntactPreassembly.class, e);
            }
        }
    }

    public void clearCache() {
        // nothing to do
    }

    @Override
    protected Object extractIdentifier(AbstractIntactCooperativeEffect object) {
        return object.getId();
    }

    @Override
    protected AbstractIntactCooperativeEffect instantiateNewPersistentInstance(CooperativeEffect object, Class<? extends AbstractIntactCooperativeEffect> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactPreassembly newEffect = new IntactPreassembly(object.getOutCome());
        CooperativeEffectCloner.copyAndOverrideBasicCooperativeEffectProperties(object, newEffect);
        return newEffect;
    }

    @Override
    protected void storeInCache(CooperativeEffect originalObject, AbstractIntactCooperativeEffect persistentObject, AbstractIntactCooperativeEffect existingInstance) {
        // nothing to do
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactDbMergerIgnoringPersistentObject<CooperativeEffect, AbstractIntactCooperativeEffect>(this));
    }
}
