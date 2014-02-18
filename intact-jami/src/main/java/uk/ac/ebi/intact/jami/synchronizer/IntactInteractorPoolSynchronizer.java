package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.InteractorCloner;
import psidev.psi.mi.jami.utils.comparator.interactor.UnambiguousExactInteractorPoolComparator;
import psidev.psi.mi.jami.utils.comparator.interactor.UnambiguousInteractorPoolComparator;
import uk.ac.ebi.intact.jami.merger.IntactInteractorBaseMergerEnrichOnly;
import uk.ac.ebi.intact.jami.merger.IntactInteractorPoolMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

/**
 * Synchronizer for IntAct interactor pools
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/01/14</pre>
 */

public class IntactInteractorPoolSynchronizer extends IntactInteractorBaseSynchronizer<InteractorPool,IntactInteractorPool> {

    private IntactDbSynchronizer<Interactor, IntactInteractor> interactorSynchronizer;

    public IntactInteractorPoolSynchronizer(EntityManager entityManager) {
        super(entityManager, IntactInteractorPool.class);
    }

    @Override
    public void synchronizeProperties(IntactInteractorPool intactInteractor) throws FinderException, PersisterException, SynchronizerException {
        super.synchronizeProperties(intactInteractor);

        // then synchronize subInteractors if not done
        prepareInteractors(intactInteractor);
    }

    public IntactDbSynchronizer<Interactor, IntactInteractor> getInteractorSynchronizer() {
        if (this.interactorSynchronizer == null){
            this.interactorSynchronizer = new IntactInteractorSynchronizer(getEntityManager());
            ((IntactInteractorSynchronizer)this.interactorSynchronizer).setInteractorPoolSynchronizer(this);
        }
        return interactorSynchronizer;
    }

    public void setInteractorSynchronizer(IntactDbSynchronizer<Interactor, IntactInteractor> interactorSynchronizer) {
        this.interactorSynchronizer = interactorSynchronizer;
    }

    protected void prepareInteractors(IntactInteractorPool intactInteractor) throws FinderException, PersisterException, SynchronizerException {
        if (intactInteractor.areInteractorsInitialized()){
            List<Interactor> interactorToPersist = new ArrayList<Interactor>(intactInteractor);
            for (Interactor interactor : interactorToPersist){
                Interactor interactorCheck = getInteractorSynchronizer().synchronize(interactor, false);
                // we have a different instance because needed to be synchronized
                if (interactorCheck != interactor){
                    intactInteractor.remove(interactor);
                    intactInteractor.add(interactorCheck);
                }
            }
        }
    }

    @Override
    protected IntactInteractorPool postFilter(InteractorPool term, Collection<IntactInteractorPool> results) {
        Collection<IntactInteractorPool> filteredResults = new ArrayList<IntactInteractorPool>(results.size());
        for (IntactInteractorPool interactor : results){
            if (UnambiguousInteractorPoolComparator.areEquals(term, interactor)){
                filteredResults.add(interactor);
            }
        }

        if (filteredResults.size() == 1){
            return filteredResults.iterator().next();
        }
        else{
            return null;
        }
    }

    @Override
    protected Collection<IntactInteractorPool> findByOtherProperties(InteractorPool term, IntactCvTerm existingType, IntactOrganism existingOrganism) {
        Query query;
        if (existingOrganism == null){
            query = getEntityManager().createQuery("select i from IntactInteractorPool i " +
                    "join i.interactorType as t " +
                    "where i.organism is null " +
                    "and t.ac = :typeAc " +
                    "and size(i.interactors) =:interactorSize");
            query.setParameter("typeAc", existingType.getAc());
            query.setParameter("interactorSize", term.size());
        }
        else{
            query = getEntityManager().createQuery("select i from IntactInteractorPool i " +
                    "join i.interactorType as t " +
                    "join i.organism as o " +
                    "where o.ac = :orgAc " +
                    "and t.ac = :typeAc " +
                    "and size(i.interactors) =:interactorSize");
            query.setParameter("orgAc", existingOrganism.getAc());
            query.setParameter("typeAc", existingType.getAc());
            query.setParameter("interactorSize", term.size());
        }
        return query.getResultList();
    }

    @Override
    protected void initialisePersistedObjectMap() {
        super.setPersistedObjects(new TreeMap<InteractorPool, IntactInteractorPool>(new UnambiguousExactInteractorPoolComparator()));
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactInteractorPoolMergerEnrichOnly(this));
    }

    @Override
    protected IntactInteractorPool instantiateNewPersistentInstance(InteractorPool object, Class<? extends IntactInteractorPool> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactInteractorPool newInteractor = new IntactInteractorPool(object.getShortName());
        InteractorCloner.copyAndOverrideBasicInteractorPoolProperties(object, newInteractor);
        return newInteractor;
    }

    @Override
    public void clearCache() {
        super.clearCache();
        getInteractorSynchronizer().clearCache();
    }
}
