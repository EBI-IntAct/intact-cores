package uk.ac.ebi.intact.jami.dao.impl;

import org.springframework.stereotype.Repository;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Parameter;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.dao.ParameterDao;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactParameter;
import uk.ac.ebi.intact.jami.synchronizer.impl.ParameterSynchronizerTemplate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of parameter DAO
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/01/14</pre>
 */
@Repository
public class ParameterDaoImpl<P extends AbstractIntactParameter> extends AbstractIntactBaseDao<Parameter, P> implements ParameterDao<P> {

    public ParameterDaoImpl() {
        super((Class<P>)AbstractIntactParameter.class);
    }

    public ParameterDaoImpl(Class<P> entityClass) {
        super(entityClass);
    }

    public ParameterDaoImpl(Class<P> entityClass, EntityManager entityManager) {
        super(entityClass, entityManager);
    }

    public Collection<P> getByType(String typeName, String typeMI) {
        Query query;
        if (typeMI != null){
            query = getEntityManager().createQuery("select p from "+getEntityClass()+" p " +
                    "join p.type as t " +
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
            query = getEntityManager().createQuery("select p from "+getEntityClass()+" p " +
                    "join p.type as t " +
                    "where t.shortName = :paramName");
            query.setParameter("paramName", typeName);
        }
        return query.getResultList();
    }

    public Collection<P> getByUnit(String unitName, String unitMI) {
        Query query;
        if (unitMI == null && unitName == null){
            query = getEntityManager().createQuery("select p from "+getEntityClass()+" p " +
                    "where p.unit is null");
        }
        else if (unitMI != null){
            query = getEntityManager().createQuery("select p from "+getEntityClass()+" p " +
                    "join p.unit as u " +
                    "join u.persistentXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and x.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", unitMI);
        }
        else{
            query = getEntityManager().createQuery("select p from "+getEntityClass()+" p " +
                    "join p.unit as u " +
                    "where u.shortName = :unitName");
            query.setParameter("unitName", unitName);
        }
        return query.getResultList();
    }

    public Collection<P> getByTypeAndUnit(String typeName, String typeMI, String unitName, String unitMI) {
        Query query;
        if (typeMI != null){
            if (unitMI == null && unitName == null){
                query = getEntityManager().createQuery("select p from "+getEntityClass()+" p " +
                        "join p.type as t " +
                        "join t.persistentXrefs as x " +
                        "join x.database as d " +
                        "join x.qualifier as q " +
                        "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and x.id = :mi " +
                        "and p.unit is null");
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", typeMI);
            }
            else if (unitMI != null){
                query = getEntityManager().createQuery("select p from "+getEntityClass()+" p " +
                        "join p.type as t " +
                        "join t.persistentXrefs as x " +
                        "join x.database as d " +
                        "join x.qualifier as q " +
                        "join p.unit as u " +
                        "join u.persistentXrefs as x2 " +
                        "join x2.database as d2 " +
                        "join x2.qualifier as q2 " +
                        "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and x.id = :mi " +
                        "and (q2.shortName = :identity or q2.shortName = :secondaryAc) "+
                        "and d2.shortName = :psimi "+
                        "and x2.id = :mi2 ");
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", typeMI);
                query.setParameter("mi2", unitMI);
            }
            else{
                query = getEntityManager().createQuery("select p from "+getEntityClass()+" p " +
                        "join p.type as t " +
                        "join t.persistentXrefs as x " +
                        "join x.database as d " +
                        "join x.qualifier as q " +
                        "join p.unit as u " +
                        "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and x.id = :mi " +
                        "and u.shortName = :unitName");
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", typeMI);
                query.setParameter("unitName", unitName);
            }
        }
        else{
            if (unitMI == null && unitName == null){
                query = getEntityManager().createQuery("select p from "+getEntityClass()+" p " +
                        "join p.type as t " +
                        "where t.shortName = :confName " +
                        "and p.unit is null");
                query.setParameter("typeName", typeName);
            }
            else if (unitMI != null){
                query = getEntityManager().createQuery("select p from "+getEntityClass()+" p " +
                        "join p.type as t " +
                        "join p.unit as u " +
                        "join u.persistentXrefs as x " +
                        "join x.database as d " +
                        "join x.qualifier as q " +
                        "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and x.id = :mi " +
                        "and t.shortName = :typeName");
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", unitMI);
                query.setParameter("typeName", typeName);
            }
            else{
                query = getEntityManager().createQuery("select p from "+getEntityClass()+" p " +
                        "join p.type as t " +
                        "join p.unit as u " +
                        "where u.shortName = :unitName " +
                        "and t.shortName = :typeName");
                query.setParameter("unitName", unitName);
                query.setParameter("typeName", typeName);
            }
        }
        return query.getResultList();
    }

    public Collection<P> getByParentAc(String parentAc) {
        Query query = getEntityManager().createQuery("select par from " + getEntityClass() + " par " +
                "join par.parent as p " +
                "where p.ac = :ac ");
        query.setParameter("ac",parentAc);
        return query.getResultList();
    }

    @Override
    public void setEntityClass(Class<P> entityClass) {
        super.setEntityClass(entityClass);
        initialiseDbSynchronizer();
    }

    @Override
    protected void initialiseDbSynchronizer() {
        super.setDbSynchronizer(new ParameterSynchronizerTemplate<P>(getEntityManager(), getEntityClass()));
    }
}
