package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.collections.CollectionUtils;
import psidev.psi.mi.jami.model.Interactor;
import psidev.psi.mi.jami.model.InteractorPool;
import psidev.psi.mi.jami.utils.clone.InteractorCloner;
import psidev.psi.mi.jami.utils.comparator.interactor.DefaultInteractorPoolComparator;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.InteractorPoolMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractorPool;
import uk.ac.ebi.intact.jami.model.extension.IntactOrganism;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.comparator.IntactInteractorPoolComparator;

import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Synchronizer for IntAct interactor pools
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/01/14</pre>
 */

public class InteractorPoolSynchronizer extends InteractorSynchronizerTemplate<InteractorPool,IntactInteractorPool> {

    public InteractorPoolSynchronizer(SynchronizerContext context) {
        super(context, IntactInteractorPool.class);
    }

    @Override
    public void synchronizeProperties(IntactInteractorPool intactInteractor) throws FinderException, PersisterException, SynchronizerException {
        // synchronize subInteractors if not done
        prepareInteractors(intactInteractor, true);

        super.synchronizeProperties(intactInteractor);
    }

    @Override
    public void convertPersistableProperties(IntactInteractorPool intactInteractor) throws FinderException, PersisterException, SynchronizerException {
        // synchronize subInteractors if not done
        prepareInteractors(intactInteractor, false);

        super.convertPersistableProperties(intactInteractor);
    }

    protected void prepareInteractors(IntactInteractorPool intactInteractor, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactInteractor.areInteractorsInitialized()){
            List<Interactor> interactorToPersist = new ArrayList<Interactor>(intactInteractor);
            Set<Interactor> processedInteractors = new HashSet<Interactor>(intactInteractor.size());
            intactInteractor.clear();
            for (Interactor interactor : interactorToPersist){
                if (interactor != intactInteractor){
                    Interactor interactorCheck = enableSynchronization ?
                            getContext().getInteractorSynchronizer().synchronize(interactor, true) :
                            getContext().getInteractorSynchronizer().convertToPersistentObject(intactInteractor);
                    // we have a different instance because needed to be synchronized
                    if (processedInteractors.add(interactorCheck)){
                        intactInteractor.add(interactorCheck);
                    }
                }
            }
        }
    }

    @Override
    protected IntactInteractorPool postFilter(InteractorPool term, Collection<IntactInteractorPool> results) {
        Collection<IntactInteractorPool> filteredResults = new ArrayList<IntactInteractorPool>(results.size());
        for (IntactInteractorPool interactor : results){
            if (DefaultInteractorPoolComparator.areEquals(term, interactor)){
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
    protected Collection<IntactInteractorPool> postFilterAll(InteractorPool term, Collection<IntactInteractorPool> results) {
        Collection<IntactInteractorPool> filteredResults = new ArrayList<IntactInteractorPool>(results.size());
        for (IntactInteractorPool interactor : results){
            if (DefaultInteractorPoolComparator.areEquals(term, interactor)){
                filteredResults.add(interactor);
            }
        }

        return filteredResults;
    }

    @Override
    protected Collection<String> postFilterAllAcs(InteractorPool term, Collection<IntactInteractorPool> results) {
        Collection<String> filteredResults = new ArrayList<String>(results.size());
        for (IntactInteractorPool interactor : results){
            if (DefaultInteractorPoolComparator.areEquals(term, interactor) && interactor.getAc()!= null){
                filteredResults.add(interactor.getAc());
            }
        }

        return filteredResults;
    }

    @Override
    protected Collection<IntactInteractorPool> findByOtherProperties(InteractorPool term, Collection<String> existingTypes, Collection<String> existingOrganisms) {
        Query query;
        if (existingOrganisms.isEmpty()){
            query = getEntityManager().createQuery("select i from IntactInteractorPool i " +
                    "join i.interactorType as t " +
                    "where i.organism is null " +
                    "and t.ac in (:typeAc) " +
                    "and size(i.interactors) =:interactorSize");
            query.setParameter("typeAc", existingTypes);
            query.setParameter("interactorSize", term.size());
        }
        else{
            query = getEntityManager().createQuery("select i from IntactInteractorPool i " +
                    "join i.interactorType as t " +
                    "join i.organism as o " +
                    "where o.ac in (:orgAc) " +
                    "and t.ac in (:typeAc) " +
                    "and size(i.interactors) =:interactorSize");
            query.setParameter("orgAc", existingOrganisms);
            query.setParameter("typeAc", existingTypes);
            query.setParameter("interactorSize", term.size());
        }
        return query.getResultList();
    }

    @Override
    protected void initialisePersistedObjectMap() {
        super.initialisePersistedObjectMap(new IntactInteractorPoolComparator());
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new InteractorPoolMergerEnrichOnly(this));
    }

    @Override
    protected IntactInteractorPool instantiateNewPersistentInstance(InteractorPool object, Class<? extends IntactInteractorPool> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactInteractorPool newInteractor = new IntactInteractorPool(object.getShortName());
        InteractorCloner.copyAndOverrideBasicInteractorPoolProperties(object, newInteractor);
        return newInteractor;
    }

    @Override
    protected void synchronizePropertiesBeforeCacheMerge(IntactInteractorPool mergedObject, IntactInteractorPool originalObject) throws SynchronizerException, PersisterException, FinderException {
        super.synchronizePropertiesBeforeCacheMerge(mergedObject, originalObject);
        // synchronize subInteractors if not done
        if (!CollectionUtils.isEqualCollection(mergedObject, originalObject)){
            prepareInteractors(mergedObject, true);
        }
    }
}
