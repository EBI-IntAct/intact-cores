package uk.ac.ebi.intact.jami.synchronizer;

import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.comparator.CollectionComparator;
import psidev.psi.mi.jami.utils.comparator.interactor.InteractorPoolComparator;
import psidev.psi.mi.jami.utils.comparator.interactor.UnambiguousExactInteractorPoolComparator;
import psidev.psi.mi.jami.utils.comparator.interactor.UnambiguousInteractorPoolComparator;
import uk.ac.ebi.intact.jami.model.extension.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

/**
 * Synchronizer for IntAct interactor pools
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/01/14</pre>
 */

public class IntactInteractorPoolSynchronizer extends IntactInteractorBaseSynchronizer<InteractorPool,IntactInteractorPool>{

    public IntactInteractorPoolSynchronizer(EntityManager entityManager) {
        super(entityManager, IntactInteractorPool.class);
    }

    public IntactInteractorPoolSynchronizer(EntityManager entityManager, IntactDbSynchronizer<Alias, InteractorAlias> aliasSynchronizer, IntactDbSynchronizer<Annotation, InteractorAnnotation> annotationSynchronizer, IntactDbSynchronizer<Xref, InteractorXref> xrefSynchronizer, IntactDbSynchronizer<Organism, IntactOrganism> organismSynchronizer, IntactDbSynchronizer<CvTerm, IntactCvTerm> typeSynchronizer, IntactDbSynchronizer<Checksum, InteractorChecksum> checksumSynchronizer) {
        super(entityManager, IntactInteractorPool.class, aliasSynchronizer, annotationSynchronizer, xrefSynchronizer, organismSynchronizer, typeSynchronizer, checksumSynchronizer);
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
                    "where i.shortName = :name " +
                    "and i.organism is null " +
                    "and t.ac = :typeAc " +
                    "and size(i.interactors) =:interactorSize");
            query.setParameter("name", term.getShortName().trim().toLowerCase());
            query.setParameter("typeAc", existingType.getAc());
            query.setParameter("interactorSize", term.size());
        }
        else{
            query = getEntityManager().createQuery("select i from IntactInteractorPool i " +
                    "join i.interactorType as t " +
                    "join i.organism as o " +
                    "where i.shortName = :name " +
                    "and o.ac = :orgAc " +
                    "and t.ac = :typeAc " +
                    "and size(i.interactors) =:interactorSize");
            query.setParameter("name", term.getShortName().trim().toLowerCase());
            query.setParameter("orgAc", existingOrganism.getAc());
            query.setParameter("typeAc", existingType.getAc());
            query.setParameter("interactorSize", term.size());
        }
        return query.getResultList();
    }

    @Override
    protected void initialisePersistedObjectMap() {
        super.setPersistedObjects(new TreeMap<IntactInteractorPool, IntactInteractorPool>(new UnambiguousExactInteractorPoolComparator()));
    }
}
