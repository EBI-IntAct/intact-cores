package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.Allostery;
import psidev.psi.mi.jami.model.CooperativeEffect;
import psidev.psi.mi.jami.model.Preassembly;
import psidev.psi.mi.jami.utils.clone.CooperativeEffectCloner;
import uk.ac.ebi.intact.jami.merger.IntactMergerIgnoringPersistentObject;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactCooperativeEffect;
import uk.ac.ebi.intact.jami.model.extension.IntactAllostery;
import uk.ac.ebi.intact.jami.model.extension.IntactPreassembly;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;

/**
 * Default finder/synchronizer for cooperative effect
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class IntactCooperativeEffectSynchronizer extends AbstractIntactDbSynchronizer<CooperativeEffect, AbstractIntactCooperativeEffect>{
    private IntactDbSynchronizer<Preassembly, IntactPreassembly> preAssemblySynchronizer;
    private IntactDbSynchronizer<Allostery, IntactAllostery> allosterySynchronizer;

    private static final Log log = LogFactory.getLog(IntactCooperativeEffectSynchronizer.class);

    public IntactCooperativeEffectSynchronizer(EntityManager entityManager) {
        super(entityManager, AbstractIntactCooperativeEffect.class);
        this.preAssemblySynchronizer = new IntactCooperativeEffectBaseSynchronizer<Preassembly, IntactPreassembly>(entityManager, IntactPreassembly.class);
        this.allosterySynchronizer = new IntactAllosteryBaseSynchronizer(entityManager);
    }

    public IntactCooperativeEffectSynchronizer(EntityManager entityManager,
                                               IntactDbSynchronizer<Preassembly, IntactPreassembly> preAssemblySynchronizer,
                                               IntactDbSynchronizer<Allostery, IntactAllostery> allosterySynchronizer) {
        super(entityManager, AbstractIntactCooperativeEffect.class);
        this.preAssemblySynchronizer = preAssemblySynchronizer != null ? preAssemblySynchronizer : new IntactCooperativeEffectBaseSynchronizer<Preassembly, IntactPreassembly>(entityManager, IntactPreassembly.class);
        this.allosterySynchronizer = allosterySynchronizer != null ? allosterySynchronizer : new IntactAllosteryBaseSynchronizer(entityManager);
    }

    public AbstractIntactCooperativeEffect find(CooperativeEffect object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(AbstractIntactCooperativeEffect object) throws FinderException, PersisterException, SynchronizerException {
        // preassembly
        if (object instanceof IntactPreassembly){
            this.preAssemblySynchronizer.synchronizeProperties((IntactPreassembly)object);
        }
        // allostery
        else {
            this.allosterySynchronizer.synchronizeProperties((IntactAllostery)object);
        }
    }

    @Override
    public AbstractIntactCooperativeEffect persist(AbstractIntactCooperativeEffect object) throws FinderException, PersisterException, SynchronizerException {
        // preassembly
        if (object instanceof IntactPreassembly){
            return this.preAssemblySynchronizer.persist((IntactPreassembly)object);
        }
        // allostery
        else {
            return this.allosterySynchronizer.persist((IntactAllostery)object);
        }
    }

    @Override
    public AbstractIntactCooperativeEffect synchronize(CooperativeEffect object, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        // preassembly
        if (object instanceof Preassembly){
            return this.preAssemblySynchronizer.synchronize((IntactPreassembly) object, persist);
        }
        // allostery
        else if (object instanceof Allostery){
            return this.allosterySynchronizer.synchronize((IntactAllostery) object, persist);
        }
        // consider that as preassembly in intact
        else{
            try {
                IntactPreassembly preassembly = (IntactPreassembly)instantiateNewPersistentInstance(object, getIntactClass());
                return this.preAssemblySynchronizer.synchronize(preassembly, persist);
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
        this.preAssemblySynchronizer.clearCache();
        this.allosterySynchronizer.clearCache();
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
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactMergerIgnoringPersistentObject<CooperativeEffect, AbstractIntactCooperativeEffect>(this));
    }
}
