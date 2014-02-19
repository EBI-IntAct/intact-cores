package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.CausalRelationship;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.context.DefaultSynchronizerContext;
import uk.ac.ebi.intact.jami.dao.CausalRelationshipDao;
import uk.ac.ebi.intact.jami.model.extension.IntactCausalRelationship;
import uk.ac.ebi.intact.jami.synchronizer.impl.CausalRelationchipSynchronizer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of causal relationship dao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/01/14</pre>
 */
@Repository
public class CausalRelationshipDaoImpl extends AbstractIntactBaseDao<CausalRelationship, IntactCausalRelationship> implements CausalRelationshipDao {

    public CausalRelationshipDaoImpl() {
        super(IntactCausalRelationship.class);
    }

    public CausalRelationshipDaoImpl(EntityManager entityManager) {
        super(IntactCausalRelationship.class, entityManager);
    }

    public Collection<IntactCausalRelationship> getByRelationType(String typeName, String typeMI) {
        Query query;
        if (typeMI != null){
            query = getEntityManager().createQuery("select c from IntactCausalRelationship c " +
                    "join c.relationType as t " +
                    "join t.persistentXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and x.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", typeMI);
        }
        else{
            query = getEntityManager().createQuery("select c from IntactCausalRelationship c " +
                    "join c.relationType as t " +
                    "where t.shortName = :unitName");
            query.setParameter("unitName", typeName);
        }
        return query.getResultList();
    }

    public Collection<IntactCausalRelationship> getByParentAc(String parentAc) {
        Query query = getEntityManager().createQuery("select c from IntactCausalRelationship c " +
                "join c.parent as p " +
                "where p.ac = :ac ");
        query.setParameter("ac",parentAc);
        return query.getResultList();
    }

    public Collection<IntactCausalRelationship> getByTargetAc(String parentAc) {
        Query query = getEntityManager().createQuery("select c from IntactCausalRelationship c " +
                "join c.target as t " +
                "where t.ac = :ac ");
        query.setParameter("ac",parentAc);
        return query.getResultList();
    }

    public Collection<IntactCausalRelationship> getByParentAndTargetAc(String parentAc, String targetAc) {
        Query query = getEntityManager().createQuery("select c from IntactCausalRelationship c " +
                "join c.parent as p " +
                "join c.target as t " +
                "where p.ac = :ac " +
                "and t.ac = :targetAc");
        query.setParameter("ac",parentAc);
        query.setParameter("targetAc",targetAc);
        return query.getResultList();
    }

    @Override
    protected void initialiseDbSynchronizer() {
        super.setDbSynchronizer(new CausalRelationchipSynchronizer(new DefaultSynchronizerContext(getEntityManager())));
    }
}