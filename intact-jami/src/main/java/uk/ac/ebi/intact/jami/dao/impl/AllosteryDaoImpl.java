package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.Allostery;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.dao.AllosteryDao;
import uk.ac.ebi.intact.jami.model.extension.IntactAllostery;
import uk.ac.ebi.intact.jami.synchronizer.IntactAllosteryBaseSynchronizer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of cooperative effect dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23/01/14</pre>
 */
@Repository
public class AllosteryDaoImpl extends CooperativeEffectDaoImpl<Allostery, IntactAllostery> implements AllosteryDao {

    protected AllosteryDaoImpl() {
        super(IntactAllostery.class);
    }

    public AllosteryDaoImpl(EntityManager entityManager) {
        super(IntactAllostery.class, entityManager);
    }

    public Collection<IntactAllostery> getByAllostericMoleculeAc(String ac) {
        Query query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                "join f.allostericMolecule as c " +
                "where c.ac = :moleculeAc");
        query.setParameter("moleculeAc", ac);
        return query.getResultList();
    }

    public Collection<IntactAllostery> getByMechanism(String name, String mi) {
        Query query;
        if (mi != null){
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "join f.mechanism as c " +
                    "join c.persistentXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", mi);
        }
        else{
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "join f.mechanism as c " +
                    "where c.shortName = :name");
            query.setParameter("name", name);
        }
        return query.getResultList();
    }

    public Collection<IntactAllostery> getByAllosteryType(String name, String mi) {
        Query query;
        if (mi != null){
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "join f.allosteryType as r " +
                    "join r.persistentXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", mi);
        }
        else{
            query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                    "join f.allosteryType as r " +
                    "where r.shortName = :name");
            query.setParameter("name", name);
        }
        return query.getResultList();
    }

    public Collection<IntactAllostery> getByMoleculeEffectorAc(String ac) {
        Query query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                "join f.allostericEffector as c " +
                "where c.ac = :moleculeAc");
        query.setParameter("moleculeAc", ac);
        return query.getResultList();
    }

    public Collection<IntactAllostery> getByFeatureModificationEffectorAc(String ac) {
        Query query = getEntityManager().createQuery("select f from "+getEntityClass()+" f "  +
                "join f.allostericEffector as c " +
                "where c.ac = :featureAc");
        query.setParameter("featureAc", ac);
        return query.getResultList();
    }

    @Override
    protected void initialiseDbSynchronizer() {
        super.setDbSynchronizer(new IntactAllosteryBaseSynchronizer(getEntityManager()));
    }
}
