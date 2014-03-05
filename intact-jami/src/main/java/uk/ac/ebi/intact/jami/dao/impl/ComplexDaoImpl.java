package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.Complex;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.InteractorPool;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.ComplexDao;
import uk.ac.ebi.intact.jami.dao.InteractorPoolDao;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactChecksum;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractorPool;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.impl.ComplexSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.impl.InteractorPoolSynchronizer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of complexDao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31/01/14</pre>
 */
public class ComplexDaoImpl extends InteractorDaoImpl<Complex,IntactComplex> implements ComplexDao{

    public ComplexDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super(IntactComplex.class, entityManager, context);
    }

    @Override
    public IntactDbSynchronizer<Complex, IntactComplex> getDbSynchronizer() {
        return getSynchronizerContext().getComplexSynchronizer();
    }

    public Collection<IntactComplex> getByInteractionType(String typeName, String typeMI, int first, int max) {
        Query query;
        if (typeName == null && typeMI == null){
            query = getEntityManager().createQuery("select f from IntactComplex f "  +
                    "where f.interactionType is null order by f.ac");
        }
        else if (typeMI != null){
            query = getEntityManager().createQuery("select distinct f from IntactComplex f "  +
                    "join f.interactionType as t " +
                    "join t.persistentXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi order by f.ac");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", typeMI);
        }
        else{
            query = getEntityManager().createQuery("select f from IntactComplex f "  +
                    "join f.interactionType as t " +
                    "where t.shortName = :typeName order by f.ac");
            query.setParameter("typeName", typeName);
        }
        return query.getResultList();
    }

    public Collection<IntactComplex> getByLifecycleEvent(String evtName, int first, int max) {
        Query query;
        if (evtName != null){
            query = getEntityManager().createQuery("select p from IntactComplex p "  +
                    "where p.lifecycleEvents is empty order by p.ac");
        }
        else{
            query = getEntityManager().createQuery("select distinct p from IntactComplex p "  +
                    "join p.lifecycleEvents as l "  +
                    "join l.event as e "  +
                    "where e.shortName = :name order by p.ac");
            query.setParameter("name", evtName);
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<IntactComplex> getByStatus(String statusName, int first, int max) {
        Query query;
        if (statusName != null){
            query = getEntityManager().createQuery("select p from IntactComplex p "  +
                    "where p.status is null order by p.ac");
        }
        else{
            query = getEntityManager().createQuery("select p from IntactComplex p "  +
                    "join p.status as s "  +
                    "where s.shortName = :name order by p.ac");
            query.setParameter("name", statusName);
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }
}
