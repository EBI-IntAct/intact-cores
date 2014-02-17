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
    }

    public AbstractIntactCooperativeEffect find(CooperativeEffect object) throws FinderException {
        return null;
    }

    public void synchronizeProperties(AbstractIntactCooperativeEffect object) throws FinderException, PersisterException, SynchronizerException {
        // preassembly
        if (object instanceof IntactPreassembly){
            getPreAssemblySynchronizer().synchronizeProperties((IntactPreassembly)object);
        }
        // allostery
        else {
            getAllosterySynchronizer().synchronizeProperties((IntactAllostery)object);
        }
    }

    @Override
    public AbstractIntactCooperativeEffect persist(AbstractIntactCooperativeEffect object) throws FinderException, PersisterException, SynchronizerException {
        // preassembly
        if (object instanceof IntactPreassembly){
            return getPreAssemblySynchronizer().persist((IntactPreassembly)object);
        }
        // allostery
        else {
            return getAllosterySynchronizer().persist((IntactAllostery)object);
        }
    }

    @Override
    public AbstractIntactCooperativeEffect synchronize(CooperativeEffect object, boolean persist) throws FinderException, PersisterException, SynchronizerException {
        // preassembly
        if (object instanceof Preassembly){
            return getPreAssemblySynchronizer().synchronize((IntactPreassembly) object, persist);
        }
        // allostery
        else if (object instanceof Allostery){
            return getAllosterySynchronizer().synchronize((IntactAllostery) object, persist);
        }
        // consider that as preassembly in intact
        else{
            try {
                IntactPreassembly preassembly = (IntactPreassembly)instantiateNewPersistentInstance(object, getIntactClass());
                return getPreAssemblySynchronizer().synchronize(preassembly, persist);
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
        getPreAssemblySynchronizer().clearCache();
        getAllosterySynchronizer().clearCache();
    }

    public IntactDbSynchronizer<Preassembly, IntactPreassembly> getPreAssemblySynchronizer() {
        if (this.preAssemblySynchronizer == null){
            this.preAssemblySynchronizer = new IntactCooperativeEffectBaseSynchronizer<Preassembly, IntactPreassembly>(getEntityManager(), IntactPreassembly.class);
        }
        return preAssemblySynchronizer;
    }

    public void setPreAssemblySynchronizer(IntactDbSynchronizer<Preassembly, IntactPreassembly> preAssemblySynchronizer) {
        this.preAssemblySynchronizer = preAssemblySynchronizer;
    }

    public IntactDbSynchronizer<Allostery, IntactAllostery> getAllosterySynchronizer() {
        if (this.allosterySynchronizer == null){
            this.allosterySynchronizer = new IntactAllosterySynchronizer(getEntityManager());
        }
        return allosterySynchronizer;
    }

    public void setAllosterySynchronizer(IntactDbSynchronizer<Allostery, IntactAllostery> allosterySynchronizer) {
        this.allosterySynchronizer = allosterySynchronizer;
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
