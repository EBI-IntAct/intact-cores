package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.InteractorCloner;
import psidev.psi.mi.jami.utils.comparator.interactor.UnambiguousExactPolymerComparator;
import uk.ac.ebi.intact.jami.merger.IntactPolymerMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

/**
 * Synchronizer for IntAct polymers
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/01/14</pre>
 */

public class IntactPolymerSynchronizer<T extends Polymer, P extends IntactPolymer> extends IntactInteractorBaseSynchronizer<T,P> {
    public IntactPolymerSynchronizer(EntityManager entityManager, Class<P> intactClass) {
        super(entityManager, intactClass);
    }

    public IntactPolymerSynchronizer(EntityManager entityManager, Class<P> intactClass, IntactDbSynchronizer<Alias, InteractorAlias> aliasSynchronizer, IntactDbSynchronizer<Annotation, InteractorAnnotation> annotationSynchronizer, IntactDbSynchronizer<Xref, InteractorXref> xrefSynchronizer, IntactDbSynchronizer<Organism, IntactOrganism> organismSynchronizer, IntactDbSynchronizer<CvTerm, IntactCvTerm> typeSynchronizer, IntactDbSynchronizer<Checksum, InteractorChecksum> checksumSynchronizer) {
        super(entityManager, intactClass, aliasSynchronizer, annotationSynchronizer, xrefSynchronizer, organismSynchronizer, typeSynchronizer, checksumSynchronizer);
    }

    @Override
    protected P postFilter(T term, Collection<P> results) {
        Collection<P> filteredResults = new ArrayList<P>(results.size());
        for (P interactor : results){
            if (term.getSequence() != null && term.getSequence().equalsIgnoreCase(interactor.getSequence())){
                filteredResults.add(interactor);
            }
            // we accept null sequences when finding polymers
            else if (term.getSequence() == null){
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
    protected Collection<P> findByOtherProperties(T term, IntactCvTerm existingType, IntactOrganism existingOrganism) {
        Query query;
        if (existingOrganism == null){
            query = getEntityManager().createQuery("select i from "+getIntactClass()+" i " +
                    "join i.interactorType as t " +
                    "where i.organism is null " +
                    "and t.ac = :typeAc" + (term.getSequence() != null ? " and i.sequence = :seq" : ""));
            query.setParameter("typeAc", existingType.getAc());
            if (term.getSequence() != null){
                query.setParameter("seq", term.getSequence());
            }
        }
        else{
            query = getEntityManager().createQuery("select i from "+getIntactClass()+" i " +
                    "join i.interactorType as t " +
                    "join i.organism as o " +
                    "where o.ac = :orgAc " +
                    "and t.ac = :typeAc"+ (term.getSequence() != null ? " and i.sequence = :seq" : ""));
            query.setParameter("orgAc", existingOrganism.getAc());
            query.setParameter("typeAc", existingType.getAc());
            if (term.getSequence() != null){
                query.setParameter("seq", term.getSequence());
            }
        }
        return query.getResultList();
    }

    @Override
    protected void initialisePersistedObjectMap() {
        super.setPersistedObjects(new TreeMap<T, P>(new UnambiguousExactPolymerComparator()));
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new IntactPolymerMergerEnrichOnly(this));
    }

    @Override
    protected P instantiateNewPersistentInstance(T object, Class<? extends P> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        P newInteractor = intactClass.getConstructor(String.class).newInstance(object.getShortName());
        InteractorCloner.copyAndOverrideBasicPolymerProperties(object, newInteractor);
        return newInteractor;
    }
}
