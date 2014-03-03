package uk.ac.ebi.intact.jami.synchronizer.impl;

import psidev.psi.mi.jami.model.Allostery;
import psidev.psi.mi.jami.model.CooperativeEffect;
import psidev.psi.mi.jami.model.Preassembly;
import psidev.psi.mi.jami.utils.clone.CooperativeEffectCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.IntactDbMerger;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactCooperativeEffect;
import uk.ac.ebi.intact.jami.model.extension.IntactAllostery;
import uk.ac.ebi.intact.jami.model.extension.IntactPreassembly;
import uk.ac.ebi.intact.jami.synchronizer.CooperativeEffectSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

/**
 * Default finder/synchronizer for cooperative effect
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class CompositeCooperativeEffectSynchronizer implements CooperativeEffectSynchronizer<CooperativeEffect, AbstractIntactCooperativeEffect>{

    private SynchronizerContext context;

    public CompositeCooperativeEffectSynchronizer(SynchronizerContext context) {
        if (context == null){
            throw new IllegalArgumentException("An IntAct database synchronizer needs a non null synchronizer context");
        }
        this.context = context;
    }

    public AbstractIntactCooperativeEffect find(CooperativeEffect object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(AbstractIntactCooperativeEffect object) throws FinderException, PersisterException, SynchronizerException {
        // preassembly
        if (object instanceof IntactPreassembly){
            this.context.getPreAssemblySynchronizer().synchronizeProperties((IntactPreassembly)object);
        }
        // allostery
        else {
            this.context.getAllosterySynchronizer().synchronizeProperties((IntactAllostery)object);
        }
    }

    public AbstractIntactCooperativeEffect persist(AbstractIntactCooperativeEffect object) throws FinderException, PersisterException, SynchronizerException {
        // preassembly
        if (object instanceof IntactPreassembly){
            return this.context.getPreAssemblySynchronizer().persist((IntactPreassembly)object);
        }
        // allostery
        else {
            return this.context.getAllosterySynchronizer().persist((IntactAllostery)object);
        }
    }

    public AbstractIntactCooperativeEffect synchronize(CooperativeEffect object, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        // preassembly
        if (object instanceof Preassembly){
            return this.context.getPreAssemblySynchronizer().synchronize((Preassembly) object, persist);
        }
        // allostery
        else if (object instanceof Allostery){
            return this.context.getAllosterySynchronizer().synchronize((Allostery) object, persist);
        }
        // consider that as preassembly in intact
        else{
            IntactPreassembly preassembly = new IntactPreassembly();
            CooperativeEffectCloner.copyAndOverrideBasicCooperativeEffectProperties(object, preassembly);

            return this.context.getPreAssemblySynchronizer().synchronize(preassembly, persist);
        }
    }

    public void clearCache() {
        // nothing to do
    }

    public IntactDbMerger<CooperativeEffect, AbstractIntactCooperativeEffect> getIntactMerger() {
        return null;
    }

    public void setIntactMerger(IntactDbMerger<CooperativeEffect, AbstractIntactCooperativeEffect> intactMerger) {
        throw new UnsupportedOperationException("The cooperative effect synchronizer does not support this method as it is a composite synchronizer");
    }

    public Class<? extends AbstractIntactCooperativeEffect> getIntactClass() {
        return AbstractIntactCooperativeEffect.class;
    }

    public void setIntactClass(Class<? extends AbstractIntactCooperativeEffect> intactClass) {
        throw new UnsupportedOperationException("The cooperative effect synchronizer does not support this method as it is a composite synchronizer");
    }

    public boolean delete(CooperativeEffect object) {
        // preassembly
        if (object instanceof Preassembly){
            return this.context.getPreAssemblySynchronizer().delete((Preassembly) object);
        }
        // allostery
        else if (object instanceof Allostery){
            return this.context.getAllosterySynchronizer().delete((Allostery) object);
        }
        // consider that as preassembly in intact
        else{
            return false;
        }
    }
}
